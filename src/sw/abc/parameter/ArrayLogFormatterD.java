package sw.abc.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;

import dr.evolution.io.NewickImporter;
import dr.inference.trace.Trace;
import dr.inference.trace.TraceFactory;

import dr.inference.loggers.LogFormatter;


public class ArrayLogFormatterD<T> implements LogFormatter {

	private static LogColumn lc = new LogColumn("");

	int dp = -1;
	String heading;
	String[] labels = new String[]{};
	ArrayList<Trace> traces = new ArrayList<Trace>();

	List<String> lines = new ArrayList<String>();
//	ArrayList<double[]> traceDouble = new ArrayList<double[]>();
	
//	List<Parameters> params = new ArrayList<Parameters>();
	
	boolean echo = false;

	public ArrayLogFormatterD() {
		this(false);
	}
	
	public ArrayLogFormatterD(int dp) {
		setDp(dp);
	}
	
	public ArrayLogFormatterD(boolean echo) {
		this.echo = echo;
	}
	
	public ArrayLogFormatterD(int i, ArrayList<Trace> traceAL) {
		setDp(dp);
		addTrace(traceAL);
	}

	public void addTrace(Trace<T> trace){
		traces.add(trace);
		labels = (String[]) ArrayUtils.add(labels,  trace.getName() );	
	}

	public void addTrace(Trace<T>... t){
		for (Trace<T> trace : t) {
			addTrace(trace);
		}
	}
	
	public void addTrace(ArrayList<Trace>... allT ){

		for (ArrayList<Trace> arrayList : allT) {
			for (Trace trace : arrayList) {
				addTrace(trace);
			}
		}
	}

//	public void add(ArrayList<Trace> t) {
//		for (Trace trace : t) {
//			traces.add(trace);
//		}
//	}
//	public void add(Trace t) {
//		
//			traces.add(t);
//		
//	}

	
	public void setDp(int dp) {
		lc.setDecimalPlaces(dp);
	}
	public void setSf(int sf) {
		lc.setSignificantFigures(sf);
	}
	private void echo(String s) {
		if (echo)
			System.out.println(s);
	}

	private void echo(String[] strings) {
		if (echo) {
			for (String s : strings) {
				System.out.print(s + "\t");
			}
			System.out.println();
		}
	}

	public String getLabels() {
		StringBuilder sb = new StringBuilder();
		for (String s : labels) {
			sb.append(s).append("\t");
		}
		return sb.toString().trim();
	}

//	public String getLine(int n) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < getTraces().size(); i++) {
//			double d = (Double) (getTraces().get(i).getValue(n));
//			sb.append(lc.formatValue(d)).append("\t");
//			// sb.append((getTraces().get(i).getValue(n))).append("\t");
//		}
//		
//		return sb.toString().trim();
//	}
	public String getLine(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < traces.size(); i++) {
			double d = (Double) (traces.get(i).getValue(n));
			
			if(d == Math.rint(d) ){
				sb.append(d).append("\t");
			}
			else{
				sb.append(lc.formatValue(d)).append("\t");
			}
		}
		return sb.toString().trim();
	}

	public String getLine(int n, int noDec) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getTraces().size(); i++) {
			sb.append(getTraces().get(i).getValue(n)).append("\t");
		}
		// nc.setDecimalPlaces(5);
		//
		// sb.append( nc.formatValue(td.getValue(index)) ).append('\t');

		return sb.toString();
	}

	public int getSize() {
		return traces.size();
	}

	public List<Trace> getTraces() {
		return traces;
	}

	@Override
	public void logHeading(String heading) {
		this.heading = heading;
		echo(heading);
	}

	@Override
	public void logLabels(String[] labels) {
		if (this.labels == null) {
			this.labels = labels;
			for (String label : labels) {
				traces.add(new Trace<T>(label,
						TraceFactory.TraceType.DOUBLE));
			}
			echo(labels);
		} else
			throw new RuntimeException(
					"logLabels() method should only be called once!");
	}

	public void logValues(double[] ds) {
		for (int i = 0; i < traces.size(); i++) {
			traces.get(i).add(ds[i]);
		}
	}
	
	@Override
	public void logLine(String line) {
		lines.add(line);
		echo(line);
	}

//	public void logValues(double[] values) {
//		for (int i = 0; i < values.length; i++) {
//			// Double v = Double.parseDouble(values[i]);
//			getTraces().get(i).add(values[i]);
//		}
//	}

	@Override
	public void logValues(String[] values) {
//		for (int i = 0; i < values.length; i++) {
//			// Double v = Double.parseDouble(values[i]);
//			traces.get(i).add(Double.parseDouble(values[i]));
//		}
//		echo(values);
	}

	@Override
	public void startLogging(String title) {
	}

	@Override
	public void stopLogging() {
	}

	public double[][] to2DArray() {

		int noPar = getSize();
		int size = traces.get(0).getValuesSize();
		double[][] summary = new double[noPar][size];
		for (int i = 0; i < noPar; i++) {
			double[] d = Doubles.toArray((Collection<Double>) traces.get(i).getValues(0, size, null) );
			for (int j = 0; j < d.length; j++) {
				summary[i][j] = d[j];
			}
		}
		return summary;
	}

	public double[] toArray(int i) {
		
		Trace t = traces.get(i);
		int size = t.getValuesSize();
		double[] d = Doubles.toArray((Collection<Double>) t.getValues(0, size, null) );
		return d;
	}
}
