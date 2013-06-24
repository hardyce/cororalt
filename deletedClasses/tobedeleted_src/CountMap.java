/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.util;

import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import java.util.Vector;

/**
 * A map that associate a counter with each element in the map. Vector methods
 * are supplied allowing more flexible operations.
 * @author Wei Tai
 */
public class CountMap extends Map{

    /** Creates a new instance of Map */
    public CountMap() {
	super();
    }

    public CountMap(CountMap other) {
	super(other);
    }

    public CountMap(int initialVectorCapacity, int vectorAutoIncrement) {
	super(initialVectorCapacity, vectorAutoIncrement);
    }

    public Object put(Object newKey, Object newValue){
        if(!(newValue instanceof Count)){
            throw new RuntimeException("the value of CountMap should be Count.");
        }
        else{
            return super.put(newKey, newValue);
        }
    }

    /**
     * Add a triple
     * @param triple
     * @return the count of the triple after addition
     */
    public Count addElement(Object triple){
        Count ct = (Count)get(triple);
        if(ct != null) ct.inc();
        else {
            ct = new Count(1);
            put(triple, ct);
        }

//        if(StartApplication.state > 0){
//            System.err.println(" DEBUG (CountMap::addElement) : adding "+triple+" : "+ct.count);
//        }
        return ct;
    }

    /**
     * remove a element
     * @param element
     * @return the count of the triple after been removed
     */
    public Count removeElement(Object triple){
        Count ct = (Count)get(triple);
        if(ct != null){
            ct.dec();
            if(ct.count == 0)
                super.remove(triple);
        }
//        if(StartApplication.state > 0){
//            System.err.println(" DEBUG (CountMap::removeElement) : removing "+triple+" : "+(ct != null?ct.count:0));
//        }
        return ct;
    }

    /**
     * Vector operation.
     * return all triples with count in a java Vector.
     * @return
     */
    public Vector getAllElements(){
        return v;
    }

    /**
     * Vector operation.
     * Remove the ith element in the underlying vector
     * @param i the ith element to be removed
     */
    public Count removeElementAt(int i){
        Entry e = (Entry)v.elementAt(i);
        Count c = (Count)e.getValue();
        c.dec();
        if(c.count == 0){
//            if(StartApplication.state > 0){
//                System.err.println(" DEBUG (CountMap::removeElementAt) : removing "+e.getKey()+" : "+(c != null?c.count:0));
//            }
            v.removeElementAt(i);
        }
        return c;
    }

    /**
     * retrieve the ith element
     * @param i the ith element to be retrieved
     * @return the ith element
     */
    public Triple elementAt(int i){
       Entry e = (Entry)v.elementAt(i);
       return (Triple)e.getKey();
    }

    /**
     * retrieve the count associate with the ith element
     * @param i the count associated to the ith element
     * @return
     */
    public Count countAt(int i){
        Entry e = (Entry)v.elementAt(i);
        return (Count)e.getValue();
    }

    
    
     /**
     * Inner class used to represent an updatable count.
     */
    public static class Count {
        /** the count */
        int count;

        /** Constructor */
        public Count(int count) {
            this.count = count;
        }

        /** Access count value */
        public int getCount() {
            return count;
        }

        /** Increment the count value */
        public void inc() {
            count++;
        }

        /** Decrement the count value */
        public void dec() {
            count--;
        }

        /** Set the count value */
        public void setCount(int count) {
            this.count = count;
        }
    }
}
