package net.imglib2.ops.image.neighborhood;

import java.lang.reflect.Array;
import java.util.Arrays;

import net.imglib2.AbstractCursor;
import net.imglib2.RandomAccess;
import net.imglib2.type.Type;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Benjamin Schmid
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Christian Dietz <christian.dietz@uni-konstanz.de>
 */
public class BufferedRectangularNeighborhoodCursor< T extends Type< T >> extends AbstractCursor< T > implements NeighborhoodCursor< T >
{
	private final RandomAccess< T > source;

	private final long[] bufferElements;

	private final long[] center;

	private final long[] min;

	private final long[] max;

	private final long[] span;

	private final long[] bck;

	private final long[] currentPos;

	private final T[] buffer;

	private final int maxCount;

	private int bufferOffset;

	private int activeDim;

	private int bufferPtr;

	private int count;

	@SuppressWarnings( "unchecked" )
	public BufferedRectangularNeighborhoodCursor( final T type, final RandomAccess< T > source, final long[] center, final long[] span )
	{
		super( source.numDimensions() );
		this.source = source;
		this.center = center;

		currentPos = center.clone();

		max = new long[ n ];
		min = new long[ n ];
		bufferElements = new long[ n ];
		bck = new long[ n ];

		this.span = span;

		Arrays.fill( bufferElements, 1 );

		int tmp = 1;
		for ( int d = 0; d < span.length; d++ )
		{
			tmp *= ( span[ d ] * 2 ) + 1;
			bck[ d ] = ( -2 * span[ d ] );

			for ( int dd = 0; dd < n; dd++ )
				if ( dd != d )
					bufferElements[ d ] *= ( span[ d ] * 2 ) + 1;
		}

		maxCount = tmp;

		buffer = ( T[] ) Array.newInstance( type.getClass(), maxCount );

		for ( int t = 0; t < buffer.length; t++ )
		{
			buffer[ t ] = type.copy();
		}

	}

	protected BufferedRectangularNeighborhoodCursor( final BufferedRectangularNeighborhoodCursor< T > c )
	{
		super( c.numDimensions() );
		this.source = c.source.copyRandomAccess();
		this.center = c.center.clone();
		max = c.max.clone();
		min = c.min.clone();
		span = c.span;
		bck = c.bck;
		maxCount = c.maxCount;
		buffer = c.buffer.clone();
		bufferElements = c.bufferElements.clone();
		currentPos = c.currentPos.clone();
		bufferOffset = c.bufferOffset;
		activeDim = c.activeDim;
		bufferPtr = c.bufferPtr;
		count = c.count;
	}

	@Override
	public T get()
	{
		return buffer[ bufferPtr ];
	}

	@Override
	public void fwd()
	{

		if ( ++bufferPtr >= buffer.length )
			bufferPtr = 0;

		if ( activeDim < 0 || count >= buffer.length - bufferElements[ activeDim ] )
		{
			for ( int d = n - 1; d > -1; d-- )
			{
				if ( d == activeDim )
					continue;

				if ( source.getLongPosition( d ) < max[ d ] )
				{
					source.fwd( d );
					break;
				}
				else
					source.move( bck[ d ], d );

			}
			buffer[ bufferPtr ].set( source.get() );
		}

		count++;
	}

	@Override
	public void reset()
	{
		activeDim = -1;

		for ( int d = 0; d < center.length; d++ )
		{
			long tmp = currentPos[ d ];
			currentPos[ d ] = center[ d ];

			min[ d ] = center[ d ] - span[ d ];
			max[ d ] = center[ d ] + span[ d ];

			if ( center[ d ] - tmp != 0 )
			{
				if ( activeDim != -1 )
				{
					activeDim = -1;
					break;
				}
				activeDim = d;
			}
		}

		if ( activeDim >= 0 && bufferOffset + bufferElements[ activeDim ] < buffer.length )
			bufferOffset += bufferElements[ activeDim ];
		else
			bufferOffset = 0;

		bufferPtr = bufferOffset - 1;

		for ( int d = 0; d < n; d++ )
		{
			if ( d == activeDim )
				source.setPosition( max[ d ], d );
			else
				source.setPosition( min[ d ], d );
		}

		source.bck( source.numDimensions() - 1 );
		count = 0;
	}

	@Override
	public boolean hasNext()
	{
		return count < maxCount;
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return source.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return source.getLongPosition( d );
	}

	@Override
	public void localize( final long[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final int[] position )
	{
		source.localize( position );
	}

	@Override
	public BufferedRectangularNeighborhoodCursor< T > copy()
	{
		return new BufferedRectangularNeighborhoodCursor< T >( this );
	}

	@Override
	public BufferedRectangularNeighborhoodCursor< T > copyCursor()
	{
		return copy();
	}
}
