package test.sw.abc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sw.sequence.Importer;
import sw.sequence.SiteAlignment;
import sw.zold.OldSetup;
import sw.zold.abc.stat.AlignmentStat;

public class AlignmentStatTest {

	public static final String sysSep = System.getProperty("file.separator");
	public static final String userDir = System.getProperty("user.dir");

	String fileName = "junit.paup";
	int noSeq = 80;
	SiteAlignment sa;

	@Before
	public void setUp() throws Exception {

		String dataDir = userDir + sysSep + "data" + sysSep;

		OldSetup setting = new OldSetup(dataDir, fileName);
		// setting.setStat(null);
		// setting.setStat(new SStatBig());

		int noSeqPerTime = 40;
		int noTime = 2;
		int seqLength = 750;

		setting.setAlignmentFile(fileName);
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime);
		setting.setTimeGap(400);
		sa = new SiteAlignment(setting);
		Importer imp = new Importer(setting.getAlignmentFile(), noSeqPerTime*noTime);
		try {
			sa.updateAlignment(imp.importAlignment());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public final void testAddSiteDists() {

		AlignmentStat aliStat = new AlignmentStat();

		aliStat.updateSiteAlignment(sa);
		double[] dists = aliStat.getSiteDists();
		assertEquals(0.059155, dists[0], 1E-6);
		assertEquals(0.053313, dists[1], 1E-6);
		assertEquals(0.0579083, dists[2], 1E-6);
	}

	@Test
	public final void testAddSiteFreqSpecSiteAlignment() {
		AlignmentStat aliStat = new AlignmentStat();

		aliStat.updateSiteAlignment(sa);

		double[][] freqSpec = aliStat.getSiteFreqSpec();
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
		assertArrayEquals(exp[0], freqSpec[0] , 0.01);
		assertArrayEquals(exp[1], freqSpec[1] , 0.01);
	}



	
	
}

