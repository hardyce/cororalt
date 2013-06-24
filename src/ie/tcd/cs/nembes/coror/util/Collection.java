package ie.tcd.cs.nembes.coror.util;

/**
 * A J2ME version of the interface java.util.Collection.
 * This migration version is a class rather than an interface.
 * @author Wei Tai
 *
 */
public interface Collection {
	
	public boolean add(Object o);
	
	public boolean addAll(Collection c);
	
	public void clear();
	
	public boolean contains(Object o);
	
	public boolean containsAll(Collection c);
	
	public boolean equals(Object o);
	
	public int hashCode();
	
	public boolean isEmpty();
	
	public Iterator iterator();
	
	public boolean remove(Object o);
	
	public boolean removeAll(Collection c);
	
	public boolean retainAll(Collection c);
	
	public int size();
	
	public Object[] toArray();
	
	public Object[] toArray(Object[] a);
}
