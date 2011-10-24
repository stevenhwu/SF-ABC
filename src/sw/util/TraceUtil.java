package sw.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import sw.math.Combination;

import dr.inference.trace.Trace;
import dr.inference.trace.TraceFactory;
import dr.inference.trace.TraceFactory.TraceType;

import dr.inference.loggers.NumberColumn;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TraceUtil {

//	TraceFactory.TraceType
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

	public static Trace creatTrace(String prefix){
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
