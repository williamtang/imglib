package net.imglib2.io.img.virtual.file;

import java.io.File;
import java.io.RandomAccessFile;

import net.imglib2.FlatIterationOrder;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;


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
