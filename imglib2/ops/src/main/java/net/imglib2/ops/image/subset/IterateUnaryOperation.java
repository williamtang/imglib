package net.imglib2.ops.image.subset;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.subset.ImgPlusView;
import net.imglib2.img.subset.ImgView;
import net.imglib2.img.subset.LabelingView;
import net.imglib2.img.subset.SubsetViews;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.Type;

/**
 * Applies a given Operation to each interval separately.
 * 
 * @author dietzc, hornm University of Konstanz
 */
public final class IterateUnaryOperation< T extends Type< T >, V extends Type< V >, S extends RandomAccessibleInterval< T >, U extends RandomAccessibleInterval< V >> implements UnaryOperation< S, U >
{

	private ExecutorService m_service;

	private final UnaryOperation< S, U > m_op;

	private final Interval[] m_outIntervals;

	private final Interval[] m_inIntervals;

	private int m_numThreads;

	public IterateUnaryOperation( UnaryOperation< S, U > op, Interval[] inIntervals, Interval[] outIntervals, int numThreads )
	{

		if ( inIntervals.length != outIntervals.length ) { throw new IllegalArgumentException( "In and out intervals do not match! Most likely an implementation error!" ); }

		m_op = op;
		m_inIntervals = inIntervals;
		m_outIntervals = outIntervals;
		m_numThreads = numThreads;
		m_service = createExecutionService();
	}

	private ExecutorService createExecutionService()
	{
		return m_numThreads <= 1 ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool( m_numThreads );
	}

	public IterateUnaryOperation( UnaryOperation< S, U > op, Interval[] inIntervals, Interval[] outIntervals )
	{
		this( op, inIntervals, outIntervals, 1 );

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final U compute( final S in, final U out )
	{

		Future< ? >[] futures = new Future< ? >[ m_inIntervals.length ];
		if ( m_service.isShutdown() )
		{
			m_service = createExecutionService();
		}

		for ( int i = 0; i < m_outIntervals.length; i++ )
		{
			OperationTask t = new OperationTask( m_op, createSubType( m_inIntervals[ i ], in ), createSubType( m_outIntervals[ i ], out ) );
			futures[ i ] = m_service.submit( t );
		}
		try
		{
			for ( Future< ? > f : futures )
			{

				f.get();

			}
		}
		catch ( InterruptedException e )
		{
			m_service.shutdownNow();
			throw new RuntimeException( e.getMessage() );
		}
		catch ( ExecutionException e )
		{
			m_service.shutdownNow();
			throw new RuntimeException( e.getMessage() );
		}

		return out;
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	private synchronized < TT extends Type< TT >, II extends RandomAccessibleInterval< TT > > II createSubType( final Interval i, final II in )
	{
		if ( in instanceof Labeling ) { return ( II ) new LabelingView( SubsetViews.subsetView( ( NativeImgLabeling ) in, i, false ), ( ( NativeImgLabeling ) in ).factory() ); }

		if ( in instanceof ImgPlus ) { return ( II ) new ImgPlusView( SubsetViews.subsetView( ( ImgPlus ) in, i, false ), ( ( ImgPlus ) in ).factory(), ( ImgPlus ) in ); }

		if ( in instanceof Img ) { return ( II ) new ImgView( SubsetViews.subsetView( ( Img ) in, i, false ), ( ( Img ) in ).factory() ); }

		return ( II ) SubsetViews.iterableSubsetView( in, i, false );
	}

	@Override
	public UnaryOperation< S, U > copy()
	{
		return new IterateUnaryOperation< T, V, S, U >( m_op.copy(), m_inIntervals, m_outIntervals, m_numThreads );
	}

	/**
	 * Future task
	 * 
	 * @author muethingc, dietzc
	 * 
	 */
	private class OperationTask implements Runnable
	{

		private final UnaryOperation< S, U > m_op;

		private final S m_in;

		private final U m_out;

		public OperationTask( final UnaryOperation< S, U > op, final S in, final U out )
		{
			m_in = in;
			m_out = out;
			m_op = op.copy();
		}

		@Override
		public void run()
		{
			m_op.compute( m_in, m_out );
		}

	}
}
