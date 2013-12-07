package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.JavaDNF.lib.Utils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Push equivalent AbstracTerm into buckets of the hashTable.
 * Equivalence of AbstractTerms: the coverage indicator vectors are the same.
 * @author cc
 */
public class Uniqifier<E extends ITerm> {

    /** The hashTable stores the buckets of equivalent terms */
    public HashMap<Integer, Vector<E>> hashTable;
    /** The big prime number used for the hashFunction */
    private int prime;

    /**
     * Default constructor.
     */
    public Uniqifier() {
        hashTable = new HashMap<Integer, Vector<E>>();
    }

    /**
     * Automatically uniqify.
     * @param originalItems
     */
    public Uniqifier(Collection<E> originalItems) {
        this();
        this.prime = AbstractTerm.TOTAL_SPACE;
        this.uniqify(originalItems);
    }

    /**
     * Push equivalent AbstracTerm into buckets of the hashTable.
     * Equivalence of AbstractTerms: the coverage indicator vectors are the same
     * @param originalItems
     * @return hashTable
     *
     */
    public void uniqify(Collection<E> originalItems) {
        hashTable.clear();
        this.pushMoreDataIntoHashTable(originalItems);
    }

    /**
     * If the hashTable is already constructed, push more terms into the hashTable.
     * @param extraItems
     */
    public void pushMoreDataIntoHashTable(Collection<E> extraItems) {

        for (Iterator it = extraItems.iterator(); it.hasNext();) {
            E term = (E) it.next();
            int n = 0;
            int k = hashFunction(term);

            boolean isSuccessful = false;
            while (hashTable.containsKey(k + n * prime)) {
                if (hashTable.get(k + n * prime).get(0).isCoverageEqual(term)) {
                    hashTable.get(k + n * prime).add(term);
                    isSuccessful = true;
                    break;
                } else {
                    n++;
                }
            }
            // No key found; create new bucket.
            if (!isSuccessful) {
                Vector<E> tempV = new Vector<E>();
                tempV.add(term);
                hashTable.put(k + n * prime, tempV);
            }
        }
    }

    /**
     * Map an AbstractTerm to an integer key.
     * @param term
     * @return
     */
    public int hashFunction(E term) {
        return term.getCoverageSummation();
    }

    /**
     * Given an AbstractTerm, find the corresponding key in the hashTable; -1 if not found.
     * @param term
     * @return
     */
    public int findKey(E term) {
        int k = hashFunction(term);
        int n = 0;

        while (hashTable.containsKey(k + n * prime)) {
            if (hashTable.get(k + n * prime).get(0).isCoverageEqual(term)) {
                return k + n * prime;
            } else {
                n++;
            }
        }
        return -1;
    }

    /**
     * 
     */
    public String ToString() {
        StringBuilder s = new StringBuilder();
        for (Iterator it = hashTable.entrySet().iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry)it.next();
            Utils.debug(me.getKey() + " : ");
            Vector<E> term = (Vector<E>) me.getValue();
            for (int i = 0; i < term.size(); ++i) {
                Utils.debug(term.get(i).ToString() + "; ");
            }
            Utils.debugln();
        }
        return s.toString();
    }
}
