package net.imglib2.ops.image.sliding;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.iterator.LocalizingIntervalIterator;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * 
 * Naive implementation of a sliding {@link Interval}.
 * 
 */
public class NaiveSlidingIntervalOp< T extends Type< T >> implements SlidingWindowIterator< T >
{

	private final LocalizingIntervalIterator m_intervalIterator;

	private final long[] m_displacement;

	private final ExtendedRandomAccessibleInterval< T, RandomAccessibleInterval< T >> m_rndAccessible;

	private final long[] m_min;

	private final long[] m_max;

	private final MoveableInterval m_moveableInterval;

	private final long[] m_dims;

	public NaiveSlidingIntervalOp( RandomAccessibleInterval< T > rndAccessible, final Interval interval )
	{
		m_intervalIterator = new LocalizingIntervalIterator( rndAccessible );
		m_rndAccessible = Views.extendBorder( rndAccessible );
		m_displacement = new long[ m_rndAccessible.numDimensions() ];

		for ( int d = 0; d < m_displacement.length; d++ )
		{
			m_displacement[ d ] = ( long ) Math.floor( interval.dimension( d ) / 2 );
		}

		m_dims = new long[ m_rndAccessible.numDimensions() ];
		m_min = new long[ m_rndAccessible.numDimensions() ];

		// Set it to behave like a cursor
		m_min[ 0 ] = -1;
		m_max = new long[ m_rndAccessible.numDimensions() ];

		interval.max( m_max );
		interval.dimensions( m_dims );

		m_moveableInterval = new MoveableInterval();
	}

	@Override
	public boolean hasNext()
	{
		return m_intervalIterator.hasNext();
	}

	@Override
	public void fwd()
	{
		m_intervalIterator.fwd();

		for ( int d = 0; d < m_displacement.length; d++ )
		{
			m_min[ d ] = m_intervalIterator.getLongPosition( d ) - m_displacement[ d ];
			m_max[ d ] = m_min[ d ] + m_dims[ d ] - 1;
		}

	}

	@Override
	public Iterable< T > getIterable()
	{
		return Views.iterable( Views.interval( m_rndAccessible, m_moveableInterval ) );
	}

	@Override
	public Iterable< T > next()
	{
		fwd();
		return getIterable();
	}

	class MoveableInterval implements Interval
	{

		@Override
		public double realMin( int d )
		{
			return m_min[ d ];
		}

		@Override
		public void realMin( double[] min )
		{
			for ( int d = 0; d < m_min.length; d++ )
				min[ d ] = m_min[ d ];
		}

		@Override
		public void realMin( RealPositionable min )
		{
			for ( int d = 0; d < m_min.length; d++ )
				min.setPosition( m_min[ d ], d );

		}

		@Override
		public double realMax( int d )
		{
			return m_max[ d ];
		}

		@Override
		public void realMax( double[] max )
		{
			for ( int d = 0; d < m_max.length; d++ )
				max[ d ] = m_max[ d ];
		}

		@Override
		public void realMax( RealPositionable max )
		{
			for ( int d = 0; d < m_max.length; d++ )
				max.setPosition( m_max[ d ], d );

		}

		@Override
		public int numDimensions()
		{
			return m_min.length;
		}

		@Override
		public long min( int d )
		{
			return m_min[ d ];
		}

		@Override
		public void min( long[] min )
		{
			for ( int d = 0; d < m_min.length; d++ )
				min[ d ] = m_min[ d ];

		}

		@Override
		public void min( Positionable min )
		{
			for ( int d = 0; d < m_min.length; d++ )
				min.setPosition( m_min[ d ], d );

		}

		@Override
		public long max( int d )
		{
			return m_max[ d ];
		}

		@Override
		public void max( long[] max )
		{
			for ( int d = 0; d < m_max.length; d++ )
				max[ d ] = m_max[ d ];
		}

		@Override
		public void max( Positionable max )
		{
			for ( int d = 0; d < m_max.length; d++ )
				max.setPosition( m_max[ d ], d );
		}

		@Override
		public void dimensions( long[] dimensions )
		{
			for ( int d = 0; d < m_min.length; d++ )
				dimensions[ d ] = m_max[ d ] - m_min[ d ] + 1;

		}

		@Override
		public long dimension( int d )
		{
			return m_max[ d ] - m_min[ d ] + 1;
		}

	}
}
