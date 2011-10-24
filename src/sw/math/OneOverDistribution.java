package sw.math;

public class OneOverDistribution extends RandomGenerator implements DistributionPrior {

	double x;
	double logP;
	
	public OneOverDistribution(double init) {
		this.x = init;
	
	}

	@Override
	public double init() {
		return x;
	}


	@Override
	public double getLogPrior(double x) {
		return calLogPrior(x);
	}

	public static double calLogPrior(double x){
		return Math.log(1/x);
	}



}
