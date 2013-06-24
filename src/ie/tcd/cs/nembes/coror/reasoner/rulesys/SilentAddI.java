package ie.tcd.cs.nembes.coror.reasoner.rulesys;

import ie.tcd.cs.nembes.coror.graph.Triple;



/**
 * Interface supported by each of the rule system interpreters that
 * allow triples to added directly to the deductions cache, by-passing
 * any processing machinery.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.7 $ on $Date: 2008/01/02 12:07:47 $
 */
public interface SilentAddI {
    
    /**
     * Assert a new triple in the deduction graph, bypassing any processing machinery.
     */
    public void silentAdd(Triple t);

}
