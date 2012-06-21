package sw.math;


public class ZTestDistribution extends AbstractDistributionProposal implements DistributionPrior{

	double mean;
	
	public ZTestDistribution(double mean) {
		this.mean = mean;
	}


	@Override
	public double nextPrior() {

		return mean++;
	}
	

	@Override
	public double next(double mean) {
		logQ = mean;
		return mean+2;
	}

	@Override
	public double getLogPrior(double x) {
		return x/2;
	}


	@Override
	public void updateVar(double var) {
		// do nothing		
	}


	@Override
	public double getVar() {

		return 0;
	}

}
