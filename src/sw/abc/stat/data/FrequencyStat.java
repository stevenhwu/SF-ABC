package sw.abc.stat.data;

import org.apache.commons.math.util.MathUtils;

public class FrequencyStat {

	public FrequencyStat() {

	}

	// public static double calDiff(int[] f1, int[] f2){
	//
	// double dist = MathUtils.distance(f1, f2);
	// return dist;
	// }
	public static double calDiff(double[] f1, double[] f2) {

		double dist = MathUtils.distance(f1, f2);
		return dist;
	}

	// public static double calDiff(int[][] f1, int[][] f2){
	//
	// double count = 0;
	// for (int i = 0; i < f2.length; i++) {
	// count += MathUtils.distance(f1[i], f2[i]);
	// }
	// return count;
	// }
	public static double calDiff(double[][] f1, double[][] f2) {

		double count = 0;
		for (int i = 0; i < f2.length; i++) {
			count += MathUtils.distance(f1[i], f2[i]);
		}
		return count;
	}

	private static double calChiDiff(double[] f1) {

		double exp = 1.0 / f1.length;
		double chiSq = 0;

		for (int i = 0; i < f1.length; i++) {
			double dev = f1[i] - exp;
			chiSq += dev * dev / exp;
		}
		return chiSq;
	}

	public static double[] calChiDiff(double[][] spec) {
		double[] chiStat = new double[spec.length]; 
		for (int i = 0; i < spec.length; i++) {
			chiStat[i] = calChiDiff(spec[i]);
		}
		return chiStat;
	}

	public static double[] calFreqPattern(double[][] spec) {
		double[] pattern = new double[spec.length]; 
		for (int i = 0; i < spec.length; i++) {
			pattern[i] = calChiDiff(spec[i]);
		}
		return pattern;
		
	}
}
