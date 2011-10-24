package sw.abc.stat.summary;

public class SStatBrLn implements SummaryStat {

	public SStatBrLn() {

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

		double stat = 
			4.26E-002
			+1.64E-003*par[0][0]
			-1.29E-002*par[0][1]
			-1.28E-002*par[0][2]
			-7.14E-004*par[1][0]
			-8.87E-004*par[1][1]
			-6.63E-003*par[2][0]
			-4.68E-002*par[2][1]
			-1.97E-006*par[3][1]
			+1.37E-006*par[3][2]
			-1.10E-006*par[3][3]
			-1.83E-006*par[3][4]
			+7.37E-007*par[3][5]
			+7.47E-007*par[3][6]
			-2.83E-006*par[3][7]
			+2.01E-006*par[3][8]
			-1.59E-006*par[3][9]
			-6.09E-007*par[3][10]
			-3.24E-007*par[3][11]
			+2.65E-007*par[3][12]
			-5.04E-007*par[3][13]
			-4.76E-007*par[3][14]
			-2.27E-007*par[3][15]
			+4.60E-007*par[3][16]
			-1.54E-006*par[3][17]
			+9.60E-008*par[3][18]
			-6.31E-007*par[3][19]
			+1.72E-007*par[3][20]
			-5.77E-007*par[3][21]
			-2.69E-007*par[3][22]
			-1.76E-007*par[3][23]
			+5.13E-007*par[3][24]
			-3.45E-007*par[3][25]
			-5.60E-007*par[3][26]
			-5.54E-007*par[3][27]
			-7.80E-007*par[3][28]
			-1.32E-006*par[3][29]
			-1.72E-006*par[3][30]
			-1.41E-006*par[3][31]
			-1.55E-006*par[3][32]
			-2.19E-006*par[3][33]
			-7.52E-007*par[3][34]
			-1.51E-006*par[3][35]
			-5.68E-007*par[3][36]
			-6.15E-007*par[3][37]
			+2.26E-006*par[3][38]
			+2.31E-006*par[3][39]
			+2.38E-006*par[3][40]
			-1.57E-006*par[3][41]
			-1.60E-007*par[3][42]
			-1.79E-007*par[3][43]
			-1.40E-006*par[3][44]
			+6.11E-007*par[3][45]
			-3.46E-007*par[3][46]
			-7.13E-007*par[3][47]
			-6.48E-007*par[3][48]
			+3.31E-007*par[3][49]
			+1.64E-007*par[3][50]
			-1.38E-006*par[3][51]
			+3.90E-007*par[3][52]
			-3.93E-007*par[3][53]
			-6.82E-007*par[3][54]
			+1.12E-007*par[3][55]
			-2.47E-007*par[3][56]
			+8.42E-007*par[3][57]
			-1.24E-006*par[3][58]
			-6.47E-007*par[3][59]
			-8.13E-007*par[3][60]
			-9.46E-008*par[3][61]
			-1.57E-007*par[3][62]
			-3.93E-007*par[3][63]
			+2.23E-007*par[3][64]
			-5.55E-007*par[3][65]
			-2.18E-007*par[3][66]
			-2.26E-007*par[3][67]
			-2.90E-007*par[3][68]
			-2.30E-007*par[3][69]
			-5.05E-007*par[3][70]
			-8.56E-008*par[3][71]
			-3.13E-007*par[3][72]
			-3.09E-007*par[3][73]
			-2.90E-007*par[3][74]
			-2.56E-007*par[3][75]
			-2.54E-007*par[3][76]
			-2.89E-007*par[3][77]
			-5.22E-007*par[3][78];
			
//		System.out.println(stat);
		return stat;

	}

	public double calStatTheta(double[]... par) {

		double stat = 
			-3.36E+003
			+3.58E+002*par[0][0]
			-2.37E+002*par[0][1]
			-4.50E+002*par[0][2]
			+1.00E+001*par[1][0]
			-1.49E+001*par[1][1]
			+8.12E+002*par[2][0]
			+1.54E+003*par[2][1]
			+7.16E-001*par[3][1]
			+3.05E-001*par[3][2]
			+9.58E-001*par[3][3]
			+6.58E-001*par[3][4]
			+2.00E-001*par[3][5]
			+6.48E-001*par[3][6]
			+1.12E+000*par[3][7]
			-1.38E-001*par[3][8]
			+3.39E-001*par[3][9]
			+5.61E-001*par[3][10]
			+6.74E-001*par[3][11]
			+1.80E-001*par[3][12]
			+4.24E-001*par[3][13]
			+2.77E-001*par[3][14]
			+5.78E-002*par[3][15]
			+5.58E-001*par[3][16]
			+1.21E-001*par[3][17]
			+1.75E-001*par[3][18]
			+4.50E-001*par[3][19]
			+8.18E-002*par[3][20]
			+1.88E-001*par[3][21]
			+1.34E-001*par[3][22]
			+3.83E-002*par[3][23]
			+1.02E-001*par[3][24]
			+2.52E-001*par[3][25]
			+2.87E-001*par[3][26]
			+5.06E-001*par[3][27]
			+4.48E-001*par[3][28]
			+5.89E-001*par[3][29]
			+3.44E-001*par[3][30]
			+3.83E-001*par[3][31]
			+1.41E-001*par[3][32]
			+9.73E-002*par[3][33]
			+2.56E-002*par[3][34]
			-6.86E-002*par[3][35]
			-9.34E-002*par[3][36]
			+9.97E-002*par[3][37]
			-1.11E-001*par[3][38]
			+1.07E+000*par[3][39]
			-1.82E-001*par[3][40]
			+5.98E-001*par[3][41]
			+3.59E-001*par[3][42]
			+7.53E-001*par[3][43]
			+2.29E-001*par[3][44]
			+1.70E-001*par[3][45]
			+9.52E-001*par[3][46]
			+3.34E-001*par[3][47]
			+3.30E-001*par[3][48]
			-1.38E-001*par[3][49]
			+5.44E-001*par[3][50]
			+3.63E-001*par[3][51]
			+2.51E-001*par[3][52]
			+1.95E-001*par[3][53]
			+3.98E-001*par[3][54]
			+1.83E-001*par[3][55]
			+2.58E-001*par[3][56]
			+3.21E-001*par[3][57]
			+8.49E-002*par[3][58]
			+4.89E-001*par[3][59]
			+5.23E-002*par[3][60]
			+1.04E-001*par[3][61]
			+2.16E-001*par[3][62]
			+1.56E-001*par[3][63]
			+2.08E-001*par[3][64]
			+1.37E-001*par[3][65]
			+6.34E-002*par[3][66]
			+1.59E-001*par[3][67]
			+1.83E-001*par[3][68]
			+6.48E-002*par[3][69]
			+1.34E-001*par[3][70]
			+6.53E-002*par[3][71]
			+9.19E-002*par[3][72]
			+6.13E-002*par[3][73]
			+6.22E-002*par[3][74]
			+6.40E-002*par[3][75]
			+3.12E-002*par[3][76]
			+2.85E-002*par[3][77]
			+2.61E-002*par[3][78];
		//TEMP
//		System.out.println(stat);
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