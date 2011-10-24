package sw.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.Parameters;


public class CreateControlFile {

	String controlFile;
	

	double mu; //rate
	double theta;  //pop
	StringBuilder sb;

	public CreateControlFile(String cFileName) {
		setControlFile(cFileName);
		sb = new StringBuilder();
	}

	public String getControlFile() {
		return controlFile;
	}
	public double getMu() {
		return mu;
	}
	
	public double getTheta() {
		return theta;
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
			updateMu(p.getValue());
		}
		else if (p instanceof ParaTheta) {
			updateTheta(p.getValue());
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
			updateMu(p.nextPrior());
		}
		else if (p instanceof ParaTheta) {
			updateTheta(p.nextPrior());
		}
	}
	public void setParProposal(ArrayList<Parameters> par, int ind){

		Parameters p = par.get(ind);
		if (p instanceof ParaMu) {
			updateMu(p.nextProposal());
			updateTheta(par.get(ind+1).getValue());
		}
		else if (p instanceof ParaTheta) {
			updateTheta(p.nextProposal());
			updateMu(par.get(ind-1).getValue());
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
	
	private void updateMu(double mu){
		this.mu = mu;
	}

	private void updateTheta(double theta){
		this.theta = theta;
	}

	public StringBuilder updateTemplate2T(){
	
		
		sb.delete(0, sb.length());
		sb.append("An test ABC run\n")
		.append("1 population with ancient data\n")
		.append("Deme size\n")
		.append((int) theta).append("\n")
		.append("Sample sizes\n")
		.append(2).append(" sample groups\n")
		.append("100 0 0 0\n")
		.append("100 400 0 0\n")
		.append("Growth rates\n")
		.append("0\n")
		.append("Number of migration matrices\n0\n")
		.append("Historical event\n0\n")
		.append("Mutations per generation for the whole sequence\n")
		.append(mu).append("\n")
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
		.append((int) theta).append("\n")
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
		.append(mu).append("\n")
		.append("Number of loci\n")
		.append("750\n")
		.append("Data type\n")
		.append("DNA 0.5\n")
		.append("Mutation rates\n0 0 0\n");
		return sb;
	
	}
	
	public void setMu(double mu) {
		updateMu(mu);
	}

	public void setTheta(double theta) {
		updateTheta(theta);
	}

	public double[] getAllPar() {
		
		return new double[]{mu, theta};
	}
}
