package sw.abc.stat.summary;

import sw.abc.parameter.Parameters;

public abstract class AbstractSummaryStat implements SummaryStat{

	protected static final String MU = Parameters.MU;
	protected static final String POP = Parameters.POP;

	
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
	
	protected double calStatMu(double[] par) {
		return Double.MAX_VALUE;
	}

	
	protected double calStatPop(double[] par) {
		return Double.MAX_VALUE;
	}

	@Override
	public double[] calStat(double[]... par) {
		double[] stat = new double[] { calStatMu(par), calStatPop(par) };
		return stat;
	}	

	
	protected double calStatMu(double[]... par) {
		return Double.MAX_VALUE;
	}

	
	protected double calStatPop(double[]... par) {
		return Double.MAX_VALUE;
	}
}
