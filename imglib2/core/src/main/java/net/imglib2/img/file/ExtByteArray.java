/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2011
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   Oct 20, 2012 (hornm): created
 */
package net.imglib2.img.file;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

/**
 * 
 * @author hornm, University of Konstanz
 */
public class ExtByteArray implements ByteAccess, ArrayDataAccess< ExtByteArray >, ExternalizableType< ExtByteArray >
{

	private boolean isDirty = false;

	protected byte data[];

	public ExtByteArray()
	{
		this( null );
		// empty constructor for externalization
	}

	public ExtByteArray( final int numEntities )
	{
		this.data = new byte[ numEntities ];
	}

	public ExtByteArray( final byte[] data )
	{
		this.data = data;
	}

	@Override
	public byte getValue( final int index )
	{
		return data[ index ];
	}

	@Override
	public void setValue( final int index, final byte value )
	{
		data[ index ] = value;
		isDirty = true;

	}

	@Override
	public byte[] getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public ExtByteArray createArray( final int numEntities )
	{
		return new ExtByteArray( numEntities );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeExternal( ObjectOutput out ) throws IOException
	{
		out.writeInt( data.length );
		out.write( data, 0, data.length );

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException
	{
		data = new byte[ in.readInt() ];
		in.read( data );

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty()
	{
		return isDirty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numBytes()
	{
		return data.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtByteArray emptyCopy()
	{
		return new ExtByteArray( new byte[ data.length ] );
	}

}
