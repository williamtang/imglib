package net.imglib2.ops.iterable;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Min< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterable< T >, V >
{

	@Override
	public V compute( Iterable< T > input, V output )
	{
		T min = null;
		for ( T in : input )
		{
			if ( min == null || in.compareTo( min ) < 0 )
				min = in;
		}

		output.setReal( min.getRealDouble() );

		return output;
	}

	@Override
	public UnaryOperation< Iterable< T >, V > copy()
	{
		return new Min< T, V >();
	}

}
