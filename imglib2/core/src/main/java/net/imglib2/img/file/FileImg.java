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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.imglib2.FlatIterationOrder;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.file.FileImgFactory.FileNameGenerator;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;

/**
 * This {@link Img} stores an image in a single linear {@link ArrayList}. Each
 * pixel is stored as an individual object, so {@link FileImg} should only be
 * used for images with relatively few pixels. In principle, the number of
 * entities stored is limited to {@link Integer#MAX_VALUE}.
 * 
 * @param <T>
 *            The value type of the pixels. You can us {@link Type}s or
 *            arbitrary {@link Object}s. If you use non-{@link Type} pixels,
 *            note, that you cannot use {@link Type#set(Type)} to change the
 *            value stored in every reference. Instead, you can use the
 *            {@link FileCursor#set(Object)} and
 *            {@link FileRandomAccess#set(Object)} methods to alter the
 *            underlying {@link ArrayList}.
 * 
 * @author ImgLib2 developers
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FileImg< T extends ExternalizableType< T >> extends AbstractImg< T >
{
	private static final Logger LOGGER = Logger.getLogger( "FileImg" );

	final protected int[] step;

	final protected int[] dim;

	private final HashMap< Integer, T > pixels;

	private final String baseFilename;

	private T type;

	private final FileNameGenerator fileNameGen;

	private static final MemoryWarningSystem MEMORY_WARNING_SYSTEM = new MemoryWarningSystem();

	protected FileImg( final long[] dim, final T type, FileNameGenerator fileNameGen )
	{
		super( dim );

		this.dim = new int[ n ];
		this.baseFilename = fileNameGen.nextAbsoluteFileName();
		this.fileNameGen = fileNameGen;
		this.type = type;
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int ) dim[ d ];

		this.step = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, this.step );

		this.pixels = new HashMap< Integer, T >( ( int ) numPixels );

		// if (type instanceof Type<?>) {
		// final Type<?> t = (Type<?>) type;
		// @SuppressWarnings("unchecked")
		// final ArrayList<Type<?>> tpixels = (ArrayList<Type<?>>) this.pixels;
		// for (int i = 0; i < this.numPixels; ++i)
		// tpixels.add(t.createVariable());
		// } else {
		// for (int i = 0; i < this.numPixels; ++i)
		// pixels.add(null);
		// }

		MemoryWarningSystem.setPercentageUsageThreshold( .8 );
		MEMORY_WARNING_SYSTEM.addListener( new MemoryWarningSystem.Listener()
		{

			public void memoryUsageLow( long usedMemory, long maxMemory )
			{
				LOGGER.log( Level.INFO, "low memory. used mem: " + usedMemory + ";max mem: " + maxMemory + "." );
				freeAllMemory();

			}
		} );
	}

	void set( T t, int i )
	{
		pixels.put( i, t );
	}

	T get( int i )
	{
		// freeAllMemory();
		T t = pixels.get( i );
		if ( t == null )
		{
			try
			{
				t = ( T ) type.getClass().newInstance();
				BufferedDataInputStream stream = null;
				File f = new File( baseFilename + i );
				if ( !f.exists() )
				{
					t = type.emptyCopy();
				}
				else
				{
					stream = new BufferedDataInputStream( new FileInputStream( f ) );
					ObjectInputStream objectInput = new ObjectInputStream( stream );
					t.readExternal( objectInput );
					stream.close();
				}

			}
			catch ( Exception e )
			{
				throw new RuntimeException( e );
			}
		}
		pixels.put( i, t );
		return t;
	}

	/* Makes all pixels persistent (if dirty) and releases them */
	void freeAllMemory()
	{
		for ( Entry< Integer, T > e : pixels.entrySet() )
		{
			if ( e.getValue().isDirty() )
			{
				// make persistent
				try
				{
					BufferedDataOutputStream stream = new BufferedDataOutputStream( new FileOutputStream( new File( baseFilename + e.getKey() ), false ) );
					ObjectOutputStream objectOutput = new ObjectOutputStream( stream );
					e.getValue().writeExternal( objectOutput );
					stream.close();

				}
				catch ( IOException ioe )
				{
					throw new RuntimeException( ioe );
				}
			}
		}

		// free memory
		pixels.clear();

	}

	@Override
	public FileCursor< T > cursor()
	{
		return new FileCursor< T >( this );
	}

	@Override
	public FileLocalizingCursor< T > localizingCursor()
	{
		return new FileLocalizingCursor< T >( this );
	}

	@Override
	public FileRandomAccess< T > randomAccess()
	{
		return new FileRandomAccess< T >( this );
	}

	@Override
	public FileImgFactory< T > factory()
	{
		return new FileImgFactory< T >( fileNameGen );
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}

	// private static <A extends Type<A>> FileImg<A> copyWithType(
	// final FileImg<A> img) {
	// final FileImg<A> copy = new FileImg<A>(img.dimension, img
	// .firstElement().createVariable());
	//
	// final FileCursor<A> source = img.cursor();
	// final FileCursor<A> target = copy.cursor();
	//
	// while (source.hasNext())
	// target.next().set(source.next());
	//
	// return copy;
	// }

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public FileImg< T > copy()
	{
		// final T type = firstElement();
		// if (type instanceof Type<?>) {
		// return copyWithType((FileImg<Type>) this);
		// } else {
		return new FileImg< T >( dimension, type, fileNameGen );
		// }
	}
}
