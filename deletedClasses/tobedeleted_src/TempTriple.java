/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh;

import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.graph.Triple;

/**
 * Temporal triple. With a node 
 * @author Wei Tai
 */
public class TempTriple extends Triple{

    public Byte updateID;

    public TempTriple(Node subject, Node predicate, Node object, Byte updateID){
        super(subject, predicate, object);
        this.updateID = updateID;
    }

    public TempTriple(Triple triple, Byte updateID){
        super(triple.getSubject(), triple.getPredicate(), triple.getObject());
        this.updateID = updateID;        
    }
}
