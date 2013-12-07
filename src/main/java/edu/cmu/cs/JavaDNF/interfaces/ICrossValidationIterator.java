
package edu.cmu.cs.JavaDNF.interfaces;

import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;
import java.util.Iterator;

/**
 *
 * @author cc
 */
public interface ICrossValidationIterator {

    /**
     * 
     * @return
     */
    int getFoldNumber();

    /**
     * 
     * @return
     */
    Iterator<TrainTestDataClass> iterator();

    /**
     *
     * @return
     */
    boolean hasNext();

    /**
     *
     * @return
     */
    TrainTestDataClass next() ;

    /**
     *
     */
    void remove();
}
