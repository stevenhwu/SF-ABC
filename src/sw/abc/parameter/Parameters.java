package sw.abc.parameter;


import sw.math.distribution.DistributionPrior;
import sw.math.distribution.DistributionProposal;


public interface Parameters {

	
	
	// add prior distribution
	public void setPrior(DistributionPrior d);
	
	public void setProposal(DistributionProposal d);
	
	// generated from proprsal
	public void nextProposal();
	
	// generated from prior
	public void nextPrior();

	
	double getLogQ();

//	void setNewValue(double newValue);

//	void setValue(double v);

	void acceptNewValue();
	
	double getPriorRatio();
	
	public abstract double getValue();

	public abstract int getAcceptCount();

	public void updateProposalDistVar(double var);
	

	public double getProposalDistVar();

	double getNewValue();

}
