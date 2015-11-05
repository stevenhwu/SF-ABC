package sw.abc.parameter;

import java.util.HashMap;


public class ParametersCollection {
	
	final static String MU = "mu";
	final static String POPSIZE = "popsize";
	final static String THETA = "theta";
//	ArrayList<Parameters> allPar = new ArrayList<Parameters>();

	
	private String[] keys;
//	private String[] keys = new String[]{MU, POPSIZE, THETA};
	private double[] currentValue;
	
	private HashMap<String, Parameters> allParams = new HashMap<String, Parameters>();
//	HashMap<String, Double> params = new HashMap<String, Double>() {{
//			for (String s : keys) {
//				put(s, 0.0);
//			}
//		}};
	private int size;
		
	public ParametersCollection(String[] k, Parameters... p) {
		this.size = p.length;
		keys = new String[size];
		currentValue = new double[size];
		for (int i = 0; i < size; i++) {
			keys[i] = k[i].toLowerCase().trim();
			allParams.put(keys[i], p[i]);
		}
		
		
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
		
		return getParameter(keys[i]);
	}
	
	public void nextProir(int i){
		nextPrior(keys[i]);
	}

	private void nextPrior(String s){
		allParams.get(s).nextPrior();
	}

	public void nextProirs() {
		for (Parameters p : allParams.values()) {
			p.nextPrior();
		}
	}

	public void nextProposals() {
		for (Parameters p : allParams.values()) {
			p.nextProposal();
		}
	}

	
	public void nextProposal(int i) {
		allParams.get(keys[i]).nextProposal();
		
	}

	//	public void setParam(String s, double value) {
	//		params.put(s, value);
	//	}
	
	public void acceptNewValues() {
		for (int i = 0; i < size; i++) {
			acceptNewValue(i);
		}
		
	}

	public void acceptNewValue(int p) {
		allParams.get(keys[p]).acceptNewValue();
		
	}

	//	public void setParam(String s, double value) {
	//		params.put(s, value);
	//	}
	
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

	//	public void setParam(String s, double value) {
	//		params.put(s, value);
	//	}
	
	public double getNewValue(String s){
		
		return allParams.get(s).getNewValue();
		
	}

	public double[] getValues(){
		for (int i = 0; i < keys.length; i++) {
			currentValue[i] = getValue(keys[i]);
		}
		return currentValue;
	}
	public double getValue(String s) {
		
		return allParams.get(s).getValue();
	}
	public double getValue(int i) {
		
		return getValue(keys[i]);
	}
	
	public void updateProposalDistVar(TunePar tPar){
	
		for (int i = 0; i < allParams.size(); i++) {
	//		TODO recode this
			allParams.get(keys[i]).updateProposalDistVar(tPar.getTunePar(i));
		}
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
