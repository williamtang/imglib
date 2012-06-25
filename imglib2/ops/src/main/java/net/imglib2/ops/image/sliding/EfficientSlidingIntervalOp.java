package net.imglib2.ops.image.sliding;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.subset.SubsetViews;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * 
 * (Hopefully) more efficient method to slide an interval over a
 * RandomAccessibleInterval
 * 
 * @author dietzc, horm
 * 
 * @param <T>
 */
public class EfficientSlidingIntervalOp< T extends Type< T >> implements SlidingWindowIterator< T >
{

	// Tmp
	private final long[] m_currentPos;

	private final long[] m_positionOffsets;

	private final long[] m_dims;

	// Iterations
	private final CursorRegionOfInterest m_fastRoiIterator;

	private Iterable< T > m_fastIterable;

	// Accessors
	private OutOfBounds< T > m_rndAccess;

	private final Cursor< T > m_cursor;

	public EfficientSlidingIntervalOp( final RandomAccessibleInterval< T > interval, Interval slidingInterval )
	{

		m_cursor = SubsetViews.iterableSubsetView( interval, interval, false ).cursor();
		m_rndAccess = Views.extendMirrorSingle( interval ).randomAccess();

		m_dims = new long[ interval.numDimensions() ];
		slidingInterval.dimensions( m_dims );

		int size = 1;

		for ( int d = 0; d < slidingInterval.numDimensions(); d++ )
			size *= m_dims[ d ];

		m_fastRoiIterator = new CursorRegionOfInterest( size );

		m_fastIterable = new Iterable< T >()
		{

			@Override
			public Iterator< T > iterator()
			{
				return m_fastRoiIterator;
			}
		};

		m_currentPos = new long[ interval.numDimensions() ];
		m_cursor.localize( m_currentPos );

		m_positionOffsets = new long[ interval.numDimensions() ];
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

			for ( int d = 0; d < m_dims.length; d++ )
				if ( m_doneSteps[ d ] < m_dims[ d ] )
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

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Unsupported" );
		}

	}
}
