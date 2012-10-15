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
 *   Sep 5, 2012 (hornm): created
 */

package net.imglib2.img.file;

import net.imglib2.img.basictypeaccess.array.ByteArray;

/**
 *
 * @author Martin Horn, University of Konstanz
 *
 */
public class DirtyFlagByteArray extends ByteArray implements DirtyFlag {

    /**
     * @param data
     */
    public DirtyFlagByteArray(final int numEntities) {
        super(numEntities);
    }

    private boolean isDirty = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final int index, final byte value) {
        super.setValue(index, value);
        isDirty = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirtyFlagByteArray createArray(final int numEntities) {
        return new DirtyFlagByteArray(numEntities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return isDirty;
    }

}
