package sw.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.Parameters;


public class CreateControlFile {

	final static String MU = "Mu";
	final static String THETA = "Theta";
	
	String controlFile;
	
	String[] parList = new String[]{MU, THETA};
	double[] allpar = new double[parList.length];
//	double mu; //rate
//	double theta;  //pop
	HashMap<String, Double> params = new HashMap<String, Double>(){{
		for (String s : parList) {
			put(s, 0.0);
		}
	}};
	StringBuilder sb;

	public CreateControlFile(String cFileName) {
		setControlFile(cFileName);
		sb = new StringBuilder();
	}

	public String getControlFile() {
		return controlFile;
	}
	
	@Deprecated
	public double getMu() {
		return params.get(MU);
	}
	@Deprecated
	public double getTheta() {
		return params.get(THETA);
	}
	public double getPar(String s) {
		return params.get(s);
	}


	public void setControlFile(String controlFile) {
		this.controlFile = controlFile;
	}

	public void setInitPar(ArrayList<Parameters> p) {
		for (Parameters parameters : p) {
			setInitPar(parameters);
		}
	}
	public void setInitPar(Parameters p) {
		if (p instanceof ParaMu) {
			setParam(MU, p.getValue());
		}
		else if (p instanceof ParaTheta) {
			setParam(THETA, p.getValue());
		}
	}

	public void setParPrior(ArrayList<Parameters> p){
		for (Parameters parameters : p) {
			setParPrior(parameters);
		}
	}
	public void setParPrior(Parameters p){
		//		System.out.println( p.getClass().toString());
		//		System.out.println( p.getClass().getName());
		if (p instanceof ParaMu) {
			setParam(MU, p.nextPrior());
		}
		else if (p instanceof ParaTheta) {
			setParam(THETA, p.nextPrior());
		}
	}
	public void setParProposal(ArrayList<Parameters> par, int ind){

		Parameters p = par.get(ind);
		if (p instanceof ParaMu) {
			setParam(MU, p.nextProposal());
			setParam(THETA, par.get(ind+1).getValue());
		}
		else if (p instanceof ParaTheta) {
			setParam(THETA, p.nextProposal());
			setParam(MU, par.get(ind-1).getValue());
		}
	}

	public void updateFile(int noTime){
		if(noTime==2){
			sb = updateTemplate2T();
		}
		else if (noTime==3){
			sb = updateTemplate3T();
		}
		

		try {
			PrintWriter out
			= new PrintWriter(new BufferedWriter(new FileWriter(controlFile)));
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
	
//	@Deprecated
//	private void setMu(double mu){
//		params.put("Mu", mu);
//	}
//	
//	@Deprecated
//	private void setTheta(double theta){
//		params.put("Theta", theta);
//	}

	public StringBuilder updateTemplate2T(){
	
		
		sb.delete(0, sb.length());
		sb.append("An test ABC run\n")
		.append("1 population with ancient data\n")
		.append("Deme size\n")
		.append((int) ((double) params.get(THETA))).append("\n")
		.append("Sample sizes\n")
		.append(2).append(" sample groups\n")
		.append("100 0 0 0\n")
		.append("100 400 0 0\n")
		.append("Growth rates\n")
		.append("0\n")
		.append("Number of migration matrices\n0\n")
		.append("Historical event\n0\n")
		.append("Mutations per generation for the whole sequence\n")
		.append(params.get(MU)).append("\n")
		.append("Number of loci\n")
		.append("750\n")
		.append("Data type\n")
		.append("DNA 0.5\n")
		.append("Mutation rates\n0 0 0\n");
		return sb;
	
	}

	

	public StringBuilder updateTemplate3T(){
	
		
		sb.delete(0, sb.length());
		sb.append("An test ABC run\n")
		.append("1 population with ancient data\n")
		.append("Deme size\n")
		.append((int) ((double) params.get(THETA))).append("\n")
		.append("Sample sizes\n")
		.append(3).append(" sample groups\n")
		.append("40 0 0 0\n")
		.append("40 400 0 0\n")
		.append("40 800 0 0\n")
		.append("Growth rates\n")
		.append("0\n")
		.append("Number of migration matrices\n0\n")
		.append("Historical event\n0\n")
		.append("Mutations per generation for the whole sequence\n")
		.append(params.get(MU)).append("\n")
		.append("Number of loci\n")
		.append("750\n")
		.append("Data type\n")
		.append("DNA 0.5\n")
		.append("Mutation rates\n0 0 0\n");
		return sb;
	
	}
	
	@Deprecated
	public void setMu(double mu) {
		setMu(mu);
	}
	@Deprecated
	public void setTheta(double theta) {
		setTheta(theta);
	}
	public void setParam(String s, double value) {
		params.put(s, value);
	}

	public double[] getAllPar() {

		for (int i = 0; i < parList.length; i++) {
			allpar[i] = params.get(parList[i]);
		}

		return allpar;
	}
}
