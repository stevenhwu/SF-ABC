package sw.abc.stat.summary;

public abstract class AbstractSummaryStat implements SummaryStat{

	
	public double calStat1P1R(int p, double[] par){
		double stat = 0 ;
		switch (p) {
		case 0:
			stat =  calStatMu(par);
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
	public double calStatMu(double[] par) {
		return Double.MAX_VALUE;
	}

	@Override
	public double calStatTheta(double[] par) {
		return Double.MAX_VALUE;
	}

	@Override
	public double[] calStat(double[]... par) {
		double[] stat = new double[] { calStatMu(par), calStatTheta(par) };
		return stat;
	}	

	@Override
	public double calStatMu(double[]... par) {
		return Double.MAX_VALUE;
	}

	@Override
	public double calStatTheta(double[]... par) {
		return Double.MAX_VALUE;
	}
}
