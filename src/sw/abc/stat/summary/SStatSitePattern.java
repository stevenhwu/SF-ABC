package sw.abc.stat.summary;

import java.util.Arrays;

public class SStatSitePattern implements SummaryStat {

	public SStatSitePattern() {

	}

	@Override
	public double calStat1P1R(int p, double[] par) {
		double stat = 0;
		switch (p) {
		case 0:
			stat = calStatMu(par);
			break;

		case 1:
			stat = calStatTheta(par);
			break;
		}
		return stat;
	}

	@Override
	public double[] calStat(double[] par) {
		double[] stat = new double[] { calStatMu(par), calStatTheta(par) };
		return stat;
	}

	@Override
	public double[] calStat(double[]... par) {
		double[] stat = new double[] { calStatMu(par), calStatTheta(par) };
		return stat;
	}

	public double calStatMu(double[]... par) {


		double stat = -20.8793987277876*1 +0.0954625438269027*par[0][0] -0.0673538247080562*par[0][1] -0.0265682764114079*par[0][2] +0.000512597342017*par[1][0] +0.000184980846948933*par[1][1] +0.072695502210599*par[2][0] -0.0369443992557843*par[2][1] +20.7378820919289*par[3][0] +20.8743931091084*par[3][1] +20.8938861632824*par[3][2] +20.887662733164*par[3][3] +21.7142210390494*par[3][4] +3.11784108348849e-05*par[4][0] -2.20837929558139e-05*par[4][1] -1.11692374730006e-05*par[4][2] -6.12601396670206e-06*par[4][3] +0*par[4][4] -3.336919205229e-06*par[4][5] -1.15787333446021e-06*par[4][6] +3.79592873565228e-07*par[4][7] +0*par[4][8] +0.000140208266368995*par[5][0] +1.30371915806176e-06*par[5][1] +2.25636062137299e-06*par[5][2] +2.33341032452833e-06*par[5][3] +0*par[5][4] +2.22884885562829e-06*par[5][5] +1.80831118477254e-06*par[5][6] +1.85191040738641e-06*par[5][7] +0*par[5][8];

		return stat;

	}

	public double calStatTheta(double[]... par) {
		 
		double stat = 7106187.14676177*1 -28824.2486488602*par[0][0] +26778.6600860624*par[0][1] +28274.1484266296*par[0][2] -296.946100009669*par[1][0] -155.67140433418*par[1][1] -31487.5810867864*par[2][0] -28454.1288854737*par[2][1] -7093681.91783574*par[3][0] -7107911.47652539*par[3][1] -7108312.67079564*par[3][2] -7106552.83788278*par[3][3] -7110942.86354525*par[3][4] +5.39655978062944*par[4][0] +10.0694553936354*par[4][1] +5.96204034481586*par[4][2] +3.78716602774189*par[4][3] +0*par[4][4] +2.59816240979537*par[4][5] +1.29529551520393*par[4][6] +0.570615070971672*par[4][7] +0*par[4][8] -7.8357221093757*par[5][0] +8.84976242783021*par[5][1] +5.50561942195379*par[5][2] +3.59101130449997*par[5][3] +0*par[5][4] +2.3258092064723*par[5][5] +1.3092586864502*par[5][6] +0.652619713586941*par[5][7] +0*par[5][8];

		return stat;
	}

	@Override
	public double calStatMu(double[] par) {

		return Double.MAX_VALUE;

	}

	@Override
	public double calStatTheta(double[] par) {

		return Double.MAX_VALUE;
	}

}