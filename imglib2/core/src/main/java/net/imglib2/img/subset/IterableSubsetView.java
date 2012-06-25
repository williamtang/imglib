package net.imglib2.img.subset;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
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

	public IterableSubsetView( RandomAccessibleInterval< T > src, Interval interval, boolean keepSizeOneDims )
	{
		super( SubsetViews.subsetView( src, interval, keepSizeOneDims ) );

		m_iterableIntervalSource = Views.iterable( src );
		m_iterableIntervalView = Views.iterable( super.interval );

		m_isOptimizable = false;
		m_planeOffset = 1;

		if ( SubsetViews.intervalEquals( src, interval ) )
			return;

		if ( ( src instanceof IterableInterval ) )
		{
			m_isOptimizable = true;
			for ( int d = 0; d < interval.numDimensions(); d++ )
			{
				if ( interval.dimension( d ) > 1 )
				{
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

	public static void main( String[] args )
	{

		callTest( true, 10 );
		callTest( false, 10 );

	}

	private static void callTest( boolean std, int numRuns )
	{
		int numPlaneDims = 2;

		long[] dims = new long[] { 1000, 1000, 1 };
		long[] iterationDims = new long[ dims.length - numPlaneDims ];

		for ( int d = 0; d < iterationDims.length; d++ )
		{
			iterationDims[ d ] = dims[ d + numPlaneDims ];
		}

		int numPlanes = 1;
		for ( int i = numPlaneDims; i < dims.length; i++ )
		{
			numPlanes *= dims[ i ];
		}

		Img< IntType > srcImg = new PlanarImgFactory< IntType >().create( dims, new IntType() );
		long curr = System.nanoTime();

		for ( int k = 0; k < numRuns; k++ )
			for ( int i = 0; i < numPlanes; i++ )
			{
				long[] pos = new long[ dims.length - numPlaneDims ];
				IntervalIndexer.indexToPosition( i, iterationDims, pos );

				long[] min = new long[ dims.length ];
				long[] max = new long[ dims.length ];

				for ( int d = 0; d < dims.length; d++ )
				{
					if ( d < numPlaneDims )
						max[ d ] = dims[ d ] - 1;
					else
					{
						min[ d ] = pos[ d - numPlaneDims ];
						max[ d ] = pos[ d - numPlaneDims ];
					}

				}

				Interval subsetInterval = new FinalInterval( min, max );

				Cursor< IntType > cursor = SubsetViews.iterableSubsetView( srcImg, subsetInterval, false ).cursor();

				while ( cursor.hasNext() )
				{
					cursor.fwd();
					cursor.get().get();
				}

				cursor.reset();

				while ( cursor.hasNext() )
				{
					cursor.next();
					// cursor.get().getRealDouble();
					// long[] pos2 = new long[ cursor.numDimensions() ];
					// cursor.localize( pos2 );
				}

			}

		long diff = System.nanoTime() - curr;

		if ( std )
			System.out.println( "Standard took " + diff / 1000 / 1000 );
		else
			System.out.println( "Optimized took " + diff / 1000 / 1000 );

	}

}
