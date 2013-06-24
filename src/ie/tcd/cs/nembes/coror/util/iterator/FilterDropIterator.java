package ie.tcd.cs.nembes.coror.util.iterator;

import ie.tcd.cs.nembes.coror.util.Iterator;




/**
A subclass of FiterIterator which discards the elements that pass the
filter.

@author Wei
*/
public class FilterDropIterator extends FilterIterator implements Iterator
{
public FilterDropIterator( Filter f, Iterator it )
    { super( f, it ); }

protected boolean accept( Object x )
    { return !f.accept( x ); }
}
