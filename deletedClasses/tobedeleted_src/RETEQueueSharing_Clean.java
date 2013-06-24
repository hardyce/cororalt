/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.CororReasoner;
import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl.RETEQueue.Count;
import ie.tcd.cs.nembes.microjenaenh.util.Iterator;
import ie.tcd.cs.nembes.microjenaenh.util.List;
import ie.tcd.cs.nembes.microjenaenh.util.Map;
import java.util.Vector;

/**
 * Test
 * 
 * Share the buffer rather than the node object.
 * 
 * @author WEI TAI
 */
public class RETEQueueSharing_Clean extends RETESourceNode implements RETESinkNode, SharedNodeI {
    
    /** replace matchIndices in RETEQueue */
    protected JoinStrategy strategies;

    /** continuation nodes */
    protected List continuations;
    
    /** if this queue is on the left */
    protected boolean left;
    
    /** the size of the partial binding vector this queue can generate. It is kept as a field to avoid computing this for every join. */
    protected int pbvSize;
    
    /** A multi-set of partially bound envionments */
    protected Map queue;
    
    /** The sibling queue which forms the other half of the join node */
    protected RETEQueueSharing_Clean sibling;
    
    /** Indicating the parent filter node of this RETE queue.*/
    protected RETESourceNode parent;
    
    ////////////////////////////////////////////////////////
    /// Fields for test uses
    ////////////////////////////////////////////////////////
    
    /** the id of the filter node */
    public String id = "";
    
//    /** number of joins */
//    public long noJ = 0;
//    
//    /** number of successful joins */
//    public long noSJ = 0;
//    
//    /** total number of joins for the entire network */
//    public static long noJ_All = 0;
//    
//    /** number of successful joins for the entire network */
//    public static long noSJ_All = 0;
    /**
     * A 
     * @param strategy
     * @param left
     * @param pbvSize size of the partial binding vector this RETE queue will produce for successful joins
     */
    public RETEQueueSharing_Clean(JoinStrategy strategy, boolean left, int pbvSize, String name){
        this(strategy, new Map(), left, pbvSize, name);
    }
    
    /**
     * This constructor is called to share a buffer.
     */
    public RETEQueueSharing_Clean(JoinStrategy strategy, Map queue, boolean left, int pbvSize, String name){
        this.queue = queue;
        this.strategies = strategy;
        this.left = left;
        this.pbvSize = pbvSize;
        this.continuations = new List();
        this.id = name;
    }
    
    @Override
    public void setContinuation(RETESinkNode continuation){
        throw new UnsupportedOperationException("The mothod setContinuation is replaced by addContinuation in RETEQueueSharing_Test");
    }
    
    @Override
    public RETESinkNode getContinuation(){
        throw new UnsupportedOperationException("Unsupported operation");        
    }
    /**
     * If the partial binding vector has been processed before then no need to 
     * change the count again. This is to maintain correct count for 
     */
    void fire(PartialBindingVector env, boolean isAdd, boolean processed){
        // Store the new token in this store
        Count count = (Count)queue.get(env);
        if (count == null) {
            // no entry yet
            if (!isAdd) return;
            queue.put(env, new Count(1));
        } else {
            if (isAdd && !processed) {
                    count.inc();
            } else {
                if(!processed){
                    count.dec();
                    if (count.getCount() == 0) {
                        queue.remove(env);
                    }
                }
            }
        }
       
//        // avoid siblings pointing to the same parent do the same joins twice. e.g. transtive predicates such as sameAs, subClassOf, subPropertyOf
//        if(parent == sibling.parent && left == false)
//            return;
//        
//        if(continuations.isEmpty() || sibling == null){
//            return;
//        }
        
        // choose different strategies if this queue is a left/right sibling
        if(left){
            boolean matchOK = true;
            byte strategyCount = (byte)strategies.count;
            byte l, r;
            for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {
                Node [] candidate = ((PartialBindingVector)i.next()).getEnvironment();
                
                //XXX use this when trace is not required.
//                Node[] candidate = ((PartialBindingVector)i.next()).getEnvironment();
                Node[] envNodes = env.getEnvironment();
//                if(candidate.length==2 
//                        && candidate[0].hasURI("http://owl.man.ac.uk/2005/sssw/teams#Person") 
//                        && candidate[1].hasURI("http://www.w3.org/2002/07/owl#Thing")
//                        && envNodes.length == 2
//                        && envNodes[0].hasURI("http://owl.man.ac.uk/2005/sssw/teams#Female")
//                        && envNodes[1].hasURI("http://owl.man.ac.uk/2005/sssw/teams#Person"))
//                    System.err.println("!!!!!!!!!!!!!!!!!!!!!!");

                
                matchOK = true;
                
                for (byte j = 0; j < strategyCount; j++) {
                    l = strategies.left[j];
                    r = strategies.right[j];
                    if ( ! candidate[r].sameValueAs(envNodes[l])) {
                        matchOK = false;
                        break;
                    }
                }
                
                /** 
                 * If the matchIndices.length == 0, namely no common variable was found for
                 * two adjacent clauses, matchOK will be true and all possible combinations 
                 * will be constructed and propagated to the continuations.
                 */
                if (matchOK) {
//                    noSJ ++; noSJ_All ++;
                    // generate a new partial binding vector for successful joins combining the left partial binding vector and the right partial binding vector
                    Node[] newNodes = new Node[pbvSize];
                    //System.arraycopy(envNodes, 0, newNodes, 0, envNodes.length);
                    for (byte j = 0; j < envNodes.length; j++) {
                            newNodes[j] = envNodes[j];
                    }
                    // copy right partial binding vector to the new vector
                    // XXX this is not an efficient copy algorithm
                    byte insPos = (byte)envNodes.length;
                    for(int k = 0; k < candidate.length; k++){
                        boolean requiredNode = true;
                        for(int n = 0; n<strategies.count; n++){
                            if(k == strategies.right[n]){
                                requiredNode = false;
                                break;
                            }
                        }
                        if(requiredNode){
                            newNodes[insPos++] = candidate[k];
                        }
                    }

                    PartialBindingVector newEnv = new PartialBindingVector(newNodes);

                    /**
                    * Pass the new partial binding vector to all continuations and fire them cascadingly,
                    * fire the continuations. Care needs to be taken on the count
                    * of queue when there are multiple continuations. All the associated
                    * beta node continuations are pointed to the same queue, hence when a 
                    * binding vector comes it will be added multiple times to the 
                    * shared queue. To solve this, we only add count for
                    * the first continuation and not for the rest continuations. A continuation
                    * of RETETerminal does not have such a problem.
                    * cause the count 
                    */
                    Object cont;
                    boolean queueCounterProcessed = false;
                    for(int j=0; j<continuations.size(); j++){
                        cont = continuations.get(j);
                        if(cont instanceof RETEQueueSharing_Clean)
                            if(!queueCounterProcessed){
                                ((RETEQueueSharing_Clean)cont).fire(newEnv, isAdd);
                                queueCounterProcessed = true;
                            }
                            else{
                                ((RETEQueueSharing_Clean)cont).fire(newEnv, isAdd, true);
                            }
                        else
                            ((RETESinkNode)cont).fire(newEnv, isAdd);
//                        if(CororReasoner.printTrace) ((PartialTraceBindingVector_Test)newEnv).setTrace(recoveryTrace);
                    }
                }
            }            
        }
        else{
            for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {
                //XXX use this when trace is not required.
                Node[] candidate = ((PartialBindingVector)i.next()).getEnvironment();
                Node[] envNodes = env.getEnvironment();
                
                boolean matchOK = true;
                for (int j = 0; j < strategies.count; j++) {
                    byte l = strategies.left[j];
                    byte r = strategies.right[j];
                    if ( ! candidate[l].sameValueAs(envNodes[r])) {
                        matchOK = false;
                        break;
                    }
                }
                
                
                if (matchOK) {
                    // generate a new partial binding vector for successful joins combining the left partial binding vector and the right partial binding vector
                    Node[] newNodes = new Node[pbvSize];
                    // copy left partial binding vector
                    for (int j = 0; j < candidate.length; j++) {
                        newNodes[j] = candidate[j];
                    }
                    // copy right partial binding vector
                    // XXX this is not an efficient copy algorithm
                    byte insPos = (byte)candidate.length;
                    for(byte k = 0; k < envNodes.length; k++){
                        boolean requiredNode = true;
                        for(byte n = 0; n<strategies.count; n++){
                            if(k == strategies.right[n]){
                                requiredNode = false;
                                break;
                            }
                        }
                        if(requiredNode){
                            newNodes[insPos ++] = envNodes[k];
                        }
                    }

                    PartialBindingVector newEnv = new PartialBindingVector(newNodes);


                    /**
                    * Pass the new partial binding vector to all continuations and fire them cascadingly,
                    * fire the continuations. Care needs to be taken on the count
                    * of queue when there are multiple continuations. All the associated
                    * beta node continuations are pointed to the same queue, hence when a 
                    * binding vector comes it will be added multiple times to the 
                    * shared queue. To solve this, we only add count for
                    * the first continuation and not for the rest continuations. A continuation
                    * of RETETerminal does not have such a problem.
                    * cause the count 
                    */
                    Object cont;
                    boolean queueCounterProcessed = false;
                    for(int j=0; j<continuations.size(); j++){
                        cont = continuations.get(j);
                        if(cont instanceof RETEQueueSharing_Clean)
                            if(!queueCounterProcessed){
                                ((RETEQueueSharing_Clean)cont).fire(newEnv, isAdd);
                                queueCounterProcessed = true;
                            }
                            else{
                                ((RETEQueueSharing_Clean)cont).fire(newEnv, isAdd, true);
                            }
                        else
                            ((RETESinkNode)cont).fire(newEnv, isAdd);
//                        if(CororReasoner.printTrace) ((PartialTraceBindingVector_Test)newEnv).setTrace(recoveryTrace);
                    }
                }
            }            
        }        
    }
    
    private String beAsString(Node[] be){
        String str = "[";
        for(int i=0; i<be.length; i++){
            str += (be[i].getLocalName()+" ");
        }
        str += "]";
        return str;
    }
    
    @Override
    public void fire(PartialBindingVector env, boolean isAdd){
        fire(env, isAdd, false);
            
    }

    @Override
    public RETENode clone(Map netCopy, RETERuleContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fire(BindingVector env, boolean isAdd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fire(IntermediateBindingVector env, boolean isAdd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addContinuation(RETESinkNode continuation) {
        if(continuations.contains(continuation)) return;
        continuations.add(continuation);
        if(sibling != null)
            sibling.continuations.add(continuation);
        if(continuation instanceof RETEQueueSharing_Clean)
            ((RETEQueueSharing_Clean)continuation).parent = this;
    }

    @Override
    public List getContinuations() {
        return continuations;
    }    
       
    public void setSibling(RETEQueueSharing_Clean sibling){
        this.sibling = sibling;       
    }
    
    public JoinStrategy getJoinStrategies(){
        return strategies;
    }

    @Override
    public String getNodeID() {
        return id;
    }
}
