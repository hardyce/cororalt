/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys;

import ie.tcd.cs.nembes.microjenaenh.graph.Graph;
import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import ie.tcd.cs.nembes.microjenaenh.reasoner.Finder;

/**
 * Implementors of this interface can be used as proprocessing passes
 * during intialization of (hybrid) rule systems. They are typically
 * used to generate additional data-dependent rules or additional
 * deductions (normally from comprehension axioms) which are cheaper
 * this way than using the generic rule engines.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.9 $ on $Date: 2008/01/02 12:07:47 $
 */
public interface RulePreprocessHook_deprecated {

    /**
     * Invoke the preprocessing hook. This will be called during the
     * preparation time of the hybrid reasoner.
     * @param infGraph the inference graph which is being prepared,
     * the hook code can use this to add pure deductions or add additional
     * rules (using addRuleDuringPrepare).
     * @param dataFind the finder which packages up the raw data (both
     * schema and data bind) and any cached transitive closures.
     * @param inserts a temporary graph into which the hook should insert
     * all new deductions that should be seen by the rules.
     */
    public void run(BasicForwardRuleInfGraph infGraph, Finder dataFind, Graph inserts);
    
    /**
     * Validate a triple add to see if it should reinvoke the hook. If so
     * then the inference will be restarted at next prepare time. Incremental
     * re-processing is not yet supported.
     */
    public boolean needsRerun(BasicForwardRuleInfGraph infGraph, Triple t);
    
}

