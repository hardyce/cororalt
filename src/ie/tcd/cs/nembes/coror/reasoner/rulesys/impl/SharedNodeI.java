/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.util.List;
import java.util.Vector;

/**
 * Shared alpha and beta nodes implement this interface to construct a shared
 * RETE network.
 * 
 * @author WEI TAI
 */
public interface SharedNodeI {
    
   public void addContinuation(RETESinkNode continuation);
   
   public List getContinuations();
   
   /** for test only */
   public String getNodeID();
}
