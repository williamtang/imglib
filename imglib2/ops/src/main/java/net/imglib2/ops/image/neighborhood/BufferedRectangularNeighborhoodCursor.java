package net.imglib2.ops.image.neighborhood;

/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

import java.lang.reflect.Array;

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

	private int bufferOffset;

	private final long[] bufferElements;

	private final long[] center;

	private final long[] min;

	private final long[] max;

	private final long[] span;

	private final long[] bck;

	private final T[] buffer;

	private int maxCount;

	private int m_activeDim;

	private int bufferPtr;

	private int count;

	@SuppressWarnings( "unchecked" )
	public BufferedRectangularNeighborhoodCursor( final T type, final RandomAccess< T > source, final long[] span )
	{
		super( source.numDimensions() );
		this.source = source;

		center = new long[ n ];
		max = new long[ n ];
		min = new long[ n ];
		bufferElements = new long[ n ];
		bck = new long[ n ];

		this.span = span;

		maxCount = 1;

		for ( int d = 0; d < span.length; d++ )
		{
			maxCount *= span[ d ];
			bck[ d ] = ( -2 * span[ d ] ) - 1;
		}

		for ( int d = 0; d < n; d++ )
			for ( int dd = 0; dd < n; dd++ )
				if ( dd != d )
					bufferElements[ d ] *= span[ d ];

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
		this.center = c.center;
		max = c.max.clone();
		min = c.min.clone();
		span = c.span;
		bck = c.bck;
		maxCount = c.maxCount;
		buffer = c.buffer.clone();
		bufferElements = c.bufferElements.clone();
	}

	@Override
	public T get()
	{
		return buffer[ bufferPtr++ ];
	}

	@Override
	public void fwd()
	{
		if ( m_activeDim < 0 || count >= buffer.length - bufferElements[ m_activeDim ] )
		{
			for ( int d = n - 1; d > -1; d-- )
			{
				if ( d == m_activeDim )
					continue;

				if ( source.getLongPosition( d ) < max[ d ] )
				{
					source.fwd( d );
					break;
				}
				else
					source.move( bck[ d ], d );

				buffer[ bufferPtr ].set( source.get() );

			}
		}

		count++;
	}

	@Override
	public void reset()
	{
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = center[ d ] - span[ d ];
			max[ d ] = center[ d ] + span[ d ];
		}
		source.setPosition( min );
		source.bck( 0 );
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

	@Override
	public void updateCenter( long[] newPos )
	{

		// Current pos is always the upper left corner!
		for ( int d = 0; d < center.length; d++ )
		{
			long tmp = center[ d ];
			center[ d ] = newPos[ d ];

			if ( center[ d ] - tmp != 0 )
			{
				if ( m_activeDim != -1 )
				{
					m_activeDim = -1;
					break;
				}
				m_activeDim = d;
			}
		}

		if ( m_activeDim >= 0 && bufferOffset + bufferElements[ m_activeDim ] < buffer.length )
			bufferOffset += bufferElements[ m_activeDim ];
		else
			bufferOffset = 0;

		bufferPtr = bufferOffset;

		reset();
	}
}
