package sw.math.distribution;

import sw.math.RandomUtils;



public class OneOverDistribution extends RandomUtils implements DistributionPrior {

	double x;
	
	
	public OneOverDistribution(double init) {
		this.x = init;
	
	}

	@Override
	public double nextPrior() {
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
