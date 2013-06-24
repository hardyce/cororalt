/*
 * Set.java
 *
 *  this class substitutes java.util.Set, which is not present
 *  in the latest versione of the J2ME library at the time we are
 *  implementing this code
 */

package ie.tcd.cs.nembes.coror.util;

import ie.tcd.cs.nembes.coror.util.iterator.IteratorImpl;
import java.util.Vector;

/**
 *
 * @author ilBuccia
 */
public class Set extends List{
    
    /** Creates a new instance of Set */
    public Set() {
	super();
    }
    
    public Set(Vector newVector) {
	Iterator it = new IteratorImpl(newVector);
	while(it.hasNext())
	    this.add(it.next());
    }
    
    public boolean add(Object o) {
	if(this.contains(o))
	    return false;
	else
	    v.addElement(o);
	return true;
    }
    
    /**
     * J2ME Version
     */
	public Set(Set s){
		for(Iterator i = s.iterator(); i.hasNext();){
			v.addElement(i.next());
		}
	}
	
	public boolean addAll(Collection c) {
		for(Iterator i = c.iterator(); i.hasNext();){
			v.addElement(i.next());
		}
		return true;
	}

	public void clear() {
                v.removeAllElements();
	}

	public boolean containsAll(Collection c) {
		return false;
	}

	public boolean removeAll(Collection c) {
		return false;
	}

	public boolean retainAll(Collection c) {
		return false;
	}

	public Object[] toArray() {
		return null;
	}

	public Object[] toArray(Object[] a) {
		return null;
	}
}
