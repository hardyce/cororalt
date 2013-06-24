/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.CororReasoner;
import ie.tcd.cs.nembes.coror.debug.Debugger;
import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.util.Iterator;
import ie.tcd.cs.nembes.microjenaenh.util.List;
import ie.tcd.cs.nembes.microjenaenh.util.Map;
import ie.tcd.cs.nembes.microjenaenh.util.Set;

/**
 *
 * @author Wei Tai
 */
public class RETEQueueSharing_deprecated extends RETEQueue implements RETESinkNode {
    
    protected List siblingList = new List();
    
    // fields for current queue
    
    protected byte[] bindingIndices = null;
    
    protected boolean left;
    
    protected int ruleId;
    
//    protected static int callTime = 0;
    
    public byte[] matchPositions;
    
    /** a field used to avoid joining current environment to a same sibling */
    private Set processedSiblings = new Set();

//    
//    private boolean inUse = false;
    
    /** 
     * @deprecated replaced by RETEQueueSharing(ruleId, matchIndices)
     * Constructor. The queue is not usable until it has been bound
     * to a sibling and a continuation node.
     * @param A set of variable indices which should match between the two inputs
     */
    public RETEQueueSharing(byte[] matchIndices) {
    }
    
//    public RETEQueueSharing(Integer ruleId, byte[] matchIndices){
//        matchIndicesMap.put(ruleId, matchIndices);
//    }
    
    public RETEQueueSharing(RETESibling sib) {
        super();
        siblingList.add(sib);
    }
    
    /** 
     * @deprecated replaced by RETEQueueSharing(Map matchIndicesMap)
     * Constructor. The queue is not usable until it has been bound
     * to a sibling and a continuation node.
     * @param A List of variable indices which should match between the two inputs
     */
    public RETEQueueSharing(List matchIndexList) {
    }

    public void fire(IntermediateBindingVector env, boolean isAdd) {
//        if(StartApplication.state != 0)
//            System.err.println("DEBUG (RETEQueueSharing::fire) : inserting "+ (env instanceof TempIntermediateBindingVector?"TempIBV.":"IBV."));
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
                // NOTE if Fast remove
//                if(env instanceof TempIntermediateBindingVector){
//                    byte updateID = ((TempIntermediateBindingVector)env).updateID;
//                    // A vector containing all intermediate binding vectors will be constructed for every env. low performance
//                    Vector allBVs = queue.valuesVector();
//                    for(int i = 0; i<queue.size(); i++){
//                        Map.Entry entry = (Map.Entry)allBVs.elementAt(i);
//                        IntermediateBindingVector bv = (IntermediateBindingVector)entry.getKey();
//                        if(bv instanceof TempIntermediateBindingVector){
//
//                        }
//                    }
//                }
            }
        }
       
        // if this call is only for preMatch then return
        if(siblingList.size() == 0){
            return;
        }
        
        
//        RETEQueue sibling = null;
//        RETESinkNode continuation = null;
//        byte[] matchIndices = null;
//        byte[] bindingIndices = null;
//        boolean left = true;
//        byte[] matchPositions = null;

        boolean exist = false;
        
        Node[] candidate = null;        
        Node[] envNodes = env.getEnvironment();
        Node[] firstBV = null;
        Node[] secondBV = null;
        byte[] secondBI = null;
//        byte[] secondMP = null;
        boolean matchOK = true;
        IntermediateBindingVector newEnv = null;
        
        for(Iterator siblingsIt = siblingList.iterator(); siblingsIt.hasNext(); ){
            RETESibling siblingEntry = (RETESibling)siblingsIt.next();

            restoreMe(siblingEntry);
            restoreSibling(siblingEntry);
            
//            sibling = siblingEntry.sibling;
//            continuation = siblingEntry.continuation;
//            matchIndices = siblingEntry.matchIndices;
//            bindingIndices = siblingEntry.myBindingIndices;
//            left = siblingEntry.iAmleft;
//            matchPositions = siblingEntry.myMatchPositions;
//            
//            ((RETEQueueSharing)sibling).bindingIndices = siblingEntry.siblingBindingIndices;
//            ((RETEQueueSharing)sibling).matchPositions = siblingEntry.siblingMatchPositions;  
            
            if(!processed(siblingEntry.sibling))
                processedSiblings.add(siblingEntry.sibling);
            else{
                if (continuation instanceof RETEQueueSharing)
                    continue;
            }

            // Cross match new token against the entries in the sibling queue

            byte[] candidateBI = ((RETEQueueSharing)sibling).bindingIndices;
            
            for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {
                candidate = ((IntermediateBindingVector)i.next()).getEnvironment();
                // BI stands for Binding Indices.
                
                Debugger.NoJ_All++;
                
                matchOK = true;
                for(byte j = 0; j < matchIndices.length; j++) {
//                    byte candidatePosition = (((RETEQueueSharing)sibling).matchPositions)[j];
//                    byte envNodesPosition = matchPositions[j];
                    if(! candidate[(((RETEQueueSharing)sibling).matchPositions)[j]].sameValueAs(envNodes[matchPositions[j]])){
                        matchOK = false;
                        break;
                    }                    
                }          
                if (matchOK) {
                    Debugger.NoSJ_All++;
                    Node[] newNodes = new Node[candidate.length + envNodes.length - matchIndices.length];

                    
                    if(left == true){
                        firstBV = candidate;
                        secondBV = envNodes;
                        secondBI = bindingIndices;
                    }
                    else{
                        firstBV = envNodes;
                        secondBV = candidate;
                        secondBI = candidateBI;
                    }
                        
                    
                    for (byte j = 0; j < firstBV.length; j++) {
                        newNodes[j] = firstBV[j];
                    }
                    
                    byte next = (byte)firstBV.length;
                    
                    
                    for(byte j = 0; j < secondBI.length; j++) {
                        exist = false;
                        for(byte n = 0; n < matchIndices.length; n++) {
                            if(secondBI[j] == matchIndices[n]){
                                exist = true;
                                break;
                            }
                        }
                        if(exist == false){
                            newNodes[next++] = secondBV[j];
//                            newNodesBI[next++] = secondBI[j];
                        }
                    }
                    
                    
                    if(env instanceof TempIntermediateBindingVector)
                        newEnv = new TempIntermediateBindingVector(newNodes, ((TempIntermediateBindingVector)env).updateID);
                    else
                        newEnv = new IntermediateBindingVector(newNodes);
                    // Fire the successor processing
                    continuation.fire(newEnv, isAdd);
                    restoreMe(siblingEntry);
                    restoreSibling(siblingEntry);
                }
            }
        }
        processedSiblings.clear();
    }      
    
    
    private void testPrintByteArray(byte[] tobePrint){
        for(int i = 0; i < tobePrint.length; i++){
            System.err.print((new Byte(tobePrint[i])).toString() + ", ");         
        }
        System.err.println();
    }

    private void testPrintBooleanArray(boolean[] tobePrint){
        for(int i = 0; i < tobePrint.length; i++){
            System.err.print((new Boolean(tobePrint[i])).toString() + ", ");         
        }  
        System.err.println();
    }
    
    private void testPrintObjArray(Object[] tobePrint){
        for(int i = 0; i < tobePrint.length; i++){
            System.err.print(tobePrint[i].toString() + ", ");         
        }   
        System.err.println();
    }

    public void crossJoin(){

        IntermediateBindingVector env = null;
        Node[] candidate = null;
        Node[] envNodes = null;
        Node[] newNodes = null;
        boolean matchOK = true;
        IntermediateBindingVector newEnv = null;
        
        boolean exist = false;
        
//        RETEQueue sibling = null;
//        RETESinkNode continuation = null;
//        byte[] matchIndices = null;
//        byte[] bindingIndices = null;
//        boolean left = true;
//        byte[] matchPositions = null;
                
        for(Iterator siblingsIt = siblingList.iterator(); siblingsIt.hasNext();){
            RETESibling siblingEntry = (RETESibling)siblingsIt.next();
            if(!siblingEntry.needCrossJoin){
                continue;
            }
            restoreMe(siblingEntry);
            restoreSibling(siblingEntry);
            
//            sibling = siblingEntry.sibling;
//            continuation = siblingEntry.continuation;
//            matchIndices = siblingEntry.matchIndices;
//            bindingIndices = siblingEntry.myBindingIndices;
//            left = siblingEntry.iAmleft;
//            matchPositions = siblingEntry.myMatchPositions;
//            
//            ((RETEQueueSharing)sibling).bindingIndices = siblingEntry.siblingBindingIndices;
//            ((RETEQueueSharing)sibling).matchPositions = siblingEntry.siblingMatchPositions;       
            
            if(!processed(siblingEntry.sibling))
                processedSiblings.add(siblingEntry.sibling);
            else
                if (continuation instanceof RETEQueueSharing)
                    continue;

            Iterator BindingVectorIt = queue.keySet().iterator();
            while(BindingVectorIt.hasNext()){
                env = (IntermediateBindingVector) BindingVectorIt.next();
                for (Iterator i = sibling.queue.keySet().iterator(); i.hasNext(); ) {
                    candidate = ((IntermediateBindingVector)i.next()).getEnvironment();
                    // BI stands for Binding Indices.
//                    candidateBI = ((RETEQueueSharing)sibling).bindingIndices;
                    envNodes = env.getEnvironment();

                    matchOK = true;
                    for(byte j = 0; j < matchIndices.length; j++) {
                        if(! candidate[(((RETEQueueSharing)sibling).matchPositions)[j]].sameValueAs(envNodes[matchPositions[j]])){
                            matchOK = false;
                            break;
                        }
                    }
                    Debugger.NoJ_All++;
                    if (matchOK) {
                        Debugger.NoSJ_All++;
                        // Instantiate a new extended environment
                        newNodes = new Node[(candidate.length + envNodes.length - matchIndices.length)];

                        for (byte j = 0; j < candidate.length; j++) {
                            newNodes[j] = candidate[j];
                        }

                        byte next = (byte)candidate.length;

                        for(byte j = 0; j < bindingIndices.length; j++) {
                            exist = false;
                            for(byte n = 0; n < matchIndices.length; n++) {
                                if(bindingIndices[j] == matchIndices[n]){
                                    exist = true;
                                    break;
                                }
                            }
                            if(exist == false){
                                newNodes[next++] = envNodes[j];
                            }
                        }
//                        for(byte j = 0; j < envNodes.length; j++) {
//                            exist = false;
//                            for(byte n = 0; n < matchPositions.length; n++) {
//                                if(j == matchPositions[n]){
//                                    exist = true;
//                                    break;
//                                }
//                            }
//                            if(exist == false){
//                                newNodes[next++] = envNodes[j];
//                            }
//                        }


                        newEnv = new IntermediateBindingVector(newNodes);
//                        System.err.println("Match success and the new IBV is "+TestUtilities.array2Str(newEnv.getEnvironment()));
                        // Fire the successor processing
                        continuation.fire(newEnv, true);

                        restoreMe(siblingEntry);
                        restoreSibling(siblingEntry);
                    }
                }
            }
        }  
        processedSiblings.clear();
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
        RETEQueueSharing clone = (RETEQueueSharing)netCopy.get(this);
        if (clone == null) {
            clone = new RETEQueueSharing();
            netCopy.put(this, clone);
            clone.siblingList = new List(siblingList.size());
            for(Iterator it = siblingList.iterator(); it.hasNext();){
                RETESibling curSib = (RETESibling)it.next();
                RETESibling copy = new RETESibling();
                
                // clone fields
                copy.conditionId = new Integer(curSib.conditionId.intValue());
                copy.continuation = (RETEQueueSharing)curSib.continuation.clone(netCopy, context);
                copy.iAmleft = curSib.iAmleft;
                copy.matchIndices = new byte[curSib.matchIndices.length];
                for(int i = 0; i<curSib.matchIndices.length; i++)
                    copy.matchIndices[i] = curSib.matchIndices[i];
                copy.me = clone;
                copy.myBindingIndices = new byte[curSib.myBindingIndices.length];
                for(int i = 0; i<curSib.myBindingIndices.length; i++)
                    copy.myBindingIndices[i] = curSib.myBindingIndices[i];
                copy.ruleId = new Integer(curSib.ruleId.intValue());
                copy.sibling = (RETEQueueSharing)curSib.sibling.clone(netCopy, context);
                copy.siblingBindingIndices = new byte[curSib.siblingBindingIndices.length];
                for(int i = 0; i < curSib.siblingBindingIndices.length; i++)
                    copy.siblingBindingIndices[i] = curSib.siblingBindingIndices[i];
                
            }
            clone.queue.putAll(queue);
        }
        return clone;
    }
    
//    public void release(){
//        inUse = false;
//    }
    
    public void restoreSibling(RETESibling siblingEntry){        
        RETEQueueSharing share = siblingEntry.sibling;
        if(share == this){
            share = new RETEQueueSharing();
            share.queue = queue;
            sibling = share;
        }
        share.ruleId = siblingEntry.ruleId.intValue();
        share.matchIndices = siblingEntry.matchIndices;
        share.sibling = siblingEntry.me;
        share.continuation = siblingEntry.continuation;
        share.bindingIndices = siblingEntry.siblingBindingIndices;
        share.left = !siblingEntry.iAmleft;
        share.matchPositions = siblingEntry.siblingMatchPositions;
    }
    
    public void restoreMe(RETESibling siblingEntry) {
        this.ruleId = siblingEntry.ruleId.intValue();
        this.matchIndices = siblingEntry.matchIndices;
        this.sibling = siblingEntry.sibling;
        this.continuation = siblingEntry.continuation;
        this.bindingIndices = siblingEntry.myBindingIndices;
        this.left = siblingEntry.iAmleft;
        this.matchPositions = siblingEntry.myMatchPositions;
    }
    
//  ===========================================================
//  methods for composable reasoner
    
    /**
     * A default constructor
     */
    public RETEQueueSharing(){
        super();
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
        return null;
    }

    /**
     * @deprecated replaced by the getContinuation( Integer ruleId )
     * @return null
     */
    public RETESinkNode getContinuation() {
        return null;
    }


    public int getSize() {
        return queue.size();
    }

    public void addBingdingIndices(Integer conditionId, byte[] bi) {
    }
    
    /**
     * To retrieve a list of RETESibling object each of which has RETEQueueSharing
     * continuation.
     * @return a list of RETEQueueSharing objs or null otherwise.
     */
    public List getSibsWithQueueContinuation(){
        List ret = new List();
        for(Iterator it = siblingList.iterator(); it.hasNext(); ){
            RETESibling sib = (RETESibling)it.next();
//            System.err.println("the type is " + sib.continuation.getClass());
            if(sib.continuation instanceof RETEQueueSharing){
                
                ret.add(sib);
            }
        }
        if(ret.size() != 0)
            return ret;
        else
            return null;
    }
    
        
    public void addSibling(RETESibling sib){
        siblingList.add(sib);
    }
    
    private boolean processed(RETEQueueSharing sibling){
        if(processedSiblings.contains(sibling))
            return true;
        return false;
    }
}
