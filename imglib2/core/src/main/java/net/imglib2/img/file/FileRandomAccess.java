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

import net.imglib2.AbstractLocalizableInt;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

/**
 * {@link RandomAccess} on an {@link ArrayImg}.
 *
 * @param <T>
 *
 * @author ImgLib2 developers
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class FileRandomAccess<T extends NativeType<T>, A extends ArrayDataAccess<A>>
        extends AbstractLocalizableInt implements RandomAccess<T>,
        FileImg.FileContainerSampler<T, A> {
    protected final T type;

    final FileImg<T, ?> img;

    private long globalIndex;

    /* the start index of the current buffer */
    private long bufferStartIndex;

    /* the length of the current buffer */
    private int bufferLength;

    protected FileRandomAccess(final FileRandomAccess<T, A> randomAccess) {
        super(randomAccess.numDimensions());

        this.img = randomAccess.img;
        this.type = img.createLinkedType();

        long index = 0;
        for (int d = 0; d < n; d++) {
            position[d] = randomAccess.position[d];
            index += position[d] * img.steps[d];
        }
        globalIndex = index;
        type.updateContainer(this);
        type.updateIndex((int)(globalIndex - bufferStartIndex));
        // type.updateIndex(index);
    }

    public FileRandomAccess(final FileImg<T, A> container) {
        super(container.numDimensions());

        this.img = container;
        this.type = container.createLinkedType();

        for (int d = 0; d < n; d++)
            position[d] = 0;

        type.updateContainer(this);
        type.updateIndex(0);
    }

    @Override
    public T get() {
        return type;
    }

    @Override
    public void fwd(final int d) {
        // type.incIndex(img.steps[d]);
        globalIndex += img.steps[d];

        // if we are outside of the buffer, retrieve the next one
        if (globalIndex >= bufferStartIndex + bufferLength) {
            // load the according buffer if necessary (decided based on the
            // globalIndex, see FileContainerSampler)
            type.updateContainer(this);
        }
        // set the current index within the buffer
        type.updateIndex((int)(globalIndex - bufferStartIndex));
        ++position[d];
    }

    @Override
    public void bck(final int d) {
        globalIndex -= img.steps[d];
        if (globalIndex < bufferStartIndex) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
        --position[d];
    }

    @Override
    public void move(final int distance, final int d) {
        type.incIndex(img.steps[d] * distance);
        position[d] += distance;
    }

    @Override
    public void move(final long distance, final int d) {
        type.incIndex(img.steps[d] * (int)distance);
        position[d] += distance;
    }

    @Override
    public void move(final Localizable localizable) {
        int index = 0;
        for (int d = 0; d < n; ++d) {
            final int distance = localizable.getIntPosition(d);
            position[d] += distance;
            index += distance * img.steps[d];
        }

        globalIndex += index;
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
    }

    @Override
    public void move(final int[] distance) {
        int index = 0;
        for (int d = 0; d < n; ++d) {
            position[d] += distance[d];
            index += distance[d] * img.steps[d];
        }
        globalIndex += index;
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
    }

    @Override
    public void move(final long[] distance) {
        int index = 0;
        for (int d = 0; d < n; ++d) {
            position[d] += distance[d];
            index += distance[d] * img.steps[d];
        }
        globalIndex += index;
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
    }

    @Override
    public void setPosition(final Localizable localizable) {
        localizable.localize(position);
        long index = 0;
        for (int d = 0; d < n; ++d)
            index += position[d] * img.steps[d];
        globalIndex = index;
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
    }

    @Override
    public void setPosition(final int[] pos) {
        long index = 0;
        for (int d = 0; d < n; ++d) {
            position[d] = pos[d];
            index += pos[d] * img.steps[d];
        }
        globalIndex = index;
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
    }

    @Override
    public void setPosition(final long[] pos) {
        long index = 0;
        for (int d = 0; d < n; ++d) {
            final int p = (int)pos[d];
            position[d] = p;
            index += (long)p * img.steps[d];
        }
        globalIndex = index;
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
    }

    @Override
    public void setPosition(final int pos, final int d) {
        // type.incIndex((pos - position[d]) * img.steps[d]);
        globalIndex += (pos - position[d]) * img.steps[d];
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
        position[d] = pos;
    }

    @Override
    public void setPosition(final long pos, final int d) {
        // type.incIndex(((int)pos - position[d]) * img.steps[d]);
        globalIndex += (pos - position[d]) * img.steps[d];
        if (globalIndex < bufferStartIndex
                || globalIndex >= bufferStartIndex + bufferLength) {
            type.updateContainer(this);
        }
        type.updateIndex((int)(globalIndex - bufferStartIndex));
        position[d] = (int)pos;
    }

    @Override
    public FileRandomAccess<T, A> copy() {
        return new FileRandomAccess<T, A>(this);
    }

    @Override
    public FileRandomAccess<T, A> copyRandomAccess() {
        return copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEntityIndex() {
        return globalIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferStartIndex(final long bufferStartIndex) {
        this.bufferStartIndex = bufferStartIndex;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferLength(final int length) {
        this.bufferLength = length;

    }

}
