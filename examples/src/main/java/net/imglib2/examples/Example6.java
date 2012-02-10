package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
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
		
		//
		// convolve with a different outofboundsstrategy
		//
		
		// first extend the image to infinity, zeropad
		RandomAccessible< FloatType > infiniteImg = Views.extendValue( image, new FloatType() );
		
		// now we convolve the whole image in-place
		// note that is is basically the same as the call above, just called in a more generic way
		//
		// sigma .. the sigma
		// infiniteImg ... the RandomAccessible that is the source for the convolution
		// image ... defines the Interval in which convolution is performed
		// image ... defines the target of the convolution
		// new Point( image.numDimensions() ) ... defines the offset for the target, in this case (0,0)
		// image.factory ... the image factory which is required to create temporary images
		Gauss.inFloat( sigma, infiniteImg, image, image, new Point( image.numDimensions() ), image.factory() );
		
		// show the in-place convolved image (note the different outofboundsstrategy at the edges)
		ImageJFunctions.show( image );
		
		//
		// convolve just a part of the image
		//
		
		// open the image again as we convolved it in-place already
		Img< FloatType > image2 = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory< FloatType >(), new FloatType() );
		
		// we need to extend it nevertheless as the algorithm needs more area around convolved spot
		// and we are not sure how much exactly (altough we could compute it with some effort from the sigma)
		RandomAccessible< FloatType > infiniteImg2 =infiniteImg = Views.extendMirrorSingle( image );
		
		// define the area
		long[] min = new long[] { 100, 30 };
		long[] max = new long[] { 500, 250 };
		FinalInterval region = new FinalInterval( min, max );
		
		// call the gauss, we convolve only a region and write it back to the exact same coordinates
		Gauss.inFloat( sigma, infiniteImg2, region, image2, new Point( min ), image2.factory() );
		
		ImageJFunctions.show( image2 );
		
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
