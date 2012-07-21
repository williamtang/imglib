package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.outofbounds.OutOfBoundsFactory;

public class HyperSphereNeighborhood<T> extends AbstractNeighborhood<T> {

	protected final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds;

	/** The radius of the sphere, in calibrated units. */
	protected long radius;

	private HyperSphere<T> sphere;

	/*
	 * CONSTRUCTORS
	 */
	public HyperSphereNeighborhood(
			final RandomAccessibleInterval<T> source,
			final long radius,
			final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds,
			Localizable center) {
		super(source, outOfBounds);
		this.outOfBounds = outOfBounds;
		this.sphere = new HyperSphere<T>(extendedSource, center, radius);
	}

	/*
	 * METHODS
	 */

	/**
	 * Overridden not to do anything.
	 * 
	 * @see #setRadius(double)
	 */
	@Override
	public void setSpan(long[] span) {

		// Since now this is an ugly hack, actually setSpan shouldn't have the
		// setSpan method
		sphere.updateRadius(span[0]);
	}

	/**
	 * Change the radius of this neighborhood.
	 */
	public void setRadius(long radius) {

		// More dimensions should be supported
		sphere.updateRadius(radius);
	}

	@Override
	public HyperSphereCursor<T> cursor() {
		return sphere.cursor();
	}

	@Override
	public HyperSphereCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public HyperSphereCursor<T> iterator() {
		return cursor();
	}

	@Override
	public long size() {
		return sphere.size();
	}

}
