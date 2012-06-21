package sw.abc.parameter;


import sw.math.DistributionPrior;
import sw.math.DistributionProposal;

public abstract class AbstractParameter implements Parameters {

	
	private double logQ;
	
	protected int acceptCount;
	protected double value;
	protected double newValue;
	protected double logP;	
	protected double newLogP;

	protected DistributionPrior priorDist;
	protected DistributionProposal proposalDist;
	
	
	public AbstractParameter(){}
@Override
	public void acceptNewValue(){
		value = newValue;
		logP = newLogP;
		acceptCount++;
	}
	@Override
	public void nextPrior(){
		value = priorDist.nextPrior();
		newValue = value;
		logP = priorDist.getLogPrior(value);
	}
	@Override
	public void nextProposal(){

		newValue = proposalDist.next(value);
		newLogP = priorDist.getLogPrior(newValue);
		while(Double.isInfinite(newLogP) ) {
			newValue = proposalDist.next(value);
			newLogP = priorDist.getLogPrior(newValue);
		}	
		logQ = proposalDist.getLogq();
		
	}
	

	//	
	//	public AbstractParameter() {
	//		super();
	//	}
		
	//	@Override
	//	public void setNewValue(double newValue) {
	//		this.newValue = newValue;
	//	}
	
		@Override
	public double getValue() {
		return value;
	}
	@Override
	public double getNewValue() {
		return newValue;
	}
		@Override
	public double getPriorRatio(){
		return (newLogP - logP);
	}
	@Override
		public double getLogQ() {
	//		System.out.println("logq\t"+logq);
			return logQ;
		}
	@Override
	public int getAcceptCount() {
		return acceptCount;
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

	public void setInitValue(double v) {
		value = v;
		newValue = value;
		logP = priorDist.getLogPrior(value);
	}
	
	
	@Override
	public void updateProposalDistVar(double var) {
		proposalDist.updateVar(var);
		
	}
	
	@Override
	public double getProposalDistVar(){
		
		return proposalDist.getVar();
	}
}