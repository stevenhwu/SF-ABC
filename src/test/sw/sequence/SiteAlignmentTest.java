package test.sw.sequence;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.sequences.Sequence;
import jebl.evolution.taxa.Taxon;

import org.junit.Before;
import org.junit.Test;

import sw.main.Setting;
import sw.sequence.Importer;
import sw.sequence.Site;
import sw.sequence.SiteAlignment;
import sw.simulator.SSC;
import dr.evolution.alignment.SimpleAlignment;
import dr.evolution.io.NexusImporter;

public class SiteAlignmentTest {

	public static String sysSep = System.getProperty("file.separator");
	public static String userDir = System.getProperty("user.dir");
	SiteAlignment saOld;
	SiteAlignment sa;
	
	
	@Before
	public void setUp() throws Exception {
		setUpA();
		setUpB();
	}
	private void setUpA() throws Exception{
		String fileName = "junit.paup";
		File dataDir = new File(userDir+sysSep+"data"+sysSep);
		String alignmentFileName = dataDir.toString()+sysSep+fileName;
		NexusImporter importer;
		int noTime = 2;
		double timeGap = 400;
		try {
			importer = new NexusImporter(new FileReader(alignmentFileName));
			saOld = new SiteAlignment(importer.importAlignment(), noTime, timeGap, true);
		} catch (Exception e) {
			e.printStackTrace();
		}


//		int[] t1 = new int[noTime];
//		for (int i = 0; i < t1.length; i++) {
//			t1[i] = i;
//		}
//
//		int[] t2 = new int[noTime];
//		for (int i = 0; i < t2.length; i++) {
//			t2[i] = i+40;
//		}
//
//		saOld.addTimeGroup(t1);
//		saOld.addTimeGroup(t2);
		
	}
	private void setUpB() throws Exception{		
		
		String dataName = "junit.paup";
//		String dataDir = userDir+sysSep+"data"+sysSep;
		String dataDir = "/home/sw167/workspace/SF-ABC/data";
		Setting setting = new Setting(dataDir, dataDir, dataName);
//		setting.setObsFile(dataName);
		int noSeqPerTime = 40;
		int noTime = 2;
		int seqLength = 750;
		int timeGap = 400;
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime, timeGap);
		setting.setTimeGap(400);
		sa = new SiteAlignment(setting);
		Importer imp = new Importer(setting.getDataFile(), noSeqPerTime*noTime);
		try {
			sa.updateAlignment(imp.importAlignment());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCalcFreq() {

		SimpleAlignment simpleA0 = saOld.getTimeGroup(0);
		SimpleAlignment simpleA1 = saOld.getTimeGroup(1);
		
		ArrayList<Site> allSite0 = Site.init(750);
		ArrayList<Site> allSite1 = Site.init(750);

		allSite0 = SiteAlignment.calcFreq(simpleA0, allSite0);
		allSite1 = SiteAlignment.calcFreq(simpleA1, allSite1);
		
		assertEquals(0.059155, Site.pairDist(allSite0, allSite1), 1E-6);
		assertEquals(0.053313, Site.pairDist(allSite0), 1E-6);
		assertEquals(0.0579083, Site.pairDist(allSite1), 1E-6);
	}

	@Test
	public void testCalcFreqTime() {
		
		ArrayList<Site> allSite0 = Site.init(750);
		ArrayList<Site> allSite1 = Site.init(750);
		
		allSite0 = saOld.calcFreqTime(0, allSite0);
		allSite1 = saOld.calcFreqTime(1, allSite1);

		assertEquals(0.059155, Site.pairDist(allSite0, allSite1), 1E-6);
		assertEquals(0.053313, Site.pairDist(allSite0), 1E-6);
		assertEquals(0.0579083, Site.pairDist(allSite1), 1E-6);

	}


	@Test
	public void testCalcFreqWithSitePerTimeClass() {
		
		double[] dists = sa.calDists();
		double[] exp = {0.059155, 0.053313, 0.0579083};
		assertArrayEquals(exp, dists, 1E-5);
				
	}
	
	
	@Test
	public void testCalcVar() {
		
		double[] siteVar = sa.getVar();
		double[] exp = {0.01457378, 0.01952424};
		assertArrayEquals(exp, siteVar, 1E-5);
		
		
		
	}
	
	@Test
	public void testCalcSiteSpectrum() {
		
		double[][] freqSpec = sa.getFreqSpectrumAll();
		double[][] exp = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 6, 7, 1, 57, 5, 6, 44,
						36, 583 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 45, 4, 8, 7, 5, 9, 27,
						47, 587 } };
		for (int i = 0; i < exp.length; i++) {
			for (int j = 0; j < exp[i].length; j++) {
				exp[i][j] = exp[i][j]/750;
			}
		}
		//	57, 5(8), 6(3), 44,
		// check rounding error  8, 7, 5(12), 9(2), 27 

		assertArrayEquals(exp[0], freqSpec[0] , 0.01);
		assertArrayEquals(exp[1], freqSpec[1] , 0.01);
		
		
	}
	@Test
	public void testCalcSitePattern() {
		
		double[][] sitePattern = sa.calSitePattern();
		double[][] exp = {{0.704, 0.152, 0.140, 0.004, 0.0}};

//		for (int i = 0; i < exp.length; i++) {
//			System.out.println(ArrayUtils.toString(sitePattern));
//		}
		assertEquals(exp[0][0], sitePattern[0][0], 0.0 );
		assertEquals(exp[0][1], sitePattern[0][1], 0.0 );
		assertEquals(exp[0][2], sitePattern[0][2], 0.0 );

	}
	
	@Test
	public void testDifferentAlignmentClass() throws Exception {
		
		String infile = "simulated2.seq001.nex";
		int noTime = 3;
		int timeGap = 1000;
		jebl.evolution.io.NexusImporter ni = new jebl.evolution.io.NexusImporter(new FileReader(infile));
		
		
		
		int noSeq = 5;
		
		SSC sim = new SSC(noTime, noSeq, timeGap);
		Alignment ali = sim.simulateAlignment(3300, 1.75E-5);
		List<Sequence> allSSSC = ali.getSequenceList();
		
		List<Sequence> allS = ni.importSequences();
		SimpleAlignment[] allSiteAlignment = new SimpleAlignment[noTime];
		SimpleAlignment temp = new SimpleAlignment();
		
		String[] timeLabel = {"_0.0","1000.0","2000.0"}; 
		Arrays.fill(allSiteAlignment, temp);
		for (Sequence s : allS) {
			Taxon name = s.getTaxon();
			int timeIndex = searchTimeIndex(name, timeLabel);
			dr.evolution.sequence.Sequence drS = new dr.evolution.sequence.Sequence(s.getString());
			allSiteAlignment[timeIndex].addSequence( drS);

//			System.out.println(drS.getSequenceString());
			
//			System.out.println(sequence.getTaxon() + "\t"
//					+ sequence.getString());
		}
		for (int i = 0; i < allSiteAlignment.length; i++) {
			SimpleAlignment s = allSiteAlignment[i];
//			System.out.println(s.getSequenceCount());
//			System.out.println(s.getSequence(1).getSequenceString());
		}

	}
	
	private void name() {
		
	}
	
	private int searchTimeIndex(Taxon name, String[] timeLabel) {
		int index = -1;
		String s = name.getName();
		for (int i = 0; i < timeLabel.length; i++) {
			if (s.contains(timeLabel[i])) {
				index = i;
				break;						
			}
		}
		return index;
	}
}
