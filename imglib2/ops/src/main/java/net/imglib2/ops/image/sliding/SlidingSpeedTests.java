package net.imglib2.ops.image.sliding;

import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.image.sliding.buffered.BufferedEfficientSlidingIntervalIterator;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class SlidingSpeedTests
{

	public static void main( String[] args )
	{

		long[] imgDims = new long[] { 1000, 1000, 3 };
		int roiExtend = 3;

		ImgFactory< UnsignedByteType > fac = new ArrayImgFactory< UnsignedByteType >();

		Img< UnsignedByteType > img = fac.create( imgDims, new UnsignedByteType() );
		Img< UnsignedByteType > res = fac.create( imgDims, new UnsignedByteType() );

		long[] roiMin = new long[ img.numDimensions() ];
		long[] roiMax = new long[ img.numDimensions() ];

		double[] roiDim = new double[ img.numDimensions() ];

		Arrays.fill( roiDim, roiExtend );
		Arrays.fill( roiMax, roiExtend - 1 );

		// NaiveSlidingROIIterator< UnsignedByteType, Img< UnsignedByteType >>
		// naiveRoi = new NaiveSlidingROIIterator< UnsignedByteType, Img<
		// UnsignedByteType > >( new OutOfBoundsMirrorFactory< UnsignedByteType,
		// Img< UnsignedByteType >>( Boundary.SINGLE ), img, new
		// RectangleRegionOfInterest( new double[ img.numDimensions() ], roiDim
		// ) );

		BufferedEfficientSlidingIntervalIterator< UnsignedByteType, Img< UnsignedByteType >> bufferedEfficient = new BufferedEfficientSlidingIntervalIterator< UnsignedByteType, Img< UnsignedByteType > >( new OutOfBoundsMirrorFactory< UnsignedByteType, Img< UnsignedByteType > >( Boundary.SINGLE ), img, new FinalInterval( roiMin, roiMax ) );

		// NaiveSlidingIntervalIterator< UnsignedByteType, Img< UnsignedByteType
		// >> naive = new NaiveSlidingIntervalIterator< UnsignedByteType, Img<
		// UnsignedByteType > >( new OutOfBoundsMirrorFactory< UnsignedByteType,
		// Img< UnsignedByteType >>( Boundary.SINGLE ), img, new FinalInterval(
		// roiMin, roiMax ) );

		EfficientSlidingIntervalIterator< UnsignedByteType, Img< UnsignedByteType > > efficient = new EfficientSlidingIntervalIterator< UnsignedByteType, Img< UnsignedByteType >>( new OutOfBoundsMirrorFactory< UnsignedByteType, Img< UnsignedByteType >>( Boundary.SINGLE ), img, new FinalInterval( roiMin, roiMax ) );

		UnaryOperation< Iterable< UnsignedByteType >, UnsignedByteType > op = new UnaryOperation< Iterable< UnsignedByteType >, UnsignedByteType >()
		{

			@Override
			public UnsignedByteType compute( Iterable< UnsignedByteType > input, UnsignedByteType output )
			{
				Iterator< UnsignedByteType > it = input.iterator();
				while ( it.hasNext() )
				{
					it.next();
					// it.next();
				}
				return output;
			}

			@Override
			public UnaryOperation< Iterable< UnsignedByteType >, UnsignedByteType > copy()
			{
				return null;
			}
		};

		long curr = System.nanoTime();
		test( res, bufferedEfficient, op );
		System.out.println( "Speed " + ( System.nanoTime() - curr ) / 1000 / 1000 );
		//
		// curr = System.nanoTime();
		// test( res, naiveRoi, op );
		// System.out.println( "Naive ROI " + ( System.nanoTime() - curr ) /
		// 1000 / 1000 );
		//
		// curr = System.nanoTime();
		// test( res, efficient, op );
		// System.out.println( "Efficient " + ( System.nanoTime() - curr ) /
		// 1000 / 1000 );
		//
		// curr = System.nanoTime();
		// test( res, bufferedEfficient, op );
		// System.out.println( "BufferedEfficient " + ( System.nanoTime() - curr
		// ) / 1000 / 1000 );
	}

	private static void test( Img< UnsignedByteType > res, SlidingWindowIterator< UnsignedByteType > rect, UnaryOperation< Iterable< UnsignedByteType >, UnsignedByteType > op )
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
