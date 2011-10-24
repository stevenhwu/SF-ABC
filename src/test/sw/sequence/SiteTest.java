package test.sw.sequence;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sw.sequence.Site;

public class SiteTest {
	
	private Site s1;
	
	@Before
	public void setUp() throws Exception {
		double[] f1 = {0.1,0.2,0.3,0.4};
		
		s1 = new Site(f1);
		
	}

	@Test @Ignore
	public void testSiteIntDoubleArray() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testSiteDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIndex() {
		
		assertEquals(0, s1.getIndex() );
		
	}

	@Test
	public void testSetIndex() {
		s1.setIndex(10);
		
		assertEquals(10, s1.getIndex());
		
		
	}

	@Test
	public void testGetFreqs() {
		double[] f1 = new double[] {0.1,0.2,0.3,0.4};
		assertArrayEquals(f1, s1.getFreqs(), 0);

	}

	@Test
	public void testSetFreqs() {
		s1.setFreqs(new double[] {0.3,0.2,0.35,0.15});
		double[] f1 = new double[] {0.3,0.2,0.35,0.15};
		assertArrayEquals(f1, s1.getFreqs(), 0);
		
	}



}
