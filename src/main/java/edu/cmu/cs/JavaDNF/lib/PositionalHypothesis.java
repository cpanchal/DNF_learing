/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.JavaDNF.lib;

import java.util.Vector;

/**
 *
 * @author cc
 */
public class PositionalHypothesis {
    /** */
    public int hypothesisSize;
    /** */
    public Vector<Integer> hypothesis;
    /** */
    public int hypothesisDensitySummation;

    /**
     * 
     */
    public PositionalHypothesis() {
        hypothesisSize = 0;
        hypothesis = new Vector<Integer>();
        hypothesisDensitySummation = 0;
    }

    /**
     * 
     * @param position
     */
    public void add(int position) {
        this.hypothesis.add(position);
        this.hypothesisSize++;
    }

    /**
     * 
     */
    public void clear() {
        hypothesisSize = 0;
        hypothesis.clear();
        hypothesisDensitySummation = 0;
    }

    /**
     * 
     * @return
     */
    public String ToString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < hypothesisSize; ++i) {
            s.append(hypothesis.get(i) + " ");
        }
        return s.toString();
    }
}
