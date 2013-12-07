/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.lib.DNFList;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.JavaDNF.lib.Utils;

/**
 *
 * @author cc
 */
public class Prediction {

    public static boolean IS_VERBOSE = false;
    //public static double PREDICTION_CUTOFF = 0.5;
    //public int error;

    public static int predict(DNFList dnfList, TrainTestDataClass ttdc, double predictionCutOff ) {
        // learn predictionCutOff
        int error = 0;
        for (int i = 0; i < ttdc.getTestSequences().getNumberOfSequence(); ++i) {
            double fraction = dnfList.predict(ttdc.getTestSequences().get(i));
            if (IS_VERBOSE) {
                Utils.debugln("Prediction fraction is: " + fraction);
            }
            boolean prediction = fraction > predictionCutOff;
            if (prediction != ttdc.getTestGroundTruth().get(i)) {
                error++;
            }
        }
        return error;
    }
}
