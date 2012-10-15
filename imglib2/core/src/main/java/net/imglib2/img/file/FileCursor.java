/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.img.file;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.cell.CellImg;
import net.imglib2.type.NativeType;

/**
 * {@link Cursor} on a {@link CellImg}.
 *
 *
 * @author ImgLib2 developers
 * @author Tobias Pietzsch
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FileCursor<T extends NativeType<T>, A extends ArrayDataAccess<A>>
        extends AbstractCursor<T> implements FileImg.FileContainerSampler<T, A> {
    protected final T type;

    /*
     * The current index of the type. It is faster to duplicate this here than
     * to access it through type.getIndex(). Its the index within the current
     * buffer.
     */
    protected int index;

    /*
     * Index of the offset of the buffer.
     */
    protected int bufferOffsetIndex;

    /*
     * Caches cursorOnCells.hasNext().
     */
    protected boolean isNotLastBuffer;

    /*
     * Number of total entities in the image.
     */
    private final long numEntities;

    /*
     * Number of entities in the current buffer.
     */
    private int numEntitiesInBuffer;

    protected FileCursor(final FileCursor<T, A> cursor) {
        super(cursor.numDimensions());

        this.type = cursor.type.duplicateTypeOnSameNativeImg();
        isNotLastBuffer = cursor.isNotLastBuffer;
        index = cursor.index;
        bufferOffsetIndex = cursor.bufferOffsetIndex;
        numEntitiesInBuffer = cursor.numEntitiesInBuffer;
        numEntities = cursor.numEntities;

        type.updateContainer(this);
        type.updateIndex(index);
    }

    public FileCursor(final FileImg<T, A> container) {
        super(container.numDimensions());

        this.type = container.createLinkedType();
        this.numEntities = container.size();
        reset();
    }

    @Override
    public T get() {
        return type;
    }

    @Override
    public FileCursor<T, A> copy() {
        return new FileCursor<T, A>(this);
    }

    @Override
    public FileCursor<T, A> copyCursor() {
        return copy();
    }

    @Override
    public boolean hasNext() {
        return (index < numEntitiesInBuffer - 1) || isNotLastBuffer;
    }

    @Override
    public void jumpFwd(final long steps) {
        long newIndex = index + steps;
        while (newIndex >= numEntitiesInBuffer) {
            newIndex -= numEntitiesInBuffer;
            fillNextBuffer();
        }
        index = (int)newIndex;
        type.updateIndex(index);
        type.updateContainer(this);
    }

    @Override
    public void fwd() {
        if (++index >= numEntitiesInBuffer) {
            fillNextBuffer();
            index = 0;
            type.updateIndex(index);
        } else {
            type.incIndex();
        }
    }

    @Override
    public void reset() {
        bufferOffsetIndex = 0;
        fillNextBuffer();
        type.updateIndex(index);
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public long getLongPosition(final int dim) {
        return 0;
    }

    @Override
    public void localize(final long[] position) {
        // getCell().indexToGlobalPosition(index, position);
    }

    /**
     * Move cursor right before the first element of the next cell. Update type
     * and index variables.
     */
    private void fillNextBuffer() {
        bufferOffsetIndex += numEntitiesInBuffer;
        type.updateContainer(this);
        isNotLastBuffer = bufferOffsetIndex + numEntitiesInBuffer < numEntities;
        index = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEntityIndex() {
        return bufferOffsetIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferLength(final int length) {
        numEntitiesInBuffer = length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferStartIndex(final long bufferStartIndex) {
        // nothing to do here

    }

}
