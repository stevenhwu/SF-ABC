package sw.abc.parameter;

import dr.inference.loggers.LogColumn;
import sw.math.DistributionPrior;
import sw.math.DistributionProposal;

public abstract class AbstractParameter implements Parameters {

	double value;
	double newValue;


	private double logQ;
	private double logP;	
	private double newLogP;

	DistributionPrior prior;
	DistributionProposal proposal;
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
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public double nextPrior(){
		value =  prior.init();
		logP = prior.getLogPrior(value);
		return value;
	}
	@Override
	public double nextProposal(){

		newValue = proposal.next(value);
		newLogP = prior.getLogPrior(newValue);
		while(Double.isInfinite(newLogP) ) {
			newValue = proposal.next(value);
			newLogP = prior.getLogPrior(newValue);
		}	
		logQ = proposal.getLogq();
		return newValue;
	}
	

	@Override
	public void setPrior(DistributionPrior d) {
		prior = d;
		nextPrior();
		
	}

	@Override
	public void setProposal(DistributionProposal d) {
		proposal = d;
//		value = proposal.next();

		
	}


	@Override
	public LogColumn[] getColumns() {
		
		return null;
	}
	
	public void setInitValue(double v) {
		value = v;
		logP = prior.getLogPrior(v);
	}


}