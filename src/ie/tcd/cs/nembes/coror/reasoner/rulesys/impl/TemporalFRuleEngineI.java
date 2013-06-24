/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

/**
 * Extends FRuleEngineI with the capability to handle temporal features.
 * @author WEI TAI
 */
public interface TemporalFRuleEngineI extends FRuleEngineI {
    
    /**
     * Temporal FRuleEngine should be able to tidy up the buffers fast, according
     * to the given time slot. Start is -1 indicates remove all the T-PBV until the
     * time given by end.
     */
    public void sweepRETE(long start, long end);
   
}
