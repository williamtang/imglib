package net.imglib2.ops.image.sliding;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.type.Type;

public class SlidingWindowProviderFactory
{
	//
	public static < T extends Type< T >> SlidingWindowIteratorProvider< T > createBufferedEfficientSlidingIntervalProvider( final Interval roi )
	{

		return new SlidingWindowIteratorProvider< T >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, RandomAccessibleInterval< T >> fac, RandomAccessibleInterval< T > randomAccessible )
			{
				return new BufferedEfficientSlidingIntervalIterator< T >( fac, randomAccessible, roi );
			}
		};
	}

	//
	public static < T extends Type< T >> SlidingWindowIteratorProvider< T > createEfficientSlidingIntervalProvider( final Interval roi )
	{

		return new SlidingWindowIteratorProvider< T >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, RandomAccessibleInterval< T >> fac, RandomAccessibleInterval< T > randomAccessible )
			{
				return new EfficientSlidingIntervalIterator< T >( fac, randomAccessible, roi );
			}
		};
	}

	//
	public static < T extends Type< T >> SlidingWindowIteratorProvider< T > createNaiveSlidingRoiProvider( final Interval roi )
	{

		return new SlidingWindowIteratorProvider< T >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, RandomAccessibleInterval< T >> fac, RandomAccessibleInterval< T > randomAccessible )
			{
				return new NaiveSlidingIntervalIterator< T >( fac, randomAccessible, roi );
			}
		};
	}

	//
	public static < T extends Type< T >> SlidingWindowIteratorProvider< T > createNaiveSlidingIntervalProvider( final IterableRegionOfInterest roi )
	{

		return new SlidingWindowIteratorProvider< T >()
		{

			@Override
			public SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, RandomAccessibleInterval< T >> fac, RandomAccessibleInterval< T > randomAccessible )
			{
				return new NaiveSlidingROIIterator< T >( fac, randomAccessible, roi );
			}
		};
	}

}
