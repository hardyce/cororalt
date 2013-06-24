/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.microjenaenh.graph.Node;

/**
 *
 * @author x61
 */
public class TempIntermediateBindingVector extends IntermediateBindingVector{
    public byte updateID;

    public TempIntermediateBindingVector(byte size, byte updateID){
        super(size);
        this.updateID = updateID;
    }

    public TempIntermediateBindingVector(Node[] env, byte updateID){
        super(env);
        this.updateID = updateID;
    }
}
