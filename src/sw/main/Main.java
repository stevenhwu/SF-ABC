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
import java.util.HashMap;
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

	private static final int TUNESIZE = TunePar.TUNESIZE;//400 ; 
	private static final int TUNEGROUP = TunePar.TUNEGROUP;//10; 
	
	private static final double initScale = 0.5;
	private static final String VERSION = "1.0";
	
	private static final boolean DYNAMIC_ERROR_RATE = false;//Alter the error rate in MCMC. Testing methods only with unknown behaviour. Turn on with caution!!
	
	public static void main(String[] args) {


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
		
		options.addOption(Option.builder("i").longOpt("infile").hasArg()
				.argName("infile").desc("REQUIRED: Infile (nexus format)")
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
				.desc("REQUIRED: Three Parameters in the following orders: "
						+ "(1) Length of preprocessing, "
						+ "(2) Length of MCMC chain, "
						+ "(3) Sample every n iterations. ");
		options.addOption(C.build());

		C = Option.builder("t").longOpt("time")
				.numberOfArgs(2).argName("numTime interval")
				.desc("REQUIRED: Two Parameters in the following orders: "
						+ "(1) Number of time points, "
						+ "(2) Interval between two time points");
		options.addOption(C.build());

		options.addOption(Option.builder("Xe").longOpt("error").hasArg()
				.argName("").desc("Error threshold for ABC. [default: 0.01]")
				.build());
		options.addOption(Option.builder("Xm").longOpt("initMutation").hasArg()
				.argName("").desc("Initial mutation rate. [default: 0.00001]")
				.build());
		options.addOption(Option.builder("Xp").longOpt("initPopulation").hasArg()
				.argName("").desc("Initial Population size. [default: 5000]")
				.build());
		
		HelpFormatter formatter = new HelpFormatter();
		String syntax = "sfabc -i <infile> -m <preprocess mcmc sampling> -t <numTime interval>";
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
				formatter.printHelp(syntax, header, options, footer, false);
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
			if (cmd.hasOption("infile")){
//				numberOfObservation= Integer.parseInt(cmd.getOptionValue("obs"));
				String obsDataName = cmd.getOptionValue("infile");
				setting = new Setting(obsDataName);
				
				NexusImporter imp = new NexusImporter(new FileReader(setting.getDataFile()));
				Alignment jeblAlignment = imp.importAlignments().get(0);
				int seqLength = jeblAlignment.getPatternCount();
				int totalSeqCount = jeblAlignment.getPatternLength(); 
				setting.setSeqInfo(seqLength, totalSeqCount);
//				System.out.println("Infile: "+obsDataName);
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
				
				setting.setTime(numTimePoints, intervalBetweenTime);
				System.out.println("Number of time points: "+numTimePoints);
			}			
			else{
				System.out.println("Error: Number of time point (-t,--time) required\n");
				System.exit(6);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		}
		
		startSimulation(setting);

	}
	
//	public static void startSimulation(String[] args) {
	public static void startSimulation(Setting setting) {
		
		try {
			ABCSetup(setting);
			
			System.out.println(setting.toString());
			//TODO: Check NO_SEQ_PER_TIME, num seq used for simulation, actual number seq per time in the real data
			
//			MCMCFeatures.testStatFile(setting);
			
			generateStatFile(setting);

			// double error = MCMCFeatures.generateErrorRate(setting, NO_REPEAT_CAL_ERROR, NO_REPEAT_PER_PARAMETERS);
			// setting.setError(error);
		
			ABCUpdateMCMC(setting);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	public static Setting ABCSetup(Setting setting) throws IOException {
		
//		double[] initValue = new double[]{0.00001, 3000};
				
		HashMap<String, Double> initValues = setting.GetInitValues();
		String[] paramListName = new String[]{"mu", "popsize"};
		String[] statList = new String[]{"dist", "chisq", "var", "covar", "sitePattern"};
		
		setting.setParamList(paramListName);
		setting.setStatList(statList);				

		double scaleFactor = 5.0;
		double muMean = initValues.get("mu");
		double muLower = muMean / scaleFactor;
		double muUpper = muMean * scaleFactor;
		
		double popSizeMean = initValues.get("popsize");
		double popSizeLower = popSizeMean / scaleFactor;
		double popSizeUpper = popSizeMean * scaleFactor;
		
		ParaMu unifMu = new ParaMu(new UniformDistribution(muLower, muUpper));
		ParaPopsize unifPopsize = new ParaPopsize(new UniformDistribution(popSizeLower, popSizeUpper));
	
		
		TunePar tPar = new TunePar(TUNESIZE, TUNEGROUP, new double[]{initScale , initScale}, new String[] { "Scale", "Scale" });

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



}
