/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.debug;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.BindingVector;
import ie.tcd.cs.nembes.coror.util.List;

/**
 * Binding vector with trace.
 * @author WEI TAI
 */
public class TraceBindingVector extends BindingVector{
    public List trace;
    
    /**
     * Constructor - create an empty binding environment 
     */
    public TraceBindingVector(int size, List trace) {
        super(size);
        this.trace = trace;
    }
    
    /**
     * Constructor - create a binding environment from a vector of bindings 
     */
    public TraceBindingVector(Node [] env, List trace) {
        super(env);
        this.trace = trace;
    }
    
    /**
     * Constructor - create a binding environment which is a copy
     * of the given environment
     */
    public TraceBindingVector(BindingVector clone, List trace) {
        super(clone);
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
