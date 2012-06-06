package net.imglib2.img.subimg;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.type.NativeType;

public class IterableSubsetViewCursor< T extends NativeType< T >> implements Cursor< T >
{

	private Cursor< T > m_fullCursor;

	private int m_typeIdx = 0;

	private int m_offset;

	private int m_numElements;

	private int m_numPlaneDims;

	private T m_mainType;

	public IterableSubsetViewCursor( Cursor< T > cursor, int entries, int offset, int numPlaneDims )
	{
		m_fullCursor = cursor;
		m_numElements = entries;

		m_numPlaneDims = numPlaneDims;
		m_mainType = cursor.get();

		m_offset = offset;
		reset();

	}

	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
			position[ d ] = m_fullCursor.getFloatPosition( d );
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
			position[ d ] = m_fullCursor.getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return m_fullCursor.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return m_fullCursor.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return m_numPlaneDims;
	}

	@Override
	public T get()
	{
		return m_mainType;
	}

	@Override
	public Sampler< T > copy()
	{
		return m_fullCursor.copy();
	}

	@Override
	public void jumpFwd( long steps )
	{
		m_mainType.incIndex( ( int ) steps );
		m_typeIdx += steps;
	}

	@Override
	public void fwd()
	{
		m_mainType.incIndex();
		m_typeIdx++;
	}

	@Override
	public void reset()
	{
		m_typeIdx = -1;
		m_fullCursor.reset();
		m_fullCursor.jumpFwd( m_offset );
	}

	@Override
	public boolean hasNext()
	{
		return m_typeIdx < m_numElements - 1;
	}

	@Override
	public T next()
	{
		m_mainType.incIndex();
		m_typeIdx++;

		return m_mainType;
	}

	@Override
	public void remove()
	{
		// NO action
	}

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
		{
			position[ d ] = m_fullCursor.getIntPosition( d );
		}
	}

	@Override
	public void localize( long[] position )
	{
		for ( int d = 0; d < m_numPlaneDims; d++ )
		{
			position[ d ] = m_fullCursor.getLongPosition( d );
		}

	}

	@Override
	public int getIntPosition( int d )
	{
		return m_fullCursor.getIntPosition( d );
	}

	@Override
	public long getLongPosition( int d )
	{
		return m_fullCursor.getLongPosition( d );
	}

	@Override
	public Cursor< T > copyCursor()
	{
		return new IterableSubsetViewCursor< T >( m_fullCursor.copyCursor(), m_numElements, m_offset, m_numPlaneDims );
	}
}
