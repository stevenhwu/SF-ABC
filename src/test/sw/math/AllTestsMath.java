package test.sw.math;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CombinationTest.class, OneOverDistributionTest.class,
		RegressionTest.class, TruncatedScaleTest.class })
public class AllTestsMath {

}
