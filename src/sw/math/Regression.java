package sw.math;

import java.util.Arrays;

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;

import sw.util.Trace;
import sw.util.TraceUtil;


public class Regression {

	private OLSMultipleLinearRegression lm;
	private double[] y;
	private double[][] x;
	
	public Regression(){
		lm = new OLSMultipleLinearRegression();
		
	}
	
	public void setY(double[] y) {
		this.y = y;

	}
	
	public void setX(double[] newX) {
		
		int noCount = x.length;
		x[noCount++] = newX;
	}
	
	public void setNoIntercept(boolean noInt) {
		lm.setNoIntercept(noInt);
	}
	
	public void setAllX(double[][] x) {
		this.x = x;
	}

	public void runNoIntercept(){
		try {
			lm.setNoIntercept(true);
			lm.newSampleData(y, x);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			lm.newSampleData(y, x);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void check() {

		try {
			if(y.length != x.length){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(y.length != x.length){
			System.err.println("Different length! X: "+x.length+" Y: "+y.length);
			
			System.exit(-1);
		}
		System.out.println("end check");
	}



	public double[] estimateRegressionParameters() {
		double[] beta = lm.estimateRegressionParameters();
		return beta;
	}
	public String estimateRegressionParametersToString() {
		double[] beta = lm.estimateRegressionParameters();
		return Arrays.toString(beta);
	}

	public double[] estimateResiduals() {
		double[] residuals = lm.estimateResiduals();
		return residuals;
	}
	public double[][] estimateRegressionParametersVariance(){
		double[][] parametersVariance = lm.estimateRegressionParametersVariance();
		return parametersVariance ;
	}
	public double estimateRegressandVariance(){
		double regressandVariance = lm.estimateRegressandVariance();
		return regressandVariance ;
	}
	public double caclulateRSquared(){
		double rSquared = lm.calculateRSquared();
		return rSquared;
	}
	public double estimateRegressionStandardError(){
		double sigma = lm.estimateRegressionStandardError();
		return sigma;
	}

	public double[] getY() {
		return y;
	}

	public double[][] getX() {
		return x;
	}

	public void setY(Trace<Double> t) {

		double[] y = TraceUtil.toPrimitive(t);
		setY(y);
	}

	public void addX(Trace<Double>... t) {
		
		int noX = t.length;
		System.out.println(noX);
		int lengthX = t[0].getValuesSize();
		System.out.println(lengthX);
		x = new double[lengthX][noX];
		for (int i = 0; i < lengthX; i++) {
			for (int j = 0; j < noX; j++) {
				x[i][j] = t[j].getValue(i);
			}
		}
//		double[] x = TraceUtil.toPrimitive(t);
//		setX(x);
		
	}
	
	


}

/*

Instantiate an OLS regression object and load a dataset:
OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
double[] x = new double[6][];
x[0] = new double[]{0, 0, 0, 0, 0};
x[1] = new double[]{2.0, 0, 0, 0, 0};
x[2] = new double[]{0, 3.0, 0, 0, 0};
x[3] = new double[]{0, 0, 4.0, 0, 0};
x[4] = new double[]{0, 0, 0, 5.0, 0};
x[5] = new double[]{0, 0, 0, 0, 6.0};          
regression.newSample(y, x);
          
Get regression parameters and diagnostics:
double[] beta = regression.estimateRegressionParameters();       

double[] residuals = regression.estimateResiduals();

double[][] parametersVariance = regression.estimateRegressionParametersVariance();

double regressandVariance = regression.estimateRegressandVariance();

double rSquared = regression.caclulateRSquared();

double sigma = regression.estimateRegressionStandardError();	

*/