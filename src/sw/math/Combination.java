package sw.math;

import org.apache.commons.math.util.MathUtils;

public class Combination {

	public static int calNoComb(int n){
		int noComb = (int)( MathUtils.factorial(n)/MathUtils.factorial(n-2)/2.0 );
		return noComb;
	}
	
	public static int[][] ListCombination(int n) {

		int noComb = calNoComb(n);
		int comb[][] = new int[noComb][2];
		int i = 0;
		for (int j = 0; j < n; j++) {
			for (int k = j+1; k < n; k++) {
				comb[i][0] = j;
				comb[i][1] = k;
				i++;

			}
		}			
		

		return comb;

	}

}
