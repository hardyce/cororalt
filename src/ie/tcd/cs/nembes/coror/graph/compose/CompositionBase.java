package ie.tcd.cs.nembes.coror.graph.compose;

import ie.tcd.cs.nembes.coror.graph.impl.GraphBase;
import ie.tcd.cs.nembes.coror.util.Set;
import ie.tcd.cs.nembes.coror.util.UnsupportedOperationException;
import ie.tcd.cs.nembes.coror.util.iterator.ClosableIterator;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.coror.util.iterator.Filter;
import ie.tcd.cs.nembes.coror.util.iterator.NiceIterator;

/**
 * <b>J2ME version:</b>
 * Only required methods and fields are kept. Event managers are removed in this version.
 * @author Wei Tai
 *
 */
public abstract class CompositionBase extends GraphBase {

    /**
     * <p>
     * Answer an iterator over the elements of iterator i that are not in the set <code>seen</code>. 
     * </p>
     * 
     * @param i An extended iterator
     * @param seen A set of objects
     * @return An iterator over the elements of i that are not in the set <code>seen</code>.
     */
    public static ExtendedIterator rejecting( final ExtendedIterator i, final Set seen )
    {
        Filter seenFilter = new Filter()
            { public boolean accept( Object x ) { return seen.contains( x ); } };
        return i.filterDrop( seenFilter );
    }
    
    /**
     * <p>
     * Answer an iterator that will record every element delived by <code>next()</code> in
     * the set <code>seen</code>. 
     * </p>
     * 
     * @param i A closable iterator
     * @param seen A set that will record each element of i in turn
     * @return An iterator that records the elements of i.
     */
    public static ExtendedIterator recording( final ClosableIterator i, final Set seen )
        {
        return new NiceIterator()
            {
            public void remove()
                { 
					try {
						i.remove();
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
					} }
            
            public boolean hasNext()
                { return i.hasNext(); }    
            
            public Object next()
                { Object x = i.next();
                try { seen.add( x ); } catch (OutOfMemoryError e) { throw e; } return x; }  
                
            public void close()
                { i.close(); }
            };
        }
}
