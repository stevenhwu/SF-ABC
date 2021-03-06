package sw.abc.stat.data;

import java.util.Arrays;
import java.util.HashMap;

import jebl.evolution.alignments.Alignment;
import sw.abc.stat.summary.SummaryStat;
import sw.main.Setting;
import sw.sequence.SiteAlignment;

import com.google.common.primitives.Doubles;

public class AlignmentStatFlex {

	private SummaryStat sumStat;

	// Alignment properties
	// private double[][] siteFreqSpec;
	private double[][] siteFreqSpecEach;

	private StatArray siteDists = new StatArray("dist");
	private StatArray siteVarinace = new StatArray("var");
	private StatArray siteCovarinace = new StatArray("covar");
	private StatArray siteChiDist = new StatArray("chisq");
	private StatArray sitePattern = new StatArray("sitePattern");

	private double[] obsStat;
	private double[] summaryStatAll;
	private String[] statsList;

	private HashMap<String, StatArray> siteStats = new HashMap<String, StatArray>();
	private SiteAlignment siteAlig;



	public AlignmentStatFlex(Setting setting) {
		
		this.sumStat = setting.getSummaryStat();
		this.statsList = setting.getStatList();
		this.siteAlig = new SiteAlignment(setting);
		
		siteStats.put("dist", siteDists);
		siteStats.put("chisq", siteChiDist);
		siteStats.put("var", siteVarinace);
		siteStats.put("covar", siteCovarinace);
		siteStats.put("sitePattern", sitePattern);
		
	}

	
	public double[] getSummaryStatAll() {
		return summaryStatAll;
	}

	public StatArray getStat(String key){
		return siteStats.get(key);
	}
	

	
//	// TODO fix adding/cal/updating/preprocessing then cal stat
//	public void updateSiteAlignment(SiteAlignment sa) {
//		siteAlig = sa;
//		updateStieStat();
//
//	}

	public void updateAlignmentAndStat(Alignment jeblAlignment) {
		updateAlignment(jeblAlignment);
		calSumStat();		
	}

	
	public void updateAlignment(Alignment jeblAlignment) {
		siteAlig.updateJEBLAlignment(jeblAlignment);
		updateStieStat();
	}

	private void updateStieStat(){
		addSiteDists();
		addSiteFreqSpec();
		addSiteVar();
		addSiteCovar();
		addSitePattern();
		
	}

	private void addSiteDists() {
		
		this.siteDists.setStats(siteAlig.calDists() );

	}

	private void addSiteFreqSpec() {
		double[][] siteFreqSpec = siteAlig.getFreqSpectrumAll();
		this.siteFreqSpecEach = siteAlig.getFreqSpectrumEach();
		this.siteChiDist.setStats( FrequencyStat.calChiDiff(siteFreqSpec) );
		
	}


	private void addSiteVar() {
		this.siteVarinace.setStats( siteAlig.getVar() );
	}

	private void addSiteCovar() {
		this.siteCovarinace.setStats( siteAlig.getCovar() );
	}
	
	private void addSitePattern() {
		double[][] stat2d = siteAlig.calSitePattern();
		this.sitePattern.setStats( Doubles.concat(stat2d) );
		
	}

	public double calDeltaSep(AlignmentStatFlex obsStat, int p) {
		calSumStat();
		double obsStatP = obsStat.getSummaryStatAll()[p];

		double dif = summaryStatAll[p] - obsStatP;
		double delta = Math.abs(dif / obsStatP);
			
			// System.out.print(statAll[i] +"\t");
			// System.out.print(
			// Math.abs(statAll[i]-obsStatAll[i])/obsStatAll[i] + "\t");
			// System.out.print( (dif*dif)/obsStatAll[i] + "\t");

		return delta;

	}
//	
	public double calDelta() {
//		calSumStat();
//		double[] obsStatAll = obsStat.getSummaryStatAll();
//		System.out.println(Arrays.toString(summaryStatAll));
		double delta = 0;
		for (int i = 0; i < summaryStatAll.length; i++) {
			//
			// delta += Math.abs(statAll[i]-obsStatAll[i])/obsStatAll[i];

			double s = calAbsDiff(obsStat[i], summaryStatAll[i]);
//			System.out.println(s+"\t"+summaryStatAll[i] +"\t"+ obsStat[i]+"\t"); 
			delta += s;
			 
			// System.out.print(
			// Math.abs(statAll[i]-obsStatAll[i])/obsStatAll[i] + "\t");
			// System.out.print( (dif*dif)/obsStatAll[i] + "\t");
		}
		// System.out.println();
		delta /= summaryStatAll.length;
		return delta;

	}


	public double calDelta(int i) {

		double delta = calAbsDiff(obsStat[i], summaryStatAll[i]);
		
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
//		System.out.println(Arrays.toString(stat));
		return stat;
		
	}


	public void setObsStat(double[] obsStat) {
		this.obsStat = obsStat;
		
	}


	public static double calAbsDiff(double expectStat, double obsStat){

		double diff = (obsStat - expectStat);
		diff *= diff;
		double stat = Math.abs ( (diff) / expectStat ); 

		return stat;
	}

}
