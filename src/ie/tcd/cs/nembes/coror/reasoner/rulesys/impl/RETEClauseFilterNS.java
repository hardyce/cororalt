/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;


import ie.tcd.cs.nembes.coror.util.Collection;
import ie.tcd.cs.nembes.coror.util.Map;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.OneToManyMap;
import ie.tcd.cs.nembes.coror.Coror;
import ie.tcd.cs.nembes.coror.debug.Debugger;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.graph.temporal.TemporalTriple;
import ie.tcd.cs.nembes.coror.reasoner.TriplePattern;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Functor;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Node_RuleVariable;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.CororException;
//import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh.TempTriple;
import java.lang.UnsupportedOperationException;
import java.util.Vector;


/**
 *
 * @author WEI TAI
 * @deprecated 
 */
public class RETEClauseFilterNS extends RETESourceNode implements SharedNodeI, FireTripleI {
    
    /** Contains the set of byte-coded instructions and argument pointers */
    protected byte[] instructions;
    
    /** Contains the object arguments referenced from the instructions array */
    protected Object[] args;
    
    /** the continuations of this alpha node. Multiple continuations when this node is shared by multiple rules */
    protected List continuations;
    
//    /** An indicator for the RETE engine inject() to know that this node is already invoked previously */
//    protected boolean fired = false;
        
    /** Instruction code: Check triple entry (arg1) against literal value (arg2). */
    public static final byte TESTValue = 0x01;
    
    /** Instruction code: Check literal value is a functor of id arg1 */
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

    
    private boolean wildcard;
    
    ////////////////////////////////////////////////////////
    /// Fields for test uses
    
    /** the id of the filter node */
    public String id = "";
    
    
    /**
     * Contructor.
     * @param instructions the set of byte-coded instructions and argument pointers.
     * @param args the object arguments referenced from the instructions array.
     */
    public RETEClauseFilterNS(byte[] instructions, Object[] args, String name) {
        super();
        this.instructions = instructions;
        this.args = args;
        this.continuations = new List();
        this.id = name;
    }
    
    /**
     * compile a filter node from a rule clause. Or return a shared filter node 
     * if there is an existing one can be shared. 
     * @param clause the rule clause
     * @param clauseIndex an index of all constructed filter nodes
     * @param varList a list to which all clause variables will be appended
     * @param shared an indicator for if the returned alpha node is shared with others
     */
    public static RETEClauseFilterNS compileOrShare(TriplePattern clause, OneToManyMap clauseIndex, List varList, Boolean shared, String name){
        
        Collection filterNodeSet = clauseIndex.values();
        RETEClauseFilterNS currentNode;
        Iterator filterNodeIt = filterNodeSet.iterator();
        
        while(filterNodeIt.hasNext()){
            currentNode = (RETEClauseFilterNS)filterNodeIt.next();
//            System.err.println("Testing "+name+" with "+currentNode.id);
            if(currentNode.canShare(clause)){
                shared = true;
//                System.err.println(currentNode.id+" is shared with "+name);
                // construct the variable list if the alpha node is shared
                if(clause.getSubject().isVariable())
                    varList.add(clause.getSubject());
                if(clause.getPredicate().isVariable())
                    varList.add(clause.getPredicate());
                if(clause.getObject().isVariable())
                    varList.add(clause.getObject());
                
                return currentNode;
            }
        }
//        System.err.println(name+" is not shared with anyone");
        
        return compile(clause, -1, varList, name);
    }
    
    /**
     * Create a filter node from a rule clause.
     * Clause complexity is limited to less than 50 args in a Functor.
     * @param clause the rule clause
     * @param envLength the size of binding environment that should be created on successful matches
     * @param varList a list to which all clause variables will be appended
     */
    public static RETEClauseFilterNS compile(TriplePattern clause, int envLength, List varList, String name) {
        
        byte[] instructions = new byte[300];
        byte[] bindInstructions = new byte[100];
        List args = new List();
        int pc = 0;   
        int bpc = 0;
//        List bindingIndicesList = new List();

        // Pass 0 - prepare env creation statement
        bindInstructions[bpc++] = CREATEToken;
        bindInstructions[bpc++] = (byte)envLength;
        
        // Pass 1 - check literal values
        Node n = clause.getSubject();
        if ( !n.isVariable() ) {
            instructions[pc++] = TESTValue;
            instructions[pc++] = ADDRSubject;
            instructions[pc++] = (byte)args.size();
            args.add( n );
        } else {
            bindInstructions[bpc++] = BIND;
            bindInstructions[bpc++] = ADDRSubject;
            bindInstructions[bpc++] = (byte)((Node_RuleVariable)n).getIndex();            
            varList.add(n);
        }
        n = clause.getPredicate();
        if ( !n.isVariable() ) {
            instructions[pc++] = TESTValue;
            instructions[pc++] = ADDRPredicate;
            instructions[pc++] = (byte)args.size();
            args.add( n );
        } else {
            bindInstructions[bpc++] = BIND;
            bindInstructions[bpc++] = ADDRPredicate;
            bindInstructions[bpc++] = (byte)((Node_RuleVariable)n).getIndex();
            varList.add(n);
        }
        n = clause.getObject();
        if ( !n.isVariable() ) {
            if (Functor.isFunctor(n)) {
                // Pass 2 - check functor
                Functor f = (Functor)n.getLiteralValue();
                instructions[pc++] = TESTFunctorName;
                instructions[pc++] = (byte)args.size();
                args.add(f.getName());
                Node[] fargs = f.getArgs();
                for (int i = 0; i < fargs.length; i++) {
                    Node fn = fargs[i];
                    byte addr = (byte) (ADDRFunctorNode | (0x0f & i));
                    if ( !fn.isVariable() ) {
                        instructions[pc++] = TESTValue;
                        instructions[pc++] = addr;
                        instructions[pc++] = (byte)args.size();
                        args.add( fn );
                    } else {
                        bindInstructions[bpc++] = BIND;
                        bindInstructions[bpc++] = addr;
                        bindInstructions[bpc++] = (byte)((Node_RuleVariable)fn).getIndex();
                       
                        varList.add(fn);
                    }
                }
            } else {
                instructions[pc++] = TESTValue;
                instructions[pc++] = ADDRObject;
                instructions[pc++] = (byte)args.size();
                args.add( n );
            }
        } else {
            bindInstructions[bpc++] = BIND;
            bindInstructions[bpc++] = ADDRObject;
            bindInstructions[bpc++] = (byte)((Node_RuleVariable)n).getIndex();           
            
            varList.add(n);
        }
        bindInstructions[bpc++] = END;
        
        // Repair the variable size to construct size-varied partial binding vectors
        bindInstructions[1] = (byte)varList.size();
        
        
        // Pass 4 - Pack instructions
        byte[] packed = new byte[pc + bpc];
        System.arraycopy(instructions, 0, packed, 0, pc);
        System.arraycopy(bindInstructions, 0, packed, pc, bpc);
        Object[] packedArgs = args.toArray();
           
        
//        return new RETEClauseFilter(packed, packedArgs); 
        RETEClauseFilterNS condition =  new RETEClauseFilterNS(packed, packedArgs, name);
        if(bindInstructions[1] == 3) condition.wildcard = true;
        return condition;
    }
    
    /**
     * Set the continuation node for this node.
     * This method is replaced by addContinuation here.
     */
    public void setContinuation(RETESinkNode continuation) {
         throw new UnsupportedOperationException("setContinuation is not supported");
    }
    
    public void addContinuation(RETESinkNode continuation) {
        if(continuations.contains(continuation)) return;
            continuations.add(continuation);
        if(continuation instanceof RETEQueueNS)
            ((RETEQueueNS)continuation).parent = this;
    }

    /**
     * Insert or remove a triple into the network.
     * @param triple the triple to process.
     * @param isAdd true if the triple is being added to the working set.
     */
    public void fire(Triple triple, boolean isAdd) {
        //if(triple instanceof TemporalTriple){
        //System.out.println("fire "+triple.toString());
        //}
        Functor lastFunctor = null;     // bound by TESTFunctorName
        PBV env = null;       // bound by CREATEToken
        Node n = null; // Temp workspace
        List trace;
        
        Debugger.NoM_All ++;
        
//        if(wildcard){
//            env = new PBV(new Node[3]);
//            env.pEnvironment[0] = (triple.getSubject());
//            env.pEnvironment[1] = (triple.getPredicate());
//            env.pEnvironment[2] = (triple.getObject());
//            
//            Coror.NoSM_All++;
//                   
//            boolean queueCounterAdded = false;
//            for(int i=0; i<continuations.size(); i++){
//                if(continuations.get(i) instanceof RETEQueueNS){
//                    if(queueCounterAdded == false){
//                        ((RETESinkNode)continuations.get(i)).fire(env, isAdd);
//                        queueCounterAdded = true;
//                    }
//                    else
//                        ((RETEQueueNS)continuations.get(i)).fire(env, isAdd, true);
//                }
//                else
//                    ((RETETerminal)continuations.get(i)).fire(env, isAdd);
////                    if(Coror.printTrace) ((PartialTraceBindingVector_Test)env).setTrace(recoveryTrace);
//            }                
//            return;
//        }
        byte nextBindPos = 0;
        for (int pc = 0; pc < instructions.length; ) {
            switch(instructions[pc++]) {
                
            case TESTValue: 
                // Check triple entry (arg1) against literal value (arg2)
                if (! getTripleValue(triple, instructions[pc++], lastFunctor)
                                .sameValueAs(args[instructions[pc++]])) {
//                    System.err.println();
                    return;
                }
                break;
                
            case TESTFunctorName:
                // Check literal value is a functor of id arg1.
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

//                if(!Coror.printTrace)
//                    env = new PBV(new Node[instructions[pc++]]);
//                else{
//                    trace = new List();
//                    trace.add(new Trace(id, null));
//                    env = new PartialTraceBindingVector_Test(new Node[instructions[pc++]], trace);
//                }
                
                if(triple instanceof TemporalTriple){
                   
                    env = new TemporalPBV(new Node[instructions[pc++]], ((TemporalTriple)triple).getTime());}
                else
                    env = new PBV(new Node[instructions[pc++]]);
                
                
                break;
                
            case BIND:
                // Bind a node (arg1) to a place in the rules token (arg2)
                n = getTripleValue(triple, instructions[pc++], lastFunctor);
                pc++;
                if ( !env.bind(n, nextBindPos++) ) throw new CororException("fail in binding a node "+n);
                break;
                
            case END:
                /**
                 * Success, fire the continuations. Care needs to be taken on the count
                 * of binding vector when there are multiple continuations. All the multiple
                 * beta node continuations are all pointed to the same queue, hence when a 
                 * binding vector comes it will be added multiple times to the shared queue.
                 * THis will cause problems in count. To solve this, we only add count for
                 * the first continuation and not for the rest continuations. A continuation
                 * of RETETerminal does not have such a problem.
                 * cause the count 
                 */
                Debugger.NoSM_All++;
                boolean queueCounterAdded = false;
                //System.out.print("size of cont "+continuations.size());
                for(int i=0; i<continuations.size(); i++){
                    if(continuations.get(i) instanceof RETEQueueNS){
                        if(queueCounterAdded == false){
                            ((RETESinkNode)continuations.get(i)).fire(env, isAdd);
                            queueCounterAdded = true;
                        }
                        else{
                            //System.out.println("alpha fire "+env.getEnvironment()[0]);
                            ((RETEQueueNS)continuations.get(i)).fire(env, isAdd, true);
                        }
                    }
                    else
                        ((RETETerminal)continuations.get(i)).fire(env, isAdd);
//                    if(Coror.printTrace) ((PartialTraceBindingVector_Test)env).setTrace(recoveryTrace);
                }
            }
        }

    }
    
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
     * Check if this clause filter can be shared by the given triple pattern.
     */
    private boolean canShare(TriplePattern clause){
        
        for(byte i = 0; i<instructions.length; ){
            if(instructions[i] == BIND){
                i++;
                switch(instructions[i++] & 0xf0){
                    case ADDRSubject:
                        if(!clause.getSubject().isVariable())
                            return false;
                        break;
                    case ADDRPredicate:
                        if(!clause.getPredicate().isVariable())
                            return false;
                        break;
                    case ADDRObject:
                        if(!clause.getObject().isVariable())
                            return false;
                        break;
                    case ADDRFunctorNode:
                        return false;
                }
                i++;
            }
            else if(instructions[i] == TESTValue){
                i++;
                switch(instructions[i++] & 0xf0){
                    case ADDRSubject:
                        if(!clause.getSubject().sameValueAs(args[instructions[i++]]))
                            return false;
                        break;
                    case ADDRPredicate:
                        if(!clause.getPredicate().sameValueAs(args[instructions[i++]]))
                            return false;
                        break;
                    case ADDRObject:
                        if(!clause.getObject().sameValueAs(args[instructions[i++]]))
                            return false;
                        break;
                    case ADDRFunctorNode:
                        throw new CororException();
                }                
            }
            else{
                if(instructions[i] == CREATEToken) i+=2;
                else if(instructions[i] == END) {i+=1; return true;}
                else if(instructions[i] == TESTFunctorName) throw new CororException();
                else throw new CororException();
            }
        }
        
        return true;
    }
    
    /**
     * Clone this node in the network.
     * @param netCopy a map from RETENode to cloned instance
     * @param context the new context to which the network is being ported
     */
    @Override
    public RETENode clone(Map netCopy, RETERuleContext context) {
        throw new UnsupportedOperationException("Clone is not implemented");
    }
    
    @Override
    public RETESinkNode getContinuation() {
        throw new UnsupportedOperationException("getContinuation is not implemented");
    }

    @Override
    public List getContinuations() {
        return continuations;
    }

    @Override
    public String getNodeID() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
    

}