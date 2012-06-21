package sw.math;




// should be Operator class NOT Distribution
public class Scale extends AbstractDistributionProposal {

	protected double scaleFactor;
	public Scale(double s){
		scaleFactor = s;
	}
	


	@Override
	public double next(double mean) {
	    
	    final double scale = (scaleFactor + (RandomGenerator.nextDouble() * ((1.0 / scaleFactor) - scaleFactor)) );
	    logQ = -Math.log(scale);
	    final double newValue = scale * mean;

		return newValue;
	}
	
	@Override
	public void updateVar(double sFactor) {
		
		scaleFactor = sFactor;
	}



	@Override
	public double getVar() {
		return scaleFactor;
	}


}
