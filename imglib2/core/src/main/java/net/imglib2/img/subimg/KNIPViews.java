package net.imglib2.img.subimg;

import java.util.Arrays;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.MixedTransformView;
import net.imglib2.view.Views;

public class KNIPViews
{
	/**
	 * View on interval of a source img. If needed, dims with size one can be
	 * removed.
	 * 
	 * @param src
	 *            Source img for the view
	 * @param interval
	 *            Interval which will be used for to create this view
	 * @param keepDimsWithSizeOne
	 *            If false, dimensions with size one will be virtually removed
	 *            from the resulting view
	 * @return
	 */
	public static final < T extends Type< T > > RandomAccessibleInterval< T > getSubsetView( final RandomAccessibleInterval< T > src, final Interval interval, final boolean keepDimsWithSizeOne )
	{
		RandomAccessibleInterval< T > res = src;

		if ( Util.contains( res, interval ) )
			res = Views.offsetInterval( res, interval );

		if ( !keepDimsWithSizeOne )
			for ( int d = interval.numDimensions() - 1; d >= 0; --d )
				if ( interval.dimension( d ) == 1 && res.numDimensions() > 1 )
					res = Views.hyperSlice( res, d, 0 );

		return res;
	}

	public static final < T extends Type< T > > RandomAccessibleInterval< T > getExtendedDimensionalityView( final RandomAccessibleInterval< T > src, final Interval target )
	{

		if ( src.numDimensions() >= target.numDimensions() )
			return src;

		RandomAccessibleInterval< T > res = src;
		for ( int d = src.numDimensions(); d < target.numDimensions(); d++ )
		{
			res = addDimension( res, target.min( d ), target.max( d ) );
		}

		return Views.interval( Views.extendBorder( res ), target );
	}

	public static < T > MixedTransformView< T > addDimension( final RandomAccessible< T > view )
	{
		final int m = view.numDimensions();
		final int n = m + 1;
		final MixedTransform t = new MixedTransform( n, m );
		return new MixedTransformView< T >( view, t );
	}

	public static < T > IntervalView< T > addDimension( final RandomAccessibleInterval< T > view, final long minOfNewDim, final long maxOfNewDim )
	{
		final int m = view.numDimensions();
		final long[] min = new long[ m + 1 ];
		final long[] max = new long[ m + 1 ];
		for ( int d = 0; d < m; ++d )
		{
			min[ d ] = view.min( d );
			max[ d ] = view.max( d );
		}
		min[ m ] = minOfNewDim;
		max[ m ] = maxOfNewDim;
		return Views.interval( addDimension( view ), min, max );
	}

	public static < T > RandomAccessibleInterval< T > synchronizeDimensionality( final RandomAccessibleInterval< T > src, final Interval target )
	{
		RandomAccessibleInterval< T > res = src;

		long[] resDims = new long[ src.numDimensions() ];
		long[] targetDims = new long[ target.numDimensions() ];

		res.dimensions( resDims );
		target.dimensions( targetDims );

		// Check direction of conversion
		if ( res.numDimensions() == target.numDimensions() && Arrays.equals( resDims, targetDims ) )
			return res;

		// adjust dimensions
		if ( res.numDimensions() < target.numDimensions() )
		{
			for ( int d = res.numDimensions(); d < target.numDimensions(); d++ )
			{
				res = addDimension( res, target.min( d ), target.max( d ) );
			}
		}
		else
		{
			for ( int d = res.numDimensions() - 1; d >= target.numDimensions(); --d )
				res = Views.hyperSlice( res, d, 0 );
		}

		resDims = new long[ res.numDimensions() ];
		res.dimensions( resDims );

		// now targetDims.length == resDims.length
		// Dimensionality needs to be adjusted

		return Views.interval( res.numDimensions() < target.numDimensions() ? Views.extendBorder( res ) : res, target );

	}

}
