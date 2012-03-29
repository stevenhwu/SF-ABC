package test.sw.sequence;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import sw.main.Setup;
import sw.sequence.Importer;
import sw.sequence.Site;
import sw.sequence.SiteAlignment;
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
		
		try {
			importer = new NexusImporter(new FileReader(alignmentFileName));
			saOld = new SiteAlignment(importer.importAlignment());
		} catch (Exception e) {
			e.printStackTrace();
		}


		int[] t1 = new int[40];
		for (int i = 0; i < t1.length; i++) {
			t1[i] = i;
		}

		int[] t2 = new int[40];
		for (int i = 0; i < t2.length; i++) {
			t2[i] = i+40;
		}

		saOld.addTimeGroup(t1);
		saOld.addTimeGroup(t2);
		
	}
	private void setUpB() throws Exception{		

		String dataName = "junit.paup";
//		String dataDir = userDir+sysSep+"data"+sysSep;
		String dataDir = "/dev/shm/JUnit/";
		Setup setting = new Setup(dataDir, dataName);
		setting.setObsFile(dataName);
		int noSeqPerTime = 40;
		int noTime = 2;
		int seqLength = 750;
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime);
		
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
		System.out.println(Arrays.toString(siteVar));
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

		for (int i = 0; i < exp.length; i++) {
			System.out.println(ArrayUtils.toString(sitePattern));
		}
		assertArrayEquals(exp[0], sitePattern[0], 0.0 );

	}
}
