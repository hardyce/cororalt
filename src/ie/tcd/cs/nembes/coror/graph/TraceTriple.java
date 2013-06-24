/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.graph;

import ie.tcd.cs.nembes.coror.reasoner.rulesys.Rule;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;


/**
 * A triple with trace.
 * @author WEI TAI
 */
public class TraceTriple extends Triple{
    public Rule rule;
    public List trace;
            
    public TraceTriple(Node s, Node p, Node o, Rule r, List trace){
        super(s, p, o);
        rule = r;
        this.trace = trace;
    }
    
    public String getTraceAsString(){
        
        String traceStr = null;
            if(trace.get(0)!= null)
                traceStr = "(" + trace.get(0).toString();
        for(int i=1; i<trace.size(); i++){
            if(trace.get(i) != null)
                traceStr = traceStr+" -> "+trace.get(i).toString();
        }       
        return traceStr+")";
    }

    public void setTrace(List traces) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
