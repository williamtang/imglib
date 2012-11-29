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
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.junit.Test;


/**
 * @author Barry DeZonia
 */
public class FileBackedTypeTest {

	@Test
	public void test() {
		FileBackedImgFactory fact = new FileBackedImgFactory();
		FileBackedImg<ComplexFloatType> img =
			fact.create(new int[] { 12, 3 }, new ComplexFloatType(), 9);
		FileBackedType<ComplexFloatType> value = img.firstElement();
		assertNotNull(value);
		assertEquals(0, value.getRealDouble(), 0);
		assertEquals(0, value.getImaginaryDouble(), 0);
		value.setReal(7);
		value.setImaginary(12);
		assertEquals(7, value.getRealDouble(), 0);
		assertEquals(12, value.getImaginaryDouble(), 0);

		FileBackedCursor<ComplexFloatType> cursor = img.cursor();
		int p = 0;
		while (cursor.hasNext()) {
			FileBackedType<ComplexFloatType> ref = cursor.next();
			ref.setImaginary(p++);
			ref.setReal(p + 7);
		}
		cursor.reset();
		p = 0;
		while (cursor.hasNext()) {
			FileBackedType<ComplexFloatType> ref = cursor.next();
			assertEquals(p++, ref.getImaginaryDouble(), 0);
			assertEquals(p + 7, ref.getRealDouble(), 0);
		}

		cursor.reset();
		cursor.fwd();
		cursor.get().setOne();
		assertEquals(1, cursor.get().getRealDouble(), 0);
		assertEquals(0, cursor.get().getImaginaryDouble(), 0);
		cursor.get().mul(14);
		assertEquals(14, cursor.get().getRealDouble(), 0);
		assertEquals(0, cursor.get().getImaginaryDouble(), 0);
	}

}
