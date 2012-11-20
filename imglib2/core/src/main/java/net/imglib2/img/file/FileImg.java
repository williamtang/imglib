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
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.imglib2.img.file.FileImgFactory.FileNameGenerator;
import net.imglib2.img.list.AbstractListImg;

/**
 * TODO
 *
 * @author Martin Horn
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FileImg< T extends ExternalizableType< T >> extends AbstractListImg< T >
{
	private static final Logger LOGGER = Logger.getLogger( "FileImg" );

	private final HashMap< Integer, T > pixels;

	private final String baseFilename;

	private final T type;

//	private final FileNameGenerator fileNameGen;

	private static final MemoryWarningSystem MEMORY_WARNING_SYSTEM = new MemoryWarningSystem();

	protected FileImg( final long[] dim, final T type, final FileNameGenerator fileNameGen )
	{
		super( dim );

		this.baseFilename = fileNameGen.nextAbsoluteFileName();
//		this.fileNameGen = fileNameGen;
		this.type = type;
		this.pixels = new HashMap< Integer, T >( ( int ) numPixels );

		MemoryWarningSystem.setPercentageUsageThreshold( .8 );
		MEMORY_WARNING_SYSTEM.addListener( new MemoryWarningSystem.Listener()
		{
			@Override
			public void memoryUsageLow( final long usedMemory, final long maxMemory )
			{
				LOGGER.log( Level.INFO, "low memory. used mem: " + usedMemory + ";max mem: " + maxMemory + "." );
				freeAllMemory();

			}
		} );
	}


	@Override
	protected void setPixel( final int index, final T value )
	{
		pixels.put( index, value );
	}

	@Override
	protected T getPixel( final int index )
	{
		// freeAllMemory();
		T t = pixels.get( index );
		if ( t == null )
		{
			try
			{
				t = ( T ) type.getClass().newInstance();
				BufferedDataInputStream stream = null;
				final File f = new File( baseFilename + index );
				if ( !f.exists() )
				{
					t = type.emptyCopy();
				}
				else
				{
					stream = new BufferedDataInputStream( new FileInputStream( f ) );
					final ObjectInputStream objectInput = new ObjectInputStream( stream );
					t.readExternal( objectInput );
					stream.close();
				}

			}
			catch ( final Exception e )
			{
				throw new RuntimeException( e );
			}
		}
		pixels.put( index, t );
		return t;
	}

	/* Makes all pixels persistent (if dirty) and releases them */
	void freeAllMemory()
	{
		for ( final Entry< Integer, T > e : pixels.entrySet() )
		{
			if ( e.getValue().isDirty() )
			{
				// make persistent
				try
				{
					final BufferedDataOutputStream stream = new BufferedDataOutputStream( new FileOutputStream( new File( baseFilename + e.getKey() ), false ) );
					final ObjectOutputStream objectOutput = new ObjectOutputStream( stream );
					e.getValue().writeExternal( objectOutput );
					stream.close();

				}
				catch ( final IOException ioe )
				{
					throw new RuntimeException( ioe );
				}
			}
		}

		// free memory
		pixels.clear();

	}

	@Override
	public FileImg< T > copy()
	{
		throw new UnsupportedOperationException( "Not supported, yet!" );
		// TODO
		// The copy should contain the same values.
		// It is not clear how to do this.
	}
}
