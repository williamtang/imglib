package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.algorithm.gauss.Gauss;

/**
 * Use of Gaussian Convolution on the Image
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example6
{
	public Example6() throws ImgIOException, IncompatibleTypeException
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory< FloatType >(), new FloatType() );

		// perform gaussian convolution with float precision
		double[] sigma = new double[ image.numDimensions() ];
		
		for ( int d = 0; d < image.numDimensions(); ++d )
			sigma[ d ] = 4;
		
		Img< FloatType > convolved = Gauss.toFloat( sigma, image );

		// display
		ImageJFunctions.show( convolved );

		// find maxima again
		final Img< ByteType > maxima = Example4.findAndDisplayLocalMaxima( convolved, new ByteType() );

		// display maxima
		ImageJFunctions.show( maxima );
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6();
	}
}
