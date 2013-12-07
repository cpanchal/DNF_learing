package edu.cmu.cs.JavaDNF.lib;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.compbio.lib.Sequence;
import java.util.Vector;

/**
 *
 * @author cc
 */
public abstract class AbstractTerm implements ITerm {

    /** */
    public static int TOTAL_SPACE;
    /** */
    public static int SPACE;
    /** */
    public static boolean IS_VERBOSE = false;
    /** */
    public static boolean IS_FAST_VERSION = true;
    /** */
    public static boolean USE_AMBIGUOUS_CHAR = true;
    /** */
    public static boolean USE_NEGATION = false;
    /** */
    protected boolean[] coverage;
    /** */
    protected Vector<ITerm> terms;
    /** */
    protected int numberOfITerms;
    /** */
    protected boolean isLiteral = false;

    /**
     * 
     */
    public AbstractTerm() {
        coverage = new boolean[TOTAL_SPACE];
    }

    /**
     *
     * @return
     */
    public int getPosition() {
        throw new IllegalArgumentException("No position");
    }

    /**
     *
     * @return
     */
    public char getSequenceIterm() {
        throw new IllegalArgumentException("No position");
    }

    /**
     * 
     * @return
     */
    public Vector<ITerm> getITerms() {
        return terms;
    }

    /**
     *
     * @return
     */
    public int getNumberOfITerms() {
        return numberOfITerms;
    }

    /**
     * 
     * @return
     */
    public boolean[] getCoverage() {
        return coverage;
    }

    /**
     * 
     * @param i
     * @return
     */
    public boolean getCoverage(int i) {
        if (i < 0 || i >= this.coverage.length) {
            throw new IllegalArgumentException("0 <= i < coverage.length");
        }
        return coverage[i];
    }

    /**
     *
     * @return
     */
    public int getCoverageSummation() {
        int result = 0;
        for (int i = 0; i < TOTAL_SPACE; ++i) {
            if (coverage[i]) {
                result++;
            }
        }
        return result;
    }

    /**
     *
     * @return
     */
    public int getPositiveCoverageSummation() {
        int result = 0;
        for (int i = 0; i < SPACE; ++i) {
            if (coverage[i]) {
                result++;
            }
        }
        return result;
    }
    
    /**
     *
     * @return
     */
    public int getNegativeCoverageSummation() {
        int result = 0;
        for (int i = SPACE; i < TOTAL_SPACE; ++i) {
            if (coverage[i]) {
                result++;
            }
        }
        return result;
    }


    /**
     * 
     * @return
     */
    public int getCoverageLength() {
        return coverage.length;
    }

    /**
     * 
     * @param coverage
     */
    public void setCoverage(boolean[] coverage) {
        if (coverage.length != this.coverage.length) {
            throw new IllegalArgumentException("uneuqal length");
        }
        this.coverage = coverage;
    }

    /**
     * 
     * @param i
     * @param curCoverage
     */
    public void setCoverage(int i, boolean curCoverage) {
        if (i < 0 || i >= this.coverage.length) {
            throw new IllegalArgumentException("0 <= i < coverage.length");
        }
        this.coverage[i] = curCoverage;
    }

    /**
     * 
     * @param seq
     * @return
     */
    abstract public boolean isConsistent(Sequence seq);

    /**
     * 
     * @return
     */
    public boolean isCoveringPositive() {
        for (int i = 0; i < SPACE; ++i) {
            if (coverage[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public boolean isCoveringOnlyPositive() {
        return this.isCoveringPositive() && !this.isCoveringNegative();
    }

    /**
     * 
     * @return
     */
    public boolean isCoveringAllOnlyPositive() {
        if (!this.isCoveringOnlyPositive()) {
            return false;
        }
        for (int i = 0; i < SPACE; ++i) {
            if (!coverage[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @return
     */
    public boolean isCoveringNegative() {
        for (int i = SPACE; i < TOTAL_SPACE; ++i) {
            if (coverage[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param anotherTerm
     * @return
     */
    public boolean isCoverageEqual(ITerm anotherTerm) {
        if (coverage.length != anotherTerm.getCoverageLength()) {
            return false;
        }
        for (int i = 0; i < coverage.length; ++i) {
            if (coverage[i] != anotherTerm.getCoverage(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param term
     */
    public void OR(ITerm term) {
        for (int i = 0; i < TOTAL_SPACE; ++i) {
            coverage[i] = coverage[i] || term.getCoverage(i);
        }
    }

    /**
     *
     * @param term
     */
    public void AND(ITerm term) {
        for (int i = 0; i < TOTAL_SPACE; ++i) {
            coverage[i] = coverage[i] && term.getCoverage(i);
        }
    }

    /**
     * 
     * @return
     */
    public boolean isFull() {
        return (this.getCoverageSummation() == TOTAL_SPACE);
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return (this.getCoverageSummation() == 0);
    }

    /**
     * 
     * @return
     */
    public String printCoverage() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < SPACE; ++i) {
            s.append(coverage[i] ? 1 : 0);
        }
        s.append('*');
        for (int i = SPACE; i < TOTAL_SPACE; ++i) {
            s.append(coverage[i] ? 1 : 0);
        }
        return s.toString();
    }

    /**
     *
     * @param position
     * @return
     */
    public ITerm get(int position) {
        if (position < 0 || position > this.numberOfITerms) {
            throw new IllegalArgumentException("0 <= position < length");
        }
        return this.terms.get(position);
    }

    /**
     *
     * @param cc
     */
    public abstract void add(ITerm cc);

    /**
     *
     * @param position
     */
    public abstract void remove(int position);

    /**
     *
     */
    public void clear() {
        terms.clear();
        numberOfITerms = 0;
    }

    /**
     * 
     * @return
     */
    abstract public String ToString();
}
