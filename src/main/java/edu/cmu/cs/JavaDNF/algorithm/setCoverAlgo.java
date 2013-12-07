package edu.cmu.cs.JavaDNF.algorithm;

/**
 *
 * @author Chuang Wu <chuangwu@cmu.edu>
 */
import java.util.Vector;
import java.util.HashSet;

public class setCoverAlgo {

    /** */
    Vector<Vector<Integer>> sets;

    /** */
    int spaceSize;

    /**
     *
     */
    public setCoverAlgo() {
    }

    /**
     *
     * @param sets
     * @param setIndexOrder
     * @param spaceSize
     * @param curSet
     * @param curCoverage
     * @param iter
     * @param minNumOfSets
     * @param answers
     */
    public void recursiveSetCover(Vector<Vector<Integer>> sets,
            Vector<Vector<Integer>> setIndexOrder, int spaceSize,
            Vector<Integer> curSet, HashSet curCoverage,
            int iter, int minNumOfSets, Vector<Vector<Integer>> answers) {

        Vector<Integer> curSetIndex = setIndexOrder.get(iter);
        for (int i = 0; i < curSetIndex.size(); ++i) {
            if (!curSet.contains(curSetIndex.get(i))) {
                HashSet curCoverageNew = new HashSet(curCoverage);
                curCoverageNew.addAll(sets.get(curSetIndex.get(i)));
                if (curCoverageNew.size() == spaceSize) {
                    curSet.add(curSetIndex.get(i));
                    answers.add((Vector<Integer>) curSet.clone());
                    curSet.remove(curSetIndex.get(i));
                } else {
                    if (curSet.size() < minNumOfSets - 1) {
                        if (iter < setIndexOrder.size() - 1) {
                            curSet.add(curSetIndex.get(i));
                            recursiveSetCover(sets, setIndexOrder, spaceSize, curSet, curCoverageNew,
                                    ++iter, minNumOfSets, answers);
                            curSet.remove(curSetIndex.get(i));
                        }
                    }
                }
            } else {
                if (iter < setIndexOrder.size() - 1) {
                    recursiveSetCover(sets, setIndexOrder, spaceSize, curSet, curCoverage,
                            ++iter, minNumOfSets, answers);
                }
            }
        }
    }

    /**
     * 
     * @param sets
     * @param spaceSize
     * @return
     */
    public Vector<Vector<Integer>> sortDensityGetIndexOrder(Vector<Vector<Integer>> sets, int spaceSize) {

        int[] density = new int[spaceSize];
        for (int i = 0; i < sets.size(); ++i) {
            for (int j = 0; j < sets.get(i).size(); ++j) {
                density[sets.get(i).get(j) - 1]++;
            }
        }
        int[] indexOrg = new int[spaceSize];
        for (int i = 0; i < spaceSize; ++i) {
            indexOrg[i] = i + 1;
        }
        int[] densityIndex = countingSort(density, indexOrg, spaceSize, sets.size() + 1, true);

        Vector<Vector<Integer>> setIndexOrder = new Vector<Vector<Integer>>(spaceSize);
        for (int i = 0; i < spaceSize; ++i) {
            if (density[densityIndex[i] - 1] > 0) {
                Vector<Integer> curIndex = new Vector<Integer>();
                for (int j = 0; j < sets.size(); ++j) {
                    if (sets.get(j).contains(densityIndex[i])) {
                        curIndex.add(j);
                    }
                }
                setIndexOrder.add(curIndex);
            }
        }
        return setIndexOrder;
    }

    /**
     * 
     * @param A
     * @param B
     * @param n
     * @param k
     * @param isAscending
     * @return
     */
    public int[] countingSort(int[] A, int[] B, int n, int k, Boolean isAscending) {
        int[] C = new int[k];
        for (int i = 0; i < k; ++i) {
            C[i] = 0;
        }
        int[] D = new int[n];
        for (int i = 0; i < n; ++i) {
            D[i] = 0;
        }
        for (int i = 0; i < n; ++i) {
            C[A[i]]++;
        }
        for (int i = 1; i < k; ++i) {
            C[i] += C[i - 1];
        }
        for (int i = n - 1; i > -1; --i) {
            D[C[A[i]] - 1] = i;
            C[A[i]]--;
        }
        if (isAscending == false) {
            int[] D_r = new int[n];
            for (int i = 0; i < n; ++i) {
                D_r[i] = D[n - i];
                D = D_r;
            }
        }
        int[] E = new int[n];
        for (int i = 0; i < n; ++i) {
            E[i] = B[D[i]];
        }
        return E;
    }

    /**
     * 
     * @param sets
     * @param spaceSize
     * @return
     */
    public Boolean isSpaceCovered(Vector<Vector<Integer>> sets, int spaceSize) {
        HashSet allSet = new HashSet();
        for (int i = 0; i < sets.size(); ++i) {
            allSet.addAll(sets.get(i));
        }
        if (allSet.size() == spaceSize) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
    }
}
