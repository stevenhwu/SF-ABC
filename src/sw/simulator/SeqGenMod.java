package sw.simulator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jebl.evolution.alignments.Alignment;
import jebl.evolution.alignments.BasicAlignment;
import jebl.evolution.sequences.BasicSequence;
import jebl.evolution.sequences.NucleotideState;
import jebl.evolution.sequences.Nucleotides;
import jebl.evolution.sequences.SequenceType;
import jebl.evolution.sequences.State;
import jebl.evolution.taxa.Taxon;
import jebl.math.Random;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.sitemodel.SiteModel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.SubstitutionModel;

/**
 * @author Andrew Rambaut
 * @version $Id$
 */
public class SeqGenMod {
	
	double substitutionRate;
    double[][][] transitionProbabilities;

    public SeqGenMod(final int length, double substitutionRate, final FrequencyModel freqModel, final SubstitutionModel substModel, final SiteModel siteModel, final double damageRate) {
        this.length = length;
        this.substitutionRate = substitutionRate;
        this.freqModel = freqModel;
        this.substModel = substModel;
        this.siteModel = siteModel;
        this.damageRate = damageRate;
        int stateCount = substModel.getDataType().getStateCount();
        

        transitionProbabilities = new double[length][stateCount][stateCount];
        
    }

    /**
	 * @param substitutionRate the substitutionRate to set
	 */
	public void setSubstitutionRate(double substitutionRate) {
		this.substitutionRate = substitutionRate;
	}

	public Alignment simulate(Tree tree) {


        int[] initialSequence = new int[length];

        drawSequence(initialSequence, freqModel);

        int[] siteCategories = new int[length];
        
        drawSiteCategories(siteModel, siteCategories);


        double[] rates = new double[siteModel.getCategoryCount()];
        for (int i = 0; i < rates.length; i++) {
            rates[i] = siteModel.getRateForCategory(i) * substitutionRate;
        }

        for (int i = 0; i < tree.getChildCount(tree.getRoot()); i++) {
            NodeRef child = tree.getChild(tree.getRoot(), i);
            evolveSequences(initialSequence, tree, child, substModel, siteCategories, rates);
        }

            
//        Map<State, State[]> damageMap = new HashMap<State, State[]>();
//        damageMap.put(Nucleotides.A_STATE, new State[]{Nucleotides.G_STATE});
//        damageMap.put(Nucleotides.C_STATE, new State[]{Nucleotides.T_STATE});
//        damageMap.put(Nucleotides.G_STATE, new State[]{Nucleotides.A_STATE});
//        damageMap.put(Nucleotides.T_STATE, new State[]{Nucleotides.C_STATE});

//        BasicAlignment alignment = new BasicAlignment();

        List<NucleotideState> nucs = jebl.evolution.sequences.Nucleotides.getCanonicalStates();
        
BasicSequence[] allSeq = new BasicSequence[tree.getExternalNodeCount()];
        for (int i = 0; i < tree.getExternalNodeCount(); i++) {
            NodeRef node = tree.getExternalNode(i);
            int[] seq = (int[]) tree.getNodeTaxon(node).getAttribute("seq");
            State[] states = new State[seq.length];
            for (int j = 0; j < states.length; j++) {
                states[j] = nucs.get(seq[j]);
            }

//            if (damageRate > 0) {
//                damageSequence(states, damageRate, tree.getNodeHeight(node), damageMap);
//            }

            allSeq[i] = new BasicSequence(SequenceType.NUCLEOTIDE,
                    Taxon.getTaxon(tree.getNodeTaxon(node).getId()),
                    states);
        }
        BasicAlignment alignment = new BasicAlignment(allSeq);

//BasicAlignment alignment = new BasicAlignment();


        return alignment;
    }

    void drawSiteCategories(SiteModel siteModel, int[] siteCategories) {
        double[] categoryProportions = siteModel.getCategoryProportions();
        double[] cumulativeProportions = new double[categoryProportions.length];
        cumulativeProportions[0] = categoryProportions[0];
        for (int i = 1; i < cumulativeProportions.length; i++) {
            cumulativeProportions[i] = cumulativeProportions[i - 1] + categoryProportions[i];
        }

        for (int i = 0; i < siteCategories.length; i++) {
            siteCategories[i] = draw(cumulativeProportions);
        }
    }

    public void drawSequence(int[] initialSequence, FrequencyModel freqModel) {
        double[] freqs = freqModel.getCumulativeFrequencies();
        for (int i = 0; i < initialSequence.length; i++) {
            initialSequence[i] = draw(freqs);
        }
    }

    void evolveSequences(int[] sequence0,
                         Tree tree, NodeRef node,
                         SubstitutionModel substModel,
                         int[] siteCategories,
                         double[] categoryRates) {
        int stateCount = substModel.getDataType().getStateCount();

        int[] sequence1 = new int[sequence0.length];

//        double[][][] transitionProbabilities = new double[siteCategories.length][stateCount][stateCount];

        for (int i = 0; i < categoryRates.length; i++) {
            double branchLength = tree.getBranchLength(node) * categoryRates[i];
            double[] tmp = new double[stateCount * stateCount];
            substModel.getTransitionProbabilities(branchLength, tmp);

            int l = 0;
            for (int j = 0; j < stateCount; j++) {
                transitionProbabilities[i][j][0] = tmp[l];
                l++;
                for (int k = 1; k < stateCount; k++) {
                    transitionProbabilities[i][j][k] = transitionProbabilities[i][j][k - 1] + tmp[l];
                    l++;
                }

            }
        }

        evolveSequence(sequence0, siteCategories, sequence1);

        if (!tree.isExternal(node)) {
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                evolveSequences(sequence1, tree, child, substModel, siteCategories, categoryRates);
            }
        } else {
            tree.getNodeTaxon(node).setAttribute("seq", sequence1);
        }
    }

    private void evolveSequence(int[] ancestralSequence,
                                int[] siteCategories,
//                                double[][][] cumulativeTransitionProbabilities,
                                int[] descendentSequence) {

        for (int i = 0; i < ancestralSequence.length; i++) {
            descendentSequence[i] = draw(transitionProbabilities[siteCategories[i]][ancestralSequence[i]]);
        }
    }

    private void damageSequence(State[] sequence, double rate, double time, Map<State, State[]> damageMap) {
        double pUndamaged = Math.exp(-rate * time);

        for (int i = 0; i < sequence.length; i++) {
            double r = Random.nextDouble();
            if (r >= pUndamaged) {
                State[] states = damageMap.get(sequence[i]);
                if (states.length > 0) {
                    int index = Random.nextInt(states.length);
                    sequence[i] = states[index];
                } else {
                    sequence[i] = states[0];
                }
            }
        }
    }

    /**
     * draws a state from using a set of cumulative frequencies (last value should be 1.0)
     *
     * @param cumulativeFrequencies
     * @return
     */

    private static int draw(double[] cumulativeFrequencies) {
        double r = Random.nextDouble();
    	
        // defensive - make sure that it is actually set...
        int state = -1;

        for (int j = 0; j < cumulativeFrequencies.length; j++) {
            if (r < cumulativeFrequencies[j]) {
                state = j;
                break;
            }
        }

        assert (state != -1);

        return state;
    }

    final int length;
    final FrequencyModel freqModel;
    final SubstitutionModel substModel;
    final SiteModel siteModel;
    final double damageRate;

}
