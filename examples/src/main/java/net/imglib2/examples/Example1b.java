package net.imglib2.examples;

import java.io.File;

import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.cell.CellContainerFactory;
import ij.ImageJ;

/**
 * Opens a file with ImgOpener Bioformats as an ImgLib {@link Image}.
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example1b
{
	// within this method we define <T> to be a RealType
	public < T extends RealType< T > & NativeType< T > > Example1b()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayContainer
		Img< T > image = ImgOpener.openImg( file.getAbsolutePath(), new ArrayContainerFactory() );

		// display it via ImgLib using ImageJ
		ImageJFunctions.show( image );

		// open with ImgOpener as Float using an ArrayContainer
		Img< FloatType > imageFloat = ImgOpener.openLOCIFloatType( file.getAbsolutePath(), new CellContainerFactory( 10 ) );

		// display it via ImgLib using ImageJ
		ImageJFunctions.show( imageFloat );
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1b();
	}
}
