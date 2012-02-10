package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

import net.imglib2.RandomAccessible;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import mpicbg.imglib.algorithm.CanvasImage;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorExpWindowingFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyPeriodicFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.util.Util;

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
	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example5();
	}
}
