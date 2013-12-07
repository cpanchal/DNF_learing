package edu.cmu.cs.JavaDNF.performance;

import edu.cmu.cs.JavaDNF.interfaces.ICrossValidationIterator;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import edu.cmu.cs.compbio.lib.IDataSequence;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author cc
 */
public abstract class ACrossValidationIterator implements ICrossValidationIterator, Iterator<TrainTestDataClass>, Iterable<TrainTestDataClass> {

    /** */
    protected IDataSequence gs;
    protected int foldIndex;
    protected int totalFoldNumber;

    /**
     * 
     * @param gs
     * @param foldNumber
     */
    public ACrossValidationIterator(IDataSequence gs, int foldNumber) {
        this.gs = gs;
        foldIndex = 0;
        totalFoldNumber = foldNumber;
    }

    /**
     *
     * @return
     */
    public Iterator<TrainTestDataClass> iterator() {
        return this;
    }

    /**
     *
     * @return
     */
    public boolean hasNext() {
        return foldIndex < totalFoldNumber;
    }

    /**
     *
     * @return
     */
    protected abstract TrainTestDataClass createFromIndex();

    /**
     *
     * @return
     */
    public TrainTestDataClass next() {
        if (foldIndex > totalFoldNumber) {
            throw new NoSuchElementException("End of iterator");
        }
        TrainTestDataClass result = createFromIndex();
        foldIndex++;
        return result;
    }

    /**
     *
     */
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * 
     * @return
     */
    public int getFoldNumber() {
        return totalFoldNumber;
    }
}
