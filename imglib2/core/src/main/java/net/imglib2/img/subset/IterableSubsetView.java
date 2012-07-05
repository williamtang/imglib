package net.imglib2.img.subset;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

public class IterableSubsetView< T extends Type< T >> extends IterableRandomAccessibleInterval< T >
{

	private IterableInterval< T > m_iterableIntervalSource;

	private IterableInterval< T > m_iterableIntervalView;

	private boolean m_isOptimizable;

	private int m_planeOffset;

	private int m_numPlaneDims;

	@SuppressWarnings( "unchecked" )
	public IterableSubsetView( RandomAccessibleInterval< T > src, Interval interval, boolean keepSizeOneDims )
	{
		super( SubsetViews.subsetView( src, interval, keepSizeOneDims ) );

		m_iterableIntervalSource = Views.iterable( src );
		m_iterableIntervalView = Views.iterable( super.interval );

		m_isOptimizable = false;
		m_planeOffset = 1;

		if ( SubsetViews.intervalEquals( src, interval ) )
			return;

		if ( ( src instanceof IterableInterval ) && ( ( IterableInterval< T > ) src ).iterationOrder() instanceof FlatIterationOrder )
		{
			m_isOptimizable = true;
			for ( int d = 0; d < interval.numDimensions(); d++ )
			{
				if ( interval.dimension( d ) > 1 )
				{

					// TODO: this can be handled in the IterableSubsetViewCursor
					// (hasNext and fwd must be generalized)
					if ( interval.dimension( d ) != src.dimension( d ) )
					{
						m_isOptimizable = false;
						break;
					}

					m_numPlaneDims++;

					if ( m_numPlaneDims != d + 1 )
					{
						m_isOptimizable = false;
						break;
					}
				}

			}

			if ( m_isOptimizable )
			{

				long[] iterDims = new long[ src.numDimensions() - m_numPlaneDims ];
				long[] cubePos = iterDims.clone();
				for ( int d = m_numPlaneDims; d < src.numDimensions(); d++ )
				{
					iterDims[ d - m_numPlaneDims ] = src.dimension( d );
					cubePos[ d - m_numPlaneDims ] = interval.min( d );
				}

				if ( iterDims.length == 0 )
				{
					m_planeOffset = 0;
				}
				else
				{
					m_planeOffset = ( int ) ( IntervalIndexer.positionToIndex( cubePos, iterDims ) * super.size() );

				}

			}

		}

	}

	@Override
	public Cursor< T > cursor()
	{
		if ( m_isOptimizable )
			return new IterableSubsetViewCursor< T >( m_iterableIntervalSource.cursor(), ( int ) super.size(), m_planeOffset, m_numPlaneDims );
		else
			return m_iterableIntervalView.cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		if ( m_isOptimizable )
			return new IterableSubsetViewCursor< T >( m_iterableIntervalSource.localizingCursor(), ( int ) super.size(), m_planeOffset, m_numPlaneDims );
		else
			return m_iterableIntervalView.localizingCursor();
	}

}
