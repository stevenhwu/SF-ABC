package sw.math;

import org.apache.commons.math3.util.ArithmeticUtils;

public class Combination {

	public static int calNoComb(int n){
//		int noComb = (int)( ArithmeticUtils.factorial(n)/ArithmeticUtils.factorial(n-2)/2.0 );
//		try {
			long longList = ArithmeticUtils.binomialCoefficient(n ,2);
			if( longList > Integer.MAX_VALUE){
				try {
					throw new Exception("n too big" +"\t"+ longList +" > "+ Integer.MAX_VALUE);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		int noComb = (int) longList;
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
