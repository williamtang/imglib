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

import net.imglib2.AbstractLocalizingCursorInt;
import net.imglib2.Cursor;

/**
 * Localizing {@link Cursor} on a {@link FileImg}.
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
final public class FileLocalizingCursor<T extends ExternalizableType<T>>
		extends AbstractLocalizingCursorInt<T> {
	private int i;
	final private int maxNumPixels;

	final private long[] max;

	private FileImg<T> container;

	public FileLocalizingCursor(final FileLocalizingCursor<T> cursor) {
		super(cursor.numDimensions());

		container = cursor.container;

		this.maxNumPixels = cursor.maxNumPixels;

		this.max = new long[n];
		for (int d = 0; d < n; ++d) {
			max[d] = cursor.max[d];
			position[d] = cursor.position[d];
		}

		i = cursor.i;
	}

	public FileLocalizingCursor(final FileImg<T> img) {
		super(img.numDimensions());

		container = img;

		this.maxNumPixels = (int) img.size() - 1;

		this.max = new long[n];
		for (int d = 0; d < n; ++d) {
			max[d] = img.max(d);
		}

		reset();
	}

	@Override
	public void fwd() {
		++i;

		for (int d = 0; d < n; d++) {
			if (position[d] < max[d]) {
				position[d]++;

				for (int e = 0; e < d; e++)
					position[e] = 0;

				break;
			}
		}
	}

	@Override
	public boolean hasNext() {
		return i < maxNumPixels;
	}

	@Override
	public void reset() {
		i = -1;

		position[0] = -1;

		for (int d = 1; d < n; d++)
			position[d] = 0;
	}

	@Override
	public T get() {
		return container.get(i);
	}

	public void set(final T t) {
		container.set(t, i);
	}

	@Override
	public FileLocalizingCursor<T> copy() {
		return new FileLocalizingCursor<T>(this);
	}

	@Override
	public FileLocalizingCursor<T> copyCursor() {
		return copy();
	}
}
