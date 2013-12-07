package edu.cmu.cs.JavaDNF.lib;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import java.util.Vector;
import java.util.Collection;
import edu.cmu.cs.compbio.lib.*;
import edu.cmu.cs.JavaDNF.lib.Utils;

/**
 *
 * @author cc
 */
public class DNF extends AbstractTerm {

    /**
     * 
     */
    public DNF() {
        super();
        terms = new Vector<ITerm>();
        numberOfITerms = 0;
    }

    /**
     * 
     * @param cnfs
     */
    public DNF(Collection<ITerm> cnfs) {
        this();
        this.terms = new Vector(cnfs);
        this.numberOfITerms = this.terms.size();

        if (this.numberOfITerms > 0) {

            for (int i = 0; i < this.numberOfITerms; ++i) {
                if (i == 0) {
                    this.coverage = (boolean[]) this.terms.get(0).getCoverage().clone();
                } else {
                    this.coverage = Utils.OR(this.coverage, this.terms.get(i).getCoverage());
                }
            }
        }
    }

    /**
     *
     * @param cnfs
     */
    public DNF(Vector<ITerm> cnfs) {
        this();
        numberOfITerms = cnfs.size();
        terms = cnfs;
    }

    /**
     *
     * @param cc
     */
    public void add(ITerm cc) {
        this.terms.add(cc);
        this.numberOfITerms++;
        this.OR((ITerm) cc);
    }

    /**
     *
     * @param position
     */
    public void remove(int position) {
        if (position < 0 || position > this.numberOfITerms) {
            throw new IllegalArgumentException("0 <= position < length");
        }
        this.terms.remove(position);
        this.numberOfITerms--;
    }

    /**
     * 
     * @param filename
     * @return
     */
    public static DNF parseDNFFromFile(String filename) {
        //TO-DO
        DNF dnf = new DNF();
        return dnf;
    }

    /**
     *
     * @param sequence
     * @return
     */
    public boolean isConsistent(Sequence sequence) {
        for (int i = 0; i < numberOfITerms; ++i) {
            if (terms.get(i).isConsistent(sequence)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param strBuilder
     * @return
     */
    public boolean isConsistent(StringBuilder strBuilder) {
        for (int i = 0; i < numberOfITerms; ++i) {
            if (terms.get(i).isConsistent(strBuilder)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param gs
     * @return
     */
    public double[] qualityPerformance(IDataSequence gs) {
        double[] results = new double[2];
        double truePositive = 0, trueNegative = 0, falsePositive = 0, falseNegative = 0;

        for (int i = 0; i < gs.getTotalNumber(); ++i) {
            if (i < gs.getSequenceNumbers(0)) {
                if (this.isConsistent(gs.get(0, i))) {
                    truePositive++;
                } else {
                    falseNegative++;
                }
            } else {
                if (this.isConsistent(gs.get(1, i - gs.getSequenceNumbers(0)))) {
                    falsePositive++;
                } else {
                    trueNegative++;
                }
            }
        }
        results[0] = truePositive / (truePositive + falseNegative);
        results[1] = trueNegative / (trueNegative + falsePositive);
        return results;
    }

    /**
     * 
     * @return
     */
    public String ToString() {
        StringBuilder strBuilder = new StringBuilder();
        /*
        if (numberOfITerms > 0) {
        strBuilder.append("(");
        }
         *
         */
        for (int i = 0; i < numberOfITerms - 1; ++i) {
            if (terms.get(i).getNumberOfITerms() > 1) {
                strBuilder.append("(" + terms.get(i).ToString() + ")+");
            } else {
                strBuilder.append(terms.get(i).ToString() + '+');
            }
            //strBuilder.append(terms.get(i).ToString() + "+");
        }
        if (numberOfITerms > 0) {
            if (terms.get(numberOfITerms - 1).getNumberOfITerms() > 1) {
                strBuilder.append("(" + terms.get(numberOfITerms - 1).ToString() + ")");
            } else {
                strBuilder.append(terms.get(numberOfITerms - 1).ToString());
            }
        }
        return strBuilder.toString();
    }
}
