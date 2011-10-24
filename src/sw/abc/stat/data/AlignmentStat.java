package sw.abc.stat.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.util.MathUtils;
import org.hamcrest.internal.ArrayIterator;

import sw.abc.stat.summary.SStat3TMoreStat;
import sw.abc.stat.summary.SStat3TMoreStatNoInt;
import sw.abc.stat.summary.SStatBrLn;
import sw.abc.stat.summary.SStatFreq;
import sw.abc.stat.summary.SStatLocal;
import sw.abc.stat.summary.SStatSitePattern;
import sw.abc.stat.summary.SStatTopFreq;
import sw.abc.stat.summary.SStatTopFreqSingleProduct;
import sw.abc.stat.summary.SStatTopFreqSingleProductNoS;
import sw.abc.stat.summary.SStatTopFreqSingleSum;
import sw.abc.stat.summary.SummaryStat;
import sw.main.Setup;
import sw.sequence.SiteAlignment;

public class AlignmentStat {

	//
	// private double statMu;
	// private double statTheta;

	private SummaryStat sumStat;

	// cheating stat
	private double[] siteBrLn;

	// Alignment properties
	private double[][] siteFreqSpec;
	private double[][] siteFreqSpecEach;
	private double[] siteDists;
	
	private double[] siteVarinace;
	private double[] siteChiDist;
	private double[] siteKurtosis;
	private double[] siteSecondM;
	private double[] siteSkewness;

	private double[] parIndex;
	private double[][] multiStatAll;
	private double[] statAll;

	private double[][] sitePattern;

	
	public double[] getStatAll() {
		return statAll;
	}

	public double[][] getMultiStatAll() {
		return multiStatAll;
	}

	public AlignmentStat() {
		// this(null);
	}

	public AlignmentStat(SummaryStat sumStat) {
		this.sumStat = sumStat;
	}

	// public AlignmentStat(int noTime) {
	// this.noTime = noTime;
	// }
	//
	// public void addChiStat(double[] chiStat) {
	// this.chiStat = chiStat;
	// }

	// public void addMu(double mu) {
	// this.statMu = mu;
	// }
	//
	// public void addTheta(double theta) {
	// this.statTheta = theta;
	// }

	public AlignmentStat(Setup setting) {
		this(setting.getStat());
	}

	public double[] getSiteChiDist() {
		return siteChiDist;
	}

	public double[] getSiteKurtosis() {

		return siteKurtosis;
	}

	public double[] getSiteVarinace() {
		return siteVarinace;
	}

	public double[] getSiteDists() {
		return siteDists;
	}

	public double[][] getSiteFreqSpec() {
		return siteFreqSpec;
	}
	public double[][] getSiteFreqSpecEach() {
		return siteFreqSpecEach;
	}
	public double[][] getSitePattern() {
		return sitePattern;
	}

	// public double getMu() {
	// return statMu;
	// }

	// public double[] getParam() {
	// double[] stat = { statMu, statTheta };
	// return stat;
	// }
	
	// TODO fix adding/cal/updating
	public void updateSiteAlignment(SiteAlignment sa) {
		addSiteDists(sa);
		addSiteFreqSpec(sa);
		addSiteVar(sa);
		addSitePattern(sa);
		// addSiteKurtosis(sa);
		// addSiteSecondM(sa);
		// addSiteSkewness(sa);

	}



	public void addParIndex(double[] par) {
		parIndex = par;
	}

	
	private void addSiteSecondM(SiteAlignment sa) {
		this.siteSecondM = sa.getSecondM();
	}

	private void addSiteSkewness(SiteAlignment sa) {
		this.siteSkewness = sa.getSkewness();
	}

	private void addSiteKurtosis(SiteAlignment sa) {
		this.siteKurtosis = sa.getKurtosis();
	}

	private void addSiteDists(SiteAlignment sa) {
		this.siteDists = sa.calDists();

	}


	private void addSiteVar(SiteAlignment sa) {
		this.siteVarinace = sa.getVar();
	}

	private void addSiteFreqSpec(SiteAlignment sa) {

		this.siteFreqSpec = sa.getFreqSpectrumAll();
		this.siteFreqSpecEach = sa.getFreqSpectrumEach();
		siteChiDist = FrequencyStat.calChiDiff(siteFreqSpec);
	}
	private void addSitePattern(SiteAlignment sa) {
		this.sitePattern = sa.calSitePattern();
	}

	public double calDelta(AlignmentStat obsStat) {
		calSumStat();
		double[] obsStatAll = obsStat.getStatAll();
		double delta = 0;
		for (int i = 0; i < statAll.length; i++) {
			//
			// delta += Math.abs(statAll[i]-obsStatAll[i])/obsStatAll[i];
			double dif = statAll[i] - obsStatAll[i];
			double s = Math.abs(dif) / obsStatAll[i];
			delta += s;
			// System.out.print(statAll[i] +"\t");
			// System.out.print(
			// Math.abs(statAll[i]-obsStatAll[i])/obsStatAll[i] + "\t");
			// System.out.print( (dif*dif)/obsStatAll[i] + "\t");
		}
		// System.out.println();
		delta /= statAll.length;
		return delta;

	}

	public void calSumStat() {
		// TODO fix confusing names
		if (sumStat instanceof SStatFreq) {
			statAll = sumStat.calStat(siteDists, siteChiDist);
		} else if (sumStat instanceof SStatBrLn) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					siteBrLn);
		} else if (sumStat instanceof SStat3TMoreStat) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					siteKurtosis, siteSecondM, siteSkewness);
		} else if (sumStat instanceof SStat3TMoreStatNoInt) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					siteKurtosis, siteSecondM, siteSkewness);
		} else if (sumStat instanceof SStatTopFreq) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					siteFreqSpecEach[0], siteFreqSpecEach[1] );
		}else if (sumStat instanceof SStatTopFreqSingleSum) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					siteFreqSpecEach[0], siteFreqSpecEach[1] );
		}else if (sumStat instanceof SStatTopFreqSingleProduct) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					siteFreqSpecEach[0], siteFreqSpecEach[1] );
		}else if (sumStat instanceof SStatTopFreqSingleProductNoS) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					siteFreqSpecEach[0], siteFreqSpecEach[1] );
		}else if (sumStat instanceof SStatSitePattern) {
			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
					sitePattern[0], siteFreqSpecEach[0], siteFreqSpecEach[1] );
		}
	}

	public void addBrLn(double[] readBrLn) {
		this.siteBrLn = readBrLn;
		// newStat.addBrLn(readBrLn(setting));
	}

	@Override
	public String toString() {
		return "AlignmentStat [statAll=" + Arrays.toString(statAll) + "]";
	}

	public double[] getSiteSecondM() {

		return siteSecondM;
	}

	public double[] getSiteSkewness() {

		return siteSkewness;
	}

	public double calMultiDelta(AlignmentStat obsStat) {
		calSumStat();
		double[][] obsStatAll = obsStat.getMultiStatAll();
		double delta = 0;

		for (int i = 0; i < statAll.length; i++) {
			int localIndex = calLocalIndex(parIndex, i);
			double dif = statAll[i] - obsStatAll[localIndex][i];
			double s = Math.abs(dif) / obsStatAll[localIndex][i];
			
			delta += s;
		}

		delta /= statAll.length;
		return delta;

	}

	private int calLocalIndex(double[] pI, int i) {
		int index = -1;

		if (i == 0) {
			index = calLocalMu(pI[i]);
		} else if (i == 1) {
			index = calLocalTheta(pI[i]);
		}
		return index;
	}

	private int calLocalMu(double p) {
		int index = -1;
		if( p<0.00515) {//0
			index = 0;
		}else if(p>= 0.00515 && p<0.00655 ){//1
			index = 1;	
		}else if(p>= 0.00655 && p<0.00795 ){//2
			index = 2;	
		}else if(p>= 0.00795 && p<0.00935 ){//3
			index = 3;
		}else if(p>= 0.00935 && p< 0.01075){//4
			index = 4;
		}else if(p>= 0.01075 && p< 0.01215){//5
			index = 5;
		}else if(p>= 0.01215 && p< 0.01355){//6
			index = 6;
		}else if(p>= 0.01355 ){//7
			index = 7;
		}	
		return index;
	}

	private int calLocalTheta(double p) {

		int index = -1;
		if (p < 1500) {
			index = 0;
		} else if (p >= 1500 && p < 2000) {
			index = 1;

		} else if (p >= 2000 && p < 2500) {

			index = 2;
		} else if (p >= 2500 && p < 3000) {
			index = 3;

		} else if (p >= 3000 && p < 3500) {
			index = 4;

		} else if (p >= 3500 && p < 4000) {
			index = 5;

		} else if (p >= 4000 && p < 4500) {
			index = 6;

		} else if (p >= 4500) {
			index = 7;
		}

		return index;
	}

	public void calMultiObsStat() {
		multiStatAll = new double[8][2];
		if (sumStat instanceof SStatLocal) {
			double[] parIndex = new double[2];
			for (int i = 0; i < 8; i++) {
				parIndex[0] = i *0.0015+ 0.00375;
				parIndex[1] = i * 500 + 1000;
				multiStatAll[i] = sumStat.calStat(siteDists, siteChiDist,
						siteVarinace, parIndex);
System.out.println(Arrays.toString(multiStatAll[i]));
			}

		}

	}

	// private void addSiteVar(double[] siteVar) {
	// this.siteVarinace = siteVar;
	// // calDistStat();
	// }
	
	@Deprecated
	private void calDistStat() {
		if (sumStat != null) {
			double[] statDist = sumStat.calStat(siteDists);
		}
	}

	@Deprecated
	private void addSiteFreqSpec(double[][] siteSpec) {
		this.siteFreqSpec = siteSpec;
		siteChiDist = FrequencyStat.calChiDiff(siteFreqSpec);
		// calChiDiff();
		// double[] chiStat = FrequencyStat.calChiDiff(this.s)
	}

	public double calIndStat(AlignmentStat obsStat) {

		double[] delta = null;; 
		
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(siteDists, obsStat.getSiteDists()));
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(siteChiDist, obsStat.getSiteChiDist()));
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(siteVarinace, obsStat.getSiteVarinace()));
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(sitePattern[0], obsStat.getSitePattern()[0]));

//		return StatUtils.max(delta);
		return StatUtils.mean(delta);
	}

	public double calIndStat2(AlignmentStat obsStat) {

		double[] delta = null;; 
//		System.out.println( Arrays.toString(obsStat.getSiteDists()));
//		System.out.println( Arrays.toString(obsStat.getSiteChiDist()));
//		System.out.println( Arrays.toString(obsStat.getSiteVarinace()));
//		System.out.println( Arrays.toString(obsStat.getSitePattern()[0]));
		
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(siteDists, obsStat.getSiteDists()));
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(siteChiDist, obsStat.getSiteChiDist()));
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(siteVarinace, obsStat.getSiteVarinace()));
		delta = ArrayUtils.addAll(delta, colRelaDiffAll(sitePattern[0], obsStat.getSitePattern()[0]));
//Arrays.sort(delta);
//		return StatUtils.max(delta);
		System.out.println(Arrays.toString(delta));
		return StatUtils.mean(delta);
	}
	private double[] colRelaDiffAll(double[] ob, double[] ex) {
		double[] tempDiff = new double[ob.length];
		for (int i = 0; i < ob.length; i++) {
			tempDiff[i] = calRelaDiff(ob[i], ex[i]);
		}
		return tempDiff; 
	
	}
	
	private double calRelaDiff(double ob, double ex) {
//		double diff = (Math.abs(ob - ex ));
		double diff = (ob - ex );
		diff = diff*diff;
		return diff==0 ? 0 : diff/ex;
	}


}
