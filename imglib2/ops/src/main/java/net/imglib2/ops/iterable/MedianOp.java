package net.imglib2.ops.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class MedianOp< T extends RealType< T >> implements UnaryOperation< Iterable< T >, T >
{

	private ArrayList< Double > m_statistics = new ArrayList< Double >();

	@Override
	public T compute( Iterable< T > input, T output )
	{

		m_statistics.clear();
		Iterator< T > inputCursor = input.iterator();
		while ( inputCursor.hasNext() )
		{
			m_statistics.add( inputCursor.next().getRealDouble() );
		}

		output.setReal( select( m_statistics, 0, m_statistics.size(), m_statistics.size() / 2 ) );
		return output;
	}

	@Override
	public MedianOp< T > copy()
	{
		return new MedianOp< T >();
	}

	/**
	 * Returns the value of the kth lowest element. Do note that for nth lowest
	 * element, k = n - 1.
	 */
	private static double select( List< Double > array, int left, int right, int k )
	{

		while ( true )
		{

			if ( right <= left + 1 )
			{

				if ( right == left + 1 && array.get( right ) < array.get( left ) )
				{
					swap( array, left, right );
				}

				return array.get( k );

			}
			else
			{

				int middle = ( left + right ) >>> 1;
				swap( array, middle, left + 1 );

				if ( array.get( left ) > array.get( right ) )
				{
					swap( array, left, right );
				}

				if ( array.get( left + 1 ) > array.get( right ) )
				{
					swap( array, left + 1, right );
				}

				if ( array.get( left ) > array.get( left + 1 ) )
				{
					swap( array, left, left + 1 );
				}

				int i = left + 1;
				int j = right;
				double pivot = array.get( left + 1 );

				while ( true )
				{
					do
						i++;
					while ( array.get( i ) < pivot );
					do
						j--;
					while ( array.get( j ) > pivot );

					if ( j < i )
					{
						break;
					}

					swap( array, i, j );
				}

				array.set( left + 1, array.get( j ) );
				array.set( j, pivot );

				if ( j >= k )
				{
					right = j - 1;
				}

				if ( j <= k )
				{
					left = i;
				}
			}
		}
	}

	/** Helper method for swapping array entries */
	private static void swap( List< Double > array, int a, int b )
	{
		double temp = array.get( a );
		array.set( a, array.get( b ) );
		array.set( b, temp );
	}
}
