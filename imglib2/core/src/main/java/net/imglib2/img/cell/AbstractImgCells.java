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

package net.imglib2.img.cell;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;

/**
 * Implementation of {@link Cells} that uses {@link DefaultCell}s and keeps them
 * all in memory all the time.
 * 
 * 
 * @author ImgLib2 developers
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractImgCells< A, C extends AbstractCell< A > > implements Cells< A, C >
{
	private final int entitiesPerPixel;

	private final int n;

	private final long[] dimensions;

	protected final int[] cellDimensions;

	protected Img< C > cells = null;

	private final long[] numCells;

	private int[] borderSize;

	public AbstractImgCells( final int entitiesPerPixel, final long[] dimensions, final int[] cellDimensions )
	{
		this.entitiesPerPixel = entitiesPerPixel;
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.cellDimensions = cellDimensions.clone();

		numCells = new long[ n ];
		borderSize = new int[ n ];

		for ( int d = 0; d < n; ++d )
		{
			numCells[ d ] = ( dimensions[ d ] - 1 ) / cellDimensions[ d ] + 1;
			borderSize[ d ] = ( int ) ( dimensions[ d ] - ( numCells[ d ] - 1 ) * cellDimensions[ d ] );
		}

	}

	/**
	 * @param numCells
	 *            the dimensions of the cells image
	 * @return the image holding the cells, where all cells are already set
	 *         accordingly
	 */
	protected abstract Img< C > getCellsImg( long[] numCells );

	protected void getCellDimensions( long[] currentCellOffset, int[] currentCellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentCellDims[ d ] = ( ( currentCellOffset[ d ] + 1 == numCells[ d ] ) ? borderSize[ d ] : cellDimensions[ d ] );
			currentCellOffset[ d ] *= cellDimensions[ d ];
		}
	}

	private void initCellsImg()
	{
		if ( cells == null )
		{
			cells = getCellsImg( numCells );
		}
	}

	@Override
	public RandomAccess< C > randomAccess()
	{
		initCellsImg();
		return cells.randomAccess();
	}

	@Override
	public Cursor< C > cursor()
	{
		initCellsImg();
		return cells.cursor();
	}

	@Override
	public Cursor< C > localizingCursor()
	{
		initCellsImg();
		return cells.localizingCursor();
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public void dimensions( final long[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = dimensions[ i ];
	}

	@Override
	public long dimension( final int d )
	{
		try
		{
			return this.dimensions[ d ];
		}
		catch ( final ArrayIndexOutOfBoundsException e )
		{
			return 1;
		}
	}

	@Override
	public void cellDimensions( final int[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = cellDimensions[ i ];
	}

	@Override
	public int cellDimension( final int d )
	{
		try
		{
			return this.cellDimensions[ d ];
		}
		catch ( final ArrayIndexOutOfBoundsException e )
		{
			return 1;
		}
	}

	@Override
	public int getEntitiesPerPixel()
	{
		return entitiesPerPixel;
	}
}
