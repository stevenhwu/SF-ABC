package sw.abc.stat.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
	
	private StatArray siteDists = new StatArray("dist");
	private StatArray siteVarinace = new StatArray("var");
	private StatArray siteChiDist = new StatArray("chisq");
	private StatArray sitePattern = new StatArray("sitePattern");

	private String[] statsList;
	private double[] summaryStatAll;
	
	private HashMap<String, StatArray> siteStats = new HashMap<String, StatArray>(); 



	public AlignmentStatFlex(Setup setting) {
		this.sumStat = setting.getStat();
		this.statsList = setting.getStatList();
		siteStats.put("dist", siteDists);
		siteStats.put("chisq", siteChiDist);
		siteStats.put("var", siteVarinace);
		siteStats.put("sitePattern", sitePattern);
	}

	
	public double[] getSummaryStatAll() {
		return summaryStatAll;
	}

	public StatArray getStat(String key){
		return siteStats.get(key);
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

		this.siteDists.setStats(sa.calDists() );

	}

	private void addSiteVar(SiteAlignment sa) {

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
		double[] obsStatAll = obsStat.getSummaryStatAll();
		double delta = 0;
		for (int i = 0; i < summaryStatAll.length; i++) {
			//
			// delta += Math.abs(statAll[i]-obsStatAll[i])/obsStatAll[i];
			double dif = summaryStatAll[i] - obsStatAll[i];
			double s = Math.abs(dif) / obsStatAll[i];
			delta += s;
			// System.out.print(statAll[i] +"\t");
			// System.out.print(
			// Math.abs(statAll[i]-obsStatAll[i])/obsStatAll[i] + "\t");
			// System.out.print( (dif*dif)/obsStatAll[i] + "\t");
		}
		// System.out.println();
		delta /= summaryStatAll.length;
		return delta;

	}

	public void calSumStat() {
		summaryStatAll = sumStat.calStat(getCurStat(statsList));
	}

	@Override
	public String toString() {
		return "ObsStat= " + Arrays.toString(summaryStatAll) ;
	}


	public double[] getCurStat(String[] statsList) {
		
		double[] stat = new double[0];
		for (String key : statsList) {
			stat = Doubles.concat(stat, siteStats.get(key).getStats());
		}
		return stat;
		
	}




}
