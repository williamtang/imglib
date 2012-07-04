package net.imglib2.ops.image.neighborhood;

import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.type.Type;

public abstract class AbstractNeighborhood< T extends Type< T >> implements Neighborhood< T >
{

	protected final int numDimensions;

	// Center
	protected long[] center;

	// The source random accessible
	protected RandomAccessible< T > source;

	public AbstractNeighborhood( final Localizable center )
	{
		this.numDimensions = center.numDimensions();
		this.center = new long[ center.numDimensions() ];
		center.localize( this.center );

	}

	@Override
	public void fwd( int d )
	{
		center[ d ]++;
	}

	@Override
	public void bck( int d )
	{
		center[ d ]--;
	}

	@Override
	public void move( int distance, int d )
	{
		center[ d ] += distance;
	}

	@Override
	public void move( long distance, int d )
	{
		center[ d ] += distance;
	}

	@Override
	public void move( Localizable localizable )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] += localizable.getLongPosition( d );

	}

	@Override
	public void move( int[] distance )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] += distance[ d ];
	}

	@Override
	public void move( long[] distance )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] += distance[ d ];
	}

	@Override
	public void setPosition( Localizable localizable )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] = localizable.getLongPosition( d );
	}

	@Override
	public void setPosition( int[] position )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] = position[ d ];
	}

	@Override
	public void setPosition( long[] position )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] = position[ d ];
	}

	@Override
	public void setPosition( int position, int d )
	{
		center[ d ] = d;
	}

	@Override
	public void setPosition( long position, int d )
	{
		center[ d ] = d;
	}

	@Override
	public void updateSource( RandomAccessible< T > source )
	{
		this.source = source;
	}
}
