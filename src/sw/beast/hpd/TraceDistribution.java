/*******************************************************************************
 * TraceDistribution.java
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
package sw.beast.hpd;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

//import dr.inference.trace.TraceFactory;


/**
 * A class that stores the distribution statistics for a trace
 * 
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @version $Id: TraceDistribution.java,v 1.1.1.2 2006/04/25 23:00:09 rambaut
 *          Exp $
 */
public class TraceDistribution {
    

    public TraceDistribution(List values ) {
    
//        initStatistics(values, 0.95);
        double[] newValues = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            newValues[i] = ((Number) values.get(i)).doubleValue();
        }
        analyseDistribution(newValues, 0.95);
    }

    public TraceDistribution(List values,  double ESS) {
        this(values);
        this.ESS = ESS;
    }

	public TraceDistribution(double[] values, double hpdValue) {
		analyseDistribution(values, hpdValue);

	}

	public boolean isValid() {
		return isValid;
	}

	public double getMean() {
		return mean;
	}

	public double getMedian() {
		return median;
	}

	public double getLowerHPD() {
		return hpdLower;
	}

	public double getUpperHPD() {
		return hpdUpper;
	}

	public double getLowerCPD() {
		return cpdLower;
	}

	public double getUpperCPD() {
		return cpdUpper;
	}

    public double getESS() {
        return ESS;
    }
    
	public double getMinimum() {
		return minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	/**
	 * Analyze trace
	 * @param hpdValue TODO
	 */
	private void analyseDistribution(double[] values, double hpdValue) {


//		mean = DiscreteStatistics.mean(values);
		mean = StatUtils.mean(values);
		minimum = Double.POSITIVE_INFINITY;
		maximum = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < values.length; i++) {
			if (values[i] < minimum)
				minimum = values[i];
			if (values[i] > maximum)
				maximum = values[i];
		}

		if (maximum == minimum) {
			isValid = false;
			return;
		}

		int[] indices = new int[values.length];
		HeapSort.sort(values, indices);
		median = statQuantile(0.5, values, indices);
		cpdLower = statQuantile(0.025, values, indices);
		cpdUpper = statQuantile(0.975, values, indices);
		ESS = values.length;
		calculateHPDInterval(hpdValue, values, indices);

		isValid = true;
	}

	public static double[] HPDInterval(double proportion, double[] x, int[] indices) {

        double minRange = Double.MAX_VALUE;
        int hpdIndex = 0;

        final int diff = (int) Math.round(proportion * x.length);
        for (int i = 0; i <= (x.length - diff); i++) {
            final double minValue = x[indices[i]];
            final double maxValue = x[indices[i + diff - 1]];
            final double range = Math.abs(maxValue - minValue);
            if (range < minRange) {
                minRange = range;
                hpdIndex = i;
            }
        }

        return new double[]{x[indices[hpdIndex]], x[indices[hpdIndex + diff - 1]]};
    }
    private void calculateHPDInterval(double proportion, double[] array, int[] indices) {
        final double[] hpd = HPDInterval(proportion, array, indices);
        hpdLower = hpd[0];
        hpdUpper = hpd[1];
    }

    

	// ************************************************************************
	// private methods
	// ************************************************************************

	protected boolean isValid = false;

	protected double minimum, maximum;
	protected double mean, median;
	protected double cpdLower, cpdUpper;
	protected double hpdLower, hpdUpper;
	protected double ESS;
	public static double statQuantile(double q, double[] x, int[] indices) {
		if (q < 0.0 || q > 1.0)
			throw new IllegalArgumentException("Quantile out of range");

		if (q == 0.0) {
			// for q==0 we have to "invent" an entry smaller than the smallest x

			return x[indices[0]] - 1.0;
		}

		return x[indices[(int) Math.ceil(q * indices.length) - 1]];
	}
}
