/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.lib;

import java.util.Vector;

/**
 *
 * @author Chuang
 */
public class TargetFunction {

    public AbstractTerm function;
    /** */
    public Vector<Integer> positions = null;
    /** */
    public Vector<Character> sequenceItems = null;

    /**
     *
     */
    public TargetFunction() {
        positions = new Vector<Integer>();
        sequenceItems = new Vector<Character>();
    }

    /**
     * 
     * @param positionArray
     * @param sequenceItemArray
     */
    public TargetFunction(int[] positionArray, char[] sequenceItemArray) {
        this();
        for (int i = 0; i < positionArray.length; ++i) {
            positions.add(positionArray[i]);
            sequenceItems.add(sequenceItemArray[i]);
        }
    }
}
