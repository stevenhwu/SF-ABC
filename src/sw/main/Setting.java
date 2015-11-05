package sw.main;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.Parameters;
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

	private static final double DEFAULT_ERROR = 0.01;
	private static final double DEFAULT_INIT_MU = 0.00001;
	private static final double DEFAULT_INIT_POP = 5000;
	
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

	private double[] errors;
	
	private int seqLength;
	private int numSeqPerTime;
	private int numTime;
	private int numTotalSeq;
	private int timeGap;
	private int thinning;
	private int noIteMCMC;
	private int numItePreprocess;
	private ParaTheta pTheta;
	private HashMap<String, Double> initValues;
	
	private void init(String workingDir, String outputDir, String dataFileName) {

		this.workingDir = checkDir(workingDir);
		outputDir = checkDir(outputDir);
		Path p = Paths.get(dataFileName);
		dataFileName = p.getFileName().toString();
//		System.out.println(p.getFileName());
//		System.out.println(this.workingDir +"\t"+ workingDir +"\t"+ outputDir +"\t"+ dataFileName);
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
		this.workingDir = checkDir(f.getParent());
//		String workingDir = f.getParent();//+File.separatorChar+"TemplateFiles"+File.separatorChar;
		String outputDir = workingDir;//obsDataNamePrefix+File.separatorChar;

		init(workingDir, outputDir, dataFileName);

		initValues = new HashMap<String, Double>();
		initValues.put(Parameters.MU, DEFAULT_INIT_MU);
		initValues.put(Parameters.POP, DEFAULT_INIT_POP);
		errors = new double[initValues.size()];

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
	public double[] getErrors() {
		return errors;
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
		return numItePreprocess;
	}

	public int getNoSeqPerTime() {
		return numSeqPerTime;
	}

	public int getNoTime() {
		return numTime;
	}

	public int getNoTotalSeq() {
		return numTotalSeq;
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
				.append(", error=").append(errors)
				.append(", seqLength=").append(seqLength)
				.append(", numSeqPerTime=").append(numSeqPerTime)
				.append(", numTime=").append(numTime)
				.append(", numTotalSeq=").append(numTotalSeq)
				.append(", timeGap=").append(timeGap)
				.append(", thinning=").append(thinning)
				.append(", numIteMCMC=").append(noIteMCMC)
				.append(", numItePreprocess=").append(numItePreprocess)
				.append(", InitValues=").append(initValues.values().toString())
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

		this.numItePreprocess = noItePreprocess;
		this.noIteMCMC = noIteMCMC;
		this.thinning = thinning;
		this.errors[0] = error;

	}
	public void setMCMCSetting(int noItePreprocess, int noIteMCMC,
			int thinning) {
		setMCMCSetting(noItePreprocess, noIteMCMC, thinning, DEFAULT_ERROR);
	}
	
	public void setErrors(double... errors) {
		this.errors = errors;
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
		this.numSeqPerTime = noSeqPerTime;
		this.numTime = noTime;
		this.timeGap = timeGap;
		numTotalSeq = this.numSeqPerTime * this.numTime;
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
//		System.out.println(resultOutFile +"\t"+ regressionCoefFile);
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
		this.numTotalSeq = totalSeqCount;//this.noSeqPerTime * this.noTime;
		
	}

	public void setTime(int numTimePoint, int intervalBetweenTime) {
		this.numTime = numTimePoint;
		this.timeGap = intervalBetweenTime;
		this.numSeqPerTime = numTotalSeq / numTime;
		if(numTotalSeq != (numSeqPerTime*numTime)){
			System.out.println("Warning! Total number of sequences is not divible by the number of time points.\n"
					+ "Total number of sequence: "+ numTotalSeq
					+ "\tNumber of time points:" + numTime);
		}

	}
	public HashMap<String, Double> GetInitValues() {

		return initValues;
	}

	public void setInitValue(String key, double value) {
		initValues.put(key, value);
		
	}


	

}
