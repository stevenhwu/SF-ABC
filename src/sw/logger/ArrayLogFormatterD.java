package sw.logger;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import sw.beast.trace.Trace;
import sw.beast.trace.TraceFactory;

import com.google.common.primitives.Doubles;


@SuppressWarnings({ "unchecked", "rawtypes" })
	
	public class ArrayLogFormatterD {

//	private static LogColumn lc = new LogColumn("");
	private static NumberColumn lc = new NumberColumn("");

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

	public void addTrace(Trace trace){
		traces.add(trace);
		labels = (String[]) ArrayUtils.add(labels,  trace.getName() );	
	}

	public void addTrace(Trace... t){
		for (Trace trace : t) {
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

	
	public void logHeading(String heading) {
		this.heading = heading;
		echo(heading);
	}

	
	public void logLabels(String[] labels) {
		if (this.labels == null) {
			this.labels = labels;
			for (String label : labels) {
				traces.add(new Trace(label,
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
	
	
	public void logLine(String line) {
		lines.add(line);
		echo(line);
	}




	public double[][] to2DArray() {

		int noPar = getSize();
		int size = traces.get(0).getValuesSize();
		double[][] summary = new double[noPar][size];
		for (int i = 0; i < noPar; i++) {
			double[] d = Doubles.toArray(traces.get(i).getValues(0, size, null) );
			for (int j = 0; j < d.length; j++) {
				summary[i][j] = d[j];
			}
		}

		return summary;
	}

	public double[] toArray(int i) {
		
		Trace t = traces.get(i);
		int size = t.getValuesSize();
		double[] d = Doubles.toArray(t.getValues(0, size, null) );
		return d;
	}

	public int getLength() {

		Trace t = traces.get(0);
		int length = t.getValuesSize();
		return length;
	}
}
