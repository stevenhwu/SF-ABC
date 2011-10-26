package sw.abc.stat.summary;

public class SStatTopFreqSingleProductNoS extends AbstractSummaryStat  {

	public SStatTopFreqSingleProductNoS() {

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

		double stat = -60.2406901307684*1 -56.2983423854971*par[0][0] +208.100571475311*par[0][1] +296.487645136166*par[0][2] -2.72696585470203*par[1][0] -1.5685386535731*par[1][1] -292.475321153267*par[2][0] -503.569673859628*par[2][1] +0.0909642900044817*par[3][0] +0.121627988290387*par[3][1] +0.075913844030328*par[3][2] +0.0526062633800139*par[3][3] +0*par[3][4] +0.0347346258464087*par[3][5] +0.0211210984939704*par[3][6] +0.0158271935257566*par[3][7] +0*par[3][8] +0.0982175634618126*par[4][0] +0.157777557185642*par[4][1] +0.101035707125557*par[4][2] +0.0653896721246452*par[4][3] +0*par[4][4] +0.041342642307025*par[4][5] +0.0264291561137645*par[4][6] +0.0168079662918806*par[4][7] +0*par[4][8];
		
		return stat;

	}



}