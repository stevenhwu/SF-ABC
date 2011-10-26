package sw.abc.parameter;

import sw.math.DistributionPrior;

public class ParaMu extends AbstractParameter   {

//	Distribution prior;
//	Distribution proposal;
//	double value;
//	
	
//	public ParaMu() {
//	}

	public ParaMu(DistributionPrior d) {
		setPrior(d);
//		nextPrior();
	}
	

	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
//
////	@Override
//	public double nextPrior() {
//		value = prior.next();
//		return value;
//	}
//
//	@Override
//	public double nextProposal() {
//		value = proposal.next(value);
//		return value;
//	}
//
//	@Override
//	public void setProposal(Distribution d) {
//		proposal = d;
//		
//	}
//
//	@Override
//	public void setPrior(Distribution d) {
//		prior = d;
//		
//	}


//	public void setInitValue(double d) {
//		setValue(d);
//		
//	}

}
