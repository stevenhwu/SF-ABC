package test.sw.process;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sw.process.RunExt;

public class RunExtTest {

	RunExt proc;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSetParStringStringStringStringArray() {
		
		String[] exp = new String[]{"./BCC", "-p", "-f", "jt.par", "1", }; 
		proc = new RunExt();
		proc.setPar("jt.par");
		assertArrayEquals(exp, proc.getCommand());
		proc.setPar("jt.par", "1");
		assertArrayEquals(exp, proc.getCommand());
		proc.setPar("./BCC", "jt.par", "1");
		assertArrayEquals(exp, proc.getCommand());
		proc.setPar("./BCC", "jt.par", "1", "-p", "-f");
		assertArrayEquals(exp, proc.getCommand());
		
		exp = new String[]{"./BCC", "-a", "-b", "-c", "jt.par", "1", };
		proc.setPar("./BCC", "jt.par", "1", "-a", "-b", "-c");
		
	}

}
