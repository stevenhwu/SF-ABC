package sw.abc.parameter;

import dr.inference.loggers.LogColumn;
import sw.math.DistributionPrior;

public class ParaTheta extends AbstractParameter  {

	
//	Distribution prior;
//	Distribution proposal;
//	double value;
//	
//	public ParaTheta() {
//	}

	public ParaTheta(DistributionPrior d) {
		setPrior(d);
//		nextPrior();
		
	}
	

	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LogColumn[] getColumns() {
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public double nextPrior() {
//
//	}
//
//	@Override
//	public double nextProposal() {
//		value = proposal.next(value);
//		return value;
//	}

//	@Override
//	public void setPrior(Distribution d) {
//		prior = d;
//		
//	}
//	@Override
//	public void setProposal(Distribution d) {
//		// TODO Auto-generated method stub
//		
//	}



}
