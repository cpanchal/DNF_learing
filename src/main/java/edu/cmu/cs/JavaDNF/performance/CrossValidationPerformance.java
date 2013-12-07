package edu.cmu.cs.JavaDNF.performance;

import edu.cmu.cs.JavaDNF.interfaces.ICrossValidationIterator;
import edu.cmu.cs.JavaDNF.algorithm.LearnDNF;
import edu.cmu.cs.JavaDNF.algorithm.LearnParameters;
import edu.cmu.cs.JavaDNF.algorithm.Prediction;
import edu.cmu.cs.JavaDNF.lib.DNFList;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.JavaDNF.lib.Utils;

/**
 *
 * @author cc
 */
public class CrossValidationPerformance extends APredictionPerformance {

    public int error;

    /**
     * 
     * @param gs
     */
    public CrossValidationPerformance(GenotypeSequences gs) {

        //ICrossValidationIterator lood = new CrossValidationIterator(gs, 10);
        ICrossValidationIterator lood = new LeaveOneOutIterator(gs);
        error = 0;

        int index = 0;
        while (lood.hasNext()) {
            Utils.debugln("=============== Iteration " + index);
            
            TrainTestDataClass ttdc = lood.next();
            if (index >= 43 && index <=51) {

                // learn parameters
                ICrossValidationIterator loodA = new LeaveOneOutIterator(ttdc.getTrainGenotypeSequences());
                //ICrossValidationIterator loodA = new CrossValidationIterator(ttdc.getTrainGenotypeSequences(), 21);
                LearnParameters lp = new LearnParameters(loodA);
                LearnDNF.DENSITY_CUTOFF = lp.densityCutOff;
                LearnDNF learnDNF = new LearnDNF(ttdc.getTrainGenotypeSequences());

                Prediction.IS_VERBOSE = true;
                int curError = Prediction.predict(learnDNF.getDNFs(), ttdc, lp.predictionCutOff);
                Prediction.IS_VERBOSE = false;
                error += curError;
                Utils.debugln("The number of DNFs learned is: " + learnDNF.dnfList.getNumberOfDNFs());
                
                Utils.debugln("The error for this fold is: " + curError);
            }
            index++;
        }
        Utils.debugln("The final prediction error is: " + error);
    }

    /**
     *
     * @param ttdc
     * @return
     */
    public DNFList learn(TrainTestDataClass ttdc) {
        DNFList dnfList = new DNFList();

        return dnfList;
    }

    /**
     *
     * @param ttdc
     * @return
     */
    public int predict(TrainTestDataClass ttdc, DNFList dNFList) {
        int error = 0;
        return error;
    }

    public static void main(String[] args) {
        String dataName = "";
        String dataSet = "";
        dataSet = "/Users/Chuang/Work/Research/Data/Promoter_Gene";
        dataName = "Promoter_Gene";
        if (args.length > 1) {
            dataName = args[1];
        }
        if (args.length > 0) {
            dataSet = args[0];
        }

        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
        new CrossValidationPerformance(gs);
    }
}
