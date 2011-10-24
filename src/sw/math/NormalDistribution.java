package sw.math;

import org.apache.commons.math.distribution.NormalDistributionImpl;

public class NormalDistribution extends RandomGenerator implements DistributionPrior, DistributionProposal {

	NormalDistributionImpl d = new NormalDistributionImpl();
	double logq  = 1;
	double mu;
	double sigma;

	public NormalDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		d = new NormalDistributionImpl(mu, sigma);
	}

	@Override
	public double init() {
		
		return r.nextGaussian(mu, sigma);
	}
	

	
	@Override
	public double next(double mean) {
		
		double v = r.nextGaussian(mean, sigma);
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
		// TODO Auto-generated method stub
//		logq=1;
		return logq;
	}
	
	 double pdf2(double x){
		 
		return 0;
	}

	@Override
	public double getLogPrior(double x) {
		return pdf(x);
	}

}
