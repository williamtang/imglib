package net.imglib2.ops.image.neighborhood;

import java.util.ArrayList;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.type.Type;

public abstract class AbstractNeighborhood< T extends Type< T >> implements Neighborhood< T >
{

	protected final int numDimensions;

	// The cursors
	private final ArrayList< NeighborhoodCursor< T >> cursors;

	// Center
	protected long[] center;

	// The source random accessible
	protected RandomAccessible< T > source;

	public AbstractNeighborhood( final RandomAccessible< T > source, final Localizable center )
	{
		this.numDimensions = center.numDimensions();
		this.source = source;
		this.center = new long[ source.numDimensions() ];
		center.localize( this.center );

		this.cursors = new ArrayList< NeighborhoodCursor< T >>();
	}

	protected abstract NeighborhoodCursor< T > neighborhodCursor();

	@Override
	public void updateSource( RandomAccessible< T > source )
	{
		this.source = source;
	}

	@Override
	public Cursor< T > cursor()
	{
		// the new cursor
		NeighborhoodCursor< T > cursor = neighborhodCursor();

		// Center should be handled as reference
		cursor.updateCenter( center );

		// Cursor added to list of all cursors
		cursors.add( cursor );

		return cursor;
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}

	@Override
	public void fwd( int d )
	{
		center[ d ]++;
		updateCursors();
	}

	@Override
	public void bck( int d )
	{
		center[ d ]--;
		updateCursors();
	}

	@Override
	public void move( int distance, int d )
	{
		center[ d ] += distance;
		updateCursors();
	}

	@Override
	public void move( long distance, int d )
	{
		center[ d ] += distance;
		updateCursors();
	}

	@Override
	public void move( Localizable localizable )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] += localizable.getLongPosition( d );

		updateCursors();
	}

	@Override
	public void move( int[] distance )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] += distance[ d ];
		updateCursors();
	}

	@Override
	public void move( long[] distance )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] += distance[ d ];
		updateCursors();
	}

	@Override
	public void setPosition( Localizable localizable )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] = localizable.getLongPosition( d );
		updateCursors();
	}

	@Override
	public void setPosition( int[] position )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] = position[ d ];
		updateCursors();
	}

	@Override
	public void setPosition( long[] position )
	{
		for ( int d = 0; d < center.length; d++ )
			center[ d ] = position[ d ];
		updateCursors();
	}

	@Override
	public void setPosition( int position, int d )
	{
		center[ d ] = d;
		updateCursors();
	}

	@Override
	public void setPosition( long position, int d )
	{
		center[ d ] = d;
		updateCursors();
	}

	private void updateCursors()
	{
		for ( NeighborhoodCursor< T > cursor : cursors )
			cursor.updateCenter( center );
	}
}
