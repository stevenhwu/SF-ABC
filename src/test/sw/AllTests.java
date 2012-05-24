package test.sw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.sw.abc.ParaTest;
import test.sw.math.CombinationTest;
import test.sw.math.RegressionTest;
import test.sw.sequence.SiteAlignPerTimeTest;
import test.sw.sequence.SiteAlignmentTest;
import test.sw.sequence.SiteTest;
import test.sw.simulator.SSCTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	
	ParaTest.class,

	  
//	CreateControlFileTest.class,
	
	CombinationTest.class, 
	RegressionTest.class,

//	RunExtTest.class,
	
	SiteAlignmentTest.class,
	SiteAlignPerTimeTest.class,
	SiteTest.class,
	
	SSCTest.class,
}) 
public class AllTests {
	

}