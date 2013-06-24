/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.graph;

/**
 *
 * @author Wei Tai
 */
public class NMTriple extends Triple {
    public Count count = new Count(0);

    public NMTriple( Node s, Node p, Node o ) {
        super(s, p, o);
    }

    /**
     * Inner class used to represent an updatable count.
     */
    protected static class Count {
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
