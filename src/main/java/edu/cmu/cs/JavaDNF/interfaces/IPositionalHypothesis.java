/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.JavaDNF.interfaces;

/**
 *
 * @author Chuang
 */
public interface IPositionalHypothesis {



    /**
     *
     * @param position
     */
    void add(int position);

    /**
     *
     */
    void clear();

    /**
     *
     * @return
     */
    String ToString();
}
