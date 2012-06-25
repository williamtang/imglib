package net.imglib2.ops.iterable.localthresholder;

import java.util.Iterator;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class Niblack< T extends RealType< T >, IN extends Iterable< T >> implements BinaryOperation< IN, T, BitType >
{

	private double m_c;

	private double m_k;

	public Niblack( double k, double c )
	{
		m_c = c;
		m_k = k;
	}

	@Override
	public BitType compute( IN input, T px, BitType output )
	{
		int numElements = 0;
		double mean = 0;
		double variance = 0;

		Iterator< T > iterator = input.iterator();
		while ( iterator.hasNext() )
		{
			mean += iterator.next().getRealDouble();
			numElements++;
		}

		mean /= numElements;

		iterator = input.iterator();
		while ( iterator.hasNext() )
		{
			double diff = ( mean - iterator.next().getRealDouble() );
			variance += diff * diff;
		}

		variance /= ( numElements - 1 );
		output.set( px.getRealDouble() > mean + m_k * Math.sqrt( variance ) - m_c );

		return output;
	}

	@Override
	public BinaryOperation< IN, T, BitType > copy()
	{
		return new Niblack< T, IN >( m_k, m_c );
	}

}
