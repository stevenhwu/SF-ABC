package sw.abc.parameter;

import sw.math.DistributionPrior;

public class ParaTheta extends AbstractParameter {

	public ParaTheta(DistributionPrior d) {
		setPrior(d);

	}

	public void setInitPriorRatio(ParametersCollection allPar) {
		double newTheta = calculateTheta(allPar);
		
		logP = priorDist.getLogPrior(newTheta);
	}

	public double getPriorRatio(double newValue) {
		newLogP = priorDist.getLogPrior(newValue);
//		System.out.println(newLogP +"\t"+ logP);
		return (newLogP - logP);
	}

	public double getPriorRatio(ParametersCollection allPar) {
		double newTheta = calculateTheta(allPar);
		return getPriorRatio(newTheta);
	}

	public double getProposalRatio(ParametersCollection allPar) {
		double logQ = allPar.getParameter("mu").getLogQ()
				+ allPar.getParameter("popsize").getLogQ();
		return logQ;
	}
	
	private static double calculateTheta(ParametersCollection allPar) {
		double newTheta = allPar.getParameter("mu").getNewValue()
				* allPar.getParameter("popsize").getNewValue();
		return newTheta;
	}
}
