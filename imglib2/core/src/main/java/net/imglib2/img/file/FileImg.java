/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2008 - 2012
 * KNIME.com, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 * History
 *   Sep 4, 2012 (hornm): created
 */

package net.imglib2.img.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Pair;

/**
 *
 * @author Martin Horn, University of Konstanz
 *
 */
public class FileImg<T extends NativeType<T>, A extends ArrayDataAccess<A>>
        extends AbstractNativeImg<T, A> {

    private static final Logger LOGGER = Logger.getLogger("FileImg");

    private final int maxEntitiesPerChunk;

    /* maps buffer index to the buffer */
    private final Map<Integer, SoftReference<A>> dataChunkMap =
            new HashMap<Integer, SoftReference<A>>();

    private final LinkedList<Pair<A, Long>> chunkCache =
            new LinkedList<Pair<A, Long>>();

    private final File file;

    private final A dataBufferTemplate;

    int[] dim;

    int[] steps;

    /* offset in the file where the image data starts */
    private final long offset;

    private RandomAccessFile fileAccess;

    private static MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();

    /**
     * @param dim
     * @param entitiesPerPixel
     * @param file the file backing the image, if it doesn't exist it will be
     *            created with the according size
     * @param dataBufferTemplate data buffer template to be able to create the
     *            right type of new data buffers, hence size can be 1
     * @param maxEntitiesPerChunk
     * @param offset the offset in the file where the image data starts
     */
    public FileImg(final long[] dim, final int entitiesPerPixel,
            final File file, final A dataBufferTemplate,
            final int maxEntitiesPerChunk, final long offset) {
        super(dim, entitiesPerPixel);

        this.dim = new int[n];
        for (int d = 0; d < n; ++d)
            this.dim[d] = (int)dim[d];

        this.steps = new int[n];
        IntervalIndexer.createAllocationSteps(this.dim, this.steps);

        this.dataBufferTemplate = dataBufferTemplate;
        this.file = file;

        // if the file doesn't exist allocate the harddisk memory
        boolean fileExists = file.exists();
        try {
            fileAccess = new RandomAccessFile(file, "rw");
            if (!fileExists) {
                fileAccess.setLength(numEntities);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can't access file.", e);
        } catch (IOException e) {
            throw new RuntimeException("Can't access file.", e);
        }
        this.maxEntitiesPerChunk = maxEntitiesPerChunk;

        this.offset = offset;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public A update(final Object updater) {

        // get the current index in the file/image from the updater
        long entityIndex =
                ((FileContainerSampler<T, A>)updater).getEntityIndex();
        int bufferIndex = (int)(entityIndex / maxEntitiesPerChunk);
        long bufferStartIndex = (long)bufferIndex * maxEntitiesPerChunk;
        ((FileContainerSampler<T, A>)updater)
                .setBufferStartIndex(bufferStartIndex);
        int length =
                (int)Math.min(maxEntitiesPerChunk, numEntities
                        - bufferStartIndex);
        ((FileContainerSampler<T, A>)updater).setBufferLength(length);
        checkAndFreeMemory();
        SoftReference<A> ref = dataChunkMap.get(bufferIndex);
        if (ref != null && ref.get() != null) {
            // current position is already in the buffer
            return ref.get();
        } else {

            System.out.println("Create chunk in memory ...");
            A dataBuffer = dataBufferTemplate.createArray(length);
            try {
                FileBufferIO.fillBuffer(
                        file,
                        dataBuffer,
                        offset
                                + FileBufferIO.getOffsetFromEntityIndex(
                                        dataBuffer, bufferStartIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataChunkMap.put(bufferIndex, new SoftReference<A>(dataBuffer));
            chunkCache.add(new Pair<A, Long>(dataBuffer, bufferStartIndex));
            return dataBuffer;
        }

    }

    private void checkAndFreeMemory() {
        if ((double)memBean.getHeapMemoryUsage().getUsed()
                / memBean.getHeapMemoryUsage().getMax() > .6) {
            // write and clear some chunks
            for (int i = 0; chunkCache.size() > 1 && i < 2; i++) {
                Pair<A, Long> chunk = chunkCache.removeFirst();
                System.out.println("Free memory ...");
                if (chunk.a instanceof DirtyFlag
                        && ((DirtyFlag)chunk.a).isDirty()) {
                    System.out.println("Write chunk to file ... ");
                    try {
                        FileBufferIO.writeBuffer(
                                fileAccess,
                                chunk.a,
                                offset
                                        + FileBufferIO
                                                .getOffsetFromEntityIndex(
                                                        chunk.a, chunk.b));
                    } catch (IOException e) {
                        throw new RuntimeException("Writing chunk to file "
                                + file + " failed.", e);
                    }
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImgFactory<T> factory() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Img<T> copy() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RandomAccess<T> randomAccess() {
        return new FileRandomAccess<T, A>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor<T> cursor() {
        return new FileCursor<T, A>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor<T> localizingCursor() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object iterationOrder() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This interface is implemented by all samplers on the {@link FileImg}. It
     * allows the container to ask for the current absolute offset in the file.
     */
    public interface FileContainerSampler<T extends NativeType<T>, A extends ArrayDataAccess<A>> {
        /**
         * @return the offset in the file where the sampler is
         */
        public long getEntityIndex();

        public void setBufferStartIndex(long bufferStartIndex);

        public void setBufferLength(int length);
    }

    // private class WriteOnFinalizeBuffer {
    //
    // final A buffer;
    //
    // public WriteOnFinalizeBuffer(final A buffer) {
    // this.buffer = buffer;
    // }
    //
    // @Override
    // protected void finalize() throws Throwable {
    // // only write chunk to file if it is dirty!
    // System.out.println("Garbage collect chunk ...");
    // if (buffer instanceof DirtyFlag && ((DirtyFlag)buffer).isDirty()) {
    // System.out.println("Write chunk to file ... (free memory: "
    // + Runtime.getRuntime().freeMemory()
    // + ";#chunks in memory: " + dataChunkMap.size() + ")");
    // FileBufferIO.writeBuffer(
    // fileAccess,
    // buffer,
    // offset
    // + FileBufferIO.getOffsetFromEntityIndex(buffer,
    // lastEntityIndex));
    // }
    // }
    // }

}
