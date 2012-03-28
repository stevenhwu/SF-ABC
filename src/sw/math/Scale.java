package sw.math;



// should be Operator class NOT Distribution
public class Scale implements  DistributionProposal {

	private double scaleFactor;
	double logq;
	
	public Scale(double s){
		scaleFactor = s;
	}
	


	@Override
	public double next(double mean) {
	    
	    double scale = (scaleFactor + (RandomGenerator.nextDouble() * ((1.0 / scaleFactor) - scaleFactor)));
	    logq = -Math.log(scale);
	    double newValue = scale * mean;

		return newValue;
	}
	
	@Override
	public double getLogq() {
		return logq;
	}



	@Override
	public void updateVar(double sFactor) {
		
		this.scaleFactor = sFactor;
	}


}
