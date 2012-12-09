package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HyperVolumePointSetTest {

	@Test
	public void test() {
		PointSet ps = new HyperVolumePointSet(new long[]{2,4}, new long[]{5,7});

		assertEquals(16, ps.size());
		assertEquals(2, ps.min(0));
		assertEquals(4, ps.min(1));
		assertEquals(5, ps.max(0));
		assertEquals(7, ps.max(1));
		assertEquals(2, ps.realMin(0), 0);
		assertEquals(4, ps.realMin(1), 0);
		assertEquals(5, ps.realMax(0), 0);
		assertEquals(7, ps.realMax(1), 0);
		assertEquals(4, ps.dimension(0));
		assertEquals(4, ps.dimension(1));
		assertTrue(ps.includes(new long[]{2,4}));
		assertTrue(ps.includes(new long[]{5,7}));
		assertTrue(ps.includes(new long[]{2,7}));
		assertTrue(ps.includes(new long[]{5,4}));
		assertTrue(ps.includes(new long[]{4,5}));
		assertFalse(ps.includes(new long[]{0,0}));
		assertFalse(ps.includes(new long[]{2,3}));
		assertFalse(ps.includes(new long[]{6,2}));
		
		ps.translate(new long[]{1,2});

		assertEquals(16, ps.size());
		assertEquals(3, ps.min(0));
		assertEquals(6, ps.min(1));
		assertEquals(6, ps.max(0));
		assertEquals(9, ps.max(1));
		assertEquals(3, ps.realMin(0), 0);
		assertEquals(6, ps.realMin(1), 0);
		assertEquals(6, ps.realMax(0), 0);
		assertEquals(9, ps.realMax(1), 0);
		assertEquals(4, ps.dimension(0));
		assertEquals(4, ps.dimension(1));
		assertTrue(ps.includes(new long[]{3,6}));
		assertTrue(ps.includes(new long[]{6,9}));
		assertTrue(ps.includes(new long[]{3,9}));
		assertTrue(ps.includes(new long[]{6,6}));
		assertTrue(ps.includes(new long[]{5,7}));
		assertFalse(ps.includes(new long[]{1,2}));
		assertFalse(ps.includes(new long[]{3,5}));
		assertFalse(ps.includes(new long[]{7,4}));
	}

}
