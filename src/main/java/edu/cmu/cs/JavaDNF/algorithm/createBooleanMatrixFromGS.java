package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.interfaces.IBooleanMatrix;
import java.util.Vector;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.JavaDNF.lib.BooleanMatrix;
import edu.cmu.cs.JavaDNF.lib.Utils;

/**
 *
 * @author cc
 */
public class createBooleanMatrixFromGS {

    /** */
    public static double PERCENTAGE;

    /** */
    private Vector<IBooleanMatrix> mat;
    /** */
    private Vector<Integer> nonUniquePositions;
    /** */
    private IDataSequence gs;
    /** */
    private double coverage;

    /**
     * 
     */
    public createBooleanMatrixFromGS() {
        mat = new Vector<IBooleanMatrix>();
        nonUniquePositions = new Vector<Integer>();
    }

    /**
     * 
     * @param gs
     */
    public createBooleanMatrixFromGS(GenotypeSequences gs) {
        this();
        this.gs = gs;
        this.createCompeletely();
    }
    
    /**
     * 
     * @param gs
     */
    public createBooleanMatrixFromGS(IDataSequence gs) {
        this();
        this.gs = gs;
        this.createCompeletely();
    }

    /**
     * 
     */
    public void createCompeletely() {
        this.createHeuristically(0);
    }

    /**
     * 
     * @param PERCENTAGE
     */
    public void createHeuristically(double percentage) {
        int m = gs.getSequenceNumbers(0);
        int n = gs.getSequenceNumbers(1);
        int size = gs.getNonUniquePositions().size();
        double cutoff = m * n * percentage;
        mat.clear();
        for (int i = 0; i < size; ++i) {
            IBooleanMatrix curM = new BooleanMatrix(m, n);

            for (int j = 0; j < m; ++j) {
                for (int k = 0; k < n; ++k) {
                    curM.set(j, k, gs.get(0, j).get(gs.getNonUniquePositions().get(i)) != gs.get(1, k).get(gs.getNonUniquePositions().get(i)));
                }
            }
            if (curM.getSummation() >= cutoff) {
                mat.add(curM);
                this.nonUniquePositions.add(gs.getNonUniquePositions().get(i));
            }
        }
        this.calculateCoverage();
    }

    /**
     * 
     */
    private void calculateCoverage() {
        IBooleanMatrix tempM = new BooleanMatrix(gs.getSequenceNumbers(0), gs.getSequenceNumbers(1));
        for (int i = 0; i < this.mat.size(); ++i) {
            tempM.OR(mat.get(i));
        }
        this.coverage = (double) tempM.getSummation() / (double) (tempM.getRowDimension() * tempM.getColumnDimension());
    }

    /**
     *
     * @return
     */
    public Vector<IBooleanMatrix> getBooleanMatrix() {
        return this.mat;
    }

    /**
     * 
     * @return
     */
    public Vector<Integer> getNonUniquePositions() {
        return this.nonUniquePositions;
    }

    /**
     * 
     * @return
     */
    public double getCoverage() {
        return this.coverage;
    }

    /**
     * 
     * @return
     */
    public String ToString() {
        StringBuilder s = new StringBuilder();
        s.append("The size of nonUniquePositions is: " + this.nonUniquePositions.size() + "\n");
        for (int i = 0; i < this.nonUniquePositions.size(); ++i) {
            s.append(this.nonUniquePositions.get(i) + " ");
        }
        s.append("\n");
        /*
        s.append("The boolean matrices are: \n");
        for (int i = 0; i < this.nonUniquePositions.size(); ++i) {
            s.append(this.mat.get(i).toString() + "\n");
        }
         * 
         */
        s.append("The coverage is: " + this.coverage);
        return s.toString();
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        String dataSet = "/home/cc/Work/Research/Data/HIV_Stanford";
        String dataName = "APV_New";

        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
        System.out.println(gs.ToString());
        createBooleanMatrixFromGS cbmgs = new createBooleanMatrixFromGS(gs);
        Utils.debugln(cbmgs.toString());
    }
}
