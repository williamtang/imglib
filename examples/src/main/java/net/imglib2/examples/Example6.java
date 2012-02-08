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

import mpicbg.imglib.algorithm.gauss.GaussianConvolution;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.integer.ByteType;

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

		// perform gaussian convolution
		GaussianConvolution< FloatType > gauss = new GaussianConvolution< FloatType >( image, new OutOfBoundsStrategyValueFactory< FloatType >(), 4 );

		// run the algorithm
		if ( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( "Error running gaussian convolution: " + gauss.getErrorMessage() );
			return;
		}

		// get the result
		Img< FloatType > convolved = gauss.getResult();

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
