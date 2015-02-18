package sw.math.distribution;

import sw.math.RandomUtils;

public abstract class AbstractDistributionProposal extends RandomUtils implements
		DistributionProposal {

	protected double logQ = 1;

//	public AbstractDistributionProposal() {
//		super();
//	}

	@Override
	public double getLogq() {
		return logQ;
	}

}