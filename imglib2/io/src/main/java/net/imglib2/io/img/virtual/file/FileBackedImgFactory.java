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

package net.imglib2.io.img.virtual.file;

import net.imglib2.Dimensions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;


// TODO - this class does not implement the ImgFactory interface!

/**
 * @author Barry DeZonia
 */
public class FileBackedImgFactory
{

	public FileBackedImgFactory() {
	}

	public <T extends NativeType<T> & ComplexType<T>> FileBackedImg<T>
		create(long[] dims, T type, int bufSize)
	{
		return new FileBackedImg<T>(dims, type, bufSize);
	}

	public <T extends NativeType<T> & ComplexType<T>> FileBackedImg<T>
		create(int[] dims, T type, int bufSize)
	{
		long[] ldims = new long[dims.length];
		for (int i = 0; i < ldims.length; i++)
			ldims[i] = dims[i];
		return create(ldims, type, bufSize);
	}

	public <T extends NativeType<T> & ComplexType<T>> FileBackedImg<T>
		create(Dimensions dims, T type, int bufSize)
	{
		long[] ldims = new long[dims.numDimensions()];
		for (int i = 0; i < ldims.length; i++)
			ldims[i] = dims.dimension(i);
		return create(ldims, type, bufSize);
	}

}
