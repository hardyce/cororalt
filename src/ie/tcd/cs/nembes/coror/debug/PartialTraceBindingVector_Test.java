/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.debug;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.PBV;
import ie.tcd.cs.nembes.coror.util.List;


/**
 * Partial binding vector with trace.
 * @author WEI TAI
 */
public class PartialTraceBindingVector_Test extends PBV {
    
    /** A list of triples showing the trace of this binding vector */
    protected List trace;
    
    public PartialTraceBindingVector_Test (int num, List trace){
        super(num);
        this.trace = trace;
    }
    
    public PartialTraceBindingVector_Test(Node[] environment, List trace){
        super(environment);
        this.trace = trace;
    }
    
    public static List combineTrace(List lTrace, List rTrace){
        List trace = new List();
        trace.addAll(lTrace);
        trace.addAll(rTrace);
        return trace;
    }
    
    public String toString(){
        String envStr = "[";
        for(int i=0; i<pEnvironment.length; i++){
            envStr += pEnvironment[i]+" ";
        }
        envStr += "]";
        return envStr;
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
        trace = traces;
    }
    
}
