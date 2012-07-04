package net.imglib2.ops.image.neighborhood;

import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class SlidingSpeedTests
{

	public static void main( String[] args )
	{

		long[] imgDims = new long[] { 20, 20 };

		ImgFactory< UnsignedByteType > fac = new ArrayImgFactory< UnsignedByteType >();

		Img< UnsignedByteType > img = fac.create( imgDims, new UnsignedByteType() );
		Img< UnsignedByteType > res = fac.create( imgDims, new UnsignedByteType() );

		long[] span = new long[ img.numDimensions() ];
		Arrays.fill( span, 1 );

		BufferedRectangularNeighborhood< UnsignedByteType > neighborhood = new BufferedRectangularNeighborhood< UnsignedByteType >( img.firstElement().createVariable(), new Point( new long[ img.numDimensions() ] ), span );

		UnaryOperation< Iterator< UnsignedByteType >, UnsignedByteType > op = new UnaryOperation< Iterator< UnsignedByteType >, UnsignedByteType >()
		{

			@Override
			public UnsignedByteType compute( Iterator< UnsignedByteType > input, UnsignedByteType output )
			{

				while ( input.hasNext() )
				{
					input.next();
				}
				return output;
			}

			@Override
			public UnaryOperation< Iterator< UnsignedByteType >, UnsignedByteType > copy()
			{
				return null;
			}
		};

		SlidingNeighborhoodOp< UnsignedByteType, UnsignedByteType, Img< UnsignedByteType >, Img< UnsignedByteType >> slidingOp = new SlidingNeighborhoodOp< UnsignedByteType, UnsignedByteType, Img< UnsignedByteType >, Img< UnsignedByteType >>( new OutOfBoundsBorderFactory< UnsignedByteType, Img< UnsignedByteType > >(), neighborhood, op );

		long curr = System.nanoTime();
		slidingOp.compute( img, res );
		System.out.println( "Speed " + ( System.nanoTime() - curr ) / 1000 / 1000 );
	}

}
