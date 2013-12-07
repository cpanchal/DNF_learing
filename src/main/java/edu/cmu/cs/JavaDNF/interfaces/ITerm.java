/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.interfaces;

import edu.cmu.cs.compbio.lib.Sequence;
import java.util.Vector;

/**
 *
 * @author cc
 */
public interface ITerm {

    /**
     *
     * @return
     */
    int getPosition();

    /**
     *
     * @return
     */
    char getSequenceIterm();

    /**
     *
     * @return
     */
    Vector<ITerm> getITerms();

    /**
     * 
     * @return
     */
    int getNumberOfITerms();

    /**
     *
     * @return
     */
    boolean[] getCoverage();

    /**
     *
     * @param i
     * @return
     */
    boolean getCoverage(int i);

    /**
     *
     * @return
     */
    int getCoverageSummation();

    /**
     *
     * @return
     */
    int getPositiveCoverageSummation();

    /**
     *
     * @return
     */
    int getNegativeCoverageSummation();


    /**
     *
     * @return
     */
    int getCoverageLength();

    /**
     *
     * @param coverage
     */
    void setCoverage(boolean[] coverage);

    /**
     *
     * @param i
     * @param coverage
     */
    void setCoverage(int i, boolean coverage);

    /**
     *
     * @param seq
     * @return
     */
    abstract boolean isConsistent(Sequence seq);

    /**
     * 
     * @param strBuilder
     * @return
     */
    abstract boolean isConsistent(StringBuilder strBuilder);

    /**
     *
     * @return
     */
    boolean isCoveringPositive();

    /**
     *
     * @return
     */
    boolean isCoveringOnlyPositive();

    /**
     *
     * @return
     */
    boolean isCoveringAllOnlyPositive();

    /**
     *
     * @return
     */
    boolean isCoveringNegative();

    /**
     *
     * @param anotherTerm
     * @return
     */
    boolean isCoverageEqual(ITerm anotherTerm);

    /**
     *
     * @param term
     */
    void OR(ITerm term);

    /**
     *
     * @return
     */
    boolean isFull();

    /**
     *
     * @return
     */
    boolean isEmpty();

    /**
     *
     * @return
     */
    String printCoverage();

    /**
     *
     * @param position
     * @return
     */
    ITerm get(int position);

    /**
     *
     * @param cc
     */
    void add(ITerm cc);

    /**
     *
     * @param position
     */
    void remove(int position);

    /**
     *
     */
    void clear();

    /**
     *
     * @return
     */
    abstract public String ToString();
}
