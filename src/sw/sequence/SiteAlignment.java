package sw.sequence;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;

import sw.main.Setup;
import sw.math.Combination;

import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.SimpleAlignment;
import dr.evolution.datatype.Nucleotides;

public class SiteAlignment {

	private final static int DNASTATCOUNT = 4; 

	private int noSeq;
	private int noState;
	
	private Alignment ali;
	private int noSite; 
	private int noTime;
	private ArrayList<int[]> timeGroup;
	private ArrayList<SiteAlignPerTime> st = new ArrayList<SiteAlignPerTime>();
	// private

	public SiteAlignment(Alignment ali) {
		
		this.ali = ali;
		noSite = ali.getSiteCount(); // 750 length
		noSeq = ali.getSequenceCount(); // 80 = 40*2  
		noState = ali.getStateCount(); // 4
		timeGroup = new ArrayList<int[]>();

	}

	public SiteAlignment(int[] info) {
		this(info[0], info[1], DNASTATCOUNT);
	}
	
	public SiteAlignment(int noSite, int noSeq) {
		this(noSite, noSeq, DNASTATCOUNT);
	}

	public SiteAlignment(int noSite, int noSeq, int noState) {

		this.noSite = noSite; // 750 length
		this.noSeq = noSeq; // 80 = 40*2  
		this.noState = noState; // 4
		timeGroup = new ArrayList<int[]>();

	}
	
	public SiteAlignment(Setup setting) {
		this(setting.getAlignmentInfo());;
		addAllTimeGroup(setting.setupTimeGroup());	
		
		for (int i = 0; i < noTime; i++) {
			st.add(new SiteAlignPerTime(noSite));
		}
//		ArrayList<Site> allSite0 = Site.init(setting.getSeqLength());
//		ArrayList<Site> allSite1 = Site.init(setting.getSeqLength());
		
	}

	public void updateAlignment(Importer imp) {
		
		try {
			this.ali = imp.importAlignment();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < noTime; i++) {
			st.get(i).addAlignment(getTimeGroup(i));
		}
		calAllFreq();
		calAllSpectrum();
		
	}
	
	public void updateAlignment(Alignment ali) {
		this.ali = ali;
		for (int i = 0; i < noTime; i++) {
			st.get(i).addAlignment(getTimeGroup(i));
		}
		calAllFreq();
		calAllSpectrum();
		
	}



	public void addAllTimeGroup(int[]... t) {

		this.noTime = t.length;
		for (int i = 0; i < noTime; i++) {
			timeGroup.add(t[i]);
//			addTimeGroup(t[i]);
		}
	}
	
	public void addTimeGroup(int[] t) {
		timeGroup.add(t);		
	}

	public SimpleAlignment getTimeGroup(int index){
		
		int[] t = timeGroup.get(index);
		SimpleAlignment simpA = new SimpleAlignment();
		simpA.setDataType(Nucleotides.INSTANCE);
		for (int i : t) {
			simpA.addSequence(ali.getSequence(i));	
		}
		
		return simpA;
	}
	
	

	public String summary() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("No Seq: ").append(getSequenceCount()).append(" No Site: ").append(getSiteCount())
		.append(" No of time group: ").append(getNoTimeGroup());
		
		return sb.toString();
		
	}

	public static ArrayList<Site> calcFreq(SimpleAlignment sa, ArrayList<Site> allSites) {
	
			int noState = sa.getStateCount();
			double[] freqs = new double[noState];
			int[] pattern;
	//		int[] freqCount = new int[noState];
			int[] freqCount = new int[18];
			int i, j;
			int noSite = sa.getSiteCount();
			int noSeq = sa.getSequenceCount();
//			ArrayList<Site> allSites = new ArrayList<Site>(noSite);
//			Site s = new Site();
			
			for (i = 0; i < noSite; i++) {
				freqs = new double[noState];
				freqCount = new int[18];
				pattern = sa.getPattern(i);
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
			return allSites;
		}


	
	private int getSequenceCount() {
		return ali.getSequenceCount();
	}

	private int getSiteCount() {
		return ali.getSiteCount();
	}

	public int getNoTimeGroup() {
		return timeGroup.size();
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
		for (int i = 0; i < noTime; i++) {
			st.get(i).calcFreq();
		}
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
		
		
		int[][] comb = Combination.ListCombination(noTime);

		double[] dist = new double[comb.length+noTime];
		//Inter then intra
		for (int i = 0; i < comb.length; i++) {
			dist[i] = st.get(comb[i][0]).calInterDist(st.get( comb[i][1] ));
		}
		for (int i = 0; i < noTime; i++) {
			dist[i+comb.length] = st.get(i).calIntraDist();
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


}
