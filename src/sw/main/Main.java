package sw.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.util.MathUtils;

import sw.abc.parameter.AbstractParameter;

import sw.abc.parameter.ArrayLogFormatterD;
import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.Parameters;
import sw.abc.stat.data.AlignmentStat;
import sw.abc.stat.data.FrequencyStat;
import sw.abc.stat.summary.*;

import sw.math.NormalDistribution;

import sw.math.Combination;
import sw.math.OneOverDistribution;
import sw.math.Scale;
import sw.math.UniformDistribution;
import sw.math.ZTestDistribution;
import sw.process.RunExt;
import sw.sequence.Importer;
import sw.sequence.Site;

import sw.sequence.SiteAlignment;
import sw.util.TraceUtil;
import dr.evolution.alignment.Alignment;
import dr.inference.trace.*;
import dr.inference.trace.TraceFactory.TraceType;
import flanagan.analysis.Regression;

public class Main {
	/**
	 * 
	 * @author Steven Wu
	 * @version $Id$
	 */
	final private static Double DOUBLE_ZERO = new Double(0);

	public static String sysSep = System.getProperty("file.separator");
	public static String userDir = System.getProperty("user.dir");
	// public static String fileName = "Shankarappa.Patient9.nex";
	private static String alignmentName = "jt.paup";
	private static String controlName = "jt.par";
	private static String resultName = "summary.log";

	private static String[] switchPar = new String[] { "-t", "-f" };
	private static String noSimPerPar = "1";

	private static int NOSEQPERTIME = 100;

	public static void main(String[] args) throws Exception {

		Setup setting;

		String dataDir = "/dev/shm/testLargeSample/";
		// String dataDir = userDir;
		
		
		setting = ABCSetup(dataDir, null, "simData.paup", 40, 2);
//		generateStatFile(500, setting);

//		SummaryStat sumStat = new SStatSitePattern();
		SummaryStat sumStat = new SStatTopFreqSingleProduct();
		setting = ABCSetup(dataDir, sumStat, "simData.paup", 40, 2);
		
//		SummaryStat sumStat = new 
		AlignmentStat obsDataStat = calObsStat(setting);
		

		setting = ABCSetup(dataDir, sumStat, "simData.paup", NOSEQPERTIME, 2);
//		ABCUpdateMCMC(setting, 1000, 10, 0.01, obsDataStat);

		// testRead();

	}

	private static void testRead() throws IOException {

	}

	public static Setup ABCSetup(String dataDir, SummaryStat stat,
			String obsFileName, int noS, int noTime) {

		Setup setting = new Setup(dataDir);
		setting.setStat(stat);
		setting.setObsFile(obsFileName);
		setting.setAlignmentFile(alignmentName);
		setting.setBCCControlFile(controlName);
		setting.setResultFile(resultName);

		int noSeqPerTime = noS;
		int seqLength = 750;
		setting.setSeqInfo(seqLength, noSeqPerTime, noTime);

		ArrayList<Trace<Double>> allTrace = new ArrayList<Trace<Double>>();
		ArrayList<Parameters> allPar = new ArrayList<Parameters>();

		// ParaMu pMu = new ParaMu(new UniformDistribution(1E-5*seqLength/3,
		// 1E-5*seqLength*3));
		// ParaTheta pTheta = new ParaTheta(new UniformDistribution(1, 5000));
		// ParaMu pMu = new ParaMu(new NormalDistribution(1E-5 * 750, 1E-6 *
		// 750));

		// ParaMu pMu = new ParaMu(new UniformDistribution(0, 1));
		// ParaMu pMu = new ParaMu(new UniformDistribution(1E-5*seqLength*0.95,
		// 1E-5*seqLength*1.05));
		ParaMu pMu = new ParaMu(new UniformDistribution(1E-5 * seqLength / 3,
				1E-5 * seqLength * 3));
		pMu.setProposal(new Scale(0.75));
		// pMu.setProposal(new UniformDistribution(1E-5*seqLength,
		// 1E-5*seqLength));
		pMu.setInitValue(1E-5 * seqLength);
		// pMu.setProposal(new NormalDistribution(1E-5*750, 5E-7*750));

		// ParaTheta pTheta = new ParaTheta(new UniformDistribution(2900,
		// 3100));
		ParaTheta pTheta = new ParaTheta(new UniformDistribution(1000, 5000));
		// ParaTheta pTheta = new ParaTheta(new OneOverDistribution(3000));
		pTheta.setProposal(new Scale(0.75));
		pTheta.setInitValue(3000);

		// pTheta.setProposal(new NormalDistribution(3000, 100));
		allPar.add(pMu);
		allPar.add(pTheta);

		// ArrayList<Trace<Double>> allTrace = new ArrayList<Trace<Double>>();
		Trace<Double> allMu = new Trace<Double>("Mu",
				TraceFactory.TraceType.DOUBLE);
		Trace<Double> allTheta = new Trace<Double>("theta",
				TraceFactory.TraceType.DOUBLE);
		Trace<Double> allGap1 = new Trace<Double>("gap",
				TraceFactory.TraceType.DOUBLE);
		Trace<Double> allGap2 = new Trace<Double>("gap",
				TraceFactory.TraceType.DOUBLE);
		allTrace.add(allMu);
		allTrace.add(allTheta);
		allTrace.add(allGap1);
		allTrace.add(allGap2);

		setting.setAllPar(allPar);
		setting.setAllTrace(allTrace);
		return setting;
	}

	public static AlignmentStat calObsStat(Setup setting) {

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void ABCUpdateMCMC(Setup setting, int nRun, int logInt,
			double error, AlignmentStat obsStat) throws Exception {

		long startTime = System.nanoTime();

		ArrayList<Parameters> allPar = setting.getAllPar();
		int noPar = allPar.size();
		int noTime = setting.getNoTime();
		double[] saveGap = new double[noPar];

		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStat newStat = new AlignmentStat(setting);

		Trace tMu = TraceUtil.creatTrace("Mu");
		Trace tTheta = TraceUtil.creatTrace("Theta");
		ArrayList<Trace> tGap = TraceUtil.creatTrace(noTime, "gap");
		// ArrayList<Trace> tDist = TraceUtil.creatTraceDist(noTime);

		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6);
		traceLog.addTrace(tMu, tTheta);
		traceLog.addTrace(tGap);

		CreateControlFile cFile = new CreateControlFile(
				setting.getBCCControlFile());
		RunExt proc = new RunExt(setting.getfWorkingDir());
		proc.setPar("./BCC", cFile.getControlFile(), noSimPerPar, switchPar);

		cFile.setInitPar(allPar);
		cFile.updateFile(noTime);

		TraceUtil.logValue(tMu, cFile.getMu());
		TraceUtil.logValue(tTheta, cFile.getTheta());
		TraceUtil.logValue(tGap, 0);
		// TraceUtil.logValue(tDist, 0);

		PrintWriter oResult = new PrintWriter(new BufferedWriter(
				new FileWriter(setting.getResultFile())));
		oResult.println("Ite\t" + traceLog.getLabels());
		oResult.flush();

		double[] deltaDup = new double[10];
		for (int i = 0; i < nRun; i++) {
			// System.out.println();
			for (int p = 0; p < allPar.size(); p++) {
				cFile.setParProposal(allPar, p);
				cFile.updateFile(noTime);

				sa = updateAlignment(sa, proc, setting);
				newStat.updateSiteAlignment(sa);
				// saveGap[p] = newStat.calDelta(obsStat);
				saveGap[p] = newStat.calIndStat(obsStat);
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
						+ cFile.getMu() + "\t" + cFile.getTheta() + "\t"
						+ setting.getWorkingDir());

				TraceUtil.logValue(tMu, allPar.get(0).getValue());
				TraceUtil.logValue(tTheta, allPar.get(1).getValue());
				TraceUtil.logValue(tGap, saveGap);

				// TraceUtil.logValue(tDist, 0);

				String s = i + "\t" + traceLog.getLine(i / logInt);

				// System.out.println(s + "\t" + setting.getWorkingDir());
				oResult.println(s);
				oResult.flush();
			}

		}
		oResult.close();

		System.out.println((System.nanoTime() - startTime) / 1000 / 1000);
		// System.out
		// .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(0))));
		// System.out
		// .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(1))));

	}

	@SuppressWarnings("rawtypes")
	public static void generateStatFile(int nRun, Setup setting) {

		System.out.println("Generate stats\t"+ setting.getWorkingDir());
		int seqLength = 750;
		int noTime = setting.getNoTime();

		ArrayList<Parameters> allPar = new ArrayList<Parameters>();

		ParaMu pMu = new ParaMu(new UniformDistribution(1E-5 / 3 * seqLength, 1E-5 * 3 * seqLength));
		ParaTheta pTheta = new ParaTheta(new UniformDistribution(1000, 5000));

//		ParaMu pMu = new ParaMu(new ZTestDistribution(1E-5 * seqLength));
//		ParaTheta pTheta = new ParaTheta(new ZTestDistribution( 3000));
				
		allPar.add(pMu);
		allPar.add(pTheta);

		// Setup setting = new Setup(dataDir);
		//
		// // setting.setDataFile(dataName);
		// setting.setAlignmentFile(alignmentName);
		// setting.setBCCControlFile(controlName);
		// setting.setResultFile(resultName);
		// setting.setSeqInfo(seqLength, noSeqPerTime, noTime);

		CreateControlFile cFile = new CreateControlFile(
				setting.getBCCControlFile());
		SiteAlignment sa = new SiteAlignment(setting);
		AlignmentStat newStat = new AlignmentStat();
		// ArrayList<Trace<Double>> allTrace = new ArrayList<Trace<Double>>();

		Trace tMu = TraceUtil.creatTrace("Mu");
		Trace tTheta = TraceUtil.creatTrace("Theta");

		ArrayList<Trace> tDist = TraceUtil.creatTraceDist(noTime);

		ArrayList<Trace> tChisq = TraceUtil.creatTrace(noTime, "chisq");
		ArrayList<Trace> tVar = TraceUtil.creatTrace(noTime, "var");

		ArrayList<Trace> tPattern = TraceUtil.creatTrace(Site.PATTERN, "sitePattern");
		ArrayList<Trace> tFreq1 = TraceUtil.creatTrace(9, "ferqT1");
		ArrayList<Trace> tFreq2 = TraceUtil.creatTrace(9, "ferqT2");

		ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6);
		ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6);
		traceLogParam.addTrace(tMu, tTheta);
		traceLogStats.addTrace(tDist, tChisq, tVar, tPattern);//, tFreq1, tFreq2);


		RunExt proc = new RunExt(setting.getfWorkingDir());
		proc.setPar("./BCC", cFile.getControlFile(), "1", switchPar);

		long startTime = System.currentTimeMillis();
		try {
			PrintWriter oResult = new PrintWriter(new BufferedWriter(
					new FileWriter(setting.getResultFile())));

			oResult.println("Ite\t" + traceLogParam.getLabels() +"\t"+ traceLogStats.getLabels());

			for (int i = 0; i < nRun; i++) {
			
				cFile.setParPrior(allPar);
				cFile.updateFile(noTime);

				sa = updateAlignment(sa, proc, setting);
				newStat.updateSiteAlignment(sa);
				
				TraceUtil.logValue(tMu, cFile.getMu());
				TraceUtil.logValue(tTheta, cFile.getTheta());

				TraceUtil.logValue(tDist, newStat.getSiteDists());
				TraceUtil.logValue(tChisq, newStat.getSiteChiDist());
				TraceUtil.logValue(tVar,  newStat.getSiteVarinace());
				TraceUtil.logValue(tPattern,  newStat.getSitePattern()[0]);
//				TraceUtil.logValue(tFreq1, newStat.getSiteFreqSpecEach()[0]);
//				TraceUtil.logValue(tFreq2, newStat.getSiteFreqSpecEach()[1]);

				oResult.println(i + "\t" + traceLogParam.getLine(i) + "\t"
						+ traceLogStats.getLine(i));

				// TEMP
				// double[] record = readBrLn(setting);
				// String tempOut = Arrays.toString(record);
				// oResult2.println(tempOut.substring(1, tempOut.length()-1));
				// oResult2.flush();

				if ((i % 100) == 0) {
					oResult.flush();
					System.out.println(i+"\t"+ ((System.currentTimeMillis() - startTime) / 1000));
				}

			}

			oResult.close();
			// oResult2.close();

			// cleanUpFiles(dataDir);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println((System.nanoTime() - startTime) / 1000 / 1000);
		//
		Regression lm = new Regression(traceLogStats.to2DArray(), traceLogParam.toArray(0) );
		 // lm.setAllX(allDist);
//		 lm.addX() ;
//		 lm.setY(allTheta);
		 lm.linear();//runNoIntercept();
//		
		 System.out.println(ArrayUtils.toString( lm.getBestEstimates() ));
		 System.out.println(ArrayUtils.toString( lm.getCoeff() ));
		 System.out.println(ArrayUtils.toString( lm.getPvalues() ));

		 //		
//		 lm.setY(allMu);
//		 lm.runNoIntercept();
//		
//		 System.out.println(lm.estimateRegressionParametersToString());
//		 System.out.println(lm.caclulateRSquared());
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