package edu.cmu.cs.JavaDNF.lib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author cc
 */
public class PositionalHypotheses {

    /** */
    public Vector<PositionalHypothesis> hypotheses;
    /** */
    public int numberOfHypotheses;
    /** */
    public HashMap<Integer, Integer> densities;
    /** */
    public Vector<Integer> features;
    /** */
    public int hypothsisSize;

    /**
     * 
     */
    public PositionalHypotheses() {
        hypotheses = new Vector<PositionalHypothesis>();
        numberOfHypotheses = 0;
        densities = new HashMap<Integer, Integer>();
        features = new Vector<Integer>();
        hypothsisSize = 0;
    }

    /**
     * 
     * @param positionalHypothesis
     */
    public void add(PositionalHypothesis positionalHypothesis) {
        if (this.hypothsisSize == 0) {
            this.hypothsisSize = positionalHypothesis.hypothesisSize;
        }
        if (positionalHypothesis.hypothesisSize != this.hypothsisSize) {
            throw new IllegalArgumentException("Unequal length hypothesis");
        }
        hypotheses.add(positionalHypothesis);
        numberOfHypotheses++;
    }

    /**
     * 
     * @param index
     * @return
     */
    public PositionalHypothesis get(int index) {
        if (index < 0 || index >= this.numberOfHypotheses) {
            throw new IllegalArgumentException("0 <= index < numberOfHypotheses");
        }
        return this.hypotheses.get(index);
    }

    /**
     * 
     */
    public void generateDensityMap() {
        this.features.clear();
        this.densities.clear();
        for (int i = 0; i < this.hypotheses.size(); ++i) {
            for (int j = 0; j < this.hypotheses.get(i).hypothesisSize; ++j) {
                int curP = this.hypotheses.get(i).hypothesis.get(j);
                if (!this.features.contains(curP)) {
                    this.features.add(curP);
                }
                // Update densities HashMap
                if (densities.containsKey(curP)) {
                    densities.put(curP, densities.get(curP) + 1);
                } else {
                    densities.put(curP, 1);
                }
            }
        }
        // Update hypothesisDensitySummation
        for (int i = 0; i < numberOfHypotheses; ++i) {
            int dSum = 0;
            for (int j = 0; j < hypotheses.get(i).hypothesisSize; ++j) {
                int value = (Integer) densities.get(hypotheses.get(i).hypothesis.get(j));
                dSum += value;
            }
            hypotheses.get(i).hypothesisDensitySummation = dSum;
        }
    }

    /**
     * 
     */
    public void clear() {
        hypotheses.clear();
        numberOfHypotheses = 0;
        densities.clear();
    }

    /**
     *
     * @return
     */
    public String ToString() {
        StringBuilder s = new StringBuilder();

        s.append("The number of hypothesis sets is: " + this.numberOfHypotheses + "\n");
        for (int i = 0; i < this.numberOfHypotheses; ++i) {
            s.append("The number of positions selected " + this.hypothsisSize + ": ");
            s.append(this.hypotheses.get(i).ToString());
            s.append("\n");
        }
        s.append("The number of features is: " + this.features.size() + ": ");
        for (int i = 0; i < this.features.size(); ++i) {
            s.append(this.features.get(i) + " ");
        }
        s.append("\n");
        s.append("The density map is: ");
        for (Iterator it = densities.entrySet().iterator(); it.hasNext();) {
            Map.Entry me = (Map.Entry)it.next();
            s.append((Integer) me.getKey() + "-" + (Integer) me.getValue() + "; ");
        }
        return s.toString();
    }
}
