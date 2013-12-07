package edu.cmu.cs.JavaDNF.lib;

import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.compbio.lib.MultipleSequenceAlignment;
import edu.cmu.cs.compbio.lib.TwoClassesSequences;

import java.util.Vector;

/**
 *
 * @author cc
 */
public class TrainTestDataClass {

    /** */
    private IDataSequence trainGenotypeSequences;
    private IDataSequence testGenotypeSequences;
    private MultipleSequenceAlignment testSequences;
    private Vector<Boolean> testGroundTruth;

    /**
     * 
     */
    public TrainTestDataClass() {
        trainGenotypeSequences = new TwoClassesSequences();
        testGenotypeSequences = new TwoClassesSequences();
        testSequences = new MultipleSequenceAlignment();
        testGroundTruth = new Vector<Boolean>();
    }

    /**
     * 
     * @return
     */
    public IDataSequence getTrainGenotypeSequences() {
        return this.trainGenotypeSequences;
    }

    /**
     * 
     * @return
     */
    public IDataSequence getTestGenotypeSequences() {
        return this.testGenotypeSequences;
    }

    /**
     * 
     * @return
     */
    public MultipleSequenceAlignment getTestSequences() {
        return this.testSequences;
    }

    /**
     * 
     * @return
     */
    public Vector<Boolean> getTestGroundTruth() {
        return this.testGroundTruth;
    }

    /**
     * 
     * @param gs
     */
    public void setTrainGenotypeSequences(IDataSequence gs) {
        this.trainGenotypeSequences = gs;
    }

    /**
     * 
     * @param gs
     */
    public void setTestGenotypeSequences(IDataSequence gs) {
        this.testGenotypeSequences = gs;
    }

    /**
     * 
     * @param msa
     */
    public void setTestSequences(MultipleSequenceAlignment msa) {
        this.testSequences = msa;
    }

    /**
     * 
     * @param groundTruth
     */
    public void setTestGroundTruth(Vector<Boolean> groundTruth) {
        this.testGroundTruth = groundTruth;
    }

    /**
     * 
     * @return
     */
    public String ToString() {
        StringBuilder s = new StringBuilder();
        s.append("The training datasets: \n");
        s.append(trainGenotypeSequences.ToString());
        s.append("The test datasets: \n");
        s.append(testGenotypeSequences.ToString());

        return s.toString();
    }
}
