package net.imglib2.ops.iterable.localthresholder;

import java.util.Iterator;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class Sauvola< T extends RealType< T >, IN extends Iterable< T >> implements BinaryOperation< IN, T, BitType >
{

	private double m_r;

	private double m_k;

	public Sauvola( double k, double r )
	{
		m_r = r;
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
		output.set( px.getRealDouble() > mean * ( 1 + m_k * ( Math.sqrt( variance ) / m_r - 1 ) ) );

		return output;
	}

	@Override
	public BinaryOperation< IN, T, BitType > copy()
	{
		return new Sauvola< T, IN >( m_k, m_r );
	}

}
