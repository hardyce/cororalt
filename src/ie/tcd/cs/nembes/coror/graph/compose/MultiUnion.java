package ie.tcd.cs.nembes.coror.graph.compose;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.TripleMatch;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.Set;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.coror.util.iterator.NullIterator;


/**
 * <b>J2ME version:</b>
 * Only required methods and fields are kept. Event managers are removed in this version.
 * <p>
 * A graph implementation that presents the union of zero or more subgraphs,
 * one of which is distinguished as the updateable graph.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: MultiUnion.java,v 1.28 2008/01/02 12:10:20 andy_seaborne Exp $
 */
public class MultiUnion extends Polyadic{
    
	
	/**
     * <p>
     * Add the given graph to this union.  If it is already a member of the union, don't
     * add it a second time.
     * </p>
     *
     * @param graph A sub-graph to add to this union
     */
    public void addGraph( Graph graph ) {
        if (!m_subGraphs.contains( graph )) {
            m_subGraphs.add( graph );
        }
    }

    /**
    Answer true iff we're optimising find and query over unions with a
    single element.
*/
    private boolean optimiseOne()
    { return optimising && m_subGraphs.size() == 1; }

    /**
     * J2ME version
     * set the optimising as true
     */
	private boolean optimising = true;

    /**
     * <b>J2ME version:</b>
     * Event Manager Removed
     * <p>
     * Answer an iterator over the triples in the union of the graphs in this composition. <b>Note</b>
     * that the requirement to remove duplicates from the union means that this will be an
     * expensive operation for large (and especially for persistent) graphs.
     * </p>
     *
     * @param t The matcher to match against
     * @return An iterator of all triples matching t in the union of the graphs.
     */
	protected ExtendedIterator graphBaseFind(final TripleMatch t) {
		// optimise the case where there's only one component graph.
        return optimiseOne() ? singleGraphFind( t ) : multiGraphFind( t );
	}
	
    /**
    Answer the result of <code>find( t )</code> on the single graph in
    this union.
	*/
	private ExtendedIterator singleGraphFind( final TripleMatch t ){ 
		return ((Graph) m_subGraphs.get( 0 )).find(  t  ); 
	}

	/**
		Answer the concatenation of all the iterators from a-subGraph.find( t ).
	*/
	private ExtendedIterator multiGraphFind( final TripleMatch t )
	{
		Set seen = new Set();
		ExtendedIterator result = NullIterator.instance;
		for (Iterator graphs = m_subGraphs.iterator(); graphs.hasNext(); ) 
		{
		    ExtendedIterator newTriples = recording( rejecting( ((Graph) graphs.next()).find( t ), seen ), seen );
		    result = result.andThen( newTriples );
		}
		return result;
	}
}
