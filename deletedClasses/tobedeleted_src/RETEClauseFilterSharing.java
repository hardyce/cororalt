package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.CororReasoner;
import ie.tcd.cs.nembes.coror.debug.Debugger;
import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import ie.tcd.cs.nembes.microjenaenh.reasoner.TriplePattern;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.Functor;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh.TempTriple;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.test.TestUtilities;
import ie.tcd.cs.nembes.microjenaenh.util.Iterator;
import ie.tcd.cs.nembes.microjenaenh.util.List;
import ie.tcd.cs.nembes.microjenaenh.util.Map;
import ie.tcd.cs.nembes.microjenaenh.util.Set;

/******************************************************************
 * File:        RETEClauseFilterSharing.java
 * Created by:  Wei Tai
 * Created on:  06-Oct-2009
 *****************************************************************/


/**
 * Checks a triple against the grounded matches and intra-triple matches
 * for a single rule clause. If the match passes it creates a binding
 * environment token and passes it on the the RETE network itself. The checks
 * and bindings are implemented using a simple byte-coded interpreter.
 */
public class RETEClauseFilterSharing_Deprecated extends RETESourceNode implements FireTripleI{
    
    /** Contains the set of byte-coded instructions and argument pointers */
    protected byte[] instructions;
    
    /** Contains the object arguments referenced from the instructions array */
    protected Object[] args;
    
    /** The network node to receive any created tokens 
     * Multiple continuations are for multiple RETETerminalNode. There are some
     * rules with the same singleton condition but different functors. This can 
     * cause a shared RETEClauseFilter connects to different RETETerminalNode. However a
     * RETEClauseFilterSharing can connect to only one RETEQueue.
     */
    protected Map continuations = new Map();
    
//    protected Set continuationSet = new Set();
    
    /** Instruction code: Check triple entry (arg1) against literal value (arg2). */
    public static final byte TESTValue = 0x01;
    
    /** Instruction code: Check literal value is a functor of name arg1 */
    public static final byte TESTFunctorName = 0x02;
    
    /** Instruction code: Cross match two triple entries (arg1, arg2) */
    public static final byte TESTIntraMatch = 0x03;
    
    /** Instruction code: Create a result environment of length arg1. */
    public static final byte CREATEToken = 0x04;
    
    /** Instruction code: Bind a node (arg1) to a place in the rules token (arg2). */
    public static final byte BIND = 0x05;
    
    /** Instruction code: Final entry - dispatch to the network. */
    public static final byte END = 0x06;
    
    /** Argument addressing code: triple subject */
    public static final byte ADDRSubject = 0x10;
    
    /** Argument addressing code: triple predicate */
    public static final byte ADDRPredicate = 0x20;
    
    /** Argument addressing code: triple object as a whole */
    public static final byte ADDRObject = 0x30;
    
    /** Argument addressing code: triple object functor node, offset in 
     *  low nibble, only usable after a successful TestFunctorName. */
    public static final byte ADDRFunctorNode = 0x40;

// ============================================
// Fields supporting delayed beta network building    
    
    /** Variable List of this condition*/
    protected Map varListMap = new Map();
    
//    protected List roommates = new List();
    
    protected Map bindingIndicesMap = new Map();
    
    protected RETEQueueSharing queueContinuation = null;
    
    protected List terminalContinuations;
    
    //==========================================
    // Fields below are for restored condition
    public Integer currentConditionId;
    
    public List varList;
    
    public byte[] bindingIndices;
    
    public RETESinkNode continuation;
    
    // =========================================
    // Test fields
    
//    public boolean connectToTerminal = false;
       
    private int roommieNum = 0;
    
    private boolean wildcard = false;
    
    public boolean doneCrossJoin = false;
    
    
    /**
     * Contructor.
     * @param instructions the set of byte-coded instructions and argument pointers.
     * @param args the object arguments referenced from the instructions array.
     */
    public RETEClauseFilterSharing(byte[] instructions, Object[] args) {
        super();
        this.instructions = instructions;
        this.args = args;
    }

    /**
     * A constructor which construct a RETEClauseFilterSharing node from an normal
     * RETEClauseFilter node.
     * @param ruleId
     * @param condition
     */
    public RETEClauseFilterSharing(RETEClauseFilter condition) {
        super();
        
        this.condition = condition.condition;
        if(this.condition.getObject().isVariable() && this.condition.getPredicate().isVariable() && this.condition.getSubject().isVariable())
            wildcard = true;
        
        // Clone instructions
        this.instructions = new byte[condition.instructions.length];
        for(int i = 0; i<condition.instructions.length; i++){
            this.instructions[i] = condition.instructions[i];
        }
        
        // Clone args (how to clone)
        this.args = new Object[condition.args.length];
        for(int i = 0; i<condition.args.length; i++){
            this.args[i] = condition.args[i];
        }
        
        varListMap.put(condition.ID, condition.varList);
//        roommates.add(condition.ID);
        bindingIndicesMap.put(condition.ID, condition.bindingIndices);
        RETEQueueSharing queueNode = new RETEQueueSharing();
        continuations.put(condition.ID, queueNode);        
        
//        continuationSet.add(queueNode);
        
        queueContinuation = queueNode;
        
        roommieNum ++;
        
        restore(condition.ID);
    }
    
    public RETEClauseFilterSharing(RETEClauseFilter condition1, RETEClauseFilter condition2) {
        super();
        
        this.condition = condition1.condition;
        if(this.condition.getObject().isVariable() && this.condition.getPredicate().isVariable() && this.condition.getSubject().isVariable())
            wildcard = true;
        
        // Clone instructions
        this.instructions = new byte[condition1.instructions.length];
        for(int i = 0; i<condition1.instructions.length; i++){
            this.instructions[i] = condition1.instructions[i];
        }
        
        // Clone args (how to clone)
        this.args = new Object[condition1.args.length];
        for(int i = 0; i<condition1.args.length; i++){
            this.args[i] = condition1.args[i];
        }
        
        varListMap.put(condition1.ID, condition1.varList);
        varListMap.put(condition2.ID, condition2.varList);
        
//        roommates.add(condition1.ID);
//        roommates.add(condition2.ID);
        
        bindingIndicesMap.put(condition1.ID, condition1.bindingIndices);
        bindingIndicesMap.put(condition2.ID, condition2.bindingIndices);
        
        // to make sure all RETEClauseFilterSharing nodes have a RETEQueue continuation
        RETEQueueSharing queueNode = new RETEQueueSharing();
        continuations.put(condition1.ID, queueNode);
        continuations.put(condition2.ID, queueNode);
        
//        continuationSet.add(queueNode);
        
        queueContinuation = queueNode;
        
        roommieNum += 2;
        
        restore(condition2.ID);
    }
    
    
    /**
     * Merge an condition to this sharing condition node.
     * @param condition the condition to be merged.
     * @param ruleId the rule to which this condition belong to.
     */
    public void merge(RETEClauseFilter condition){
        varListMap.put(condition.ID, condition.varList);
//        roommates.add(condition.ID);
        bindingIndicesMap.put(condition.ID, condition.bindingIndices);
        continuations.put(condition.ID, queueContinuation);
        roommieNum ++;
    }
    
    
     /**
      * @deprecated RETEClausefilterSharing can only be created by merging an instance
      * of RETEClauseFilterSharing with a RETEClauseFilter.
     * Create a condition node for composable reasoner
     * Clause complexity is limited to less than 50 args in a Functor.
     * @param clause the rule clause
     */
    public static RETEClauseFilterSharing compile(TriplePattern clause, int envLength) { 
        return null;
    }
    
    
    
    
    /**
     * @deprecated here
     * Create a filter node from a rule clause.
     * Clause complexity is limited to less than 50 args in a Functor.
     * @param clause the rule clause
     * @param envLength the size of binding environment that should be created on successful matches
     * @param varList a list to which all clause variables will be appended
     */
    public static RETEClauseFilterSharing compile(TriplePattern clause, int envLength, List varList) { 
        return null;
    }
    
    /**
     * Set the continuation node for this node.
     */
    public void setContinuation(RETESinkNode continuation) {
        this.continuation = continuation;
    }
    
    public void addTerminal(Integer conditionId, RETETerminal terminal){
        continuations.put(conditionId, terminal);
        if(terminalContinuations == null)
            terminalContinuations = new List();
        terminalContinuations.add(terminal);
    }
    
//    /**
//     * 
//     * @param continuation
//     */
//    public void addContinuation(Integer conditionId, RETESinkNode continuation) {
//        this.continuations.put(conditionId, continuation);
////        continuationSet.add(continuation);
//        if(++continuationNum == roommieNum){
//            queueContinuation = null;
////            for(int i=0; i<continuationSet.size(); i++){
////                if(continuationSet.get(i) instanceof RETEQueueSharing)
////                    continuationSet.remove(i);
////            }
//        }
//                
//        if(continuation instanceof RETETerminal){
//            if(terminalContinuations == null)
//                terminalContinuations = new List();
//            terminalContinuations.add(continuation);
//        }
//        
//    }
    
//    public void fire(Triple triple, boolean isAdd) {
//        if(terminalContinuations != null)
//            fireSingletonRules(triple, isAdd);
//        if(queueContinuation != null)
//            fireNonsingletonRules(triple, isAdd);
//    }
//    
//    
//    public void fireSingletonRules(Triple triple, boolean isAdd) {
//        Functor lastFunctor = null;     // bound by TESTFunctorName
//        BindingVector env = null;       // bound by CREATEToken
//        Node n = null;                  // Temp workspace
//        
//        for (int pc = 0; pc < instructions.length; ) {
//            switch(instructions[pc++]) {
//                
//            case TESTValue: 
//                // Check triple entry (arg1) against literal value (arg2)
////                System.err.println("Testing triple "+triple);
//                if (! getTripleValue(triple, instructions[pc++], lastFunctor)
//                                .sameValueAs(args[instructions[pc++]])) {
////                    System.err.println("Failed");
//                    return;
//                }
//                break;
//                
//            case TESTFunctorName:
//                // Check literal value is a functor of name arg1.
//                // Side effect: leaves a loop variable pointing to functor 
//                // for possible later functor argument accesses
//                n = triple.getObject();
//                if ( !n.isLiteral() ) return;
//                if ( n.getLiteralDatatype() != Functor.FunctorDatatype.theFunctorDatatype) return;
//                lastFunctor = (Functor)n.getLiteralValue();
//                if ( !lastFunctor.getName().equals(args[instructions[pc++]]) ) return;
//                break;
//                
//            case CREATEToken:
//                // Create a result environment of length arg1
//                env = new BindingVector(new Node[instructions[pc++]]);
//                break;
//                
//            case BIND:
//                // Bind a node (arg1) to a place in the rules token (arg2)
//                n = getTripleValue(triple, instructions[pc++], lastFunctor);
//                if ( !env.bind(instructions[pc++], n) ) return;
//                break;
//                
//            case END:
//                // Success, fire the continuation
//            	
//            	// Comment by Wei
//            	// Actually there is no need to cache successfully matched triples here as 
//            	// all matched triples are cached in the RETEQueue. The RETEQueue works as a combination
//            	// of alpha memory and beta node. From here, matched triples will be propagated 
//            	// to beta network one by one with each triple as an instance of BindingVector. 
//                for(byte i = 0; i < terminalContinuations.size(); i++)
//                    ((RETESinkNode)terminalContinuations.get(i)).fire(env, isAdd);
//            }
//        } 
//    }
    
    /**
     * Insert or remove a triple into the network.
     * @param triple the triple to process.
     * @param isAdd true if the triple is being added to the working set.
     */
    public void fire(Triple triple, boolean isAdd) {
//        if(StartApplication.state != 0)
//            System.err.println("DEBUG (RETEClauseFilterSharing::fire) : inserting "+ (triple instanceof TempTriple?"TempTriple ":"Triple ")+triple);
        Debugger.NoM_All++;
//        System.err.print(triple);
        Functor lastFunctor = null;     // bound by TESTFunctorName
        IntermediateBindingVector i_env = null;       // bound by CREATEToken
//        BindingVector env = null;
        Node n = null;                  // Temp workspace
        
//        System.err.println("(RETEClauseFilterSharing::fire)Received a new Triple "+triple);
        
        if(wildcard){
            if(triple instanceof TempTriple){
                i_env = new TempIntermediateBindingVector((byte)3, ((TempTriple)triple).updateID.byteValue());
            }
            else{
                i_env = new IntermediateBindingVector((byte)3);
            }

            i_env.environment[0] = (triple.getSubject());
            i_env.environment[1] = (triple.getPredicate());
            i_env.environment[2] = (triple.getObject());
            
            Debugger.NoSM_All++;
            
            if(terminalContinuations != null){                    
                for(byte i = 0; i < terminalContinuations.size(); i++){
                    RETETerminal t = (RETETerminal)terminalContinuations.get(i);
                    BindingVector env = null;
                    if(i_env instanceof TempIntermediateBindingVector)
                        env = new TempBindingVector(t.varNum, ((TempIntermediateBindingVector)i_env).updateID);
                    else
                        env = new BindingVector(t.varNum);
                    for(byte j = 0; j<i_env.environment.length; j++)
                        env.environment[j] = i_env.environment[j];
                    
                    t.fire(env, isAdd);   
                }
            }       
            
            if(queueContinuation != null)
                queueContinuation.fire(i_env, isAdd); 
            
            return;
        }
        
        for (int pc = 0; pc < instructions.length; ) {
            switch(instructions[pc++]) {
                
            case TESTValue: 
                // Check triple entry (arg1) against literal value (arg2)
//                System.err.println("Testing triple "+triple);
                if (! getTripleValue(triple, instructions[pc++], lastFunctor)
                                .sameValueAs(args[instructions[pc++]])) {
//                    System.err.println("Failed");
                    return;
                }
                break;
                
            case TESTFunctorName:
                // Check literal value is a functor of name arg1.
                // Side effect: leaves a loop variable pointing to functor 
                // for possible later functor argument accesses
                n = triple.getObject();
                if ( !n.isLiteral() ) return;
                if ( n.getLiteralDatatype() != Functor.FunctorDatatype.theFunctorDatatype) return;
                lastFunctor = (Functor)n.getLiteralValue();
                if ( !lastFunctor.getName().equals(args[instructions[pc++]]) ) return;
                break;
                
            case CREATEToken:
                // Create a result environment of length arg1                
                if(triple instanceof TempTriple){
                    i_env = new TempIntermediateBindingVector((byte)varList.size(), ((TempTriple)triple).updateID.byteValue());
                }
                else{
                    i_env = new IntermediateBindingVector((byte)varList.size());
                }
//                env = new BindingVector(new Node[(byte)varList.size()]);
                pc++;
                // ignore the length of bindingVector
                break;
                
            case BIND:
                // Bind a node (arg1) to a place in the rules token (arg2)
                n = getTripleValue(triple, instructions[pc++], lastFunctor);
                i_env.bind(n);
//                if ( !env.bind(instructions[pc++], n) ) return;
                pc++;
                break;
                
            case END:
                // Success, fire the continuation
            	
            	// Comment by Wei
            	// Actually there is no need to cache successfully matched triples here as 
            	// all matched triples are cached in the RETEQueue. The RETEQueue works as a combination
            	// of alpha memory and beta node. From here, matched triples will be propagated 
            	// to beta network one by one with each triple as an instance of BindingVector. 
                Debugger.NoSM_All++;
                if(terminalContinuations != null){                    
                    for(byte i = 0; i < terminalContinuations.size(); i++){
                        RETETerminal t = (RETETerminal)terminalContinuations.get(i);
                        BindingVector env;
                        if(triple instanceof TempTriple){
                            env = new TempBindingVector(t.varNum, ((TempIntermediateBindingVector)i_env).updateID);
                        }
                        else{
                            env = new BindingVector(t.varNum);
                        }
                        for(byte j = 0; j<i_env.environment.length; j++)
                            env.environment[j] = i_env.environment[j];
                        t.fire(env, isAdd);   
                    }
                }
                
                if(queueContinuation != null){
                    queueContinuation.fire(i_env, isAdd);
                }
                
//                for(byte i = 0; i < continuationSet.size(); i++)
//                    ((RETESinkNode)continuationSet.get(i)).fire(env, isAdd);
            }
        }

    }
    
//    private void fireContinuation(IntermediateBindingVector i_env, boolean isAdd){
//        if(terminalContinuations != null){                    
//            for(byte i = 0; i < terminalContinuations.size(); i++){
//                RETETerminal t = (RETETerminal)terminalContinuations.get(i);
//                BindingVector env = new BindingVector(t.varNum);
//                for(byte j = 0; j<i_env.environment.length; j++)
//                    env.environment[j] = i_env.environment[j];
//                t.fire(env, isAdd);   
//            }
//        }
//        if(queueContinuation != null)
//            queueContinuation.fire(i_env, isAdd);        
//    }
    
    /**
     * Helperful function. Return the node from the argument triple
     * corresponding to the byte code address.
     */
    private Node getTripleValue(Triple triple, byte address, Functor lastFunctor) {
        switch (address & 0xf0) {
        case ADDRSubject:
            return triple.getSubject();
        case ADDRPredicate:
            return triple.getPredicate();
        case ADDRObject:
            return triple.getObject();
        case ADDRFunctorNode:
            return lastFunctor.getArgs()[address & 0x0f];
        }
        return null;
    }
    
    /**
     * Clone this node in the network. Might need clone other fields.
     * @param netCopy a map from RETENode to cloned instance
     * @param context the new context to which the network is being ported
     */
    public RETENode clone(Map netCopy, RETERuleContext context) {
        RETEClauseFilterSharing clone = (RETEClauseFilterSharing)netCopy.get(this);
        if (clone == null) {
            clone = new RETEClauseFilterSharing(instructions, args);

            for(Iterator it = continuations.keySet().iterator(); it.hasNext(); ){
                Object key = it.next();
                Object entry = continuations.get(key);
                RETESinkNode cloneContinuation = (RETESinkNode)((RETESinkNode)entry).clone(netCopy, context);
                clone.continuations.put((Integer)key, cloneContinuation);
                if(cloneContinuation instanceof RETETerminal)
                    clone.terminalContinuations.add(cloneContinuation);
                else
                    clone.queueContinuation = (RETEQueueSharing)cloneContinuation;
            }
            netCopy.put(this, clone);
        }
        return clone;
    }
    
    // J2ME Version: code added below are for test use    
    
    /** Hold the triple pattern for this alpha node*/
    public TriplePattern condition = null;
    
    /**
     * 
     */
    public RETEClauseFilterSharing(byte[] instructions, Object[] args, TriplePattern pattern){
        this(instructions, args);
        condition = pattern;
    }
    
    public String toString(){
        return condition.toString();
    }
    
    
    public boolean equals(Object o){
        return o instanceof RETEClauseFilter && condition.equals(((RETEClauseFilter)o).condition);
    }

    /**
     * return the continuation of currently restored RETEClauseFilterSharing node
     * @return the continuation or null if no RETEClauseFilterSharing node is restored yet
     */
    public RETESinkNode getContinuation() {
        return continuation;
    }
    
//    /**
//     * whether this sharing condition node is shared by the given condition node.
//     * @param conditionId
//     * @return
//     */
//    public boolean hasRoommate(Integer conditionId){
//        return roommates.contains(conditionId);
//    }

    public int getSize() {
//        for(byte i = 0; i < continuations.keySet().size(); i++){
//            Object continuationObj = continuations.get(continuations.keySet().get(i));
//            if(continuationObj instanceof RETEQueue)
//                return ((RETEQueue)continuationObj).queue.size();
//        }
//        return 0;
        if(queueContinuation != null)
            return queueContinuation.getSize();
        return 0;
    }    
    
    /**
     * Retrieve the only queue continuations that attached to this node
     * @return the RETE queue or null if no RETE queue is attached
     */
    public RETEQueueSharing getQueueContinuation(){
        return queueContinuation;
    }
    
    /**
     * Restore this shared node to specific node
     * @param conditionId the node to be restored to
     * @return the shared node after restoring
     */    
    public RETEClauseFilterSharing restore(Integer conditionId){
        this.currentConditionId = conditionId;
        varList = (List)varListMap.get(conditionId);
        bindingIndices = (byte[])bindingIndicesMap.get(conditionId);
        continuation = (RETESinkNode)continuations.get(conditionId);
        return this;
    }
    
//    /**
//     * To save changes done to fields of restored node
//     */
//    public void save(){
//        varListMap.put(currentConditionId, varList);
//        bindingIndicesMap.put(currentConditionId, bindingIndices);
//        continuations.put(currentConditionId, continuation);
////        continuationSet.add(continuation);
//    }
    
    public void clear(){
        currentConditionId = null;
        varList = null;
        bindingIndices = null;
        continuation = null;
    }
    
    public void testRemoveQueueContinuation(){
        if(terminalContinuations != null && terminalContinuations.size() == roommieNum)
            queueContinuation = null;
    }
    
}
