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
public class TempBindingVector extends BindingVector{
    public byte updateID;

    public TempBindingVector(byte size, byte updateID){
        super(size);
        this.updateID = updateID;
    }

    public TempBindingVector(Node[] evn, byte updateID){
        super(evn);
        this.updateID = updateID;
    }
}
