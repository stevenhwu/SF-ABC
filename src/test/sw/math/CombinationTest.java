package test.sw.math;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import sw.math.Combination;

public class CombinationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testListCombination() {
		int[][] exp = {{0,1},{0,2},{1,2}};
		int[][] comb = Combination.ListCombination(3);
		for (int i = 0; i < comb.length; i++) {
			assertArrayEquals(exp[i], comb[i]);
		}

		exp = new int[][]{{0,1},{0,2},{0,3},{0,4},{1,2},{1,3},{1,4},{2,3},{2,4},{3,4}};
		comb = Combination.ListCombination(5);
		for (int i = 0; i < comb.length; i++) {
			assertArrayEquals(exp[i], comb[i]);
		}

		
		
	}

}
