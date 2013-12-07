package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.JavaDNF.lib.AmbiguousCharacter;
import edu.cmu.cs.JavaDNF.lib.Literal;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.compbio.lib.IDataSequence;
import edu.cmu.cs.compbio.lib.Sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author cc
 */
public class LearnLiteralsFromGS {

    /** */
    public static boolean IS_FAST_VERSION = false;
    /** */
    public static boolean USE_UNIQIFY = false;
    /** When creating literals, use rules to ignore certain literals */
    public static boolean USE_RULE = true;
    /** */
    public int numberOfLiterals;
    /** */
    public int numberOfPositions;
    /** */
    public int numberOfDataPoints;
    /** */
    public Vector<ITerm> orgLiteralList;
    /** */
    public Vector<ITerm> activeLiteralList;
    /** */
    public Uniqifier<ITerm> uniqifier;
    /** Ignore these position + char */
    private HashMap<Integer, ArrayList<Character>> rules = null;

    /**
     * 
     */
    public LearnLiteralsFromGS() {
        orgLiteralList = new Vector<ITerm>();
        if (USE_RULE) {
        	rules = new HashMap<Integer, ArrayList<Character>>();

        }
    }

    /**
     * 
     * @param gs
     */
    public LearnLiteralsFromGS(IDataSequence gs) {
        this();
        AbstractTerm.SPACE = gs.getSequenceNumbers(0);
        AbstractTerm.TOTAL_SPACE = gs.getTotalNumber();
        Vector<Integer> keyResidues = new Vector<Integer>(gs.getNonUniquePositions().size());
        for (int i = 0; i < gs.getNonUniquePositions().size(); ++i) {
            keyResidues.add(gs.getNonUniquePositions().get(i));
        }
        this.create(gs, keyResidues);
    }

    /**
     * 
     * @param gs
     * @param keyResidues
     */
    public LearnLiteralsFromGS(IDataSequence gs, Vector<Integer> keyResidues) {
        this();
        AbstractTerm.SPACE = gs.getSequenceNumbers(0);
        AbstractTerm.TOTAL_SPACE = gs.getTotalNumber();
        this.create(gs, keyResidues);
    }

    /**
     * 
     * @param gs
     * @param keyResidues
     */
    public void create(IDataSequence gs, Vector<Integer> keyResidues) {
        this.numberOfDataPoints = gs.getTotalNumber();
        this.numberOfPositions = keyResidues.size();

        for (int i = 0; i < gs.getSequenceItems().length(); ++i) {
            char item;
            if (AbstractTerm.USE_AMBIGUOUS_CHAR) {
                item = Character.toLowerCase(gs.getSequenceItems().charAt(i));
                if (AmbiguousCharacter.PROTEIN_AMBIGUOUSCHAR.containsKey(item)) {
                    this.create(item, gs, keyResidues);
                }
            } else if (AbstractTerm.USE_NEGATION) {
                item = Character.toLowerCase(gs.getSequenceItems().charAt(i));
                this.create(item, gs, keyResidues);
            }
            item = Character.toUpperCase(gs.getSequenceItems().charAt(i));
            this.create(item, gs, keyResidues);
        }

        this.numberOfLiterals = this.orgLiteralList.size();
        if (USE_UNIQIFY) {
            this.uniqfy();
        } else {
            activeLiteralList = orgLiteralList;
        }
    }

    /**
     * 
     * @param currentC
     * @param gs
     * @param keyResidues
     */
    private void create(char currentC, IDataSequence gs, Vector<Integer> keyResidues) {
        for (int j = 0; j < this.numberOfPositions; ++j) {
        	if (USE_RULE) {
        		char cc = Character.toLowerCase(currentC);
        		int p = keyResidues.get(j);
        		// missing value not used
        		if (cc == 'n') {
        			break;
        		}
        		if (p == 27) {
        			if (cc == 'p') {
        				break;
        			}
        		}
        		if (p == 29) {
        			if (cc == 'p' || cc == 'q' || cc == 'r') {
        			//if (cc == 'q' || cc == 'r') {
            			break;
            		}
        		}
        		if (p == 30) {
        			if (cc == 'q') {
            			break;
            		}
        		}
        		if (p > 30) {
        			if (cc == 'q' || cc == 'r') {
            			break;
            		}	
        		}
        		/*
        		if (p > 39) {
        			break;
        		}
        		*/
        		if (p == 0 && cc == 'o') {
        			break;
        		}
        	}
        	
            ITerm l = new Literal(keyResidues.get(j), currentC);
            l.setCoverage(this.createCoverage(gs, l));
            if (IS_FAST_VERSION) {
                if (l.isCoveringOnlyPositive()) {
                    this.orgLiteralList.add(l);
                }
            } else {
                if (!l.isFull() && l.isCoveringPositive()) {
                    this.orgLiteralList.add(l);
                }
            }
        }
    }

    /**
     * 
     * @param gs
     * @param l
     * @return
     */
    public boolean[] createCoverage(IDataSequence gs, ITerm l) {
        boolean[] result = new boolean[gs.getTotalNumber()];
        if (Character.isUpperCase(l.getSequenceIterm())) {
            for (int i = 0; i < gs.getTotalNumber(); ++i) {
                if (i < AbstractTerm.SPACE) {
                    result[i] = (gs.get(0, i).get(l.getPosition()) == l.getSequenceIterm());
                } else {
                    result[i] = (gs.get(1, i - AbstractTerm.SPACE).get(l.getPosition()) == l.getSequenceIterm());
                }
            }
        } else {
            if (AbstractTerm.USE_AMBIGUOUS_CHAR) {
                for (int i = 0; i < gs.getTotalNumber(); ++i) {
                    char cA = l.getSequenceIterm();
                    char cB;
                    if (i < AbstractTerm.SPACE) {
                        cB = Character.toUpperCase(gs.get(0, i).get(l.getPosition()));
                    } else {
                        cB = Character.toUpperCase(gs.get(1, i - AbstractTerm.SPACE).get(l.getPosition()));
                    }
                    if (AmbiguousCharacter.PROTEIN_AMBIGUOUSCHAR.containsKey(cA)) {
                        Iterator it = AmbiguousCharacter.PROTEIN_AMBIGUOUSCHAR.get(cA).iterator();
                        while (it.hasNext()) {
                            char currentC = (Character) it.next();
                            if (currentC == cB) {
                                result[i] = true;
                            }
                        }
                    }
                }
            } else if (AbstractTerm.USE_NEGATION) {
                for (int i = 0; i < gs.getTotalNumber(); ++i) {
                    char cA = Character.toUpperCase(l.getSequenceIterm());
                    char cB;
                    if (i < AbstractTerm.SPACE) {
                        cB = Character.toUpperCase(gs.get(0, i).get(l.getPosition()));
                    } else {
                        cB = Character.toUpperCase(gs.get(1, i - AbstractTerm.SPACE).get(l.getPosition()));
                    }
                    result[i] = (cA != cB);
                }
            }
        }
        return result;
    }

    /**
     *
     */
    public void uniqfy() {
        uniqifier = new Uniqifier<ITerm>(orgLiteralList);
        activeLiteralList = new Vector<ITerm>(uniqifier.hashTable.size());
        for (Iterator it = uniqifier.hashTable.entrySet().iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry) it.next();
            Vector<ITerm> term = (Vector<ITerm>) me.getValue();
            activeLiteralList.add(term.get(0));
        }
        this.numberOfLiterals = activeLiteralList.size();
    }

    /**
     * 
     * @return
     */
    public String ToString() {

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("The number of DataPoint is: ");
        strBuilder.append(Integer.toString(this.numberOfDataPoints) + "\n");
        strBuilder.append("The number of Position is: ");
        strBuilder.append(Integer.toString(this.numberOfPositions) + "\n");
        strBuilder.append("The number of original Literal is: ");
        strBuilder.append(Integer.toString(this.orgLiteralList.size()) + "\n");
        strBuilder.append("The number of activeLiteral is: ");
        strBuilder.append(Integer.toString(this.numberOfLiterals) + "\n");

        for (int i = 0; i < this.numberOfLiterals; ++i) {
            strBuilder.append(this.activeLiteralList.get(i).ToString());
            if (AbstractTerm.IS_VERBOSE) {
                strBuilder.append(" ");
                strBuilder.append(this.activeLiteralList.get(i).printCoverage());
            }
            strBuilder.append("\n");
        }
        return strBuilder.toString();
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        String dataSet = "/Users/Chuang/Work/Research/Data/Test";
        String dataName = "test2";

        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
        gs.sequenceItems = Sequence.PROTEIN_ITERMS;
        
        AbstractTerm.IS_FAST_VERSION = false;
        AbstractTerm.IS_VERBOSE = true;
        AbstractTerm.USE_NEGATION = true;
        AbstractTerm.USE_AMBIGUOUS_CHAR = false;

        LearnLiteralsFromGS.IS_FAST_VERSION = false;
        LearnLiteralsFromGS.USE_UNIQIFY = false;



        LearnLiteralsFromGS clfgs = new LearnLiteralsFromGS(gs);
        System.out.println(clfgs.ToString());


    }
}
