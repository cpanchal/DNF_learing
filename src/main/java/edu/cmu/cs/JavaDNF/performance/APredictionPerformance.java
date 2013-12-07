package edu.cmu.cs.JavaDNF.performance;

import edu.cmu.cs.JavaDNF.interfaces.ICrossValidationIterator;
import edu.cmu.cs.JavaDNF.interfaces.IPredictionPerformance;
import edu.cmu.cs.JavaDNF.lib.DNFList;
import edu.cmu.cs.JavaDNF.lib.TrainTestDataClass;

/**
 * 
 * @author cc
 */
public abstract class APredictionPerformance implements IPredictionPerformance {

	public void learn() {

	}

	public void predict() {

	}

	public void perform(ICrossValidationIterator acvi) {
		int error = 0;
		while (acvi.hasNext()) {
			TrainTestDataClass ttdc = acvi.next();
			error += this.perform(ttdc);
		}
	}

	/**
	 * 
	 * @param ttdc
	 */
	public int perform(TrainTestDataClass ttdc) {
		DNFList dnfList = this.learn(ttdc);
		return this.predict(ttdc, dnfList);
	}

	/**
	 * 
	 * @param ttdc
	 * @return
	 */
	abstract public DNFList learn(TrainTestDataClass ttdc);

	/**
	 * 
	 * @param ttdc
	 * @return
	 */
	abstract public int predict(TrainTestDataClass ttdc, DNFList dNFList);
}
