package sw.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;




import sw.abc.parameter.ParameterList;
import sw.abc.parameter.Parameters;
import sw.abc.parameter.ParametersCollection;
import sw.abc.parameter.TunePar;
import sw.abc.stat.summary.SummaryStat;

public class Setting {

	

	private String workingDir;
	private String obsFileName;
	
	
	private String resultOutFile;
	private String regressionCoefFile;
	

	

	private int seqLength;
	private int noSeqPerTime;
	private int noTime;
	private int noTotalSeq;
	private int timeGap;

	private String[] paramList;
	private String[] statList;
	
	private ParametersCollection allPar = null;
	private ParametersCollection allParPrior = null;//new ArrayList<Parameters>();


	private String summarySettingString;
	
	private SummaryStat summaryStat;
	private TunePar tPar;
	private boolean doRegression;
	private double[] obsStat;

	



	/**
	 * @param timeGap the timeGap to set
	 */
	public void setTimeGap(int timeGap) {
		this.timeGap = timeGap;
	}
	public int getTimeGap() {
		return timeGap;
	}
	
	
	
	public Setting(String workingDir, String outputDir, String dataFileName) {
		
		this.workingDir = checkDir(workingDir);
		setupOutputFiles(outputDir, dataFileName);
//		workingDir == templateDir
		

		
		try {
//			System.out.println(outputDir);
			File fOutputDir = new File(outputDir);

			
			if(!fOutputDir.exists()){
				fOutputDir.mkdir();		
				System.out.println("Creating dir:"+fOutputDir.toString() +"\t"+ fOutputDir.exists());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("List of files:\t"+Arrays.toString( fwDir.list() ));//TEMP

	}

	private String checkDir(String tDir) {
		if (tDir.charAt(tDir.length() - 1) == File.separatorChar) {
			return tDir;
		} else {
			return tDir + File.separatorChar;
		}
		
	}
	private void setupOutputFiles(String outputDir, String dataFileName) {

		this.resultOutFile =  outputDir + dataFileName + "_summary.log";
		this.obsFileName = workingDir + dataFileName;
		this.regressionCoefFile = obsFileName + "_regressionCoef.coef";;
		File tFile = new File(regressionCoefFile);
		if(tFile.exists()){
			System.out.println("Regression file exist\t"+tFile.toString());
			this.doRegression = false;
//			FileUtils.copyFileToDirectory(tFile, fwDir);	
//			regressionCoefFile = fwDir.toString()+File.separatorChar+this.obsFileName+"_regressionCoef.coef";
		}
		else{
			this.doRegression = true;
		}
		
	}
	
	/**
	 * @return the doRegression
	 */
	public boolean isDoRegression() {
		return doRegression;
	}
	public void setSeqInfo(int seqLength, int noSeqPerTime, int noTime, int timeGap) {
		this.seqLength = seqLength;
		this.noSeqPerTime = noSeqPerTime;
		this.noTime = noTime;
		this.timeGap = timeGap;
		noTotalSeq = this.noSeqPerTime * this.noTime;
	}

	public int[] getAlignmentInfo() {
		int[] aliInfo = new int[] { seqLength, noTotalSeq, noTime, timeGap };
		return aliInfo;
	}

	public int[] getTimeInfo() {
		int[] timeInfo = new int[] { noTime, noSeqPerTime };
		return timeInfo;
	}




	public String getWorkingDir() {
		return workingDir;
	}


	public String getDataFile() {
		return obsFileName;
	}

	public String getResultOutFile() {
		return resultOutFile;
	}


	public String getRegressionCoefFile() {
		return regressionCoefFile;
	}
	
	public int getSeqLength() {
		return seqLength;
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


	public void setSummaryStat(SummaryStat stat) {
		this.summaryStat = stat;

	}

	
	public SummaryStat getSummaryStat() {
		return summaryStat;
	}

	@Deprecated
	public void setNoSeqPerTime(int noSeq) {
		noSeqPerTime = noSeq;
		noTotalSeq = this.noSeqPerTime * this.noTime;
	}

	public String[] getParamList() {
		return paramList;
	}

	public void setParamList(String[] paramList) {
		this.paramList = paramList;
	}

	public String[] getStatList() {
		return statList;
	}

	public void setStatList(String[] statList) {
		this.statList = statList;
	}

	public void setSummarySettingString(String summarySetting) {
		this.summarySettingString = summarySetting;
		
	}
	public String getSummarySettingString() {
		return summarySettingString;
		
	}

	public void setTunePar(TunePar tPar) {
		this.tPar = tPar;
		
	}

	public TunePar getTunePar() {
		return tPar;
	}

	/**
	 * @return the allPar
	 */
	public ParametersCollection getAllPar() {
		return allPar;
	}

	/**
	 * @param allPar the allPar to set
	 */
	public void setAllPar(ParametersCollection allPar) {
		this.allPar = allPar;
	}

	/**
	 * @return the allParPrior
	 */
	public ParametersCollection getAllParPrior() {
		return allParPrior;
	}

	/**
	 * @param allParPrior the allParPrior to set
	 */
	public void setAllParPrior(ParametersCollection allParPrior) {
		this.allParPrior = allParPrior;
	}
	public double[] getObsStat() {
		
		return obsStat;
	}
	
	public void setObsStat(double[] stat){
		this.obsStat = stat;
	}

}
