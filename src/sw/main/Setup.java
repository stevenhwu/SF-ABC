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

public class Setup {

	final private char sysSep = System.getProperty("file.separator").charAt(0);

	private String softwareName = "BCC_fixParams";
	private String workingDir;
	private String obsName;
	
	private String regressionOutFile;
	private String resultOutFile;
	private String regressionCoefFile;
	
	@Deprecated
	private String controlName;
	@Deprecated
	private String alignmentName;
	@Deprecated
	private File fAliFile;
	
	private File fwDir;

	private int seqLength;
	private int noSeqPerTime;
	private int noTime;
	private int noTotalSeq;
	private SummaryStat stat;

	private String[] paramList;
	private String[] statList;
	
	private ParametersCollection allPar = null;
	private ParametersCollection allParPrior = null;//new ArrayList<Parameters>();

	@Deprecated
	private ArrayList<Parameters> allParArrayList = new ArrayList<Parameters>();
	@Deprecated
	private ArrayList<Parameters> allParPriorArrayList = new ArrayList<Parameters>();
	
	private String summarySetting;

	private TunePar tPar;

	



	public Setup(String wDir, String obsFileName) {
		if (wDir.charAt(wDir.length() - 1) == sysSep) {
			this.workingDir = wDir;
		} else {
			this.workingDir = wDir + sysSep;
		}
		fwDir = new File(wDir);
		try {
			if (fwDir.exists()) {
				File[] toDel = fwDir.listFiles();
				for (File file : toDel) {
					file.delete();
				}
				fwDir.delete();
			}
			String templateDir = System.getProperty("user.dir") + sysSep
					 + "TemplateFiles" + sysSep;
//			File tDir = new File(templateDir);
//			FileUtils.copyDirectory(tDir, fwDir);
//			new File(workingDir + softwareName).setExecutable(true);

			File tFile = new File(templateDir+softwareName);
			FileUtils.copyFileToDirectory(tFile, fwDir);
			
			tFile = new File(templateDir+obsFileName);
			FileUtils.copyFileToDirectory(tFile, fwDir);
			
			tFile = new File(templateDir+obsFileName+"_regressionCoef.coef");
			if(tFile.exists()){
				System.out.println("Regression file exist\t"+tFile.toString());
				FileUtils.copyFileToDirectory(tFile, fwDir);	
				regressionCoefFile = fwDir.toString()+File.separatorChar+obsFileName+"_regressionCoef.coef";
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("List of files:\t"+Arrays.toString( fwDir.list() ));//TEMP

	}

	public void setObsFile(String dataName) {
		this.obsName = workingDir + dataName;

	}
	@Deprecated
	public void setAlignmentFile(String alignmentName) {
		this.alignmentName = workingDir + alignmentName;
		fAliFile = new File(this.alignmentName);
	}

	@Deprecated
	public void setBCCControlFile(String controlName) {
		this.controlName = workingDir + controlName;

	}


	public void setSeqInfo(int seqLength, int noSeqPerTime, int noTime) {
		this.seqLength = seqLength;
		this.noSeqPerTime = noSeqPerTime;
		this.noTime = noTime;
		noTotalSeq = this.noSeqPerTime * this.noTime;
	}

	public int[] getAlignmentInfo() {
		int[] aliInfo = new int[] { seqLength, noTotalSeq };
		return aliInfo;
	}

	public int[] getTimeInfo() {
		int[] timeInfo = new int[] { noTime, noSeqPerTime };
		return timeInfo;
	}

	public void setOutputFiles(String outputDir, String obsFileName) {
		String outFilePrefix = outputDir + obsFileName;
		this.resultOutFile =  outFilePrefix + "_summary.log";
		this.regressionOutFile = outFilePrefix + "_regressionCoef.coef";;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public File getfWorkingDir() {
		return fwDir;
	}

	public String getDataFile() {
		return obsName;
	}

	public String getResultOutFile() {
		return resultOutFile;
	}

	public String getRegressionOutFile() {
		return regressionOutFile;
	}
	public String getRegressionCoefFile() {
		return regressionCoefFile;
	}
	
	@Deprecated
	public String getBCCControlFile() {
		return controlName;
	}
	
	@Deprecated
	public String getAlignmentFile() {
		return alignmentName;
	}
	@Deprecated
	public File getfAliFile() {
		return fAliFile;
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

	public int[][] setupTimeGroup() {

		int[][] t = new int[noTime][noSeqPerTime];
		// int[] ta = new int[noSeqPerTime];

		for (int i = 0; i < noTime; i++) {
			int offset = i * noSeqPerTime;
			for (int j = 0; j < noSeqPerTime; j++) {
				t[i][j] = j + offset;
			}
		}

		//
		// int[] t2 = new int[40];
		// for (int i = 0; i < t2.length; i++) {
		// t2[i] = i+40;
		// }
		// t[0]=ta;
		// t[1]=t2;
		// sa.addTimeGroup(t1);
		// sa.addTimeGroup(t2);

		return t;

		// return null;
	}

	public void setStat(SummaryStat stat) {
		this.stat = stat;

	}
	public void setNoSeqPerTime(int noSeq) {
		noSeqPerTime = noSeq;
		noTotalSeq = this.noSeqPerTime * this.noTime;
	}
	
	public SummaryStat getStat() {
		return stat;
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

	public void setSummarySetting(String summarySetting) {
		this.summarySetting = summarySetting;
		
	}
	public String getSummarySetting() {
		return summarySetting;
		
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

	/**
	 * @return the allParArrayList
	 */
	
	@Deprecated
	public ArrayList<Parameters> getAllParArrayList() {
		return allParArrayList;
	}

	/**
	 * @param allParArrayList the allParArrayList to set
	 */
	@Deprecated
	public void setAllParArrayList(Parameters ... allP) {
		for (Parameters p : allP) {
			this.allParArrayList.add(p);
		}
	}

	/**
	 * @return the allParPriorArrayList
	 */
	@Deprecated
	public ArrayList<Parameters> getAllParPriorArrayList() {
		return allParPriorArrayList;
	}

	/**
	 * @param allParPriorArrayList the allParPriorArrayList to set
	 * @Deprecated
	 */
	@Deprecated
	public void setAllParPriorArrayList(Parameters ... allP) {
		for (Parameters p : allP) {
			this.allParPriorArrayList.add(p);
		}
		
	}

}
