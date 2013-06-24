/*
  (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: GraphBase.java,v 1.47 2007/01/12 15:01:33 chris-dollin Exp $
 */

package ie.tcd.cs.nembes.coror.graph.impl;

import ie.tcd.cs.nembes.coror.debug.Debugger;
import ie.tcd.cs.nembes.coror.graph.BulkUpdateHandler;
import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.GraphUtil;
import ie.tcd.cs.nembes.coror.graph.Node;
//import ie.tcd.cs.nembes.microjenaenh.graph.Reifier;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.graph.TripleMatch;
import ie.tcd.cs.nembes.coror.shared.AddDeniedException;
import ie.tcd.cs.nembes.coror.shared.ClosedException;
import ie.tcd.cs.nembes.coror.shared.DeleteDeniedException;
import ie.tcd.cs.nembes.coror.shared.PrefixMapping;
//import ie.tcd.cs.nembes.microjenaenh.shared.ReificationStyle;
import ie.tcd.cs.nembes.coror.util.Map;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.coror.shared.impl.PrefixMappingImpl;


public abstract class GraphBase implements GraphWithPerform {
	
    
    /**
     * The reification style of this graph, used when the reifier is created (and
     * nowhere else, as it happens, which is good).
     */
//    protected final ReificationStyle style;
    
    /**
     * Whether or not this graph has been closed - used to report ClosedExceptions
     * when an operation is attempted on a closed graph.
     */
    protected boolean closed = false;
    
    protected Map nodes = new Map(20,20);
    
    
    /**
     * Initialise this graph as one with reification style Minimal.
     */
    public GraphBase() {
//	this( ReificationStyle.Minimal );
    }
    
//    /**
//     * Initialise this graph with the given reification style (which will be supplied to
//     * the reifier when it is created).
//     */
//    public GraphBase( ReificationStyle style ) {
//	this.style = style;
//    }
    
    /**
     * Utility method: throw a ClosedException if this graph has been closed.
     */
    protected void checkOpen() {
	if (closed) throw new ClosedException( "already closed", this );
    }
    
    /**
     * Close this graph. Subgraphs may extend to discard resources.
     */
    public void close() {
	closed = true;
//	if (reifier != null) reifier.close();
    }
    
    public boolean isClosed() {
	return closed;
    }
    
    /**
     * Default implemenentation answers <code>true</code> iff this graph is the
     * same graph as the argument graph.
     */
    public boolean dependsOn( Graph other ) {
	return this == other;
    }
    
    /**
     * Answer the PrefixMapping object for this graph, the same one each time.
     * Subclasses are unlikely to want to modify this.
     */
    public PrefixMapping getPrefixMapping() {
	return pm;
    }
    
    protected PrefixMapping pm = new PrefixMappingImpl();
    
    /**
     * Add a triple, and notify the event manager. Subclasses should not need to
     * override this - we might make it final. The triple is added using performAdd,
     * and notification done by notifyAdd.
     */
    public void add( Triple t ) {
	checkOpen();
	performAdd( t );
    }
    
    /**
     * Add a triple to the triple store. The default implementation throws an
     * AddDeniedException; subclasses must override if they want to be able to
     * add triples.
     */
    @Override
    public void performAdd( Triple t ) {
	throw new AddDeniedException( "GraphBase::performAdd" );
    }
    
    /**
     * Delete a triple, and notify the event manager. Subclasses should not need to
     * override this - we might make it final. The triple is added using performDelete,
     * and notification done by notifyDelete.
     */
    public final void delete( Triple t ) {
	delete(t, false);
    }
    
    public final void delete( Triple t, boolean removeAxiom ) {
	checkOpen();
	performDelete( t, removeAxiom );
    }    
    
    /**
     * Remove a triple from the triple store. The default implementation throws
     * a DeleteDeniedException; subclasses must override if they want to be able
     * to remove triples.
     */
    public void performDelete( Triple t ) {
	throw new DeleteDeniedException( "GraphBase::delete" );
    }
    
    public void performDelete( Triple t, boolean removeAxiom ) {
	throw new DeleteDeniedException( "GraphBase::delete" );
    }

    /**
     * Answer an (extended) iterator over all the triples in this Graph matching
     * <code>m</code>. Subclasses cannot over-ride this, because it implements
     * the appending of reification quadlets; instead they must implement
     * graphBaseFind(TripleMatch).
     */
    public final ExtendedIterator find( TripleMatch m ) {
	checkOpen();
//	return reifierTriples( m ) .andThen( graphBaseFind( m ) );
        return graphBaseFind(m);
    }
    
    /**
     * Answer an iterator over all the triples held in this graph's non-reified triple store
     * that match <code>m</code>. Subclasses <i>must</i> override; it is the core
     * implementation for <code>find(TripleMatch)</code>.
     */
    protected abstract ExtendedIterator graphBaseFind( TripleMatch m );
    
    public ExtendedIterator forTestingOnly_graphBaseFind( TripleMatch tm ) {
	return graphBaseFind( tm );
    }

    // NOTE Reasoning 2
    public final ExtendedIterator find( Node s, Node p, Node o ) {
	checkOpen();
	return graphBaseFind( s, p, o );
    }

    protected ExtendedIterator graphBaseFind( Node s, Node p, Node o ) {
	return find( Triple.createMatch( s, p, o ) );
    }
    
    /**
     * Answer <code>true</code> iff <code>t</code> is in the graph as revealed by
     * <code>find(t)</code> being non-empty. <code>t</code> may contain ANY
     * wildcards. Sub-classes may over-ride reifierContains and graphBaseContains
     * for efficiency.
     */
    public final boolean contains( Triple t ) {
	checkOpen();
//	return reifierContains( t ) || graphBaseContains( t );
        return graphBaseContains(t);
    }
    
    /**
     * Answer true if the reifier contains a quad matching <code>t</code>. The
     * default implementation uses the reifier's <code>findExposed</code> method.
     * Subclasses probably don't need to override (if they're interested, they
     * probably have specialised reifiers).
     */
//    protected boolean reifierContains( Triple t ) {
//	ExtendedIterator it = getReifier().findExposed( t );
//	try {
//	    return it.hasNext();
//	} finally {
//	    it.close();
//	}
//    }
    
    /**
     * Answer true if the graph contains any triple matching <code>t</code>.
     * The default implementation uses <code>find</code> and checks to see
     * if the iterator is non-empty.
     */
    protected boolean graphBaseContains( Triple t ) {
	return containsByFind( t );
    }
    
    /**
     * Answer <code>true</code> if this graph contains <code>(s, p, o)</code>;
     * this canonical implementation cannot be over-ridden.
     */
    public final boolean contains( Node s, Node p, Node o ) {
	checkOpen();
	return contains( Triple.create( s, p, o ) );
    }
    
    /**
     * Utility method: answer true iff we can find at least one instantiation of
     * the triple in this graph using find(TripleMatch).
     *
     * @param t Triple that is the pattern to match
     * @return true iff find(t) returns at least one result
     */
    final protected boolean containsByFind( Triple t ) {
	ExtendedIterator it = find( t );
	try {
	    return it.hasNext();
	} finally {
	    it.close();
	}
    }
    
//    /**
//     * Answer an iterator over all the triples exposed in this graph's reifier that
//     * match <code>m</code>. The default implementation delegates this to
//     * the reifier; subclasses probably don't need to override this.
//     */
//    protected ExtendedIterator reifierTriples( TripleMatch m ) {
//	return getReifier().findExposed( m );
//    }
    
    /**
     * Answer this graph's reifier. The reifier may be lazily constructed, and it
     * must be the same reifier on each call. The default implementation is a
     * SimpleReifier. Generally DO NOT override this method: override
     * <code>constructReifier</code> instead.
     */
//    public Reifier getReifier() {
//	if (reifier == null) reifier = constructReifier();
//	return reifier;
//    }
    
    /**
     * Answer a reifier approperiate to this graph. Subclasses override if
     * they need non-SimpleReifiers.
     */
//    protected Reifier constructReifier() {
//	return new SimpleReifier( this, style );
//    }
    
    /**
     * The cache variable for the allocated Reifier.
     */
//    protected Reifier reifier = null;
    
    /**
     * Answer the size of this graph (ie the number of exposed triples). Defined as
     * the size of the triple store plus the size of the reification store. Subclasses
     * must override graphBaseSize() to reimplement (and reifierSize if they have
     * some special reason for redefined that).
     */
    public final int size() {
	checkOpen();
//	int baseSize = graphBaseSize();
//	int reifierSize = reifierSize();
//	return baseSize + reifierSize;
        return graphBaseSize();
    }
    
    private String leafName( String name ) {
	int dot = name.lastIndexOf( '.' );
	return name.substring( dot + 1 );
    }
    
    /**
     * Answer the number of visible reification quads. Subclasses will not normally
     * need to override this, since it just invokes the reifier's size() method, and
     * they can implement their own reifier.
     */
//    protected int reifierSize() {
//	return getReifier().size();
//    }
    
    /**
     * Answer the number of triples in this graph. Default implementation counts its
     * way through the results of a findAll. Subclasses must override if they want
     * size() to be efficient.
     */
    protected int graphBaseSize() {
	ExtendedIterator it = GraphUtil.findAll( this );
	int tripleCount = 0;
	while (it.hasNext()) { it.next(); tripleCount += 1; }
	return tripleCount;
    }
    
    /**
     * Answer true iff this graph contains no triples (hidden reification quads do
     * not count). The default implementation is <code>size() == 0</code>, which is
     * fine if <code>size</code> is reasonable efficient. Subclasses may override
     * if necessary. This method may become final and defined in terms of other
     * methods.
     */
    public boolean isEmpty() {
	return size() == 0;
    }
    
    /**
     * Answer a human-consumable representation of this graph. Not advised for
     * big graphs, as it generates a big string: intended for debugging purposes.
     */
    
    public String toString() {
	return toString( (closed ? "closed " : ""), this );
    }
    
    /**
     * Answer a human-consumable representation of <code>that</code>. The
     * string <code>prefix</code> will appear near the beginning of the string. Nodes
     * may be prefix-compressed using <code>that</code>'s prefix-mapping. This
     * default implementation will display all the triples exposed by the graph (ie
     * including reification triples if it is Standard).
     */
    public static String toString( String prefix, Graph that ) {
	PrefixMapping pm = that.getPrefixMapping();
	StringBuffer b = new StringBuffer( prefix + " {" );
	String gap = "";
	ExtendedIterator it = GraphUtil.findAll( that );
	while (it.hasNext()) {
	    b.append( gap );
	    gap = "; ";
	    b.append( ((Triple) it.next()).toString( pm ) );
	}
	b.append( "}" );
	return b.toString();
    }
   
	   /**
	   Answer a BulkUpdateHandler bound to this graph. The default is a
	   SimpleBulkUpdateHandler, which does bulk update by repeated simple
	   (add/delete) updates; the same handler is returned on each call. Subclasses
	   may override if they have specialised implementations.
	*/
	public BulkUpdateHandler getBulkUpdateHandler()
	{ 
	  if (bulkHandler == null) bulkHandler = new SimpleBulkUpdateHandler( this ); 
	  return bulkHandler;
	}

	/**
	   The allocated BulkUpdateHandler, or null if no handler has been allocated yet.
	*/
	protected BulkUpdateHandler bulkHandler;
	
}