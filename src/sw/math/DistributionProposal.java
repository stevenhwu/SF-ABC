package sw.math;

public interface DistributionProposal  {


	
	public double next(double mean);

	public double getLogq();

	public void updateVar(double var);
	
}
