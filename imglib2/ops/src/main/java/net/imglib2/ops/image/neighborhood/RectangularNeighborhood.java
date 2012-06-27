//package net.imglib2.ops.image.neighborhood;
//
//import net.imglib2.IterableInterval;
//import net.imglib2.Localizable;
//import net.imglib2.RandomAccess;
//import net.imglib2.type.Type;
//
//public class RectangularNeighborhood< T extends Type< T >> implements Neighborhood< T >
//{
//
//	private long[] m_radii;
//
//	private long[] m_center;
//
//	public RectangularNeighborhood( long[] center, long[] radii )
//	{
//		m_radii = radii.clone();
//		m_center = center.clone();
//	}
//
//	@Override
//	public void localize( int[] position )
//	{
//		for ( int d = 0; d < position.length; d++ )
//		{
//			position[ d ] = ( int ) m_center[ d ];
//		}
//	}
//
//	@Override
//	public void localize( long[] position )
//	{
//		for ( int d = 0; d < position.length; d++ )
//		{
//			position[ d ] = m_center[ d ];
//		}
//	}
//
//	@Override
//	public int getIntPosition( int d )
//	{
//		return ( int ) m_center[ d ];
//	}
//
//	@Override
//	public long getLongPosition( int d )
//	{
//		return m_center[ d ];
//	}
//
//	@Override
//	public void localize( float[] position )
//	{
//		for ( int d = 0; d < position.length; d++ )
//		{
//			position[ d ] = m_center[ d ];
//		}
//	}
//
//	@Override
//	public void localize( double[] position )
//	{
//		for ( int d = 0; d < position.length; d++ )
//		{
//			position[ d ] = m_center[ d ];
//		}
//	}
//
//	@Override
//	public float getFloatPosition( int d )
//	{
//		return m_center[ d ];
//	}
//
//	@Override
//	public double getDoublePosition( int d )
//	{
//		return m_center[ d ];
//	}
//
//	@Override
//	public int numDimensions()
//	{
//		return m_center.length;
//	}
//
//	@Override
//	public void fwd( int d )
//	{
//		m_center[ d ]++;
//	}
//
//	@Override
//	public void bck( int d )
//	{
//		m_center[ d ]--;
//	}
//
//	@Override
//	public void move( int distance, int d )
//	{
//		m_center[ d ] += distance;
//	}
//
//	@Override
//	public void move( long distance, int d )
//	{
//		m_center[ d ] += distance;
//	}
//
//	@Override
//	public void move( Localizable localizable )
//	{
//		for ( int d = 0; d < localizable.numDimensions(); d++ )
//		{
//			m_center[ d ] += localizable.getLongPosition( d );
//		}
//	}
//
//	@Override
//	public void move( int[] distance )
//	{
//		for ( int d = 0; d < distance.length; d++ )
//		{
//			m_center[ d ] += distance[ d ];
//		}
//
//	}
//
//	@Override
//	public void move( long[] distance )
//	{
//		for ( int d = 0; d < distance.length; d++ )
//		{
//			m_center[ d ] += distance[ d ];
//		}
//	}
//
//	@Override
//	public void setPosition( Localizable localizable )
//	{
//		for ( int d = 0; d < localizable.numDimensions(); d++ )
//		{
//			m_center[ d ] = localizable.getLongPosition( d );
//		}
//	}
//
//	@Override
//	public void setPosition( int[] position )
//	{
//		for ( int d = 0; d < position.length; d++ )
//		{
//			m_center[ d ] = position[ d ];
//		}
//	}
//
//	@Override
//	public void setPosition( long[] position )
//	{
//		for ( int d = 0; d < position.length; d++ )
//		{
//			m_center[ d ] = position[ d ];
//		}
//	}
//
//	@Override
//	public void setPosition( int position, int d )
//	{
//		m_center[ d ] = position;
//
//	}
//
//	@Override
//	public void setPosition( long position, int d )
//	{
//		m_center[ d ] = position;
//	}
//
//	@Override
//	public IterableInterval< T > getIterableInterval( RandomAccess< T > randomAccess )
//	{
//		return new RectangularNeighborhodIterableInterval();
//	}
// }
