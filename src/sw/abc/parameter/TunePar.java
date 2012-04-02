package sw.abc.parameter;


import java.util.Arrays;

import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.stat.StatUtils;

import sw.math.NormalDistribution;

//import bmde.core.Setting;
//import bmde.math.Constant;
//import bmde.math.NormalDistribution;

// TUNESIZE = average of tuneSize iterations
// TuneGroup = over tuenGroup
//		overall, averaging over TuneSize*tuneGroup iterations
// tuneStepSize = maximum step size of tuning, nextUniform(0,tuneStepSize);

public class TunePar {
	

	final public static double OPTIMRATE = 0.1; // Use different value for ABC-MCMC 0.234;
	final public static double ACCTOL=0.05;
	final public static int TUNESIZE = 500 ;
	final public static int TUNEGROUP = 6;
	final public static int TUNEGROUP1 = TUNEGROUP - 1;
	final public static double TUNESTEPSIZE = 0.05; //used for scale tuning
//	final public static double TUNEINITSIZE = 2.38;
	
	
	private double[] tunePar;
	private double[][] accept;
	private int[] tuneType;

	private final double INV_OPT_ACC = NormalDistribution.quantile(OPTIMRATE / 2);
	// private int count=0;

	private int tuneSize;
	private int noTunePar;
	private int tuneGroup;
	private int tuneGroup1;
	private double minAccRate;

	private double tuneStepSize = TUNESTEPSIZE;
//	private double tuneInitSize = TUNEINITSIZE;
	// private double accTol = Constant.ACCTOL;
	private double accLower = OPTIMRATE - ACCTOL;
	private double accUpper = OPTIMRATE + ACCTOL;
	private double[] initValue;

	private static RandomDataImpl rd = new RandomDataImpl();

	public TunePar(int tuneSize, int tuneGroup, double[] initValue, String[] type) {

		this.initValue = initValue.clone();
		this.tunePar = initValue;
		this.noTunePar = initValue.length;
		
		this.tuneSize = tuneSize;
		this.tuneGroup = tuneGroup;
		this.tuneGroup1 = tuneGroup - 1;
		this.accept = new double[noTunePar][tuneGroup];

		setType(type);
		
//		tuneInitSize = 2.38;
//		tuneStepSize = 0.001;
		
//		for (int i = 0; i < noTunePar; i++) {
//			Arrays.fill(tunePar, tuneInitSize);
//		}
//		minAccRate = 1.0 / tuneSize;


	}

	public void setType(String[] type) {

		try {
			if (noTunePar != type.length) {
				System.out.println("incorrect tune type length"
						+ Arrays.toString(type));
				System.out.println("Number of tuning par: " + noTunePar);
				 System.exit(-1);
			}
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		tuneType = new int[noTunePar];
		for (int i = 0; i < type.length; i++) {
			if (type[i].equalsIgnoreCase("normal")) {
				tuneType[i] = 0;
			} else if (type[i].equalsIgnoreCase("normalbig")) {
				tuneType[i] = 1;
			} else if (type[i].equalsIgnoreCase("scale")) {
				tuneType[i] = 2;
			}
		}
//		 System.out.println("Tune type:\t"+Arrays.toString(tuneType));
	}
	

	public void update(SavePar all, int ite) {

		int index = ite / tuneSize;

		double[] newAcc = all.calAccRate(tuneSize);
		double[] accRate = new double[newAcc.length];
		if (index >= tuneGroup) {

			for (int i = 0; i < accRate.length; i++) {
				System.arraycopy(accept[i], 1, accept[i], 0, tuneGroup1);
				accept[i][tuneGroup1] = newAcc[i];
				accRate[i] = StatUtils.mean(accept[i]);
				
			}
			for (int i = 0; i < tunePar.length; i++) {
				tunePar[i] = checkRate(tunePar[i], accRate[i], tuneType[i], initValue[i]);
			}	
		} else {
			for (int i = 0; i < accRate.length; i++) {
				accept[i][index] = newAcc[i];
				accRate[i] = StatUtils.mean(accept[i]);
			}

		}
//		for (int i = 0; i < tunePar.length; i++) {
//			tunePar[i] = checkRate(tunePar[i], accRate[i], tuneType[i], initValue[i]);
//		}
	}

	private double checkRate(double tp, double d, int type, double reset) {

		double newRate = tp;
		if (type == 0) {
			newRate = checkNormal2(tp, d, reset);
		} else if (type == 1) {
			newRate = checkNormal2(tp, d, reset);
		} else if (type == 2) {
			newRate = checkScale(tp, d);
		}
		return newRate;
	}

	private double checkScale(double tp, double d) {

		if (d >= accUpper) {
			tp -= rd.nextUniform(0, tuneStepSize);
		} else if (d < accLower) {
			tp += rd.nextUniform(0, tuneStepSize);

		}

		if (tp <= 0 | tp >= 1) {
			tp = rd.nextUniform(0.7, 0.8);
		}

		return tp;

	}
	

	private double checkNormal2(double tp, double d, double reset) {

		double newTp = tp * INV_OPT_ACC
				/ NormalDistribution.quantile(d / 2);
		System.out.println(d+"\t"+ NormalDistribution.quantile(d / 2)+
				"\t" + (tp * INV_OPT_ACC)+
				"\t" + newTp+
				"\t" + Double.isNaN(newTp) );
		if (Double.isNaN(newTp)) {
			if(tp == reset){
				newTp = reset*0.99;
			}
			else{
				newTp = reset;
			}
		}
//		if (tp< (reset/100) ) {
//			if(tp == reset){
//				newTp = 2.38;
//			}
//			else{
//				newTp = reset;
//			}
//		}
		if(newTp == 0){
			newTp = reset*5;
		}
		if( newTp> (reset*10) | newTp < (reset/20) ){
			newTp = reset;
		}

		return newTp;
	}
	
	
	@Deprecated
	private double checkNormal(double tp, double d, double reset) {

		double newTp = tp * INV_OPT_ACC
				/ NormalDistribution.quantile(d / 2);
		if (Double.isNaN(newTp)) {
			if(tp == reset){
				newTp = 2.38;
			}
			else{
				newTp = reset;
			}
		}
		if (tp< 0.001) {
			if(tp == reset){
				newTp = 2.38;
			}
			else{
				newTp = reset;
			}
		}
		if(newTp>10){
			newTp = 2.38;
		}
		return newTp;
	}
	
	@Override
	public String toString() {
		return "TunePar [getAveAccRate()=" + Arrays.toString(getEachAccRate())
				+ ", tunePar=" + Arrays.toString(tunePar) + "]";
	}

	public double[] getEachAccRate() {

		double[] r = new double[noTunePar];
		for (int i = 0; i < noTunePar; i++) {
			r[i] = StatUtils.mean(accept[i]);
		}
		return r;

	}

	public double getMeanAccRate() {

		
		double rMean = StatUtils.mean(getEachAccRate());
		
		return rMean;

	}
	
	public double[] getTunePar() {

		return tunePar;
	}

	public double getTunePar(int index) {

		return tunePar[index];
	}

	public int[] getTuneType() {
		return tuneType;
	}

	public int getTuneSize() {
		return tuneSize;
	}

	public int getTuneGroup() {
		return tuneGroup;
	}

	public void setTuneSize(int tuneSize) {
		this.tuneSize = tuneSize;
	}

	public void setTuneGroup(int tuneGroup) {
		this.tuneGroup = tuneGroup;
	}



}
