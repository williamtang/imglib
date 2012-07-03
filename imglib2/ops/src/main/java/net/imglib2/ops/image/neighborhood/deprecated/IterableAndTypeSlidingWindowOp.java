package net.imglib2.ops.image.neighborhood.deprecated;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.BinaryOperation;
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
public class IterableAndTypeSlidingWindowOp< T extends Type< T >, V extends Type< V >, IN1 extends RandomAccessibleInterval< T >, OUT extends IterableInterval< V >> implements UnaryOperation< IN1, OUT >
{

	// the operation which is applied one each window
	private BinaryOperation< Iterable< T >, T, V > m_op;

	// Iterator over the input
	private SlidingWindowIteratorProvider< T, IN1 > m_provider;

	// Factory for out of bounds
	private OutOfBoundsFactory< T, IN1 > m_fac;

	public IterableAndTypeSlidingWindowOp( final OutOfBoundsFactory< T, IN1 > fac, SlidingWindowIteratorProvider< T, IN1 > provider, BinaryOperation< Iterable< T >, T, V > op )
	{
		m_provider = provider;
		m_op = op;
		m_fac = fac;
	}

	@Override
	public OUT compute( IN1 input1, OUT output )
	{

		SlidingWindowIterator< T > iterator = m_provider.createSlidingWindowIterator( m_fac, input1 );

		IterableInterval< T > iterable = Views.iterable( input1 );

		if ( !iterable.iterationOrder().equals( output.iterationOrder() ) )
			throw new IllegalArgumentException( "Iteration order doesn't fit in IterableAndTypeSlidingWindowOp" );

		Cursor< V > resCursor = output.cursor();
		Cursor< T > srcCursor = iterable.cursor();

		while ( iterator.hasNext() )
		{
			srcCursor.fwd();
			resCursor.fwd();

			m_op.compute( iterator.next(), srcCursor.get(), resCursor.get() );
		}

		return output;
	}

	@Override
	public UnaryOperation< IN1, OUT > copy()
	{
		return new IterableAndTypeSlidingWindowOp< T, V, IN1, OUT >( m_fac, m_provider, m_op.copy() );
	}

}
