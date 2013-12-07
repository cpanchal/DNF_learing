/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.lib;

import edu.cmu.cs.compbio.lib.Sequence;
import java.util.Vector;

/**
 *
 * @author cc
 */
public class DNFList {

    /** */
    private Vector<DNF> dnfs;
    /** */
    private int numberOfDNFs;

    /**
     * 
     */
    public DNFList() {
        this.dnfs = new Vector<DNF>();
        this.numberOfDNFs = 0;
    }

    /**
     * 
     * @return
     */
    public Vector<DNF> getDNFs() {
        return this.dnfs;
    }

    /**
     * 
     * @return
     */
    public int getNumberOfDNFs() {
        return this.numberOfDNFs;
    }

    /**
     * 
     * @param dnf
     */
    public void add(DNF dnf) {
        this.dnfs.add(dnf);
        this.numberOfDNFs++;
    }

    /**
     * 
     * @param i
     * @return
     */
    public DNF get(int i) {
        if (i < 0 || i >= this.numberOfDNFs) {
            throw new IllegalArgumentException("0 <= i < this.numberOfDNFs");
        }
        return this.dnfs.get(i);
    }

    /**
     * 
     */
    public void clear() {
        this.dnfs.clear();
        this.numberOfDNFs = 0;
    }

    /**
     * 
     * @param sequence
     * @return
     */
    public double predict(Sequence sequence) {
        int numberPositivePrediction = 0;
        for (int i = 0; i < numberOfDNFs; ++i) {
            if (this.dnfs.get(i).isConsistent(sequence)) {
                numberPositivePrediction++;
            }
        }
        return (double) numberPositivePrediction / (double) numberOfDNFs;
    }

    /**
     * 
     * @return
     */
    public String ToString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.numberOfDNFs; ++i) {
            s.append(this.dnfs.get(i).ToString() + "\n");
        }
        return s.toString();
    }
}
