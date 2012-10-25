/*******************************************************************************
 * HeapSort.java
 * 
 * This file is part of BIDE-2D
 * 
 * Copyright (C) 2012 Steven Wu
 * 
 * BIDE-2D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BIDE-2D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BIDE-2D.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * HeapSort.java
 *
 * Copyright (C) 2002-2006 Alexei Drummond and Andrew Rambaut
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package sw.hpd;

import java.util.AbstractList;
import java.util.Comparator;

/**
 * sorts numbers and comparable objects by treating contents of array as a
 * binary tree. KNOWN BUGS: There is a horrible amount of code duplication here!
 * 
 * @version $Id: HeapSort.java,v 1.7 2006/02/20 17:36:23 rambaut Exp $
 * 
 * @author Alexei Drummond
 * @author Korbinian Strimmer
 */
@SuppressWarnings("unchecked")
public class HeapSort {

	/**
	 * Sorts an array of indices into an array of doubles into increasing order.
	 */
	public static void sort(double[] array, int[] indices) {

		// ensures we are starting with valid indices
		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
		}

		int temp;
		int j, n = indices.length;

		// turn input array into a heap
		for (j = n / 2; j > 0; j--) {
			adjust(array, indices, j, n);
		}

		// remove largest elements and put them at the end
		// of the unsorted region until you are finished
		for (j = n - 1; j > 0; j--) {
			temp = indices[0];
			indices[0] = indices[j];
			indices[j] = temp;
			adjust(array, indices, 1, j);
		}
	}


	/**
	 * helps sort an array of indices into an array of doubles. Assumes that
	 * array[lower+1] through to array[upper] is already in heap form and then
	 * puts array[lower] to array[upper] in heap form.
	 */
	private static void adjust(double[] array, int[] indices, int lower,
			int upper) {

		int j, k;
		int temp;

		j = lower;
		k = lower * 2;

		while (k <= upper) {
			if ((k < upper) && (array[indices[k - 1]] < array[indices[k]])) {
				k += 1;
			}
			if (array[indices[j - 1]] < array[indices[k - 1]]) {
				temp = indices[j - 1];
				indices[j - 1] = indices[k - 1];
				indices[k - 1] = temp;
			}
			j = k;
			k *= 2;
		}
	}
}
