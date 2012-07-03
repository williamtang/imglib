package net.imglib2.ops.image.neighborhood.deprecated;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * Operation performing sliding window
 * 
 * 
 * @author eethyo
 * 
 * @param <T>
 * @param <V>
 * @param <IN>
 * @param <OUT>
 */
public class IterableSlidingWindowOp< T extends Type< T >, V extends Type< V >, IN extends RandomAccessibleInterval< T >, OUT extends IterableInterval< V >> implements UnaryOperation< IN, OUT >
{

	// the operation which is applied one each window
	private UnaryOperation< Iterable< T >, V > m_op;

	// Iterator over the input
	private SlidingWindowIteratorProvider< T, IN > m_provider;

	// OutOfBounds
	private OutOfBoundsFactory< T, IN > m_fac;

	// ...
	public IterableSlidingWindowOp( final OutOfBoundsFactory< T, IN > fac, SlidingWindowIteratorProvider< T, IN > provider, UnaryOperation< Iterable< T >, V > op )
	{
		m_provider = provider;
		m_op = op;
		m_fac = fac;
	}

	@Override
	public OUT compute( IN input, OUT output )
	{

		IterableInterval< T > iterable = Views.iterable( input );

		if ( !iterable.iterationOrder().equals( output.iterationOrder() ) )
			throw new IllegalArgumentException( "Iteration order doesn't fit in UnarySlidingWindowOp" );

		SlidingWindowIterator< T > iterator = m_provider.createSlidingWindowIterator( m_fac, input );
		Cursor< V > resCursor = output.cursor();

		while ( iterator.hasNext() )
		{
			resCursor.fwd();
			m_op.compute( iterator.next(), resCursor.get() );
		}

		return output;
	}

	@Override
	public UnaryOperation< IN, OUT > copy()
	{
		return new IterableSlidingWindowOp< T, V, IN, OUT >( m_fac, m_provider, m_op.copy() );
	}

}
