/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.util;

/**
 * This map caches both keys and values as sets hence the invocation of keySet()
 * and valueSet() needs not to recalculate the key set and value set if 
 * @author Wei Tai
 */
public class CacheMap extends Map{
        
    private Set keySetCache;
    private Set entrySetCache;
    // a new entry has been added. This causes both keyset cache and entryset cache to be recalculated.
    private boolean updateKeyCache = false;

    // only value has been changed for existing key. This only cause the entryset cache to be recalculated.
    private boolean updateEntryCache = false;
    
    
    public CacheMap() {
	super();
    }
    
    public CacheMap(Map other) {
	super(other);
    }
    
    public CacheMap(int initialVectorCapacity, int vectorAutoIncrement) {
	super(initialVectorCapacity, vectorAutoIncrement);
    }
    
    public Object put(Object newKey, Object newValue) {
//        Iterator i = new IteratorImpl(v);
//	boolean found = false;
//	Entry aus = null;
//	while(i.hasNext() && !found) {
//	    aus = ((Entry)i.next());
//	    found = (aus.getKey().equals(newKey));
//	}
//	if(found) {
//	    Object ausValue = aus.getValue();
//	    aus.setValue(newValue);
//            return ausValue;
//	}
//	else {
//            update = true;
//	    v.addElement(new Entry(newKey, newValue));
//	    return null;
//	}
        for(int i = 0; i < v.size(); i++){
            Entry aus = (Entry)v.elementAt(i);
            if(aus.getKey().equals(newKey)){
                Object ausValue = aus.getValue();
                aus.setValue(newValue);
                updateEntryCache = true;
                return ausValue;
            }
        }
        updateKeyCache = true;
        updateEntryCache = true;
        v.addElement(new Entry(newKey, newValue));
        return null;
    }
    
    public Set keySet() {
	if(updateKeyCache || keySetCache == null){
            updateKeyCache = false;
            return keySetCache = super.keySet();
        }
        else{
            return keySetCache;
        }
    }
    
    public Set entrySet() {
        if(updateEntryCache || entrySetCache == null){
            updateEntryCache = false;
            return entrySetCache = super.entrySet();          
        }
        else{
            return entrySetCache;
        }
    }
    
    public Object remove(Object searchKey) {
        Object o = super.remove(searchKey);
        updateKeyCache = (o == null? false: true);
        updateEntryCache = updateKeyCache;
        return o;
    }
    
//    public boolean containsKey(Object o) {
//        if(update == false && keySetCache != null){
//            return keySetCache.contains(o);
//        }
//        else{
//            return super.containsKey(o);
//        }
//    }
    
}
