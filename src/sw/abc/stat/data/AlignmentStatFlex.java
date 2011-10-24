package sw.abc.stat.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.util.MathUtils;
import org.hamcrest.internal.ArrayIterator;

import com.google.common.primitives.Doubles;

import dr.inference.trace.Trace;

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
import sw.sequence.Site;
import sw.sequence.SiteAlignment;
import sw.util.TraceUtil;

public class AlignmentStatFlex {

	//
	// private double statMu;
	// private double statTheta;

	private SummaryStat sumStat;

	// Alignment properties
//	private double[][] siteFreqSpec;
	private double[][] siteFreqSpecEach;
	
	private StatArray siteDists;
	private StatArray siteVarinace;
	private StatArray siteChiDist;


	private double[] statAll;

	private StatArray sitePattern;


	public double[] getStatAll() {
		return statAll;
	}


	public AlignmentStatFlex(SummaryStat sumStat) {
		this.sumStat = sumStat;
	}


	public AlignmentStatFlex(Setup setting) {
		this(setting.getStat());
	}

	public double[] getSiteChiDist() {
		return siteChiDist.getStats();
	}
	
	public double[] getSiteVarinace() {
		return siteVarinace.getStats();
	}

	public double[] getSiteDists() {
		return siteDists.getStats();
	}

	public double[][] getSiteFreqSpecEach() {
		return siteFreqSpecEach;
	}
	public double[] getSitePattern() {
		return sitePattern.getStats();
	}


	
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


	private void addSiteDists(SiteAlignment sa) {
		this.siteDists.setName("chisq");
		this.siteDists.setStats(sa.calDists() );

	}

	private void addSiteVar(SiteAlignment sa) {
		this.siteVarinace.setName("var");
		this.siteVarinace.setStats( sa.getVar() );
	}

	private void addSiteFreqSpec(SiteAlignment sa) {

		double[][] siteFreqSpec = sa.getFreqSpectrumAll();
		this.siteFreqSpecEach = sa.getFreqSpectrumEach();
		this.siteChiDist.setStats( FrequencyStat.calChiDiff(siteFreqSpec) );
	}
	
	private void addSitePattern(SiteAlignment sa) {
		double[][] stat2d = sa.calSitePattern();
		this.sitePattern.setStats( Doubles.concat(stat2d) );
	}

	public double calDelta(AlignmentStatFlex obsStat) {
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
//		if (sumStat instanceof SStatFreq) {
//			statAll = sumStat.calStat(siteDists, siteChiDist);
//		} else if (sumStat instanceof SStatTopFreq) {
//			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
//					siteFreqSpecEach[0], siteFreqSpecEach[1] );
//		}else if (sumStat instanceof SStatTopFreqSingleSum) {
//			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
//					siteFreqSpecEach[0], siteFreqSpecEach[1] );
//		}else if (sumStat instanceof SStatTopFreqSingleProduct) {
//			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
//					siteFreqSpecEach[0], siteFreqSpecEach[1] );
//		}else if (sumStat instanceof SStatTopFreqSingleProductNoS) {
//			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
//					siteFreqSpecEach[0], siteFreqSpecEach[1] );
//		}else if (sumStat instanceof SStatSitePattern) {
//			statAll = sumStat.calStat(siteDists, siteChiDist, siteVarinace,
//					sitePattern[0], siteFreqSpecEach[0], siteFreqSpecEach[1] );
//		}
	}

	@Override
	public String toString() {
		return "AlignmentStat [statAll=" + Arrays.toString(statAll) + "]";
	}









}
