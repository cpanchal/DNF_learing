package edu.cmu.cs.JavaDNF.lib;

import edu.cmu.cs.JavaDNF.interfaces.IBooleanMatrix;

/**
 *
 * @author cc
 */
public class BooleanMatrix implements IBooleanMatrix {
    
    private boolean[][] mat = null;
    /** */
    private int m, n;

    /**
     * 
     */
    public BooleanMatrix() {

    }

    /**
     * 
     * @param m
     * @param n
     */
    public BooleanMatrix(int m, int n) {
        this.m = m;
        this.n = n;
        mat = new boolean[m][n];
    }

    /**
     * 
     * @param mbm
     */
    public BooleanMatrix(IBooleanMatrix mbm) {
        this(mbm.getRowDimension(), mbm.getColumnDimension());
        this.OR(mbm);
    }

    /**
     *
     * @return
     */
    public int getRowDimension() {
        return m;
    }

    /**
     *
     * @return
     */
    public int getColumnDimension() {
        return n;
    }

    /**
     *
     * @param pA
     * @param pB
     * @return
     */
    public boolean get(int pA, int pB) {
        if (pA < 0 || pA > m || pB < 0 || pB > n) {
            throw new IllegalArgumentException("0 <= index < maxDimension");
        }
        return mat[pA][pB];
    }

    /**
     *
     * @param pA
     * @param pB
     * @param ele
     */
    public void set(int pA, int pB, boolean ele) {
        if (pA < 0 || pA > m || pB < 0 || pB > n) {
            throw new IllegalArgumentException("0 <= index < maxDimension");
        }
        mat[pA][pB] = ele;
    }

    /**
     *
     * @return
     */
    public boolean[][] getArray() {
        return mat;
    }
    /**
     * 
     * @return
     */
    public int getSummation() {
        int s = 0;
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                s += mat[i][j] ? 1 : 0;
            }
        }
        return s;
    }

    /**
     * 
     * @return
     */
    public boolean isFull() {
        return this.getSummation() == m * n;
    }

    /**
     * 
     * @param mat
     */
    public void OR(IBooleanMatrix mat) {

        if (m != mat.getRowDimension() || n != mat.getColumnDimension()) {
            throw new IllegalArgumentException("Unequal matrix sizes");
        }
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                this.set(i, j, this.get(i, j) || mat.get(i, j));
            }
        }
    }

    /**
     * 
     * @param mat
     */
    public void XOR(IBooleanMatrix mat) {

        if (m != mat.getRowDimension() || n != mat.getColumnDimension()) {
            throw new IllegalArgumentException("Unequal matrix sizes");
        }
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                this.set(i, j, this.get(i, j) ^ mat.get(i, j));
            }
        }
    }

    /**
     * 
     */
    public void clear() {
        this.mat = null;
        this.m = 0;
        this.n = 0;
    }

    /**
     *
     * @return
     */
    public String ToString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                s.append(mat[i][j] + " ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * 
     * @param matA
     * @param matB
     */
    public static IBooleanMatrix OR(IBooleanMatrix matA, IBooleanMatrix matB) {

        if (matA.getRowDimension() != matB.getRowDimension()
                || matA.getColumnDimension() != matB.getColumnDimension()) {
            throw new IllegalArgumentException("Unequal matrix sizes");
        }
        int m = matA.getRowDimension();
        int n = matA.getColumnDimension();
        IBooleanMatrix matNew = new BooleanMatrix(m, n);
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                matNew.set(i, j, matA.get(i, j) || matB.get(i, j));
            }
        }
        return matNew;
    }

    /**
     *
     * @param matA
     * @param matB
     */
    public static IBooleanMatrix AND(IBooleanMatrix matA, IBooleanMatrix matB) {

        if (matA.getRowDimension() != matB.getRowDimension()
                || matA.getColumnDimension() != matB.getColumnDimension()) {
            throw new IllegalArgumentException("Unequal matrix sizes");
        }
        int m = matA.getRowDimension();
        int n = matA.getColumnDimension();
        IBooleanMatrix matNew = new BooleanMatrix(m, n);
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                matNew.set(i, j, matA.get(i, j) && matB.get(i, j));
            }
        }
        return matNew;
    }
}