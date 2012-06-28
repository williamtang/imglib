package net.imglib2.ops.iterable;

import java.util.Iterator;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author siedentopl, dietzc
 * 
 * @param <T>
 * @param <V>
 */
public class Variance< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterable< T >, V >
{

	@Override
	public V compute( Iterable< T > input, V output )
	{
		double sum = 0;
		double sumSqr = 0;
		int n = 0;

		Iterator< T > iter = input.iterator();

		while ( iter.hasNext() )
		{
			double px = iter.next().getRealDouble();
			n++;
			sum += px;
			sumSqr += px * px;
		}

		output.setReal( ( sumSqr - ( sum * sum / n ) ) / ( n - 1 ) );
		return output;
	}

	@Override
	public UnaryOperation< Iterable< T >, V > copy()
	{
		return new Variance< T, V >();
	}

}
