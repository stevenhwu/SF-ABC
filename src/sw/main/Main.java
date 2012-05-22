package sw.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;

import org.apache.commons.math3.stat.StatUtils;
import org.netlib.util.doubleW;

import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaPopsize;
import sw.abc.parameter.ParametersCollection;
import sw.abc.parameter.SavePar;
import sw.abc.parameter.TunePar;
import sw.abc.stat.data.AlignmentStatFlex;
import sw.abc.stat.summary.SStatFlexable;
import sw.logger.ArrayLogFormatterD;
import sw.math.Combination;
import sw.math.Scale;
import sw.math.TruncatedNormalDistribution;
import sw.math.UniformDistribution;
import sw.sequence.SiteAlignment;
import sw.simulator.SSC;
import sw.util.TraceUtil;
import flanagan.analysis.Regression;


public class Main {
	
	/**
	 * 
	 * @author Steven Wu
	 * @version $Id$
	 */
	private static final int TUNESIZE = 50 ;
	private static final int TUNEGROUP = 6;
	
	
	private static final int NO_SEQ_PER_TIME = 50;
	private static final int SEQ_LENGTH = 750;
	private static final int NO_TIME_POINT = 3;
	private static final int TIME_GAP = 400;
	private static final int NO_REPEAT_CAL_ERROR = 1000;
	private static final int NO_REPEAT_PER_PARAMETERS = 100;
//	private static String noSimPerPar = "1";
	
	
	/*
		param:
		args[0]: data file 
		args[1]: noItePreprocess
		args[2]: noIteMCMC
		args[3]: thinning
		args[4]: error
		e.g.	0 simData.paup 50 100 10 0.1 
		e.g.	0 simData.paup 500000 1000000 1000 0.01
	*/
	public static void main(String[] args) {
		
		startSimulation(args);
//		SSC ssc = new SSC(3,5,100);

       
	}
	
	public static void startSimulation(String[] args) {
		
		String obsDataName = args[0];
		
		int noItePreprocess = Integer.parseInt(args[1]);
		int noIteMCMC = Integer.parseInt(args[2]); 
		int thinning = Integer.parseInt(args[3]);
//		double error = Double.parseDouble(args[4]);
 		

		Setting setting = ABCSetup(obsDataName);
		setting.setSeqInfo(SEQ_LENGTH, NO_SEQ_PER_TIME, NO_TIME_POINT , TIME_GAP);
		setting.setMCMCSetting(noItePreprocess, noIteMCMC, thinning);
		
		
		generateStatFile(setting);
		
		System.out.println(setting.toString());
		try {
			ABCUpdateMCMC(setting);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public static Setting ABCSetup(String obsDataName) {

		
		
		
		String obsDataNamePrefix = obsDataName.split("\\.")[0];
		String workingDir = System.getProperty("user.dir")+File.separatorChar+"TemplateFiles"+File.separatorChar;
		String outputDir = workingDir+obsDataNamePrefix+File.separatorChar;
		System.out.println("output Dir:\t"+outputDir);
		
		double[] initValue = getRegressionResult(obsDataNamePrefix);
				
		Setting setting = new Setting(workingDir, outputDir, obsDataName);
		String[] paramListName = new String[]{"mu", "popsize"};
		String[] statList = new String[]{"dist", "chisq", "var", "covar", "sitePattern"};
		
		setting.setParamList(paramListName);
		setting.setStatList(statList);				

		double muMean = initValue[0];
		double muLower = muMean / 10;
		double muUpper = muMean * 10;
		
		double thetaMean = initValue[1];
		double thetaLower = thetaMean / 5;;
		double thetaUpper = thetaMean * 5;
		
		ParaMu unifMu = new ParaMu(new UniformDistribution(muLower, muUpper));
		ParaPopsize unifPopsize = new ParaPopsize(new UniformDistribution(thetaLower, thetaUpper));
	
		double initScale = 0.75;
		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{initScale , initScale}, new String[] { "Scale", "Scale" });

		ParaMu pMu = new ParaMu(new TruncatedNormalDistribution(muMean, muMean/2, muLower, muUpper));
		pMu.setInitValue(muMean);
		pMu.setProposal(new Scale(initScale ));
//		pMu.setProposal(new NormalDistribution(muMean, muMean/5));
		
		ParaPopsize pPop = new ParaPopsize(new TruncatedNormalDistribution(thetaMean, thetaMean/2, thetaLower, thetaUpper));
		pPop.setInitValue(thetaMean);
		pPop.setProposal(new Scale(initScale ));
//		pPop.setProposal(new NormalDistribution(thetaMean, thetaMean/5));


		ParametersCollection allPars = new ParametersCollection(paramListName, pMu, pPop); 
		ParametersCollection allParsPrior = new ParametersCollection(paramListName, unifMu, unifPopsize);
//		
		
//		double muTune = muMean/10;
//		double thetaTune = thetaMean/10;
//		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{muTune, thetaTune}, new String[] { "Normal", "Normal" });
//		pMu.setProposal(new NormalDistribution(muMean, muTune));
//		pTheta.setProposal(new NormalDistribution(thetaMean, thetaTune));
	
		setting.setTunePar(tPar);
		setting.setAllPar(allPars);
		setting.setAllParPrior(allParsPrior);

		return setting;
	}
	

//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Setting generateStatFile(Setting setting) {
		
		SStatFlexable sStat = null;
		if(setting.getDoRegression()){
			
			System.out.println("Preprocessing: semi-auto regression");
			int nRun = setting.getNoItePreprocess();
			int thinning = setting.getThinning();
			int noTime = setting.getNoTime();
			String[] paramList = setting.getParamList();
			String[] statsList = setting.getStatList();
	
			ParametersCollection allParUniformPrior = setting.getAllParPrior();

			SiteAlignment sa = new SiteAlignment(setting);
			AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
			
			SSC simulator = new SSC(setting);
			
			TraceUtil tu = new TraceUtil(noTime);
			ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAll(paramList));
			ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAll(statsList));
	

			long startTime = System.currentTimeMillis();
			for (int i = 0; i < nRun; i++) {
				allParUniformPrior.getNextProir();
									
				Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
				sa.updateJEBLAlignment(jeblAlignment);
				newStat.updateSiteAlignment(sa);

				traceLogParam.logValues( allParUniformPrior.getValues() );
				traceLogStats.logValues( newStat.getCurStat(statsList) );

				if ((i % thinning) == 0) {
					System.out.println("Ite:\t"+i+"\t"+ Math.round((System.currentTimeMillis() - startTime)/60e3)+" mins");
				}
			}
			
			sStat = semiAutoRegression(nRun, traceLogStats, traceLogParam, 1);
			String regressionSummary = sStat.toString();
			System.out.println(regressionSummary);
			System.out.println("Time:\t" + Math.round( (System.currentTimeMillis()-startTime)/60e3)+" mins" );	
			
			
			try {	
				
				String regressionCoefFile =  setting.getRegressionCoefFile();
				PrintWriter oResult1 = new PrintWriter(new BufferedWriter(
						new FileWriter(regressionCoefFile)));
				oResult1.println("# " + setting.toString());
				oResult1.println(regressionSummary);
				oResult1.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else{
			
			sStat = readRegressionFile(setting);
			
		}
		
		setting.setSummaryStat(sStat);
		setting.setObsStat( calObsStat(setting) );
		
		double error = generateErrorRate(setting, NO_REPEAT_CAL_ERROR, NO_REPEAT_PER_PARAMETERS);
		setting.setError(error);
		
		return setting;
		
		
		 
	
	}


public static double generateErrorRate(Setting setting, int nRun, int nRepeat) {
			
	//		nRun = 10;
	//		nRepeat = 10;
			
			SSC simulator = new SSC(setting);
			
			SiteAlignment sa = new SiteAlignment(setting);
			AlignmentStatFlex[] newStat = new AlignmentStatFlex[nRepeat];
			double[] repeatStat = new double[nRepeat];
			for (int i = 0; i < newStat.length; i++) {
				newStat[i] = new AlignmentStatFlex(setting);
			}
			
			ParametersCollection allParUniformPrior = setting.getAllParPrior();
	
			
	//		newStat.setObsStat(setting.getObsStat()) ;
	//		setting.setObsStat( calObsStat(setting) );
			
			int[][] listAllComb = Combination.ListCombination(nRepeat);
			int noOfComb = listAllComb.length;
			double[] expectedError = new double[noOfComb*2];
			double[] meanOfMean = new double[nRun];
			for (int i = 0; i < nRun; i++) {
			
					allParUniformPrior.getNextProir();
					
					for (int j = 0; j < nRepeat; j++) {
						Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
						sa.updateJEBLAlignment(jeblAlignment);
						
						newStat[j].updateSiteAlignment(sa);	
						newStat[j].calSumStat();
						repeatStat[j] = newStat[j].getSummaryStatAll()[0];
	//					System.out.println(newStat[j].calDelta());
	//					System.out.println(Arrays.toString(newStat[j].getSummaryStatAll()));
	//					System.out.println(repeatStat[j]);
	//					System.out.println();
					}
					
				for (int j = 0; j < noOfComb; j++) {
					double s1 = repeatStat[listAllComb[j][0]]; 
					double s2 = repeatStat[listAllComb[j][1]];
					expectedError[j] = AlignmentStatFlex.calAbsDiff(s1, s2);
					expectedError[j+noOfComb] = AlignmentStatFlex.calAbsDiff(s2, s1);
	//				System.out.println(s1 +"\t"+ s2 +"\t"+ expectedError[j] +"\t"+ expectedError[j+noOfComb]);
					// System.out.println(listAllComb[j][0] +"\t"+
					// listAllComb[j][1]);
	
				}
				meanOfMean[i] = StatUtils.mean(expectedError);
			}
			return(StatUtils.mean(meanOfMean));
	
		}

	//	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void ABCUpdateMCMC(Setting setting) throws Exception {

		int nRun = setting.getNoIteMCMC();
		int thinning = setting.getThinning();
		double error = setting.getError();
		
		System.out.println("Start ABCMCMC");
		System.out.println("Tolerance:\t"+error);
		long startTime = System.currentTimeMillis();

		
		SSC simulator = new SSC(setting);
		ParametersCollection allPar = setting.getAllPar();
		TunePar tPar = setting.getTunePar();
		SavePar sPar = new SavePar(TUNESIZE);
		
		int noPar = allPar.getSize();
		int noTime = setting.getNoTime();
		double[] saveGap = new double[noPar];
		
		
		
		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
		newStat.setObsStat(setting.getObsStat()) ;

//		String[] paramList = new String[setting.getParamList().length+1];
//		System.arraycopy(setting.getParamList(), 0, paramList, 0, setting.getParamList().length);
//		paramList[setting.getParamList().length] = "Gap";
//		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAL(paramList));
		
		TraceUtil ut = new TraceUtil(noTime, noPar);
		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAll(setting.getParamList()));
		double[] logValues = allPar.getValues();
		traceLog.logValues( logValues );

		PrintWriter oResult = new PrintWriter(new BufferedWriter(new FileWriter(setting.getResultOutFile())));
		oResult.println("# "+setting.getDataFile()+"\t"+setting.toString());
		oResult.println("Ite\t" + traceLog.getLabels());
		oResult.flush();

//		double[] deltaDup = new double[10];
		
		
		for (int i = 0; i < nRun; i++) {

			
			for (int p = 0; p < allPar.getSize(); p++) {
				allPar.getNextProposal(p);

				Alignment jeblAlignment = simulator.simulateAlignment(allPar);
				sa.updateJEBLAlignment(jeblAlignment);
				newStat.updateSiteAlignment(sa);
	

				
				
				saveGap[p] = newStat.calDelta();
//				saveGap[p] = newStat.calDeltaSep(obsStat, p);
				

				// for (int dup = 0; dup < deltaDup.length; dup++) {
				// newStat.addParIndex(cFile.getAllPar());
				// deltaDup[dup] = newStat.calDelta(obsStat);
				// }
				// double delta = newStat.calMultiDelta(obsStat);
				// saveGap[p] = StatUtils.mean(deltaDup);
				// System.out.println(saveGap[p]+"\t"+StatUtils.min(deltaDup)+"\t"+Arrays.toString(deltaDup));

				if (saveGap[p] < error) {
					// newStat.calIndStat2(obsStat);
//					 System.out.print("FreqGap: " + saveGap[p]+"\n");

					if (MH.accept(allPar.getParameter(p))) { //TODO: think about this
						allPar.acceptNewValue(p);
					}

				}
			}
			sPar.add(allPar);
			
			if (i % TUNESIZE == 0 & i!=0) {
				
				tPar.update(sPar, i);
//				System.out.println(Arrays.toString(tPar.getTunePar()));
//				error = updateTol(tPar.getAccRate(), error);
//				System.out.println(error);
				for (int j = 0; j < allPar.getSize(); j++) {
//					TODO recode this
					allPar.getParameter(j).updateProposalDistVar(tPar.getTunePar(j));
				}
//				double accRate = tPar.getMeanAccRate();
//				error = updateErrorRate(i, nRun, error, accRate );
				
				
				if (allPar.getAcceptCount(0) == 0 & allPar.getAcceptCount(1) == 0 & i>(TUNESIZE*10)){
					allPar.getNextProir();
				}
				
			}
			
			if ((i % thinning) == 0) {
				System.out.println("Ite:\t"+i + "\t"+ Arrays.toString(allPar.getAcceptCounts()) + "\t" + error +"\t"+ 
						Arrays.toString(tPar.getEachAccRate()) + "\t" +
						Arrays.toString(allPar.getValues()) );

//				logValues = Doubles.concat(new double[]{allPar.get(0).getValue(), allPar.get(1).getValue()}, saveGap);
				logValues = allPar.getValues();
				traceLog.logValues(logValues);
//				TraceUtil.logValue(tMu, allPar.get(0).getValue());
//				TraceUtil.logValue(tTheta, allPar.get(1).getValue());
//				TraceUtil.logValue(tGap, saveGap);

				// TraceUtil.logValue(tDist, 0);

				String s = i + "\t" + traceLog.getLine(i / thinning);

				// System.out.println(s + "\t" + setting.getWorkingDir());
				oResult.println(s);
				oResult.flush();
			}

		}
		System.out.println(Arrays.toString( tPar.getEachAccRate() ));
		oResult.close();

		System.out.println("Time:\t"+ Math.round((System.currentTimeMillis() - startTime) / 60e3) + " mins");
		// System.out
		// .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(0))));
		// System.out
		// .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(1))));

	}




	private static double updateTol(double[] ds, double error) {
		double rate = StatUtils.mean(ds);
		
		double newError = error;
		if(rate > 0.5){
			newError -= 0.02;
		}
		if(rate < 0.1){
			newError += 0.02;
		}
		return newError;
	}

	private static SStatFlexable readRegressionFile(Setting setting) {
		
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


	private static double[] calObsStat(Setting setting) {
	
		System.out.println("Calculate observed stat");
		SiteAlignment sa = new SiteAlignment(setting);
		
//		Importer imp = new Importer(setting.getDataFile());
		AlignmentStatFlex aliStat = new AlignmentStatFlex(setting);

		try {

			NexusImporter imp = new NexusImporter(new FileReader(
					setting.getDataFile()));
			sa.updateAlignment(imp.importAlignments());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		}

		aliStat.updateSiteAlignment(sa);

		aliStat.calSumStat();
	
		System.out.println("Observed stat:\t"+aliStat.toString());
		
		return aliStat.getSummaryStatAll();
	
	}


	private static double[] getRegressionResult(String obsDataNamePrefix) {
		int fileIndex = obsDataNamePrefix.indexOf("_");
		double[] initValue = new double[2];
		if(fileIndex== -1){
			initValue = new double[]{0.00001, 3000};
		}
		else {
			int i = Integer.parseInt(obsDataNamePrefix.substring(fileIndex+1));
			initValue = RegressionResult.result[i];
		}
		System.out.println("Init values:\t"+Arrays.toString(initValue));
		return initValue;
	}


	private static SStatFlexable semiAutoRegression(int nRun, ArrayLogFormatterD traceLogStats, ArrayLogFormatterD traceLogParam, int sParam) {
		
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
			double[] logMuTheta = new double[allTheta.length];
			for (int i = 0; i < allTheta.length; i++) {
				logMuTheta[i] = Math.log(allMu[i]*allTheta[i]);
			}

			Regression lm = new Regression(xxData, logMuTheta );
			
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



}
