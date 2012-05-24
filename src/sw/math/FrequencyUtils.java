package sw.math;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

public class FrequencyUtils {

	public static double[] summaryTable(int[] count, int noElement){
		// noElement == Total number of patterns - 1
		//Sum up to one, so take out the last position
		double[] table = new double[noElement+1];
		for (int i : count) {
			table[i]++;
		}
		table = normalise(table, count.length);
		final double[] result = new double[noElement];
		System.arraycopy(table, 0, result, 0, result.length);
		return result;
	}
	
	public static double[] summaryTable(int[] count, int[] elementList){
		
//		int[] unique = removeDuplicates(count);
		double[] table = new double[elementList.length];
		for (int i : count) {
			final int index = ArrayUtils.indexOf(elementList, i);
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
	    final Integer[] ints = ArrayUtils.toObject(array);
	    final Set<Integer> set = new LinkedHashSet<Integer>(Arrays.asList(ints));
	    final int[] temp = ArrayUtils.toPrimitive(set.toArray(new Integer[set.size()]));
	    Arrays.sort(temp);
	    return temp;
	}
}
