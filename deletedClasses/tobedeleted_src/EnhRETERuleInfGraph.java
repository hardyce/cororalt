/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys;

import ie.tcd.cs.nembes.microjenaenh.graph.Factory;
import ie.tcd.cs.nembes.microjenaenh.graph.Graph;
import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import ie.tcd.cs.nembes.microjenaenh.graph.impl.GraphImpl;
import ie.tcd.cs.nembes.microjenaenh.graph.impl.TempUpdateGraph;
import ie.tcd.cs.nembes.microjenaenh.reasoner.FGraph;
import ie.tcd.cs.nembes.microjenaenh.reasoner.Finder;
import ie.tcd.cs.nembes.microjenaenh.reasoner.FinderUtil;
import ie.tcd.cs.nembes.microjenaenh.reasoner.Reasoner;
import ie.tcd.cs.nembes.microjenaenh.reasoner.TriplePattern;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh.TempTriple;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh.TempUpdate;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl.RETEEngine;
import ie.tcd.cs.nembes.microjenaenh.util.List;
import ie.tcd.cs.nembes.microjenaenh.util.iterator.ExtendedIterator;

/**
 * This graph enhances the RETERuleInfGraph with couple of functions: 
 * 1. preprocessing hook
 * 2. temporary updates
 * 
 * @author Wei Tai
 */
public class EnhRETERuleInfGraph_deprecated extends RETERuleInfGraph{
     
    
    /** a finder adjoins fdata, fdeduction and new triples deduced by pre processors */
    protected Finder finder;

    /** Graphs for storing temporary deductions */
    protected FGraph tempDeductionsGraph;

    /** Graphs for storing original updates */
    protected FGraph tempRawGraph;
    
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
    public EnhRETERuleInfGraph(Reasoner reasoner, Graph schema) {
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
    public EnhRETERuleInfGraph(Reasoner reasoner, List rules, Graph schema) {
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
     public EnhRETERuleInfGraph(Reasoner reasoner, List rules, Graph schema, Graph data) {
         super(reasoner, rules, schema, data);
     }
     

    /**
     * enforce the whole reasoning to be performed.
     */
    public synchronized void enforePrepare(){
        isPrepared = false;
        prepare();
    }

    @Override
    public synchronized void prepare(){
        // reasoning will not be performed on a same ontology once reasoned

        if (isPrepared) return;
        isPrepared = true;
        
        // initilize the deductions graph
        fdeductions = new FGraph( createDeductionsGraph() );
        boolean rulesLoaded = false;
        if (schemaGraph != null) {
            rulesLoaded = preloadDeductions(schemaGraph);
        }        
     
        Finder dataSource = fdata;
        finder = (fdata.getGraph() == null) ? fdeductions :  FinderUtil.cascade(fdeductions, fdata);
        
        if (rulesLoaded) {
            engine.fastInit(dataSource); 
        } else {
            engine.init(true, dataSource);
        }   
    }

    // NOTE Reasoning 4
    /**
     * <b>J2ME Version:</b> Have not been tested
     * @param pattern a TriplePattern to be matched against the data
     * @param continuation either a Finder or a normal Graph which
     * will be asked for additional match results if the implementor
     * may not have completely satisfied the query.
     */
    public ExtendedIterator findWithContinuation(TriplePattern pattern, Finder continuation) {

        checkOpen();
        if (!isPrepared) prepare();
        
        if (finder == null){
            finder = FinderUtil.cascade(fdeductions, fdata);
        }

        if(tempRawGraph != null) finder = FinderUtil.cascade(finder, tempRawGraph);
        if(tempDeductionsGraph != null) finder = FinderUtil.cascade(finder, tempDeductionsGraph);
            
        ExtendedIterator result = finder.findWithContinuation(pattern, continuation);
        
        return result.filterDrop(Functor.acceptFilter);
    }
    
    /**
     * Add a new triple to underlying graph. This triple should be checked against 
     * the registered preprocessorHooks to make sure preprocessors need to rerun or
     * not. If rerun is required then another invocation of prepare() is need to
     * get new deductions introduced by the new added triple. If not then the triple
     * is directly added into graph and inserted into underlying RETE engine.
     * @param t new added triple
     */
    public synchronized void performAdd(Triple t) {
        version++;
        fdata.getGraph().add(t);
        
        if (isPrepared) {
            boolean needReset = false;
            if (needReset) {
                isPrepared = false;
            } else {
                engine.add(t);
            }
        }
    }

    /**
     * Add multiple triples into RETE engine and run inference.
     * @param triples
     */
    public synchronized void performBulkAdd(List triples){
        version ++;
        for(int i=0; i<triples.size(); i++){
            Triple t = (Triple)triples.get(i);
            fdata.getGraph().add(t);
            
            ((RETEEngine)engine).addTriple(t, false);
        }

        if(isPrepared){
            ((RETEEngine)engine).runAll();
        }
    }

    /**
     * Delete multiple triples from RETE engine and run inference.
     */
    public synchronized void performBulkDelete(List triples){
        version ++;
        for(int i=0; i<triples.size(); i++){
            Triple t = (Triple)triples.get(i);
            fdata.getGraph().delete(t);

            ((RETEEngine)engine).deleteTriple(t, false);
        }

        if(isPrepared){
            ((RETEEngine)engine).runAll();
        }
    }

    /**
     * J2ME Version:<br>
     * 1. Create default graph instead of creating graph mem.
     */
    protected Graph createDeductionsGraph() {
        if (fdeductions != null) {
            Graph dg = fdeductions.getGraph();
            if (dg != null) {
                // Reuse the old graph in order to preserve any listeners
                dg.getBulkUpdateHandler().removeAll();
                return dg;
            }
        }
//        return Factory.createGraphMem( style );
        
        // NOTE use the old approach or use updategraph
        return Factory.createDefaultGraph();
    }
    
    /**
     * Delete temporal triples within the given time scale.
     */
    public void deleteTemporalTriples(long start, long end){
        ((RETEEngine)engine).deleteTriples(start, end);
    }
    
}

