package sw.abc.stat.summary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;



public class SStatFlexable extends AbstractSummaryStat {

	HashMap<String, double[]> coef = new HashMap<String, double[]>(); 
//	double[] coefMu;
//	double[] coefTheta;
	
	public SStatFlexable(){
		
	}
	
	
	public double calStatMu(double[] par) {

		double stat = calStatParam(par, coef.get("Mu"));
		return stat;
	}
	
	public double calStatTheta(double[] par) {
		double stat = calStatParam(par, coef.get("Theta"));
		return stat;
	}

	private double calStatParam(double[] par, double[] indCoef) {
		double stat = indCoef[0];
		for (int i = 0; i < par.length; i++) {
			stat += indCoef[i+1] * par[i];
		}
		return stat;
	}

	public void addCoef(String key, double[] value) {
		coef.put(key, value);		
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Regression Coefficient\n");
		coef.size();
		for (String key : coef.keySet()) {
			sb.append(key).append("\t").append(Arrays.toString( coef.get(key) )).append("\n");
		}
		
		return sb.toString();
	}
	
	
}
