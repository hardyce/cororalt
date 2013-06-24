package ie.tcd.cs.nembes.coror.reasoner.rulesys;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.graph.temporal.TemporalTriple;
import ie.tcd.cs.nembes.coror.reasoner.Reasoner;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.RETEEngine;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.TemporalFRuleEngineI;
import ie.tcd.cs.nembes.coror.util.List;

/**
 * RETE implementation of the forward rule infernce graph.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.11 $ on $Date: 2008/01/02 12:07:47 $
 */
public class RETERuleInfGraph extends BasicForwardRuleInfGraph {

    /**
     * Constructor. Creates a new inference graph to which a (compiled) rule set
     * and a data graph can be attached. This separation of binding is useful to allow
     * any configuration parameters (such as logging) to be set before the data is added.
     * Note that until the data is added using {@link #rebind rebind} then any operations
     * like add, remove, find will result in errors.
     * 
     * @param reasoner the parent reasoner 
     * @param schema the (optional) schema data which is being processed
     */
    public RETERuleInfGraph(Reasoner reasoner, Graph schema) {
        super(reasoner, schema);
    }    

    /**
     * Constructor. Creates a new inference graph based on the given rule set. 
     * No data graph is attached at this stage. This is to allow
     * any configuration parameters (such as logging) to be set before the data is added.
     * Note that until the data is added using {@link #rebind rebind} then any operations
     * like add, remove, find will result in errors.
     * 
     * @param reasoner the parent reasoner 
     * @param rules the list of rules to use this time
     * @param schema the (optional) schema or preload data which is being processed
     */
    public RETERuleInfGraph(Reasoner reasoner, List rules, Graph schema) {
        super(reasoner, rules, schema);
    }    

     /**
      * Constructor. Creates a new inference graph based on the given rule set
      * then processes the initial data graph. No precomputed deductions are loaded.
      * 
      * @param reasoner the parent reasoner 
      * @param rules the list of rules to use this time
      * @param schema the (optional) schema or preload data which is being processed
      * @param data the data graph to be processed
      */
     public RETERuleInfGraph(Reasoner reasoner, List rules, Graph schema, Graph data) {
         super(reasoner, rules, schema, data);
     }

    /**
     * Instantiate the forward rule engine to use.
     * Subclasses can override this to switch to, say, a RETE imlementation.
     * @param rules the rule set or null if there are not rules bound in yet.
     */
    protected void instantiateRuleEngine(List rules) {
        if (rules != null) {
            engine = new RETEEngine(this, rules);
        } else {
            engine = new RETEEngine(this);
        }
    }

    /**
     * Add one triple to the data graph, run any rules triggered by
     * the new data item, recursively adding any generated triples.
     */
    public synchronized void performAdd(Triple t) {
        
        if (!isPrepared) prepare();
        fdata.getGraph().add(t);
        engine.add(t);
    }
    
    /** 
     * Removes the triple t (if possible) from the set belonging to this graph. 
     */   
    public void performDelete(Triple t) {
        System.out.println("norm del");
        if (!isPrepared) prepare();
        if (fdata != null) {
            Graph data = fdata.getGraph();
            if (data != null) {
                data.delete(t);
            }
        }
        engine.delete(t);
        fdeductions.getGraph().delete(t);
    }
        public void performDeleteTimeTriples(TemporalTriple t,int i) {
        
        if (!isPrepared) prepare();
        if (fdata != null) {
            Graph data = fdata.getGraph();
            if (data != null) {
                if(t.getTime()<i){
                data.delete(t);
                }
            }
        }
        if(t.getTime()<i){
        fdeductions.getGraph().delete(t);
        
        //engine.delete(t);
        }
     
        //((TemporalFRuleEngineI)engine).sweepRETE(0, i);
    }
        public void sweepRete(long i){
            ((TemporalFRuleEngineI)engine).sweepRETE(0, i);
        }

}

