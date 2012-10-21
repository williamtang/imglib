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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.cell.AbstractCell;

/**
 * Default (empty) implementation of the {@link AbstractCell}.
 * 
 * 
 * @author ImgLib2 developers
 * @author Tobias Pietzsch
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public final class FileCell< A extends ArrayDataAccess< A > & ExternalizableType< A >> extends AbstractCell< A > implements ExternalizableType< FileCell< A >>
{
	private A data;

	private int entitiesPerPixel;

	public FileCell()
	{
		// empty constructor for externalization
	}

	public FileCell( final A creator, final int[] dimensions, final long[] min, final int entitiesPerPixel )
	{
		super( dimensions, min );
		this.entitiesPerPixel = entitiesPerPixel;
		this.data = creator.createArray( numPixels * entitiesPerPixel );
	}

	@Override
	public A getData()
	{
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeExternal( ObjectOutput out ) throws IOException
	{
		byte[] clazz = data.getClass().getCanonicalName().getBytes();
		out.writeInt( clazz.length );
		out.write( clazz );
		out.writeInt( this.n );
		for ( int i = 0; i < this.n; i++ )
		{
			out.writeInt( this.dimension( i ) );
			out.writeLong( this.min( i ) );
		}
		out.writeInt( entitiesPerPixel );
		data.writeExternal( out );

	}

	/**
	 * {@inheritDoc}
	 */
	public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException
	{
		byte[] clazz = new byte[ in.readInt() ];
		in.read( clazz );
		try
		{
			data = ( A ) Class.forName( new String( clazz ) ).newInstance();
		}
		catch ( Exception e )
		{
			new RuntimeException( e );
		}
		int[] dim = new int[ in.readInt() ];
		long[] min = new long[ dim.length ];
		for ( int i = 0; i < dim.length; i++ )
		{
			dim[ i ] = in.readInt();
			min[ i ] = in.readLong();
		}
		entitiesPerPixel = in.readInt();
		init( dim, min );
		data.readExternal( in );

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty()
	{
		return data.isDirty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numBytes()
	{
		return data.numBytes() + data.getClass().getName().getBytes().length + n * 4 + n * 8;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileCell< A > emptyCopy()
	{
		int[] dim = new int[ n ];
		long[] min = new long[ n ];
		for ( int i = 0; i < dim.length; i++ )
		{
			dim[ i ] = dimension( i );
			min[ i ] = min( i );
		}
		FileCell< A > fileCell = new FileCell< A >( data.emptyCopy(), dim, min, entitiesPerPixel );
		return fileCell;
	}

}
