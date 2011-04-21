/**
 * Copyright (c) 2011, Stephan Saalfeld
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

package net.imglib2.nearestneighbor;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * <em>k</em>-nearest-neighbor search on {@link IterableRealInterval}
 * implemented as linear search.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class NearestNeighborSearchOnIterableRealInterval< T > implements NearestNeighborSearch< T >
{
	final protected IterableRealInterval< T > iterable;
	
	final protected int n;
	protected RealCursor< T > element = null;
	protected double squareDistance = Double.MAX_VALUE;
	
	final protected double[] referenceLocation;
	
	/**
	 * Calculate the square Euclidean distance of a query location to the
	 * location stored in referenceLocation.
	 * 
	 * @param query
	 * @return
	 */
	final protected double squareDistance( final RealLocalizable query )
	{
		double squareSum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double distance = query.getDoublePosition( d ) - referenceLocation[ d ];
			squareSum += distance * distance;
		}
		return squareSum;
	}
	
	public NearestNeighborSearchOnIterableRealInterval( final IterableRealInterval< T > iterable )
	{
		this.iterable = iterable;
		n = iterable.numDimensions();
		
		referenceLocation = new double[ n ];
	}
	
	@Override
	public void search( final RealLocalizable reference )
	{
		squareDistance = Double.MAX_VALUE;
		
		reference.localize( referenceLocation );
		
		final RealCursor< T > cursor = iterable.localizingCursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			final double cursorSquareDistance = squareDistance( cursor );
			if ( squareDistance > cursorSquareDistance )
			{
				squareDistance = cursorSquareDistance;
				element = cursor.copyCursor();
			}
		}
	}
	
	
	/* NearestNeighborSearch */
	
	@Override
	public RealLocalizable getPosition()
	{
		return element;
	}

	@Override
	public Sampler< T > getSampler()
	{
		return element;
	}

	@Override
	public double getSquareDistance()
	{
		return squareDistance;
	}
	
	@Override
	public double getDistance()
	{
		return Math.sqrt( squareDistance );
	}
}