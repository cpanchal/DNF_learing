/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.JavaDNF.lib.CNF;
import edu.cmu.cs.JavaDNF.lib.DNF;
import edu.cmu.cs.JavaDNF.lib.Literal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author Chuang
 */
public class DNFFunctions {

    /**
     * Replace clauses with clauses with fewer literals.
     * @param dnf
     * @param hashTable
     */
    public static DNF generalizeDNF(DNF dnf, Uniqifier<CNF> uniqifier) {
        DNF newDNF = new DNF();
        for (int i = 0; i < dnf.getNumberOfITerms(); ++i) {
            CNF term = (CNF) dnf.get(i);
            int key = uniqifier.findKey(term);
            Vector<CNF> tempCNFClauses = uniqifier.hashTable.get(key);
            for (int j = 1; j < tempCNFClauses.size(); ++j) {
                if (tempCNFClauses.get(j).getNumberOfITerms() < term.getNumberOfITerms()) {
                    term = tempCNFClauses.get(j);
                }
            }
            newDNF.add(term);
        }
        return newDNF;
    }

    /**
     * 
     * @param dnf
     * @return
     */
    public static DNF mergeITermWithTheSamePosition(DNF dnf) {
        DNF newDNF = new DNF();
        
        // construct hashmap
        HashMap<Integer, HashSet<Character>> literalHashMap = new HashMap<Integer, HashSet<Character>>();
        for (int i = 0; i < dnf.getNumberOfITerms(); ++i) {
            for (int j = 0; j < dnf.get(i).getNumberOfITerms(); ++j) {
                if (literalHashMap.containsKey(dnf.get(i).get(j).getPosition())) {
                    literalHashMap.get(dnf.get(i).get(j).getPosition()).add(dnf.get(i).get(j).getSequenceIterm());
                } else {
                    HashSet<Character> tempSet = new HashSet<Character>();
                    tempSet.add(dnf.get(i).get(j).getSequenceIterm());
                    literalHashMap.put(dnf.get(i).get(j).getPosition(), tempSet);
                }
            }
        }
        
        // convert literal hashmap to another hashmap
        HashMap<Integer, ITerm> newTerms = new HashMap<Integer, ITerm>();
        Iterator it = literalHashMap.keySet().iterator();
        while (it.hasNext()) {
            int position = (Integer) it.next();
            Collection<ITerm> termList = new Vector<ITerm>();

            HashSet<Character> tempSet = literalHashMap.get(position);
            Iterator itt = tempSet.iterator();
            while (itt.hasNext()) {
                char c = (Character) itt.next();
                ITerm newL = new Literal(position, c);
                termList.add(newL);
            }
            ITerm newITerm = new DNF(termList);
            newTerms.put(position, newITerm);
        }

        // replace literals
        for (int i = 0; i < dnf.getNumberOfITerms(); ++i) {
            ITerm newCNF = new CNF();
            for (int j = 0; j < dnf.get(i).getNumberOfITerms(); ++j) {
                int position = dnf.get(i).get(j).getPosition();
                newCNF.add(newTerms.get(position));
            }
            newDNF.add(newCNF);
        }
        return newDNF;
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Vector<Literal> ls = new Vector<Literal>(10);
        ls.add(new Literal(9, 'L'));
        ls.add(new Literal(81, 'A'));
        ls.add(new Literal(36, 'N'));
        ls.add(new Literal(70, 'V'));
        ls.add(new Literal(71, 'I'));
        ls.add(new Literal(19, 'K'));
        ls.add(new Literal(35, 'M'));
        ls.add(new Literal(45, 'I'));
        ls.add(new Literal(53, 'V'));
        ls.add(new Literal(63, 'I'));

        ITerm cnfA = new CNF();
        cnfA.add(ls.get(0));
        cnfA.add(ls.get(1));
        cnfA.add(ls.get(2));

        ITerm dnfA = new DNF();
        dnfA.add(cnfA);
        dnfA.add(ls.get(0));

        ITerm cnfB = new CNF();
        cnfB.add(dnfA);
        cnfB.add(ls.get(2));
        cnfB.add(cnfA);

        ITerm dnfB = new DNF();
        dnfB.add(cnfB);
        dnfB.add(dnfA);
        dnfB.add(cnfA);
        dnfB.add(ls.get(3));

        System.out.println(cnfA.ToString());
        System.out.println(dnfA.ToString());
        System.out.println(cnfB.ToString());
        System.out.println(dnfB.ToString());
    }
}
