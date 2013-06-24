/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.util.CacheMap;
import ie.tcd.cs.nembes.coror.util.Set;
import ie.tcd.cs.nembes.coror.util.Map;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.Coror;
import ie.tcd.cs.nembes.coror.debug.Debugger;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.RETEQueue.Count;
import java.lang.UnsupportedOperationException;
import java.util.Vector;

/**
 * 
 * RETEQueueNS is a RETEQueue with node sharing
 * 
 * @author WEI TAI
 */
public class RETEQueueNS extends RETESourceNode implements RETESinkNode, SharedNodeI {
    
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
    protected RETEQueueNS sibling;
    
    /** Indicating the parent filter node of this RETE queue.*/
    protected RETESourceNode parent;
    
    protected boolean tidied = false;
    
    ////////////////////////////////////////////////////////
    /// Fields for test uses
    ////////////////////////////////////////////////////////
    
    /** the id of the filter node */
    public String id = "";
    
    /**
     * A 
     * @param strategy
     * @param left
     * @param pbvSize size of the partial binding vector this RETE queue will produce for successful joins
     */
    public RETEQueueNS(JoinStrategy strategy, boolean left, int pbvSize, String name){
        this(strategy, new CacheMap(), left, pbvSize, name);
    }
    
    /**
     * This constructor is called to share a buffer.
     */
    public RETEQueueNS(JoinStrategy strategy, Map queue, boolean left, int pbvSize, String name){
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
    void fire(PBV env, boolean isAdd, boolean processed){

        
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
//                if(!processed){
                    count.dec();
                    if (count.getCount() == 0) {
                        queue.remove(env);
                    }
//                }
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
            byte[] left = strategies.left, right = strategies.right;
            Node[] candidate, envNodes, newNodes;
            Object cont;
            boolean queueCounterProcessed, requiredNode;
            byte continuationSize = (byte)continuations.size();
            for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {
                
                //XXX use this when trace is not required.
                PBV cand = ((PBV)i.next());
                candidate = cand.getEnvironment();
                envNodes = env.getEnvironment();

//                System.err.println(env + " JOIN " +cand);
                
                matchOK = true;
                

                Debugger.NoJ_All++;
//                
//                if(Coror.printJoin) System.err.print(beAsString(envNodes) + " + "+beAsString(candidate));
                
                for (byte j = 0; j < strategyCount; j++) {
                    if ( ! candidate[right[j]].sameValueAs(envNodes[left[j]])) {
//                      as the nodes with the same values are often the same nodes so this might be a potential optimization.
//                    if ( candidate[right[j]] != (envNodes[left[j]])) { 
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

                    Debugger.NoSJ_All++;
                    // generate a new partial binding vector for successful joins combining the left partial binding vector and the right partial binding vector
                    newNodes = new Node[pbvSize];
                    //System.arraycopy(envNodes, 0, newNodes, 0, envNodes.length);
                    for (byte j = 0; j < envNodes.length; j++) {
                            newNodes[j] = envNodes[j];
                    }
                    // copy right partial binding vector to the new vector
                    // XXX this is not an efficient copy algorithm
                    byte insPos = (byte)envNodes.length;
                    for(byte k = 0; k < candidate.length; k++){
                        requiredNode = true;
                        for(byte n = 0; n<strategies.count; n++){
                            if(k == strategies.right[n]){
                                requiredNode = false;
                                break;
                            }
                        }
                        if(requiredNode){
                            newNodes[insPos++] = candidate[k];
                        }
                    }

//                    if(Coror.printJoin) System.err.println(" = " + beAsString(newNodes));
//                        PBV newEnv;
//                    if(!Coror.printTrace) newEnv = new PBV(newNodes);
//                    else {
//                        List newTrace = new List(((PartialTraceBindingVector_Test)env).trace);
//                        newTrace.add(new Trace(id, new Pair(env, cand)));
//                        newEnv = new PartialTraceBindingVector_Test(newNodes, newTrace);
//                   }
                    
                    PBV newEnv = getNewPartialBindingVector(env, cand, newNodes);
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
                    
                    queueCounterProcessed = false;
                    for(int j=0; j<continuationSize; j++){
                        cont = continuations.get(j);
                        if(cont instanceof RETEQueueNS)
                            if(!queueCounterProcessed){
                                ((RETEQueueNS)cont).fire(newEnv, isAdd);
                            }
                            else{
                                ((RETEQueueNS)cont).fire(newEnv, isAdd, true);
                                queueCounterProcessed = true;
                            }
                        else
                            ((RETESinkNode)cont).fire(newEnv, isAdd);
//                        ((RETESinkNode)continuations.get(j)).fire(newEnv, isAdd);
                    }
                }
//                 if(Coror.printJoin) System.err.println();
            }            
        }
        else{
            byte[] left = strategies.left;
            byte[] right = strategies.right;
            Node[] candidate, envNodes, newNodes;
            boolean matchOK, requiredNode, queueCounterProcessed=false;
            byte strategyCount = (byte)strategies.count, continuationSize = (byte)continuations.size();;
            PBV newEnv;
            Object cont;
            for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {

                //XXX use this when trace is not required.
                PBV cand = (PBV)i.next();
                candidate = cand.getEnvironment();
                envNodes = env.getEnvironment();
//                System.err.println(cand + " JOIN " +env);
               matchOK = true;
                for (byte j = 0; j < strategyCount; j++) {
                    if ( ! candidate[left[j]].sameValueAs(envNodes[right[j]])) {
//                      as the nodes with the same values are often the same nodes so this might be a potential optimization.
//                    if ( candidate[left[j]] != envNodes[right[j]]) {
                        matchOK = false;
                        break;
                    }
                }
                
//                if(Coror.printJoin) System.err.print(beAsString(envNodes) + " + "+beAsString(candidate));

                Debugger.NoJ_All++;
                
                if (matchOK) {

                    Debugger.NoSJ_All++;
                    // generate a new partial binding vector for successful joins combining the left partial binding vector and the right partial binding vector
                    newNodes = new Node[pbvSize];
                    // copy left partial binding vector
                    byte insPos = (byte)candidate.length;
                    for (int j = 0; j < insPos; j++) {
                        newNodes[j] = candidate[j];
                    }
                    // copy right partial binding vector
                    // XXX this is not an efficient copy algorithm
                    
                    for(byte k = 0; k < envNodes.length; k++){
                        requiredNode = true;
                        for(byte n = 0; n<strategyCount; n++){
                            if(k == right[n]){
                                requiredNode = false;
                                break;
                            }
                        }
                        if(requiredNode){
                            newNodes[insPos ++] = envNodes[k];
                        }
                    }

//                    if(Coror.printJoin) System.err.println(" = " + beAsString(newNodes));
//                    PBV newEnv;
//                    List newTrace;
//                    if(!Coror.printTrace) newEnv = new PBV(newNodes);
//                    else {
//                        newTrace = new List(((PartialTraceBindingVector_Test)cand).trace);
//                        newTrace.add(new Trace(id, new Pair(cand, env)));
//                        newEnv = new PartialTraceBindingVector_Test(newNodes, newTrace);
//                    }
                     newEnv = getNewPartialBindingVector(cand, env, newNodes);
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
                    
                    for(int j=0; j<continuationSize; j++){
                        cont = continuations.get(j);
                        if(cont instanceof RETEQueueNS)
                            if(!queueCounterProcessed){
                                ((RETEQueueNS)cont).fire(newEnv, isAdd);
                            }
                            else{
                                ((RETEQueueNS)cont).fire(newEnv, isAdd, true);
                                queueCounterProcessed = true;
                            }
                        else
                            ((RETESinkNode)cont).fire(newEnv, isAdd);
//                        ((RETESinkNode)continuations.get(j)).fire(newEnv, isAdd);
                    }
                }
            }            
        }        
    }
    
    private PBV getNewPartialBindingVector(PBV left, PBV right, Node[] payload){
        if(left instanceof TemporalPBV && right instanceof TemporalPBV){
            return new TemporalPBV(payload, ((TemporalPBV)left).getTimeStamp() > ((TemporalPBV)right).getTimeStamp()? ((TemporalPBV)right).getTimeStamp():((TemporalPBV)left).getTimeStamp());
        }
        else if (left instanceof TemporalPBV)
            return new TemporalPBV(payload, ((TemporalPBV)left).getTimeStamp());
        else if (right instanceof TemporalPBV)
            return new TemporalPBV(payload, ((TemporalPBV)right).getTimeStamp());
        else
            return new PBV(payload);
    }
    
    /**
     * Remove those buffered T-PBVs within a given time slot.
     * @param start start of the time slot.
     * @param end end of the time slot.
     */
    public void sweepBuffer(long start, long end,boolean sib){
        
        if(tidied) return;       
        
        Set vectors = queue.keySet();
        //System.out.println(vectors.size());
        for(int i=vectors.size()-1; i>=0; i--){
            
             Object pbv = vectors.get(i);
             if(!(pbv instanceof TemporalPBV))
                     continue;
             if(((TemporalPBV)pbv).within(start, end)){
                 //System.out.println("removed "+vectors.get(i).toString());
                 vectors.remove(i);
                 
                 i--;
             }
        }
            
        //if(sibling!=null && sibling instanceof RETEQueueNS)
        
        if(!sib){
            sibling.sweepBuffer(start, end,true);
        }
        for(int i=0; i<continuations.size(); i++){
            Object continuation = continuations.get(i);
            if(continuation != null && continuation instanceof RETEQueueNS){
                ((RETEQueueNS)continuation).sweepBuffer(start, end,false);
            }
                
        }
    }
    
    /**
     * restore the tidied status after tidy up is finished.
     */
    public void finishSweep(){
        tidied = false;
        sibling.tidied = false;
        for(int i=0; i<continuations.size(); i++){
            Object continuation = continuations.get(i);
            if(continuation != null && continuation instanceof RETEQueueNS){
                ((RETEQueueNS)continuation).finishSweep();
            }      
        }        
    }
    
//    private String beAsString(Node[] be){
//        String str = "[";
//        for(int i=0; i<be.length; i++){
//            str += (be[i].getLocalName()+" ");
//        }
//        str += "]";
//        return str;
//    }
    
    @Override
    public void fire(PBV env, boolean isAdd){
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

//    @Override
//    public void fire(IntermediateBindingVector env, boolean isAdd) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public void addContinuation(RETESinkNode continuation) {
        if(continuations.contains(continuation)) return;
        continuations.add(continuation);
        if(sibling != null)
            sibling.continuations.add(continuation);
        if(continuation instanceof RETEQueueNS)
            ((RETEQueueNS)continuation).parent = this;
    }

    @Override
    public List getContinuations() {
        return continuations;
    }    
       
    public void setSibling(RETEQueueNS sibling){
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