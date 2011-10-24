package sw.sequence;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math.stat.descriptive.moment.Skewness;
import org.apache.commons.math.util.MathUtils;

import sw.math.FrequencyUtils;

import dr.evolution.alignment.SimpleAlignment;
import dr.util.Citation.Status;

public class SiteAlignPerTime {

//	private static final int SITE_PATTERN_COUNT = Site.PATTERN;
	final static double SITE_SPEC_BIN_SIZE = 0.05;
	final static int SITE_SPEC_NO_BIN = (int) (1/SITE_SPEC_BIN_SIZE);
	

	private ArrayList<Site> allSites;
	private double[] siteSpecturm; 
	private double[] siteSpecturmEach;
	private SimpleAlignment saAlignment;
	
	public SiteAlignPerTime(int n) {
		allSites = init(n);
		siteSpecturm = new double[SITE_SPEC_NO_BIN+1];
		siteSpecturmEach = new double[9];
	}

	public ArrayList<Site> init(int n){
		ArrayList<Site> s = new ArrayList<Site>(n);
		for (int i = 0; i < n; i++) {
			s.add(new Site(i));
		}
		return s;

	}


	public void calcFreq() {

		int noState = saAlignment.getStateCount();
		int noSite = saAlignment.getSiteCount();
		int noSeq = saAlignment.getSequenceCount();

		double[] freqs = new double[noState];
		int[] pattern;
		//		int[] freqCount = new int[noState];
		int[] freqCount = new int[18];
		int i, j;

		//			ArrayList<Site> allSites = new ArrayList<Site>(noSite);
		//			Site s = new Site();

		for (i = 0; i < noSite; i++) {
			freqs = new double[noState];
			freqCount = new int[18];
			pattern = saAlignment.getPattern(i);
			for (j = 0; j < noSeq; j++) {
				freqCount[pattern[j]]++;
			}
			double sum = 0.0+freqCount[0]+freqCount[1]+freqCount[2]+freqCount[3];
			for (int k = 0; k < 4; k++) {
				freqs[k] = freqCount[k]/sum;
			}
			//				s.updateSite(i, freqs);
			allSites.get(i).updateSite(freqs);

		}

	}
//
//	private void calcSpectrumProb(){
//		
//		double n = allSites.size();
//		siteSpecturmProb = new double[siteSpecturm.length];
//		for (int i = 0; i < siteSpecturm.length; i++) {
//			siteSpecturmProb[i] = siteSpecturm[i] / n;
//		}  
//	}

	public void calcSpectrum() {
		
//		allSites
		
		Arrays.fill(siteSpecturm, 0);
		Arrays.fill(siteSpecturmEach, 0);
		double n = allSites.size();
		for (Site s : allSites) {
			double max = s.getMaxFreq();
			int ind = (int) (max / SITE_SPEC_BIN_SIZE);
			siteSpecturm[ind]++;
			ind = putSmallFreqBin(max);
			siteSpecturmEach[ind]++;
		} 
		for (int i = 0; i < siteSpecturm.length; i++) {
			siteSpecturm[i] = siteSpecturm[i] / n;
		}  


	}

	private int putSmallFreqBin(double max) {
		int ind = 8;
		if(max>=0.99){
			ind = 0;
		}else if(max>=0.97){
			ind = 1;
		}else if(max>=0.95){
			ind = 2;
		}else if(max>=0.92){
			ind = 3;
		}else if(max>=0.95){
			ind = 4;
		}else if(max>=0.9){
			ind = 5;
		}else if(max>=0.87){
			ind = 6;
		}else if(max>=0.85){
			ind = 7;
		}		

		return ind;
	}

	public double[] getSiteSpecturm() {
		return siteSpecturm;
	}
	public double[] getSiteSpecturmEach() {
		return siteSpecturmEach;
	}
//	public double[] getSiteSpecturmProb() {
//		return siteSpecturmProb;
//	}
	public int calNoDiffSiteSpecturm(int[] otherSpec){
		int count = 0;
		for (int i = 0; i < otherSpec.length; i++) {
			count += Math.abs(siteSpecturm[i] - otherSpec[i]);
		}
		return count;
	}

	public double calInterDist(SiteAlignPerTime st2) {

		double dist = 0;
		for (int i = 0; i < allSites.size(); i++) {
			dist += allSites.get(i).calDist(st2.getSite(i));
		}
		dist /= allSites.size();
		return dist;
	}

	public Site getSite(int i) {
		return allSites.get(i);
	}

	public double calIntraDist() {

		double dist = 0;
		for (int i = 0; i < allSites.size(); i++) {
			dist += allSites.get(i).calDist();
		}
		dist /= allSites.size();
		return dist;
	}

	public void addAlignment(SimpleAlignment timeAlignment) {
		this.saAlignment = timeAlignment;
		
	}

	public double calVar() {
		double[] siteDist = new double[allSites.size()];
		for (int i = 0; i < allSites.size(); i++) {
			siteDist[i] = allSites.get(i).calDist();
		}
		return StatUtils.variance(siteDist);
	}

	public double calKurtosis() {
		double[] siteDist = new double[allSites.size()];
		for (int i = 0; i < allSites.size(); i++) {
			siteDist[i] = allSites.get(i).calDist();
		}
		Kurtosis k = new Kurtosis();
		return k.evaluate(siteDist) ;
	}

	public double calSkewness() {
		double[] siteDist = new double[allSites.size()];
		for (int i = 0; i < allSites.size(); i++) {
			siteDist[i] = allSites.get(i).calDist();
		}
		Skewness s = new Skewness();
		return s.evaluate(siteDist) ;
	}

	public double cal2ndMoment() {
				
		double[] siteDist = new double[allSites.size()];
		for (int i = 0; i < allSites.size(); i++) {
			siteDist[i] = allSites.get(i).calDist();
		}
		SecondMoment s = new SecondMoment();
		return s.evaluate(siteDist) ;

	}

	public double[] calSitePattern(SiteAlignPerTime st2) {

		int[] pattern = new int[allSites.size()];
		for (int i = 0; i < allSites.size(); i++) {
			pattern[i] = allSites.get(i).calPattern(st2.getSite(i));
		}		
		double[] table = FrequencyUtils.summaryTable(pattern, Site.PATTERN);

		return table;
	}



}
