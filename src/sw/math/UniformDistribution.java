package sw.math;

public class UniformDistribution extends RandomGenerator implements DistributionPrior , DistributionProposal{

	double lower;
	double upper;
	
	double logPrior;
	
	public UniformDistribution(double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
		
		logPrior = Math.log(1/ (upper-lower));
	}

	
	public double next() {

		return r.nextUniform(lower, upper);
	}

	@Override
	public double next(double mean) {
		
		return next();
	}
	@Override
	public double init() {

		return next();
	}
	@Override
	public double getLogq() {
		
		return 1;
	}

	@Override
	public double getLogPrior(double x) {
		if (x>lower && x<upper){
			return logPrior;
		}
		else{
			return Double.NEGATIVE_INFINITY;
		}
	}

}
