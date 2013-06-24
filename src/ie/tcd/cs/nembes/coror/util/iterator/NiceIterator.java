/*
  (c) Copyright 2003, 2004, 2005 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: NiceIterator.java,v 1.16 2007/01/02 11:49:41 andy_seaborne Exp $
*/

package ie.tcd.cs.nembes.coror.util.iterator;

import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.Set;
import java.util.NoSuchElementException;
import java.util.Vector;
import ie.tcd.cs.nembes.coror.shared.JenaUnsupportedOperationException;


/**
 * J2ME version
NiceIterator is the standard base class implementing ExtendedIterator. It provides
the static methods for <code>andThen</code>, <code>filterKeep</code> and
<code>filterDrop</code>; these can be reused from any other class. It defines
equivalent instance methods for descendants and to satisfy ExtendedIterator.  
@author Wei
*/

public class NiceIterator implements ExtendedIterator{
    public NiceIterator() {
    	super();
        }

        /**
            default close: don't need to do anything.
        */
        public void close() {
        }

        /**
            default hasNext: no elements, return false.
        */
        public boolean hasNext() {
    	return false;
        }

        protected void ensureHasNext() {
    	if (hasNext() == false)
    	    throw new NoSuchElementException();
        }
        
        /**
            default next: throw an exception.
        */
        public Object next() {
    	return noElements( "empty NiceIterator" );
        }
        
        /**
            Utility method for this and other (sub)classes: raise the appropriate
            "no more elements" exception. I note that we raised the wrong exception
            in at least one case ...
        
            @param message the string to include in the exception
            @return never - but we have a return type to please the compiler
        */
        protected Object noElements( String message ) {
    	throw new NoSuchElementException( message );
        }
            
        /**
            default remove: we have no elements, so we can't remove any.
        */
        public void remove() {
    	throw new JenaUnsupportedOperationException( "remove not supported for this iterator" );
        }
        
        /**
             Answer the next object, and remove it.
        */
        public Object removeNext() {
    	Object result = next();
    	remove();
    	return result;
        }
            
        /**
            concatenate two closable iterators.
        */
        
        public static ExtendedIterator andThen( final Iterator a, final Iterator b ) {
    	Vector newIt = new Vector(5,5);
    	while(a.hasNext())
    	    newIt.addElement(a.next());
    	while(b.hasNext())
    	    newIt.addElement(b.next());
    	Iterator result = new IteratorImpl(newIt);
    	return WrappedIterator.create(result);
        }
        
        /**
            make a new iterator, which is us then the other chap.
        */   
        public ExtendedIterator andThen( ClosableIterator other ) {
    	return andThen( this, other );
        }
            
        /**
            If <code>it</code> is a Closableiterator, close it. Abstracts away from
            tests [that were] scattered through the code.
        */
        public static void close( Iterator it ) {
    	if (it instanceof ClosableIterator)
    	    ((ClosableIterator) it).close();
        }
       
        static final private NiceIterator emptyInstance = new NiceIterator();
        
        /**
         * An iterator over no elements.
         * @return A class singleton which doesn't iterate.
         */
        static public ExtendedIterator emptyIterator() {
    	return emptyInstance;
        }

        /**
            Answer a list of the elements in order, consuming this iterator.
        */
        public List toList() {
    	return asList( this );
        }

        /**
            Answer a list of the elements in order, consuming this iterator.
        */
        public Set toSet() {
    	return asSet( this );
        }

        /**
            Answer a list of the elements of <code>it</code> in order, consuming this iterator.
            Canonical implementation of toSet().
        */
        public static Set asSet( ExtendedIterator it ) {
    	Set result = new Set();
    	while (it.hasNext())
    	    result.add( it.next() );
    	return result;
        }

        /**
            Answer a list of the elements from <code>it</code>, in order, consuming
            that iterator. Canonical implementation of toList().
        */
        public static List asList( ExtendedIterator it ) {
    	List result = new List();
    	while (it.hasNext())
    	    result.add( it.next() );
    	return result;
        }

        /**
        make a new iterator, which is our elements that do not pass the filter
         */        
        public ExtendedIterator filterDrop( final Filter f )
        { return new FilterDropIterator( f, this ); }
}
