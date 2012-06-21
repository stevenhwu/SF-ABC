package sw.main;

import sw.abc.parameter.Parameters;
import sw.abc.parameter.ParametersCollection;
import sw.math.RandomGenerator;

public class MH {

	
//	public MH() {
//		// TODO Auto-generated constructor stub
//	}

	public static boolean accept(double ratio) {
		return accept(ratio, 0);
	}

	
	public static boolean accept(double logP, double logQ) {
		
		final double ratio = logP+logQ;
		final double alpha = RandomGenerator.nextLogDouble();
		final boolean isAccept = (alpha < ratio);
//		System.out.println("p:"+logP+"\tq: "+logQ+"\tratio" + ratio +"\talpha: "+alpha +"\t"+ isAccept);
		return isAccept;
	}
	
	public static boolean accept(Parameters p){
		return MH.accept(p.getPriorRatio(), p.getLogQ()); 
	}
	
	
	public static boolean accept(ParametersCollection pc){
		double logP = 0;
		double logQ = 0;
		for (int i = 0; i < pc.getSize(); i++) {
			Parameters p = pc.getParameter(i);
			logP += p.getPriorRatio();
			logQ += p.getLogQ();
			
		}
		return MH.accept(logP, logQ); 
	}
}
