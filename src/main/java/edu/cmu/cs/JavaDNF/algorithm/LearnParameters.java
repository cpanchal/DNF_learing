
package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.interfaces.ICrossValidationIterator;
import edu.cmu.cs.JavaDNF.lib.DNFList;
import edu.cmu.cs.JavaDNF.lib.PositionalHypotheses;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.JavaDNF.performance.CrossValidationIterator;
import edu.cmu.cs.JavaDNF.performance.LeaveOneOutIterator;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.JavaDNF.lib.Utils;
import java.util.Vector;

/**
 *
 * @author cc
 */
public class LearnParameters {

    /** */
    public static boolean IS_VERBOSE = true;

    public double densityCutOff;
    public double predictionCutOff;
    public int error;

    /**
     * 
     * @param icvi
     */
    public LearnParameters(ICrossValidationIterator icvi) {
        this.preprocess(icvi);
    }

    /**
     * 
     * @param icvi
     */
    private void preprocess(ICrossValidationIterator icvi) {
        int totalNumber = icvi.getFoldNumber();
        Vector<DNFList> allDNFs = new Vector<DNFList>(totalNumber);
        Vector<TrainTestDataClass> allData = new Vector<TrainTestDataClass>(totalNumber);
        Vector<PositionalHypotheses> allHypotheseses = new Vector<PositionalHypotheses>(totalNumber);

        if (IS_VERBOSE) {
            Utils.debugln("========= Start preprocessing data");
        }
        int index = 0;
        while (icvi.hasNext()) {

            if (IS_VERBOSE) {
                Utils.debugln("==== Iteration " + index);
            }

            TrainTestDataClass curData = icvi.next();
            allData.add(curData);

            FeatureSelector.USE_GREEDY = false;
            FeatureSelector fs = new FeatureSelector(curData.getTrainGenotypeSequences());
            allHypotheseses.add(fs.hypotheses);

            LearnDNF.USE_GREEDY = true;
            LearnDNF learnDNF = new LearnDNF(curData.getTrainGenotypeSequences(), fs.hypotheses);
            allDNFs.add(learnDNF.getDNFs());

            if (IS_VERBOSE) {
                Utils.debugln("Learned DNFs " + learnDNF.getDNFs().getNumberOfDNFs());
                Utils.debugln("Learned Features " + fs.hypotheses.numberOfHypotheses);
            }
            index++;
        }

        if (IS_VERBOSE) {
            Utils.debugln("========= Start learning parameters");
        }

        this.learn(totalNumber, allDNFs, allData, allHypotheseses);

        Utils.debugln("the density cut off is: " + densityCutOff);
        Utils.debugln("the prediction cut off is: " + predictionCutOff);
        Utils.debugln("The smallest error of corresponding to these 2 parameter is: "+ " " + error);
    }

    /**
     * 
     * @param totalNumber
     * @param allDNFs
     * @param allData
     * @param allHypotheseses
     */
    public void learn(int totalNumber, Vector<DNFList> allDNFs,
            Vector<TrainTestDataClass> allData,
            Vector<PositionalHypotheses> allHypotheseses) {

        double curDensityCutOff = 0;
        double dStepSize = 0.01;

        error = Integer.MAX_VALUE;
        
        while (curDensityCutOff < 1) {
            double curPredictionCutOff = 0;
            double pStepSize = 0.01;
            
            while (curPredictionCutOff <= 1) {

                int curError = 0;
                for (int i = 0; i < totalNumber; ++i) {
                    PositionalHypotheses ph = allHypotheseses.get(i);
                    TrainTestDataClass curD = allData.get(i);
                    DNFList curAllDNF = allDNFs.get(i);

                    DNFList newDNFs = new DNFList();
                    for (int j = 0; j < ph.numberOfHypotheses; ++j) {
                        if (ph.get(j).hypothesisDensitySummation
                                > curDensityCutOff * ph.numberOfHypotheses * ph.hypothsisSize) {
                            newDNFs.add(curAllDNF.get(j));
                        }
                    }

                    // learn predictionCutOff
                    int tempError = Prediction.predict(newDNFs, curD, curPredictionCutOff);
                    curError += tempError;
                }

                //Utils.debugln(curDensityCutOff + " " + curPredictionCutOff + " " + curError);
                if (curError <= error) {
                    error = curError;
                    predictionCutOff = curPredictionCutOff;
                    densityCutOff = curDensityCutOff;

                }
                curPredictionCutOff += pStepSize;
            }
            curDensityCutOff += dStepSize;
        }
    }

    /*
    public LearnParameters(TrainTestDataClass ttdc) {
        this.learn(ttdc);
        Utils.debugln(densityCutOff + " " + predictionCutOff + " " + error);
    }


    public void learn(TrainTestDataClass ttdc) {
        FeatureSelector.USE_GREEDY = false;
        FeatureSelector fs = new FeatureSelector(ttdc.getTrainGenotypeSequences());
        LearnDNF.USE_GREEDY = true;
        LearnDNF dnfs = new LearnDNF(ttdc.getTrainGenotypeSequences(), fs.hypotheses);
        Utils.debugln(dnfs.getDNFs().ToString());


        double curDensityCutOff = 0;
        double dStepSize = 0.05;
        int totalError = Integer.MAX_VALUE;

        while (curDensityCutOff < 1) {
            // filter DNFs
            DNFList newDNFs = new DNFList();
            for (int j = 0; j < fs.hypotheses.numberOfHypotheses; ++j) {
                if (fs.hypotheses.get(j).hypothesisDensitySummation
                        > curDensityCutOff * fs.hypotheses.numberOfHypotheses * fs.hypotheses.hypothsisSize) {
                    newDNFs.add(dnfs.getDNFs().get(j));
                }
            }

            // learn predictionCutOff
            double curPredictionCutOff = 0;
            double pStepSize = 0.05;

            int error = Integer.MAX_VALUE;

            while (curPredictionCutOff <= 1) {
                int curError = 0;

                for (int j = 0; j < ttdc.getTestSequences().getNumberOfSequence(); ++j) {
                    boolean prediction = newDNFs.predict(ttdc.getTestSequences().get(j)) >= curPredictionCutOff;
                    if (prediction != ttdc.getTestGroundTruth().get(j)) {
                        curError++;
                    }
                }
                if (curError <= error) {
                    error = curError;
                    predictionCutOff = curPredictionCutOff;
                }
                curPredictionCutOff += pStepSize;
            }

            if (error < totalError) {
                totalError = error;
                densityCutOff = curDensityCutOff;
            }

            curDensityCutOff += dStepSize;
        }
    }
     * 
     */

    public static void main(String[] args) {
        String dataName = "";
        String dataSet = "";
        if (args.length > 1) {
            dataName = args[1];
        }
        if (args.length > 0) {
            dataSet = args[0];
        }
        dataSet = "/home/cc/Work/Research/Data/Promoter_Gene";
        dataName = "Promoter_Gene";

        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);

        int foldNumber = 21;
        ICrossValidationIterator cvCrossValidationIterator = new CrossValidationIterator(gs, foldNumber);
        ICrossValidationIterator looCrossValidationIterator = new LeaveOneOutIterator(gs);
        LearnParameters lp = new LearnParameters(cvCrossValidationIterator);
    }
}
