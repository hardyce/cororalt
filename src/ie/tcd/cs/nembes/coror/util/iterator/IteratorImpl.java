/*
 * IteratorImpl.java
 *
 * Implementation of Iterator
 */

package ie.tcd.cs.nembes.coror.util.iterator;

import java.util.NoSuchElementException;
import java.util.Vector;
import ie.tcd.cs.nembes.coror.util.Iterator;

/**
 *
 * @author ilBuccia
 */
public class IteratorImpl implements Iterator {
    
    protected Vector v;
    protected int index;
    protected boolean gotNext = false;
    
    /** Creates an empty iterator */
    public IteratorImpl() {
	this(new Vector(0));
    }
    
    /** Creates a new instance of Iterator */
    public IteratorImpl(Vector newVector) {
        v = newVector;
        index = 0;
    }
    
    public boolean hasNext() {
        if(v.size() > index)
            return true;
        else
            return false;
    }
    
    public Object next() throws NoSuchElementException {
        gotNext = true;
        return v.elementAt(index++);
    }

    public void remove() {
	if(!gotNext) throw new IllegalStateException("Next is not called before remove is called.");
        gotNext = false;
        v.remove(--index);
    }
    
}
