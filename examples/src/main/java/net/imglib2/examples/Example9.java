package net.imglib2.examples;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import mpicbg.imglib.algorithm.fft.FourierConvolution;
import mpicbg.imglib.algorithm.fft.FourierTransform;
import mpicbg.imglib.algorithm.fft.InverseFourierTransform;
import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.type.numeric.complex.ComplexFloatType;
import ij.ImageJ;

/**
 * Perform template matching through Convolution int the Fourier domain
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example9
{
	public Example9()
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openLOCIFloatType( "JohannesAndAlbert.jpg", new ArrayImgFactory< T >() );
		Img< FloatType > kernel = new ImgOpener().openLOCIFloatType( "kernelAlbert.tif", new ArrayImgFactory< T >() );

		final FourierTransform< FloatType, ComplexFloatType > fft = new FourierTransform< FloatType, ComplexFloatType >( kernel, new ComplexFloatType() );
		final Img< ComplexFloatType > kernelFFT;
		if ( fft.checkInput() && fft.process() )
			kernelFFT = fft.getResult();
		else
		{
			System.err.println( "Cannot compute fourier transform: " + fft.getErrorMessage() );
			return;
		}

		// complex invert the kernel
		final ComplexFloatType c = new ComplexFloatType();
		for ( final ComplexFloatType t : kernelFFT.createCursor() )
		{
			c.set( t );
			t.complexConjugate();
			c.mul( t );
			t.div( c );
		}

		// compute inverse fourier transform of the kernel
		final InverseFourierTransform< FloatType, ComplexFloatType > ifft = new InverseFourierTransform< FloatType, ComplexFloatType >( kernelFFT, fft );
		final Img< FloatType > kernelInverse;
		if ( ifft.checkInput() && ifft.process() )
			kernelInverse = ifft.getResult();
		else
		{
			System.err.println( "Cannot compute inverse fourier transform: " + ifft.getErrorMessage() );
			return;
		}

		// normalize the kernel
		NormalizeImageFloat< FloatType > normImage = new NormalizeImageFloat< FloatType >( kernel );

		if ( !normImage.checkInput() || !normImage.process() )
		{
			System.out.println( "Cannot normalize kernel: " + normImage.getErrorMessage() );
			return;
		}

		kernel.close();
		kernel = normImage.getResult();

		// display all
		ImageJFunctions.copyToImagePlus( kernel ).show().setTitle( "kernel" );

		ImageJFunctions.copyToImagePlus( kernelInverse ).show().setTitle( "inverse kernel" );

		ImageJFunctions.copyToImagePlus( image ).show();

		// compute fourier convolution
		FourierConvolution< FloatType, FloatType > fourierConvolution = new FourierConvolution< FloatType, FloatType >( image, kernelInverse );

		if ( !fourierConvolution.checkInput() || !fourierConvolution.process() )
		{
			System.out.println( "Cannot compute fourier convolution: " + fourierConvolution.getErrorMessage() );
			return;
		}

		Img< FloatType > convolved = fourierConvolution.getResult();

		final String nameConvolved = "(" + fourierConvolution.getProcessingTime() + " ms) Convolution of " + image.getName();
		ImageJFunctions.copyToImagePlus( convolved ).show().setTitle( nameConvolved );

	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example9();
	}
}
