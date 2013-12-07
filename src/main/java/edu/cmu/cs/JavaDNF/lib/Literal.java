/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.lib;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.compbio.lib.Sequence;

/**
 *
 * @author cc
 */
public class Literal extends AbstractTerm {

    /** The position of the literal */
    private int position;
    /** The sequence item of the literal */
    private char sequenceIterm;

    /**
     *
     */
    public Literal() {
        super();
        isLiteral = true;
        position = -1;
        sequenceIterm = '\0';
    }

    /**
     * 
     * @param l
     */
    public Literal(Literal l) {
        this();
        this.position = l.position;
        this.sequenceIterm = l.sequenceIterm;
        this.coverage = new boolean[l.coverage.length];
        System.arraycopy(l.coverage, 0, this.coverage, 0, l.coverage.length);
    }

    /**
     * 
     * @param pos
     * @param c
     */
    public Literal(int pos, char c) {
        this();
        position = pos;
        sequenceIterm = c;
    }

    /**
     * 
     * @return
     */
    @Override
    public int getPosition() {
        return this.position;
    }

    /**
     *
     * @return
     */
    @Override
    public char getSequenceIterm() {
        return this.sequenceIterm;
    }

    /**
     * 
     * @param seq
     * @return
     */
    public boolean isConsistent(Sequence seq) {
        if (position < 0 || position > seq.length) {
            throw new IllegalArgumentException("0 <= position < seq.length");
        }
        char seqChar = seq.get(position);
        if (sequenceIterm == seqChar) {
            return true;
        } else if (Character.isLowerCase(sequenceIterm)) {
            if (USE_AMBIGUOUS_CHAR) {
                if (AmbiguousCharacter.PROTEIN_AMBIGUOUSCHAR.containsKey(sequenceIterm)) {
                    if (AmbiguousCharacter.PROTEIN_AMBIGUOUSCHAR.get(sequenceIterm).contains(seqChar)) {
                        return true;
                    }
                }
                /*
                if (AmbiguousCharacter.PROTEIN_AMBIGUOUSCHAR.containsKey(seqChar)) {
                if (AmbiguousCharacter.PROTEIN_AMBIGUOUSCHAR.get(seqChar).contains(sequenceIterm)) {
                return true;
                }
                }
                 *
                 */
            } else if (USE_NEGATION) {
                if (Character.toUpperCase(sequenceIterm) != seqChar) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param c
     * @return
     */
    public boolean isConsistent(char c) {
        return (sequenceIterm == c);
    }

    /**
     *
     * @param strBuilder
     * @return
     */
    public boolean isConsistent(StringBuilder strBuilder) {
        if (position < 0 || position > strBuilder.length()) {
            throw new IllegalArgumentException("0 <= position < strBuilder .length");
        }
        return (sequenceIterm == strBuilder.charAt(position));
    }

    /**
     * 
     * @param l
     * @return
     */
    public boolean isEqual(Literal l) {
        return (this.position == l.position && this.sequenceIterm == l.sequenceIterm);
    }

    /**
     *
     * @param position
     */
    public void remove(int position) {
        throw new IllegalArgumentException("Attempting to modify immutable Literal iterms");
    }

    /**
     *
     */
    public void add(ITerm l) {
        throw new IllegalArgumentException("Attempting to modify immutable Literal iterms");
    }

    /**
     * 
     * @return
     */
    public String ToString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(Integer.toString(position));
        strBuilder.append(sequenceIterm);
        return strBuilder.toString();
    }
}
