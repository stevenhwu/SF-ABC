package sw.zold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.io.filefilter.FileFilterUtils;

import sw.main.Setup;

@SuppressWarnings("unused")
public class OldMain {
	
	private static double[] readBrLn(Setup setting) throws IOException {
		int noSeqPerTime = setting.getNoSeqPerTime();
		double[] record = new double[noSeqPerTime * setting.getNoTime() - 1];

		BufferedReader in = new BufferedReader(new FileReader(
				setting.getWorkingDir() + "jt.log"));
		String line;
		in.readLine();
		int[] logInfo = new int[4];

		int noBranch = noSeqPerTime + 1;
		int count = 0;
		while ((line = in.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			for (int j = 0; j < logInfo.length; j++) {
				logInfo[j] = Integer.parseInt(st.nextToken());
			}
			if (logInfo[3] != noBranch) {
				int diff = noBranch - logInfo[3];
				noBranch = logInfo[3];
				for (int j = 0; j < diff; j++) {
					record[count] = logInfo[1];
					count++;
				}

			}
		}

		return record;
	}

	private static void cleanUpFiles(String dataDir) {

		File dir = new File(dataDir);
		String[] suffix = new String[] { "bat", "gen", "trees", "csv", "paup",
				"par" };
		for (int i = 0; i < suffix.length; i++) {
			FileFilter filter = FileFilterUtils.suffixFileFilter(suffix[i]);
			File[] allFiles = dir.listFiles(filter);
			for (File file : allFiles) {
				file.delete();
			}
		}
	}

	
	/*
	 * public static void calGenStat(int nRun, String dataDir) {
	 * 
	 * // String dataDir = "/dev/shm/genStatBig2/"; // String dataDir =
	 * "/dev/shm/genStatSmall2/"; System.out.println(dataDir); int noSeqPerTime
	 * = 40; int noTime = 2; int seqLength = 750;
	 * 
	 * ArrayList<Parameters> allPar = new ArrayList<Parameters>();
	 * 
	 * ParaMu pMu = new ParaMu(new UniformDistribution(1E-5 / 2 * seqLength,
	 * 1E-5 * 2 * seqLength)); ParaTheta pTheta = new ParaTheta(new
	 * UniformDistribution(1000, 5000));
	 * 
	 * // ParaMu pMu = new ParaMu(new UniformDistribution(1E-5/10*seqLength, //
	 * 1E-5*10*seqLength)); // ParaTheta pTheta = new ParaTheta(new
	 * UniformDistribution(100, // 10000));
	 * 
	 * allPar.add(pMu); allPar.add(pTheta);
	 * 
	 * Setup setting = new Setup(dataDir);
	 * 
	 * // setting.setDataFile(dataName);
	 * setting.setAlignmentFile(alignmentName);
	 * setting.setBCCControlFile(controlName);
	 * setting.setResultFile(resultName); setting.setSeqInfo(seqLength,
	 * noSeqPerTime, noTime);
	 * 
	 * CreateControlFile cFile = new CreateControlFile(
	 * setting.getBCCControlFile()); SiteAlignment sa = new
	 * SiteAlignment(setting); Alignment newAli = null;
	 * 
	 * // ArrayList<Trace<Double>> allTrace = new ArrayList<Trace<Double>>();
	 * 
	 * ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6);
	 * traceLog.logLabels(new String[] { "Mu", "Theta", "inter01", "intra0",
	 * "intra1", "freq1", "freq2" }); // traceLog.logValues(values)
	 * 
	 * 
	 * 
	 * String[] tempName = new String[42]; for (int i = 0; i < 21; i++) {
	 * tempName[i] = ("F1_" + i); tempName[21 + i] = ("F2_" + i); }
	 * System.out.println(Arrays.toString(tempName)); ArrayLogFormatterD freqLog
	 * = new ArrayLogFormatterD(6); freqLog.logLabels(tempName);
	 * 
	 * // MCLogger mcL = new MCLogger(traceLog, 1, true); // Parameter freqs =
	 * new // Parameter.Default(alignment.getStateFrequencies());//new //
	 * double[]{0.25, 0.25, 0.25, 0.25}); // mcL.
	 * 
	 * // ArrayLogFormatter drformatter = new ArrayLogFormatter(false); //
	 * MCLogger drloggers = new MCLogger(drformatter, 1000, false); //
	 * dr.inference.model.Parameter drmu = new //
	 * dr.inference.model.Parameter.Default( // "Mu", 1, 0.1); //
	 * dr.inference.model.Parameter drtheta = new //
	 * dr.inference.model.Parameter.Default( // "Theta", 1, 3000); // //
	 * drloggers.add(drmu); // drloggers.add(drtheta);
	 * 
	 * // allTrace.add(new Trace<Double>("Mu", TraceFactory.TraceType.DOUBLE));
	 * // allTrace.add(new Trace<Double>("Theta", //
	 * TraceFactory.TraceType.DOUBLE)); // allTrace.add(new
	 * Trace<Double>("inter01", // TraceFactory.TraceType.DOUBLE)); //
	 * allTrace.add(new Trace<Double>("intra0", //
	 * TraceFactory.TraceType.DOUBLE)); // allTrace.add(new
	 * Trace<Double>("intra1", // TraceFactory.TraceType.DOUBLE));
	 * 
	 * RunExt proc = new RunExt(setting.getfWorkingDir()); proc.setPar("./BCC",
	 * setting.getBCCControlFile(), "1", "-p", "-f");
	 * 
	 * long startTime = System.nanoTime(); boolean isReRun = true; try {
	 * PrintWriter oResult = new PrintWriter(new BufferedWriter( new
	 * FileWriter(setting.getResultFile())));
	 * oResult.println(traceLog.getLabels()); double[] allOutValues = new
	 * double[traceLog.getSize()];
	 * 
	 * //  PrintWriter oResultFreq = new PrintWriter(new BufferedWriter( new
	 * FileWriter(setting.getWorkingDir() + "freqLog.log")));
	 * oResultFreq.println(freqLog.getLabels()); double[] allOutFreq = new
	 * double[freqLog.getSize()];
	 * 
	 * for (int i = 0; i < nRun; i++) { for (Parameters parameters : allPar) {
	 * cFile.setParPrior(parameters); } cFile.updateFile();
	 * 
	 * while (isReRun) { proc.run(); newAli = new
	 * Importer(setting.getAlignmentFile(), setting) .importAlignment(); if
	 * (newAli != null) { isReRun = false; } } isReRun = true;
	 * sa.updateAlignment(newAli);
	 * 
	 * AlignmentStat newStat = new AlignmentStat(); newStat.addSiteDists(sa);
	 * newStat.addSiteFreqSpec(sa);
	 * 
	 * double[] siteSimDists = newStat.getSiteDists(); double[] siteChiDist =
	 * newStat.getSiteChiDist();
	 * 
	 * double[][] freqSpecturm = newStat.getSiteFreqSpec();
	 * 
	 * // allMu.add(cFile.getMu()); // allTheta.add(cFile.getTheta()); //
	 * inter01.add(simDists[0]); // intra0.add(simDists[1]); //
	 * intra1.add(simDists[2]);
	 * 
	 * // {cFile.getMu(), cFile.getTheta()}; allOutValues[0] = cFile.getMu();
	 * allOutValues[1] = cFile.getTheta(); for (int j = 0; j <
	 * siteSimDists.length; j++) { allOutValues[2 + j] = siteSimDists[j]; } for
	 * (int j = 0; j < siteChiDist.length; j++) { allOutValues[5 + j] =
	 * siteChiDist[j]; } traceLog.logValues(allOutValues);
	 * 
	 * //  for (int j = 0; j < freqSpecturm[0].length; j++) { allOutFreq[j]
	 * = freqSpecturm[0][j]; allOutFreq[21 + j] = freqSpecturm[1][j]; }
	 * freqLog.logValues(allOutFreq);
	 * 
	 * // oResult.println(TraceUtil.summary(i, allTrace)); oResult.println(i +
	 * ":\t" + traceLog.getLine(i)); oResultFreq.println(i + ":\t" +
	 * freqLog.getLine0(i)); // oResult.println(Arrays.toString(allOutValues));
	 * if ((i % 1000) == 0) { oResult.flush(); oResultFreq.flush();
	 * System.out.println(i + "\t" + ((System.nanoTime() - startTime) / 1000 /
	 * 1000)); } }
	 * 
	 * oResult.close(); oResultFreq.close(); } catch (Exception e) {
	 * e.printStackTrace(); } System.out.println((System.nanoTime() - startTime)
	 * / 1000 / 1000);
	 * 
	 * }
	 */
	/*
	 * public static void ABCUpdateSep(Setup setting, int nRun, double error,
	 * double[] dataStat) {
	 * 
	 * long startTime = System.nanoTime(); int noPar = allPar.size(); try {
	 * 
	 * CreateControlFile cFile = new CreateControlFile(
	 * setting.getControlFile()); cFile.updateFile(); for (int p = 0; p < noPar;
	 * p++) { cFile.setParPrior(allPar.get(p));
	 * allTrace.get(p).add(allPar.get(p).getValue()); } double mu =
	 * cFile.getMu(); double theta = cFile.getTheta();
	 * 
	 * SiteAlignment sa = new SiteAlignment(setting); // Importer imp;// = new
	 * Importer(setting.getDataFile()); Alignment newAli = null;
	 * 
	 * RunExt proc = new RunExt(setting.getfWorkingDir()); proc.setPar("./BCC",
	 * setting.getControlFile(), "1", "-p", "-f");
	 * 
	 * PrintWriter oResult = new PrintWriter(new BufferedWriter( new
	 * FileWriter(setting.getResultFile())));
	 * 
	 * int ii = 0; boolean outSum = false; boolean isReRun = true; SummaryStat
	 * stat = setting.getStat(); double saveGap = 0; for (int i = 0; i < nRun;
	 * i++) {
	 * 
	 * for (int p = 0; p < allPar.size(); p++) {
	 * 
	 * cFile.setParPrior(allPar.get(p)); cFile.updateFile();
	 * 
	 * while (isReRun) { proc.run();
	 * 
	 * // newAli = new Importer(setting).importAlignment(); newAli = new
	 * Importer(setting.getAlignmentFile(), setting).importAlignment(); if
	 * (newAli != null) { isReRun = false; } } isReRun = true;
	 * sa.updateAlignment(newAli);
	 * 
	 * double[] simDists = sa.getDists(); double simStat = stat.calStat1P1R(p,
	 * simDists); double gap = Math.abs(simStat - dataStat[p]) / dataStat[p]; //
	 * System.out.println(dataStat[p]+"\t"+ simStat +"\t" + // gap); if (gap <
	 * error) { outSum = true; saveGap = gap;//  should have 2 records for
	 * // different parameters if (p == 0) {
	 * 
	 * mu = cFile.getMu(); } else if (p == 1) { theta = cFile.getTheta(); } //
	 * allTrace.get(p).add( allPar.get(p).getValue() ); // oResult.println(
	 * TraceUtil.summary(ii, allTrace)); // ii++; } } if (outSum) {
	 * 
	 * allTrace.get(0).add(mu); allTrace.get(1).add(theta);
	 * allTrace.get(2).add(saveGap); String s = TraceUtil.summary(ii, allTrace);
	 * System.out.println(i + "\t" + s + "\t" + setting.getWorkingDir());
	 * oResult.println(s); oResult.flush(); ii++; } outSum = false; }
	 * oResult.close(); } catch (Exception e) { e.printStackTrace(); }
	 * System.out.println((System.nanoTime() - startTime) / 1000 / 1000);
	 * System.out
	 * .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(0))));
	 * System.out
	 * .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(1))));
	 * 
	 * }
	 * 
	 * public static void ABCUpdateTogether(Setup setting, int nRun, double
	 * error, double[] dataStat) {
	 * 
	 * long startTime = System.nanoTime(); try {
	 * 
	 * File fAliFile = setting.getfAliFile(); CreateControlFile cFile = new
	 * CreateControlFile( setting.getControlFile()); SiteAlignment sa = new
	 * SiteAlignment(setting); Importer imp;// = new
	 * Importer(setting.getDataFile());
	 * 
	 * RunExt proc = new RunExt(setting.getfWorkingDir()); proc.setPar("./BCC",
	 * setting.getControlFile(), "1", "-p", "-f");
	 * 
	 * PrintWriter oResult = new PrintWriter(new BufferedWriter( new
	 * FileWriter(setting.getResultFile())));
	 * 
	 * SummaryStat stat = setting.getStat();
	 * 
	 * long fileSize; int ii = 0; for (int i = 0; i < nRun; i++) {
	 * 
	 * cFile.setParPrior(allPar.get(0)); cFile.setParPrior(allPar.get(1));
	 * cFile.updateFile(); proc.run();
	 * 
	 * while ((fileSize = FileUtils.sizeOf(fAliFile)) < 50000) { Thread.sleep(0,
	 * 1000); System.out.println(i + "\t" + FileUtils.sizeOf(fAliFile)); if
	 * (FileUtils.sizeOf(fAliFile) == fileSize) { // break; } }
	 * 
	 * imp = new Importer(setting.getAlignmentFile());
	 * sa.updateAlignment(imp.importAlignment());
	 * 
	 * double[] simDists = sa.getDists(); // double simStat = stat.calStat(p,
	 * simDists); double simStatMu = stat.calStat1P1R(0, simDists); double
	 * simStatTheta = stat.calStat1P1R(1, simDists);
	 * 
	 * double gap = Math.abs(simStatTheta - dataStat[0]) / dataStat[0] +
	 * Math.abs(simStatMu - dataStat[1]) / dataStat[1]; System.out
	 * .println(simStatMu + "\t" + simStatTheta + "\t" + gap); if (gap < error)
	 * { allTrace.get(0).add(allPar.get(0).getValue());
	 * allTrace.get(1).add(allPar.get(1).getValue());
	 * oResult.println(TraceUtil.summary(ii, allTrace)); ii++; }
	 * 
	 * }
	 * 
	 * oResult.close(); } catch (Exception e) { e.printStackTrace(); }
	 * System.out.println((System.nanoTime() - startTime) / 1000 / 1000);
	 * System.out
	 * .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(0))));
	 * System.out
	 * .println(StatUtils.mean(TraceUtil.toPrimitive(allTrace.get(1))));
	 * 
	 * }
	 */
}
