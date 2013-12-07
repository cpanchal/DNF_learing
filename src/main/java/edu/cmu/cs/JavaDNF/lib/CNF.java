/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.lib;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.compbio.lib.Sequence;
import java.util.Vector;
import java.util.Collection;
import edu.cmu.cs.JavaDNF.lib.Utils;

/**
 *
 * @author Chuang Chris Wu
 */
public class CNF extends AbstractTerm {

    /**
     *
     */
    public CNF() {
        super();
        terms = new Vector<ITerm>();
        numberOfITerms = 0;
    }

    /**
     * 
     * @param literals
     */
    public CNF(Collection<ITerm> literals) {
        this();
        this.terms = new Vector(literals);
        this.numberOfITerms = this.terms.size();

        if (this.numberOfITerms > 0) {
            this.coverage = (boolean[]) this.terms.get(0).getCoverage().clone();
            for (int i = 1; i < this.numberOfITerms; ++i) {
                this.coverage = Utils.AND(this.coverage, this.get(i).getCoverage());
            }
        }
    }

    /**
     *
     * @param literalList
     */
    public CNF(Vector<ITerm> literalList) {
        this();
        numberOfITerms = literalList.size();
        terms = literalList;
    }

    /**
     * 
     * @param anotherCNFClause
     */
    public CNF(ITerm anotherCNFClause) {
        this();
        numberOfITerms = anotherCNFClause.getNumberOfITerms();
        terms = (Vector<ITerm>) anotherCNFClause.getITerms().clone();
        this.OR((ITerm) anotherCNFClause);
    }

    /**
     *
     * @param l
     */
    public void add(ITerm l) {
        this.terms.add(l);
        this.numberOfITerms++;
        this.AND(l);
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
     * @param strBuilder
     * @return
     */
    public boolean isConsistent(Sequence sequence) {
        for (int i = 0; i < numberOfITerms; ++i) {
            if (!terms.get(i).isConsistent(sequence)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param strBuilder
     * @return
     */
    public boolean isConsistent(StringBuilder strBuilder) {
        for (int i = 0; i < numberOfITerms; ++i) {
            if (!terms.get(i).isConsistent(strBuilder)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    public String ToString() {
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < numberOfITerms - 1; ++i) {
            if (terms.get(i).getNumberOfITerms() > 1) {
                strBuilder.append("(" + terms.get(i).ToString() + ")^");
            } else {
                strBuilder.append(terms.get(i).ToString() + '^');
            }
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

