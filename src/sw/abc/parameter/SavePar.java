/**
 * 
 */
package sw.abc.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;

/**
 * @author steven
 * 
 */
public class SavePar {

	private static final int NOTUNE = 2;

	private int count;
	private int tunesize;
	private int tunesizeP1;

	private int[] state;

	private double[][] allPar;

	public SavePar(int n) {
		tunesize = n;
		tunesizeP1 = ++n;
		state = new int[tunesizeP1];

		init();
	}

	public void resetCount() {
		allPar[0] = allPar[tunesize];
		count = 1;
	}


	public void init() {
		allPar = new double[tunesizeP1][NOTUNE];
//		resetCount();
		count=0;
	}
//
//	public void add(ArrayList<Parameters> newPar) {
//
//		for (int i = 0; i < newPar.size(); i++) {
//			allPar[count][i] = newPar.get(i).getValue();
//		}
//		count++;
//
//	}

	
	public void add(ParametersCollection newPar) {

		for (int i = 0; i < NOTUNE; i++) {
			allPar[count][i] = newPar.getValues(i);
		}

		count++;

	}

	/**
	 * calculate the acceptance rate
	 * 
	 * @return
	 */

	public double[] calAccRate(int size) {

		double[] accRate = new double[NOTUNE];
		
//		size -= 1;
		for (int i = 0; i < tunesize; i++) {
			for (int j = 0; j < NOTUNE; j++) {
				if (allPar[i][j] != allPar[i + 1][j]) {
					accRate[j]++;
				}				
			}
		}
		for (int i = 0; i < accRate.length; i++) {
			accRate[i] /= tunesize;
		}
		resetCount();

		return accRate;
	}


	public int[] getState() {
		return state;
	}



	public int getCount() {
		return count;
	}


	public double[][] getAllPar() {
		return allPar;
	}

	public static int getNotune() {
		return NOTUNE;
	}

}
