package net.imglib2.io.img.virtual.file;

import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;

public class FileBackedRandomAccess<T extends NativeType<T> & ComplexType<T>>
	extends Point implements RandomAccess<FileBackedType<T>>
{
	private final FileBackedImg<T> img;
	private final FileBackedType<T> type;

	public FileBackedRandomAccess(FileBackedImg<T> img) {
		super(img.numDimensions());
		this.img = img;
		type = new FileBackedType<T>(img);
	}

	@Override
	public FileBackedType<T> get() {
		long pos = img.calcPos(position);
		type.fillFromFile(pos);
		return type;
	}

	@Override
	public FileBackedRandomAccess<T> copy() {
		return new FileBackedRandomAccess<T>(img);
	}

	@Override
	public FileBackedRandomAccess<T> copyRandomAccess() {
		return new FileBackedRandomAccess<T>(img);
	}

}
