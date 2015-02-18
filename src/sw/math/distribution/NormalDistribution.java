package sw.math.distribution;


//import static sw.math.RandomGenerator.r;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import sw.math.RandomUtils;


public class NormalDistribution extends AbstractDistributionProposal implements DistributionPrior {

	static NormalDistributionImpl stdNorm = new NormalDistributionImpl(0,1);
	
	NormalDistributionImpl d = new NormalDistributionImpl();
	double logq  = 0;
	double mu;
	double sigma;

	public NormalDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		d = new NormalDistributionImpl(mu, sigma);
		
	}

	@Override
	public double nextPrior() {
		
		return RandomUtils.nextGaussian(mu, sigma);
	}
	

	
	@Override
	public double next(double mean) {
		
		double v = RandomUtils.nextGaussian(mean, sigma);
//		logq = pdf(v);
		return v;
	}

//	public void setPar(double mu, double sigma){
//		setMu(mu);
//		setSigma(sigma);
//	}
	
	public double getMu() {
		return mu;
	}

//	public void setMu(double mu) {
//		this.mu = mu;
//	}

	public double getSigma() {
		return sigma;
	}

//	public void setSigma(double sigma) {
//		this.sigma = sigma;
//	}
	
	public double pdf(double x){
		return d.density(x);
	}

	@Override
	public double getLogq() {
		return logq;
	}
	
	public double logPdf(double x){
		return Math.log(pdf(x));
	}

	@Override
	public double getLogPrior(double x) {
		return logPdf(x);
	}

	public static double quantile(double p) {

		double q = 0;
		try {
			q = stdNorm.inverseCumulativeProbability(p);
		} catch (MathException e) {
			e.printStackTrace();
		}
		return q;
	}

	@Override
	public void updateVar(double var) {
		this.sigma = Math.sqrt(var);
		d = new NormalDistributionImpl(mu, sigma);
		
	}

	@Override
	public double getVar() {
		return sigma;
	}

}
