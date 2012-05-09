package sw.util;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import sw.math.Combination;
import sw.sequence.Site;
import sw.util.TraceFactory.TraceType;



import sw.abc.parameter.NumberColumn;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TraceUtil {

	
	private  HashMap<String, Integer> summaryStatCollection = new HashMap<String, Integer>();

	private int noTime;
	private int noParam;
	
	public TraceUtil(int noTime, int noParam){

		this.noTime = noTime;
		this.noParam = noParam;
		int noComb = Combination.calNoComb(this.noTime);
		summaryStatCollection.put("mu", 1);
		summaryStatCollection.put("popsize", 1);
		summaryStatCollection.put("gap", this.noParam);
		
		summaryStatCollection.put("dist", -1);
		summaryStatCollection.put("chisq", this.noTime);
		summaryStatCollection.put("var", this.noTime);
		summaryStatCollection.put("sitePattern", Site.PATTERN_COUNT*noComb);
		summaryStatCollection.put("freq", 9);
	}
	
	public TraceUtil(int noTime){
		this(noTime, 0);

	}
	
	public ArrayList<Trace> createTraceAL(String[] paramList){
		
		ArrayList<Trace> allAL = new ArrayList<Trace>();
		for (String key : paramList) {
			System.out.println(key);
			int noTrace = summaryStatCollection.get(key);	
			if(noTrace == 0){
	            System.err.println("Check noParam OR parameter name: "+ key);
	            System.exit(-1);
			}
			else if(noTrace== -1){
				allAL.addAll( TraceUtil.creatTraceDist(noTime) );	
			}
			else if(noTrace==1){
				allAL.add( TraceUtil.creatTraceOne( key) );	
			}
			else{
				allAL.addAll( TraceUtil.creatTrace(noTrace, key) );
			}
		}
		return allAL; 
	}


	public ArrayList<Trace> createTraceAL(String key){
		int noTrace = summaryStatCollection.get(key);
		return TraceUtil.creatTrace(noTrace, key);
	}
	

	static NumberColumn nc = new NumberColumn("") {
		
		@Override
		public double getDoubleValue() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	};
	
	public static double[] toPrimitive(Trace<Double> t){
		
		List<Double> y = t.getValues(0, t.getValuesSize(), null);
		Double[] array = new Double[t.getValuesSize()];
		array = y.toArray(array);

		return ArrayUtils.toPrimitive(array);
		
	}


	public static ArrayList<Trace> creatTraceDist(int noTime){
		return creatTraceDist(noTime, TraceType.DOUBLE);
	}
	public static ArrayList<Trace> creatTraceDist(int noTime, TraceType tf){
		
		ArrayList<Trace> newT = new ArrayList<Trace>();
		int noComb = Combination.calNoComb(noTime);
		for (int i = 0; i < noComb; i++){
			Trace t = new Trace("Inter_"+i, tf);
			newT.add(t);
		}
		for (int i = noComb; i < noTime+noComb; i++) {
			Trace t = new Trace("Intra_"+i, tf);
			newT.add(t);
		}
	
		return newT;
		
	}

	
	public static ArrayList<Trace> creatTrace(int noTrace, String prefix){
		return creatTrace(noTrace, prefix, TraceType.DOUBLE);
	}
	public static ArrayList<Trace> creatTrace(int noTrace, String prefix, TraceType tf){
		
		ArrayList<Trace> newT = new ArrayList<Trace>();
		for (int i = 0; i < noTrace; i++) {
			Trace t = new Trace(prefix+"_"+i, tf);
			newT.add(t);
		}
		return newT;
		
	}
	
	public static Trace creatTraceOne(String prefix){
		return creatTrace(prefix, TraceType.DOUBLE);
	}
	public static Trace creatTrace(String prefix, TraceType tf) {
		Trace t = new Trace(prefix, tf);
		return t;
	}


	public static void logValue(Trace t, double value) {
		t.add(value);
	}

	public static void logValue(ArrayList<Trace> trace, double[] value) {
		for (int i = 0; i < trace.size(); i++) {
			trace.get(i).add(value[i]);
		}
	}
	public static void logValue(ArrayList<Trace> trace, double value) {
		for (int i = 0; i < trace.size(); i++) {
			trace.get(i).add(value);
		}
	}	
	
	
	public static String toString(Trace<Double> t){
//		return ArrayUtils.toString( toPrimitive(t) );
		return Arrays.toString( toPrimitive(t) );
		
	}
	
	
	
	


	public static String summary(int index, ArrayList<?> t){
		
		nc.setDecimalPlaces(5);
		StringBuilder sb = new StringBuilder();
		sb.append(index).append('\t');
		for (int i = 0; i < t.size(); i++) {
			Trace<Double> td = (Trace<Double>) t.get(i);
			sb.append( nc.formatValue(td.getValue(index)) ).append('\t');
		}
		return sb.toString();
		
	}
}
