package sw.abc.stat.summary;

public interface SummaryStat {

	double calStat1P1R(int p, double[] par);

	double[] calStat(double[] par);

	double calStatMu(double[] par);
	
	double calStatTheta(double[] par);
	
	
	
	double[] calStat(double[]... par);

	double calStatMu(double[]... par);

	double calStatTheta(double[]... par);

	
}
