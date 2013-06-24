/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.microjenaenh.util.List;

/**
 * Enable the trace of reasoning to be recorded.
 * 
 * @author WEI TAI
 */
public interface Trackable {
    public String getTraceAsString();
    
    public void setTrace(List traces);
}
