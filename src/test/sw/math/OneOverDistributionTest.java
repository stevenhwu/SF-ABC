package test.sw.math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sw.math.OneOverDistribution;

public class OneOverDistributionTest {

	OneOverDistribution d;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		d = new OneOverDistribution(1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalLogPrior() {
		assertEquals(0, d.getLogPrior(1), 0);
		assertEquals(Math.log(1.0/2), d.getLogPrior(2), 1e-10);
		assertEquals(Math.log(1.0/10), d.getLogPrior(10), 1e-10);
		assertEquals(Math.log(1.0/100), d.getLogPrior(100), 1e-10);
		assertEquals(Math.log(1.0/10000), d.getLogPrior(10000), 1e-10);
		assertTrue(d.getLogPrior(10)>d.getLogPrior(10.1) );
		assertTrue(d.getLogPrior(10)>d.getLogPrior(100) );
		
		
	}

}
