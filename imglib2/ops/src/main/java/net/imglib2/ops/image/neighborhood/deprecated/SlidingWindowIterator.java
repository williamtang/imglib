package net.imglib2.ops.image.neighborhood.deprecated;

import net.imglib2.type.Type;

/**
 * Interface providing methods to slide a window (or any other struture,
 * depending on the implemenation).
 * 
 * @author dietzc, hornm
 * 
 * @param <T>
 */
public interface SlidingWindowIterator< T extends Type< T >>
{

	/**
	 * Fwd to the next sliding window
	 */
	void fwd();

	/**
	 * Get the next sliding window
	 * 
	 * @return
	 */
	Iterable< T > next();

	/**
	 * 
	 * @return
	 */
	boolean hasNext();

	/**
	 * Get the current iterable over the sliding window
	 */
	Iterable< T > getIterable();

	/**
	 * 
	 */
	void reset();

}
