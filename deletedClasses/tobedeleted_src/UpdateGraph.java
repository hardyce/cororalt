/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.graph.impl;

import ie.tcd.cs.nembes.microjenaenh.graph.Axiom;
import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import ie.tcd.cs.nembes.microjenaenh.graph.TripleMatch;
import ie.tcd.cs.nembes.microjenaenh.shared.ReificationStyle;
import ie.tcd.cs.nembes.microjenaenh.util.CountMap;
import ie.tcd.cs.nembes.microjenaenh.util.CountMap.Count;
import ie.tcd.cs.nembes.microjenaenh.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.microjenaenh.util.iterator.IteratorImpl;
import ie.tcd.cs.nembes.microjenaenh.util.iterator.WrappedIterator;
import java.util.Vector;

/**
 * Extends the normal graph with truth maintenance ability
 * @author Wei Tai
 */
public class UpdateGraph extends GraphImpl{

    protected CountMap countTriples;

    /**
     * Constructor.
     * @param style
     */
    public UpdateGraph(ReificationStyle style){
        super(style);
        countTriples = new CountMap(20, 20);
    }

    /**
     * Default constructor.
     */
    public UpdateGraph(){
        super();
        countTriples = new CountMap(20, 20);
    }

    /**
     * add a triple.
     * @param t
     */
    public void performAdd( Triple t ) {
        if (!getReifier().handledAdd( t )){
//            System.err.println("UpdateGraph adding "+t);
            // NOTE in UpdateGraph adding the same triple means increasing count by 1.
    //        if(!this.contains(t)) {
            Count ct = countTriples.addElement(t);
            // only cache triple if it is the first time added
            if(ct.getCount() == 1)
                cacheTriple(t);
        }
    //        }
    }

    /**
     * 
     * @param t
     */
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
	if (!getReifier().handledRemove( t ))
	    if(removeAxiom || !(t instanceof Axiom))
		countTriples.removeElement(t);
    }

    public int graphBaseSize() {
	return countTriples.size();
    }

    /**
     * Answer an ExtendedIterator over all the triples in this graph that match the
     * triple-pattern <code>m</code>. Delegated to the store.
     */
    public ExtendedIterator graphBaseFind( TripleMatch m ) {
	Vector newVector = new Vector();
	Triple aus;
	for(int i=0; i<countTriples.size(); i++){
	    aus = (Triple)countTriples.elementAt(i);
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
	countTriples.getAllElements().removeAllElements();
	countTriples.getAllElements().trimToSize();
	((SimpleReifier) getReifier()).clear();
    }

    protected void destroy() {
	countTriples.getAllElements().setSize(0);
    }

}
