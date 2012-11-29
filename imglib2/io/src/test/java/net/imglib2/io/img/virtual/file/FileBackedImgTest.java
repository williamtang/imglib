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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.junit.Test;


/**
 * @author Barry DeZonia
 */
public class FileBackedImgTest {

	@Test
	public void test() {
		
		FileBackedImgFactory fact = new FileBackedImgFactory();
		
		Img<FileBackedType<ComplexFloatType>> img =
				fact.create(new long[] { 9, 2, 7 }, new ComplexFloatType(), 5);

		assertNotNull(img);
		assertEquals(3, img.numDimensions());
		assertEquals(9, img.dimension(0));
		assertEquals(2, img.dimension(1));
		assertEquals(7, img.dimension(2));
		assertEquals(126, img.size());
		Cursor<?> cursor = img.cursor();
		assertNotNull(cursor);
		RandomAccess<?> accessor = img.randomAccess();
		assertNotNull(accessor);
		Img<?> img2 = img.copy();
		assertNotNull(img2);
		try {
			img.factory();
			fail();
		}
		catch (Exception e) {
			assertTrue(true);
		}
		FileBackedType<ComplexFloatType> value = img.firstElement();
		assertNotNull(value);
		Object o = img.iterationOrder();
		assertTrue(o instanceof FlatIterationOrder);
		cursor = img.localizingCursor();
		assertNotNull(cursor);
	}

}
