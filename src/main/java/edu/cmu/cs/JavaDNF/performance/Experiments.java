package edu.cmu.cs.JavaDNF.performance;

import edu.cmu.cs.JavaDNF.algorithm.LearnLiteralsFromGS;
import edu.cmu.cs.JavaDNF.algorithm.LearnCNFClauses;
import edu.cmu.cs.JavaDNF.algorithm.LearnDNF;
import edu.cmu.cs.JavaDNF.lib.AbstractTerm;
import edu.cmu.cs.compbio.lib.*;
import java.util.Vector;
import java.io.*;

/**
 * Experiments as functions
 * @author cc
 */
public class Experiments {

    /** */
    public String dataSet, dataName;
    
    /**
     * 
     */
    public Experiments() {
    }

    /**
     * 
     * @param dataSet
     * @param dataName
     */
    public Experiments(String dataSet, String dataName) {
        this.dataSet = dataSet;
        this.dataName = dataName;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dataName = "", dataSet = "";
        if (args.length > 1) {
            dataName = args[1];
        }
        if (args.length > 0) {
            dataSet = args[0];
        }

        Experiments exp = new Experiments(dataSet, dataName);
        //exp.testCreateDNFonTest();
        //exp.testCreateDNFonOneSimulation();
        //exp.testCreateDNFonSimulations();
        //exp.testLeaveOneOutonSimulations();
    }

    /**
     * 
     */
    public void testCreateDNFonTest() {
        String dataSetA = "/home/cc/Work/Research/Data/Test";
        String dataNameA = "test";
        this.testCreateDNFOnDataSet(dataSetA, dataNameA);
    }

    /**
     *
     */
    public void testCreateDNFonOneSimulation() {
        String dataSetA = "/home/cc/Work/Research/Data/Simulation";
        String dataNameA = "APV_New_Simulation_0";
        Vector<Integer> keyResidues = new Vector<Integer>();
        keyResidues.add(9);
        keyResidues.add(81);
        keyResidues.add(36);
        keyResidues.add(70);
        keyResidues.add(71);
        this.testCreateDNFOnDataSetWithKeyResidues(dataSetA, dataNameA, keyResidues);
    }

    /**
     * 
     */
    public void testCreateDNFonSimulations() {
        String dataSetA = "/home/cc/Work/Research/Data/Simulation";
        String dataNameA = "";
        AbstractTerm.IS_VERBOSE = true;
        int T = 20, I = 4;
        long timeStart, timeEnd;
        long memoryStart, memoryEnd;
        int[] numberOfSeqs = {1, 5, 10, 20, 50, 100, 200};
        GenotypeSequences gs = new GenotypeSequences();

        int length = numberOfSeqs.length;
        double[][][] numberOfLiterals = new double[5][length][length];
        double[][][] numberOfCNFClauses = new double[5][length][length];
        double[][][] numberOfDNFs = new double[5][length][length];
        double[][][] runningTime = new double[5][length][length];
        double[][][] memoryUsage = new double[5][length][length];
        for (int i = 0; i < I; ++i) {
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {

                    for (int t = 0; t < T; ++t) {
                        dataNameA = "APV_New_Simulation_" + i + "_" + t;
                        System.out.println("Analyzing " + dataNameA + ": pos" + numberOfSeqs[a1] + " neg" + numberOfSeqs[a2]);
                        gs = new GenotypeSequences(dataSetA, dataNameA);
                        gs.setAvailableNumbers(numberOfSeqs[a1], numberOfSeqs[a2]);

                        // Read Features
                        Vector<Integer> keyResidues = new Vector<Integer>();
                        String dataFeaturesA = dataSetA + File.separator + "APV_New_features_" + i + "_" + t;
                        try {
                            InputStream fstream = new FileInputStream(dataFeaturesA);
                            BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
                            String inputLine = "";
                            while ((inputLine = in.readLine()) != null) {
                                keyResidues.add(Integer.parseInt(inputLine.trim()));
                            }
                            in.close();
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                        // Start experiments
                        timeStart = System.currentTimeMillis();
                        Runtime.getRuntime().gc();
                        memoryStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                        LearnLiteralsFromGS clgs = new LearnLiteralsFromGS(gs, keyResidues);
                        LearnCNFClauses ccc = new LearnCNFClauses(clgs);
                        LearnDNF cd = new LearnDNF(ccc);
                        
                        timeEnd = System.currentTimeMillis();
                        memoryEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        Runtime.getRuntime().gc();
                        // End experiments

                        System.out.println(clgs.toString());
                        System.out.println(ccc.toString());
                        System.out.println(cd.toString());
                        numberOfLiterals[i][a1][a2] += clgs.numberOfLiterals;
                        numberOfCNFClauses[i][a1][a2] += ccc.orgCNFClauseList.size();
                        numberOfDNFs[i][a1][a2] += cd.dnfList.getNumberOfDNFs();
                        runningTime[i][a1][a2] += timeEnd - timeStart;
                        memoryUsage[i][a1][a2] += memoryEnd - memoryStart;
                    }
                }
            }
        }
        for (int i = 0; i < I; ++i) {
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    numberOfLiterals[i][a1][a2] /= T;
                    numberOfCNFClauses[i][a1][a2] /= T;
                    numberOfDNFs[i][a1][a2] /= T;
                    runningTime[i][a1][a2] /= T * 1000;
                    memoryUsage[i][a1][a2] /= T * 1e6;
                }
            }
        }

        for (int i = 0; i < I; ++i) {
            System.out.println("==== Number of Key Residues: " + (i + 2) + " ========");
            System.out.println("--- number of Literals ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(numberOfLiterals[i][a1][a2] + " ");
                }
                System.out.println();
            }
            System.out.println("--- number of CNFClauses ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(numberOfCNFClauses[i][a1][a2] + " ");
                }
                System.out.println();
            }
            System.out.println("--- number of DNFs ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(numberOfDNFs[i][a1][a2] + " ");
                }
                System.out.println();
            }
            System.out.println("--- running Time (seconds) ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(runningTime[i][a1][a2] + " ");
                }
                System.out.println();
            }
            System.out.println("--- memory usage (Mega) ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(memoryUsage[i][a1][a2] + " ");
                }
                System.out.println();
            }
        }

        
    }

    /**
     *
     */
    /*
    public void testLeaveOneOutonSimulations() {
        AbstractTerm.IS_VERBOSE = true;
        String dataSetA = "/home/cc/Work/Research/Data/Simulation";
        String dataNameA = "";
        int T = 10, I = 4;
        long timeStart, timeEnd;
        long memoryStart, memoryEnd;
        int[] numberOfSeqs = {2, 5, 10, 20, 50, 100};
        GenotypeSequences gs = new GenotypeSequences();
        
        int length = numberOfSeqs.length;
        double[][][] sensitivity = new double[5][length][length];
        double[][][] specificity = new double[5][length][length];
        double[][][] runningTime = new double[5][length][length];
        double[][][] memoryUsage = new double[5][length][length];
        for (int i = 0; i < I; ++i) {
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {

                    for (int t = 0; t < T; ++t) {
                        dataNameA = "APV_New_Simulation_" + i + "_" + t;
                        System.out.println("Analyzing " + dataNameA + ": pos" + numberOfSeqs[a1] + " neg" + numberOfSeqs[a2]);
                        gs = new GenotypeSequences(dataSetA, dataNameA);
                        gs.setAvailableNumbers(numberOfSeqs[a1], numberOfSeqs[a2]);

                        // Read Features
                        Vector<Integer> keyResidues = new Vector<Integer>();
                        String dataFeaturesA = dataSetA + File.separator + "APV_New_features_" + i + "_" + t;
                        try {
                            InputStream fstream = new FileInputStream(dataFeaturesA);
                            BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
                            String inputLine = "";
                            while ((inputLine = in.readLine()) != null) {
                                keyResidues.add(Integer.parseInt(inputLine.trim()));
                            }
                            in.close();
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                        // Starts Experiment
                        timeStart = System.currentTimeMillis();
                        Runtime.getRuntime().gc();
                        memoryStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                        LeaveOneOutIterator lood = new LeaveOneOutIterator(gs, keyResidues);
                        //lood.leaveOneOut();
                        //Utils.debugln(lood.sensitivity + " " + lood.specificity);

                        timeEnd = System.currentTimeMillis();
                        memoryEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        Runtime.getRuntime().gc();
                        // End Experiment

                        sensitivity[i][a1][a2] += lood.sensitivity;
                        specificity[i][a1][a2] += lood.specificity;
                        runningTime[i][a1][a2] += timeEnd - timeStart;
                        memoryUsage[i][a1][a2] += memoryEnd - memoryStart;
                    }
                }
            }
        }
        for (int i = 0; i < I; ++i) {
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    sensitivity[i][a1][a2] /= T;
                    specificity[i][a1][a2] /= T;
                    runningTime[i][a1][a2] /= T * 1000;
                    memoryUsage[i][a1][a2] /= T * 1e6;
                }
            }
        }

        for (int i = 0; i < I; ++i) {
            System.out.println("==== Number of Key Residues: " + (i + 2) + " ========");
            System.out.println("--- sensitivity ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(sensitivity[i][a1][a2] + " ");
                }
                System.out.println();
            }
            System.out.println("--- specificity ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(specificity[i][a1][a2] + " ");
                }
                System.out.println();
            }
            System.out.println("--- running Time (seconds) ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(runningTime[i][a1][a2] + " ");
                }
                System.out.println();
            }
            System.out.println("--- memory usage (Mega) ---");
            for (int a1 = 0; a1 < length; ++a1) {
                for (int a2 = 0; a2 < length; ++a2) {
                    System.out.print(memoryUsage[i][a1][a2] + " ");
                }
                System.out.println();
            }
        }

    }
     * 
     */


    /**
     * 
     * @param dataSet
     * @param dataName
     */
    public void testCreateDNFOnDataSet(String dataSet, String dataName) {
        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
        gs.sequenceItems = Sequence.PROTEIN_ITERMS;
        AbstractTerm.IS_VERBOSE = true;

        LearnLiteralsFromGS clgs = new LearnLiteralsFromGS(gs);
        System.out.println(clgs.toString());
        LearnCNFClauses ccc = new LearnCNFClauses(clgs);
        System.out.println(ccc.toString());
        LearnDNF cd = new LearnDNF(ccc);
        System.out.println(cd.toString());
    }

    /**
     * 
     * @param dataSet
     * @param dataName
     * @param keyResidues
     */
    public void testCreateDNFOnDataSetWithKeyResidues(String dataSet, String dataName, Vector<Integer> keyResidues) {
        GenotypeSequences gs = new GenotypeSequences(dataSet, dataName);
        gs.sequenceItems = Sequence.PROTEIN_ITERMS;
        AbstractTerm.IS_VERBOSE = true;

        LearnLiteralsFromGS clgs = new LearnLiteralsFromGS(gs, keyResidues);
        System.out.println(clgs.toString());
        LearnCNFClauses ccc = new LearnCNFClauses(clgs);
        System.out.println(ccc.toString());
        LearnDNF cd = new LearnDNF(ccc);
        System.out.println(cd.toString());
    }

    /**
     * 
     * @param gs
     */
    public void testCreateDNFOnDataSet(GenotypeSequences gs) {
        gs.sequenceItems = Sequence.PROTEIN_ITERMS;
        AbstractTerm.IS_VERBOSE = true;

        LearnLiteralsFromGS clgs = new LearnLiteralsFromGS(gs);
        System.out.println(clgs.toString());
        LearnCNFClauses ccc = new LearnCNFClauses(clgs);
        System.out.println(ccc.toString());
        LearnDNF cd = new LearnDNF(ccc);
        System.out.println(cd.toString());
    }

    /**
     * 
     * @param gs
     * @param keyResidues
     */
    public void testCreateDNFOnDataSetWithKeyResidues(GenotypeSequences gs, Vector<Integer> keyResidues) {
        gs.sequenceItems = Sequence.PROTEIN_ITERMS;
        AbstractTerm.IS_VERBOSE = true;

        LearnLiteralsFromGS clgs = new LearnLiteralsFromGS(gs, keyResidues);
        System.out.println(clgs.toString());
        LearnCNFClauses ccc = new LearnCNFClauses(clgs);
        System.out.println(ccc.toString());
        LearnDNF cd = new LearnDNF(ccc);
        System.out.println(cd.toString());
    }

}
