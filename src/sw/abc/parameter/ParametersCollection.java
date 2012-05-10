package sw.abc.parameter;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math.random.RandomDataImpl;


public class ParametersCollection {
	
	final static String MU = "mu";
	final static String POPSIZE = "popsize";
//	ArrayList<Parameters> allPar = new ArrayList<Parameters>();
	static RandomDataImpl r = new RandomDataImpl();
	
	
	String[] keys = new String[]{MU, POPSIZE};
	double[] currentValue;
	
	HashMap<String, Parameters> allParams = new HashMap<String, Parameters>();
//	HashMap<String, Double> params = new HashMap<String, Double>() {{
//			for (String s : keys) {
//				put(s, 0.0);
//			}
//		}};
	private int size;
		
	public ParametersCollection(String[] k, Parameters... p) {
		this.size = k.length;
		this.keys = k;
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i].toLowerCase().trim();
			allParams.put(keys[i], p[i]);
		}
		currentValue = new double[keys.length];
		
	}
	
	public int getSize(){
		return size;
	}
//	public double getPar(String s) {
//			return params.get(s);
//	}
	public Parameters getParameter(String s) {
		return allParams.get(s);
	}
	public Parameters getParameter(int i) {
		
		return allParams.get(keys[i]);
	}


	
	public double getNextProir(int i){
		return getNextPrior(keys[i]);
	}
	
	public void getNextProir() {
		for (Parameters p : allParams.values()) {
			p.nextPrior();
		}
	}

	private double getNextPrior(String s){
		double v = allParams.get(s).nextPrior();
		return v;
	}

	public double getNextProposal(int i) {

		double v = allParams.get(keys[i]).nextProposal();
		return v;
	}



	public void acceptNewValue(int p) {
		allParams.get(keys[p]).acceptNewValue();
		
	}

	public int[] getAcceptCounts() {
		int[] c = new int[size];
		for (int i = 0; i < c.length; i++) {
			c[i] = getAcceptCount(i); 
		}
		return c;
	}
	
	public int getAcceptCount(int i) {
		
		return allParams.get(keys[i]).getAcceptCount();
	}
	public double[] getValues(){
		for (int i = 0; i < keys.length; i++) {
			currentValue[i] = allParams.get(keys[i]).getValue();
		}
		return currentValue;
	}
	public double getValues(String s) {
		
		return allParams.get(s).getValue();
	}
	public double getValues(int i) {
		
		return allParams.get(keys[i]).getValue();
	}
//	public void setParam(String s, double value) {
//		params.put(s, value);
//	}

//	public double[] getAllPar() {
//
//		for (int i = 0; i < parList.length; i++) {
//			allpar[i] = params.get(parList[i]);
//		}
//
//		return allpar;
//	}

}
