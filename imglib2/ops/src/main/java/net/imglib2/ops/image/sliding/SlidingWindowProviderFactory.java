package net.imglib2.ops.image.sliding;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.image.sliding.buffered.BufferedEfficientSlidingIntervalIterator;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.type.Type;

public class SlidingWindowProviderFactory
{
	//
	public static < T extends Type< T >, IN extends RandomAccessibleInterval< T >> SlidingWindowIteratorProvider< T, IN > createBufferedEfficientSlidingIntervalProvider( final Interval roi )
	{

		return new SlidingWindowIteratorProvider< T, IN >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, IN > fac, IN randomAccessible )
			{
				return new BufferedEfficientSlidingIntervalIterator< T, IN >( fac, randomAccessible, roi );
			}
		};
	}

	//
	public static < T extends Type< T >, IN extends RandomAccessibleInterval< T >> SlidingWindowIteratorProvider< T, IN > createEfficientSlidingIntervalProvider( final Interval roi )
	{

		return new SlidingWindowIteratorProvider< T, IN >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, IN > fac, IN randomAccessible )
			{
				return new EfficientSlidingIntervalIterator< T, IN >( fac, randomAccessible, roi );
			}
		};
	}

	//
	public static < T extends Type< T >, IN extends RandomAccessibleInterval< T >> SlidingWindowIteratorProvider< T, IN > createNaiveSlidingRoiProvider( final Interval roi )
	{

		return new SlidingWindowIteratorProvider< T, IN >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, IN > fac, IN randomAccessible )
			{
				return new NaiveSlidingIntervalIterator< T, IN >( fac, randomAccessible, roi );
			}
		};
	}

	//
	public static < T extends Type< T >, IN extends RandomAccessibleInterval< T >> SlidingWindowIteratorProvider< T, IN > createNaiveSlidingIntervalProvider( final IterableRegionOfInterest roi )
	{

		return new SlidingWindowIteratorProvider< T, IN >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, IN > fac, IN randomAccessible )
			{
				return new NaiveSlidingROIIterator< T, IN >( fac, randomAccessible, roi );
			}
		};
	}

}
