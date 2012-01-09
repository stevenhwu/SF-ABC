package sw.abc.parameter;

import org.apache.commons.math.random.RandomDataImpl;

@Deprecated
public class AllParameters {

	double mu;
	double theta;
//	NormalDistributionImpl norm = new NormalDistributionImpl();
//	UniformDistribution unif = new UniformDistribution(2500, 3500);
	static RandomDataImpl r = new RandomDataImpl();
	double muLower;
	double muUpper;
	int thetaLower;
	int thetaUpper;
	
	public AllParameters() {
		update();
		
	}
	
	public void update(){
		theta = nextTheta();
		mu = nextMu();
	}
	
	public void setMuPrior(double lower, double upper){
		muLower = lower;
		muUpper = upper;
	}
	private double nextMu() {

		return r.nextUniform(muLower, muUpper);
	}
	
	public void setThetaPrior(int lower, int upper){
		thetaLower = lower;
		thetaUpper = upper;
	}

	private double nextTheta(){
		return r.nextInt(thetaLower, thetaUpper);
		
	}
	@Override
	public String toString() {
		return "Mu: "+mu+" Theta: "+theta;
	}
	public double getTheta() {
		return theta;
	}

	public double getMu() {
		return mu;
	}
}
