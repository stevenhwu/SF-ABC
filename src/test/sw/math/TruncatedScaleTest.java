package test.sw.math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sw.math.distribution.TruncatedScale;

public class TruncatedScaleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNext() {
		double lower = 0;
		double upper = 1;
		for (int j = 0; j < 100; j++) {
			TruncatedScale s = new TruncatedScale(Math.random(), lower, upper);
			for (int i = 0; i < 1e4; i++) {
				double next = s.next(s.nextDouble());
				assertTrue("not equal\t"+next, (next>lower && next < upper) );
			}
		}
	}

}
