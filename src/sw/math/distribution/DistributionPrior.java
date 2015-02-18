package sw.math.distribution;

public interface DistributionPrior  {


	public double getLogPrior(double x);
	
	public double nextPrior();
}
