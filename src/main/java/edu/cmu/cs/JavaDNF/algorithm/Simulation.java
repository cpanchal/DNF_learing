/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.JavaDNF.algorithm;

import edu.cmu.cs.JavaDNF.interfaces.ITerm;
import edu.cmu.cs.JavaDNF.lib.TargetFunction;
import edu.cmu.cs.JavaDNF.lib.DNF;
import edu.cmu.cs.JavaDNF.lib.CNF;
import edu.cmu.cs.JavaDNF.lib.Literal;
import edu.cmu.cs.compbio.io.FastaWriter;
import java.util.Vector;
import java.util.Random;
import java.io.File;
import edu.cmu.cs.compbio.lib.*;
import java.util.Collections;

/**
 *
 * @author Chuang
 */
public class Simulation {

    /** */
    public GenotypeSequences genotypeSequences = null;
    /** */
    private Random randGenerator = null;

    /**
     * 
     */
    public Simulation() {
        genotypeSequences = new GenotypeSequences();
        randGenerator = new Random(System.currentTimeMillis());
    }

    /**
     * 
     * @param genotypeSeq
     * @param targetFunction
     * @param numberPositive
     * @param numberNegative
     */
    public void generateSimulation(GenotypeSequences genotypeSeq, TargetFunction targetFunction, int numberPositive, int numberNegative) {
        this.genotypeSequences.clear();
        //Vector<Sequence> genotypesPos = new Vector<Sequence>();
        for (int i = 0; i < numberPositive; ++i) {
            this.genotypeSequences.add(0, this.generateOneSimulationSequence(genotypeSeq, targetFunction, true));
        }
        //Vector<Sequence> genotypesNeg = new Vector<Sequence>();
        for (int i = 0; i < numberNegative; ++i) {
            this.genotypeSequences.add(1, this.generateOneSimulationSequence(genotypeSeq, targetFunction, false));
            //genotypesNeg.add(this.generateOneSimulationSequence(genotypeSeq, targetFunction, false));
        }
        this.genotypeSequences.dataName = "Simulation";
    }

    /**
     * 
     * @param genotypeSeq
     * @param dnf
     * @param numberPositive
     * @param numberNegative
     */
    public void generateSimulation(GenotypeSequences genotypeSeq, DNF dnf, int numberPositive, int numberNegative) {
        this.genotypeSequences.clear();
        //Vector<Sequence> genotypesPos = new Vector<Sequence>();
        for (int i = 0; i < numberPositive; ++i) {
            this.genotypeSequences.add(0, this.generateOneSimulationSequence(genotypeSeq, dnf, true));
            //genotypesPos.add(this.generateOneSimulationSequence(genotypeSeq, dnf, true));
        }
        //Vector<Sequence> genotypesNeg = new Vector<Sequence>();
        for (int i = 0; i < numberNegative; ++i) {
            this.genotypeSequences.add(1, this.generateOneSimulationSequence(genotypeSeq, dnf, false));
            //genotypesNeg.add(this.generateOneSimulationSequence(genotypeSeq, dnf, false));
        }
        this.genotypeSequences.dataName = "Simulation";
    }

    /**
     * 
     * @param genotypeSeq
     * @param cnf
     * @param numberPositive
     * @param numberNegative
     */
    public void generateSimulation(GenotypeSequences genotypeSeq, CNF cnf, int numberPositive, int numberNegative) {
        this.genotypeSequences.clear();
        //Vector<Sequence> genotypesPos = new Vector<Sequence>();
        for (int i = 0; i < numberPositive; ++i) {
            this.genotypeSequences.add(0, this.generateOneSimulationSequence(genotypeSeq, cnf, true));
            //genotypesPos.add(this.generateOneSimulationSequence(genotypeSeq, cnf, true));
        }
        //Vector<Sequence> genotypesNeg = new Vector<Sequence>();
        for (int i = 0; i < numberNegative; ++i) {
            this.genotypeSequences.add(1, this.generateOneSimulationSequence(genotypeSeq, cnf, false));
            //genotypesNeg.add(this.generateOneSimulationSequence(genotypeSeq, cnf, false));
        }
        this.genotypeSequences.dataName = "Simulation";
    }

    /**
     * 
     * @param genotypeSeq
     * @param targetFunction
     * @param isPositive
     * @return
     */
    public Sequence generateOneSimulationSequence(GenotypeSequences genotypeSeq, TargetFunction targetFunction, boolean isPositive) {
        StringBuilder strBuilder = new StringBuilder();
        int randomIndex;

        // Fill in sequence items randomly
        for (int i = 0; i < genotypeSeq.sequenceLength; ++i) {
            Sequence seqSampled = this.sampleOneSequence(genotypeSeq);
            strBuilder.append(seqSampled.sequence.charAt(i));
        }

        // Set phenotype
        if (isPositive) {
            for (int i = 0; i < targetFunction.positions.size(); ++i) {
                if (targetFunction.sequenceItems.get(i) == strBuilder.charAt(targetFunction.positions.get(i))) {
                    return new Sequence(strBuilder);
                }
            }
            randomIndex = randGenerator.nextInt(targetFunction.positions.size());
            strBuilder.setCharAt(targetFunction.positions.get(randomIndex), targetFunction.sequenceItems.get(randomIndex));
            return new Sequence(strBuilder);
        } else {
            for (int i = 0; i < targetFunction.positions.size(); ++i) {
                int position = targetFunction.positions.get(i);
                char item = targetFunction.sequenceItems.get(i);
                while (item == strBuilder.charAt(position)) {
                    Sequence seqSampled = this.sampleOneSequence(genotypeSeq);
                    strBuilder.setCharAt(position, seqSampled.sequence.charAt(position));
                }
            }
            return new Sequence(strBuilder);
        }
    }

    /**
     * 
     * @param genotypeSeq
     * @param dnf
     * @param isPositive
     * @return
     */
    public Sequence generateOneSimulationSequence(GenotypeSequences genotypeSeq, DNF dnf, boolean isPositive) {
        StringBuilder strBuilder = new StringBuilder();
        int randomIndex;

        // Fill in sequence items randomly
        for (int i = 0; i < genotypeSeq.sequenceLength; ++i) {
            Sequence seqSampled = this.sampleOneSequence(genotypeSeq);
            strBuilder.append(seqSampled.sequence.charAt(i));
        }

        // Set phenotype
        if (isPositive) {
            for (int i = 0; i < dnf.getNumberOfITerms(); ++i) {
                if (dnf.getITerms().get(i).isConsistent(strBuilder)) {
                    return new Sequence(strBuilder);
                }
            }
            randomIndex = randGenerator.nextInt(dnf.getNumberOfITerms());
            for (int j = 0; j < dnf.getITerms().get(randomIndex).getNumberOfITerms(); ++j) {
                strBuilder.setCharAt(dnf.getITerms().get(randomIndex).getITerms().get(j).getPosition(), dnf.getITerms().get(randomIndex).getITerms().get(j).getSequenceIterm());
            }
            return new Sequence(strBuilder);
        } else {
            for (int i = 0; i < dnf.getNumberOfITerms(); ++i) {
                while (dnf.getITerms().get(i).isConsistent(strBuilder)) {
                    for (int j = 0; j < dnf.getITerms().get(i).getNumberOfITerms(); ++j) {
                        Sequence sampledSeq = this.sampleOneSequence(genotypeSeq);
                        int position = dnf.getITerms().get(i).getITerms().get(j).getPosition();
                        strBuilder.setCharAt(position, sampledSeq.sequence.charAt(position));
                    }
                }
            }
            return new Sequence(strBuilder);
        }
    }

    /**
     * 
     * @param genotypeSeq
     * @param cnf
     * @param isPositive
     * @return
     */
    public Sequence generateOneSimulationSequence(GenotypeSequences genotypeSeq, CNF cnf, boolean isPositive) {
        StringBuilder strBuilder = new StringBuilder();
        int randomIndex;

        // Fill in sequence items randomly
        for (int i = 0; i < genotypeSeq.sequenceLength; ++i) {
            Sequence seqSampled = this.sampleOneSequence(genotypeSeq);
            strBuilder.append(seqSampled.sequence.charAt(i));
        }

        // Set phenotype
        if (isPositive) {
            for (int i = 0; i < cnf.getNumberOfITerms(); ++i) {

                while (!cnf.getITerms().get(i).isConsistent(strBuilder)) {
                    randomIndex = randGenerator.nextInt(cnf.getITerms().get(i).getNumberOfITerms());
                    strBuilder.setCharAt(cnf.getITerms().get(i).getITerms().get(randomIndex).getPosition(), cnf.getITerms().get(i).getITerms().get(randomIndex).getSequenceIterm());
                }
            }
            return new Sequence(strBuilder);
        } else {
            randomIndex = randGenerator.nextInt(cnf.getNumberOfITerms());
            while (cnf.getITerms().get(randomIndex).isConsistent(strBuilder)) {
                for (int j = 0; j < cnf.getITerms().get(randomIndex).getNumberOfITerms(); ++j) {
                    Sequence sampledSeq = this.sampleOneSequence(genotypeSeq);
                    int position = cnf.getITerms().get(randomIndex).getITerms().get(j).getPosition();
                    strBuilder.setCharAt(position, sampledSeq.sequence.charAt(position));
                }
            }
            return new Sequence(strBuilder);
        }
    }

    /**
     * 
     * @param genotypeSeq
     * @return
     */
    public Sequence sampleOneSequence(GenotypeSequences genotypeSeq) {
        int randomIndex = randGenerator.nextInt(genotypeSeq.totalNumber);
        if (randomIndex >= genotypeSeq.sequenceNumbers[0]) {
            randomIndex -= genotypeSeq.sequenceNumbers[0];
            return genotypeSeq.get(1, randomIndex);
        } else {
            return genotypeSeq.get(0, randomIndex);
        }
    }

    /**
     * 
     * @param ls
     * @param numberOfLiteralsInEachTerm
     * @return
     */
    public DNF randomlyGenerateDNF(Vector<Literal> ls, int[] numberOfLiteralsInEachTerm) {
        Collections.shuffle(ls);
        int index = 0;
        Vector<ITerm> cnfs = new Vector<ITerm>();
        for (int i = 0; i < numberOfLiteralsInEachTerm.length; ++i) {
            Vector<ITerm> vectorL = new Vector<ITerm>();
            for (int j = 0; j < numberOfLiteralsInEachTerm[i]; ++j) {
                vectorL.add(ls.get(index));
                index++;
            }
            cnfs.add(new CNF(vectorL));
        }
        return new DNF(cnfs);
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        String dataName = "", dataSet = "";
        if (args.length > 1) {
            dataName = args[1];
        }
        if (args.length > 0) {
            dataSet = args[0];
        }
        dataSet = "/Users/Chuang/Work/Research/Data/HIV_Stanford";
        dataName = "APV_New";


        GenotypeSequences genotypeSequences = new GenotypeSequences(dataSet, dataName);
        System.out.println("Entropy is:");
        float[] entropy = GenotypeSequences.calculateEntropy(genotypeSequences);
        for (int i = 0; i < entropy.length; ++i) {
            System.out.print(i + "-" + entropy[i] + "\t");
        }
        System.out.println();

        Simulation simulation = new Simulation();
        Vector<Literal> ls = new Vector<Literal>(10);
        ls.add(new Literal(9, 'L'));
        ls.add(new Literal(81, 'A'));
        ls.add(new Literal(36, 'N'));
        ls.add(new Literal(70, 'V'));
        ls.add(new Literal(71, 'I'));
        ls.add(new Literal(19, 'K'));
        ls.add(new Literal(35, 'M'));
        ls.add(new Literal(45, 'I'));
        ls.add(new Literal(53, 'V'));
        ls.add(new Literal(63, 'I'));


        DNF dnf = new DNF();

        int[] numberOfTerm = new int[]{2, 3, 4, 5};

        int T = 20;
        //String newDataName = "/home/cc/Work/Research/Data/Simulation" + File.separatorChar + dataName + "_Simulation";
        //String newDNFName = "/home/cc/Work/Research/Data/Simulation" + File.separatorChar + dataName + "_DNF";
        FastaWriter writer = new FastaWriter();
        for (int n = 0; n < numberOfTerm.length; ++n) {
            int[] numberOfLiteralsInEacTerm = new int[2];
            numberOfLiteralsInEacTerm[0] = numberOfTerm[n] / 2;
            numberOfLiteralsInEacTerm[1] = numberOfTerm[n] - numberOfLiteralsInEacTerm[0];
            for (int t = 0; t < T; ++t) {
                //Collections.shuffle(ls);
                dnf = simulation.randomlyGenerateDNF(ls, numberOfLiteralsInEacTerm);
                simulation = new Simulation();
                simulation.generateSimulation(genotypeSequences, dnf, 500, 500);
                for (int i = 0; i < simulation.genotypeSequences.genotypes.size(); ++i) {
                    for (int j = 0; j < simulation.genotypeSequences.sequenceNumbers[i]; ++j) {
                        System.out.println("Consistent: " + j + " : " + dnf.isConsistent(simulation.genotypeSequences.get(i, j)));
                    }
                }
                String newDataNameA = "~/Work/Research/Data/Simulation" + File.separatorChar + dataName + "_Simulation_" + n + "_" + t;
                String newDNFNameA = "~/Work/Research/Data/Simulation" + File.separatorChar + dataName + "_DNF_" + n + "_" + t;
                String newFeaturesNameA = "~/Work/Research/Data/Simulation" + File.separatorChar + dataName + "_features_" + n + "_" + t;

                writer.write(simulation.genotypeSequences.get(0), newDataNameA + "_Pos.fasta");
                writer.write(simulation.genotypeSequences.get(1), newDataNameA + "_Neg.fasta");
                writer.write(dnf.ToString(), newDNFNameA);
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < dnf.getITerms().size(); ++i) {
                    for (int  j = 0; j < dnf.getITerms().get(i).getITerms().size(); ++j) {
                        s.append(dnf.getITerms().get(i).getITerms().get(j).getPosition() + "\n");
                    }
                }
                writer.write(s.toString(), newFeaturesNameA);
            }
        }

    }
}
