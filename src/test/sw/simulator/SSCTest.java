package test.sw.simulator;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.coalescent.ConstantPopulation;
import jebl.evolution.coalescent.ExponentialGrowth;
import jebl.evolution.coalescent.LogisticGrowth;
import jebl.evolution.io.NexusExporter;
import jebl.evolution.sequences.Sequence;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.treesimulation.CoalescentIntervalGenerator;
import jebl.evolution.treesimulation.IntervalGenerator;
import jebl.evolution.treesimulation.TreeSimulator;


import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import dr.app.seqgen.SeqGen;
import dr.evolution.io.Importer;
import dr.evolution.io.NexusImporter;
import dr.evolution.io.TreeImporter;
import dr.evolution.tree.Tree;
import dr.evomodel.sitemodel.GammaSiteModel;
import dr.evomodel.sitemodel.SiteModel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.HKY;
import dr.math.matrixAlgebra.Vector;




import sw.simulator.SSC;
import sw.simulator.SeqGenMod;

public class SSCTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSSC() {
		SSC sim = new SSC(5, 2, 100);
		Alignment ali = sim.simulateAlignment(3300, 1.75E-5);
//		System.out.println("Pattern Count:\t"+ali.getPatternCount());
//		System.out.println("Site    Count:\t"+ali.getSiteCount() );
		Set<Sequence> allS = ali.getSequences();
		for (Sequence ss : allS) {
			
//			System.out.println(ss.getTaxon().getName());
//			System.out.println(ss.getString());
			
		}
		
	}
	
	@Test
	public void testCorrectName() {
		
		int noTime = 5;
		int noSeq = 2;
		int timeGap = 100;
		SSC sim = new SSC(noTime, noSeq, timeGap);
		Alignment ali = sim.simulateAlignment(3300, 1.75E-5);
		
		List<Taxon> expectedList = new ArrayList<Taxon>();
		Set<Taxon> expectedSet = new HashSet<Taxon>();
		
		int seqCount = 1;
		double currentTime = 0;
		for (int i = 0; i < noTime; i++) {
			for (int j = 0; j < noSeq; j++) {
				Taxon t = Taxon.getTaxon("Tip_"+seqCount+"_"+currentTime);
				
				expectedList.add(t);
				expectedSet.add(t);
				seqCount++;
				
			}
			currentTime += timeGap;	
		}
		
		List<Taxon> actualList = ali.getTaxa();
		Collections.sort(actualList);
		Collections.sort(expectedList);
		
		Set<Sequence> actualSet = ali.getSequences();

		assertEquals(expectedList, actualList);
	}
	
	@Test
	public void testGenTree() throws Exception {
		//from BEAST
		double[] samplingTimes = new double[] {
				0.0, 0.0, 0.0, 0.0, 0.0, 5.0, 5.0, 5.0, 5.0, 5.0
		};

		LogisticGrowth logisticGrowth = new LogisticGrowth();
		logisticGrowth.setN0(10);
		logisticGrowth.setGrowthRate(2.0);
		logisticGrowth.setTime50(5);

		ExponentialGrowth exponentialGrowth = new ExponentialGrowth();
		exponentialGrowth.setN0(10);
		exponentialGrowth.setGrowthRate(0.1);

		ConstantPopulation constantPopulation = new ConstantPopulation();
		constantPopulation.setN0(10);

		IntervalGenerator intervals = new CoalescentIntervalGenerator(exponentialGrowth);
		TreeSimulator sim = new TreeSimulator("tip", samplingTimes);
//		RootedTree tree1 = sim.simulate(true);
//		RootedTree tree2 = sim.oldSimulate(true);
//
//		List<Double> heights1 = new ArrayList<Double>();
//		for (Node node : tree1.getInternalNodes()) {
//			heights1.add(tree1.getHeight(node));
//		}
//
//		List<Double> heights2 = new ArrayList<Double>();
//		for (Node node : tree2.getInternalNodes()) {
//			heights2.add(tree2.getHeight(node));
//		}
//
//		Collections.sort(heights1);
//		Collections.sort(heights2);
//
//		for (int i = 0; i < heights1.size(); i++) {
//			System.out.println(i + "\t" + heights1.get(i) + "\t" + heights2.get(i));
//		}

		int REPLICATE_COUNT = 1;

		try {
			RootedTree[] trees = new RootedTree[REPLICATE_COUNT];

			System.err.println("Simulating " + REPLICATE_COUNT + " trees of " + samplingTimes.length + " tips:");
			System.err.print("[");
			for (int i = 0; i < REPLICATE_COUNT; i++) {

				trees[i] = sim.simulate(intervals, true);
				if (i != 0 && i % 100 == 0) {
					System.err.print(".");
				}
			}
			System.err.println("]");

			Writer writer = new FileWriter("simulated.trees");
			NexusExporter exporter = new NexusExporter(writer);
			exporter.exportTrees(Arrays.asList(trees));

			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	public void testSeqGen() {
		//from BEAST
		System.out.println("testSeqGen");
		String treeFileName = "simulated.trees";
		String outputFileStem = "simulated.seq";

		int length = 470;

		double[] frequencies = new double[] { 0.25, 0.25, 0.25, 0.25 };
		double kappa = 10.0;
		double alpha = 0.5;
		double substitutionRate = 1.0E-9;
		int categoryCount = 0;
		double damageRate = 1.56E-6;

		FrequencyModel freqModel = new FrequencyModel(
				dr.evolution.datatype.Nucleotides.INSTANCE, frequencies);

		HKY hkyModel = new HKY(kappa, freqModel);
		SiteModel siteModel = null;

		if (categoryCount > 1) {
			siteModel = new GammaSiteModel(hkyModel, alpha, categoryCount);
		} else {
			// no rate heterogeneity
			siteModel = new GammaSiteModel(hkyModel);
		}

		List<Tree> trees = new ArrayList<Tree>();

		FileReader reader = null;
		try {
			reader = new FileReader(treeFileName);
			TreeImporter importer = new NexusImporter(reader);

			while (importer.hasTree()) {
				Tree tree = importer.importNextTree();
				trees.add(tree);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (Importer.ImportException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		SeqGenMod seqGen = new SeqGenMod(length, substitutionRate, freqModel,
				hkyModel, siteModel, damageRate);
		int i = 1;
		for (Tree tree : trees) {
			Alignment alignment = seqGen.simulate(tree);

			FileWriter writer = null;
			try {
				writer = new FileWriter(outputFileStem
						+ (i < 10 ? "00" : (i < 100 ? "0" : "")) + i + ".nex");
				NexusExporter exporter = new NexusExporter(writer);

				exporter.exportAlignment(alignment);

				writer.close();

				i++;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

	

	}

	
	@Test
	public void testSetKappa() throws Exception {
		//from BEAST
	
		double[] frequencies = new double[] { 0.25, 0.25, 0.25, 0.25 };
		double kappa = 1;
		FrequencyModel freqModel = new FrequencyModel(
				dr.evolution.datatype.Nucleotides.INSTANCE, frequencies);
	
	    double time = 0.1;
	    HKY hky = new HKY(kappa,freqModel);
	    double[] probs = new double[16];
	    hky.getTransitionProbabilities(time,probs);
	    System.out.println("kappa="+kappa+" probs = "+new Vector(probs));
	    
	    kappa = 2;
	    hky.setKappa(kappa);
	    hky.getTransitionProbabilities(time,probs);
	    System.out.println("kappa="+kappa+" probs = "+new Vector(probs));
		
	
	}
}
