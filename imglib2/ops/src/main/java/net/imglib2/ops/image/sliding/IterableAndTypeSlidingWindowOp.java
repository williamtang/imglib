package net.imglib2.ops.image.sliding;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.UnaryOperation;
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
	private SlidingWindowIteratorProvider< T > m_provider;

	//
	public IterableAndTypeSlidingWindowOp( SlidingWindowIteratorProvider< T > provider, BinaryOperation< Iterable< T >, T, V > op )
	{
		m_provider = provider;
		m_op = op;
	}

	@Override
	public OUT compute( IN1 input1, OUT output )
	{
		SlidingWindowIterator< T > iterator = m_provider.createSlidingWindowIterator( input1 );
		Cursor< V > resCursor = output.cursor();
		Cursor< T > srcCursor = Views.iterable( input1 ).cursor();

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
		// TODO Auto-generated method stub
		return null;
	}

}
