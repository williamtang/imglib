package net.imglib2.ops.iterable;

import java.util.Iterator;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Max< T extends RealType< T >, V extends RealType< V >> implements UnaryOperation< Iterator< T >, V >
{

	@Override
	public V compute( Iterator< T > input, V output )
	{
		T max = null;
		while ( input.hasNext() )
		{
			T in = input.next();

			if ( max == null || in.compareTo( max ) > 0 )
				max = in;
		}

		output.setReal( max.getRealDouble() );
		return output;
	}

	@Override
	public UnaryOperation< Iterator< T >, V > copy()
	{
		return new Max< T, V >();
	}

}
