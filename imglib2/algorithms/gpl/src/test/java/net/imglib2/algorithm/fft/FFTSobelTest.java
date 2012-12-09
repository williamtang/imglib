package net.imglib2.algorithm.fft;

import net.imglib2.algorithm.convolver.DirectConvolver;
import net.imglib2.algorithm.convolver.ImgLib2FourierConvolver;
import net.imglib2.algorithm.convolver.filter.linear.ConstantFilter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class FFTSobelTest {

	/**
	 * Test class to validate Sobel settings. If you apply both filters, it
	 * works. if you apply only one, it doesn't
	 */
	public static void main(String[] args) {

		// Read in your own image here
		Img<FloatType> in = null;

		Img<FloatType> out = in.factory().create(in,
				in.firstElement().createVariable());

		Img<DoubleType> kernelX = ConstantFilter.Sobel.createImage("X");
		Img<DoubleType> kernelY = ConstantFilter.Sobel.createImage("Y");

		// Filtering with only X
		filter(in, kernelX, out);

		// FIltering in X and Y
		filter(in, kernelX, kernelY, out);

	}

	private static void filter(Img<FloatType> in, Img<DoubleType> kernel1,
			Img<DoubleType> kernel2, Img<FloatType> out) {
		
		// Convoling with first kernel
		convolveDirect(in, kernel1, out);
		convolveFFT(in, kernel1, out.copy());

		// Convolving result from first convolution with second kernel.
		
		// Result from direct convolution
		Img<FloatType> resultDirect = convolveDirect(out, kernel2,
				in.factory().create(out, out.firstElement().createVariable()));
		
		// Result from FFT convolution
		Img<FloatType> resultFFT = convolveFFT(out, kernel2,
				in.factory().create(out, out.firstElement().createVariable()));
		
		
		// Show/Compare the results here
		
	}

	private static void filter(Img<FloatType> in, Img<DoubleType> kernel,
			Img<FloatType> out) {
		// Result from direct convolution
		Img<FloatType> convolveDirect = convolveDirect(in, kernel, out);
		
		// Result from FFT convolution
		Img<FloatType> convolveFFT = convolveFFT(in, kernel, out.copy());
		
		
		// Show/Compare the results here
		
	}

	private static Img<FloatType> convolveDirect(Img<FloatType> in,
			Img<DoubleType> kernel, Img<FloatType> out) {
		new DirectConvolver<FloatType, DoubleType, FloatType>().compute(in,
				kernel, out);
		return out;
	}

	private static Img<FloatType> convolveFFT(Img<FloatType> in,
			Img<DoubleType> kernel, Img<FloatType> out) {
		new ImgLib2FourierConvolver<FloatType, DoubleType, FloatType>()
				.compute(in, kernel, out);
		return out;
	}
}
