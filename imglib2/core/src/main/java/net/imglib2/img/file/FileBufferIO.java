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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;

/**
 *
 * @author Martin Horn, University of Konstanz
 *
 */
public class FileBufferIO {

    private FileBufferIO() {
        // utility class
    }

    public static <A extends ArrayDataAccess<A>> long getOffsetFromEntityIndex(
            final A buffer, final long entityIndex) {
        if (ByteArray.class.isAssignableFrom(buffer.getClass())) {
            return entityIndex;
        } else if (buffer instanceof ShortArray) {
            return entityIndex * Short.SIZE / 8;
        }
        return -1;
    }

    public static <A extends ArrayDataAccess<A>> void writeBuffer(
            final RandomAccessFile fileAccess, final A buffer, final long offset)
            throws IOException {
        fileAccess.seek(offset);
        if (buffer instanceof ByteArray) {
            byte[] array = ((ByteArray)buffer).getCurrentStorageArray();
            for (int i = 0; i < array.length; i++) {
                fileAccess.writeByte(array[i]);
            }
        }

    }

    public static <A extends ArrayDataAccess<A>> int fillBuffer(
            final File file, final A buffer, final long offset)
            throws IOException {
        BufferedDataInputStream stream =
                new BufferedDataInputStream(new FileInputStream(file));
        long length = file.length();
        stream.skip(offset);
        int entitiesRead = 0;
        if (buffer instanceof ByteArray) {
            byte[] array = ((ByteArray)buffer).getCurrentStorageArray();
            entitiesRead =
                    offset + array.length < length ? array.length
                            : (int)(length - offset);
            stream.read(array, 0, entitiesRead);
        } else if (buffer instanceof ShortArray) {
            short[] array = ((ShortArray)buffer).getCurrentStorageArray();
            entitiesRead =
                    offset + array.length < length ? array.length
                            : (int)(length - offset);
            stream.read(array, 0, entitiesRead);
        } else if (buffer instanceof IntArray) {
            int[] array = ((IntArray)buffer).getCurrentStorageArray();
            entitiesRead =
                    offset + array.length < length ? array.length
                            : (int)(length - offset);
            stream.read(array, 0, entitiesRead);
        }
        stream.close();
        return entitiesRead;

    }

}
