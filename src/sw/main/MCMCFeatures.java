package sw.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import jebl.evolution.alignments.Alignment;

import org.apache.commons.math3.stat.StatUtils;

import sw.abc.parameter.ParametersCollection;
import sw.abc.stat.data.AlignmentStatFlex;
import sw.logger.ArrayLogFormatterD;
import sw.math.Combination;
import sw.simulator.SSC;
import sw.util.TraceUtil;

public class MCMCFeatures {

//	public MCMCFeatures() {
//		// TODO Auto-generated constructor stub
//	}
	private static final int NO_REPEAT_CAL_ERROR = 1000; //1000
	private static final int NO_REPEAT_PER_PARAMETERS = 100; //100

	public static double generateErrorRate(Setting setting) {
		return generateErrorRate(setting, NO_REPEAT_CAL_ERROR,
				NO_REPEAT_PER_PARAMETERS);
	}

	public static double generateErrorRate(Setting setting, int nRun,
			int nRepeat) {

		// nRun = 10;
		// nRepeat = 10;
		System.out.println("Estimating Error Rate");
		ParametersCollection allParUniformPrior = setting.getAllParPrior();
		SSC simulator = new SSC(setting);
		AlignmentStatFlex[] newStat = new AlignmentStatFlex[nRepeat];
		for (int i = 0; i < newStat.length; i++) {
			newStat[i] = new AlignmentStatFlex(setting);
		}

		double[] repeatStat = new double[nRepeat];
		int[][] listAllComb = Combination.ListCombination(nRepeat);
		int noOfComb = listAllComb.length;
		double[] expectedError = new double[noOfComb * 2];
		double[] meanOfMean = new double[nRun];
		
		for (int i = 0; i < nRun; i++) {
			allParUniformPrior.nextProirs();
			
			for (int j = 0; j < nRepeat; j++) {
				Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
				newStat[j].updateAlignmentAndStat(jeblAlignment);
				repeatStat[j] = newStat[j].getSummaryStatAll()[0];
			}

			for (int j = 0; j < noOfComb; j++) {
				double s1 = repeatStat[listAllComb[j][0]];
				double s2 = repeatStat[listAllComb[j][1]];
				expectedError[j] = AlignmentStatFlex.calAbsDiff(s1, s2);
				expectedError[j + noOfComb] = AlignmentStatFlex.calAbsDiff(s2, s1);

			}
			meanOfMean[i] = StatUtils.mean(expectedError);
		}
		double error = StatUtils.mean(meanOfMean);
//		setting.setError(error);
		
		return error;
		

	}

	
	//	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	public static void testStatFile(Setting setting) {
		
		System.out.println("Test Stat File");
		final int nRun = 100;
		final int noTime = setting.getNoTime();
		String[] paramList = setting.getParamList();
		String[] statsList = setting.getStatList();
	
		ParametersCollection allParUniformPrior = setting.getAllParPrior();
	
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
	
		SSC simulator = new SSC(setting);
	
		TraceUtil tu = new TraceUtil(noTime);
		ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAll(paramList));
		ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAll(statsList));
		long start = System.currentTimeMillis();
		try {
			PrintWriter oResult = new PrintWriter(new BufferedWriter(
					new FileWriter("zzz_testRegressionOutput2")));
			String s = traceLogParam.getLabels() +"\t"+ traceLogStats.getLabels();
			oResult.println(s);
			for (int i = 0; i < nRun; i++) {
				allParUniformPrior.nextProirs();
				traceLogParam.logValues(allParUniformPrior.getValues());
				
				for (int j = 0; j < 100; j++) {
					
				
				Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
				newStat.updateAlignment(jeblAlignment);
	
				
				traceLogStats.logValues(newStat.getCurStat(statsList));
				s = traceLogParam.getLine(i) + "\t" + traceLogStats.getLine(i*100+j);
				oResult.println(s);
				oResult.flush();
				
				}
				
	
			}
			oResult.close();
			System.out.println("Time:\t"+(System.currentTimeMillis()-start));
		} catch (Exception e) {
			e.printStackTrace();
		}	
	
	}


}