package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.Coror;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.util.CacheMap;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.Map;

/******************************************************************
 * File:        RETEQueue.java
 * Created by:  Dave Reynolds
 * Created on:  09-Jun-2003
 * 
 * (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: RETEQueue.java,v 1.11 2008/01/02 12:06:16 andy_seaborne Exp $
 *****************************************************************

/**
 * Represents one input left of a join node. The queue points to 
 * a sibling queue representing the other leg which should be joined
 * against.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.11 $ on $Date: 2008/01/02 12:06:16 $
 */
public class RETEQueue extends RETESourceNode implements RETESinkNode {
    
    /** A multi-set of partially bound envionments */
    protected Map queue;
    
    /** A set of variable indices which should match between the two inputs */
    protected byte[] matchIndices;
    
    /** The sibling queue which forms the other half of the join node */
    protected RETEQueue sibling;
    
    /** The node that results should be passed on to */
    protected RETESinkNode continuation;
    
    /** 
     * Constructor. The queue is not usable until it has been bound
     * to a sibling and a continuation node.
     * @param A set of variable indices which should match between the two inputs
     */
    public RETEQueue(byte[] matchIndices) {
        super();
        this.matchIndices = matchIndices; 
        if(ReasonerConfig.useCacheMap)
            queue = new CacheMap();
        else{
            queue = new Map();
        }
    }
    
    /** 
     * Constructor. The queue is not usable until it has been bound
     * to a sibling and a continuation node.
     * @param A List of variable indices which should match between the two inputs
     */
    public RETEQueue(List matchIndexList) {
        super();
        if(ReasonerConfig.useCacheMap)
            queue = new CacheMap();
        else{
            queue = new Map();
        }
        int len = matchIndexList.size();
        matchIndices = new byte[len];
        for (int i = 0; i < len; i++) {
            matchIndices[i] = ((Byte)matchIndexList.get(i)).byteValue();
        }
    }
    
    
    /**
     * Set the sibling for this node.
     */
    public void setSibling(RETEQueue sibling) {
        this.sibling = sibling;
    }
    
    /**
     * Set the continuation node for this node (and any sibling)
     */
    public void setContinuation(RETESinkNode continuation) {
        this.continuation = continuation;
        if (sibling != null) sibling.continuation = continuation;
    }
    
    /** 
     * Propagate a token to this node.
     * @param env a set of variable bindings for the rule being processed. 
     * @param isAdd distinguishes between add and remove operations.
     */
    public void fire(BindingVector env, boolean isAdd) {

        // Store the new token in this store
        Count count = (Count)queue.get(env);
        if (count == null) {
            // no entry yet
            if (!isAdd) return;
            queue.put(env, new Count(1));
        } else {
            if (isAdd) {
                count.inc();
            } else {
                count.dec();
                if (count.getCount() == 0) {
                    queue.remove(env);
                }
            }
        }
       
        if(continuation == null || sibling == null){
            return;
        }
        
        // Cross match new token against the entries in the sibling queue
        for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {
            Node[] candidate = ((BindingVector)i.next()).getEnvironment();
            Node[] envNodes = env.getEnvironment();
            boolean matchOK = true;
            for (int j = 0; j < matchIndices.length; j++) {
                int index = matchIndices[j];
                if ( ! candidate[index].sameValueAs(envNodes[index])) {
                    matchOK = false;
                    break;
                }
            }
//            Coror.NoJ_All++;
            // Comment by Wei
            // If the matchIndices.length == 0, namely no common variable was found for
            // two adjacent clauses, all possible combinations will be constructed and 
            // propagated to the continuation using continuation.fire(newEnv, isAdd).
            if (matchOK) {
//                Coror.NoSJ_All++;
                // Instantiate a new extended environment
                Node[] newNodes = new Node[candidate.length];
                for (int j = 0; j < candidate.length; j++) {
                    Node n = candidate[j];
                    newNodes[j] = (n == null) ? envNodes[j] : n;
                }
                BindingVector newEnv = new BindingVector(newNodes);
                // Fire the successor processing
                continuation.fire(newEnv, isAdd);
            }
        }
    }

    /**
     * This method is called to perform the cross join between the first two 
     * conditions of a rule if delayed beta network building is used.
     */
    public void crossJoin(){
        
        Iterator BindingVectorIt = queue.keySet().iterator();
        
//        System.err.println(" === DEBUG (RETEQueue::crossJoin) : queue contains " + queue.size() + " triples, sibling contains "+ sibling.queue.size() + " triples.");
        
        while(BindingVectorIt.hasNext()){
            BindingVector env = (BindingVector) BindingVectorIt.next();
            
            // No sibling is defined. There is only one condition in the rule. No need
            // to perform cross join.
            if(sibling == null || continuation == null){
//                System.err.println("!!!!!!!!!!!! sibling or continuation can not be null while crossjoining !!!!!!!!!!!!!!!!!");
                return;
            }

            // Cross match the entry in this queue against the entries in the sibling queue
            for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {
                Node[] candidate = ((BindingVector)i.next()).getEnvironment();
                Node[] envNodes = env.getEnvironment();
                boolean matchOK = true;
//                Coror.NoJ_All++;
                for (int j = 0; j < matchIndices.length; j++) {
                    int index = matchIndices[j];
                    if ( ! candidate[index].sameValueAs(envNodes[index])) {
                        matchOK = false;
                        break;
                    }
                }
                // Comment by Wei
                // If the matchIndices.length == 0, namely no common variable was found for
                // two adjacent clauses, all possible combinations will be constructed and 
                // propagated to the continuation using continuation.fire(newEnv, isAdd).
                if (matchOK) {
//                    Coror.NoSJ_All++;
                    // Instantiate a new extended environment
                    Node[] newNodes = new Node[candidate.length];
                    for (int j = 0; j < candidate.length; j++) {
                        Node n = candidate[j];
                        newNodes[j] = (n == null) ? envNodes[j] : n;
                    }
                    BindingVector newEnv = new BindingVector(newNodes);
                    // Fire the successor processing
                    continuation.fire(newEnv, true);
                }
            }
        }
    }

//    @Override
//    public void fire(IntermediateBindingVector env, boolean isAdd) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public void fire(PBV env, boolean isAdd) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    
    /**
     * Clone this node in the network.
     * @param context the new context to which the network is being ported
     */
    public RETENode clone(Map netCopy, RETERuleContext context) {
        RETEQueue clone = (RETEQueue)netCopy.get(this);
        if (clone == null) {
            clone = new RETEQueue(matchIndices);
            netCopy.put(this, clone);
            clone.setSibling((RETEQueue)sibling.clone(netCopy, context));
            clone.setContinuation((RETESinkNode)continuation.clone(netCopy, context));
            clone.queue.putAll(queue);
        }
        return clone;
    }
    
//  ===========================================================
//  methods for composable reasoner
    
    /**
     * A default constructor
     */
    public RETEQueue(){
        super();
        if(ReasonerConfig.useCacheMap)
            queue = new CacheMap();
        else{
            queue = new Map();
        }        
    }
    
    public String toString() {
        
//        Printing binding environments        
//        Iterator vectorIt = queue.keySet().iterator();
//        
//        while(vectorIt.hasNext()){
//            
//            BindingVector env = (BindingVector) vectorIt.next();
//            System.err.println(env.toString());
//            str.concat(env.toString()+", ");
//        }
        
        
        
        return Integer.toString(queue.size());
    }

    public RETESinkNode getContinuation() {
        return continuation;
    }

    public int getSize() {
        return queue.size();
    }
}



/*
    (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
