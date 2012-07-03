package net.imglib2.display;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;

public class Projector2D< A, B > extends Abstract2DProjector< A, B > {
	
	final Converter< A, B > converter;	
	final protected IterableInterval< B > target;
	final int numDimensions;
	private final int dimX;
	private final int dimY;
	
	final int X = 0;
	final int Y = 1;
	
	public Projector2D(final int dimX, final int dimY, final RandomAccessible< A > source, final IterableInterval< B > target, final Converter< A, B > converter )
	{
		super(source);
		this.dimX = dimX;
		this.dimY = dimY;
		this.target = target;
		this.converter = converter;
		this.numDimensions = source.numDimensions();
	}


	@Override
	public void map()
	{
		//fix interval for all dimensions
		for ( int d = 0; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];
		
		
		min[dimX] = target.min(X);
		min[dimY] = target.min(Y);
		max[dimX] = target.max(X);
		max[dimY] = target.max(Y);
		final FinalInterval sourceInterval = new FinalInterval( min, max );

		final Cursor< B > targetCursor = target.localizingCursor();
		final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );
		sourceRandomAccess.setPosition( position );
		if ( numDimensions > 1 )
			while ( targetCursor.hasNext() )
			{
				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition(X), dimX );
				sourceRandomAccess.setPosition( targetCursor.getLongPosition(Y), dimY );
				converter.convert( sourceRandomAccess.get(), b );
			}
		else
			while ( targetCursor.hasNext() )
			{
				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition(X), dimX );
				converter.convert( sourceRandomAccess.get(), b );
			}
	}
}
