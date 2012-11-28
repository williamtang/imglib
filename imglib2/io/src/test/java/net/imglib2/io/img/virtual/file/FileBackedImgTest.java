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
