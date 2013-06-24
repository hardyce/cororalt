/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.graph.temporal;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;

/**
 * 
 * @author WEI TAI
 */
public class TemporalTriple extends Triple {
    long time = 0;
    
    public TemporalTriple(Node s, Node p, Node o, long time){
        super(s, p, o);
        this.time = time;
    }
    
    public long getTime(){
        return time;
    }
    
    public boolean within(long start, long end){
        if( time >= start && time <= end) return true;
        return false;
    }
    
    public String toString(){
        return super.toString() + " : at time "+time;
    }
    
    public static TemporalTriple create(Node s, Node p, Node o, long time){
        return new TemporalTriple(s, p, o, time);
    }    
}
