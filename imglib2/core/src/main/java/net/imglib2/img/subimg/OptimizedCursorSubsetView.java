package net.imglib2.img.subimg;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

public class OptimizedCursorSubsetView< T extends NativeType< T >> extends IterableRandomAccessibleInterval< T >
{

	private boolean m_isOptimizable;

	private IterableInterval< T > m_srcII;

	private IterableInterval< T > m_localII;

	private int m_idx;

	private int m_cubeSize;

	public OptimizedCursorSubsetView( RandomAccessibleInterval< T > src, Interval interval, boolean keepSizeOneDims )
	{
		super( KNIPViews.subsetView( src, interval, keepSizeOneDims ) );
		boolean isIterable = ( src instanceof IterableInterval );

		m_srcII = Views.iterable( src );;
		m_localII = Views.iterable( super.interval );
		m_isOptimizable = false;
		m_idx = 1;

		if ( isIterable )
		{
			if ( !intervalEquals( src, interval ) )
			{
				m_isOptimizable = true;
				for ( int d = 0; d < interval.numDimensions(); d++ )
				{
					if ( interval.dimension( d ) > 1 )
					{
						m_cubeSize++;

						if ( m_cubeSize != d + 1 )
						{
							m_isOptimizable = false;
							break;
						}
					}

				}
			}

			if ( m_isOptimizable )
			{
				long[] iterDims = new long[ src.numDimensions() - m_cubeSize ];
				long[] cubePos = new long[ src.numDimensions() - m_cubeSize ];
				for ( int d = m_cubeSize; d < src.numDimensions(); d++ )
				{
					iterDims[ d - m_cubeSize ] = src.dimension( d );
					cubePos[ d - m_cubeSize ] = interval.min( d );
				}
				m_idx = ( int ) IntervalIndexer.positionToIndex( cubePos, iterDims );
			}
			else
			{
				m_idx = 0;
				m_cubeSize = super.numDimensions();
			}
		}

	}

	@Override
	public Cursor< T > cursor()
	{
		if ( m_isOptimizable )
			return new OptimizedCursor< T >( m_srcII.cursor(), ( int ) super.size(), m_idx, m_cubeSize );
		else
			return m_localII.cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		if ( m_isOptimizable )
			return new OptimizedCursor< T >( m_srcII.localizingCursor(), ( int ) super.size(), m_idx, m_cubeSize );
		else
			return m_localII.localizingCursor();
	}

	private boolean intervalEquals( Interval a, Interval b )
	{

		if ( a.numDimensions() != b.numDimensions() ) { return false; }

		for ( int d = 0; d < a.numDimensions(); d++ )
		{
			if ( a.min( d ) != b.min( d ) || a.max( d ) != b.max( d ) )
				return false;
		}

		return true;
	}

	public static void main( String[] args )
	{

		callTest( true, 50 );
		callTest( false, 50 );

	}

	private static void callTest( boolean std, int numRuns )
	{
		int numPlaneDims = 2;

		long[] dims = new long[] { 1000, 1000, 50 };
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

				Cursor< IntType > cursor = null;

				if ( std )
				{
					cursor = Views.iterable( KNIPViews.subsetView( srcImg, subsetInterval, false ) ).cursor();
				}
				else
				{
					cursor = KNIPViews.optimizedSubsetView( srcImg, subsetInterval, false ).cursor();
				}

				while ( cursor.hasNext() )
				{
					cursor.fwd();
					cursor.get().get();
				}

				cursor.reset();

				while ( cursor.hasNext() )
				{
					cursor.fwd();
					cursor.get().getRealDouble();
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
