package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.interfaces.ILearnDNF;
import edu.cmu.cs.JavaDNF.lib.DNF;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.JavaDNF.lib.CNF;
import edu.cmu.cs.JavaDNF.lib.DNFList;
import java.util.Vector;
import java.util.Collection;
import edu.cmu.cs.Algorithm.CombinatorialIterator;
import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.JavaDNF.lib.PositionalHypothesis;
import edu.cmu.cs.JavaDNF.lib.PositionalHypotheses;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.compbio.lib.TwoClassesSequences;
import edu.cmu.cs.JavaDNF.lib.Utils;

/**
 * 
 * @author Chris Wu
 * @email <chuangw@cs.cmu.edu>
 */
public class LearnDNF implements ILearnDNF {

	/** */
	public static boolean IS_FAST_VERSION = true;
	/** */
	public static boolean IS_VERBOSE = false;
	/** */
	public static boolean USE_GENERALIZATION = false;
	/** */
	public static boolean USE_GREEDY = true;
	/**
	 * when greedily select CNF, use class data amount to balance
	 * sensitivity/specificity
	 */
	public static boolean USE_GREEDY_WEIGHT = true;
	/**
	 * balance sensitivity and specificity
	 */
	public static double GREEDY_WEIGHT = 0.5;
	/** */
	public static double COVERAGE_CUTOFF = 1;
	/** */
	public static boolean USE_FEATURE_SELECTION = true;
	/** */
	public static double DENSITY_CUTOFF = 0;
	/** */
	public DNFList dnfList;

	/**
     * 
     */
	public LearnDNF() {
		dnfList = new DNFList();
	}

	/**
	 * 
	 * @param ccc
	 */
	public LearnDNF(LearnCNFClauses ccc) {
		this();
		this.learnCombinatorially(ccc);
	}

	/**
	 * Learn DNFs given the density cutoff
	 * 
	 * @param gs
	 */
	public LearnDNF(IDataSequence gs) {
		this();
		if (USE_FEATURE_SELECTION) {
			FeatureSelector fs = new FeatureSelector(gs);
			// Utils.debugln("The number of hypotheses is: " +
			// fs.hypotheses.numberOfHypotheses);
			for (int i = 0; i < fs.hypotheses.numberOfHypotheses; ++i) {
				if (fs.hypotheses.get(i).hypothesisDensitySummation > DENSITY_CUTOFF
						* fs.hypotheses.numberOfHypotheses
						* fs.hypotheses.hypothsisSize) {
					this.learn(gs, fs.hypotheses.get(i));
				}
			}
		} else {
			this.learn(gs);
		}
	}

	/**
	 * Learn the DNF corresponding to the max hypothesis
	 * 
	 * @param gs
	 * @param learnByMaxHypothesis
	 */
	public LearnDNF(IDataSequence gs, boolean learnByMaxHypothesis) {
		this();
		if (USE_FEATURE_SELECTION) {
			FeatureSelector fs = new FeatureSelector(gs);
			Utils.debugln("The number of hypotheses is: "
					+ fs.hypotheses.numberOfHypotheses);
			int maxHypDensity = 0;
			int maxHypIndex = 0;
			for (int i = 0; i < fs.hypotheses.numberOfHypotheses; ++i) {
				if (fs.hypotheses.get(i).hypothesisDensitySummation > maxHypDensity) {
					maxHypDensity = fs.hypotheses.get(i).hypothesisDensitySummation;
					maxHypIndex = i;
				}
			}
			this.learn(gs, fs.hypotheses.get(maxHypIndex));
		} else {
			this.learn(gs);
		}
	}

	/**
	 * Learn DNFs given a set of features
	 * 
	 * @param gs
	 * @param features
	 */
	public LearnDNF(IDataSequence gs, Vector<Integer> features) {
		this();
		this.learn(gs, features);
	}

	/**
	 * Learn DNFs given a positional hypothesis
	 * 
	 * @param gs
	 * @param positionalHypothesis
	 */
	public LearnDNF(IDataSequence gs, PositionalHypothesis positionalHypothesis) {
		this(gs, positionalHypothesis.hypothesis);
	}

	/**
	 * Learn DNFs given a set of positional hypotheses
	 * 
	 * @param gs
	 * @param phs
	 */
	public LearnDNF(IDataSequence gs, PositionalHypotheses phs) {
		this();
		for (int i = 0; i < phs.numberOfHypotheses; ++i) {
			this.learn(gs, phs.get(i));
		}
	}

	/**
	 * 
	 * @return
	 */
	public DNFList getDNFs() {
		return dnfList;
	}

	/**
	 * 
	 * @param gs
	 */
	public void learn(IDataSequence gs) {
		LearnLiteralsFromGS clgs = new LearnLiteralsFromGS(gs);

		if (IS_VERBOSE) {
			Utils.debugln(clgs.ToString());
			Utils.debugln(clgs.uniqifier.ToString());
		}

		LearnCNFClauses ccc = new LearnCNFClauses(clgs);
		if (IS_VERBOSE) {
			Utils.debugln(ccc.ToString());
			Utils.debugln(ccc.uniqifier.ToString());
		}

		if (USE_GREEDY) {
			this.learnGreedily(ccc, gs);
		} else {
			this.learnCombinatorially(ccc);
		}
		if (IS_VERBOSE) {
			Utils.debugln(this.ToString());
		}
	}

	/**
	 * 
	 * @param gs
	 * @param features
	 */
	public void learn(IDataSequence gs, Vector<Integer> features) {
		LearnLiteralsFromGS clgs = new LearnLiteralsFromGS(gs, features);

		if (IS_VERBOSE) {
			Utils.debugln(clgs.ToString());
			Utils.debugln(clgs.uniqifier.ToString());
		}

		LearnCNFClauses ccc = new LearnCNFClauses(clgs);
		if (IS_VERBOSE) {
			Utils.debugln(ccc.ToString());
			Utils.debugln(ccc.uniqifier.ToString());
		}

		if (USE_GREEDY) {
			this.learnGreedily(ccc, gs);
		} else {
			this.learnCombinatorially(ccc);
		}

		if (IS_VERBOSE) {
			Utils.debugln(this.ToString());
		}
	}

	/**
	 * 
	 * @param gs
	 * @param ph
	 */
	public void learn(IDataSequence gs, PositionalHypothesis ph) {
		this.learn(gs, ph.hypothesis);
	}

	/**
	 * 
	 * @param orgCNFClauseList
	 * @param maxNumberOfDNFTerms
	 */
	public void learnCombinatorially(LearnCNFClauses ccc,
			int maxNumberOfDNFTerms) {
		int total = 0;
		// this.dnfList.clear();
		// Check if there's answer
		ITerm tempDNF = new DNF();
		for (int i = 0; i < ccc.activeCNFClauseList.size(); ++i) {
			tempDNF.add(ccc.activeCNFClauseList.get(i));
		}
		if (tempDNF.isFull()) {
			System.out.println("Solution(s) exist; start learning....");
		} else {
			System.out.println("No exhaustive solution; stop the program.");
			return;
		}

		// Learn
		for (int n = 0; n < maxNumberOfDNFTerms
				&& n < ccc.activeCNFClauseList.size(); ++n) {
			for (Collection<ITerm> c : new CombinatorialIterator<ITerm>(n + 1,
					ccc.activeCNFClauseList)) {
				total++;
				if (total % 1e5 == 0) {
					Utils.debugln("Examined DNF " + total);
				}

				DNF cc = new DNF(c);

				if (IS_FAST_VERSION) {
					if (cc.isCoveringAllOnlyPositive()) {
						this.dnfList.add(cc);
					}
				} else {
					if (cc.isCoveringAllOnlyPositive()) {
						this.dnfList.add(cc);
					}
					double posCov = cc.getPositiveCoverageSummation();
					double negCov = cc.getNegativeCoverageSummation();
					double posNum = (double) AbstractTerm.SPACE;
					double negNum = (double) AbstractTerm.TOTAL_SPACE - posNum;
					double score = ((double) posCov / posNum)
						* GREEDY_WEIGHT
						+ ((double) negCov / negNum)
						* (1 - GREEDY_WEIGHT);
					//System.err.println("score is: " + score);
					if (score > 0.7 - n * 0.02) {
						this.dnfList.add(cc);
						return;
					}                                       
				}
			}
			if (this.dnfList.getNumberOfDNFs() > 0) {
				if (USE_GENERALIZATION) {
					for (int i = 0; i < this.dnfList.getNumberOfDNFs(); ++i) {
						this.dnfList.add(DNFFunctions.generalizeDNF(
								dnfList.get(i), ccc.uniqifier));
					}
				}
				return;
			}
		}
	}

	/**
	 * 
	 * @param orgCNFClauseList
	 */
	public void learnCombinatorially(LearnCNFClauses ccc) {
		int maxNumberOfDNFTerms = ccc.activeCNFClauseList.size();
		this.learnCombinatorially(ccc, maxNumberOfDNFTerms);
	}

	/**
	 * 
	 * @param cnfClauses
	 */
	public void learnGreedily(LearnCNFClauses ccc, IDataSequence gs) {
		int size = ccc.activeCNFClauseList.size();
		if (IS_VERBOSE) {
			Utils.debugln("========= Greedily learning DNFs ==========");
			Utils.debugln("The number of candidate features is: " + size);
			Utils.debugln("The number of greedy maximum features is : " + size);
		}
		// this.dnfList.clear();
		// check total coverage
		ITerm tempDNF = new DNF();
		for (int i = 0; i < ccc.activeCNFClauseList.size(); ++i) {
			tempDNF.add(ccc.activeCNFClauseList.get(i));
		}
		Utils.debugln(">>> The maximum pos coverage is: " + tempDNF.getPositiveCoverageSummation());
		
		// snapshot
		Vector<Double> incrementsScoreSnapShotPositive = new Vector<Double>();
		
		// start
		Vector<Double> incrementsScoreSnapShotNegative = new Vector<Double>();
		DNF dnf = new DNF();
		CNF newM = new CNF();
		double newCutOff = COVERAGE_CUTOFF * AbstractTerm.SPACE;

		int t = 0;
		//while (!newM.isFull() && t < size) {
		while (newM.getPositiveCoverageSummation() < newCutOff && t < size) {
			int nextI = this.greedilySelectNextI(newM, ccc.activeCNFClauseList,
					gs);
			if (nextI >= 0) {
				if (IS_VERBOSE) {
					Utils.debugln("Greedily Selected: " + nextI);
				}
				newM.OR(ccc.activeCNFClauseList.get(nextI));
				incrementsScoreSnapShotPositive.add((double) newM
						.getPositiveCoverageSummation()
						/ (double) (AbstractTerm.SPACE));
				incrementsScoreSnapShotNegative
						.add((double) newM.getNegativeCoverageSummation()
								/ (double) (AbstractTerm.TOTAL_SPACE - AbstractTerm.SPACE));
				t++;
				dnf.add(ccc.activeCNFClauseList.get(nextI));
			} else {
				break;
			}
		}

		this.dnfList.add(dnf);
		if (IS_VERBOSE) {
			Utils.debug("Positive Coverage Snapshot: ");
			for (int i = 0; i < incrementsScoreSnapShotPositive.size(); ++i) {
				Utils.debug(incrementsScoreSnapShotPositive.get(i) + " ");
			}
			Utils.debugln();
			Utils.debug("Negative Coverage Snapshot: ");
			for (int i = 0; i < incrementsScoreSnapShotNegative.size(); ++i) {
				Utils.debug(incrementsScoreSnapShotNegative.get(i) + " ");
			}
			Utils.debugln();
			Utils.debugln(this.ToString());
		}
		if (USE_GENERALIZATION) {
			this.dnfList.add(DNFFunctions.generalizeDNF(dnf, ccc.uniqifier));
		}
	}

	/**
	 * 
	 * @param curCNFClause
	 * @param mbm
	 * @return
	 */
	private int greedilySelectNextI(CNF curCNFClause, Vector<CNF> mbm,
			IDataSequence gs) {
		int nextI = -1;
		int size = mbm.size();
		double maxScore = Double.MIN_VALUE;
		double posCov = curCNFClause.getPositiveCoverageSummation();
		double negCov = curCNFClause.getNegativeCoverageSummation();
		double posNum = (double) gs.getSequenceNumbers(0);
		double negNum = (double) gs.getSequenceNumbers(1);
		for (int i = 0; i < size; ++i) {
			CNF tempM = new CNF(curCNFClause);
			tempM.OR(mbm.get(i));
			// double score = tempM.getCoverageSummation() -
			// curCNFClause.getCoverageSummation();
			double score = 0;
			if (USE_GREEDY_WEIGHT) {
				score = ((double) tempM.getPositiveCoverageSummation() / posNum)
						* GREEDY_WEIGHT
						- ((double) tempM.getNegativeCoverageSummation() / negNum)
						* (1 - GREEDY_WEIGHT)
						- (posCov / posNum)
						* GREEDY_WEIGHT
						+ (negCov / negNum)
						* (1 - GREEDY_WEIGHT);
			} else {
				score = ((double) tempM.getPositiveCoverageSummation()) / posNum
						- ((double) tempM.getNegativeCoverageSummation()) / negNum;
						// - posCov;
						// + negCov;
			}

			if (score > maxScore + 1E-3) {
				maxScore = score;
				nextI = i;
			}
		}
		return nextI;
	}

	/**
	 * 
	 * @return
	 */
	public String ToString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("The number of DNF is: ");
		strBuilder.append(Integer.toString(this.dnfList.getNumberOfDNFs())
				+ "\n");

		for (int i = 0; i < this.dnfList.getNumberOfDNFs(); ++i) {
			strBuilder.append(this.dnfList.get(i).ToString());
			if (AbstractTerm.IS_VERBOSE) {
				strBuilder.append(" ");
				strBuilder.append(this.dnfList.get(i).printCoverage());
			}
			strBuilder.append("\n");
		}
		return strBuilder.toString();
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String dataNameA = ""; // "SIV_ENV";
		String dataNameB = "";
		if (args.length > 1) {
			dataNameB = args[1];
		}
		if (args.length > 0) {
			dataNameA = args[0];
		}

		// dataSet = "/Users/Chuang/Work/Research/Data/HIV_Stanford";
		// dataName = "EFV";

		// GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
		// gs.sequenceItems = Sequence.PROTEIN_ITERMS;
		IDataSequence gs = new TwoClassesSequences(dataNameA, dataNameB);
		Utils.debugln("Total # is: " + gs.getTotalNumber());
		Utils.debugln("# of features is: " + gs.getSequenceLength());
		Utils.debugln("Pos # is: " + gs.getSequenceNumbers(0));
		Utils.debugln("Neg # is: " + gs.getSequenceNumbers(1));

		AbstractTerm.IS_FAST_VERSION = false;
		AbstractTerm.IS_VERBOSE = true;
		AbstractTerm.USE_NEGATION = true;
		AbstractTerm.USE_AMBIGUOUS_CHAR = false;

		LearnLiteralsFromGS.IS_FAST_VERSION = false;
		LearnLiteralsFromGS.USE_UNIQIFY = true;

		LearnCNFClauses.IS_FAST_VERSION = true;

		FeatureSelector.IS_VERBOSE = true;
		FeatureSelector.USE_GREEDY = true;

		LearnDNF.IS_VERBOSE = true;
		LearnDNF.IS_FAST_VERSION = true;
		LearnDNF.USE_GENERALIZATION = false;
		LearnDNF.USE_GREEDY = true;
		LearnDNF.COVERAGE_CUTOFF = 1.0;
		LearnDNF.DENSITY_CUTOFF = 0;

		ILearnDNF cd = new LearnDNF();

		Vector<Integer> keyResidues = new Vector<Integer>();
		boolean O_N1 = false;
		boolean Z_N2 = false;
		boolean H5N2 = false;
		if (O_N1) {
			keyResidues.clear();
			keyResidues.add(115);
			keyResidues.add(274);
			keyResidues.add(125);
			// keyResidues.add(78);
			keyResidues.add(246);
			keyResidues.add(116);
			keyResidues.add(313);
			keyResidues.add(125);
			keyResidues.add(234);
		} else if (Z_N2) {
			keyResidues.clear();
			keyResidues.add(118);
			keyResidues.add(151);
			keyResidues.add(291);
			keyResidues.add(223);
			keyResidues.add(370);
			//
			keyResidues.add(41);
			keyResidues.add(142);
			keyResidues.add(386);
			keyResidues.add(438);
		} else if (H5N2) {
			keyResidues.add(275);
			keyResidues.add(325);
			keyResidues.add(323);
			keyResidues.add(324);
		}

		FeatureSelector fs = new FeatureSelector(gs);
		// LearnDNF.COVERAGE_CUTOFF = fs.getMaxCoverage();
		System.out.println("After feature selection: "
				+ LearnDNF.COVERAGE_CUTOFF);
		cd = new LearnDNF(gs, fs.hypotheses);
		Utils.debugln(cd.ToString());

		for (DNF d : cd.getDNFs().getDNFs()) {
			Utils.debugln(d.qualityPerformance(gs)[0] + " "
					+ d.qualityPerformance(gs)[1]);
		}

		for (DNF d : cd.getDNFs().getDNFs()) {
			DNF newD = DNFFunctions.mergeITermWithTheSamePosition(d);
			// Utils.debug("DNF: ");
			Utils.debugln(newD.ToString());
			Utils.debugln(newD.qualityPerformance(gs)[0] + " "
					+ newD.qualityPerformance(gs)[1]);
			// Utils.debugln("END");
		}

		/*
		 * String dataNameTest = "SPECT.test"; GenotypeSequences gsTest = new
		 * GenotypeSequences(dataSet, dataNameTest);
		 * 
		 * System.out.println("The # of pos: " + gsTest.sequenceNumbers[0]);
		 * System.out.println("The # of neg: " + gsTest.sequenceNumbers[1]); for
		 * (DNF d : cd.getDNFs().getDNFs()) {
		 * Utils.debugln(d.qualityPerformance(gsTest)[0] + " " +
		 * d.qualityPerformance(gsTest)[1]); }
		 */
	}
}
