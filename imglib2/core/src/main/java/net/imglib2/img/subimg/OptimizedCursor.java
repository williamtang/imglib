package net.imglib2.img.subimg;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.type.NativeType;

public class OptimizedCursor< T extends NativeType< T >> implements Cursor< T >
{

	private Cursor< T > m_cursor;

	private int m_offset;

	private int m_entries;

	private int m_numDims;

	private int m_idx;

	public OptimizedCursor( Cursor< T > cursor, int entries, int cubeIdx, int numDims )
	{
		m_entries = entries;
		m_offset = cubeIdx * m_entries;
		m_cursor = cursor;
		m_numDims = numDims;

		m_idx = -1;

		reset();
	}

	public OptimizedCursor( Cursor< T > cursor, long[] srcDims, long[] planePos, int[] planeDims )
	{
		this( cursor, calcNumEntries( planeDims ), calcOffset( srcDims, planePos.length ), planePos.length );
	}

	private static int calcNumEntries( int[] planeDims )
	{

		int numEntries = 1;
		for ( int d = 0; d < planeDims.length; d++ )
		{
			if ( planeDims[ d ] != d ) { throw new IllegalArgumentException( "Optimized Cursor is only possible if dimensions of selected hypercube are in one row starting from 0" ); }
			numEntries *= planeDims[ d ];
		}

		return numEntries;
	}

	private static int calcOffset( long[] srcDims, int numPlaneDims )
	{
		int offset = 1;

		for ( int d = numPlaneDims; d < srcDims.length; d++ )
		{
			offset *= srcDims[ d ];
		}

		return offset;
	}

	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < m_numDims; d++ )
		{
			position[ d ] = m_cursor.getFloatPosition( d );
		}
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < m_numDims; d++ )
		{
			position[ d ] = m_cursor.getDoublePosition( d );
		}
	}

	@Override
	public float getFloatPosition( int d )
	{
		return m_cursor.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return m_cursor.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return m_numDims;
	}

	@Override
	public T get()
	{
		return m_cursor.get();
	}

	@Override
	public Sampler< T > copy()
	{
		return m_cursor.copy();
	}

	@Override
	public void jumpFwd( long steps )
	{
		m_cursor.jumpFwd( ( int ) steps );
	}

	@Override
	public void fwd()
	{
		m_idx++;
		m_cursor.fwd();
	}

	@Override
	public void reset()
	{
		m_idx = -1;
		m_cursor.reset();
		m_cursor.jumpFwd( m_offset );
	}

	@Override
	public boolean hasNext()
	{
		return m_idx < m_entries - 1;
	}

	@Override
	public T next()
	{
		fwd();
		return m_cursor.get();
	}

	@Override
	public void remove()
	{
		// NO action
	}

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < m_numDims; d++ )
		{
			position[ d ] = m_cursor.getIntPosition( d );
		}
	}

	@Override
	public void localize( long[] position )
	{
		for ( int d = 0; d < m_numDims; d++ )
		{
			position[ d ] = m_cursor.getLongPosition( d );
		}

	}

	@Override
	public int getIntPosition( int d )
	{
		return m_cursor.getIntPosition( d );
	}

	@Override
	public long getLongPosition( int d )
	{
		return m_cursor.getLongPosition( d );
	}

	@Override
	public Cursor< T > copyCursor()
	{
		return new OptimizedCursor< T >( m_cursor.copyCursor(), m_entries, m_offset, m_numDims );
	}
}
