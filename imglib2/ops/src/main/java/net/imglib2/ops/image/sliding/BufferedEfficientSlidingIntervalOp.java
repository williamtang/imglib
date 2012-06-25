package net.imglib2.ops.image.sliding;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.LocalizingIntervalIterator;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * Efficient, buffered method to slide an {@link Interval} over an
 * {@link RandomAccessibleInterval}
 * 
 * @author dietzc, hornm
 * 
 * @param <T>
 */
public class BufferedEfficientSlidingIntervalOp< T extends Type< T >> implements SlidingWindowIterator< T >
{

	// Tmp
	private final long[] m_currentPos;

	private final long[] m_positionOffsets;

	private final long[] m_dims;

	// Iteration
	private final CursorRegionOfInterest m_fastRoiIterator;

	private Iterable< T > m_fastIterable;

	// Access
	private final LocalizingIntervalIterator m_cursor;

	private OutOfBounds< T > m_rndAccess;

	// Buffer
	private int m_activeDim;

	private T[] m_buffer;

	private long[] m_bufferElements;

	private int m_bufferOffset;

	private int m_size;

	@SuppressWarnings( "unchecked" )
	public BufferedEfficientSlidingIntervalOp( final RandomAccessibleInterval< T > interval, Interval roi )
	{

		m_cursor = new LocalizingIntervalIterator( interval );
		m_rndAccess = Views.extendMirrorSingle( interval ).randomAccess();

		m_dims = new long[ interval.numDimensions() ];
		roi.dimensions( m_dims );

		m_size = 1;
		m_activeDim = -1;

		for ( int d = 0; d < roi.numDimensions(); d++ )
			m_size *= m_dims[ d ];

		m_fastRoiIterator = new CursorRegionOfInterest();

		m_fastIterable = new Iterable< T >()
		{

			@Override
			public Iterator< T > iterator()
			{
				return m_fastRoiIterator;
			}
		};

		m_bufferElements = new long[ interval.numDimensions() ];
		Arrays.fill( m_bufferElements, 1 );
		m_currentPos = new long[ interval.numDimensions() ];

		m_positionOffsets = new long[ interval.numDimensions() ];
		for ( int d = 0; d < m_positionOffsets.length; d++ )
		{
			m_positionOffsets[ d ] = ( int ) Math.floor( roi.max( d ) / 2 );

			for ( int dd = 0; dd < m_positionOffsets.length; dd++ )
			{
				if ( dd != d )
					m_bufferElements[ d ] *= roi.dimension( d );
			}

		}

		T type = interval.randomAccess().get();
		m_buffer = ( T[] ) Array.newInstance( type.getClass(), m_size );

		for ( int t = 0; t < m_buffer.length; t++ )
		{
			m_buffer[ t ] = type.copy();
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

		// Active dim
		m_activeDim = -1;

		// Current pos is always the upper left corner!
		for ( int d = 0; d < m_currentPos.length; d++ )
		{
			long tmp = m_currentPos[ d ];
			m_currentPos[ d ] = m_cursor.getIntPosition( d );

			if ( m_currentPos[ d ] - tmp != 0 )
			{
				if ( m_activeDim != -1 )
				{
					m_activeDim = -1;
					break;
				}
				m_activeDim = d;
			}

		}

		if ( m_activeDim >= 0 && m_bufferOffset + m_bufferElements[ m_activeDim ] < m_buffer.length )
			m_bufferOffset += m_bufferElements[ m_activeDim ];
		else
			m_bufferOffset = 0;

		// Synchronize RoiIterator with cursor
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

		private int m_idx;

		private int m_bufferPtr;

		public CursorRegionOfInterest()
		{
			m_doneSteps = new long[ m_cursor.numDimensions() ];
		}

		@Override
		public boolean hasNext()
		{
			return m_idx < m_size;
		}

		@Override
		public T next()
		{
			if ( m_bufferPtr >= m_buffer.length )
				m_bufferPtr = 0;

			if ( m_activeDim < 0 || m_idx >= m_buffer.length - m_bufferElements[ m_activeDim ] )
			{
				for ( int d = m_dims.length - 1; d > -1; d-- )
				{
					if ( d == m_activeDim )
						continue;

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
				}

				m_buffer[ m_bufferPtr ].set( m_rndAccess.get() );

			}
			m_idx++;

			return m_buffer[ m_bufferPtr++ ];
		}

		private void fwdRndAccess()
		{

			m_idx = 0;
			m_bufferPtr = m_bufferOffset;
			for ( int d = 0; d < m_cursor.numDimensions(); d++ )
			{
				if ( d == m_activeDim )
					m_rndAccess.setPosition( m_cursor.getIntPosition( d ) + m_positionOffsets[ d ], d );
				else
					m_rndAccess.setPosition( m_cursor.getIntPosition( d ) - m_positionOffsets[ d ], d );
				m_doneSteps[ d ] = 0;
			}

			m_rndAccess.bck( m_activeDim == m_dims.length ? m_activeDim - 1 : m_dims.length - 1 );

		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Unsupported" );
		}

	}
}
