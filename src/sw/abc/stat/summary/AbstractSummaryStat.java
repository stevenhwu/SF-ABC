package sw.abc.stat.summary;

public abstract class AbstractSummaryStat implements SummaryStat{

	
	@Override
	public double calStat1P1R(int p, double[] par){
		double stat = 0 ;
		switch (p) {
		case 0:
			stat =  calStatMu(par);
			break;

		case 1:
			stat = calStatPop(par);
			break;
		}
		
		return stat;
	}
	
	@Override
	public double[] calStat(double[] par) {
		double[] stat = new double[] { calStatMu(par), calStatPop(par) };
		return stat;
	}
	@Override
	public double calStatMu(double[] par) {
		return Double.MAX_VALUE;
	}

	@Override
	public double calStatPop(double[] par) {
		return Double.MAX_VALUE;
	}

	@Override
	public double[] calStat(double[]... par) {
		double[] stat = new double[] { calStatMu(par), calStatPop(par) };
		return stat;
	}	

	@Override
	public double calStatMu(double[]... par) {
		return Double.MAX_VALUE;
	}

	@Override
	public double calStatPop(double[]... par) {
		return Double.MAX_VALUE;
	}
}
