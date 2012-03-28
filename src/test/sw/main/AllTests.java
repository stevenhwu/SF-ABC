package test.sw.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


import test.sw.math.CombinationTest;
import test.sw.math.RegressionTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CombinationTest.class, RegressionTest.class,  CreateControlFileTest.class} ) 
public class AllTests {
	

}
