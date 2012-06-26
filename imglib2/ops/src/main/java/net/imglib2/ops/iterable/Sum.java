package net.imglib2.ops.iterable;

import java.util.Iterator;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Sum< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterable< T >, V >
{

	@Override
	public V compute( Iterable< T > op, V r )
	{
		final Iterator< T > it = op.iterator();
		double sum = 0;
		while ( it.hasNext() )
		{
			sum += it.next().getRealDouble();
		}
		r.setReal( sum );

		return r;
	}

	@Override
	public UnaryOperation< Iterable< T >, V > copy()
	{
		return new Sum< T, V >();
	}

}
