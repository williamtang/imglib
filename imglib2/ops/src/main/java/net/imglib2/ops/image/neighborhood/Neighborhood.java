package net.imglib2.ops.image.neighborhood;

import net.imglib2.IterableInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;

public interface Neighborhood< T > extends IterableInterval< T >, Positionable
{
	// Set new neighborhood
	void updateSource( RandomAccessible< T > source );

	//
	Neighborhood< T > copy();
}
