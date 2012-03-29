package sw.simulator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.coalescent.ConstantPopulation;
import jebl.evolution.coalescent.DemographicFunction;
import jebl.evolution.io.NexusExporter;
import jebl.evolution.trees.RootedTree;

import jebl.evolution.treesimulation.CoalescentIntervalGenerator;
import jebl.evolution.treesimulation.IntervalGenerator;
import jebl.evolution.treesimulation.TreeSimulator;

import com.google.common.primitives.Doubles;

import dr.app.seqgen.SeqGen;
import dr.evolution.io.Importer;
import dr.evolution.io.NexusImporter;
import dr.evolution.io.TreeImporter;
import dr.evomodel.sitemodel.GammaSiteModel;
import dr.evomodel.sitemodel.SiteModel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.HKY;
import dr.evolution.tree.Tree;;
/*
 Watch out on changes at pop size
 either use numerical intergration
 or intergrated over time properly
 or full scare sample every interval 
 */
public class SSC {

	public SSC() {

		jeblGenTree();
		testSegGen();

	}

	public void jeblGenTree() {

		double N0 = 3000;
		DemographicFunction dfConstant = new ConstantPopulation(N0);
		CoalescentIntervalGenerator ci = new CoalescentIntervalGenerator(
				dfConstant);

		TreeSimulator tSim = new TreeSimulator("T1", new double[] { 0, 10, 10,
				20 });
		// tSim.main(new String[] { "A" });
		double[] t0 = new double[50];
		double[] t1 = new double[50];
		double[] t2 = new double[50];
		Arrays.fill(t0, 0);
		Arrays.fill(t1, 400);
		Arrays.fill(t2, 800);
		double[] samplingTimes = Doubles.concat(t0, t1, t2);
		// double[] samplingTimes = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
		// 400.0,
		// 400.0, 400.0, 400.0, 400.0 };

		ConstantPopulation constantPopulation = new ConstantPopulation();
		constantPopulation.setN0(N0);

		IntervalGenerator intervals = new CoalescentIntervalGenerator(
				constantPopulation);
		TreeSimulator sim = new TreeSimulator("tip", samplingTimes);
		// RootedTree tree1 = sim.simulate(true);
		// RootedTree tree2 = sim.oldSimulate(true);
		//
		// List<Double> heights1 = new ArrayList<Double>();
		// for (Node node : tree1.getInternalNodes()) {
		// heights1.add(tree1.getHeight(node));
		// }
		//
		// List<Double> heights2 = new ArrayList<Double>();
		// for (Node node : tree2.getInternalNodes()) {
		// heights2.add(tree2.getHeight(node));
		// }
		//
		// Collections.sort(heights1);
		// Collections.sort(heights2);
		//
		// for (int i = 0; i < heights1.size(); i++) {
		// System.out.println(i + "\t" + heights1.get(i) + "\t" +
		// heights2.get(i));
		// }

		int REPLICATE_COUNT = 1;
		System.out.println(REPLICATE_COUNT);
		try {
			RootedTree[] trees = new RootedTree[REPLICATE_COUNT];

			System.err.println("Simulating " + REPLICATE_COUNT + " trees of "
					+ samplingTimes.length + " tips:");
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

		System.out.println("End");
	}

	public void testSegGen() {

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

		List<Tree> trees = new ArrayList<dr.evolution.tree.Tree>();

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

		SeqGen seqGen = new SeqGen(length, substitutionRate, freqModel,
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
}
