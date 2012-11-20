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

package net.imglib2.img.file;

import net.imglib2.img.Img;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.cell.AbstractImgCells;
import net.imglib2.img.cell.Cells;
import net.imglib2.img.cell.DefaultCell;
import net.imglib2.img.file.FileImgFactory.FileNameGenerator;
import net.imglib2.img.list.ListLocalizingCursor;

/**
 * Implementation of {@link Cells} that uses {@link DefaultCell}s and keeps them
 * all in memory all the time.
 *
 *
 * @author ImgLib2 developers
 * @author Tobias Pietzsch
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FileImgCells< A extends ArrayDataAccess< A > & ExternalizableType< A >> extends AbstractImgCells< A, FileCell< A > >
{

	private final int entitiesPerPixel;

	private final A creator;

	private final FileNameGenerator fileNameGenerator;

	public FileImgCells( final A creator, final int entitiesPerPixel, final long[] dimensions, final int[] cellDimensions, final FileNameGenerator fileNameGenerator )
	{
		super( entitiesPerPixel, dimensions, cellDimensions );
		this.entitiesPerPixel = entitiesPerPixel;
		this.creator = creator;
		this.fileNameGenerator = fileNameGenerator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Img< FileCell< A >> getCellsImg( final long[] numCells )
	{

		final FileImg< FileCell< A >> cells = new FileImgFactory< FileCell< A > >( fileNameGenerator ).create( numCells, new FileCell< A >( creator, cellDimensions, new long[ cellDimensions.length ], entitiesPerPixel ) );
		final ListLocalizingCursor< FileCell< A > > cellCursor = cells.localizingCursor();
		final long[] currentCellOffset = new long[ numCells.length ];
		final int[] currentCellDims = new int[ numCells.length ];
		while ( cellCursor.hasNext() )
		{
			cellCursor.fwd();
			cellCursor.localize( currentCellOffset );
			getCellDimensions( currentCellOffset, currentCellDims );

			cellCursor.set( new FileCell< A >( creator, currentCellDims, currentCellOffset, entitiesPerPixel ) );
		}
		return cells;

	}

	/**
	 * To release the memory manually for debugging purposes
	 */
	public void freeAllMemory()
	{
		( ( FileImg< FileCell< A >> ) cells ).freeAllMemory();
	}
}
