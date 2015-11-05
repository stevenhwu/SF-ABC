package sw.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import sw.abc.stat.summary.SStatFlexable;
import sw.logger.ArrayLogFormatterD;
import flanagan.analysis.Regression;

public class SemiAutoRegression {

//	public SemiAutoRegression() {
//		// TODO Auto-generated constructor stub
//	}

	public static SStatFlexable semiAutoRegression(int nRun, ArrayLogFormatterD traceLogStats, ArrayLogFormatterD traceLogParam, int sParam) {
			
		if(nRun != traceLogParam.getLength()){
			System.err.println("Error! incomplete preprocessing:\t"+nRun+"\t"+traceLogParam.getLength()+"\tEXIT");
			System.exit(-1);
		}
		
		double[][] xxData = traceLogStats.to2DArray();
		SStatFlexable sStat = new SStatFlexable();
		
		if(sParam==1){
			// lm(log(Mu*Theta)~ .)
			double[] allMu = traceLogParam.toArray(0);
			double[] allTheta = traceLogParam.toArray(1);
			double[] muTheta = new double[allTheta.length];
			for (int i = 0; i < allTheta.length; i++) {
//				logMuTheta[i] = Math.log(allMu[i]*allTheta[i]);
				muTheta[i] = allMu[i]*allTheta[i];
			}

			Regression lm = new Regression(xxData, muTheta );
			
			lm.linear();	
			double[] coef = lm.getBestEstimates();
			sStat.addCoef("Mu", coef);
			sStat.addCoef("Theta", coef);
			System.out.println(lm.getAdjustedCoefficientOfDetermination());
		}
		else{
			
			Regression lm = new Regression(xxData, traceLogParam.toArray(0) );
			lm.linear();	
			System.out.println(lm.getAdjustedCoefficientOfDetermination());
			double[] coef = lm.getBestEstimates();
			sStat.addCoef("Mu", coef);
	
			lm.enterData(xxData, traceLogParam.toArray(1));
			lm.linear();
			System.out.println(lm.getAdjustedCoefficientOfDetermination());
			coef = lm.getBestEstimates();
			sStat.addCoef("Theta", coef);
		}
		return sStat;
	}

	public static SStatFlexable readRegressionFile(Setting setting) {
		
		String regressionCoefFile =  setting.getRegressionCoefFile();
		System.out.println("Skip preprocessing - parse existing coef file: "+regressionCoefFile);
		SStatFlexable sStat = new SStatFlexable();
		try {
			BufferedReader br = new BufferedReader(new FileReader(regressionCoefFile));
			String line;
			while((line = br.readLine())!= null ){
				if(line.startsWith("Mu")) {
					sStat.addCoef("Mu", parseCoef(line));
				} else if(line.startsWith("Theta")){
					sStat.addCoef("Theta", parseCoef(line));
				}
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	
		return sStat;
	}

	private static double[] parseCoef(String line) {
		
		String[] tokens = line.split("\\[|,|\\]");
		double[] coef = new double[tokens.length-1];
		for (int i = 0; i < coef.length; i++) {
			coef[i] = Double.parseDouble(tokens[i+1].trim());
		}
		return coef;
	}

}
