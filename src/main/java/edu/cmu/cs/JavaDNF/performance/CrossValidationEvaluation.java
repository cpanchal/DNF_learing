package edu.cmu.cs.JavaDNF.performance;

import java.util.ArrayList;

import edu.cmu.cs.JavaDNF.algorithm.FeatureSelector;
import edu.cmu.cs.JavaDNF.algorithm.LearnCNFClauses;
import edu.cmu.cs.JavaDNF.algorithm.LearnDNF;
import edu.cmu.cs.JavaDNF.algorithm.LearnLiteralsFromGS;
import edu.cmu.cs.JavaDNF.interfaces.ICrossValidationIterator;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.compbio.lib.TwoClassesSequences;
import edu.cmu.cs.JavaDNF.lib.Utils;
import edu.cmu.cs.machinelearning.lib.PredictionQuality;

/**
 * 
 * @author chuang
 */
public class CrossValidationEvaluation {

	/** */
	private PredictionQuality quality;

	/**
	 * 
	 * @param gs
	 */
	public void perform(IDataSequence gs, int fold) {
		ICrossValidationIterator lood = null;
		if (fold == 0) {
			lood = new LeaveOneOutIterator(gs);
		} else if (fold > 0) {
			lood = new CrossValidationIterator(gs, fold);
		}
		ArrayList<PredictionQuality> qualities = new ArrayList<PredictionQuality>();
		
		int index = 0;
		while (lood.hasNext()) {
			Utils.debugln("=============== Iteration " + index);

			TrainTestDataClass ttdc = lood.next();
			FeatureSelector fs = new FeatureSelector(gs);
			System.out.println("After feature selection: "
					+ LearnDNF.COVERAGE_CUTOFF);
			LearnDNF learnDNF = new LearnDNF(ttdc.getTrainGenotypeSequences(),
					fs.hypotheses);
			PredictionQuality quality = new PredictionQuality();
			for (int i = 0; i < ttdc.getTestSequences().getNumberOfSequence(); ++i) {
				double p = learnDNF.getDNFs().predict(
						ttdc.getTestSequences().get(i));
				quality.updateByOneTest(ttdc.getTestGroundTruth().get(i),
						(p == 1));
			}
			qualities.add(quality);
			index++;
		}
		
		double sensitivity = 0;
		double specificity = 0;
		double error = 0;
		for (PredictionQuality q : qualities) {
			Utils.debugln("The final prediction error is: " + q.ToString());
			sensitivity += q.getSensitivity();
			specificity += q.getSpecificity();
			error += q.getErrorRating();
		}
		sensitivity /= qualities.size();
		specificity /= qualities.size();
		error /= qualities.size();
		System.out.println("Final sensitivity: " + sensitivity);
		System.out.println("Final specificity: " + specificity);
		System.out.println("Final error: " + error);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String dataNameA = "";
		String dataNameB = "";
		int fold = 0;
		double ROCWeight = 0.5;
		if (args.length > 0) {
			dataNameA = args[0];
			dataNameB = args[1];
			fold = Integer.parseInt(args[2]);
			//if (args.length > 3)
				ROCWeight = Double.parseDouble(args[3]);
		}
		LearnDNF.GREEDY_WEIGHT = ROCWeight;

		AbstractTerm.IS_FAST_VERSION = false;
		AbstractTerm.IS_VERBOSE = true;
		AbstractTerm.USE_NEGATION = true;
		AbstractTerm.USE_AMBIGUOUS_CHAR = false;

		LearnLiteralsFromGS.IS_FAST_VERSION = false;
		LearnLiteralsFromGS.USE_UNIQIFY = true;
		LearnLiteralsFromGS.USE_RULE = false;

		LearnCNFClauses.IS_FAST_VERSION = true;
		LearnCNFClauses.MAX_SIZE_OF_CLAUSE = 4;

		FeatureSelector.IS_VERBOSE = true;
		FeatureSelector.USE_GREEDY = true;

		LearnDNF.IS_VERBOSE = true;
		LearnDNF.IS_FAST_VERSION = true;
		LearnDNF.USE_GENERALIZATION = false;
		LearnDNF.USE_GREEDY = true;
		LearnDNF.COVERAGE_CUTOFF = 1.0;
		LearnDNF.DENSITY_CUTOFF = 0;

		IDataSequence gs = new TwoClassesSequences(dataNameA, dataNameB);
		CrossValidationEvaluation loop = new CrossValidationEvaluation();
		loop.perform(gs, fold);

	}
}
