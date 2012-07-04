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

package net.imglib2.ops.image.neighborhood;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableRealInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.type.Type;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class HyperSphereNeighborhood< T extends Type< T > > extends AbstractNeighborhood< T >
{

	long radius;

	public HyperSphereNeighborhood( final Localizable center, final long radius )
	{
		super( center );
		this.radius = radius;
	}

	/**
	 * Compute the number of elements for iteration
	 */
	protected long computeSize()
	{
		final HyperSphereNeighborhoodCursor< T > cursor = new HyperSphereNeighborhoodCursor< T >( source, this.center, radius );

		// "compute number of pixels"
		long size = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			++size;
		}

		return size;
	}

	@Override
	public long size()
	{
		return computeSize();
	}

	@Override
	public T firstElement()
	{
		HyperSphereNeighborhoodCursor< T > cursor = new HyperSphereNeighborhoodCursor< T >( source, center, radius );
		cursor.fwd();
		return cursor.get();
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public double realMin( final int d )
	{
		return min( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min[ d ] = center[ d ] - radius;
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min.setPosition( center[ d ] - radius, d );
	}

	@Override
	public double realMax( final int d )
	{
		return max( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max[ d ] = center[ d ] + radius;
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max.setPosition( center[ d ] - radius, d );
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return center[ d ] - radius;
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min[ d ] = center[ d ] - radius;
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < numDimensions; ++d )
			min.setPosition( center[ d ] - radius, d );
	}

	@Override
	public long max( final int d )
	{
		return center[ d ] + radius;
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max[ d ] = center[ d ] + radius;
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < numDimensions; ++d )
			max.setPosition( center[ d ] + radius, d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		final long size = radius * 2 + 1;
		for ( int d = 0; d < numDimensions; ++d )
			dimensions[ d ] = size;
	}

	@Override
	public long dimension( final int d )
	{
		return radius * 2 + 1;
	}

	@Override
	public Cursor< T > cursor()
	{
		return new HyperSphereNeighborhoodCursor< T >( source, center, radius );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}

	@Override
	public Neighborhood< T > copy()
	{
		return new HyperSphereNeighborhood< T >( new Point( center.clone() ), radius );
	}

}
