package net.imglib2.io.img.virtual.file;

import net.imglib2.AbstractCursor;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;


public class FileBackedCursor<T extends NativeType<T> & ComplexType<T>> extends
	AbstractCursor<FileBackedType<T>>
{

	private final FileBackedImg<T> img;
	private final FileBackedType<T> type;
	private final long last;
	private long pos;
	
	public FileBackedCursor(FileBackedImg<T> img) {
		super(img.numDimensions());
		this.img = img;
		this.type = new FileBackedType<T>(img);
		this.pos = -1;
		this.last = img.size() - 1;
	}

	@Override
	public FileBackedType<T> get() {
		type.fillFromFile(pos);
		return type;
	}

	@Override
	public void fwd() {
		pos++;
		if (pos > last) throw new IllegalArgumentException("can't fwd beyond end");
	}

	@Override
	public void reset() {
		pos = -1;
	}

	@Override
	public boolean hasNext() {
		return pos < last;
	}

	@Override
	public void localize(long[] position) {
		for (int i = 0; i < position.length; i++) {
			position[i] = getLongPosition(i);
		}
	}

	@Override
	public long getLongPosition(int d) {
		return img.calcPos(pos, d);
	}

	@Override
	public FileBackedCursor<T> copy() {
		return new FileBackedCursor<T>(img);
	}

	@Override
	public FileBackedCursor<T> copyCursor() {
		return new FileBackedCursor<T>(img);
	}

}
