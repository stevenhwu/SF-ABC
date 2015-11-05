package sw.abc.parameter;

import sw.math.distribution.DistributionPrior;

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
		double logQ = allPar.getParameter(MU).getLogQ()
				+ allPar.getParameter(POP).getLogQ();
		return logQ;
	}
	
	private static double calculateTheta(ParametersCollection allPar) {
		double newTheta = allPar.getParameter(MU).getNewValue()
				* allPar.getParameter(POP).getNewValue();
		return newTheta;
	}

	@Override
	public String getName() {
		return null;
	}
}
