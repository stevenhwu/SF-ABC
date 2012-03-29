package sw.util;

import org.apache.commons.lang3.ArrayUtils;


public class ArrayCopyUtils {

	public static double[] addToArray(double[] src, double[] dest) {
		int po = ArrayUtils.indexOf(dest, -1);
		System.arraycopy(src, 0, dest, po, src.length);
	
		return dest;
	}
}
