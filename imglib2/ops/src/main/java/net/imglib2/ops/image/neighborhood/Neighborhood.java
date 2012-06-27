package net.imglib2.ops.image.neighborhood;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.type.Type;

public interface Neighborhood< T extends Type< T >> extends Localizable, Positionable
{
	public IterableInterval< T > getIterableInterval( RandomAccess< T > randomAccess );
}
