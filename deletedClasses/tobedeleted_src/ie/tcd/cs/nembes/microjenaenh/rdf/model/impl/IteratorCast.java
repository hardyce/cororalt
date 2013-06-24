/*
 * IteratorCast.java
 *
 * Created on 28 novembre 2007, 15.12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.rdf.model.impl;

import java.util.Vector;
import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import ie.tcd.cs.nembes.microjenaenh.rdf.model.NodeIterator;
import ie.tcd.cs.nembes.microjenaenh.rdf.model.ResIterator;
import ie.tcd.cs.nembes.microjenaenh.rdf.model.StmtIterator;
import ie.tcd.cs.nembes.microjenaenh.util.Iterator;
import ie.tcd.cs.nembes.microjenaenh.util.iterator.IteratorImpl;
import ie.tcd.cs.nembes.microjenaenh.util.iterator.NullIterator;

/**
 *
 * @author ilBuccia
 */
public class IteratorCast {
    
    /** Creates a new instance of IteratorCast */
    public IteratorCast() {
    }
    
    //converts an iterator over triples into a StmtIterator over Statement objects
    public static StmtIterator asStmtIterator(Iterator it, ModelCom eg) {
	Vector result = new Vector(20,20);
	while(it.hasNext()) {
	    result.addElement(StatementImpl.toStatement((Triple)it.next(), eg));
	}
	Iterator itResult = new IteratorImpl(result);
	return new StmtIteratorImpl(itResult);
    }
    
    //converts an ExtendedIterator over nodes into a NodeIterator
    public static NodeIterator asNodeIterator(Iterator it) {
	return new NodeIteratorImpl(it);
    }

    //converts an ExtendedIterator over nodes into a ResIterator
    public static ResIterator asResIterator(Iterator it) {
	return new ResIteratorImpl(it);
    }
}
