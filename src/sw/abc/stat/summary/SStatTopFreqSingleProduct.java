package sw.abc.stat.summary;

public class SStatTopFreqSingleProduct extends AbstractSummaryStat  {

	public SStatTopFreqSingleProduct() {

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

		double stat = -2.14536154957421*1 -2.00496207457802*par[0][0] +7.41111968535579*par[0][1] +10.5588629947323*par[0][2] -0.0971158809598577*par[1][0] -0.0558606235933177*par[1][1] -10.4159714445421*par[2][0] -17.9337091504906*par[2][1] +0.0032395261365085*par[3][0] +0.00433155743839965*par[3][1] +0.00270353214263486*par[3][2] +0.00187347546114202*par[3][3] +0*par[3][4] +0.00123700991087536*par[3][5] +0.000752189134874977*par[3][6] +0.00056365643146053*par[3][7] +0*par[3][8] +0.00349783815036685*par[4][0] +0.00561895794747757*par[4][1] +0.00359820116155175*par[4][2] +0.00232873308740241*par[4][3] +0*par[4][4] +0.00147234228178255*par[4][5] +0.000941225858984704*par[4][6] +0.00059858485237907*par[4][7] +0*par[4][8];

		return stat;

	}



}