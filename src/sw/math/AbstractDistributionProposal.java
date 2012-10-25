package sw.math;

public abstract class AbstractDistributionProposal extends RandomGenerator implements
		DistributionProposal {

	double logQ = 1;

//	public AbstractDistributionProposal() {
//		super();
//	}

	@Override
	public double getLogq() {
		return logQ;
	}

}