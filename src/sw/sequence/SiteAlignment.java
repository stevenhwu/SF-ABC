package sw.sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.alignments.BasicAlignment;
import jebl.evolution.sequences.BasicSequence;
import jebl.evolution.sequences.Sequence;
import jebl.evolution.sequences.SequenceType;
import jebl.evolution.taxa.Taxon;
import sw.main.Setting;
import sw.math.Combination;
import dr.evolution.alignment.SimpleAlignment;


public class SiteAlignment {

	private final static int DNASTATCOUNT = 4; 

//	private int noSeq;
//	private int noState;
	
	private Alignment ali;
	private int noSite; 
	private int noTime;
//	private ArrayList<int[]> timeGroup;
	private ArrayList<SiteAlignPerTime> st = new ArrayList<SiteAlignPerTime>();

	private double timeGap;
	private double[] timeGroups;
	// private

	

	private int[][] combs;

	public SiteAlignment(Setting setting) {
		this.noTime = setting.getNoTime();
		this.noSite = setting.getSeqLength();
		setTimeGroups(setting.getTimeGap());

		combs = Combination.ListCombination(noTime);
	}
	
	private void setTimeGroups(double timeGap) {
		this.timeGap = timeGap;
		timeGroups = new double[noTime];
		double time = 0;
		for (int i = 0; i < timeGroups.length; i++) {
			timeGroups[i] = time;
			time += this.timeGap;
		}
	}
	public SimpleAlignment convertJEBLAlignmentToDrAlignment(Alignment jeblAlignment) {
		
		SimpleAlignment drAlignment = new SimpleAlignment();
		List<Sequence> allSeq = jeblAlignment.getSequenceList();
		for (Sequence seq : allSeq) {
			String actualSeq = seq.getString();
			dr.evolution.sequence.Sequence drSeq = new dr.evolution.sequence.Sequence(actualSeq);
			drAlignment.addSequence(drSeq);
		}
		return drAlignment;
	}
	
	public BasicAlignment convertDrAlignmentToJEBLAlignment(dr.evolution.alignment.Alignment drAlignment) {
		
		BasicAlignment jAlignment = new BasicAlignment();
		int noSeq = drAlignment.getSequenceCount();
//		List<dr.evolution.util.Taxon> drTaxon = drAlignment.asList();
		Taxon t;
		for (int i = 0; i < noSeq; i++) {
			dr.evolution.sequence.Sequence drSeq = drAlignment.getSequence(i);
			String actualSeq = drSeq.getSequenceString();
			
			if(drSeq.getTaxon()==null){
				int timeIndex = i<(noSeq/noTime)? 0:1; 
				t = Taxon.getTaxon(i+"."+timeGroups[timeIndex] );
			}
			else {
				t = Taxon.getTaxon(drSeq.getTaxon().toString() );	
			}
			
			BasicSequence jSeq = new BasicSequence(SequenceType.NUCLEOTIDE, t, actualSeq);
			jAlignment.addSequence(jSeq);
		}

		return jAlignment;
	}
	
	private void parseAlignment() {
		
		List<Sequence> allSeq = ali.getSequenceList();

		List<Sequence>[] allSeqArray = new ArrayList[noTime];
//		Arrays.fill(allSeqArray, new ArrayList<Sequence>() ); slower
		for (int i = 0; i < allSeqArray.length; i++) {
			allSeqArray[i] = new ArrayList<Sequence>();
		}

		for (Sequence seq : allSeq) {
			String name = seq.getTaxon().getName();
			int index = parseTaxonNameToTime(name);
			allSeqArray[index].add(seq); 

		}
		st = new ArrayList<SiteAlignPerTime>();
		for (int i = 0; i < noTime; i++) {
			st.add(new SiteAlignPerTime(noSite, allSeqArray[i]));
		}

//		st.get(index).addSequence(seq);
	}
	
	private int parseTaxonNameToTime(String name) {
		int index = -1;
		for (int i = 0; i < noTime; i++) {
			if (name.endsWith("." + timeGroups[i]) | name.endsWith("_" + timeGroups[i])) {
				index = i;
				break;
			}
		}

		return index;
	}
	private int getPatternCount() {
		return ali.getPatternCount();
	}

	private int getSiteCount() {
		return ali.getSiteCount();
	}


	public ArrayList<Site> calcFreqTime(int t, ArrayList<Site> allSite) {
		return calcFreq(getTimeGroup(t), allSite);
	}

	public double[][] calSitePattern() {
	 
		int[][] comb = Combination.ListCombination(noTime);

		double[][] pattern = new double[comb.length][];
		for (int i = 0; i < comb.length; i++) {
			pattern[i] = st.get(comb[i][0]).calSitePattern(st.get( comb[i][1] ));

		}
		return pattern;
	}
	

	private void calAllFreq() {
//		st.size();
		for (SiteAlignPerTime stTime : st) {
			stTime.calcFreq();
		}
//		for (int i = 0; i < noTime; i++) {
//			st.get(i).calcFreq();
//		}
	}
	
	private void calAllSpectrum() {
		for (int i = 0; i < noTime; i++) {
			st.get(i).calcSpectrum();
		}

	}
	
//	public int[][] getAllSpectrum() {
//		int[][] allSpec = new int[noTime][];
//		for (int i = 0; i < noTime; i++) {
//			allSpec[i] = st.get(i).getSiteSpecturm();
//		}
//		return allSpec;
//	}
	public double[][] getFreqSpectrumAll() {
		double[][] allSpec = new double[noTime][];
		for (int i = 0; i < noTime; i++) {
			allSpec[i] = st.get(i).getSiteSpecturm();
		}
		return allSpec;
	}
	
	public double[][] getFreqSpectrumEach() {
		double[][] allSpec = new double[noTime][];
		for (int i = 0; i < noTime; i++) {
			allSpec[i] = st.get(i).getSiteSpecturmEach();
		}
		return allSpec;
	}
//	public double[] getFreqSpectrumTime(int time) {
//		double[] allSpec = st.get(time).getSiteSpecturm();
//		
//		return allSpec;
//	}

	
	public double[] calDists() {
		

		double[] dist = new double[combs.length+noTime];
		//Inter then intra
		for (int i = 0; i < combs.length; i++) {
			dist[i] = st.get(combs[i][0]).calInterDist(st.get( combs[i][1] ));
		}
		for (int i = 0; i < noTime; i++) {
			dist[i+combs.length] = st.get(i).calIntraDist();
		}

		return dist;
	}

	public void setNoTime(int noTime) {
		this.noTime = noTime;
	}

	public double[] getVar() {
		double[] var = new double[noTime];
		for (int i = 0; i < noTime; i++) {
			var[i] = st.get(i).calVar();
		}

		return var;
	}
	
	public double[] getCovar() {
		double[] covar = new double[combs.length];
		for (int i = 0; i < noTime; i++) {
			covar[i] = st.get(combs[i][0]).calCovar(st.get( combs[i][1] ));
		}

		return covar;
	}

	
	public double[] getKurtosis() {
		double[] kurt = new double[noTime];
		for (int i = 0; i < noTime; i++) {
			kurt[i] = st.get(i).calKurtosis();
		}

		return kurt;
	}
	
	public double[] getSecondM() {
		double[] secondM = new double[noTime];
		for (int i = 0; i < noTime; i++) {
			secondM[i] = st.get(i).cal2ndMoment();
		}

		return secondM;
	}
	public double[] getSkewness() {
		double[] kurt = new double[noTime];
		for (int i = 0; i < noTime; i++) {
			kurt[i] = st.get(i).calSkewness();
		}

		return kurt;
	}
	
	public void updateJEBLAlignment(Alignment jeblAlignment){
		this.ali = jeblAlignment;
		parseAlignment();
		calAllFreq();
		calAllSpectrum();

	}

	public void updateAlignment(List<Alignment> importAlignments) {
		
		if( importAlignments.size() == 1){
			updateJEBLAlignment(importAlignments.get(0));
			List<Sequence> s = importAlignments.get(0).getSequenceList();
	
		}
		else{
			System.err.println("Multiple Alignments"+"\t"+ importAlignments.size());
			System.exit(-1);
		}
		
	}

	@Deprecated
	public SiteAlignment(dr.evolution.alignment.Alignment alignment,
			int noTime, double timeGap, boolean isJUnit) {

//		this.isJUnit = isJUnit;
		this.ali = convertDrAlignmentToJEBLAlignment(alignment);
		noSite = alignment.getSiteCount(); // 750 length
		// noSeq = ali.getSequenceCount(); // 80 = 40*2
		this.noTime = noTime;
		setTimeGroups(timeGap);
		// timeGroup = new ArrayList<int[]>();
		parseAlignment();
		// for (int i = 0; i < noTime; i++) {
		// st.get(i).addAlignment(getTimeGroup(i));
		// }
		calAllFreq();
		calAllSpectrum();
	}

	// public SiteAlignment(int[] info) {
	// this(info[0], info[1], DNASTATCOUNT);
	// }
	// @Deprecated
	// public SiteAlignment(int noSite, int noSeq) {
	// this(noSite, noSeq, DNASTATCOUNT);
	// }
	// @Deprecated
	// public SiteAlignment(int noSite, int noSeq, int noState) {
	//
	// this.noSite = noSite; // 750 length
	// // this.noSeq = noSeq; // 80 = 40*2
	// // this.noState = noState; // 4
	// timeGroup = new ArrayList<int[]>();
	//
	// }

	@Deprecated
	public void updateAlignment(Importer imp) {

		try {

			this.ali = convertDrAlignmentToJEBLAlignment(imp.importAlignment());
		} catch (Exception e) {
			e.printStackTrace();
		}
		parseAlignment();

		calAllFreq();
		calAllSpectrum();

	}

	@Deprecated
	public void updateAlignment(dr.evolution.alignment.Alignment sa) {

		this.ali = convertDrAlignmentToJEBLAlignment(sa);
		parseAlignment();
		// for (int i = 0; i < noTime; i++) {
		// st.get(i).addAlignment(getTimeGroup(i));
		// }
		calAllFreq();
		calAllSpectrum();

	}

	//
	// @Deprecated
	// public void addAllTimeGroup(int[]... t) {
	//
	// this.noTime = t.length;
	// for (int i = 0; i < noTime; i++) {
	// timeGroup.add(t[i]);
	// // addTimeGroup(t[i]);
	// }
	// }
	// @Deprecated
	// public void addTimeGroup(int[] t) {
	// timeGroup.add(t);
	// }

	@Deprecated
	public SimpleAlignment getTimeGroup(int index) {

		BasicAlignment tempA = st.get(index).getAlignment();
		SimpleAlignment sa = convertJEBLAlignmentToDrAlignment(tempA);
		// int[] t = timeGroup.get(index);
		// SimpleAlignment simpA = new SimpleAlignment();
		//
		// for (int i : t) {
		// simpA.addSequence(ali.getSequence(i));
		// }
		// jebl.evolution.alignments.Alignment a = new
		return sa;
	}

	//

	@Deprecated
	public String summary() {

		StringBuilder sb = new StringBuilder();
		sb.append("No Seq: ").append(getPatternCount()).append(" No Site: ")
				.append(getSiteCount()).append(" No of time group: ")
				.append(Arrays.toString(timeGroups));

		return sb.toString();

	}


	@Deprecated
	public static ArrayList<Site> calcFreq(SimpleAlignment sa,
			ArrayList<Site> allSites) {

		int noState = sa.getStateCount();
		double[] freqs = new double[noState];
		int[] pattern;
		// int[] freqCount = new int[noState];
		int[] freqCount = new int[18];
		int i, j;
		int noSite = sa.getSiteCount();
		int noSeq = sa.getSequenceCount();
		// ArrayList<Site> allSites = new ArrayList<Site>(noSite);
		// Site s = new Site();

		for (i = 0; i < noSite; i++) {
			freqs = new double[noState];
			freqCount = new int[18];
			pattern = sa.getPattern(i);
			for (j = 0; j < noSeq; j++) {
				freqCount[pattern[j]]++;
			}
			double sum = 0.0 + freqCount[0] + freqCount[1] + freqCount[2]
					+ freqCount[3];
			for (int k = 0; k < 4; k++) {
				freqs[k] = freqCount[k] / sum;
			}

			// s.updateSite(i, freqs);
			allSites.get(i).updateSite(freqs);

		}
		return allSites;
	}
	

}
