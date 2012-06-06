package net.imglib2.img.subimg;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.img.planar.PlanarCursor;
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

		// I can ask for numPlaneDims as only dims in a row starting from zero
		// are optimizable
		reset();

		if ( cursor instanceof PlanarCursor && numPlaneDims == 2 )
			m_offset = offset;
	}

	// public OptimizedCursor( Cursor< T > cursor, long[] srcDims, long[]
	// planePos, int[] planeDims )
	// {
	// this( cursor, calcNumEntries( planeDims ), calcOffset( srcDims,
	// planePos.length ), planePos.length );
	// }

	// private static int calcNumEntries( int[] planeDims )
	// {
	//
	// int numEntries = 1;
	// for ( int d = 0; d < planeDims.length; d++ )
	// {
	// if ( planeDims[ d ] != d ) { throw new IllegalArgumentException(
	// "Optimized Cursor is only possible if dimensions of selected hypercube are in one row starting from 0"
	// ); }
	// numEntries *= planeDims[ d ];
	// }
	//
	// return numEntries;
	// }
	//
	// private static int calcOffset( long[] srcDims, int numPlaneDims )
	// {
	// int offset = 1;
	//
	// for ( int d = numPlaneDims; d < srcDims.length; d++ )
	// offset *= srcDims[ d ];
	//
	// return offset;
	// }

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
