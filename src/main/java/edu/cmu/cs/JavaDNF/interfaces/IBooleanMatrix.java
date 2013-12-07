/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.JavaDNF.interfaces;

/**
 *
 * @author Chuang
 */
public interface IBooleanMatrix {
    
    /**
     *
     * @return
     */
    int getRowDimension();

    /**
     *
     * @return
     */
    int getColumnDimension();

    /**
     *
     * @param pA
     * @param pB
     * @return
     */
    boolean get(int pA, int pB);

    /**
     *
     * @param pA
     * @param pB
     * @param ele
     */
    void set(int pA, int pB, boolean ele);

    /**
     *
     * @return
     */
    boolean[][] getArray();

    /**
     *
     * @return
     */
    int getSummation();

    /**
     *
     * @return
     */
    boolean isFull();

    /**
     *
     * @param mat
     */
    void OR(IBooleanMatrix mat);

    /**
     *
     * @param mat
     */
    void XOR(IBooleanMatrix mat);

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
