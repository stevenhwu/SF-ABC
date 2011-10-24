package sw.abc.stat.summary;

public class SStatBig extends AbstractSummaryStat{
	
	public SStatBig() {
		
	}
	

	@Override
	public double calStatMu(double[] par) {

//		(Intercept)          V4          V5          V6 
//		 0.01209530  0.43650600 -0.14985073 -0.21416128 

		double stat =  0.01209530+0.43650600*par[0]-0.14985073*par[1]-0.21416128*par[2];
		return stat;
		
	}

	@Override
	public double calStatTheta(double[] par) {

//		   (Intercept)          V4          V5          V6 
//		   2734.097  -51410.492   28512.817   36311.069 

		   
		double stat = 2734.097-51410.492*par[0]+28512.817*par[1]+36311.069*par[2];
		return stat;
	}





	
}