/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.graph.Node;

/**
 * Partial binding vector with a time stamp.
 * 
 * @author WEI TAI
 */
public class TemporalPBV extends PBV {
    
    /** the time stamp associated with this T-PBV. Normally if this T-PBV is constructed directly
     from a triple in the condition node, then the time associated with the triple is
     passed to the T-PBV. If this T-PBV is a result of a successful join then the earlier
     time of either side would be assigned to this time stamp. */
    protected long time;
    
    /**
     * Constructor.
     * @param num the size of the T-PBV
     * @param time the time stamp
     */
    public TemporalPBV(int num, long time){
        super(num);
        this.time = time;
    }

    /**
     * Constructor.
     * @param environment the array for variable bindings
     * @param time the time stamp
     */
    public TemporalPBV(Node[] environment, long time){
        super(environment);
        this.time = time;
    }
    
    /**
     * @return the time stamp.
     */
    public long getTimeStamp(){
        return time;
    } 
    
    /**
     * Check if this T-PBV has a time stamp within the given time slot.
     * @param start start of the time slot
     * @param end end of the time slot
     */
    public boolean within(long start, long end){
        if( time >= start && time <= end) return true;
        return false;
    }
    
    public String toString(){
        return super.toString() + " : at time " + time;
    }
}
