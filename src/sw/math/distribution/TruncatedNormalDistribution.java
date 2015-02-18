package sw.math.distribution;

import sw.math.RandomUtils;


public class TruncatedNormalDistribution extends AbstractDistributionProposal implements DistributionPrior {

	private NormalDistribution nd;


	private double m;
	private double sd;
	private double lower;
	private double upper;
	private double T;
	
	public TruncatedNormalDistribution(double mean, double sd, double lower, double upper) {
	
		if (lower == upper)
			upper += 1.E-4;
	
		if (sd == 0.)
			sd = 1.E-5;
	
		this.m = mean;
		this.sd = sd;
		this.lower = lower;
		this.upper = upper;
	
		this.T = calcT(this.m, this.sd, this.lower, this.upper);
		this.nd = new NormalDistribution(this.m, this.sd);
	}

	private static double calcT(double mean, double sd, double lower, double upper) {
		double T = standardNormalCdf((upper - mean) / sd)
				- standardNormalCdf((lower - mean) / sd);
		return T;
	}

	public double pdf(double x) {
		if (x > upper || x < lower)
			return 0.0;
		else
			return (standardNormalPdf((x - m) / sd) / sd) / T;
	}

	public double logPdf(double x) {
		return Math.log(pdf(x));
	}

	public double cdf(double x) {
		double cdf;
		if (x < lower)
			cdf = 0.;
		else if (x >= lower && x < upper)
			cdf = (standardNormalCdf((x - m) / sd) - standardNormalCdf((lower - m)
					/ sd))
					/ T;
		else
			cdf = 1.0;

		return cdf;
	}

	public double quantile(double y) {

		if (y == 0)
			return lower;

		if (y == 1.0)
			return upper;

		return quantileSearch(y, lower, upper, 20);
	}

	/* Implements a geometic search for the quantiles */
	private double quantileSearch(double y, double l, double u, int step) {
		double q, a;

		q = (u + l) / 2.0;

		if (step == 0 || q == l || q == u)
			return q;

		a = cdf(q);

		if (y <= a)
			return quantileSearch(y, l, q, step - 1);
		else
			return quantileSearch(y, q, u, step - 1);
	}


	/**
	 * probability density function of the standard normal distribution
	 * 
	 * @param x
	 *            argument
	 * @return pdf at x
	 */
	public static double standardNormalPdf(double x) {
		double a = 1.0 / (Math.sqrt(2.0 * Math.PI));
		double b = -(x) * (x) / (2.0);

		return a * Math.exp(b);
	}

	public static double logStandardNormalPdf(double x, double mean, double sd, double lower, double upper){
		double T = calcT(mean, sd, lower, upper);
		double stdX = (((x - mean) / sd) / sd);
		
		return Math.log(standardNormalPdf(stdX) / T);
	}
	


	public double logStandardNormalPdf(double x, double mean){
		return logStandardNormalPdf(x, mean, sd, lower, upper);
	}
	/**
	 * the natural log of the probability density function of the standard
	 * normal distribution
	 * 
	 * @param x
	 *            argument
	 * @return log pdf at x
	 */
	public static double logStandardNormalPdf(double x) {
		/* Can throw an expcetion if x not in the range [lower,upper] */
		return Math.log(standardNormalPdf(x));
	}

	/**
	 * cumulative density function of the standard normal distribution
	 * 
	 * @param x
	 *            argument
	 * @return cdf at x
	 */
	public static double standardNormalCdf(double x) {
		double a = (x) / (Math.sqrt(2.0));

		return 0.5 * (1.0 + ErrorFunction.erf(a));
	}


	
	@Override
	public double getLogPrior(double x) {
		return logPdf(x);
	}

	@Override
	public double nextPrior() {
		double newX;
		do {
			newX = nd.nextPrior();
		} while (newX < lower || newX > upper);
		return newX;
	}

	
	@Override
	public double next(double current) {

		double newValue = Double.NaN;
		
		try {

			do {
				newValue = RandomUtils.nextGaussian(current, sd);
			} while (newValue > upper || newValue < lower);

			double limit = Math.log(calcT(current, sd, lower, upper));
			double newGivenCur = logStandardNormalPdf(newValue, current) - limit;

			limit = Math.log(calcT(newValue, sd, lower, upper));
			double curGivenNew = logStandardNormalPdf(current, newValue) - limit;
			logQ =  curGivenNew - newGivenCur;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newValue;
	}


	@Override
	public void updateVar(double var) {
		sd = Math.sqrt(var);
		
	}

	@Override
	public double getVar() {
		return sd*sd;
	}

	public double getMean() {
		return m;
	}

	public void setMdean(double m) {
		this.m = m;
	}

	public double getSD() {
		return sd;
	}

	public void setSD(double sd) {
		this.sd = sd;
	}

	public double getLower() {
		return lower;
	}

	public double getUpper() {
		return upper;
	}


}
