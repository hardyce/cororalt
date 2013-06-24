/*
  (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: GraphImpl.java,v 1.59 2007/01/02 11:52:20 andy_seaborne Exp $
 */

package ie.tcd.cs.nembes.coror.graph.impl;

import ie.tcd.cs.nembes.coror.graph.Axiom;
import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.graph.TripleMatch;
import ie.tcd.cs.nembes.coror.graph.temporal.TemporalTriple;
//import ie.tcd.cs.nembes.microjenaenh.shared.ReificationStyle;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.coror.util.iterator.IteratorImpl;
import ie.tcd.cs.nembes.coror.util.iterator.WrappedIterator;
import java.util.Vector;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.Map;

/**
 * A memory-backed graph with S/P/O indexes.
 * @author  bwm, kers
 */
public class GraphImpl extends GraphImplBase implements Graph {
    /**
     *        Initialises a GraphImpl with the Minimal reification style. Use the
     *        factory if possible; this method is public to allow certain reflective
     *        tests.
     */
    
    protected Vector triples = new Vector(10,10);
    
    public GraphImpl() {
//	this( ReificationStyle.Minimal );
    }
    
    /**
     *        Initialises a GraphImpl with the given reification style. Use the
     *        factory if possible; this method is public to allow certain reflective
     *        tests.
     */
//    public GraphImpl( ReificationStyle style ) {
//	super( style );
//    }
        
    public void performAdd( Triple t ) {
//	if (!getReifier().handledAdd( t ))
	    if(!this.contains(t)) {
	    triples.addElement(t);
	    cacheTriple(t);
            
	    }
            else if(this.contains(t)&&t instanceof TemporalTriple){
               
            ExtendedIterator find = this.find(t);
           
            
            while(find.hasNext()){
                
                Triple trip=(Triple)find.next();
                
                if(trip instanceof TemporalTriple){
                if(trip.equals(t)){
                    
                    Triple tmatch = trip;
                    TemporalTriple tta=(TemporalTriple)t;
                    TemporalTriple tt=(TemporalTriple)tmatch;
                    if(tt.getTime()<tta.getTime()){
                        
                        triples.remove(t);
                        triples.addElement(tta);
                    }
                    
                    
                }
            }
            }
            }
            
    }
    
    private void cacheTriple(Triple t) {
	cacheNode(t.getSubject());
	cacheNode(t.getPredicate());
	cacheNode(t.getObject());
    }
    
    private boolean cacheNode(Node n) {
	if(n.isURI()) {
	    nodes.put(n.getURI(), n);
	    return true;
	} else {
	    if(n.isBlank()) {
		nodes.put(n.getBlankNodeLabel(), n);
		return true;
	    } else {
		return false;
	    }
	}
    }
    
    public void performDelete( Triple t ) {
	performDelete(t, false);
    }
    
    @Override
    public void performDelete( Triple t, boolean removeAxiom ) {
//	if (!getReifier().handledRemove( t ))
	    if(removeAxiom || !(t instanceof Axiom)){
		triples.removeElement(t);
            }
    }

    public int graphBaseSize() {
	return triples.size();
    }
    
    /**
     * Answer an ExtendedIterator over all the triples in this graph that match the
     * triple-pattern <code>m</code>. Delegated to the store.
     */
    public ExtendedIterator graphBaseFind( TripleMatch m ) {
	Vector newVector = new Vector();
	Iterator it = new IteratorImpl(triples);
	Triple aus;
	while(it.hasNext()) {
	    aus = (Triple)(it.next());
	    if(aus.matches((Triple) m))
		newVector.addElement(aus);
	}
	IteratorImpl result = new IteratorImpl(newVector);
	return WrappedIterator.create(result);
    }
    
    /**
     * Answer true iff this graph contains <code>t</code>. If <code>t</code>
     * happens to be concrete, then we hand responsibility over to the store.
     * Otherwise we use the default implementation.
     */
    public boolean graphBaseContains( Triple t ) {
	return isSafeForEquality( t )
	    ? graphBaseFind(t).hasNext()
	    : super.graphBaseContains( t );
    }
    
    /**
     *        Clear this GraphImpl, ie remove all its triples (delegated to the store).
     */
    public void clear() {
	triples.removeAllElements();
	triples.trimToSize();
//	((SimpleReifier) getReifier()).clear();
    }

    protected void destroy() {
	triples.setSize(0);
    }
}

/*
 *  (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */