package sw.abc.parameter;


import sw.math.DistributionPrior;
import sw.math.DistributionProposal;

public abstract class AbstractParameter implements Parameters {

	double value;
	double newValue;


	private double logQ;
	private double logP;	
	private double newLogP;

	DistributionPrior priorDist;
	DistributionProposal proposalDist;
	private int acceptCount;
	
//	
//	public AbstractParameter() {
//		super();
//	}
	
//	@Override
//	public void setNewValue(double newValue) {
//		this.newValue = newValue;
//	}

	@Override
	public int getAcceptCount() {
		return acceptCount;
	}
	@Override
	public void acceptNewValue(){
		value = newValue;
		logP = newLogP;
		acceptCount++;
	}
	@Override
	public double getLogq() {
//		System.out.println("logq\t"+logq);
		return logQ;
	}
	
	@Override
	public double getPriorRatio(){
		return (newLogP - logP);
	}
	
	@Override
	public double getValue() {
		return value;
	}
	

	
	@Override
	public double nextPrior(){
		value =  priorDist.init();
		logP = priorDist.getLogPrior(value);
		return value;
	}
	@Override
	public double nextProposal(){

		newValue = proposalDist.next(value);
		newLogP = priorDist.getLogPrior(newValue);
		while(Double.isInfinite(newLogP) ) {
			newValue = proposalDist.next(value);
			newLogP = priorDist.getLogPrior(newValue);
		}	
		logQ = proposalDist.getLogq();
		return newValue;
	}
	

	@Override
	public void setPrior(DistributionPrior d) {
		priorDist = d;
		nextPrior();
		
	}

	@Override
	public void setProposal(DistributionProposal d) {
		proposalDist = d;
//		value = proposal.next();

		
	}

	@Override
	public void updateProposal(double var) {
		proposalDist.updateVar(var);
		
	}
	
	public void setInitValue(double v) {
		value = v;
		logP = priorDist.getLogPrior(v);
	}

	public void setInitValue() {
		value = priorDist.init();
		logP = priorDist.getLogPrior(value);
	}
}