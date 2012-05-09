package sw.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.sequences.State;


import com.google.common.primitives.Doubles;
import sw.abc.parameter.ArrayLogFormatterD;
import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaPopsize;
import sw.abc.parameter.Parameters;
import sw.abc.parameter.ParametersCollection;
import sw.abc.parameter.SavePar;
import sw.abc.parameter.TunePar;
import sw.abc.stat.data.AlignmentStatFlex;
import sw.abc.stat.summary.*;

import sw.math.NormalDistribution;
import sw.math.Scale;
import sw.math.TruncatedNormalDistribution;
import sw.math.UniformDistribution;
//import sw.process.RunExt;
import sw.sequence.Importer;
import sw.sequence.SiteAlignment;
import sw.simulator.SSC;
import sw.util.TraceUtil;
import sw.zold.CreateControlFile;
import sw.zold.OldSetup;
import sw.zold.abc.stat.AlignmentStat;
import flanagan.analysis.Regression;


public class Main {
	
	/**
	 * 
	 * @author Steven Wu
	 * @version $Id$
	 */
	private static final int TUNESIZE = 10 ;
	private static final int TUNEGROUP = 6;
	
	
	private static final int NO_SEQ_PER_TIME = 50;
	private static final int SEQ_LENGTH = 750;
	private static final int NO_TIME_POINT = 3;
	private static final int TIME_GAP = 400;
	
//	private static String noSimPerPar = "1";
	
	
	/*
		param:
//		args[0]: ==0 local   ==1  /scratch on DSCR
		args[1]: data file 
		args[2]: noItePreprocess
		args[3]: noIteMCMC
		args[4]: thinning
		args[5]: error
		e.g.	0 simData.paup 50 100 10 0.1 
		e.g.	0 simData.paup 500000 1000000 1000 0.01
	*/
	public static void main(String[] args) {
		
		startSimulation(args);
//		SSC ssc = new SSC(3,5,100);

       
	}
	
	public static void startSimulation(String[] args) {
		
	
		int noItePreprocess = Integer.parseInt(args[2]); //TODO change back
		int noIteMCMC = Integer.parseInt(args[3]); //TODO change back
		int thinning = Integer.parseInt(args[4]);
		double error = Double.parseDouble(args[5]);
		
		String summarySetting = noItePreprocess +"\t" + noIteMCMC +"\t" + thinning +"\t" + error;
		// args[0]==0 local   ==1  /scratch
		String obsDataName = args[1];
		String obsDataNamePrefix = obsDataName.split("\\.")[0];
//		String dataDir = "/dev/shm/"+obsDataNamePrefix+SYSSEP;
//		if(args[0].equalsIgnoreCase("1")){
////			dataDir = "/scratch/sw167/"+obsDataNamePrefix+SYSSEP;
//		}
//		File f = new File(dataDir);
//		if(!f.exists()){
//			System.out.println("mkdir "+f.toString()+"\t"+f.mkdir());
//			System.out.println(f.exists());
//		}
		
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
		
		String workingDir = System.getProperty("user.dir")+File.separatorChar+"TemplateFiles"+File.separatorChar;
		String outDir = workingDir+obsDataNamePrefix+File.separatorChar;
		
System.out.println(workingDir);
System.out.println(outDir);
System.out.println(obsDataName);

		Setting setting = ABCSetup(workingDir, outDir, obsDataName, 
				SEQ_LENGTH, NO_SEQ_PER_TIME, NO_TIME_POINT , TIME_GAP, initValue, summarySetting);
		
		setting = generateStatFile(noItePreprocess, thinning, setting);


//		setting.setStat(sumStat);

		setting.setNoSeqPerTime(40);
		setting.setNoSeqPerTime(NO_SEQ_PER_TIME);
		
		
		try {
			ABCUpdateMCMC(setting, noIteMCMC, thinning, error);
		} catch (Exception e) {
			e.printStackTrace();
		}



	}


	public static Setting ABCSetup(String workingDir, String outputDir, String dataFileName,
			int seqLength, int noSeqPerTime, int noTime, int timeGap, double[] initValue, String summarySetting) {


		Setting setting = new Setting(workingDir, outputDir, dataFileName);
		String[] paramListName = new String[]{"mu", "popsize"};
		String[] statList = new String[]{"dist", "chisq", "var", "sitePattern"};
		
		statList = new String[]{"dist", "chisq", "var"};
//		String[] statList = new String[]{ "sitePattern"};
		setting.setParamList(paramListName);
		setting.setStatList(statList);				
		
		setting.setSummarySettingString(summarySetting);
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime, timeGap);
		
//		setting.setOutputFiles(USERDIR, obsFileName);
//		setting.setObsFile(obsFileName);

System.out.println("init value:\t"+Arrays.toString(initValue));

		double muMean = initValue[0];// * seqLength;
		double muTune = muMean/10;
		double muLower = muMean / 10;
		double muUpper = muMean * 10;
		
		double thetaMean = initValue[1];
		double thetaTune = thetaMean/10;
		double thetaLower = thetaMean / 5;;
		double thetaUpper = thetaMean * 5;
		
		ParaMu unifMu = new ParaMu(new UniformDistribution(muLower, muUpper));
		ParaPopsize unifPopsize = new ParaPopsize(new UniformDistribution(thetaLower, thetaUpper));

		
		
		double initScale = 0.75;
		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{initScale , initScale}, new String[] { "Scale", "Scale" });
		
		ParaMu pMu = new ParaMu(new TruncatedNormalDistribution(muMean, muMean/2, muLower, muUpper));
		pMu.setInitValue(muMean);
		pMu.setProposal(new Scale(initScale ));
//		pMu.setInitValue();
		
		ParaPopsize pPop = new ParaPopsize(new TruncatedNormalDistribution(thetaMean, thetaMean/2, thetaLower, thetaUpper));
		pPop.setInitValue(thetaMean);
		pPop.setProposal(new Scale(initScale ));
//		pTheta.setInitValue();

		
//		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{muTune, thetaTune}, new String[] { "Normal", "Normal" });
//		pMu.setProposal(new NormalDistribution(muMean, muTune));
//		pTheta.setProposal(new NormalDistribution(thetaMean, thetaTune));

		ParametersCollection allPars = new ParametersCollection(paramListName, pMu, pPop); 
		ParametersCollection allParsPrior = new ParametersCollection(paramListName, unifMu, unifPopsize);
		
		setting.setTunePar(tPar);
		setting.setAllPar(allPars);
		setting.setAllParPrior(allParsPrior);

		return setting;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Setting generateStatFile(int nRun, int thinning, Setting setting) {
		
		SStatFlexable sStat = null;
		if(setting.isDoRegression()){

			int noTime = setting.getNoTime();
			String[] paramList = setting.getParamList();
			String[] statsList = setting.getStatList();
	
			ParametersCollection allParUniformPrior = setting.getAllParPrior();

			SiteAlignment sa = new SiteAlignment(setting);
			AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
			
			TraceUtil tu = new TraceUtil(noTime);
		
			ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAL(paramList));
			ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAL(statsList));
	
			SSC simulator = new SSC(setting);
//			RunExt proc = new RunExt(setting.getfWorkingDir());
//			proc.setPar(softwareName, cFile.getControlFile(), noSimPerPar, switchPar);
	
			long startTime = System.currentTimeMillis();
			//				PrintWriter oResult = new PrintWriter(new BufferedWriter(
//						new FileWriter(setting.getResultOutFile())));
//				oResult.println("Ite\t" + traceLogParam.getLabels() +"\t"+ traceLogStats.getLabels());
//				
			for (int i = 0; i < nRun; i++) {
				allParUniformPrior.getNextProir();

									
				Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
				sa.updateJEBLAlignment(jeblAlignment);
				newStat.updateSiteAlignment(sa);

				traceLogParam.logValues( allParUniformPrior.getValues() );
				traceLogStats.logValues( newStat.getCurStat(statsList) );
//					oResult.println(i + "\t" + traceLogParam.getLine(i) + "\t"	+ traceLogStats.getLine(i));

				if ((i % thinning) == 0) {
//						oResult.flush();
					System.out.println("Ite:\t"+i+"\t"+ Math.round((System.currentTimeMillis() - startTime)/60e3)+" mins");
				}
			}
//				oResult.close();
			
			System.out.println("Time:\t" + Math.round( (System.currentTimeMillis()-startTime)/60e3)+" mins" );	
			sStat = semiAutoRegression(nRun, traceLogStats, traceLogParam, 1);
			String regressionSummary = sStat.toString();
			System.out.println(regressionSummary);
	
			
			try {	
				
				String regressionCoefFile =  setting.getRegressionCoefFile();
				PrintWriter oResult1 = new PrintWriter(new BufferedWriter(
						new FileWriter(regressionCoefFile)));
				oResult1.println("# " + setting.getDataFile() + "\t"
						+ setting.getSummarySettingString());
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
		
		return setting;
		
		
		 
	
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void ABCUpdateMCMC(Setting setting, int nRun, int logInt,
			double error) throws Exception {

		System.out.println("Start ABCMCMC");
		long startTime = System.currentTimeMillis();

		ParametersCollection allPar = setting.getAllPar();
		TunePar tPar = setting.getTunePar(); 
		int noPar = allPar.getSize();
		int noTime = setting.getNoTime();
		double[] saveGap = new double[noPar];
		double[] obsStat = setting.getObsStat();
		SavePar sPar = new SavePar(TUNESIZE);
		
		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);


//		String[] paramList = new String[setting.getParamList().length+1];
//		System.arraycopy(setting.getParamList(), 0, paramList, 0, setting.getParamList().length);
//		paramList[setting.getParamList().length] = "Gap";
//		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAL(paramList));
		
		TraceUtil ut = new TraceUtil(noTime, noPar);
		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAL(setting.getParamList()));
		

//		CreateControlFile cFile = new CreateControlFile(setting.getBCCControlFile(), noTime, setting.getNoSeqPerTime());
//		RunExt proc = new RunExt(setting.getfWorkingDir());
//		proc.setPar(softwareName, cFile.getControlFile(), noSimPerPar, switchPar);

//		cFile.setInitPar(allPar);
//		cFile.updateFile();

		
		SSC simulator = new SSC(setting);
//		double[] logValues = Doubles.concat(cFile.getAllPar(), saveGap);
//		double[] logValues = Doubles.concat(cFile.getAllPar());
		double[] logValues = allPar.getValues();
		traceLog.logValues( logValues );

		PrintWriter oResult = new PrintWriter(new BufferedWriter(new FileWriter(setting.getResultOutFile())));
		oResult.println("# "+setting.getDataFile()+"\t"+setting.getSummarySettingString());
		oResult.println("Ite\t" + traceLog.getLabels());
		oResult.flush();

//		double[] deltaDup = new double[10];
		
		double errorInit = error;
		for (int i = 0; i < nRun; i++) {

			
			for (int p = 0; p < allPar.getSize(); p++) {
				allPar.getNextProposal(p);
//				cFile.setParProposal(allPar, p);
				
//				cFile.updateFile();
//				sa = updateAlignment(sa, setting);
//				sa = updateAlignment(sa, proc, setting);
//				newStat.updateSiteAlignment(sa);
				
long time1 = System.currentTimeMillis();
for (int j = 0; j < 100; j++) {
	
Alignment jeblAlignment = simulator.simulateAlignment(allPar);
//sa.updateJEBLAlignment(jeblAlignment);
//newStat.updateSiteAlignment(sa);
	
}
long time2 = System.currentTimeMillis();
System.out.println("1Time for 100:"+"\t"+ (time2 - time1)/1000.0 + "\t");				


Alignment jeblAlignment = simulator.simulateAlignment(allPar);
sa.updateJEBLAlignment(jeblAlignment);
 time1 = System.currentTimeMillis();
for (int j = 0; j < 100; j++) {
	
//Alignment jeblAlignment = simulator.simulateAlignment(allPar);
sa.updateJEBLAlignment(jeblAlignment);
//newStat.updateSiteAlignment(sa);
	
}
 time2 = System.currentTimeMillis();
System.out.println("2Time for 100:"+"\t"+ (time2 - time1)/1000.0 + "\t");				


 time1 = System.currentTimeMillis();
for (int j = 0; j < 100; j++) {
	
//Alignment jeblAlignment = simulator.simulateAlignment(allPar);
//sa.updateJEBLAlignment(jeblAlignment);
newStat.updateSiteAlignment(sa);
	
}
 time2 = System.currentTimeMillis();
System.out.println("3Time for 100:"+"\t"+ (time2 - time1)/1000.0 + "\t");				


				saveGap[p] = newStat.calDelta(obsStat);
//				saveGap[p] = newStat.calDeltaSep(obsStat, p);
				
				
				// System.out.print(saveGap[p]+"\t");
				// for (int dup = 0; dup < deltaDup.length; dup++) {
				// newStat.addParIndex(cFile.getAllPar());
				// deltaDup[dup] = newStat.calDelta(obsStat);
				// }
				// double delta = newStat.calMultiDelta(obsStat);
				// saveGap[p] = StatUtils.mean(deltaDup);
				// System.out.println(saveGap[p]+"\t"+StatUtils.min(deltaDup)+"\t"+Arrays.toString(deltaDup));

				if (saveGap[p] < error) {
					// newStat.calIndStat2(obsStat);
//					 System.out.print("FreqGap: " + saveGap[p]+"\t");

					if (MH.accept(allPar.getParameter(p))) { //TODO: think about this
						allPar.acceptNewValue(p);
//						 System.out.println( i + "\t" +
//						 allPar.get(p).getAcceptCount() + "\t" + cFile.getMu()
//						 + "\t" + cFile.getTheta() + "\t"+
//						 setting.getWorkingDir());
					}

				}
			}
			sPar.add(allPar);
			
			if (i % TUNESIZE == 0 & i!=0) {
				
				tPar.update(sPar, i);
//				System.out.println(Arrays.toString(tPar.getTunePar()));
				
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
			
			if ((i % logInt) == 0) {
				System.out.println("Ite:\t"+i + "\t"+ allPar.getAcceptCount(0) +
						"\t" + allPar.getAcceptCount(1) + "\t" +
						Arrays.toString(tPar.getEachAccRate()) + "\t" +
//						+allPar.get(0).getValue() + "\t" + allPar.get(1).getValue() + "\t"
						setting.getWorkingDir());

//				logValues = Doubles.concat(new double[]{allPar.get(0).getValue(), allPar.get(1).getValue()}, saveGap);
				logValues = allPar.getValues();
				traceLog.logValues(logValues);
//				TraceUtil.logValue(tMu, allPar.get(0).getValue());
//				TraceUtil.logValue(tTheta, allPar.get(1).getValue());
//				TraceUtil.logValue(tGap, saveGap);

				// TraceUtil.logValue(tDist, 0);

				String s = i + "\t" + traceLog.getLine(i / logInt);

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

	private static double updateErrorRate(int i, int nRun, double error, double accRate ) {
		
//		if(i < (nRun/5)) {
//			if(accRate < 0.1){
//				error += 0.01;
//			}
//			else if (accRate > 0.4){
//				error -= 0.01;
//			}
//			if(error<0.01){
//				error = 0.01;
//			}
//			if(error > 0.5){
//				error = 0.5;
//			}
//			System.out.println("accRate:\t"+accRate+"\t"+error);
//		}
		return error;
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


	public static double[] calObsStat(Setting setting) {
	
		System.out.println("Calculate observed stat");
		SiteAlignment sa = new SiteAlignment(setting);
		
//		Importer imp = new Importer(setting.getDataFile());
		AlignmentStatFlex aliStat = new AlignmentStatFlex(setting);

		try {

			NexusImporter imp = new NexusImporter(new FileReader(
					setting.getDataFile()));
			sa.updateAlignment(imp.importAlignments());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		aliStat.updateSiteAlignment(sa);

		aliStat.calSumStat();
	
		System.out.println("Observed stat:\t"+aliStat.toString());
		
		return aliStat.getSummaryStatAll();
	
	}


	@SuppressWarnings("rawtypes")
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
			System.out.println(logMuTheta.length +"\t"+ Arrays.toString(logMuTheta) );
			System.out.println(Arrays.toString(xxData[0]));
			Regression lm = new Regression(xxData, logMuTheta );
			
			lm.linear();	
			double[] coef = lm.getBestEstimates();
			sStat.addCoef("Mu", coef);
			sStat.addCoef("Theta", coef);

		}
		else{

//			double[] allMu = traceLogParam.toArray(0);
//			double[] allTheta = traceLogParam.toArray(1);
//			double[] logMuTheta = new double[allTheta.length];
//			for (int i = 0; i < allTheta.length; i++) {
//				logMuTheta[i] = Math.log(allMu[i]*allTheta[i]);
//			}
//			Regression lm = new Regression(xxData, logMuTheta );
			
			Regression lm = new Regression(xxData, traceLogParam.toArray(0) );
			lm.linear();	
	
			double[] coef = lm.getBestEstimates();
			sStat.addCoef("Mu", coef);
	
			lm.enterData(xxData, traceLogParam.toArray(1));
			lm.linear();
			coef = lm.getBestEstimates();
			sStat.addCoef("Theta", coef);
		}
		return sStat;
	}



}
