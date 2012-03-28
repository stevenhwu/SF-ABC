package sw.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;


import com.google.common.primitives.Doubles;
import sw.abc.parameter.ArrayLogFormatterD;
import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.Parameters;
import sw.abc.parameter.SavePar;
import sw.abc.parameter.TunePar;
import sw.abc.stat.data.AlignmentStat;
import sw.abc.stat.data.AlignmentStatFlex;
import sw.abc.stat.summary.*;

import sw.math.NormalDistribution;
import sw.math.Scale;
import sw.math.TruncatedNormalDistribution;
import sw.math.UniformDistribution;
import sw.process.RunExt;
import sw.sequence.Importer;
import sw.sequence.SiteAlignment;
import sw.util.TraceUtil;
import dr.evolution.alignment.Alignment;
import flanagan.analysis.Regression;


public class Main {
	/**
	 * 
	 * @author Steven Wu
	 * @version $Id$
	 */
	public static final char SYSSEP = File.separatorChar; //System.getProperty("file.separator");
	public static final String USERDIR = System.getProperty("user.dir")+File.separatorChar;
	public static final int TUNESIZE = 10 ;
	public static final int TUNEGROUP = 6;
	
	// public static String fileName = "Shankarappa.Patient9.nex";
	private static String alignmentName = "jt.paup";
	private static String controlName = "jt.par";

	private static String[] switchPar = new String[] { "-t", "-f" };
	private static String noSimPerPar = "1";
	private static int NO_SEQ_PER_TIME = 50;
	private static String softwareName = "BCC_fixParams";
	
	
	/*
		param:
		args[0]: ==0 local   ==1  /scratch on DSCR
		args[1]: data file 
		args[2]: noItePreprocess
		args[3]: noIteMCMC
		args[4]: thinning
		args[5]: error
		e.g.	0 simData.paup 50 100 10 0.1 
		e.g.	0 simData.paup 500000 1000000 1000 0.01
	*/
	public static void main(String[] args) {
		
		int noTimePoint = 3;
		
		int noItePreprocess = Integer.parseInt(args[2]); //TODO change back
		int noIteMCMC = Integer.parseInt(args[3]); //TODO change back
		int thinning = Integer.parseInt(args[4]);
		double error = Double.parseDouble(args[5]);
		String summarySetting = noItePreprocess +"\t" + noIteMCMC +"\t" + thinning +"\t" + error;
		// args[0]==0 local   ==1  /scratch
		String obsDataName = args[1];
		String obsDataNamePrefix = obsDataName.split("\\.")[0];
		String dataDir = "/dev/shm/"+obsDataNamePrefix+SYSSEP;
		if(args[0].equalsIgnoreCase("1")){
			dataDir = "/scratch/sw167/"+obsDataNamePrefix+SYSSEP;
		}
		File f = new File(dataDir);
		if(!f.exists()){
			System.out.println("mkdir "+f.toString()+"\t"+f.mkdir());
			System.out.println(f.exists());
		}
		
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
		

		//		Setup setting = ABCSetup(dataDir, null, "simData.paup", NO_SEQ_PER_TIME, 2);
		Setup setting = ABCSetup(dataDir, null, obsDataName, NO_SEQ_PER_TIME, noTimePoint , initValue, summarySetting);
		
		SummaryStat sumStat = null;
		sumStat = generateStatFile(noItePreprocess, thinning, setting);


		setting.setStat(sumStat);
		setting.setNoSeqPerTime(40);
		
		AlignmentStatFlex obsDataStat = calObsStat(setting);
		
		setting.setNoSeqPerTime(NO_SEQ_PER_TIME);
		
		try {
			ABCUpdateMCMC(setting, noIteMCMC, thinning, error, obsDataStat);
		} catch (Exception e) {
			e.printStackTrace();
		}



	}


	public static Setup ABCSetup(String dataDir, SummaryStat stat,
			String obsFileName, int noSeqPerTime, int noTime, double[] initValue, String summarySetting) {

		
		int seqLength = 750;
		String[] paramList = new String[]{"Mu", "Theta"};
		String[] statList = new String[]{"dist", "chisq", "var", "sitePattern"};
//		String[] statList = new String[]{ "sitePattern"};
				
		Setup setting = new Setup(dataDir, obsFileName);
		setting.setOutputFiles(USERDIR, obsFileName);
		
		setting.setSummarySetting(summarySetting);
		setting.setStat(stat);
		setting.setObsFile(obsFileName);
		setting.setAlignmentFile(alignmentName);
		setting.setBCCControlFile(controlName);
		setting.setParamList(paramList);
		setting.setStatList(statList);
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime);

		double muMean = initValue[0] * seqLength;
		double muTune = muMean/10;
		double muLower = muMean / 10;
		double muUpper = muMean * 10;
		
		double thetaMean = initValue[1];
		double thetaTune = thetaMean/10;
		double thetaLower = thetaMean / 5;;
		double thetaUpper = thetaMean * 5;
		
		ParaMu priorMu = new ParaMu(new UniformDistribution(muLower, muUpper));
		ParaTheta priorTheta = new ParaTheta(new UniformDistribution(thetaLower, thetaUpper));

		ParaMu pMu = new ParaMu(new TruncatedNormalDistribution(muMean, muMean/2, muLower, muUpper));
		pMu.setInitValue(muMean);
//		pMu.setInitValue();
		
		ParaTheta pTheta = new ParaTheta(new TruncatedNormalDistribution(thetaMean, thetaMean/2, thetaLower, thetaUpper));
		pTheta.setInitValue(thetaMean);
//		pTheta.setInitValue();
		
		
		double initScale = 0.75;
		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{initScale , initScale}, new String[] { "Scale", "Scale" });
		pMu.setProposal(new Scale(initScale ));
		pTheta.setProposal(new Scale(initScale ));
		
//		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{muTune, thetaTune}, new String[] { "Normal", "Normal" });
//		pMu.setProposal(new NormalDistribution(muMean, muTune));
//		pTheta.setProposal(new NormalDistribution(thetaMean, thetaTune));

		
		
		setting.setTunePar(tPar);
		setting.setAllPar(pMu, pTheta);
		setting.setallParPrior(priorMu, priorTheta);

		
		return setting;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SummaryStat generateStatFile(int nRun, int thinning, Setup setting) {
	
		String outFile =  setting.getRegressionCoefFile();
		
		SStatFlexable sStat = null;
		if(outFile != null){
			sStat = readRegressionFile(outFile);
			System.out.println("Skip preprocessing");
		}
		else{
			
			
			System.out.println("Generate stats at:\t"+ setting.getWorkingDir());
			int noTime = setting.getNoTime();
			String[] paramList = setting.getParamList();
			String[] statsList = setting.getStatList();
	
			ArrayList<Parameters> allParUniformPrior = setting.getallParPrior();//new ArrayList<Parameters>();
			CreateControlFile cFile = new CreateControlFile(setting.getBCCControlFile(), noTime, setting.getNoSeqPerTime());
			SiteAlignment sa = new SiteAlignment(setting);
			AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
			
			TraceUtil tu = new TraceUtil(noTime);
		
			ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAL(paramList));
			ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAL(statsList));
	
			RunExt proc = new RunExt(setting.getfWorkingDir());
			proc.setPar(softwareName, cFile.getControlFile(), noSimPerPar, switchPar);
	
			long startTime = System.currentTimeMillis();
			try{	
				PrintWriter oResult = new PrintWriter(new BufferedWriter(
						new FileWriter(setting.getResultOutFile())));
				oResult.println("Ite\t" + traceLogParam.getLabels() +"\t"+ traceLogStats.getLabels());
				
				for (int i = 0; i < nRun; i++) {
					cFile.setParPrior(allParUniformPrior);
					
					cFile.updateFile();
					sa = updateAlignment(sa, proc, setting);
					newStat.updateSiteAlignment(sa);
					
					traceLogParam.logValues( cFile.getAllPar());
					traceLogStats.logValues( newStat.getCurStat(statsList));
					oResult.println(i + "\t" + traceLogParam.getLine(i) + "\t"	+ traceLogStats.getLine(i));
		
					if ((i % thinning) == 0) {
						oResult.flush();
						System.out.println("Ite:\t"+i+"\t"+ Math.round((System.currentTimeMillis() - startTime)/60e3)+" mins");
					}
				}
				oResult.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Time:\t" + Math.round( (System.currentTimeMillis()-startTime)/60e3)+" mins" );	
			sStat = semiAutoRegression(nRun, traceLogStats, traceLogParam, 2);
			String regressionSummary = sStat.toString();
			System.out.println(regressionSummary);
	
			
			try {	
				
				outFile =  setting.getRegressionOutFile();
				PrintWriter oResult1 = new PrintWriter(new BufferedWriter(
						new FileWriter(outFile)));
				oResult1.println("# " + setting.getDataFile() + "\t"
						+ setting.getSummarySetting());
				oResult1.println(regressionSummary);
				oResult1.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return sStat;
		
		
		 
	
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void ABCUpdateMCMC(Setup setting, int nRun, int logInt,
			double error, AlignmentStatFlex obsStat) throws Exception {

		System.out.println("Start ABCMCMC");
		long startTime = System.currentTimeMillis();

		ArrayList<Parameters> allPar = setting.getAllPar();
		TunePar tPar = setting.getTunePar(); 
		int noPar = allPar.size();
		int noTime = setting.getNoTime();
		double[] saveGap = new double[noPar];
		SavePar sPar = new SavePar(TUNESIZE);
		
		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);


//		String[] paramList = new String[setting.getParamList().length+1];
//		System.arraycopy(setting.getParamList(), 0, paramList, 0, setting.getParamList().length);
//		paramList[setting.getParamList().length] = "Gap";
//		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAL(paramList));
		
		TraceUtil ut = new TraceUtil(noTime, noPar);
		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAL(setting.getParamList()));
		

		CreateControlFile cFile = new CreateControlFile(setting.getBCCControlFile(), noTime, setting.getNoSeqPerTime());
		RunExt proc = new RunExt(setting.getfWorkingDir());
		proc.setPar(softwareName, cFile.getControlFile(), noSimPerPar, switchPar);

		cFile.setInitPar(allPar);
		cFile.updateFile();

//		double[] logValues = Doubles.concat(cFile.getAllPar(), saveGap);
		double[] logValues = Doubles.concat(cFile.getAllPar());
		traceLog.logValues( logValues );

		PrintWriter oResult = new PrintWriter(new BufferedWriter(new FileWriter(setting.getResultOutFile())));
		oResult.println("# "+setting.getDataFile()+"\t"+setting.getSummarySetting());
		oResult.println("Ite\t" + traceLog.getLabels());
		oResult.flush();

//		double[] deltaDup = new double[10];
		
		double errorInit = error;
		for (int i = 0; i < nRun; i++) {

			
			for (int p = 0; p < allPar.size(); p++) {
				cFile.setParProposal(allPar, p);
				
				cFile.updateFile();
				
				sa = updateAlignment(sa, proc, setting);
				newStat.updateSiteAlignment(sa);
				
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
					// System.out.print("FreqGap: " + saveGap[p]+"\t");

					if (MH.accept(allPar.get(p))) {
						allPar.get(p).acceptNewValue();
						// System.out.println( i + "\t" +
						// allPar.get(p).getAcceptCount() + "\t" + cFile.getMu()
						// + "\t" + cFile.getTheta() + "\t"+
						// setting.getWorkingDir());
					}

				}
			}
			sPar.add(allPar);
			
			if (i % TUNESIZE == 0 & i!=0) {
				
				tPar.update(sPar, i);
//				System.out.println(Arrays.toString(tPar.getTunePar()));
				
				for (int j = 0; j < allPar.size(); j++) {
					allPar.get(j).updateProposal(tPar.getTunePar(j));
				}
//				double accRate = tPar.getMeanAccRate();
//				error = updateErrorRate(i, nRun, error, accRate );
				
				
				if (allPar.get(0).getAcceptCount() == 0 & allPar.get(1).getAcceptCount() == 0 & i>(TUNESIZE*10)){
					allPar.get(0).nextPrior();
					allPar.get(1).nextPrior();
					
				}
				
			}
			
			if ((i % logInt) == 0) {
				System.out.println("Ite:\t"+i + "\t"+ allPar.get(0).getAcceptCount() +
						"\t" + allPar.get(1).getAcceptCount() + "\t" +
						Arrays.toString(tPar.getEachAccRate()) + "\t" +
//						+allPar.get(0).getValue() + "\t" + allPar.get(1).getValue() + "\t"
						setting.getWorkingDir());

//				logValues = Doubles.concat(new double[]{allPar.get(0).getValue(), allPar.get(1).getValue()}, saveGap);
				logValues = Doubles.concat(new double[]{allPar.get(0).getValue(), allPar.get(1).getValue()});
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


	private static SStatFlexable readRegressionFile(String outFile) {
		
		System.out.println("Parse existing coef file: "+outFile.toString());
		SStatFlexable sStat = new SStatFlexable();
		try {
			BufferedReader br = new BufferedReader(new FileReader(outFile));
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


	public static AlignmentStatFlex calObsStat(Setup setting) {
	
		System.out.println("Calculate observed stat");
		SiteAlignment sa = new SiteAlignment(setting);
		Importer imp = new Importer(setting.getDataFile(), setting);
		AlignmentStatFlex aliStat = new AlignmentStatFlex(setting);
	
		sa.updateAlignment(imp);
		aliStat.updateSiteAlignment(sa);
	
		aliStat.calSumStat();
	
		System.out.println("Observed stat:\t"+aliStat.toString());
	
		return aliStat;
	
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


	private static SiteAlignment updateAlignment(SiteAlignment sa, RunExt proc,
			Setup setting) throws IOException {

		Alignment ali = null;
		boolean isReRun = true;
		Importer imp = new Importer(setting); 
		
		while (isReRun) {

			proc.run();
			// newAli = new Importer(setting).importAlignment();
//			File f = new File("/home/sw167/devshmLink/simData/jt.paup");
//			f.delete();
			ali = imp.importAlignment();
			
			if (ali != null) {
				isReRun = false;
			}
//			if(isReRun){
//				System.out.println("rerun Seq: "+ali);
//				imp.testingOutPut(USERDIR);
//				System.exit(-999);
//			}
		}

		sa.updateAlignment(ali);

		return sa;
	}


	@Deprecated
	public static AlignmentStat calObsStatOld(Setup setting) {
	
		SiteAlignment sa = new SiteAlignment(setting);
		Importer imp = new Importer(setting.getDataFile(), setting);
		AlignmentStat aliStat = new AlignmentStat(setting);
	
		sa.updateAlignment(imp);
		aliStat.updateSiteAlignment(sa);
		// aliStat.addBrLn(readBrLn(setting));
	
		aliStat.calSumStat();
		// aliStat.calMultiObsStat();
	
		// TEMP
		System.out.println("ObsData: " + aliStat.toString());
	
		return aliStat;
	
	}

}
