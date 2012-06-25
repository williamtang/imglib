package net.imglib2.ops.image.sliding;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;

public interface SlidingWindowIteratorProvider< T extends Type< T >>
{

	SlidingWindowIterator< T > createSlidingWindowIterator( final RandomAccessibleInterval< T > randomAccessible );
}
