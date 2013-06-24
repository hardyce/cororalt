package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.reasoner.Finder;

//import java.util.List;

/**
 * Rule engines implement the internals of forward rule inference
 * graphs and the forward part of hybrid graphs. This interface
 * abstracts the interface onto such engines to allow a graph to
 * switch between direct and RETE style implementations.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.9 $ on $Date: 2008/01/02 12:06:15 $
 */
public interface FRuleEngineI {
    
    /**
     * Process all available data. This should be called once a deductions graph
     * has be prepared and loaded with any precomputed deductions. It will process
     * the rule axioms and all relevant existing exiting data entries.
     * @param ignoreBrules set to true if rules written in backward notation should be ignored
     * @param inserts the set of triples to be processed, normally this is the
     * raw data graph but may include additional deductions made by preprocessing hooks
     */
    public void init(boolean ignoreBrules, Finder inserts);
    
    /**
     * Process all available data. This version expects that all the axioms 
     * have already be preprocessed and the rules have been compiled
     * @param inserts the set of triples to be processed, normally this is the
     * raw data graph but may include additional deductions made by preprocessing hooks
     */
    public void fastInit(Finder inserts);
    
    /**
     * Add one triple to the data graph, run any rules triggered by
     * the new data item, recursively adding any generated triples.
     */
    public void add(Triple t);
    
    /**
     * Remove one triple to the data graph.
     * @return true if the effects could be correctly propagated or
     * false if not (in which case the entire engine should be restarted).
     */
    public boolean delete(Triple t);
    
    /**
     * Return the number of rules fired since this rule engine instance
     * was created and initialized
     */
    public long getNRulesFired();
    
    /**
     * Return true if the internal engine state means that tracing is worthwhile.
     * It will return false during the axiom bootstrap phase.
     */
//    public boolean shouldTrace_deprecated();

    /**
     * Set to true to enable derivation caching
     */
//    public void setDerivationLogging_deprecated(boolean recordDerivations);
    
    /**
     * Access the precomputed internal rule form. Used when precomputing the
     * internal axiom closures.
     */
    public Object getRuleStore();
    
    /**
     * Set the internal rule from from a precomputed state.
     */
    public void setRuleStore(Object ruleStore);
    
    /**
     * Compile a list of rules into the internal rule store representation.
     * @param rules the list of Rule objects
     * @param ignoreBrules set to true if rules written in backward notation should be ignored
     * @return an object that can be installed into the engine using setRuleStore.
     */
//    public void compile(List rules, boolean ignoreBrules);

}
