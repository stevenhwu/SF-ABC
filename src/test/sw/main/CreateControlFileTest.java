package test.sw.main;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import sw.main.CreateControlFile;

public class CreateControlFileTest {

	CreateControlFile cFile;
	@Before
	public void setUp() throws Exception {
		String testFile = System.getProperty("user.dir")+"/data/testControlPar";
		cFile = new CreateControlFile(testFile);
	}

	@Test
	public void testSetup() {
		String exp = "An test ABC run\n" +
				"1 population with ancient data\n" +
				"Deme size\n" +
				"3000\n" +
				"Sample sizes\n" +
				"2 sample groups\n" +
				"40 0 0 0\n" +
				"40 400 0 0\n" +
				"Growth rates\n" +
				"0\n" +
				"Number of migration matrices\n" +
				"0\n" +
				"Historical event\n" +
				"0\n" +
				"Mutations per generation for the whole sequence\n" +
				"0.0075\n" +
				"Number of loci\n" +
				"750\n" +
				"Data type\n" +
				"DNA 0.5\n" +
				"Mutation rates\n" +
				"0 0 0\n";
		cFile.setMu(0.0075);
		cFile.setTheta(3000);
		StringBuilder sb = cFile.updateTemplate2T();
		assertEquals(exp, sb.toString());
	}

	@Test
	public void testUpdateTheta() {
		cFile.updateFile(2);

	}

		
}
