package net.imglib2.ops.image.neighborhood;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

public class SlidingNeighborhoodOp< T extends Type< T >, V extends Type< V >, IN extends RandomAccessibleInterval< T >, OUT extends IterableInterval< V >> implements UnaryOperation< IN, OUT >
{

	private Neighborhood< T > neighborhood;

	private UnaryOperation< Iterator< T >, V > op;

	public SlidingNeighborhoodOp( Neighborhood< T > neighborhood, UnaryOperation< Iterator< T >, V > op )
	{
		this.op = op;
		this.neighborhood = neighborhood;
	}

	@Override
	public OUT compute( IN input, OUT output )
	{

		// Create an iterable to check iteration order
		if ( !Views.iterable( input ).iterationOrder().equals( output.iterationOrder() ) )
			throw new IllegalArgumentException( "Iteration order doesn't fit in SlidingNeighborhoodOp" );

		// Update neighborhood
		neighborhood.updateSource( Views.extendBorder( input ) );

		// Cursor
		Cursor< T > neighborhoodCursor = neighborhood.cursor();

		// Cursors are created.
		Cursor< V > resCursor = output.cursor();

		// Sliding
		while ( resCursor.hasNext() )
		{
			// fwd res
			resCursor.fwd();

			// NeighborhoodCursor is reseted
			neighborhood.setPosition( resCursor );

			// Neighborhood is iterable at the moment as center was updated
			op.compute( neighborhoodCursor, resCursor.get() );
		}

		return output;

	}

	@Override
	public UnaryOperation< IN, OUT > copy()
	{
		return new SlidingNeighborhoodOp< T, V, IN, OUT >( neighborhood.copy(), op.copy() );
	}

}
