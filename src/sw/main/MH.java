package sw.main;

import sw.abc.parameter.Parameters;
import sw.math.RandomGenerator;

public class MH {

	
	public MH() {
		// TODO Auto-generated constructor stub
	}

	public static boolean accept(double logq) {
		return accept(0, logq);
	}

	
	public static boolean accept(double logP, double logq) {
		
		double stat = logP+logq;
		double alpha = Math.log( RandomGenerator.nextUnif() );
		boolean isAccept = (alpha < stat);
//		System.out.println("p:"+logP+"\tq: "+logq+"\talpha: "+alpha);
		return isAccept;
	}
	
	public static boolean accept(Parameters p){
		return MH.accept(p.getPriorRatio(), p.getLogq()); 
	}
}
