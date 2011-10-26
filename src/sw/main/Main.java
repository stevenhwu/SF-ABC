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

	public static void main(String[] args) throws Exception {

		// args[0]==0 local   ==1  /scratch
		String obsDataName = args[1];
		String dataDir = "/dev/shm/"+obsDataName.split("\\.")[0]+"_"+args[2]+SYSSEP;
		if(args[0].equalsIgnoreCase("1")){
			dataDir = "/scratch"+obsDataName.split("\\.")[0]+"_"+args[2]+SYSSEP;
		}
		System.out.println(dataDir);
		File f = new File(dataDir);
		System.out.println(f.getAbsolutePath());
		System.out.println(f.mkdir());
		
		
//		Setup setting = ABCSetup(dataDir, null, "simData.paup", NO_SEQ_PER_TIME, 2);
		Setup setting = ABCSetup(dataDir, null, obsDataName, NO_SEQ_PER_TIME, 2);
		
		System.out.println(Arrays.toString( f.list() ));//TEMP
		
		SummaryStat sumStat = generateStatFile(100, setting);
//		SummaryStat sumStat = new SStatSmall();
		setting.setStat(sumStat);
		setting.setNoSeqPerTime(40);
		AlignmentStatFlex obsDataStat = calObsStat(setting);
		
		setting.setNoSeqPerTime(NO_SEQ_PER_TIME);
		ABCUpdateMCMC(setting, 100, 10, 0.01, obsDataStat);



	}


	public static Setup ABCSetup(String dataDir, SummaryStat stat,
			String obsFileName, int noSeqPerTime, int noTime) {

		
		int seqLength = 750;
		String[] paramList = new String[]{"Mu", "Theta"};
		String[] statList = new String[]{"dist", "chisq", "var", "sitePattern"};
//		String[] statList = new String[]{ "sitePattern"};
				
		Setup setting = new Setup(dataDir);
		setting.setStat(stat);
		setting.setObsFile(obsFileName);
		setting.setAlignmentFile(alignmentName);
		setting.setBCCControlFile(controlName);
		setting.setResultFile(resultName);
		setting.setParamList(paramList);
		setting.setStatList(statList);
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime);

		ArrayList<Parameters> allParUniformPrior  = new ArrayList<Parameters>();
		ParaMu pMu = new ParaMu(new UniformDistribution(1E-5 / 3 * seqLength, 1E-5 * 3 * seqLength));
		ParaTheta pTheta = new ParaTheta(new UniformDistribution(1000, 5000));
		allParUniformPrior.add(pMu);
		allParUniformPrior.add(pTheta);
		setting.setallParUniformPrior(allParUniformPrior);

		
		ArrayList<Parameters> allPar = new ArrayList<Parameters>();
//		pMu = new ParaMu(new UniformDistribution(1E-5 * seqLength / 3, 1E-5 * seqLength * 3));
		pMu = new ParaMu(new TruncatedNormalDistribution(1E-5 * seqLength, 1E-5 * seqLength/3, 1E-7, 1E-4 * seqLength));
		pMu.setProposal(new Scale(0.75));
		pMu.setInitValue(1E-5 * seqLength);
		pTheta = new ParaTheta(new TruncatedNormalDistribution(3000, 500, 100, 10000));
		pTheta.setProposal(new Scale(0.75));
		pTheta.setInitValue(3000);
		// ParaTheta pTheta = new ParaTheta(new OneOverDistribution(3000));
		// pTheta.setProposal(new NormalDistribution(3000, 100));
		allPar.add(pMu);
		allPar.add(pTheta);
		setting.setAllPar(allPar);
		
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
	

	public static AlignmentStatFlex calObsStat(Setup setting) {

		System.out.println("Calculate obs stat");
		SiteAlignment sa = new SiteAlignment(setting);
		Importer imp = new Importer(setting.getDataFile(), setting);
		AlignmentStatFlex aliStat = new AlignmentStatFlex(setting);

		sa.updateAlignment(imp);
		aliStat.updateSiteAlignment(sa);

		aliStat.calSumStat();

		System.out.println(aliStat.toString());

		return aliStat;

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
		proc.setPar("./BCC", cFile.getControlFile(), noSimPerPar, switchPar);

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
	public static SummaryStat generateStatFile(int nRun, Setup setting) {

		System.out.println("Generate stats\t"+ setting.getWorkingDir());
		int noTime = setting.getNoTime();
		String[] paramList = setting.getParamList();
		String[] statsList = setting.getStatList();

		ArrayList<Parameters> allParUniformPrior = setting.getallParUniformPrior();//new ArrayList<Parameters>();
		CreateControlFile cFile = new CreateControlFile(setting.getBCCControlFile());
		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
		
		TraceUtil tu = new TraceUtil(noTime);
	
		ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAL(paramList));
		ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAL(statsList));

		RunExt proc = new RunExt(setting.getfWorkingDir());
		proc.setPar("./BCC", cFile.getControlFile(), "1", switchPar);

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

				if ((i % 100) == 0) {
					oResult.flush();
					System.out.println("Ite:\t"+i+"\t"+ ((System.currentTimeMillis() - startTime)/60000));
				}
			}
			oResult.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Time:\t" + ( (System.currentTimeMillis()-startTime)/60000) );
		SStatFlexable sStat = semiAutoRegression(traceLogStats, traceLogParam);
		
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

/*
 * 
 * Actually, you do not need to "unpack" the JAR, you can just get a hold on the
 * exec's stream using classloader (just as you would access any other kind of
 * file contained in a JAR) and then copy that stream content to a temporary
 * folder on the disk.
 * 
 * Something similar to (from memory): ClassLoader loader =
 * this.getClass().getClassLoader(); URL url =
 * loader.getResource("my/package/mybatchscript.bat"); if (url == null) { throw
 * new IllegalStateException("Could not find script file"); } InputStream in =
 * url.openStream(); try { OutputStream out = new FileOutputStream(...); try {
 * ... } finally { out.close(); } } finally { in.close(); }
 * 
 * 
 * I happen to have a program that does just that (but with an exec instead of a
 * batch file) which has a Java/JavaFX front-end to a C++ fish population model.
 * When a scenario runs, we extract the exec in a temp folder, run it and delete
 * the tmp folder content after. Though now we've moved to a different setup
 * (cause we have to take care of distributing and updating different versions
 * of the exec), in my initial tests, I was simply extracting the demo exec out
 * of the JAR in a similar fashion and it worked just fine.
 * 
 * Note: - the app needs to be signed to be able to write on the local disk (of
 * course). - on Windows, especially on more recent version like Vista and 7,
 * you have to be careful were you try to do your extraction. - on Linux, I had
 * to change the file's permission to make it executable afterward of course by
 * doing a chmod command just before trying to execute the file.
 */