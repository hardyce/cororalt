package ie.tcd.cs.nembes.coror.util.iterator;

import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.Set;


public interface ExtendedIterator extends ClosableIterator {
	/**
	    return a new iterator which delivers all the elements of this iterator and
	    then all the elements of the other iterator. Does not copy either iterator;
	    they are consumed as the result iterator is consumed.
	*/
	public ExtendedIterator andThen( ClosableIterator other );
	
	/**
	    Answer a list of the [remaining] elements of this iterator, in order,
	    consuming this iterator.
	*/
	public List toList();
	
	/**
	   Answer a set of the [remaining] elements of this iterator, in order,
	   consuming this iterator.
	*/
	public Set toSet();
	
    /**
    return a new iterator containing only the elements of _this_ which
    are rejected by the filter _f_. The order of the elements is preserved.
    Does not copy _this_, which is consumed as the reult is consumed.
	*/
	public ExtendedIterator filterDrop( Filter f );
}
