package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.outofbounds.OutOfBoundsFactory;

public class HyperSphereNeighborhood<T> extends AbstractNeighborhood<T> {

	protected final OutOfBoundsFactory<T, RandomAccessibleInterval<T>> outOfBounds;
	private long radius;

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
		this.setPosition(center);
		this.radius = radius;
	}

	@Override
	public HyperSphereCursor<T> cursor() {
		return new HyperSphereCursor<T>(extendedSource, center, radius);
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
		return computeSize();
	}

	/**
	 * Compute the number of elements for iteration
	 */
	protected long computeSize() {
		final HyperSphereCursor<T> cursor = new HyperSphereCursor<T>(source,
				this.center, radius);

		// "compute number of pixels"
		long size = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			++size;
		}

		return size;
	}

}
