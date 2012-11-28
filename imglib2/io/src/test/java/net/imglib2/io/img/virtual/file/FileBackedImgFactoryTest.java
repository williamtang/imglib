package net.imglib2.io.img.virtual.file;

import static org.junit.Assert.assertNotNull;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;


public class FileBackedImgFactoryTest {

	@Test
	public void test() {

		FileBackedImgFactory fact = new FileBackedImgFactory();

		assertNotNull(fact);

		Img<FileBackedType<DoubleType>> img1 =
			fact.create(new long[] { 2, 3, 4 }, new DoubleType(), 6);

		assertNotNull(img1);

		Img<FileBackedType<IntType>> img2 =
			fact.create(new long[] { 7, 5, 3, 1 }, new IntType(), 9);

		assertNotNull(img2);

		Img<FileBackedType<ComplexFloatType>> img3 =
			fact.create(new long[] { 4, 4 }, new ComplexFloatType(), 3);

		assertNotNull(img3);
	}

}
