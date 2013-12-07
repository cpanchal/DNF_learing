/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Ambiguous character class.
 * The ambiguous characters are represented in lower cases.
 * @author Chuang
 */
public class AmbiguousCharacter {

    public static HashMap<Character, HashSet<Character>> PROTEIN_AMBIGUOUSCHAR;
    public static HashMap<Character, Character> PROTEIN_AMBIGUOUSCHAR_REVERSE;
    public static HashMap<Character, HashSet<Character>> DNA_AMBIGUOUSCHAR;
    public static HashMap<Character, Character> DNA_AMBIGUOUSCHAR_REVERSE;
    public static HashMap<Character, HashSet<Character>> RNA_AMBIGUOUSCHAR;
    public static HashMap<Character, Character> RNA_AMBIGUOUSCHAR_REVERSE;


    /**
     * p: Positive: R, K
     * n: Negative: D, E
     * 
     */
    static {
        PROTEIN_AMBIGUOUSCHAR = new HashMap<Character, HashSet<Character>>();
        PROTEIN_AMBIGUOUSCHAR_REVERSE = new HashMap<Character, Character>();

        // Construct
        char cuttentChar;
        Iterator it;
        HashSet<Character> tempSet;
        // B
        cuttentChar = 'b';
        tempSet = new HashSet<Character>();
        tempSet.add('D');
        tempSet.add('N');
        PROTEIN_AMBIGUOUSCHAR.put(cuttentChar, tempSet);
        it = tempSet.iterator();
        while (it.hasNext()) {
            char currentC = (Character) it.next();
            PROTEIN_AMBIGUOUSCHAR_REVERSE.put(currentC, cuttentChar);
        }

        // Z
        cuttentChar = 'z';
        tempSet = new HashSet<Character>();
        tempSet.add('E');
        tempSet.add('Q');
        PROTEIN_AMBIGUOUSCHAR.put(cuttentChar, tempSet);
        it = tempSet.iterator();
        while (it.hasNext()) {
            char currentC = (Character) it.next();
            PROTEIN_AMBIGUOUSCHAR_REVERSE.put(currentC, cuttentChar);
        }

        // J
        cuttentChar = 'j';
        tempSet = new HashSet<Character>();
        tempSet.add('I');
        tempSet.add('L');
        PROTEIN_AMBIGUOUSCHAR.put(cuttentChar, tempSet);
        it = tempSet.iterator();
        while (it.hasNext()) {
            char currentC = (Character) it.next();
            PROTEIN_AMBIGUOUSCHAR_REVERSE.put(currentC, cuttentChar);
        }

        // P
        cuttentChar = 'p';
        tempSet = new HashSet<Character>();
        tempSet.add('R');
        tempSet.add('K');
        PROTEIN_AMBIGUOUSCHAR.put(cuttentChar, tempSet);
        it = tempSet.iterator();
        while (it.hasNext()) {
            char currentC = (Character) it.next();
            PROTEIN_AMBIGUOUSCHAR_REVERSE.put(currentC, cuttentChar);
        }

        // N
        cuttentChar = 'n';
        tempSet = new HashSet<Character>();
        tempSet.add('D');
        tempSet.add('E');
        PROTEIN_AMBIGUOUSCHAR.put(cuttentChar, tempSet);
        it = tempSet.iterator();
        while (it.hasNext()) {
            char currentC = (Character) it.next();
            PROTEIN_AMBIGUOUSCHAR_REVERSE.put(currentC, cuttentChar);
        }



    }

    static {
        DNA_AMBIGUOUSCHAR = new HashMap<Character, HashSet<Character>>();
        DNA_AMBIGUOUSCHAR_REVERSE = new HashMap<Character, Character>();
    }

    static {
        RNA_AMBIGUOUSCHAR = new HashMap<Character, HashSet<Character>>();
        RNA_AMBIGUOUSCHAR_REVERSE = new HashMap<Character, Character>();
    }
}
