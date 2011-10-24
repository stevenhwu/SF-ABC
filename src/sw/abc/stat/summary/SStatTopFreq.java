package sw.abc.stat.summary;

import java.util.Arrays;

public class SStatTopFreq implements SummaryStat {

	public SStatTopFreq() {

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

		double stat = -0.0095325773832802*1 +0.0799648402807868*par[0][0] -0.0140828346587675*par[0][1] +0.00774828465079777*par[0][2] -0.000484239916615943*par[1][0] -0.00053500247505519*par[1][1] -0.0321110374543285*par[2][0] -0.109696523537348*par[2][1] +1.68555177140178e-05*par[3][0] +1.2058985442447e-05*par[3][1] +9.34587376975748e-06*par[3][2] +6.85727729479673e-06*par[3][3] +0*par[3][4] +4.86311338611673e-06*par[3][5] +3.00807660472827e-06*par[3][6] +2.80362784246574e-06*par[3][7] +0*par[3][8] +2.84952546299423e-05*par[4][0] +2.50166461180304e-05*par[4][1] +1.73160780927971e-05*par[4][2] +1.17725440947098e-05*par[4][3] +0*par[4][4] +7.30147775174147e-06*par[4][5] +5.1409390940556e-06*par[4][6] +2.24186812958168e-06*par[4][7] +0*par[4][8];

		return stat;

	}

	public double calStatTheta(double[]... par) {
		double stat =  -7702.50515101745*1 -27841.4652290276*par[0][0] +29502.5552964595*par[0][1] +40323.5413344053*par[0][2] -347.354921771253*par[1][0] -199.42792856786*par[1][1] -30464.2397172182*par[2][0] -41684.7357531681*par[2][1] +13.8228876777404*par[3][0] +9.01069302317368*par[3][1] +5.64302126938273*par[3][2] +3.88777302887793*par[3][3] +0*par[3][4] +2.70710777675008*par[3][5] +1.38899809901403*par[3][6] +0.733821933563627*par[3][7] +0*par[3][8] +12.0971186139628*par[4][0] +10.3283663382562*par[4][1] +6.91423567157567*par[4][2] +4.65656917314844*par[4][3] +0*par[4][4] +3.2314801092004*par[4][5] +1.82157620861691*par[4][6] +1.2926721461254*par[4][7] +0*par[4][8];		
			
		
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