package sw.abc.parameter;

import sw.math.distribution.DistributionPrior;


public class ParaMu extends AbstractParameter   {

	public ParaMu(DistributionPrior d) {
		setPrior(d);

	}

	@Override
	public String getName() {
		return MU;
	}
	


	

}
