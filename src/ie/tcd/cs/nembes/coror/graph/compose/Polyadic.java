package ie.tcd.cs.nembes.coror.graph.compose;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.util.List;

/**
 * <b>J2ME version:</b>
 * Only required methods and fields are kept. Event managers are removed in this version.
 * <p>
 * A base class for composition graphs that are composed from zero or more
 * sub-graphs (thus providing a basis for polyadic composition operators).
 * A distinguished graph is the designated graph for additions to the union.
 * By default, this is the first sub-graph of the composition, however any
 * of the graphs in the composition can be nominated to be the distinguished
 * graph.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: Polyadic.java,v 1.20 2008/01/02 12:10:19 andy_seaborne Exp $
 */
public abstract class Polyadic extends CompositionBase{
	
    /** A list of the sub-graphs that this composition contains */
    protected List m_subGraphs = new List();

    /** The distinguished graph for adding to. If null, use the 0'th graph in the list. */
    protected Graph m_baseGraph = null;
    
    /**
     * <p>
     * Set the designated updateable graph for this composition.
     * </p>
     *
     * @param graph One of the graphs currently in this composition to be the
     *              designated graph to receive udpates
     * @exception IllegalArgumentException if graph is not one of the members of
     *             the composition
     */
    public void setBaseGraph( Graph graph ) {
        if (m_subGraphs.contains( graph )) {
            m_baseGraph = graph;
            bulkHandler = null;
        }
        else {
            throw new IllegalArgumentException( "The updateable graph must be one of the graphs from the composition" );
        }
    }
}
