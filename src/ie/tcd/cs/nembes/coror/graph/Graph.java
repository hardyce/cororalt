package ie.tcd.cs.nembes.coror.graph;

import ie.tcd.cs.nembes.coror.graph.impl.GraphBase;
import ie.tcd.cs.nembes.coror.shared.PrefixMapping;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.coror.util.iterator.NullIterator;


public interface Graph extends GraphAdd {
    /**
    An immutable empty graph. 
     */
	public static final Graph emptyGraph = new GraphBase() {
		public ExtendedIterator graphBaseFind( TripleMatch tm ) {
		    return NullIterator.instance;
		}
	};
		
	/** 
	    true if this graph's content depends on the other graph. May be
	    pessimistic (ie return true if it's not sure). Typically true when a
	    graph is a composition of other graphs, eg union.
	    
	     @param other the graph this graph may depend on
	     @return false if this does not depend on other 
	*/
	boolean dependsOn( Graph other );
	  
	/** 
	    returns this Graph's reifier. Each call on a given Graph gets the same
	    Reifier object.
	*/
//	Reifier getReifier();
	
	/**
	    returns this Graph's prefix mapping. Each call on a given Graph gets the
	    same PrefixMapping object, which is the one used by the Graph.
	*/
	PrefixMapping getPrefixMapping();
	
	/** 
	    Remove the triple t (if possible) from the set belonging to this graph 
	
	    @param  t the triple to add to the graph
	    @throws DeleteDeniedException if the triple cannot be removed  
	*/   
	void delete(Triple t);
	
	void delete(Triple t, boolean removeAxiom);
	
	/** 
	    Returns an iterator over all the Triples that match the triple pattern.
	   
	    @param m a Triple[Match] encoding the pattern to look for
	    @return an iterator of all triples in this graph that match m
	*/
	ExtendedIterator find(TripleMatch m);
	
	/** Returns an iterator over Triple. */
	ExtendedIterator find(Node s,Node p,Node o);
	
	/** 
	    Answer true iff the graph contains a triple matching (s, p, o).
	    s/p/o may be concrete or fluid. Equivalent to find(s,p,o).hasNext,
	    but an implementation is expected to optimise this in easy cases.
	*/
	boolean contains( Node s, Node p, Node o );
	    
	/** 
	    Answer true iff the graph contains a triple that t matches; t may be
	    fluid.
	*/
	boolean contains( Triple t );
	
	/** Free all resources, any further use of this Graph is an error.
	 */
	void close();
	
	/**
	    Answer true iff this graph is empty. "Empty" means "has as few triples as it
	    can manage", because an inference graph may have irremovable axioms
	    and their consequences.
	*/
	boolean isEmpty();
	
	/**
	 * For a concrete graph this returns the number of triples in the graph. For graphs which
	 * might infer additional triples it results an estimated lower bound of the number of triples.
	 * For example, an inference graph might return the number of triples in the raw data graph. 
	 */
	int size();
	
	/**
	    Answer true iff .close() has been called onn this Graph.
	*/
	boolean isClosed();
    /** returns this Graph's bulk-update handler */
    BulkUpdateHandler getBulkUpdateHandler();	
}
