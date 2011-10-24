package sw.abc.stat.summary;

public class SStatSmall implements SummaryStat {

	public SStatSmall() {
		
	}
	
	@Override
	public double calStat1P1R(int p, double[] par){
		double stat = 0 ;
		switch (p) {
		case 0:
			stat =  calStatMu(par);
			break;

		case 1:
			stat = calStatTheta(par);
			break;
		}
		
		return stat;
	}
	
	@Override
	public double[] calStat(double[] par) {
		double[] stat = new double[] {calStatMu(par), calStatTheta(par)};
		return stat;
	}


	@Override
	public double calStatMu(double[] par) {
//		 (Intercept)           V4           V5           V6 
//		 0.006146766  0.046811005 -0.001404373 -0.006030450 

		double stat = 0.006146766 + 0.046811005*par[0] -0.001404373*par[1] -0.006030450*par[2];

		return stat;
	}

	@Override
	public double calStatTheta(double[] par) {
//		(Intercept)          V4          V5          V6 
//		   2939.095  -17859.886   12130.678   14912.505 
		double stat = 2939.095 - 17859.886*par[0] + 12130.678*par[1] + 14912.505*par[2];

		return stat;
	}

	@Override
	public double[] calStat(double[]... par) {
		// TODO Auto-generated method stub
		return null;
	}


}
