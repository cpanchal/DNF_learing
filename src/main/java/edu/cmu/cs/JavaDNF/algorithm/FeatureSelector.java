package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.Algorithm.CombinatorialIterator;
import edu.cmu.cs.JavaDNF.interfaces.IBooleanMatrix;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.JavaDNF.lib.BooleanMatrix;
import edu.cmu.cs.JavaDNF.lib.PositionalHypothesis;
import edu.cmu.cs.JavaDNF.lib.PositionalHypotheses;
import java.util.Vector;
import java.util.Collection;
import edu.cmu.cs.JavaDNF.lib.Utils;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.compbio.lib.TwoClassesSequences;

/**
 *
 * @author cc
 */
public class FeatureSelector {

    /** */
    public static double PERCENTAGE = 1;
    /** */
    public static boolean IS_VERBOSE = false;
    /** */
    public static boolean USE_GREEDY = true;
    /** */
    public static int MAX_NUMBER_OF_FEATURES = 25;


    /** */
    public PositionalHypotheses hypotheses;

    /** */
    private createBooleanMatrixFromGS cbgs;

    private Vector<Double> incrementsScoreSnapShot;
    
    /**
     *
     */
    public FeatureSelector() {
        hypotheses = new PositionalHypotheses();
    }

    /**
     *
     * @param cbgs
     */
    public FeatureSelector(createBooleanMatrixFromGS cbgs) {
        this();
        this.cbgs = cbgs;
        if (USE_GREEDY) {
            this.selectFeaturesGreedily(cbgs.getNonUniquePositions().size());
        } else {
            this.selectFeaturesCombinatorially();
        }
    }

    /**
     * 
     * @param gs
     */
    public FeatureSelector(GenotypeSequences gs) {
        this();
        this.cbgs = new createBooleanMatrixFromGS(gs);
        System.out.println(cbgs.ToString());
        if (USE_GREEDY) {
            this.selectFeaturesGreedily(cbgs.getNonUniquePositions().size());
        } else {
            this.selectFeaturesCombinatorially();
        }
    }
    
    /**
     * 
     * @param gs
     */
    public FeatureSelector(IDataSequence gs) {
        this();
        this.cbgs = new createBooleanMatrixFromGS(gs);
        System.out.println(cbgs.ToString());
        if (USE_GREEDY) {
            this.selectFeaturesGreedily(cbgs.getNonUniquePositions().size());
        } else {
            this.selectFeaturesCombinatorially();
        }
    }

    /**
     *
     */
    public void selectFeaturesCombinatorially() {
        Vector<IBooleanMatrix> mbm = this.cbgs.getBooleanMatrix();
        int mbmSize = mbm.size();
        if (mbmSize == 0) {
            throw new IllegalArgumentException("Empty mbm");
        }

        if (IS_VERBOSE) {
            Utils.debugln("========= Heuristically selecting features ==========");
            Utils.debugln("The number of candidate features is: " + mbmSize);
            Utils.debugln("The percentage is : " + PERCENTAGE);
        }
        hypotheses.clear();

        int m = mbm.get(0).getRowDimension();
        int n = mbm.get(0).getColumnDimension();
        double cutoff = m * n * PERCENTAGE;

        int numberOfFeatures = 0;
        int iteration = 0;
        Vector<Integer> indices = new Vector<Integer>(mbmSize);
        for (int i = 0; i < mbmSize; ++i) {
            indices.add(i);
        }

        int maxIteration = Math.min(MAX_NUMBER_OF_FEATURES, mbmSize);

        while (numberOfFeatures < maxIteration) {

            for (Collection<Integer> c :
                    new CombinatorialIterator<Integer>(numberOfFeatures + 1, indices)) {
                iteration++;
                if (IS_VERBOSE) {
                    if (iteration % 1e6 == 0) {
                        Utils.debugln("Iteration: " + iteration);
                    }
                }

                Vector<Integer> curIndices = new Vector<Integer>(c);
                IBooleanMatrix mat = new BooleanMatrix(m, n);
                for (int i = 0; i < curIndices.size(); ++i) {
                    mat.OR(mbm.get(curIndices.get(i)));
                }
                
                if (mat.getSummation() >= cutoff) {
                    PositionalHypothesis ph = new PositionalHypothesis();
                    for (int i = 0; i < curIndices.size(); ++i) {
                        ph.add(cbgs.getNonUniquePositions().get(curIndices.get(i)));
                    }
                    hypotheses.add(ph);
                }
            }
            if (hypotheses.numberOfHypotheses > 0) {
                break;
            }
            numberOfFeatures++;
        }
        
        hypotheses.generateDensityMap();
        if (IS_VERBOSE) {
            Utils.debugln(this.ToString());
        }
    }

    /**
     * 
     * @param mbm
     * @param maxNumberOfFeatures
     */
    public void selectFeaturesGreedily(int maxNumberOfFeatures) {
        Vector<IBooleanMatrix> allMyBooleanMatrixs = this.cbgs.getBooleanMatrix();
        if (maxNumberOfFeatures < 0 || maxNumberOfFeatures > allMyBooleanMatrixs.size()) {
            throw new IllegalArgumentException("0 <= k < mbm.size()");
        }
        if (IS_VERBOSE) {
            Utils.debugln("========= Greedily selecting features ==========");
            Utils.debugln("The number of candidate features is: " + allMyBooleanMatrixs.size());
            Utils.debugln("The number of greedy maximum features is : " + maxNumberOfFeatures);
        }
        this.hypotheses.clear();

        incrementsScoreSnapShot = new Vector<Double>();
        //double[] incrementsScoreSnapShot = new double[maxNumberOfFeatures];
        PositionalHypothesis positionalHypothesis = new PositionalHypothesis();

        IBooleanMatrix newBooleanMatrix = new BooleanMatrix(
                allMyBooleanMatrixs.get(0).getRowDimension(), allMyBooleanMatrixs.get(0).getColumnDimension());

        int numberOfFeatures = 0;
        while (!newBooleanMatrix.isFull() && numberOfFeatures < maxNumberOfFeatures) {

            int nextI = this.greedilySelectNextI(newBooleanMatrix, allMyBooleanMatrixs);
            if (nextI >= 0) {
                newBooleanMatrix.OR(allMyBooleanMatrixs.get(nextI));
                incrementsScoreSnapShot.add((double) newBooleanMatrix.getSummation() / (double) (newBooleanMatrix.getRowDimension() * newBooleanMatrix.getColumnDimension()));
                numberOfFeatures++;
                positionalHypothesis.add(cbgs.getNonUniquePositions().get(nextI));
            } else {
                break;
            }
        }

        this.hypotheses.add(positionalHypothesis);
        this.hypotheses.generateDensityMap();
        if (IS_VERBOSE) {
            for (int i = 0; i < incrementsScoreSnapShot.size(); ++i) {
                Utils.debug(incrementsScoreSnapShot.get(i) + " ");
            }
            Utils.debugln();
            Utils.debugln(this.ToString());
        }
    }

    /**
     * 
     * @param currentMyBooleanMatrix
     * @param mbm
     * @return
     */
    public int greedilySelectNextI(IBooleanMatrix currentMyBooleanMatrix, Vector<IBooleanMatrix> mbm) {
        int nextI = -1;
        int size = mbm.size();
        double maxScore = 0;
        for (int i = 0; i < size; ++i) {
            IBooleanMatrix tempM = new BooleanMatrix(currentMyBooleanMatrix);
            tempM.OR(mbm.get(i));
            double score = tempM.getSummation() - currentMyBooleanMatrix.getSummation();
            if (score > maxScore) {
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
    public double getMaxCoverage() {
        return incrementsScoreSnapShot.lastElement();
    }

    /**
     * 
     * @return
     */
    public String ToString() {
        return this.hypotheses.ToString();
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
    	
    	String dataNameA = ""; //"SIV_ENV";
        String dataNameB = "";
        if (args.length > 1) {
            dataNameB = args[1];
        }
        if (args.length > 0) {
            dataNameA = args[0];
        }

        //dataSet = "/Users/Chuang/Work/Research/Data/HIV_Stanford";
        //dataName = "EFV";

        //GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
        //gs.sequenceItems = Sequence.PROTEIN_ITERMS;
        IDataSequence gs = new TwoClassesSequences(dataNameA, dataNameB);

        //String dataSet = "/Users/Chuang/Work/Research/Data/HIV_Stanford";
        //String dataName = "APV_New";

        AbstractTerm.IS_FAST_VERSION = false;
        AbstractTerm.IS_VERBOSE = true;
        
        LearnLiteralsFromGS.IS_FAST_VERSION = false;
        
        LearnCNFClauses.IS_FAST_VERSION = true;

        LearnDNF.IS_FAST_VERSION = true;
        LearnDNF.IS_VERBOSE = true;

        FeatureSelector.IS_VERBOSE = true;
        FeatureSelector.USE_GREEDY = false;

        //GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);

        System.out.println("The # of pos is: " + gs.getSequenceNumbers(0));
        System.out.println("The # of neg is: " + gs.getSequenceNumbers(1));
        System.out.println("The # of seqLength is: " + gs.getSequenceLength());
        System.out.println("The # of nonUnique position is: " + gs.getNonUniquePositions().size());


        FeatureSelector fs = new FeatureSelector(gs);
        //System.out.println("The max of coverage is: " + fs.getMaxCoverage());

    }
}
