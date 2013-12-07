/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.JavaDNF.interfaces;

import edu.cmu.cs.JavaDNF.lib.DNFList;
import edu.cmu.cs.compbio.lib.GenotypeSequences;
import edu.cmu.cs.compbio.lib.IDataSequence;

/**
 * Learn all the DNFs without any processing.
 * @author Chris Wu
 */
public interface ILearnDNF {

    /**
     * 
     * @param gs
     * @return
     */
    void learn(IDataSequence gs) ;

    /**
     * 
     * @return
     */
    DNFList getDNFs();

    /**
     * 
     * @return
     */
    String ToString();
}
