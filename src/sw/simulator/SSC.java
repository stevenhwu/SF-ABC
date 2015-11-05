package sw.simulator;

import java.io.IOException;
import java.util.Arrays;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.coalescent.ConstantPopulation;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.treesimulation.TreeSimulator;
import sw.abc.parameter.ParametersCollection;
import sw.main.Setting;

import com.google.common.primitives.Doubles;

import dr.evolution.io.Importer.ImportException;
import dr.evolution.io.NewickImporter;
import dr.evolution.tree.Tree;
import dr.evomodel.sitemodel.GammaSiteModel;
import dr.evomodel.sitemodel.SiteModel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.HKY;


public class SSC {

	private int seqLength;
			
	private int popSize;
	private double substitutionRate;
	
	private ConstantPopulation constPop = new ConstantPopulation(3000);
	private CoalescentIntervalGeneratorMod intervals = new CoalescentIntervalGeneratorMod(constPop);
	private TreeSimulator treeSim;
	private SeqGenMod seqGen;
	private String prefix;
	
	public SSC(int seqLength, int noSeqPerTime, int noTime, int timeGap) {
		this(seqLength, noSeqPerTime, noTime, timeGap, "Tip_");
	}
	public SSC(Setting setting) {
		this(setting.getSeqLength(), setting.getNoSeqPerTime(), setting.getNoTime(), setting.getTimeGap());
	}	
	public SSC(int seqLength, int noSeqPerTime, int noTime, int timeGap, String prefix) {

		this.prefix = prefix;
		this.seqLength = seqLength;
		
		setupDefaultModel();
		
		int[] samplingCounts = new int[noTime];
		Arrays.fill(samplingCounts, noSeqPerTime);
		double[] samplingTimes = genTimeSetting(noTime, timeGap);
		treeSim = new TreeSimulator(this.prefix, samplingCounts, samplingTimes);
		
	}



	public Tree simulateTree(int pSize){//, double sRate) {
		setPopSize(pSize);
//		this.substitutionRate = sRate;

		constPop.setN0(popSize);
		intervals.setDemographicFunction(constPop);
//		seqGen.setSubstitutionRate(substitutionRate);

		Tree sTree = simTree();

		return sTree;
	}
	
	public Alignment simulateAlignment(ParametersCollection allPar) {
		setPopSize( allPar.getNewValue("popsize") );
		setSubstitutionRate(allPar.getNewValue("mu"));
		return simulateAlignment();
	}
	
	public Alignment simulateAlignment(int pSize, double sRate) {
		setPopSize(pSize);
		setSubstitutionRate(sRate);
		return simulateAlignment();
	}
	

	private Alignment simulateAlignment(){
		constPop = new ConstantPopulation(popSize);
		intervals = new CoalescentIntervalGeneratorMod(constPop);
		
//		constPop.setN0(popSize);
//		intervals.setDemographicFunction(constPop);
		seqGen.setSubstitutionRate(substitutionRate);

		Tree sTree = simTree();
		
		Alignment alignment = simSeq(sTree);
		
		return alignment;
	}

	
	private Tree simTree() {

		RootedTree jTree = treeSim.simulate(intervals, false);
		Tree bTree = convertJEBLTreeToBEASTTree(jTree);

		return bTree;
	}

	private Alignment simSeq(Tree bTree) {
		Alignment alignment = seqGen.simulate(bTree);
		
		return alignment;
	}

	private Tree[] simTrees(int noReplication) {

		Tree[] bTrees = new Tree[noReplication];
		for (int i = 0; i < noReplication; i++) {
			bTrees[i] = simTree();
		}
		return bTrees;

	}

	
	private Alignment[] simSeqs(Tree[] bTrees) {

		Alignment[] alignments = new Alignment[bTrees.length];
		for (int i = 0; i < bTrees.length; i++) {

			alignments[i] = seqGen.simulate(bTrees[i]);

		}
		return alignments;

	}
	
	private Alignment[] simSeqsSameTree(Tree bTree, int noRep) {

		Alignment[] alignments = new Alignment[noRep];
		for (int i = 0; i < noRep; i++) {
			alignments[i] = seqGen.simulate(bTree);
		}
		return alignments;

	}
	private void setupDefaultModel(){// JC

		double[] frequencies = new double[] { 0.25, 0.25, 0.25, 0.25 };
		double kappa = 1;
		double damageRate = 0;//1.56E-6;

		FrequencyModel freqModel = new FrequencyModel(
				dr.evolution.datatype.Nucleotides.INSTANCE, frequencies);

		HKY hkyModel = new HKY(kappa, freqModel);
		SiteModel siteModel = new GammaSiteModel(hkyModel);

//		int categoryCount = 0;
//		double alpha = 0.5;
//		SiteModel siteModel = null;
//		if (categoryCount > 1) {
//			siteModel = new GammaSiteModel(hkyModel, alpha, categoryCount);
//		} else {
//			// no rate heterogeneity
//			siteModel = new GammaSiteModel(hkyModel);
//		}
//		
		seqGen = new SeqGenMod(seqLength, substitutionRate, freqModel,
				hkyModel, siteModel, damageRate);
		
	}
	
	private double[] genTimeSetting(int noTime, double timeGap) {
		double timePoint = 0;
		double[] samplingTimes = new double[noTime];
		for (int i = 0; i < noTime; i++) {
			samplingTimes[i] = timePoint;
			timePoint += timeGap;
		}
		return samplingTimes;
	}
	
	private double[] genTimeSetting(int noTime, int noSeq, double timeGap) {
	
		double timePoint = 0;
		double[][] samplingTimes = new double[noTime][noSeq];
		for (int i = 0; i < noTime; i++) {
			samplingTimes[i] = new double[noSeq];
			Arrays.fill(samplingTimes[i], timePoint);
			timePoint += timeGap;
		}
//		TreeSimulator simt = new TreeSimulator(prefix, samplingTimes);
		return Doubles.concat(samplingTimes);

	}
	/**
	 * @param popSize the popSize to set
	 */
	private void setPopSize(int popSize) {
		this.popSize = popSize;
	}
	private void setPopSize(double popSize) {
		setPopSize((int) popSize);
	}
	/**
	 * @param substitutionRate the substitutionRate to set
	 */
	private void setSubstitutionRate(double substitutionRate) {
		this.substitutionRate = substitutionRate;
	}

	/**
	 * @return the popSize
	 */
	public int getPopSize() {
		return popSize;
	}

	/**
	 * @return the substitutionRate
	 */
	public double getSubstitutionRate() {
		return substitutionRate;
	}

	public static Tree convertJEBLTreeToBEASTTree(RootedTree jTree) {
		
		Tree bTree = null;
		try {
			String treeS = jebl.evolution.trees.Utils.toNewick(jTree);
			NewickImporter importer = new NewickImporter(treeS);
			bTree = importer.importNextTree();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		}
		
		return bTree;
		
	}

//	public void setPopSize(ParametersCollection allParUniformPrior) {
//		setPopSize((int) allParUniformPrior.getValue("popSize"));
//		
//	}
//
//	public void setSubstitutionRate(ParametersCollection allParUniformPrior) {
//		setSubstitutionRate(allParUniformPrior.getValue("mu"));
//		
//	}
}
