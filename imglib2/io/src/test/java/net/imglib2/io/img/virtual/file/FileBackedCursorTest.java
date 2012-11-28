package net.imglib2.io.img.virtual.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.imglib2.Cursor;
import net.imglib2.type.numeric.integer.LongType;

import org.junit.Test;


public class FileBackedCursorTest {

	@Test
	public void test() {
		FileBackedImgFactory fact = new FileBackedImgFactory();
		FileBackedImg<LongType> img =
			fact.create(new long[] { 20 }, new LongType(), 5);
		Cursor<FileBackedType<LongType>> cursor = img.cursor();
		int p = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			p++;
		}
		assertEquals(20, p);
		cursor.reset();
		p = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			p++;
		}
		assertEquals(20, p);
		cursor.reset();
		p = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			p++;
			cursor.get().setReal(p);
		}
		assertEquals(20, p);
		cursor.reset();
		p = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			p++;
			assertEquals(p, cursor.get().getRealDouble(), 0);
		}
		assertEquals(20, p);
		cursor.reset();

		img = fact.create(new long[] { 2, 3, 4 }, new LongType(), 4);
		cursor = img.cursor();
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(0, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(0, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(0, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(0, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(0, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(0, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(1, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(1, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(1, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(1, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(1, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(1, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(2, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(2, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(2, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(2, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(2, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(2, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(3, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(0, cursor.getIntPosition(1));
		assertEquals(3, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(3, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(1, cursor.getIntPosition(1));
		assertEquals(3, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(0, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(3, cursor.getIntPosition(2));
		cursor.fwd();
		assertEquals(1, cursor.getIntPosition(0));
		assertEquals(2, cursor.getIntPosition(1));
		assertEquals(3, cursor.getIntPosition(2));
		try {
			cursor.fwd();
			fail();
		}
		catch (Exception e) {
			assertTrue(true);
		}

		assertNotNull(cursor.copy());
		assertNotNull(cursor.copyCursor());
	}

}
