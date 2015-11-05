/*******************************************************************************
 *
 * Copyright (C) 2015 Steven Wu, Allen Rodrigo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package sw.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.stat.StatUtils;

import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaPopsize;
import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.ParametersCollection;
import sw.abc.parameter.SavePar;
import sw.abc.parameter.TunePar;
import sw.abc.stat.data.AlignmentStatFlex;
import sw.abc.stat.summary.SStatFlexable;
import sw.logger.ArrayLogFormatterD;
import sw.math.Combination;
import sw.math.RandomUtils;
import sw.math.distribution.OneOverDistribution;
import sw.math.distribution.TruncatedScale;
import sw.math.distribution.UniformDistribution;
import sw.simulator.SSC;
import sw.util.TraceUtil;


public class Main {
	
	/**
	 * 
	 * @author Steven Wu
	 * @version $Id$
	 */

	
	private static final int NO_SEQ_PER_TIME = 50;
	private static final int SEQ_LENGTH = 750;
	private static final int NO_TIME_POINT = 3;
	private static final int TIME_GAP = 400;
	
	private static final int TUNESIZE = 400 ; //400
	private static final int TUNEGROUP = 10; //10
	private static final int NO_REPEAT_CAL_ERROR = 1000; //1000
	private static final int NO_REPEAT_PER_PARAMETERS = 100; //100
	
	private static final double initScale = 0.5;

	private static final boolean DYNAMIC_ERROR_RATE = false;//Alter the error rate in MCMC. Testing methods only with unknown behaviour. Turn on with caution!!
	private static final String VERSION = "1.0";
	
	
	/*
		param:
		args[0]: String 	data file
		args[1]: int		noItePreprocess
		args[2]: int		noIteMCMC
		args[3]: int		thinning
		args[4]: double 	error
		
		int 	no_time_points
		e.g.	0 simData.paup 50 100 10 0.1 
		e.g.	0 simData.paup 500000 1000000 1000 0.01
	*/
	public static void main(String[] args) {

//		Init with default values
//		int numItePreprocess = 1000;
//		int numIteMCMC = 1000;
//		int numIteSample = 100;
		double error = 0.01;
		
//		int numTimePoints;
//		int timeGap;
		String obsDataName ="";
		args = new String[]{
//				"-h",
				"-i", "/home/steven/workspace/SF-ABC/Simulations/test/test.nex",
				"-t", "3", "400",
				"-m", "1000", "1000", "100",
//				"-g 400"
				
		};
		
		
		Options options = new Options();
		Option help = new Option("h", "help", false, "print this message");
		Option version = new Option("v", "version", false,
				"print the version information and exit");
		options.addOption(help);
		options.addOption(version);
		
		options.addOption(Option.builder("i").longOpt("input").hasArg()
				.argName("INPUT").desc("Infile (nexus format)")
				.build());
//		options.addOption(Option.builder("t").longOpt("time").hasArg()
//				.argName("TIME").desc("Number of time points")
//				.build());
//		
//		options.addOption(Option.builder("g").longOpt("gap").hasArg()
//				.argName("GAP").desc("Time between gaps")
//				.build());
					
		Builder C = Option.builder("m").longOpt("mcmc")
				.numberOfArgs(3).argName("preprocess mcmc sampling")
				.desc("Three Parameters in the following orders: "
						+ "(1) Length of preprocessing, "
						+ "(2) Length of MCMC chain, "
						+ "(3) Sample every n iterations. ");
		options.addOption(C.build());

		C = Option.builder("t").longOpt("time")
				.numberOfArgs(2).argName("numTime interval")
				.desc("Two Parameters in the following orders: "
						+ "(1) Number of time points, "
						+ "(2) Interval between two time points");
		options.addOption(C.build());
		
		HelpFormatter formatter = new HelpFormatter();
		String syntax = "sfabc";
		String header = 
				"\nEstimation of evolutionary parameters using short, random and partial sequences from mixed samples of anonymous individuals. "
				+ "\n\nArguments:\n";
		String footer = "\n";
		
		formatter.setWidth(80);
		
		

//		final int noItePreprocess = Integer.parseInt(args[1]);
//		final int noIteMCMC = Integer.parseInt(args[2]); 
//		final int thinning = Integer.parseInt(args[3]);
////		double error = Double.parseDouble(args[4]);
// 		final int time_gap = 3;
		
//		try {
		Setting setting = null;
		
//			
//			try {
//
//				NexusImporter imp = new NexusImporter(new FileReader(setting.getDataFile()));
//				Alignment jeblAlignment = imp.importAlignments().get(0);
//				System.out.println(jeblAlignment.getPatternCount());
//				System.out.println(jeblAlignment.getSiteCount());
//				System.out.println(jeblAlignment.getPatternLength());
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ImportException e) {
//				e.printStackTrace();
//			} 
//			
//			setting.setSeqInfo(SEQ_LENGTH, NO_SEQ_PER_TIME, NO_TIME_POINT , TIME_GAP);
//			setting.setMCMCSetting(noItePreprocess, noIteMCMC, thinning);
//			
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			String[] pct_config = cmd.getArgs();

			if (cmd.hasOption("h") || args.length == 0) {
				formatter.printHelp(syntax, header, options, footer, true);
				System.exit(0);
			}
			if(cmd.hasOption("v")){
				System.out.println("SF_ABC "+VERSION);
				System.exit(0);
			}
//					if (pct_config.length != 2){
//						System.out
//								.println("ERROR! Required exactly two argumennts for pct_env and pct_pool. It got "
//										+ pct_config.length + ": " + Arrays.toString(pct_config));
//						formatter.printHelp(syntax, header, options, footer, true);
//						System.exit(3);
//					}
			else{
//						pctEnv = Double.parseDouble(pct_config[0]);
//						pctPool = Double.parseDouble(pct_config[1]);
//						if(pctEnv<0 || pctEnv >1){
//							System.out.println(
//								"ERROR: pctEnv (Percentage of environmental acquisition) must be between 0 and 1 (pctEnv="
//								+ pctEnv + ")! EXIT");
//							System.exit(3);
//						}
//						if(pctPool<0 || pctPool >1){
//							System.out.println(
//								"ERROR: pctPool (Percentage of pooled environmental component must) must be between 0 and 1 (pctPool="
//								+ pctPool + ")! EXIT");
//							System.exit(3);
//						}
				
			}
			if (cmd.hasOption("input")){
//				numberOfObservation= Integer.parseInt(cmd.getOptionValue("obs"));
				obsDataName = cmd.getOptionValue("input");
				setting = ABCSetup(obsDataName);
				
				NexusImporter imp = new NexusImporter(new FileReader(setting.getDataFile()));
				Alignment jeblAlignment = imp.importAlignments().get(0);
				int seqLength = jeblAlignment.getPatternCount();
				int totalSeqCount = jeblAlignment.getPatternLength(); 
				System.out.println(seqLength);
//				System.out.println(jeblAlignment.getSiteCount());
				System.out.println(totalSeqCount);
				setting.setSeqInfo(seqLength, totalSeqCount);
				System.out.println("Infile: "+obsDataName);
			}
			else{
				System.out.println("Error: Input file required\n");
				System.exit(6);
			}
					
			if (cmd.hasOption("mcmc")){
				String[] configs = cmd.getOptionValues("mcmc");
				
				int numItePreprocess = Integer.parseInt(configs[0]);
				int numIteMCMC = Integer.parseInt(configs[1]);
				int numIteSample = Integer.parseInt(configs[2]);
				setting.setMCMCSetting(numItePreprocess, numIteMCMC, numIteSample);
				System.out.println("MCMC: "+ numItePreprocess +"\t"+ numIteMCMC +"\t"+ numIteSample);
			}
			else{
				System.out.println("Error: MCMC options required\n");
				System.exit(6);
			}
			
			if (cmd.hasOption("time")){
				String[] configs = cmd.getOptionValues("time");
				int numTimePoints = Integer.parseInt(configs[0]);
				int intervalBetweenTime = Integer.parseInt(configs[1]);
				
//				numTimePoints = Integer.parseInt(cmd.getOptionValue("time").trim());
				setting.setTime(numTimePoints, intervalBetweenTime);
				System.out.println("Number of time points: "+numTimePoints);
			}			
			else{
				System.out.println("Error: Number of time point (-t,--time) required\n");
				System.exit(6);
			}
			
//					numTimePoints = Integer.parseInt(cmd.getOptionValue("time"));
			
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(setting.toString());
//		System.exit(3);

//		double ratio[] = new double[100];
//		for (int i = 0; i < RegressionResult.result.length; i++) {
//			double r = RegressionResult.result[i][0]*RegressionResult.result[i][1];
//			ratio[i] = r/(1E-5*3000);
//			System.out.println(r +"\t"+ ratio[i]);
//		}
//		System.out.println(StatUtils.mean(ratio));
//		System.out.println(StatUtils.variance(ratio));
		int i = 0;
			String[] localTest = new String[] {
					obsDataName,
//					"200000", "100", "1000" };
					"2000", "10", "100" };
			System.out.println(Arrays.toString(localTest));
			startSimulation(setting);
			
//		startSimulation(args);


	}
	
//	public static void startSimulation(String[] args) {
	public static void startSimulation(Setting setting) {
		
//		final String obsDataName = args[0];
//		final int noItePreprocess = Integer.parseInt(args[1]);
//		final int noIteMCMC = Integer.parseInt(args[2]); 
//		final int thinning = Integer.parseInt(args[3]);
////		double error = Double.parseDouble(args[4]);
// 		final int time_gap = 3;
//		
		try {
//			Setting setting = ABCSetup(obsDataName);
//			
//			try {
//
//				NexusImporter imp = new NexusImporter(new FileReader(setting.getDataFile()));
//				Alignment jeblAlignment = imp.importAlignments().get(0);
//				System.out.println(jeblAlignment.getPatternCount());
//				System.out.println(jeblAlignment.getSiteCount());
//				System.out.println(jeblAlignment.getPatternLength());
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ImportException e) {
//				e.printStackTrace();
//			} 
//			
//			setting.setSeqInfo(SEQ_LENGTH, NO_SEQ_PER_TIME, NO_TIME_POINT , TIME_GAP);
//			setting.setMCMCSetting(noItePreprocess, noIteMCMC, thinning);
			
			//TODO: Check NO_SEQ_PER_TIME, num seq used for simulation, actual number seq per time in the real data
			
//			testStatFile(setting);			
			generateStatFile(setting);
//			generateErrorRate(setting, NO_REPEAT_CAL_ERROR, NO_REPEAT_PER_PARAMETERS);
//		
			System.out.println(setting.toString());
			ABCUpdateMCMC(setting);

		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	public static Setting ABCSetup(String obsDataName) throws IOException {

		File f = new File(obsDataName);
		File f2 = new File(obsDataName, "Template/");
//		System.out.println(f2.getAbsolutePath());
		System.out.println(f.getName() +"\t"+ f.getPath());
		
		String obsDataNamePrefix = f.getAbsolutePath().split("\\.")[0];
//		System.out.println(f.getAbsolutePath() +"\t"+ f.getPath() +"\t"+ f.getParent());
		
		String workingDir = f.getParent();//+File.separatorChar+"TemplateFiles"+File.separatorChar;
		String outputDir = workingDir;//obsDataNamePrefix+File.separatorChar;
		System.out.println("output Dir:\t"+outputDir +"\t"+ workingDir);
		System.out.println(f.getParent());

		System.out.println(obsDataNamePrefix);
		
		double[] initValue = getRegressionResult(obsDataNamePrefix);
				
		Setting setting = new Setting(workingDir, outputDir, obsDataName);
//		System.out.println(obsDataName);
//		System.out.println(setting.getDataFile());

				
		String[] paramListName = new String[]{"mu", "popsize"};
		String[] statList = new String[]{"dist", "chisq", "var", "covar", "sitePattern"};
		
		setting.setParamList(paramListName);
		setting.setStatList(statList);				

		double scaleFactor = 5.0;
		double muMean = initValue[0];
		double muLower = muMean / scaleFactor;
		double muUpper = muMean * scaleFactor;
		
		double popSizeMean = initValue[1];
		double popSizeLower = popSizeMean / scaleFactor;
		double popSizeUpper = popSizeMean * scaleFactor;
		
		ParaMu unifMu = new ParaMu(new UniformDistribution(muLower, muUpper));
		ParaPopsize unifPopsize = new ParaPopsize(new UniformDistribution(popSizeLower, popSizeUpper));
	
		
		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{initScale , initScale}, new String[] { "Scale", "Scale" });
// TODO plot proir against posterior
//		ParaMu pMu = new ParaMu(new TruncatedNormalDistribution(0.5, 0.5, 0, 0.4));
//		ParaMu pMu = new ParaMu(new TruncatedNormalDistribution(muMean, muMean, muLower, muUpper));
//		ParaMu pMu = new ParaMu(new UniformDistribution(muLower, muUpper));
		ParaMu pMu = new ParaMu(new UniformDistribution(0,1) );
		
		pMu.setInitValue(muMean);
//		pMu.setInitValue(3.0E-5);
//		pMu.setProposal(new Scale(initScale ));
		pMu.setProposal(new TruncatedScale(initScale, muLower, muUpper ));
//		pMu.setProposal(new TruncatedNormalDistribution(muMean, muMean/2, muLower, muUpper));
		
//		ParaPopsize pPop = new ParaPopsize(new TruncatedNormalDistribution(
//				popSizeMean, popSizeMean, popSizeLower, popSizeUpper));
//				ParaPopsize pPop = new ParaPopsize(new TruncatedNormalDistribution(1000000, 100000, 0, 1500000));
//		ParaPopsize pPop = new ParaPopsize(new UniformDistribution(popSizeLower, popSizeUpper));
		ParaPopsize pPop = new ParaPopsize(new OneOverDistribution(popSizeMean));		
		pPop.setInitValue(popSizeMean);
//		pPop.setInitValue(6E3);
//		pPop.setProposal(new Scale(initScale ));
		pPop.setProposal(new TruncatedScale(initScale, popSizeLower, popSizeUpper ));
//		pPop.setProposal(new TruncatedNormalDistribution(popSizeMean, popSizeMean/2, popSizeLower, popSizeUpper));
//		pPop.setProposal(new NormalDistribution(thetaMean, thetaMean/5));

		
		double theta = muMean * popSizeMean;
		double thetaLower = theta/scaleFactor;
		double thetaUpper = theta*scaleFactor;
		ParaTheta pTheta = new ParaTheta(new UniformDistribution(thetaLower, thetaUpper));
//		ParaTheta pTheta = new ParaTheta(new NormalDistribution(theta , theta*2));
		pTheta.setInitValue(theta);
		
		ParametersCollection allPars = new ParametersCollection(paramListName, pMu, pPop); 
		ParametersCollection allParsPrior = new ParametersCollection(paramListName, unifMu, unifPopsize);
//		
		
		
//		double muTune = muMean/10;
//		double thetaTune = thetaMean/10;
//		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{muTune, thetaTune}, new String[] { "Normal", "Normal" });
//		pMu.setProposal(new NormalDistribution(muMean, muTune));
//		pTheta.setProposal(new NormalDistribution(thetaMean, thetaTune));
	
		setting.setTunePar(tPar);
		setting.setAllPar(allPars);
		setting.setAllParPrior(allParsPrior);
		setting.setTheta(pTheta);

		return setting;
	}
	

//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Setting generateStatFile(Setting setting) {
		
		SStatFlexable sStat = null;
		if(setting.getDoRegression()){
			
			System.out.println("Preprocessing: semi-auto regression");
			final int nRun = setting.getNoItePreprocess();
			final int thinning = setting.getThinning();
			final int noTime = setting.getNoTime();
			String[] paramList = setting.getParamList();
			String[] statsList = setting.getStatList();
	
			ParametersCollection allParUniformPrior = setting.getAllParPrior();

			AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
			
			SSC simulator = new SSC(setting);
			
			TraceUtil tu = new TraceUtil(noTime);
			ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAll(paramList));
			ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAll(statsList));
	

			final long startTime = System.currentTimeMillis();
			for (int i = 0; i < nRun; i++) {
				allParUniformPrior.nextProirs();
									
				Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
				newStat.updateAlignment(jeblAlignment);

				traceLogParam.logValues( allParUniformPrior.getValues() );
				traceLogStats.logValues( newStat.getCurStat(statsList) );

				if ((i % thinning) == 0) {
					System.out.println("Ite:\t"+i+"\t"+ Math.round((System.currentTimeMillis() - startTime)/60e3)+" mins");
				}
			}
			
			sStat = SemiAutoRegression.semiAutoRegression(nRun, traceLogStats, traceLogParam, 2);
			String regressionSummary = sStat.toString();
			System.out.println(Arrays.toString(statsList));
			System.out.println(regressionSummary);
			System.out.println("Time:\t" + Math.round( (System.currentTimeMillis()-startTime)/60e3)+" mins" );	
			System.out.println("Time:\t" + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()-startTime) +" mins" );
			try {	
				
				String regressionCoefFile =  setting.getRegressionCoefFile();
				PrintWriter oResult1 = new PrintWriter(new BufferedWriter(
						new FileWriter(regressionCoefFile)));
				oResult1.println("# " + setting.toString());
				oResult1.println(regressionSummary);
				oResult1.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			sStat = SemiAutoRegression.readRegressionFile(setting);
		}
		setting.setSummaryStat(sStat);
		setting.setObsStat( calObsStat(setting) );
		
		
		return setting;
	
	}


public static double generateErrorRate(Setting setting, int nRun, int nRepeat) {

		// nRun = 10;
		// nRepeat = 10;
		System.out.println("Estimating Error Rate");
		ParametersCollection allParUniformPrior = setting.getAllParPrior();
		SSC simulator = new SSC(setting);
		AlignmentStatFlex[] newStat = new AlignmentStatFlex[nRepeat];
		for (int i = 0; i < newStat.length; i++) {
			newStat[i] = new AlignmentStatFlex(setting);
		}

		double[] repeatStat = new double[nRepeat];
		int[][] listAllComb = Combination.ListCombination(nRepeat);
		int noOfComb = listAllComb.length;
		double[] expectedError = new double[noOfComb * 2];
		double[] meanOfMean = new double[nRun];
		
		for (int i = 0; i < nRun; i++) {
			allParUniformPrior.nextProirs();
			
			for (int j = 0; j < nRepeat; j++) {
				Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
				newStat[j].updateAlignmentAndStat(jeblAlignment);
				repeatStat[j] = newStat[j].getSummaryStatAll()[0];
			}

			for (int j = 0; j < noOfComb; j++) {
				double s1 = repeatStat[listAllComb[j][0]];
				double s2 = repeatStat[listAllComb[j][1]];
				expectedError[j] = AlignmentStatFlex.calAbsDiff(s1, s2);
				expectedError[j + noOfComb] = AlignmentStatFlex.calAbsDiff(s2, s1);

			}
			meanOfMean[i] = StatUtils.mean(expectedError);
		}
		double error = StatUtils.mean(meanOfMean);
		setting.setError(error);
		
		return error;
		

	}

	//	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void ABCUpdateMCMC(Setting setting) throws Exception {

		final long startTime = System.currentTimeMillis();
		final int nRun = setting.getNoIteMCMC();
		final int thinning = setting.getThinning();
		final int noTime = setting.getNoTime();

		double error = setting.getError();
		ParametersCollection allPar = setting.getAllPar();
		TunePar tPar = setting.getTunePar();

		ParaTheta pTheta = setting.getTheta();
		pTheta.setInitPriorRatio(allPar);
		
		SSC simulator = new SSC(setting);
		SavePar sPar = new SavePar(TUNESIZE);
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
		newStat.setObsStat(setting.getObsStat()) ;

		TraceUtil ut = new TraceUtil(noTime, allPar.getSize());
		ArrayLogFormatterD traceLog = new ArrayLogFormatterD(6, ut.createTraceAll(setting.getParamList()));
		traceLog.logValues( allPar.getValues() );

		PrintWriter oResult = new PrintWriter(new BufferedWriter(new FileWriter(setting.getResultOutFile())));
		oResult.println("# "+setting.getDataFile()+"\t"+setting.toString());
		oResult.println("Ite\t" + traceLog.getLabels());
		oResult.flush();
		
		
		System.out.println("Start ABCMCMC\nTolerance:\t"+error);
		double[] saveGaps = new double[10];
		
//		error = 1;
		for (int i = 0; i < nRun; i++) {
			
			for (int p = 0; p < allPar.getSize(); p++) {
				allPar.nextProposal(p);
			}
				for (int j = 0; j < saveGaps.length; j++) {
					
//					simulator = new SSC(setting);
					Alignment jeblAlignment = simulator.simulateAlignment(allPar);
					newStat.updateAlignmentAndStat(jeblAlignment);
					saveGaps[j] = newStat.calDelta(0);
				}
//				System.out.println(Arrays.toString(saveGaps));
//				Alignment jeblAlignment = simulator.simulateAlignment(allPar);
//				newStat.updateAlignmentAndStat(jeblAlignment);
				
//				double saveGap = newStat.calDelta();
				double saveGap = StatUtils.mean(saveGaps);
//				System.out.println(saveGap);
				if (saveGap < error) {	
//					System.out.println(saveGap);
//					if (MH.accept(allPar.getParameter(p))) { //TODO: think about this
//						allPar.acceptNewValue(p);
//					}
					double logP = pTheta.getPriorRatio(allPar);
					double logQ = pTheta.getProposalRatio(allPar);
					
					if (MH.accept(logP, logQ)) { 
						allPar.acceptNewValues();
						pTheta.acceptNewValue();
					}
					//					if (MH.accept(allPar)) { 
//						allPar.acceptNewValues();
//					}
				}
//			}
			sPar.add(allPar);
			
			if (i % TUNESIZE == 0 & i!=0) {
				
				tPar.update(sPar, i);
				allPar.updateProposalDistVar(tPar);
				if(DYNAMIC_ERROR_RATE){
					error = updateTol(tPar.getAccRate(), error);
				}

//				for (int j = 0; j < allPar.getSize(); j++) {
////					TODO recode this
//					allPar.getParameter(j).updateProposalDistVar(tPar.getTunePar(j));
//				}
//				double[] accRate = tPar.getEachAccRate();
//				if ((accRate[0] == 0.0) & (accRate[1] == 0) & i>(TUNESIZE*10)){
////					allPar.getNextProir();
//				}
				
			}
			
			if ((i % thinning) == 0) {
				System.out.println("Ite:\t"+i + "\t"+ Arrays.toString(allPar.getAcceptCounts()) + "\t" + 
						error +"\t"+ 
						allPar.getParameter(0).getProposalDistVar() +"\t"+ allPar.getParameter(1).getProposalDistVar() +"\t"+ 
						Arrays.toString(tPar.getEachAccRate()) + "\t" +
						Arrays.toString(allPar.getValues()) );

				traceLog.logValues(allPar.getValues());
				String s = i + "\t" + traceLog.getLine(i / thinning);
				oResult.println(s);
				oResult.flush();
				
			}

		}				
		traceLog.logValues(allPar.getValues());
		String s = nRun + "\t" + traceLog.getLine(nRun / thinning);
		oResult.println(s);
		oResult.flush();
		oResult.close();
		
		System.out.println(Arrays.toString( tPar.getEachAccRate() ));
		System.out.println("Time:\t"+ Math.round((System.currentTimeMillis() - startTime) / 60e3) + " mins");
		
	}




	private static double updateTol(double[] accRate, double error) {
		
		double aveAccRate = StatUtils.mean(accRate);
		
		double newError = error;
		if (aveAccRate > 0.5) {
			newError -= RandomUtils.nextUniform(0, 0.02);
		} 
		if (aveAccRate < 0.1) {
			newError += RandomUtils.nextUniform(0, 0.02);
		}
		return newError;
	}

	private static double[] calObsStat(Setting setting) {
	
//		System.out.println("Calculate observed stat");
//		SiteAlignment sa = new SiteAlignment(setting);
		
//		Importer imp = new Importer(setting.getDataFile());
		AlignmentStatFlex aliStat = new AlignmentStatFlex(setting);

		try {

			NexusImporter imp = new NexusImporter(new FileReader(setting.getDataFile()));
			Alignment jeblAlignment = imp.importAlignments().get(0);
			aliStat.updateAlignmentAndStat(jeblAlignment);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		}
//
//		aliStat.updateSiteAlignment(sa);
//		aliStat.calSumStat();
	
		System.out.println("Calculating observed stat:\t"+aliStat.toString());
		
		return aliStat.getSummaryStatAll();
	
	}


	private static double[] getRegressionResult(String obsDataNamePrefix) {
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
		return initValue;
	}

	//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void testStatFile(Setting setting) {
	
		final int nRun = 100;
		final int noTime = setting.getNoTime();
		String[] paramList = setting.getParamList();
		String[] statsList = setting.getStatList();
	
		ParametersCollection allParUniformPrior = setting.getAllParPrior();
	
		AlignmentStatFlex newStat = new AlignmentStatFlex(setting);
	
		SSC simulator = new SSC(setting);
	
		TraceUtil tu = new TraceUtil(noTime);
		ArrayLogFormatterD traceLogParam = new ArrayLogFormatterD(6, tu.createTraceAll(paramList));
		ArrayLogFormatterD traceLogStats = new ArrayLogFormatterD(6, tu.createTraceAll(statsList));
		long start = System.currentTimeMillis();
		try {
			PrintWriter oResult = new PrintWriter(new BufferedWriter(
					new FileWriter("zzz_testRegressionOutput2")));
			String s = traceLogParam.getLabels() +"\t"+ traceLogStats.getLabels();
			oResult.println(s);
			for (int i = 0; i < nRun; i++) {
				allParUniformPrior.nextProirs();
				traceLogParam.logValues(allParUniformPrior.getValues());
				
				for (int j = 0; j < 100; j++) {
					
				
				Alignment jeblAlignment = simulator.simulateAlignment(allParUniformPrior);
				newStat.updateAlignment(jeblAlignment);
	
				
				traceLogStats.logValues(newStat.getCurStat(statsList));
				s = traceLogParam.getLine(i) + "\t" + traceLogStats.getLine(i*100+j);
				oResult.println(s);
				oResult.flush();
				
				}
				
	
			}
			oResult.close();
			System.out.println("Time:\t"+(System.currentTimeMillis()-start));
		} catch (Exception e) {
			// TODO: handle exception
		}	
	
	}



}
