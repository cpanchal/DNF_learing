package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.Algorithm.CombinatorialIterator;
import edu.cmu.cs.Algorithm.io.Writer;
import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.JavaDNF.lib.CNF;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.machinelearning.algorithm.Utils;
import edu.cmu.cs.machinelearning.lib.Data;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author cc
 */
public class LearnCNFClauses {

    /** */
    public static boolean IS_FAST_VERSION = true;
    /** */
    public static int MAX_SIZE_OF_CLAUSE = 4;
    /** */
    public Vector<CNF> orgCNFClauseList;
    /** */
    public Vector<CNF> activeCNFClauseList;
    /** */
    public Uniqifier<CNF> uniqifier;

    /**
     * 
     */
    public LearnCNFClauses() {
        this.orgCNFClauseList = new Vector<CNF>();
    }

    /**
     * 
     * @param cgs
     */
    public LearnCNFClauses(LearnLiteralsFromGS cgs) {
        this();
        //this.create(cgs.activeLiteralList, cgs.numberOfPositions);
        this.create(cgs.activeLiteralList, MAX_SIZE_OF_CLAUSE);
    }

    /**
     * 
     * @param orgLiteralList
     * @param numberOfPositions
     */
    public void create(Vector<ITerm> literals, int numberOfPositions) {
        int total = 0;
        for (int n = 0; n < numberOfPositions && n < literals.size(); ++n) {
            for (Collection<ITerm> c : new CombinatorialIterator<ITerm>(n + 1, literals)) {

                total++;
                if (total % 1e6 == 0) {
                    Utils.debugln("Examined CNFClauses " + total);
                }

                CNF cc = new CNF(c);
                if (IS_FAST_VERSION) {
                    //if (cc.isCoveringPositive() && !cc.isCoveringNegative()) {
                    if (cc.getPositiveCoverageSummation() > 0.25 * AbstractTerm.SPACE
                            && cc.getNegativeCoverageSummation() < 0.25 * (AbstractTerm.TOTAL_SPACE - AbstractTerm.SPACE) ) {
                    //if (cc.getPositiveCoverageSummation() > 0.25 * AbstractTerm.SPACE && !cc.isCoveringNegative()) {
                        this.orgCNFClauseList.add(cc);
                    }
                } else {
                    if (cc.isCoveringPositive()) {
                        this.orgCNFClauseList.add(cc);
                    }
                }
            }
        }
        this.uniqfy();
    }

    /**
     * 
     */
    public void uniqfy() {
        uniqifier = new Uniqifier<CNF>(orgCNFClauseList);

        activeCNFClauseList = new Vector<CNF>(uniqifier.hashTable.size());
        for (Iterator it = uniqifier.hashTable.entrySet().iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry) it.next();
            Vector<CNF> term = (Vector<CNF>) me.getValue();
            activeCNFClauseList.add(term.get(0));
        }
    }

    /**
     *
     * @return
     */
    public String ToString() {

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("The number of original CNFClauses is: ");
        strBuilder.append(Integer.toString(this.orgCNFClauseList.size()) + "\n");
        strBuilder.append("The number of active CNFClauses is: ");
        strBuilder.append(Integer.toString(this.activeCNFClauseList.size()) + "\n");

        for (int i = 0; i < this.activeCNFClauseList.size(); ++i) {
            strBuilder.append(this.activeCNFClauseList.get(i).ToString());
            if (AbstractTerm.IS_VERBOSE) {
                strBuilder.append(" ");
                strBuilder.append(this.activeCNFClauseList.get(i).printCoverage());
            }
            strBuilder.append("\n");
        }
        return strBuilder.toString();
    }

    /**
     * 
     * @return
     */
    public Data convertToData() {
        Data cnfData = new Data<Integer, Integer>(AbstractTerm.TOTAL_SPACE, activeCNFClauseList.size());
        for (int i = 0; i < cnfData.getN(); ++i) {
            for (int j = 0; j < cnfData.getM(); ++j) {
                if (activeCNFClauseList.get(j).getCoverage(i)) {
                    cnfData.setOneX(1, i, j);
                } else {
                    cnfData.setOneX(0, i, j);
                }
                
            }
        }
        for (int i = 0; i < cnfData.getN(); ++i) {
            if (i < AbstractTerm.SPACE) {
                cnfData.setOneY(1, i);
            } else {
                cnfData.setOneY(0, i);
            }
        }
        return cnfData;
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        String dataName = ""; //"SIV_ENV";
        String dataSet = "";
        if (args.length > 1) {
            dataName = args[1];
        }
        if (args.length > 0) {
            dataSet = args[0];
        }

        dataSet = "/Users/Chuang/Work/Research/Data/HIV_Stanford";
        dataName = "APV_New";

        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
        //gs.sequenceItems = Sequence.PROTEIN_ITERMS;

        AbstractTerm.IS_FAST_VERSION = false;
        AbstractTerm.IS_VERBOSE = true;
        AbstractTerm.USE_NEGATION = false;
        AbstractTerm.USE_AMBIGUOUS_CHAR = false;

        LearnLiteralsFromGS.IS_FAST_VERSION = false;
        LearnLiteralsFromGS.USE_UNIQIFY = true;

        LearnCNFClauses.IS_FAST_VERSION = true;

        FeatureSelector.IS_VERBOSE = true;
        FeatureSelector.USE_GREEDY = true;

        FeatureSelector fs = new FeatureSelector(gs);
        //LearnDNF.COVERAGE_CUTOFF = fs.getMaxCoverage();

        LearnLiteralsFromGS clgs = null;
        if (FeatureSelector.USE_GREEDY) {
            clgs = new LearnLiteralsFromGS(gs, fs.hypotheses.features);
        }

        if (AbstractTerm.IS_VERBOSE) {
            Utils.debugln(clgs.ToString());
            Utils.debugln(clgs.uniqifier.ToString());
        }

        LearnCNFClauses ccc = new LearnCNFClauses(clgs);
        if (AbstractTerm.IS_VERBOSE) {
            Utils.debugln(ccc.ToString());
            //Utils.debugln(ccc.uniqifier.ToString());
        }

        Data cnfData = ccc.convertToData();
        cnfData.write(dataName);
        Writer.write(ccc.ToString(), dataName + "_CNF");
        
    }
}
