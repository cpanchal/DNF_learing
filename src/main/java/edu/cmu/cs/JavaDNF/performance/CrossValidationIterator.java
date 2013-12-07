package edu.cmu.cs.JavaDNF.performance;

import edu.cmu.cs.Algorithm.Shuffle;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.compbio.lib.MultipleSequenceAlignment;
import edu.cmu.cs.compbio.lib.Sequence;
import edu.cmu.cs.compbio.lib.TwoClassesSequences;
import edu.cmu.cs.JavaDNF.lib.Utils;
import java.util.Vector;

/**
 *
 * @author cc
 */
public class CrossValidationIterator extends ACrossValidationIterator {

    /** */
    private int[][][] crossValidationIndices;

    /**
     * 
     * @param gs
     * @param foldNumber
     */
    public CrossValidationIterator(IDataSequence gs, int foldNumber) {
        super(gs, foldNumber);
        this.shuffleIndices();
    }

    private void shuffleIndices() {
        Shuffle<Integer> sf = new Shuffle<Integer>();
        Integer[][] shuffledIndices = new Integer[2][];
        shuffledIndices[0] = new Integer[gs.getSequenceNumbers(0)];
        shuffledIndices[1] = new Integer[gs.getSequenceNumbers(1)];
        for (int i = 0; i < shuffledIndices[0].length; ++i) {
            shuffledIndices[0][i] = new Integer(i);
        }
        for (int i = 0; i < shuffledIndices[1].length; ++i) {
            shuffledIndices[1][i] = new Integer(i);
        }

        sf.shuffleArray(shuffledIndices[0]);
        sf.shuffleArray(shuffledIndices[1]);
        crossValidationIndices = new int[totalFoldNumber][IDataSequence.PHENOTYPES.length][];

        for (int fold = 0; fold < totalFoldNumber; ++fold) {
            for (int cI = 0; cI < IDataSequence.PHENOTYPES.length; ++cI) {
                int lowerBound = fold * gs.getSequenceNumbers(cI) / totalFoldNumber;
                int upperBound = (fold + 1) * gs.getSequenceNumbers(cI) / totalFoldNumber;
                crossValidationIndices[fold][cI] = new int[upperBound - lowerBound];
                for (int i = 0; i < crossValidationIndices[fold][cI].length; ++i) {
                    crossValidationIndices[fold][cI][i] = shuffledIndices[cI][lowerBound + i];
                }
            }
        }
    }

    /**
     * 
     * @return
     */
    protected TrainTestDataClass createFromIndex() {
        TrainTestDataClass results = new TrainTestDataClass();
        MultipleSequenceAlignment testSequences = new MultipleSequenceAlignment();
        Vector<Boolean> testGroundTruth = new Vector<Boolean>();
        IDataSequence trainGenotypeSequences = new TwoClassesSequences();

        for (int k = 0; k < IDataSequence.PHENOTYPES.length; ++k) {
            for (int i = 0; i < crossValidationIndices[foldIndex][k].length; ++i) {
                testGroundTruth.add(k == 0);
                testSequences.add(gs.get(k, crossValidationIndices[foldIndex][k][i]));
            }
        }

        // create trainGenotypeSequences
        for (int fold = 0; fold < totalFoldNumber; ++fold) {
            if (fold != foldIndex) {
                for (int k = 0; k < IDataSequence.PHENOTYPES.length; ++k) {
                    for (int i = 0; i < crossValidationIndices[fold][k].length; ++i) {
                        trainGenotypeSequences.add(k, gs.get(k, crossValidationIndices[fold][k][i]));
                    }
                }
            }
        }
        trainGenotypeSequences.generateNonUniquePositions();

        results.setTrainGenotypeSequences(trainGenotypeSequences);
        results.setTestSequences(testSequences);
        results.setTestGroundTruth(testGroundTruth);

        return results;
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
        dataSet = "/home/cc/Work/Research/Data/Test";
        dataName = "test2";

        //IDataSequence.IS_VERBOSE = false;
        //IDataSequence.IS_PRINGINT_TITLE = true;
        Sequence.IS_VERBOSE = true;

        IDataSequence gs = new TwoClassesSequences(dataSet, dataName);

        CrossValidationIterator cvi = new CrossValidationIterator(gs, 5);
        while (cvi.hasNext()) {
            Utils.debugln("\n================Fold: ");
            Utils.debugln(cvi.next().ToString());
        }
    }
}
