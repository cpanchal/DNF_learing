package edu.cmu.cs.JavaDNF.performance;

import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.JavaDNF.lib.Utils;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.compbio.lib.MultipleSequenceAlignment;
import java.util.Vector;

/**
 *
 * @author cc
 */
public class LeaveOneOutIterator extends ACrossValidationIterator {

    /**
     *
     * @param gs
     */
    public LeaveOneOutIterator(IDataSequence gs) {
        super(gs, gs.getTotalNumber());
    }

    /**
     *
     * @return
     */
    //@SuppressWarnings("unchecked")
    protected TrainTestDataClass createFromIndex() {
        TrainTestDataClass results = new TrainTestDataClass();
        MultipleSequenceAlignment testSequences = new MultipleSequenceAlignment();
        Vector<Boolean> testGroundTruth = new Vector<Boolean>();
        GenotypeSequences trainGenotypeSequences = new GenotypeSequences();

        if (foldIndex < gs.getSequenceNumbers(0)) {
            testGroundTruth.add(true);
            testSequences.add(gs.get(0, foldIndex));
        } else {
            testGroundTruth.add(false);
            testSequences.add(gs.get(1, foldIndex - gs.getSequenceNumbers(0)));
        }
        
        // create trainGenotypeSequences
        for (int j = 0; j < gs.getTotalNumber(); ++j) {
            if (j != foldIndex) {
                if (j < gs.getSequenceNumbers(0)) {
                    trainGenotypeSequences.add(0, gs.get(0, j));
                } else {
                    trainGenotypeSequences.add(1, gs.get(1, j - gs.getSequenceNumbers(0)));
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
        String dataName = ""; //"SIV_ENV";
        String dataSet = "";
        if (args.length > 1) {
            dataName = args[1];
        }
        if (args.length > 0) {
            dataSet = args[0];
        }
        dataSet = "/home/cc/Work/Research/Data/Test";
        dataName = "test";

        GenotypeSequences.IS_VERBOSE = false;

        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);

        LeaveOneOutIterator lood = new LeaveOneOutIterator(gs);
        while (lood.hasNext()) {
            Utils.debugln(lood.next().ToString());
        }
    }
}
