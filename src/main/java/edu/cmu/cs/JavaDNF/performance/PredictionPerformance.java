package edu.cmu.cs.JavaDNF.performance;

import edu.cmu.cs.JavaDNF.algorithm.LearnLiteralsFromGS;
import edu.cmu.cs.JavaDNF.algorithm.FeatureSelector;
import edu.cmu.cs.JavaDNF.algorithm.LearnCNFClauses;
import edu.cmu.cs.JavaDNF.algorithm.LearnDNF;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.JavaDNF.lib.Utils;

/**
 * 
 * @author cc
 */
public class PredictionPerformance {

	/** */
	public static boolean USE_FEATURE_SELECTION = true;
	/** */
	public static boolean IS_LEARNING_PARAMETERS = true;
	/** */
	public static double DENSITY_CUTOFF = 0.1;
	/** */
	public static double PREDICTION_CUTOFF = 0.5;
	/** */
	public static boolean IS_VERBOSE = true;
	/** private field */
	private int error;
	private double sensitivity;
	private double specificity;
	private IDataSequence gs;

	/**
	 * 
	 * @param gs
	 */
	public PredictionPerformance(IDataSequence gs) {
		error = 0;
		sensitivity = 0;
		specificity = 0;
		this.gs = gs;
		// this.useLeaveOneOut();
	}

	/**
	 * Given two parameters and cross validation iterator Learn the error rate
	 * and sensitivity/specificity.
	 * 
	 * @param accIterator
	 * @param gs
	 * @param DENSITY_CUTOFF
	 * @param predictionCutOff
	 */
	public void performance(ACrossValidationIterator accIterator,
			IDataSequence gs, double densityCutOff, double predictionCutOff) {

		int totalNumber = gs.getTotalNumber();

		double[] predictions = new double[totalNumber];
		boolean[] groundTruth = new boolean[totalNumber];

		int index = 0;
		while (accIterator.hasNext()) {
			Utils.debugln("========= Iteration " + index + " =========");
			TrainTestDataClass ttdc = accIterator.next();

			LearnDNF.USE_FEATURE_SELECTION = USE_FEATURE_SELECTION;
			LearnDNF.DENSITY_CUTOFF = densityCutOff;
			LearnDNF cd = new LearnDNF(ttdc.getTrainGenotypeSequences());
			Utils.debugln("Learned DNF # is: " + cd.dnfList.getNumberOfDNFs());

			for (int i = 0; i < ttdc.getTestSequences().getNumberOfSequence(); ++i) {
				predictions[index] = cd.dnfList.predict(ttdc.getTestSequences()
						.get(i));
				groundTruth[index] = ttdc.getTestGroundTruth().get(i);
				Utils.debugln(predictions[index] + " " + groundTruth[index]);
				index++;
			}
		}

		if (IS_LEARNING_PARAMETERS) {
			double curPredictionCutOff = 0;
			double stepSize = 0.01;
			error = Integer.MAX_VALUE;

			while (curPredictionCutOff < 1) {
				curPredictionCutOff += stepSize;
				int curError = 0;
				double falsePositive = 0, falseNegative = 0;
				double truePositive = 0, trueNegative = 0;

				for (int i = 0; i < totalNumber; ++i) {
					if ((predictions[i] >= curPredictionCutOff) != groundTruth[i]) {
						curError++;
					}
					if (predictions[i] >= curPredictionCutOff) {
						if (groundTruth[i]) {
							truePositive++;
						} else {
							falsePositive++;
						}
					} else {
						if (groundTruth[i]) {
							falseNegative++;
						} else {
							trueNegative++;
						}
					}

				}
				if (curError <= error) {
					error = curError;
					PREDICTION_CUTOFF = curPredictionCutOff;
					this.sensitivity = truePositive
							/ (truePositive + falseNegative);
					this.specificity = trueNegative
							/ (trueNegative + falsePositive);
				}
			}
			if (IS_VERBOSE) {
				Utils.debug("learned: ");
				Utils.debugln(error + " " + sensitivity + " " + specificity
						+ " " + PREDICTION_CUTOFF);
			}

		} else {

			error = 0;
			double falsePositive = 0, falseNegative = 0;
			double truePositive = 0, trueNegative = 0;

			for (int i = 0; i < totalNumber; ++i) {
				if ((predictions[i] >= predictionCutOff) != groundTruth[i]) {
					error++;
				}
				if (predictions[i] >= predictionCutOff) {
					if (groundTruth[i]) {
						truePositive++;
					} else {
						falsePositive++;
					}
				} else {
					if (groundTruth[i]) {
						falseNegative++;
					} else {
						trueNegative++;
					}
				}
			}

			this.sensitivity = truePositive / (truePositive + falseNegative);
			this.specificity = trueNegative / (trueNegative + falsePositive);
		}
	}

	/**
	 * Given two parameters Learn the error rate and sensitivity/specificity
	 * with LeaveOneOut cross validation
	 */
	public void useLeaveOneOut(GenotypeSequences gs) {
		LeaveOneOutIterator lood = new LeaveOneOutIterator(gs);
		this.performance(lood, gs, DENSITY_CUTOFF, PREDICTION_CUTOFF);
	}

	/**
	 * Given two parameters Learn the error rate and sensitivity/specificity
	 * with k-fold cross validation
	 * 
	 * @param foldNumber
	 */
	public void useCrossValidation(IDataSequence gs, int foldNumber) {
		CrossValidationIterator cvi = new CrossValidationIterator(gs,
				foldNumber);
		this.performance(cvi, gs, DENSITY_CUTOFF, PREDICTION_CUTOFF);
	}

	/**
     * 
     */
	public void learnParameters() {

	}

	/**
     * 
     */
	public void completePerformance() {
		LeaveOneOutIterator lood = new LeaveOneOutIterator(gs);
		int totalNumber = gs.getTotalNumber();

		boolean[] predictions = new boolean[totalNumber];
		boolean[] groundTruth = new boolean[totalNumber];

		int index = 0;
		while (lood.hasNext()) {
			Utils.debugln("============ Iteration " + index + " ============");
			TrainTestDataClass ttdc = lood.next();

			IS_LEARNING_PARAMETERS = true;
			this.useCrossValidation(ttdc.getTrainGenotypeSequences(), 10);
			// this.useLeaveOneOut(ttdc.getTrainGenotypeSequences());

			IS_LEARNING_PARAMETERS = false;
			LearnDNF.DENSITY_CUTOFF = DENSITY_CUTOFF;
			LearnDNF cd = new LearnDNF(ttdc.getTrainGenotypeSequences());
			for (int i = 0; i < ttdc.getTestSequences().getNumberOfSequence(); ++i) {
				Utils.debugln(cd.dnfList
						.predict(ttdc.getTestSequences().get(i))
						+ ">"
						+ PREDICTION_CUTOFF);
				predictions[index] = (cd.dnfList.predict(ttdc
						.getTestSequences().get(i)) > PREDICTION_CUTOFF);
				groundTruth[index] = ttdc.getTestGroundTruth().get(i);
				Utils.debugln(predictions[index] + " " + groundTruth[index]);
				index++;
			}
		}

		error = 0;
		double falsePositive = 0, falseNegative = 0;
		double truePositive = 0, trueNegative = 0;

		for (int i = 0; i < totalNumber; ++i) {
			if (predictions[i] != groundTruth[i]) {
				error++;
			}
			if (predictions[i]) {
				if (groundTruth[i]) {
					truePositive++;
				} else {
					falsePositive++;
				}
			} else {
				if (groundTruth[i]) {
					falseNegative++;
				} else {
					trueNegative++;
				}
			}
		}

		this.sensitivity = truePositive / (truePositive + falseNegative);
		this.specificity = trueNegative / (trueNegative + falsePositive);
	}

	/**
	 * 
	 * @return
	 */
	public int getError() {
		return error;
	}

	/**
	 * 
	 * @return
	 */
	public double getSensitivity() {
		return this.sensitivity;
	}

	/**
	 * 
	 * @return
	 */
	public double getSpecificity() {
		return this.specificity;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String dataName = "";
		String dataSet = "";
		if (args.length > 1) {
			dataName = args[1];
		}
		if (args.length > 0) {
			dataSet = args[0];
		}
		dataSet = "/home/cc/Work/Research/Data/Promoter_Gene";
		// String[] dataNames = {"NFV", "SQV", "IDV", "RTV", "APV_New", "LPV",
		// "ATV"};
		String[] dataNames = { "Promoter_Gene" };

		GenotypeSequences.IS_VERBOSE = false;

		AbstractTerm.IS_VERBOSE = true;
		AbstractTerm.IS_FAST_VERSION = false;

		LearnLiteralsFromGS.IS_FAST_VERSION = false;
		LearnCNFClauses.IS_FAST_VERSION = true;

		FeatureSelector.IS_VERBOSE = false;
		FeatureSelector.USE_GREEDY = false;

		LearnDNF.IS_VERBOSE = false;
		LearnDNF.IS_FAST_VERSION = true;
		LearnDNF.USE_GREEDY = true;
		LearnDNF.USE_GENERALIZATION = false;
		LearnDNF.COVERAGE_CUTOFF = 1;

		PredictionPerformance.USE_FEATURE_SELECTION = true;
		PredictionPerformance.IS_LEARNING_PARAMETERS = true;

		PredictionPerformance pp;
		double[] densityCutOffs = { 0.3175 };

		for (int i = 0; i < dataNames.length; ++i) {
			dataName = dataNames[i];
			GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);

			for (int j = 0; j < densityCutOffs.length; ++j) {
				DENSITY_CUTOFF = densityCutOffs[j];
				PREDICTION_CUTOFF = 0.62;

				pp = new PredictionPerformance(gs);
				// pp.useLeaveOneOut(gs);
				// pp.useCrossValidation(10);
				pp.completePerformance();

				Utils.debugln("The results are: ");
				Utils.debugln("The error is: " + pp.getError());
				// Utils.debugln("The predictioncutoff is: " +
				// PREDICTION_CUTOFF);
				Utils.debugln(pp.getSensitivity() + " " + pp.getSpecificity());
			}
		}
	}
}
