/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.temporal.TemporalTriple;
import ie.tcd.cs.nembes.coror.reasoner.FGraph;
import ie.tcd.cs.nembes.coror.reasoner.Reasoner;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.TemporalFRuleEngineI;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;

/**
 * Extends the RETERuleInfGraph with the capability to handle temporal triples
 * @author WEI TAI
 */
public class TemporalRETERuleInfGraph extends RETERuleInfGraph {
    
//    protected FGraph ftempdeductions;
    
    ////////////////////////////////////////////////////////
    //  Constructors are the same as its superclass
    ////////////////////////////////////////////////////////
    
    public TemporalRETERuleInfGraph(Reasoner reasoner, Graph schema) {
        super(reasoner, schema);
    }
    
    public TemporalRETERuleInfGraph(Reasoner reasoner, List rules, Graph schema) {
        super(reasoner, rules, schema);
    }
    
    public TemporalRETERuleInfGraph(Reasoner reasoner, List rules, Graph schema, Graph data) {
         super(reasoner, rules, schema, data);
    }
    
    /**
     * sweep the inferred triples as well as the T-PBVs in RETE network within 
     * the given time slot.
     * @param start start of the time slot
     * @param end end of the time slot
     */
    public void sweepGraph(long start, long end){
       
        ExtendedIterator it = fdeductions.getGraph().find(null, null, null);
        
              
        // remove all temporal triples in the deduction graph
        if(it.hasNext()){
            
            Object next = it.next();
            System.out.println(next.toString()+" ded");
            if(next instanceof TemporalTriple){
                if(((TemporalTriple) next).within(start, end)){
                //    System.out.println("sweep ded"+next.toString());
                //old line below
                //getDeductionsGraph().delete((TemporalTriple)next);
                //performDeleteTimeTriples((TemporalTriple)next);
                //engine.delete((TemporalTriple)next);
                }
            }
            //engine.delete((TemporalTriple)next);
        }
        ExtendedIterator it2 = fdata.getGraph().find(null, null, null);   
        if(it2.hasNext()){
            
            Object next2 = it2.next();
            System.out.println(next2.toString()+" raw");
            if(next2 instanceof TemporalTriple){
                if(((TemporalTriple) next2).within(start, end)){
                //    System.out.println("sweep raw"+next2.toString());
                //old line below
                //getRawGraph().delete((TemporalTriple)next2);
                //performDeleteTimeTriples((TemporalTriple)next2);
                //engine.delete((TemporalTriple)next2);
                }
            }
        }
        ((TemporalFRuleEngineI)engine).sweepRETE(start, end);
        
    }
    
//    public synchronized void performAddTemporal(TemporalTriple t){
//        
//    }
}
