package sw.math;


public class UniformDistribution extends AbstractDistributionProposal implements DistributionPrior{

	private double lower;
	private double upper;
	
	private double logPrior;
	
	public UniformDistribution(double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
		
		logPrior = Math.log(1/ (upper-lower));
	}

	
	public double nextPrior() {

		return r.nextUniform(lower, upper);
	}

	@Override
	public double next(double mean) {
		
		return nextPrior();
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


	@Override
	public void updateVar(double var) {
		// do nothing
	}


	@Override
	public double getVar() {
		return 0;
	}

}
