package sw.math;

public class ZTestDistribution extends RandomGenerator implements DistributionPrior, DistributionProposal {

	double mean;
	
	public ZTestDistribution(double mean) {
		this.mean = mean;
	}


	@Override
	public double init() {

		return mean;
	}
	

	@Override
	public double next(double mean) {
		return mean+1;
	}

	@Override
	public double getLogq() {

		return 1;
	}

	@Override
	public double getLogPrior(double x) {
		// TODO Auto-generated method stub
		return 1;
	}

}