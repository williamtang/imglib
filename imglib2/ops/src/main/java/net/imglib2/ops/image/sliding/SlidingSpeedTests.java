package net.imglib2.ops.image.sliding;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.iterable.MedianOp;
import net.imglib2.roi.RectangleRegionOfInterest;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class SlidingSpeedTests
{

	public static void main( String[] args )
	{

		long[] imgDims = new long[] { 1000, 1000 };
		int roiExtend = 3;

		ImgFactory< UnsignedByteType > fac = new ArrayImgFactory< UnsignedByteType >();

		Img< UnsignedByteType > img = fac.create( imgDims, new UnsignedByteType() );
		Img< UnsignedByteType > res = fac.create( imgDims, new UnsignedByteType() );

		long[] roiMin = new long[ img.numDimensions() ];
		long[] roiMax = new long[ img.numDimensions() ];

		double[] roiDim = new double[ img.numDimensions() ];

		Arrays.fill( roiDim, roiExtend );
		Arrays.fill( roiMax, roiExtend - 1 );

		NaiveSlidingROIIterator< UnsignedByteType > naiveRoi = new NaiveSlidingROIIterator< UnsignedByteType >( img, new RectangleRegionOfInterest( new double[ img.numDimensions() ], roiDim ) );

		BufferedEfficientSlidingIntervalOp< UnsignedByteType > bufferedEfficient = new BufferedEfficientSlidingIntervalOp< UnsignedByteType >( img, new FinalInterval( roiMin, roiMax ) );

		NaiveSlidingIntervalIterator< UnsignedByteType > naive = new NaiveSlidingIntervalIterator< UnsignedByteType >( img, new FinalInterval( roiMin, roiMax ) );

		EfficientSlidingIntervalIterator< UnsignedByteType > efficient = new EfficientSlidingIntervalIterator< UnsignedByteType >( img, new FinalInterval( roiMin, roiMax ) );

		MedianOp< UnsignedByteType > op = new MedianOp< UnsignedByteType >();

		long curr = System.nanoTime();
		test( res, naive, op );
		System.out.println( "Naive " + ( System.nanoTime() - curr ) / 1000 / 1000 );

		curr = System.nanoTime();
		test( res, naiveRoi, op );
		System.out.println( "Naive ROI " + ( System.nanoTime() - curr ) / 1000 / 1000 );

		curr = System.nanoTime();
		test( res, efficient, op );
		System.out.println( "Efficient " + ( System.nanoTime() - curr ) / 1000 / 1000 );

		curr = System.nanoTime();
		test( res, bufferedEfficient, op );
		System.out.println( "BufferedEfficient " + ( System.nanoTime() - curr ) / 1000 / 1000 );
	}

	private static void test( Img< UnsignedByteType > res, SlidingWindowIterator< UnsignedByteType > rect, MedianOp< UnsignedByteType > op )
	{
		Cursor< UnsignedByteType > resCursor = res.cursor();
		while ( rect.hasNext() )
		{
			resCursor.fwd();
			Iterable< UnsignedByteType > iterable = rect.next();

			op.compute( iterable, resCursor.get() );
		}
	}

}
