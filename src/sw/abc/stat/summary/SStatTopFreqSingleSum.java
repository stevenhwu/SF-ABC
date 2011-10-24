package sw.abc.stat.summary;

import java.util.Arrays;

public class SStatTopFreqSingleSum extends AbstractSummaryStat  {

	public SStatTopFreqSingleSum() {

	}



	@Override
	public double[] calStat(double[] par) {
//		double[] stat = new double[] { calStatMu(par), calStatTheta(par) };
		return null;
	}

	@Override
	public double[] calStat(double[]... par) {
		double s = calStatAll(par);
		double[] stat = new double[] { s, s };
		return stat;
	}

	public double calStatAll(double[]... par) {

		double stat = -3.58787765913165*1 -0.759149430496269*par[0][0] +8.34362428968078*par[0][1] +14.2846275758943*par[0][2] -0.167600331232477*par[1][0] -0.123646995341631*par[1][1] -13.5938071771353*par[2][0] -25.6175445481528*par[2][1] +0.0064119434459353*par[3][0] +0.00429406663327652*par[3][1] +0.00288061008777587*par[3][2] +0.00202925118535751*par[3][3] +0*par[3][4] +0.00142241674931514*par[3][5] +0.000784556640986913*par[3][6] +0.000544077902649135*par[3][7] +0*par[3][8] +0.00707801972000069*par[4][0] +0.00611651236439016*par[4][1] +0.00415535222465935*par[4][2] +0.00281032893517248*par[4][3] +0*par[4][4] +0.00185761535529762*par[4][5] +0.0011565199865664*par[4][6] +0.000670648165730552*par[4][7] +0*par[4][8];

		return stat;

	}



}