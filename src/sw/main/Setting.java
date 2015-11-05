package sw.main;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.ParametersCollection;
import sw.abc.parameter.TunePar;
import sw.abc.stat.summary.SummaryStat;

public class Setting {

	private static String checkDir(String tDir) {
		if (tDir.charAt(tDir.length() - 1) == File.separatorChar) {
			return tDir;
		} else {
			return tDir + File.separatorChar;
		}

	}

	private String workingDir;
	private String obsFileName;
	private String resultOutFile;

	private String regressionCoefFile;

	private String[] paramList;
	private String[] statList;

	private ParametersCollection allPar = null;
	private ParametersCollection allParPrior = null;

	private SummaryStat summaryStat;

	private TunePar tPar;
	
	private boolean doRegression;

	private double[] obsStat;

	private double error;
	
	private int seqLength;
	private int noSeqPerTime;
	private int noTime;
	private int noTotalSeq;
	private int timeGap;
	private int thinning;
	private int noIteMCMC;
	private int noItePreprocess;
	private ParaTheta pTheta;
	
	private void init(String workingDir, String outputDir, String dataFileName) {

		this.workingDir = checkDir(workingDir);
		Path p = Paths.get(dataFileName);
		dataFileName = p.getFileName().toString();
		System.out.println(p.getFileName());
		System.out.println(this.workingDir +"\t"+ workingDir +"\t"+ outputDir +"\t"+ dataFileName);
		setupOutputFiles(outputDir, dataFileName);

		try {
			File fOutputDir = new File(outputDir);

			if (!fOutputDir.exists()) {
				fOutputDir.mkdir();
				System.out.println("Creating dir:" + fOutputDir.toString()
						+ "\t" + fOutputDir.exists());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Setting(String workingDir, String outputDir, String dataFileName) {
		init(workingDir, outputDir, dataFileName);
	}
	
	

	public Setting(String dataFileName) {

		File f = new File(dataFileName);
//		File f2 = new File(obsDataName, "Template/");
//		System.out.println(f2.getAbsolutePath());
		System.out.println(f.getName() +"\t"+ f.getPath());
		
		String obsDataNamePrefix = f.getAbsolutePath().split("\\.")[0];
//		System.out.println(f.getAbsolutePath() +"\t"+ f.getPath() +"\t"+ f.getParent());
		
		String workingDir = f.getParent();//+File.separatorChar+"TemplateFiles"+File.separatorChar;
		String outputDir = workingDir;//obsDataNamePrefix+File.separatorChar;
		System.out.println("output Dir:\t"+outputDir +"\t"+ workingDir);
		System.out.println(f.getParent());
		System.out.println(obsDataNamePrefix);
		
		
		init(workingDir, outputDir, dataFileName);
	}

	/**
	 * @return the allPar
	 */
	public ParametersCollection getAllPar() {
		return allPar;
	}

	/**
	 * @return the allParPrior
	 */
	public ParametersCollection getAllParPrior() {
		return allParPrior;
	}

	public String getDataFile() {
		return obsFileName;
	}

	/**
	 * @return the doRegression
	 */
	public boolean getDoRegression() {
		return doRegression;
	}

	/**
	 * @return the error
	 */
	public double getError() {
		return error;
	}

	/**
	 * @return the noIteMCMC
	 */
	public int getNoIteMCMC() {
		return noIteMCMC;
	}

	/**
	 * @return the noItePreprocess
	 */
	public int getNoItePreprocess() {
		return noItePreprocess;
	}

	public int getNoSeqPerTime() {
		return noSeqPerTime;
	}

	public int getNoTime() {
		return noTime;
	}

	public int getNoTotalSeq() {
		return noTotalSeq;
	}

	public double[] getObsStat() {

		return obsStat;
	}

	public String[] getParamList() {
		return paramList;
	}

	public String getRegressionCoefFile() {
		return regressionCoefFile;
	}

	public String getResultOutFile() {
		return resultOutFile;
	}

	public int getSeqLength() {
		return seqLength;
	}

	public String[] getStatList() {
		return statList;
	}



	public SummaryStat getSummaryStat() {
		return summaryStat;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Setting [workingDir=").append(workingDir)
				.append(", obsFileName=").append(obsFileName)
				.append(", resultOutFile=").append(resultOutFile)
				.append(", regressionCoefFile=").append(regressionCoefFile)
				.append(", doRegression=").append(doRegression)
				.append(", obsStat=").append(Arrays.toString(obsStat))
				.append(", error=").append(error).append(", seqLength=")
				.append(seqLength).append(", noSeqPerTime=")
				.append(noSeqPerTime).append(", noTime=").append(noTime)
				.append(", noTotalSeq=").append(noTotalSeq)
				.append(", timeGap=").append(timeGap).append(", thinning=")
				.append(thinning).append(", noIteMCMC=").append(noIteMCMC)
				.append(", noItePreprocess=").append(noItePreprocess)
				.append("]");
		return builder.toString();
	}

	/**
	 * @return the thinning
	 */
	public int getThinning() {
		return thinning;
	}

	public int getTimeGap() {
		return timeGap;
	}

	public TunePar getTunePar() {
		return tPar;
	}

	/**
	 * @param allPar
	 *            the allPar to set
	 */
	public void setAllPar(ParametersCollection allPar) {
		this.allPar = allPar;
	}

	/**
	 * @param allParPrior
	 *            the allParPrior to set
	 */
	public void setAllParPrior(ParametersCollection allParPrior) {
		this.allParPrior = allParPrior;
	}

	public void setMCMCSetting(int noItePreprocess, int noIteMCMC,
			int thinning, double error) {

		this.noItePreprocess = noItePreprocess;
		this.noIteMCMC = noIteMCMC;
		this.thinning = thinning;
		this.error = error;

	}
	public void setMCMCSetting(int noItePreprocess, int noIteMCMC,
			int thinning) {
		setMCMCSetting(noItePreprocess, noIteMCMC, thinning, 0.05);
	}
	
	public void setError(double error) {
		this.error = error;
	}

	public void setObsStat(double[] stat) {
		this.obsStat = stat;
	}

	public void setParamList(String[] paramList) {
		this.paramList = paramList;
	}

	public void setSeqInfo(int seqLength, int noSeqPerTime, int noTime,
			int timeGap) {
		this.seqLength = seqLength;
		this.noSeqPerTime = noSeqPerTime;
		this.noTime = noTime;
		this.timeGap = timeGap;
		noTotalSeq = this.noSeqPerTime * this.noTime;
	}

	public void setStatList(String[] statList) {
		this.statList = statList;
	}

	public void setSummaryStat(SummaryStat stat) {
		this.summaryStat = stat;

	}

	public void setTunePar(TunePar tPar) {
		this.tPar = tPar;

	}

	private void setupOutputFiles(String outputDir, String dataFileName) {

		this.resultOutFile = outputDir + dataFileName + "_summary.log";
		this.obsFileName = workingDir + dataFileName;
		this.regressionCoefFile = obsFileName + "_regressionCoef.coef";
		System.out.println(resultOutFile +"\t"+ regressionCoefFile);
		File tFile = new File(regressionCoefFile);
		if (tFile.exists()) {
			System.out.println("Regression file exist\t" + tFile.toString());
			this.doRegression = false;
			// FileUtils.copyFileToDirectory(tFile, fwDir);
			// regressionCoefFile =
			// fwDir.toString()+File.separatorChar+this.obsFileName+"_regressionCoef.coef";
		} else {
			this.doRegression = true;
		}

	}

	public void setTheta(ParaTheta pTheta) {
		this.pTheta = pTheta;
		
	}

	public ParaTheta getTheta() {
		return pTheta;
	}

	public void setSeqInfo(int seqLength, int totalSeqCount) {
		this.seqLength = seqLength;
		this.noTotalSeq = totalSeqCount;//this.noSeqPerTime * this.noTime;
		
	}

	public void setTime(int numTimePoint, int intervalBetweenTime) {
		this.noTime = numTimePoint;
		this.timeGap = intervalBetweenTime;
		this.noSeqPerTime = noTotalSeq / noTime;
		//TODO: What happen if there not even?
	}


	

}
