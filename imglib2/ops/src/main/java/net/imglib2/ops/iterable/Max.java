package net.imglib2.ops.iterable;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Max< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterable< T >, V >
{

	@Override
	public V compute( Iterable< T > input, V output )
	{
		T max = null;
		for ( T in : input )
		{
			if ( max == null || in.compareTo( max ) > 0 )
				max = in;
		}

		output.setReal( max.getRealDouble() );
		return output;
	}

	@Override
	public UnaryOperation< Iterable< T >, V > copy()
	{
		return new Max< T, V >();
	}

}
