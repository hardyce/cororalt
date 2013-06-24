package ie.tcd.cs.nembes.coror.reasoner;

import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.coror.util.iterator.ClosableIterator;

/**
 * Some simple helper methods used when working with Finders,
 * particularly to compose them into cascade sequences.
 * The cascades are designed to cope with null Finders as well.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.11 $ on $Date: 2008/01/02 12:07:00 $
 */
public class FinderUtil {
    
    /**
     * Create a continuation object which is a cascade of two
     * continuation objects.
     * @param first the first Graph/Finder to try
     * @param second the second Graph/Finder to try
     */
    public static Finder cascade(Finder first, Finder second) {
        if (first == null || (first instanceof FGraph && ((FGraph)first).getGraph() == null)) return second;
        if (second == null || (second instanceof FGraph && ((FGraph)second).getGraph() == null)) return first;
        return new Cascade(first, second);
    }
    
    /**
     * Create a continuation object which is a cascade of three
     * continuation objects.
     * @param first the first Graph/Finder to try
     * @param second the second Graph/Finder to try
     * @param third the third Graph/Finder to try
     */
    public static Finder cascade(Finder first, Finder second, Finder third) {
        return new Cascade(first, cascade(second, third));
    }
    
    /**
     * Create a continuation object which is a cascade of four
     * continuation objects.
     * @param first the first Graph/Finder to try
     * @param second the second Graph/Finder to try
     * @param third the third Graph/Finder to try
     * @param fourth the third Graph/Finder to try
     */
    public static Finder cascade(Finder first, Finder second, Finder third, Finder fourth) {
        return new Cascade(first, cascade(second, cascade(third, fourth)));
    }
    
    /**
     * Inner class used to implement cascades of two continuation objects
     */
    private static class Cascade implements Finder {
        /** the first Graph/Finder to try */
        Finder first;
        
        /** the second Graph/Finder to try */
        Finder second;
        
        /**
         * Constructor 
         */
        Cascade(Finder first, Finder second) {
            this.first = first;
            this.second = second;
        }
        
        /**
         * Basic pattern lookup interface.
         * @param pattern a TriplePattern to be matched against the data
         * @return a ClosableIterator over all Triples in the data set
         *  that match the pattern
         */
        public ExtendedIterator find(TriplePattern pattern) {
            if (second == null) {
                return first.find(pattern);
            } else if (first == null) {
                return second.find(pattern);
            } else {
                return first.findWithContinuation(pattern, second);
            }
        }
        
        /**
         * Extended find interface used in situations where the implementator
         * may or may not be able to answer the complete query. It will
         * attempt to answer the pattern but if its answers are not known
         * to be complete then it will also pass the request on to the nested
         * Finder to append more results.
         * @param pattern a TriplePattern to be matched against the data
         * @param continuation either a Finder or a normal Graph which
         * will be asked for additional match results if the implementor
         * may not have completely satisfied the query.
         */
        public ExtendedIterator findWithContinuation(TriplePattern pattern, Finder continuation) {
            return (FinderUtil.cascade(first, second, continuation)).find(pattern);
        }

        /**
         * Return true if the given pattern occurs somewhere in the find sequence.
         */
        public boolean contains(TriplePattern pattern) {
            ClosableIterator it = find(pattern);
            boolean result = it.hasNext();
            it.close();
            return result;
        }

    }
}

