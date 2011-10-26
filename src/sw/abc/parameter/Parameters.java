package sw.abc.parameter;


import sw.math.DistributionPrior;
import sw.math.DistributionProposal;


public interface Parameters {

	
	public void init();
	
	// add prior distribution
	public void setPrior(DistributionPrior d);
	
	public void setProposal(DistributionProposal d);
	
	// generated from proprsal
	public double nextProposal();
	
	// generated from prior
	public double nextPrior();

	
	double getLogq();

//	void setNewValue(double newValue);

//	void setValue(double v);

	void acceptNewValue();
	
	double getPriorRatio();
	
	public abstract double getValue();

	public abstract int getAcceptCount();

}
