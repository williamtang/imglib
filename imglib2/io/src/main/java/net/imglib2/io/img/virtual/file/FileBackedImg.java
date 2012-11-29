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

package net.imglib2.io.img.virtual.file;

import java.io.File;
import java.io.RandomAccessFile;

import net.imglib2.FlatIterationOrder;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;


/**
 * @author Barry DeZonia
 * @param <T>
 */
public class FileBackedImg<T extends NativeType<T> & ComplexType<T>> extends
	AbstractImg<FileBackedType<T>>
{
	private final long[] dims;
	private final T type;
	private final int numComplexes;
	private final int numDoubles;
	private final double[] buffer;
	private RandomAccessFile dataFile;
	private boolean dirty;
	private long inMemBufNum;

	// -- constructor --

	public FileBackedImg(long[] dims, T type, int numValues) {
		super(dims);
		if (numValues > Integer.MAX_VALUE / 2) {
			throw new IllegalArgumentException("cannot create such a large buffer");
		}
		this.numComplexes = numValues;
		this.numDoubles = numComplexes * 2;
		this.buffer = new double[numDoubles];
		this.dims = dims;
		this.type = type;
		try {
			File file = File.createTempFile("FileBackedImg", null);
			this.dataFile = new RandomAccessFile(file, "rw");
			long totalElems = countElems(dims);
			long numBuffers = (totalElems / numComplexes) + 1;
			for (long b = 0; b < numBuffers; b++) {
				for (long l = 0; l < numDoubles; l++) {
					dataFile.writeDouble(0);
				}
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException("failed to initialize backing file " +
				e.getMessage());
		}
		dirty = false;
		inMemBufNum = 0;
	}

	// -- FileBackedImg methods --

	public T backingType() {
		return type;
	}

	// -- Img methods --

	@Override
	public ImgFactory<FileBackedType<T>> factory() {
		throw new UnsupportedOperationException(
			"cannot get an ImgFactory from a FileBackedImg");
	}

	@Override
	public FileBackedImg<T> copy() {
		return new FileBackedImg<T>(dims.clone(), type.createVariable(),
			numComplexes);
	}

	@Override
	public FileBackedRandomAccess<T> randomAccess() {
		return new FileBackedRandomAccess<T>(this);
	}

	@Override
	public FileBackedCursor<T> cursor() {
		return new FileBackedCursor<T>(this);
	}

	@Override
	public FileBackedCursor<T> localizingCursor() {
		return new FileBackedCursor<T>(this);
	}

	@Override
	public FlatIterationOrder iterationOrder() {
		return new FlatIterationOrder(this);
	}

	// -- package access methods --

	long calcPos(long[] pos) {
		long p = 0;
		long multiplier = 1;
		for (int i = 0; i < n; i++) {
			p += pos[i] * multiplier;
			multiplier *= dimension(i);
		}
		return p;
	}

	long calcPos(long pos, int d) {
		// TODO - precompute factors rather than recalc'ing
		long bottomFactor = 1;
		for (int i = 0; i < d; i++) {
			bottomFactor *= dimension(i);
		}
		long topFactor = bottomFactor * dimension(d);
		long numer = pos % topFactor;
		long denom = bottomFactor;
		return numer / denom;
	}

	void getValue(long pos, T val) {
		synchronized (this) {
			vswap(pos);
			int elem = (int) (pos % numComplexes);
			val.setReal(buffer[elem * 2]);
			val.setImaginary(buffer[elem * 2 + 1]);
		}
	}

	void setValue(long pos, T value) {
		synchronized (this) {
			vswap(pos);
			int elem = (int) (pos % numComplexes);
			buffer[elem * 2] = value.getRealDouble();
			buffer[elem * 2 + 1] = value.getImaginaryDouble();
			dirty = true;
		}
	}

	// -- private helpers --

	private void vswap(long pos) {
		if (pos < inMemBufNum || pos >= inMemBufNum + numComplexes) {
			if (dirty) {
				writeBlock();
				dirty = false;
			}
			loadBlock(pos);
		}
	}

	private void writeBlock() {
		try {
			long filePos = inMemBufNum * numDoubles * 8;
			dataFile.seek(filePos);
			for (int i = 0; i < numDoubles; i++) {
				dataFile.writeDouble(buffer[i]);
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException("could not write data block to file " +
				e.getMessage());
		}
	}

	private void loadBlock(long pos) {
		long bufNum = pos / numComplexes;
		if (bufNum == inMemBufNum) return;
		try {
			long filePos = bufNum * numDoubles * 8;
			dataFile.seek(filePos);
			for (int i = 0; i < numDoubles; i++) {
				buffer[i] = dataFile.readDouble();
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException("could not write data block to file " +
				e.getMessage());
		}
		inMemBufNum = bufNum;
	}

	private static long countElems(long[] dims) {
		if (dims.length == 0) return 0;
		long tot = 1;
		for (long d : dims)
			tot *= d;
		return tot;
	}
}
