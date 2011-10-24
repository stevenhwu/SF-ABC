package sw.abc.stat.summary;

public class SStatFreq extends AbstractSummaryStat  {

	public SStatFreq() {

	}

	public double calStatMu(double[]... par) {

		// mu<- lm(Mu~.-Theta,data=dataAll[,1:7])
		// mu[[1]]
		// > mu[[1]]
		// (Intercept) inter01 intra0 intra1 freq1
		// 0.0189237608 0.0747913874 -0.0380416399 -0.0591645041 -0.0003002271
		// freq2
		// -0.0004424895
		double stat = 0.0189237608 + 0.0747913874 * par[0][0] - 0.0380416399
				* par[0][1] - 0.0591645041 * par[0][2] - 0.0003002271
				* par[1][0] - 0.0004424895 * par[1][1];

		return stat;

	}

	public double calStatTheta(double[]... par) {

		// th<- lm(Theta~.-Mu,data=dataAll[,1:7])
		// th[[1]]
		// (Intercept) inter01 intra0 intra1 freq1 freq2
		// 7651.1226 -27995.7271 8302.1544 11106.5307 -183.4747 -154.6406
		//
		double stat = 7651.1226 - 27995.7271 * par[0][0] + 8302.1544
				* par[0][1] + 11106.5307 * par[0][2] - 183.4747 * par[1][0]
				- 154.6406 * par[1][1];
		return stat;
	}

	@Override
	public double calStatMu(double[] par) {

		// mu<- lm(Mu~.-Theta,data=dataAll[,1:7])
		// mu[[1]]
		// > mu[[1]]
		// (Intercept) inter01 intra0 intra1 freq1
		// 0.0189237608 0.0747913874 -0.0380416399 -0.0591645041 -0.0003002271
		// freq2
		// -0.0004424895
		double stat = 0.0189237608 + 0.0747913874 * par[0] - 0.0380416399
				* par[1] - 0.0591645041 * par[2] - 0.0003002271 * par[3]
				- 0.0004424895 * par[4];

		return stat;

	}

	@Override
	public double calStatTheta(double[] par) {

		// th<- lm(Theta~.-Mu,data=dataAll[,1:7])
		// th[[1]]
		// (Intercept) inter01 intra0 intra1 freq1 freq2
		// 7651.1226 -27995.7271 8302.1544 11106.5307 -183.4747 -154.6406
		//
		double stat = 7651.1226 - 27995.7271 * par[0] + 8302.1544 * par[1]
				+ 11106.5307 * par[2] - 183.4747 * par[3] - 154.6406 * par[4];
		return stat;
	}

}