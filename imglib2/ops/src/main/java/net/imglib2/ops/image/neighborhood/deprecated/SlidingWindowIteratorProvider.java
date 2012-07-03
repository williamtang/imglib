package net.imglib2.ops.image.neighborhood.deprecated;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.Type;

public interface SlidingWindowIteratorProvider< T extends Type< T >, IN extends RandomAccessibleInterval< T >>
{

	SlidingWindowIterator< T > createSlidingWindowIterator( final OutOfBoundsFactory< T, IN > fac, final IN randomAccessible );
}
