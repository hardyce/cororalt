/** List.java
 *
 *  this class implements java.util.List, which is not present
 *  in the latest versione of the J2ME library at the time we are
 *  implementing this code, and behaves the same.
 */

package ie.tcd.cs.nembes.coror.util;

import ie.tcd.cs.nembes.coror.util.iterator.IteratorImpl;

import java.lang.IndexOutOfBoundsException;
import java.util.Vector;

/**
 *
 * @author ilBuccia
 */
public class List implements Collection{
    
    private final int INCREMENT = 1;
    
    protected Vector v;
    
    /** Creates a new instance of List */
    public List() {
        v = new Vector(0,INCREMENT);
    }
    
    public List(Vector newVector) {
        v = newVector;
    }
    
    public List(int i) {
	this(new Vector(i,1));
    }
    
    public boolean add(Object o) {
        if(o!=null) {
            v.addElement(o);
            return true;
        }
        else
            return false;
    }
    
    public boolean contains(Object o) {
//        int i=0;
//        boolean found = false;
//        while((i<v.size())&&(!found))
//            found = v.elementAt(i++).equals(o);
//        return found;
        return v.contains(o);
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof List))
            return false;
        else
            if(v.size() != ((List)o).size())
                return false;
            else {
                boolean different = false;
                int i = 0;
                while((!different)&&(i<v.size())) {
                    different = (v.elementAt(i).equals(((List)o).get(i)));
                    i++;
                }
                return different;
            }
    }
    
    public int indexOf(Object o) {
        return v.indexOf(o);
//        int result = -1;
//        int i = 0;
//        while((i<v.size())&&(result == -1)) {
//            if(v.elementAt(i).equals(o))
//                result = i;
//            i++;
//        }
//        return result;
    }
    
    public int lastIndexOf(Object o) {
        int result = -1;
        int i = v.size();
        while((i>0)&&(result == -1))
            if(v.elementAt(--i).equals(o))
                result = i;
        return result;
    }

    public boolean isEmpty() {
        return (v.size() == 0);
    }
    
    public Iterator iterator() {
        return new IteratorImpl(v);
    }
    
    public Object get(int index) {
        return v.elementAt(index);
    }
    
    public int size() {
        return v.size();
    }
    
    public boolean remove(Object o) {
	return v.removeElement(o);
    }

    public Object remove(int i) {
	Object aus = v.elementAt(i);
	v.removeElementAt(i);
	return aus;
    }
    
    /**
     * J2ME Version: newly added methods
     */
	/**
	 * Duplicate a list and arrange elements as the same order the its iterator gives
	 * @param l The list to be duplicated
	 */
	public List(List l){
		this((int)(l.size()*1.1f));
		for(int i=0; i<l.size(); i++){
			v.addElement(l.get(i));
		}
	}
	
	/**
	 * Copy the list into an array
	 * @return
	 */
	public Object[] toArray(){
		Object[] a = new Object[v.size()];
		v.copyInto(a);
		return a;
	}
	
	/**
	 * Copy the list into the specified array 
	 * @param a
	 * @return
	 */
	public Object[] toArray(Object[] a){
		int size = v.size();
		if(a.length < size)
			a = new Object[size];
			
		v.copyInto(a);
		
		if(a.length > size)
			a[size + 1] = null;
		
		return a;
	}

	/**
	 * append a collection at the end of this this in a correct sequencce
	 */
	public boolean addAll(Collection c) {
		for(Iterator i = c.iterator(); i.hasNext();){
			v.addElement(i.next());
		}
		return true;
	}
	
	/**
	 * Add an element at specific position in the list.
	 * @param index
	 * @param element
	 */
	public void add(int index, Object element){
		v.insertElementAt(element, index);
	}
	
	/**
	 * Not implemented
	 */
	public void clear() {
		
	}

	/**
	 * Not implemented
	 */
	public boolean containsAll(Collection c) {
		return false;
	}

	/**
	 * Not implemented
	 */
	public boolean removeAll(Collection c) {
		return false;
	}

	/**
	 * Not implemented
	 */
	public boolean retainAll(Collection c) {
		return false;
	}
	
	public Object clone(){
		List cloneList = new List(this.size());
		for(int i=0; i<this.size(); i++){
			cloneList.add(v.elementAt(i));
		}
		return cloneList;
	}    
    
}
