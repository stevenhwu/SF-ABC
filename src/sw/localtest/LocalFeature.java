package sw.localtest;

import java.util.Arrays;

public class LocalFeature {

	protected static double[] getRegressionResult(String obsDataNamePrefix) {
		int fileIndex = obsDataNamePrefix.indexOf("_");
		double[] initValue = new double[2];
		if(fileIndex== -1){
			initValue = new double[]{0.00001, 3000};
		}
		else {
			int i = Integer.parseInt(obsDataNamePrefix.substring(fileIndex+1));
			initValue = RegressionResult.result[i];
		}
		System.out.println("Init values:\t"+Arrays.toString(initValue));
		return initValue;
	}

	public LocalFeature() {
		super();
	}

}