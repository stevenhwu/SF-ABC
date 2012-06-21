package test.sw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.sw.abc.AllTestsABC;
import test.sw.main.SettingTest;
import test.sw.math.AllTestsMath;
import test.sw.sequence.AllTestsSequence;
import test.sw.sequence.SiteAlignPerTimeTest;
import test.sw.sequence.SiteAlignmentTest;
import test.sw.sequence.SiteTest;
import test.sw.simulator.SSCTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	
	AllTestsABC.class,

	SettingTest.class,
//	CreateControlFileTest.class,
	
	AllTestsMath.class, 


	AllTestsSequence.class,
	
	SSCTest.class,
}) 
public class AllTests {
	

}
