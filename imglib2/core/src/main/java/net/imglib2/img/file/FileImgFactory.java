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

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.NativeType;

/**
 *
 * @author Martin Horn, University of Konstanz
 *
 */
public class FileImgFactory<T extends NativeType<T>> extends
        NativeImgFactory<T> {

    private final int maxEntitiesPerBuffer;

    private final FileNameGenerator fileNameGen;

    public FileImgFactory(final int maxEntitiesPerBuffer,
            final FileNameGenerator fileNameGen) {
        this.maxEntitiesPerBuffer = maxEntitiesPerBuffer;
        this.fileNameGen = fileNameGen;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends BitAccess> createBitInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends ByteAccess> createByteInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        File file = new File(fileNameGen.nextAbsoluteFileName());
        return new FileImg<T, ByteArray>(dimensions, entitiesPerPixel, file,
                new DirtyFlagByteArray(1), maxEntitiesPerBuffer, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends CharAccess> createCharInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends ShortAccess> createShortInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends IntAccess> createIntInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends LongAccess> createLongInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends FloatAccess> createFloatInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeImg<T, ? extends DoubleAccess> createDoubleInstance(
            final long[] dimensions, final int entitiesPerPixel) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> ImgFactory<S> imgFactory(final S type)
            throws IncompatibleTypeException {
        // TODO Auto-generated method stub
        return null;
    }

    public interface FileNameGenerator {
        public String nextAbsoluteFileName();
    }

}
