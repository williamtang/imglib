package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;


/**
 * Illustrate what the outside strategies do
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example5
{
	public Example5() throws ImgIOException, IncompatibleTypeException
	{
		// define the file to open
		File file = new File( "DrosophilaWingSmall.tif" );

		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory< FloatType >(), new FloatType() );

		// test serveral out of bounds strategies
		//testCanvas( image, new OutOfBoundsStrategyValueFactory< FloatType >() );
		RandomAccessible< FloatType> infiniteImg1 = Views.extendValue( image, new FloatType( 0 ) );
		//testCanvas( image, new OutOfBoundsStrategyValueFactory< FloatType >( new FloatType( 128 ) ) );
		RandomAccessible< FloatType> infiniteImg2 = Views.extendValue( image, new FloatType( 128 ) );
		
		//testCanvas( image, new OutOfBoundsStrategyMirrorFactory< FloatType >() );
		RandomAccessible< FloatType> infiniteImg3 = Views.extendMirrorSingle( image ); 
		RandomAccessible< FloatType> infiniteImg4 = Views.extendMirrorDouble( image ); 
		
		//testCanvas( image, new OutOfBoundsStrategyPeriodicFactory< FloatType >() );
		RandomAccessible< FloatType> infiniteImg5 = Views.extendPeriodic( image ); 

		//testCanvas( image, new OutOfBoundsStrategyMirrorExpWindowingFactory< FloatType >( 0.5f ) );
		
		// if you implemented your own strategy that you want to instantiate, it will look like this
		RandomAccessible< FloatType> infiniteImg6 = new ExtendedRandomAccessibleInterval< FloatType, Img< FloatType > >( image, new OutOfBoundsConstantValueFactory< FloatType, Img< FloatType > >( new FloatType( 256 ) ) );
		
		//
		// visualizing the outofbounds strategies
		//
		
		// in order to visualize them, we have to define a new interval on them which can be displayed
		long[] min = new long[ image.numDimensions() ];
		long[] max = new long[ image.numDimensions() ];
		
		for ( int d = 0; d < image.numDimensions(); ++d )
		{
			min[ d ] = -image.dimension( d );
			max[ d ] = image.dimension( d ) * 2 - 1;
		}
		
		FinalInterval interval = new FinalInterval( min, max );
		
		// now define the interval on the infinite view and display
		RandomAccessibleInterval< FloatType > newView = Views.interval( infiniteImg1, interval );
		ImageJFunctions.show( newView );
		
		// or in short
		ImageJFunctions.show( Views.interval( infiniteImg2, interval ) );
		ImageJFunctions.show( Views.interval( infiniteImg3, interval ) );
		ImageJFunctions.show( Views.interval( infiniteImg4, interval ) );
		ImageJFunctions.show( Views.interval( infiniteImg5, interval ) );
		ImageJFunctions.show( Views.interval( infiniteImg6, interval ) );
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example5();
	}
}
