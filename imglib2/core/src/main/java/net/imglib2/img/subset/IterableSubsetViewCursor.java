package net.imglib2.img.subset;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.type.Type;

public class IterableSubsetViewCursor< T extends Type< T >> implements Cursor< T >
{

	private Cursor< T > m_cursor;

	private int m_typeIdx = 0;

	private int m_planePos;

	private int m_planeSize;

	private int m_numPlaneDims;

	private T m_type;

	public IterableSubsetViewCursor( Cursor< T > cursor, int planeSize, int planePos, int numPlaneDims )
	{
		m_cursor = cursor;
		m_planeSize = planeSize;
		m_planePos = planePos;

		m_numPlaneDims = numPlaneDims;

		m_type = cursor.get();

		reset();
	}

	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
			position[ d ] = m_cursor.getFloatPosition( d );
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
			position[ d ] = m_cursor.getDoublePosition( d );
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
		return m_numPlaneDims;
	}

	@Override
	public T get()
	{
		return m_type;
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
		m_typeIdx += steps;
	}

	@Override
	public void fwd()
	{
		m_cursor.fwd();
		m_typeIdx++;
	}

	@Override
	public void reset()
	{
		m_cursor.reset();
		m_cursor.jumpFwd( m_planePos );
		m_typeIdx = -1;
	}

	@Override
	public boolean hasNext()
	{
		return m_typeIdx < m_planeSize - 1;
	}

	@Override
	public T next()
	{
		m_cursor.fwd();
		m_typeIdx++;

		return m_type;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException( "Remove not supported in class: SubsetViewCursor" );
	}

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
		{
			position[ d ] = m_cursor.getIntPosition( d );
		}
	}

	@Override
	public void localize( long[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
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
		return new IterableSubsetViewCursor< T >( m_cursor.copyCursor(), m_planeSize, m_planePos, m_numPlaneDims );
	}
}
