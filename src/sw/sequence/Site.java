package sw.sequence;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;

public class Site {

	public static final int PATTERN_COUNT = 5-1; //N different patterns, but sum up to one, so take out the last position
	private int index;
	private double[] freqs;
	private double maxFreq;

	public static ArrayList<Site> createSiteArrayList(int n) {
		ArrayList<Site> allSites = new ArrayList<Site>(n);
		for (int i = 0; i < n; i++) {
			allSites.add(new Site(i));
		}
		return allSites;

	}

	public static Site createSiteWithFreqs(double[] freqs){
		Site s = new Site(0);
		s.updateSite(freqs);
		return s;
	}


	public Site(int i) {
		this.index = i;
	}

	public void updateSite(double[] freqs) {
		this.freqs = freqs;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double[] getFreqs() {
		return freqs;
	}

	public void setFreqs(double[] freqs) {
		this.freqs = freqs;
	}

	public double getEachFreqs(int i) {
		return freqs[i];
	}

	public static double pairDist(ArrayList<Site> s1, ArrayList<Site> s2) {

		double dist = 0;
		for (int i = 0; i < s1.size(); i++) {
			dist += s1.get(i).calDist(s2.get(i));
		}
		dist /= s1.size();
		return dist;
	}

	public static double pairDist(ArrayList<Site> s1) {

		double dist = 0;
		for (int i = 0; i < s1.size(); i++) {
			dist += s1.get(i).calDist();
		}
		dist /= s1.size();
		return dist;
	}

	public double calDist() {

		double sq = 0;
		for (int i = 0; i < freqs.length; i++) {
			sq += freqs[i] * freqs[i];
		}

		return 1 - sq;
	}

	public double calDist(Site site) {

		double sq = 0;
		for (int i = 0; i < freqs.length; i++) {
			sq += freqs[i] * site.getEachFreqs(i);
		}

		return 1 - sq;
	}

	public double getMaxFreq() {
		maxFreq = StatUtils.max(freqs);
		return maxFreq;
	}

	public int getMaxFreqIndex() {

		return ArrayUtils.indexOf(freqs, maxFreq);

	}
	

	public int calPattern(Site site) {

		int pattern = -1;
		int maxIndex = getMaxFreqIndex();
		int maxInd2 = site.getMaxFreqIndex();
		
		if (maxIndex == maxInd2) {
			if (freqs[maxIndex] == 1 && site.getFreqs()[maxIndex] == 1) {
				pattern = 0; // no change
			} else if (freqs[maxIndex] == 1 || site.getFreqs()[maxIndex] == 1) {
				pattern = 1; // 1 time point has no changes
			} else {
				pattern = 2; // majority all equal
			}
		} else if (site.getFreqs()[maxIndex] != 0 || freqs[maxInd2] != 0) {
			pattern = 3; //TEMP majority and non zero

		}
		else {
			pattern = 4; //diff maj
			// OLD: TEMP both "majority and zero", too few of them, merge with 3
			// 4 false [0.625, 0.0, 0.0, 0.375] [0.0, 0.0, 0.55, 0.45]
			// 4 false [0.4, 0.575, 0.0, 0.025] [0.4, 0.0, 0.6, 0.0]
			// 4 false [0.5, 0.5, 0.0, 0.0] [0.0, 0.4, 0.0, 0.6]	
//			System.out.println(pattern + "\t"			+ (maxIndex == site.getMaxFreqIndex()) + "\t"			+ Arrays.toString(freqs) + "\t"			+ Arrays.toString(site.getFreqs()));
			
		}

		return pattern;
	}
}
