package sw.abc.stat.summary;

import java.util.Arrays;

public class SStat3TMoreStatNoInt implements SummaryStat {

	public SStat3TMoreStatNoInt() {

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
//		System.out.println(Arrays.toString(stat));
		return stat;
	}

	public double calStatMu(double[]... par) {

		double statMu = 
			5.28E-002*par[0][0]
			                  +5.19E-002*par[0][1]
			                  +3.39E-002*par[0][2]
			                  -1.79E-002*par[0][3]
			                  -3.35E-002*par[0][4]
			                  -1.69E-002*par[0][5]
			                  -3.02E-004*par[1][0]
			                  -1.04E-004*par[1][1]
			                  -3.58E-004*par[1][2]
			                  -1.45E+001*par[2][0]
			                  -9.38E+000*par[2][1]
			                  -5.58E+000*par[2][2]
			                  -1.78E-004*par[3][0]
			                  -1.43E-004*par[3][1]
			                  -2.28E-004*par[3][2]
			                  +1.93E-002*par[4][0]
			                  +1.25E-002*par[4][1]
			                  +7.41E-003*par[4][2]
			                  +2.22E-003*par[5][0]
			                  +1.79E-003*par[5][1]
			                  +2.68E-003*par[5][2];

		return statMu;

	}

	public double calStatTheta(double[]... par) {

		double statTheta = 
			1.79E+004*par[0][0]
			                  -1.08E+004*par[0][1]
			                  -1.32E+004*par[0][2]
			                  +2.30E+004*par[0][3]
			                  +1.98E+004*par[0][4]
			                  +2.97E+004*par[0][5]
			                  -1.08E+002*par[1][0]
			                  -1.25E+002*par[1][1]
			                  -8.78E+001*par[1][2]
			                  +3.43E+006*par[2][0]
			                  +2.42E+006*par[2][1]
			                  +4.73E+006*par[2][2]
			                  -6.97E+001*par[3][0]
			                  -5.60E+001*par[3][1]
			                  -9.13E+001*par[3][2]
			                  -4.59E+003*par[4][0]
			                  -3.21E+003*par[4][1]
			                  -6.33E+003*par[4][2]
			                  +8.98E+002*par[5][0]
			                  +6.91E+002*par[5][1]
			                  +1.03E+003*par[5][2];


			return statTheta;
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