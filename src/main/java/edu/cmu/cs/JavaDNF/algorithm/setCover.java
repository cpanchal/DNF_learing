package edu.cmu.cs.JavaDNF.algorithm;

import java.util.Vector;
import java.util.HashSet;
import java.io.*;

/**
 *
 * @author Chuang Wu <chuangwu@cmu.edu>
 */
public class setCover {

    /** */
    Vector<Vector<Integer>> answers = null;

    /** */
    int minNumOfSets;

    /**
     * 
     */
    public setCover() {
        answers = new Vector<Vector<Integer>>();
        minNumOfSets = 0;
    }

    /**
     * 
     * @param sets
     * @param spaceSize
     */
    public void exhaustiveSetCover(Vector<Vector<Integer>> sets, int spaceSize) {
        setCoverAlgo SCA = new setCoverAlgo();


        if (SCA.isSpaceCovered(sets, spaceSize)) {
            Vector<Vector<Integer>> setIndexOrder;
            setIndexOrder = SCA.sortDensityGetIndexOrder(sets, spaceSize);
            Vector<Integer> curSet = new Vector<Integer>();
            HashSet curCoverage = new HashSet(spaceSize);
            while (this.answers.size() == 0) {
                this.minNumOfSets++;
                int iter = 0;
                curSet.clear();
                curCoverage.clear();
                System.out.println("Learning Sets with number of: " + this.minNumOfSets);
                SCA.recursiveSetCover(sets, setIndexOrder, spaceSize, curSet,
                        curCoverage, iter, this.minNumOfSets, this.answers);
            }
        }

    }

    /**
     * 
     * @param dataName
     * @param sets
     */
    public void cvsReader(String dataName, Vector<Vector<Integer>> sets) {

        if (dataName.equals("Data2")) {
            dataName = "Data/setCoverData2.dat";
        }
        if (dataName.equals("Data3")) {
            dataName = "Data/setCoverData3.dat";
        }

        //Vector<Vector<Integer>> sets = new Vector<Vector<Integer>>();
        InputStream fstream = null;

        try {
            fstream = getClass().getResourceAsStream(dataName);
            if (fstream == null) {
                System.out.println("Config File not found!");
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
                String line = "";
                while ((line = in.readLine()) != null) {
                    String[] words = line.split(" ");
                    Vector<Integer> wordsIntVector = new Vector<Integer>();
                    for (int i = 0; i < words.length; ++i) {
                        wordsIntVector.add(Integer.parseInt(words[i]));
                    }
                    sets.add(wordsIntVector);
                }
            }
        } catch (Exception e) {
            System.out.println("Can't read the file.");
        } finally {
            try {
                if (fstream != null) {
                    fstream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //return sets;
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        String dataSet = "Data";
        setCover SC = new setCover();

        if (args.length > 0) {
            dataSet = args[0];
        }
        if (dataSet.equals("Data1")) {
            Vector<Vector<Integer>> sets = new Vector<Vector<Integer>>();
            int spaceSize = 5;
            Vector<Integer> v1 = new Vector<Integer>();
            v1.add(1);
            v1.add(2);
            sets.add((Vector<Integer>) v1.clone());
            v1.clear();
            v1.add(1);
            v1.add(3);
            sets.add((Vector<Integer>) v1.clone());
            v1.clear();
            v1.add(1);
            v1.add(2);
            v1.add(4);
            sets.add((Vector<Integer>) v1.clone());
            v1.clear();
            v1.add(4);
            v1.add(5);
            sets.add((Vector<Integer>) v1.clone());

            long now = System.currentTimeMillis();

            for (int i = 0; i < 100000; ++i) {
                SC.exhaustiveSetCover(sets, spaceSize);
            }
            long now2 = System.currentTimeMillis();
            now2 -= now;
            System.out.println("Time used: " + now2 + "ms");
            System.out.println(SC.minNumOfSets);
            for (int i = 0; i < SC.answers.size(); ++i) {
                for (int j = 0; j < SC.answers.get(i).size(); ++j) {
                    System.out.print(SC.answers.get(i).get(j) + "\t");
                }
                System.out.println();
            }
        }
        if (dataSet.equals("Data2")) {
            Vector<Vector<Integer>> sets = new Vector<Vector<Integer>>();
            SC.cvsReader(dataSet, sets);
            int spaceSize = 10;
            long now = System.currentTimeMillis();
            //System.out.println(now);
            for (int i = 0; i < 100; ++i) {
                SC.exhaustiveSetCover(sets, spaceSize);
            }
            long now2 = System.currentTimeMillis();
            now2 -= now;
            System.out.println("Time used: " + now2 + "ms");
            System.out.println(SC.minNumOfSets);
            for (int i = 0; i < SC.answers.size(); ++i) {
                for (int j = 0; j < SC.answers.get(i).size(); ++j) {
                    System.out.print(SC.answers.get(i).get(j) + "\t");
                }
                System.out.println();
            }
        }
        if (dataSet.equals("Data3")) {
            Vector<Vector<Integer>> sets = new Vector<Vector<Integer>>();
            SC.cvsReader(dataSet, sets);
            int spaceSize = 100;
            long now = System.currentTimeMillis();
            //System.out.println(sets.size());
            for (int i = 0; i < 1; ++i) {
                SC.exhaustiveSetCover(sets, spaceSize);
            }
            long now2 = System.currentTimeMillis();
            now2 -= now;
            System.out.println("Time used: " + now2 + "ms");
            System.out.println(SC.minNumOfSets);
            for (int i = 0; i < SC.answers.size(); ++i) {
                for (int j = 0; j < SC.answers.get(i).size(); ++j) {
                    System.out.print(SC.answers.get(i).get(j) + "\t");
                }
                System.out.println();
            }
        }
    }
}


