package test.sw.math;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import sw.math.Regression;

public class RegressionTest {
	
	private Regression lm;
	
	@Before
	public void setUp() throws Exception {
		lm = new Regression();
	}



	@Test
	public void testSetXY() {
		
		double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
		double[][] x = new double[6][];
		x[0] = new double[]{0, 0, 0, 0, 0};
     
		assertArrayEquals(null, lm.getY(), 0.0);
		
		lm.setAllX(x);
		lm.setY(y);
		assertArrayEquals(y, lm.getY(), 0.0);
		assertArrayEquals(x[0], lm.getX()[0], 0.0);
		
		
	}

	@Test
	public void testCheck() {
		
		double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
		double[][] x = new double[6][];
		x[0] = new double[]{0, 0, 0, 0, 0};
		x[1] = new double[]{2.0, 0, 0, 0, 0};
		x[2] = new double[]{0, 3.0, 0, 0, 0};
		x[3] = new double[]{0, 0, 4.0, 0, 0};
		x[4] = new double[]{0, 0, 0, 5.0, 0};
		x[5] = new double[]{0, 0, 0, 0, 6.0};
		lm.setAllX(x);
		lm.setY(y);
		lm.run();
		double[] exp = new double[]{11, 0.5, 0.666666, 0.75, 0.8000,0.83333 };
		assertArrayEquals(exp, lm.estimateRegressionParameters(), 1E-5 );
		
		
	}


	
	@Test
	public void testNoIntercept() {

		double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
		double[][] x = new double[6][];
		x[0] = new double[]{0, 0, 0, 0, 0};
		x[1] = new double[]{2.0, 0, 0, 0, 0};
		x[2] = new double[]{0, 3.0, 0, 0, 0};
		x[3] = new double[]{0, 0, 4.0, 0, 0};
		x[4] = new double[]{0, 0, 0, 5.0, 0};
		x[5] = new double[]{0, 0, 0, 0, 6.0};
		lm.setNoIntercept(true);
		lm.setAllX(x);
		lm.setY(y);
		lm.run();
		double[] exp = new double[]{6, 4.333333, 3.5, 3, 2.666666 };
		assertArrayEquals(exp, lm.estimateRegressionParameters(), 1E-5 );
		
	}

//R code
//	y<- 11:16
//	x<- as.data.frame(rbind(c(0, 0, 0, 0, 0),
//			c(2.0, 0, 0, 0, 0),
//			c(0, 3.0, 0, 0, 0),
//			c(0, 0, 4.0, 0, 0),
//			c(0, 0, 0, 5.0, 0),
//			c(0, 0, 0, 0, 6.0) ))
//	lm(y~., data=x)
//	lm(y~.-1, data=x)
}
