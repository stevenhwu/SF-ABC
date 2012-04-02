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
	private static final int NOPAR = 2;

	private int count;
	private int tunesize;

	private int[] state;

	private double[][] allPar;

	public SavePar(int n) {

		tunesize = ++n;
		state = new int[tunesize];

		init();
	}

	public void resetCount() {
		count = 0;
	}


	public void init() {
		allPar = new double[tunesize][NOPAR];
		resetCount();
	}

	public void add(ArrayList<Parameters> newPar) {

		for (int i = 0; i < newPar.size(); i++) {
			allPar[count][i] = newPar.get(i).getValue();
		}
		count++;

	}

	
	public void add(ParametersCollection newPar) {

		allPar[count] = newPar.getValues();
		count++;

	}
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("state\tmu\td\tpi\trho\tsd\tlikelihood\n");
		Formatter f = new Formatter(sb);

		// NumberFormat formatter = NumberFormat.getNumberInstance();
		for (int i = 0; i < allPar.length; i++) {
			f.format("%d\t", state[i]);
			for (int j = 0; j < allPar[i].length; j++) {
				f.format("%.4f\t", allPar[i][j]);
			}
			f.format("\n", "");

		}
		return sb.toString();

	}

	/**
	 * calculate the acceptance rate
	 * 
	 * @return
	 */

	public double[] calAccRate(int size) {

		double[] accRate = new double[NOTUNE];
		
		size -= 1;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < NOTUNE; j++) {
				if (allPar[i][j] != allPar[i + 1][j]) {
					accRate[j]++;
				}				
			}
		}
		for (int i = 0; i < accRate.length; i++) {
			accRate[i] /= size;
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
