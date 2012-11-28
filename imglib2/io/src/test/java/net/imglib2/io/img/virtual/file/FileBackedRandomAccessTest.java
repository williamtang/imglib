package net.imglib2.io.img.virtual.file;

import static org.junit.Assert.assertEquals;
import net.imglib2.type.numeric.integer.LongType;

import org.junit.Test;


public class FileBackedRandomAccessTest {

	@Test
	public void test() {

		FileBackedImgFactory fact = new FileBackedImgFactory();

		FileBackedImg<LongType> img =
			fact.create(new long[] { 20 }, new LongType(), 5);

		FileBackedRandomAccess<LongType> accessor = img.randomAccess();

		accessor.setPosition(19, 0);
		accessor.get().setReal(13);
		accessor.setPosition(17, 0);
		accessor.get().setReal(12);
		accessor.setPosition(1, 0);
		accessor.get().setReal(11);
		accessor.setPosition(5, 0);
		accessor.get().setReal(10);
		accessor.setPosition(9, 0);
		accessor.get().setReal(9);

		accessor.setPosition(5, 0);
		assertEquals(10, accessor.get().getRealDouble(), 0);

		accessor.setPosition(17, 0);
		assertEquals(12, accessor.get().getRealDouble(), 0);

		accessor.setPosition(19, 0);
		assertEquals(13, accessor.get().getRealDouble(), 0);

		accessor.setPosition(1, 0);
		assertEquals(11, accessor.get().getRealDouble(), 0);

		accessor.setPosition(9, 0);
		assertEquals(9, accessor.get().getRealDouble(), 0);

	}

}
