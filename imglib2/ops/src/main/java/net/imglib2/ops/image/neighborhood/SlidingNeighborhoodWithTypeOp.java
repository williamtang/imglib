package net.imglib2.ops.image.neighborhood;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

public class SlidingNeighborhoodWithTypeOp< T extends Type< T >, V extends Type< V >, IN extends RandomAccessibleInterval< T >, OUT extends IterableInterval< V >> implements UnaryOperation< IN, OUT >
{

	private Neighborhood< T > neighborhood;

	private BinaryOperation< Iterable< T >, T, V > op;

	public SlidingNeighborhoodWithTypeOp( Neighborhood< T > neighborhood, BinaryOperation< Iterable< T >, T, V > op )
	{
		this.op = op;
		this.neighborhood = neighborhood;
	}

	@Override
	public OUT compute( IN input, OUT output )
	{

		IterableInterval< T > iterable = Views.iterable( input );

		if ( !iterable.iterationOrder().equals( output.iterationOrder() ) )
			throw new IllegalArgumentException( "Iteration order doesn't fit in SlidingNeighborhoodOp" );

		// Update neighborhood
		neighborhood.updateSource( Views.extendBorder( input ) );

		// Cursors are created.
		Cursor< V > resCursor = output.cursor();
		Cursor< T > inCursor = iterable.cursor();
		// Sliding
		while ( resCursor.hasNext() )
		{
			// fwd res
			resCursor.fwd();
			inCursor.fwd();

			// NeighborhoodCursor is reseted
			neighborhood.setPosition( resCursor );

			// Neighborhood is iterable at the moment as center was updated
			op.compute( neighborhood, inCursor.get(), resCursor.get() );
		}

		return output;

	}

	@Override
	public UnaryOperation< IN, OUT > copy()
	{
		return new SlidingNeighborhoodWithTypeOp< T, V, IN, OUT >( neighborhood.copy(), op.copy() );
	}
}
