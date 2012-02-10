package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.algorithm.gauss.Gauss;

/**
 * Use of Gaussian Convolution on the Image
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example7
{
	public Example7() throws ImgIOException, IncompatibleTypeException
	{
		// define the file to open
		File file = new File( "street_bw.tif" );

		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory< FloatType >(), new FloatType() );

		// display maxima
		ImageJFunctions.show( image );

		// perform gaussian convolution with double precision
		Img< FloatType > convolved = Gauss.toDouble( new double[]{ 0, 0, 4 }, image );
		
		// display
		ImageJFunctions.show( convolved );
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example7();
	}
}
