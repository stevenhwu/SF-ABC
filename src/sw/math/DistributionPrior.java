package sw.math;

public interface DistributionPrior  {


	public double getLogPrior(double x);
	
	public double init();
}
