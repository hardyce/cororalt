/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.graph.Triple;

/**
 *
 * @author Wei Tai
 */
public interface FireTripleI {
    public void fire(Triple triple, boolean isAdd);
}
