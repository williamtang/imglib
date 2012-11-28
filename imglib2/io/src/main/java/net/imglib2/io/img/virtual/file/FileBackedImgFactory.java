package net.imglib2.io.img.virtual.file;

import net.imglib2.Dimensions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;


// TODO - this class does not implement the ImgFactory interface!

public class FileBackedImgFactory
{

	public FileBackedImgFactory() {
	}

	public <T extends NativeType<T> & ComplexType<T>> FileBackedImg<T>
		create(long[] dims, T type, int bufSize)
	{
		return new FileBackedImg<T>(dims, type, bufSize);
	}

	public <T extends NativeType<T> & ComplexType<T>> FileBackedImg<T>
		create(int[] dims, T type, int bufSize)
	{
		long[] ldims = new long[dims.length];
		for (int i = 0; i < ldims.length; i++)
			ldims[i] = dims[i];
		return create(ldims, type, bufSize);
	}

	public <T extends NativeType<T> & ComplexType<T>> FileBackedImg<T>
		create(Dimensions dims, T type, int bufSize)
	{
		long[] ldims = new long[dims.numDimensions()];
		for (int i = 0; i < ldims.length; i++)
			ldims[i] = dims.dimension(i);
		return create(ldims, type, bufSize);
	}

}
