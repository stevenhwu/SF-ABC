package sw.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.common.primitives.Doubles;
import sw.abc.parameter.ArrayLogFormatterD;
import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.Parameters;
import sw.abc.stat.data.AlignmentStat;
import sw.abc.stat.data.AlignmentStatFlex;
import sw.abc.stat.summary.*;

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
	public static final String SYSSEP = System.getProperty("file.separator");
	
	public static String userDir = System.getProperty("user.dir");
	// public static String fileName = "Shankarappa.Patient9.nex";
	private static String alignmentName = "jt.paup";
	private static String controlName = "jt.par";
	private static String resultName = "summary.log";

	private static String[] switchPar = new String[] { "-t", "-f" };
	private static String noSimPerPar = "1";
	private static int NO_SEQ_PER_TIME = 100;
	private static String softwareName = "BCC_fixParams";
	/*
		param:
		args[0]: ==0 local   ==1  /scratch on DSCR
		args[1]: data file 
		args[2]: 
		e.g. 0 simData.paup 1
	*/
	public static void main(String[] args) throws Exception {

		
		int noItePreprocess = 500_000;
		int noIteMCMC = 1_000_000;
		int thinning = 1000;
		double error = 0.01;
				
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
		Setup setting = ABCSetup(dataDir, null, obsDataName, NO_SEQ_PER_TIME, 2, initValue);
		
		SummaryStat sumStat = generateStatFile(noItePreprocess, thinning, setting);
//		SummaryStat sumStat = new SStatSmall();

		setting.setStat(sumStat);
		setting.setNoSeqPerTime(40);
		AlignmentStatFlex obsDataStat = calObsStat(setting);
		
		setting.setNoSeqPerTime(NO_SEQ_PER_TIME);
		
		ABCUpdateMCMC(setting, noIteMCMC, thinning, error, obsDataStat);



	}


	public static Setup ABCSetup(String dataDir, SummaryStat stat,
			String obsFileName, int noSeqPerTime, int noTime, double[] initValue) {

		
		int seqLength = 750;
		String[] paramList = new String[]{"Mu", "Theta"};
		String[] statList = new String[]{"dist", "chisq", "var", "sitePattern"};
//		String[] statList = new String[]{ "sitePattern"};
				
		Setup setting = new Setup(dataDir, obsFileName);
		setting.setStat(stat);
		setting.setObsFile(obsFileName);
		setting.setAlignmentFile(alignmentName);
		setting.setBCCControlFile(controlName);
		setting.setResultFile(resultName);
		setting.setParamList(paramList);
		setting.setStatList(statList);
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime);

		double muMean = initValue[0] * seqLength;
		double muLower = muMean / 10;
		double muUpper = muMean * 10;
		
		double thetaMean = initValue[1];
		double thetaLower = 100;
		double thetaUpper = thetaMean * 5;
		
//		ArrayList<Parameters> allParUniformPrior  = new ArrayList<Parameters>();
		ParaMu priorMu = new ParaMu(new UniformDistribution(muLower, muUpper));
		ParaTheta priorTheta = new ParaTheta(new UniformDistribution(thetaLower, thetaUpper));
//		allParUniformPrior.add(pMu);
//		allParUniformPrior.add(pTheta);
//		setting.setallParUniformPrior(allParUniformPrior);
		setting.setallParPrior(priorMu, priorTheta);

		
//		ArrayList<Parameters> allPar = new ArrayList<Parameters>();
//		pMu = new ParaMu(new UniformDistribution(1E-5 * seqLength / 3, 1E-5 * seqLength * 3));
		ParaMu pMu = new ParaMu(new TruncatedNormalDistribution(muMean, muMean/2, muLower, muUpper));
		pMu.setProposal(new Scale(0.75));
		pMu.setInitValue(muMean);
		
		ParaTheta pTheta = new ParaTheta(new TruncatedNormalDistribution(thetaMean, thetaMean/2, thetaLower, thetaUpper));
		pTheta.setProposal(new Scale(0.75));
		pTheta.setInitValue(thetaMean);
		// ParaTheta pTheta = new ParaTheta(new OneOverDistribution(3000));
		// pTheta.setProposal(new NormalDistribution(3000, 100));
//		allPar.add(pMu);
//		allPar.add(pTheta);
		setting.setAllPar(pMu, pTheta);
		
			// ParaMu pMu = new ParaMu(new UniformDistribution(1E-5*seqLength/3,
		// 1E-5*seqLength*3));
		// ParaTheta pTheta = new ParaTheta(new UniformDistribution(1, 5000));
		// ParaMu pMu = new ParaMu(new NormalDistribution(1E-5 * 750, 1E-6 *
		// 750));

		// ParaMu pMu = new ParaMu(new UniformDistribution(0, 1));
		// ParaMu pMu = new ParaMu(new UniformDistribution(1E-5*seqLength*0.95,
		// 1E-5*seqLength*1.05));
		// pMu.setProposal(new UniformDistribution(1E-5*seqLength,
		// 1E-5*seqLength));
		
		// pMu.setProposal(new NormalDistribution(1E-5*750, 5E-7*750));

		// ParaTheta pTheta = new ParaTheta(new UniformDistribution(2900,
		// 3100));
		
		return setting;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void ABCUpdateMCMC(Setup setting, int nRun, int logInt,
			double error, AlignmentStatFlex obsStat) throws Exception {

		System.out.println("Start ABCMCMC");
		long startTime = System.nanoTime();

		ArrayList<Parameters> allPar = setting.getAllPar();
		int noPar = allPar.size();
		int noTime = setting.getNoTime();
		double[] saveGap = new double[noPar];

		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);


		String[] paramList = new String[setting.getParamList().length+1];
		System.arraycopy(setting.getParamList(), 0, paramList, 0, setting.getParamList().length);
		paramList[setting.getParamList().length] = "Gap";

		TraceUtil ut = new TraceUtil(noTime, noPar);
		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAL(paramList));
		

		CreateControlFile cFile = new CreateControlFile(setting.getBCCControlFile());
		RunExt proc = new RunExt(setting.getfWorkingDir());
		proc.setPar(softwareName, cFile.getControlFile(), noSimPerPar, switchPar);

		cFile.setInitPar(allPar);
		cFile.updateFile(noTime);

		double[] logValues = Doubles.concat(cFile.getAllPar(), saveGap);
		traceLog.logValues( logValues );

		PrintWriter oResult = new PrintWriter(new BufferedWriter(new FileWriter(setting.getResultFile())));
		oResult.println("Ite\t" + traceLog.getLabels());
		oResult.flush();

//		double[] deltaDup = new double[10];
		for (int i = 0; i < nRun; i++) {
			// System.out.println();
			for (int p = 0; p < allPar.size(); p++) {
				cFile.setParProposal(allPar, p);
				cFile.updateFile(noTime);

				sa = updateAlignment(sa, proc, setting);
				newStat.updateSiteAlignment(sa);
				
				saveGap[p] = newStat.calDelta(obsStat);
//				saveGap[p] = newStat.calIndStat(obsStat);
				
				
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

			if ((i % logInt) == 0) {
				System.out.println(i + "\t" + allPar.get(0).getAcceptCount()
						+ "\t" + allPar.get(1).getAcceptCount() + "\t"
						+allPar.get(0).getValue() + "\t" + allPar.get(1).getValue() + "\t"
						+ setting.getWorkingDir());

				logValues = Doubles.concat(new double[]{allPar.get(0).getValue(), allPar.get(1).getValue()}, saveGap);
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
		oResult.close();

		System.out.println("Time: "+(System.nanoTime() - startTime) / 60e6);
		// System.out
		// .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(0))));
		// System.out
		// .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(1))));

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SummaryStat generateStatFile(int nRun, int thinning, Setup setting) {

		System.out.println("Generate stats at:\t"+ setting.getWorkingDir());
		int noTime = setting.getNoTime();
		String[] paramList = setting.getParamList();
		String[] statsList = setting.getStatList();

		ArrayList<Parameters> allParUniformPrior = setting.getallParPrior();//new ArrayList<Parameters>();
		CreateControlFile cFile = new CreateControlFile(setting.getBCCControlFile());
		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
		
		TraceUtil tu = new TraceUtil(noTime);
	
		ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAL(paramList));
		ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAL(statsList));

		RunExt proc = new RunExt(setting.getfWorkingDir());
		proc.setPar(softwareName, cFile.getControlFile(), "1", switchPar);

		long startTime = System.currentTimeMillis();
		try {
			PrintWriter oResult = new PrintWriter(new BufferedWriter(
					new FileWriter(setting.getResultFile())));
			oResult.println("Ite\t" + traceLogParam.getLabels() +"\t"+ traceLogStats.getLabels());
			
			for (int i = 0; i < nRun; i++) {
				cFile.setParPrior(allParUniformPrior);
				cFile.updateFile(noTime);

				sa = updateAlignment(sa, proc, setting);
				newStat.updateSiteAlignment(sa);
				
				traceLogParam.logValues( cFile.getAllPar());
				traceLogStats.logValues( newStat.getCurStat(statsList));
				oResult.println(i + "\t" + traceLogParam.getLine(i) + "\t"	+ traceLogStats.getLine(i));

				if ((i % thinning) == 0) {
					oResult.flush();
					System.out.println("Ite:\t"+i+"\t"+ ((System.currentTimeMillis() - startTime)/60000)+" mins");
				}
			}
			oResult.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Time:\t" + ( (System.currentTimeMillis()-startTime)/60000) );
		SStatFlexable sStat = semiAutoRegression(traceLogStats, traceLogParam);
		System.out.println(sStat.toString());
		try {
			String outFile = setting.getWorkingDir()+"regressinoCoefSummary";
			PrintWriter oResult = new PrintWriter(new BufferedWriter(new FileWriter(outFile )));
			oResult.println(sStat.toString());
			oResult.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			
		
		 
		return sStat;
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
	private static SStatFlexable semiAutoRegression(ArrayLogFormatterD traceLogStats, ArrayLogFormatterD traceLogParam) {
		double[][] xxData = traceLogStats.to2DArray();
		SStatFlexable sStat = new SStatFlexable();
		
		Regression lm = new Regression(xxData, traceLogParam.toArray(0) );
		lm.linear();
		double[] coef = lm.getBestEstimates();
		sStat.addCoef("Mu", coef);

		lm.enterData(xxData, traceLogParam.toArray(1));
		lm.linear();
		coef = lm.getBestEstimates();
		sStat.addCoef("Theta", coef);
		
		return sStat;
	}


	private static SiteAlignment updateAlignment(SiteAlignment sa, RunExt proc,
			Setup setting) throws Exception {

		Alignment ali = null;
		boolean isReRun = true;
		Importer imp = new Importer(setting); 
		while (isReRun) {

			proc.run();
			// newAli = new Importer(setting).importAlignment();
			imp.updateAliFile(setting.getAlignmentFile());
			ali = imp.importAlignment();
			if (ali != null) {
				isReRun = false;
			}
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
