package sw.math.distribution;

import sw.math.RandomUtils;





// should be Operator class NOT Distribution
public class TruncatedScale extends Scale {

	private double lower;
	private double upper;
	
	public TruncatedScale(double s, double lower, double upper){
		super(s);
		this.lower = lower;
		this.upper = upper;
	}
	


	@Override
	public double next(double mean) {
	    
		double scale;
		double newValue = mean;
		do{
		    scale = (scaleFactor + (RandomUtils.nextDouble() * ((1.0 / scaleFactor) - scaleFactor)) );
		    logQ = -Math.log(scale);
		    newValue = scale * mean;
		} while  (newValue > upper || newValue < lower);
		return newValue;
	}
	

}
