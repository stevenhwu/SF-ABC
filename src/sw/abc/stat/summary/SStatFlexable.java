package sw.abc.stat.summary;

public class SStatFlexable extends AbstractSummaryStat {

	
	public SStatFlexable(){
		
	}
	
	public void setParamSets(double[] parLengthSetting){
		
	}
	
	public void addCoefMu(double[] coef){
		
	}
	

	public void addCoefTheta(double[] coef){
		
	}

	public double calStatMu(double[]... par) {


		double stat = -20.8793987277876*1 +0.0954625438269027*par[0][0] -0.0673538247080562*par[0][1] -0.0265682764114079*par[0][2] +0.000512597342017*par[1][0] +0.000184980846948933*par[1][1] +0.072695502210599*par[2][0] -0.0369443992557843*par[2][1] +20.7378820919289*par[3][0] +20.8743931091084*par[3][1] +20.8938861632824*par[3][2] +20.887662733164*par[3][3] +21.7142210390494*par[3][4] +3.11784108348849e-05*par[4][0] -2.20837929558139e-05*par[4][1] -1.11692374730006e-05*par[4][2] -6.12601396670206e-06*par[4][3] +0*par[4][4] -3.336919205229e-06*par[4][5] -1.15787333446021e-06*par[4][6] +3.79592873565228e-07*par[4][7] +0*par[4][8] +0.000140208266368995*par[5][0] +1.30371915806176e-06*par[5][1] +2.25636062137299e-06*par[5][2] +2.33341032452833e-06*par[5][3] +0*par[5][4] +2.22884885562829e-06*par[5][5] +1.80831118477254e-06*par[5][6] +1.85191040738641e-06*par[5][7] +0*par[5][8];

		return stat;

	}
	
}
