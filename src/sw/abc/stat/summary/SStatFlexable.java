package sw.abc.stat.summary;

public class SStatFlexable extends AbstractSummaryStat {

	double[] coefMu;
	double[] coefTheta;
	
	public SStatFlexable(){
		
	}
	
	public void setParamSets(double[] parLengthSetting){
		
	}
	
	public void addCoefMu(double[] coef){
		coefMu = coef;
	}
	

	public void addCoefTheta(double[] coef){
		
	}

	public double calStatMu(double[] par) {

		
		double stat = coefMu[0];
		for (int i = 0; i < par.length; i++) {
			stat += coefMu[i+1] * par[0];
		}


		return stat;

	}
	
}
