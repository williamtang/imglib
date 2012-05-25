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

package net.imglib2.img.subimg;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.view.IterableRandomAccessibleInterval;

/**
 * Helper class to create a subview on an {@link Img} which behaves exactly as
 * an {@link Img}.
 * 
 * @author Tobias Pietzsch, Christian Dietz
 */
public class ImgView< T extends Type< T > > extends IterableRandomAccessibleInterval< T > implements Img< T >
{

	// src img
	private final RandomAccessibleInterval< T > m_src;

	// origin of source img
	private final long[] m_origin;

	// factory
	private final ImgFactory< T > m_fac;

	/**
	 * SubImg is created. View on {@link Img} which is defined by a given
	 * Interval, but still is an {@link Img}
	 * 
	 * @param RandomAccessibleInterval
	 *            Source interval for the view
	 * @param ImgFactory
	 *            <T> Factory to create img
	 * @param interval
	 *            Interval which will be used for to create this view
	 * @param keepDimsWithSizeOne
	 *            If false, dimensions with size one will be virtually removed
	 *            from the resulting view
	 */
	public ImgView( final RandomAccessibleInterval< T > src, ImgFactory< T > fac )
	{
		super( src );
		m_src = src;
		m_fac = fac;
		m_origin = new long[ interval.numDimensions() ];
		interval.min( m_origin );
	}

	/**
	 * Origin in the source img
	 * 
	 * @param origin
	 */
	public final void getOrigin( final long[] origin )
	{
		for ( int d = 0; d < origin.length; d++ )
			origin[ d ] = m_origin[ d ];
	}

	/**
	 * Origin of dimension d in the source img
	 */
	public final long getOrigin( final int d )
	{
		return m_origin[ d ];
	}

	/**
	 * @return Source image
	 */
	public RandomAccessibleInterval< T > getSrc()
	{
		return m_src;
	}

	@Override
	public ImgFactory< T > factory()
	{
		return m_fac;
	}

	@Override
	public Img< T > copy()
	{
		final Img< T > copy = m_fac.create( this, m_src.randomAccess().get().createVariable() );

		Cursor< T > srcCursor = localizingCursor();
		RandomAccess< T > resAccess = copy.randomAccess();

		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			resAccess.setPosition( srcCursor );
			resAccess.get().set( srcCursor.get() );

		}

		return copy;
	}

}
