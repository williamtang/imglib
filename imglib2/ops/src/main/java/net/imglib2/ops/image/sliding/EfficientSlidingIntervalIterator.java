package net.imglib2.ops.image.sliding;

import java.util.Iterator;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.LocalizingIntervalIterator;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.Type;

public class EfficientSlidingIntervalIterator< T extends Type< T >> implements SlidingWindowIterator< T >
{

	private LocalizingIntervalIterator m_cursor;

	private CursorRegionOfInterest m_fastRoiIterator;

	private Iterable< T > m_fastIterable;

	private OutOfBounds< T > m_rndAccess;

	private long[] m_roiDims;

	private long[] m_positionOffsets;

	protected EfficientSlidingIntervalIterator( final OutOfBoundsFactory< T, RandomAccessibleInterval< T >> fac, RandomAccessibleInterval< T > randomAccessible, Interval slidingInterval )
	{
		m_cursor = new LocalizingIntervalIterator( randomAccessible );
		m_rndAccess = fac.create( randomAccessible );

		m_fastIterable = new Iterable< T >()
		{

			@Override
			public Iterator< T > iterator()
			{
				return m_fastRoiIterator;
			}
		};

		m_roiDims = new long[ randomAccessible.numDimensions() ];
		slidingInterval.dimensions( m_roiDims );

		int size = 1;

		for ( int d = 0; d < slidingInterval.numDimensions(); d++ )
			size *= m_roiDims[ d ];

		m_fastRoiIterator = new CursorRegionOfInterest( size );

		m_fastIterable = new Iterable< T >()
		{

			@Override
			public Iterator< T > iterator()
			{
				return m_fastRoiIterator;
			}
		};

		m_positionOffsets = new long[ randomAccessible.numDimensions() ];
		for ( int d = 0; d < m_positionOffsets.length; d++ )
		{
			m_positionOffsets[ d ] = slidingInterval.max( d ) / 2;
		}

	}

	@Override
	public boolean hasNext()
	{
		return m_cursor.hasNext();
	}

	@Override
	public void fwd()
	{
		m_cursor.fwd();
		m_fastRoiIterator.fwdRndAccess();
	}

	@Override
	public Iterable< T > getIterable()
	{
		m_fastRoiIterator.reset();
		return m_fastIterable;
	}

	@Override
	public Iterable< T > next()
	{
		fwd();
		return getIterable();
	}

	private class CursorRegionOfInterest implements Iterator< T >
	{

		private final long[] m_doneSteps;

		private final int m_numElements;

		private int m_idx;

		public CursorRegionOfInterest( int numElements )
		{
			m_numElements = numElements;
			m_doneSteps = new long[ m_cursor.numDimensions() ];
		}

		@Override
		public boolean hasNext()
		{
			return m_idx < m_numElements;
		}

		@Override
		public T next()
		{
			m_idx++;

			for ( int d = 0; d < m_roiDims.length; d++ )
				if ( m_doneSteps[ d ] < m_roiDims[ d ] )
				{
					m_doneSteps[ d ]++;
					m_rndAccess.fwd( d );
					break;
				}
				else
				{
					m_rndAccess.move( -( m_doneSteps[ d ] - 1 ), d );
					m_doneSteps[ d ] = 1;
				}

			return m_rndAccess.get();
		}

		private void fwdRndAccess()
		{
			m_idx = 0;
			for ( int d = 0; d < m_cursor.numDimensions(); d++ )
			{
				m_rndAccess.setPosition( m_cursor.getIntPosition( d ) - m_positionOffsets[ d ], d );
				m_doneSteps[ d ] = 0;
			}

			// Set one backward to start in negativ (cursor
			// behaviour)
			m_rndAccess.bck( 0 );
		}

		private void reset()
		{
			m_idx = 0;
			for ( int d = 0; d < m_cursor.numDimensions(); d++ )
			{
				m_rndAccess.move( -m_doneSteps[ d ] - 1, d );
				m_doneSteps[ d ] = 0;
			}
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Unsupported" );
		}

	}

	@Override
	public void reset()
	{
		m_cursor.reset();
		m_rndAccess.setPosition( m_cursor );
		m_fastRoiIterator.reset();
	}
}
