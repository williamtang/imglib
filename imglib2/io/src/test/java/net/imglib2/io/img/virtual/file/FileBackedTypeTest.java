package net.imglib2.io.img.virtual.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.junit.Test;


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
