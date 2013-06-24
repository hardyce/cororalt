/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.graph.Node;

/**
 *
 * @author WEI TAI
 */
public class BindingVectorTemporal extends BindingVector {
    protected long time;
    
    /**
     * Constructor - create an empty binding environment 
     */
    public BindingVectorTemporal(int size, long time) {
        super(size);
        this.time = time;
    }
    
    /**
     * Constructor - create a binding environment from a vector of bindings 
     */
    public BindingVectorTemporal(Node [] env, long time) {
        super(env);
        this.time = time;
    }
    
    /**
     * Constructor - create a binding environment which is a copy
     * of the given environment
     */
    public BindingVectorTemporal(BindingVector clone, long time) {
        super(clone);
        this.time = time;
    }    
    
    public long getTime(){
        return time;
    }
    
    public boolean within(long start, long end){
        if( time >= start && time <= end) return true;
        return false;
    }
}
