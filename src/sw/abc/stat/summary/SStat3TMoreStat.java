package sw.abc.stat.summary;

public class SStat3TMoreStat extends AbstractSummaryStat  {

	public SStat3TMoreStat() {

	}

	@Override
	public double calStatMu(double[]... par) {

		double stat = 
			+1.602e-02
			+5.112e-02*par[0][0]
			+4.757e-02*par[0][1]
			+3.250e-02*par[0][2]
			-4.257e-02*par[0][3]
			-4.583e-02*par[0][4]
			-4.767e-02*par[0][5]
			-3.746e-04*par[1][0]
			-1.587e-04*par[1][1]
			-4.345e-04*par[1][2]
			-1.600e+01*par[2][0]
			-1.158e+01*par[2][1]
			-7.818e+00*par[2][2]
			-4.643e-05*par[3][0]
			-4.548e-05*par[3][1]
			-6.767e-05*par[3][2]
			+2.137e-02*par[4][0]
			+1.543e-02*par[4][1]
			+1.041e-02*par[4][2]
			+5.604e-04*par[5][0]
			+6.566e-04*par[5][1]
			+9.455e-04*par[5][2];
		return stat;

	}

	@Override
	public double calStatTheta(double[]... par) {

		double stat = 
			7.15E+003
			-1.87E+004*par[0][0]
			-1.27E+004*par[0][1]
			-1.38E+004*par[0][2]
			+1.20E+004*par[0][3]
			+1.43E+004*par[0][4]
			+1.60E+004*par[0][5]
			-1.40E+002*par[1][0]
			-1.49E+002*par[1][1]
			-1.22E+002*par[1][2]
			+2.75E+006*par[2][0]
			+1.44E+006*par[2][1]
			+3.73E+006*par[2][2]
			-1.10E+001*par[3][0]
			-1.26E+001*par[3][1]
			-1.99E+001*par[3][2]
			-3.67E+003*par[4][0]
			-1.91E+003*par[4][1]
			-4.99E+003*par[4][2]
			+1.58E+002*par[5][0]
			+1.86E+002*par[5][1]
			+2.55E+002*par[5][2];

			return stat;
	}

}