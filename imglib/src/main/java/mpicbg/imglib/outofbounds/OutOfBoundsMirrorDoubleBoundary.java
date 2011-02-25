/**
 * Copyright (c) 2010--2011, Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccessible;

/**
 * Coordinates out of image bounds are mirrored between boundary coordinates.
 * So boundary pixels are repeated.
 * 
 * <pre>
 * Example:
 * 
 * width=4
 * 
 *                                  |<-inside->|
 * x:    -9 -8 -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8  9
 * f(x):  0  0  1  2  3  3  2  1  0  0  1  2  3  3  2  1  0  0  1
 * </pre>
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class OutOfBoundsMirrorDoubleBoundary< T > extends AbstractOutOfBoundsMirror< T >
{
	public < F extends Interval & RandomAccessible< T > > OutOfBoundsMirrorDoubleBoundary( final F f )
	{
		super( f );
		
		for ( int i = 0; i < dimension.length; ++i )
			p[ i ] = 2 * dimension[ i ];
	}
	
	
	/* Positionable */
	
	@Override
	final public void fwd( final int d ) 
	{
		final long x = ++position[ d ];
		if ( x == 0 )
		{
			dimIsOutOfBounds[ d ] = false;
			if ( isOutOfBounds ) checkOutOfBounds();
		}
		else if ( x == dimension[ d ] )
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
		
		final long y = outOfBoundsRandomAccess.getLongPosition( d );
		if ( inc[ d ] )
		{
			if ( y + 1 == dimension[ d ] )
				inc[ d ] = false;
			else
				outOfBoundsRandomAccess.fwd( d );
		}
		else
		{
			if ( y == 0 )
				inc[ d ] = true;
			else
				outOfBoundsRandomAccess.bck( d );
		}
	}
	
	@Override
	final public void bck( final int d )
	{
		final long x = position[ d ]--;
		if ( x == 0 )
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
		else if ( x == dimension[ d ] )
		{
			dimIsOutOfBounds[ d ] = false;
			if ( isOutOfBounds ) checkOutOfBounds();
		}
		
		final long y = outOfBoundsRandomAccess.getLongPosition( d );
		if ( inc[ d ] )
		{
			if ( y == 0 )
				inc[ d ] = false;
			else
				outOfBoundsRandomAccess.bck( d );
		}
		else
		{
			if ( y + 1 == dimension[ d ] )
				inc[ d ] = true;
			else
				outOfBoundsRandomAccess.fwd( d );
		}
	}
	
	@Override
	final public void setPosition( long position, final int d )
	{
		position -= min[ d ];
		this.position[ d ] = position;
		final long x = this.p[ d ];
		final long mod = dimension[ d ];
		final boolean pos;
		if ( position < 0 )
		{
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
			position = -position - 1;
			pos = false;
		}
		else
			pos = true;	
		
		if ( position >= mod )
		{
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
			if ( position < x )
			{
				position = x - position - 1;
				inc[ d ] = !pos;
			}
			else
			{
				/* catches mod == 1 to no additional cost */
				try
				{
					position %= x;
					if ( position >= mod )
					{
						position = x - position - 1;
						inc[ d ] = !pos;
					}
					else
						inc[ d ] = pos;
				}
				catch ( ArithmeticException e ){ position = 0; }
			}
		}
		else
		{
			if ( pos )
			{
				dimIsOutOfBounds[ d ] = false;
				if ( isOutOfBounds )
					checkOutOfBounds();
			}
			inc[ d ] = pos;
		}
		
		outOfBoundsRandomAccess.setPosition( position, d );
	}
}