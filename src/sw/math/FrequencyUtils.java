package sw.math;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.util.MathUtils;

import dr.app.tools.NormaliseMeanTreeRate;

public class FrequencyUtils {

	public static double[] summaryTable(int[] count, int noElement){
		
//		int[] unique = removeDuplicates(count);
		double[] table = new double[noElement];
		for (int i : count) {
			table[i]++;
		}
		table = normalise(table, count.length);
		return table;
	}
	
	public static double[] summaryTable(int[] count, int[] elementList){
		
//		int[] unique = removeDuplicates(count);
		double[] table = new double[elementList.length];
		for (int i : count) {
			int index = ArrayUtils.indexOf(elementList, i);
			table[index]++;
		}
		table = normalise(table, count.length);
		return table;
	}
	
	private static double[] normalise(double[] table, int total){
		for (int i = 0; i < table.length; i++) {
			table[i] = table[i]/total;
		}
		return table;
	}
	
	private static int[] removeDuplicates(int... array) {
	    Integer[] ints = ArrayUtils.toObject(array);
	    Set<Integer> set = new LinkedHashSet<Integer>(Arrays.asList(ints));
	    int[] temp = ArrayUtils.toPrimitive(set.toArray(new Integer[set.size()]));
	    Arrays.sort(temp);
	    return temp;
	}
}
