package test.sw.main;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sw.main.Setting;

public class SettingTest {

	Setting setting;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		setting = new Setting("wDir/", "outDir/", "dataName");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetup() {

		
		String[] paramListName = new String[]{"mu", "popsize"};
		String[] statList = new String[]{"dist", "chisq", "var", "sitePattern"};
		
		statList = new String[]{"var"};
//		String[] statList = new String[]{ "sitePattern"};
		setting.setParamList(paramListName);
		setting.setStatList(statList);				
		
		setting.setSummarySettingString("summarySetting");
		setting.setSeqInfo(750, 50, 3, 400);
		
		assertArrayEquals(paramListName, setting.getParamList() );
		assertArrayEquals(statList, setting.getStatList() );
		
		assertEquals("summarySetting", setting.getSummarySettingString());
		
		assertEquals(750, setting.getSeqLength() );
		assertEquals(50, setting.getNoSeqPerTime() );
		assertEquals(3, setting.getNoTime() );
		assertEquals(400, setting.getTimeGap() );
		assertEquals(50*3, setting.getNoTotalSeq());
		
		assertArrayEquals(new int[]{750, 50*3, 3, 400}, setting.getAlignmentInfo());
		assertArrayEquals(new int[]{3, 50}, setting.getTimeInfo());
		
		
		assertEquals("wDir/", setting.getWorkingDir());
		assertEquals("wDir/dataName", setting.getDataFile());
		assertEquals("wDir/dataName_regressionCoef.coef", setting.getRegressionCoefFile());
		assertEquals(true, setting.isDoRegression());
		assertEquals("outDir/dataName_summary.log", setting.getResultOutFile());
		
	}

}
