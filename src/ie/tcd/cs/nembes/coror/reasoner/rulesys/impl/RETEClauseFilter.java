package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.Coror;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.reasoner.TriplePattern;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Functor;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Node_RuleVariable;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.Map;
import ie.tcd.cs.nembes.coror.util.Set;

/******************************************************************
 * File:        RETEClauseFilter.java
 * Created by:  Dave Reynolds
 * Created on:  09-Jun-2003
 * 
 * (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: RETEClauseFilter.java,v 1.14 2008/01/02 12:06:16 andy_seaborne Exp $
 *****************************************************************/


/**
 * Checks a triple against the grounded matches and intra-triple matches
 * for a single rule clause. If the match passes it creates a binding
 * environment token and passes it on the the RETE network itself. The checks
 * and bindings are implemented using a simple byte-coded interpreter.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.14 $ on $Date: 2008/01/02 12:06:16 $
 */
public class RETEClauseFilter extends RETESourceNode implements FireTripleI{
    
    /** Contains the set of byte-coded instructions and argument pointers */
    protected byte[] instructions;
    
    /** Contains the object arguments referenced from the instructions array */
    protected Object[] args;
    
    /** The network node to receive any created tokens */
    protected RETESinkNode continuation;
    
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

    
    /** Variable List of this condition*/
    public List varList;
    
    /** A list containing the index of variables to be bound for this condition */
    public byte[] bindingIndices;
    
    /**
     * Contructor.
     * @param instructions the set of byte-coded instructions and argument pointers.
     * @param args the object arguments referenced from the instructions array.
     */
    public RETEClauseFilter(byte[] instructions, Object[] args) {
        super();
        this.instructions = instructions;
        this.args = args;
    }

    
//     /**
//     * Create a condition node for composable reasoner
//     * Clause complexity is limited to less than 50 args in a Functor.
//     * @param clause the rule clause
//     */
//    public static RETEClauseFilter compile(TriplePattern clause, int envLength) { 
//        byte[] instructions = new byte[300];
//        byte[] bindInstructions = new byte[100];
//        List args = new List();
//        int pc = 0;   
//        int bpc = 0;
//        
//        List tempVarList = new List(envLength);
//        List bindingIndicesList = new List();
//               
//        // Pass 0 - prepare env creation statement
//        bindInstructions[bpc++] = CREATEToken;
//        bindInstructions[bpc++] = (byte)envLength;
//        
//        // Pass 1 - check literal values
//        Node n = clause.getSubject();
//        if ( !n.isVariable() ) {
//            instructions[pc++] = TESTValue;
//            instructions[pc++] = ADDRSubject;
//            instructions[pc++] = (byte)args.size();
//            args.add( n );
//        } else {
//            bindInstructions[bpc++] = BIND;
//            bindInstructions[bpc++] = ADDRSubject;
//            bindInstructions[bpc++] = (byte)((Node_RuleVariable)n).getIndex();
//            
//            if(ReasonerConfig.shareAlphaNodes == true){
//                bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                bpc++;
//            }
//            
//            tempVarList.add(n);
//        }
//        n = clause.getPredicate();
////        System.err.println("the predicate for this alpha node is "+n);
//        if ( !n.isVariable() ) {
//            instructions[pc++] = TESTValue;
//            instructions[pc++] = ADDRPredicate;
//            instructions[pc++] = (byte)args.size();
//            args.add( clause.getPredicate() );
//        } else {
//            bindInstructions[bpc++] = BIND;
//            bindInstructions[bpc++] = ADDRPredicate;
//            bindInstructions[bpc++] = (byte)((Node_RuleVariable)n).getIndex();
//            
//            if(ReasonerConfig.shareAlphaNodes == true){
//                bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                bpc++;
//            }
//            
//            tempVarList.add(n);
//        }
//        n = clause.getObject();
//        if ( !n.isVariable() ) {
//            if (Functor.isFunctor(n)) {
//                // Pass 2 - check functor
//                Functor f = (Functor)n.getLiteralValue();
//                instructions[pc++] = TESTFunctorName;
//                instructions[pc++] = (byte)args.size();
//                args.add(f.getName());
//                Node[] fargs = f.getArgs();
//                for (int i = 0; i < fargs.length; i++) {
//                    Node fn = fargs[i];
//                    byte addr = (byte) (ADDRFunctorNode | (0x0f & i));
//                    if ( !fn.isVariable() ) {
//                        instructions[pc++] = TESTValue;
//                        instructions[pc++] = addr;
//                        instructions[pc++] = (byte)args.size();
//                        args.add( fn );
//                    } else {
//                        bindInstructions[bpc++] = BIND;
//                        bindInstructions[bpc++] = addr;
//                        bindInstructions[bpc++] = (byte)((Node_RuleVariable)fn).getIndex();
//                        
//                        if(ReasonerConfig.shareAlphaNodes == true){
//                            bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                            bpc++;
//                        }
//                        
//                        tempVarList.add(fn);
//                    }
//                }
//            } else {
//                instructions[pc++] = TESTValue;
//                instructions[pc++] = ADDRObject;
//                instructions[pc++] = (byte)args.size();
//                args.add( n );
//            }
//        } else {
//            bindInstructions[bpc++] = BIND;
//            bindInstructions[bpc++] = ADDRObject;
//            bindInstructions[bpc++] = (byte)((Node_RuleVariable)n).getIndex();
//            
//            if(ReasonerConfig.shareAlphaNodes == true){
//                bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                bpc++;
//            }
//            
//            tempVarList.add(n);
//        }
//        bindInstructions[bpc++] = END;
//        
//        // Pass 4 - Pack instructions
//        byte[] packed = new byte[pc + bpc];
//        System.arraycopy(instructions, 0, packed, 0, pc);
//        System.arraycopy(bindInstructions, 0, packed, pc, bpc);
//        Object[] packedArgs = args.toArray();
//        
////        return new RETEClauseFilter(packed, packedArgs);
//
//        RETEClauseFilter f = new RETEClauseFilter(packed, packedArgs, clause, bindingIndicesList);
//
//        f.varList = tempVarList;
//        return f;
//    }
    
    /**
     * Create a filter node from a rule clause.
     * Clause complexity is limited to less than 50 args in a Functor.
     * @param clause the rule clause
     * @param envLength the size of binding environment that should be created on successful matches
     * @param varList a list to which all clause variables will be appended
     */
    public static RETEClauseFilter compile(TriplePattern clause, int envLength, List varList) { 
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
            
//            if(ReasonerConfig.shareAlphaNodes == true){
//                bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                bpc++;
//            }
            
            varList.add(n);
        }
        n = clause.getPredicate();
//        System.err.println("the predicate for this alpha node is "+n);
        if ( !n.isVariable() ) {
            instructions[pc++] = TESTValue;
            instructions[pc++] = ADDRPredicate;
            instructions[pc++] = (byte)args.size();
            args.add( clause.getPredicate() );
        } else {
            bindInstructions[bpc++] = BIND;
            bindInstructions[bpc++] = ADDRPredicate;
            bindInstructions[bpc++] = (byte)((Node_RuleVariable)n).getIndex();
            
//            if(ReasonerConfig.shareAlphaNodes == true){
//                bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                bpc++;
//            }
            
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
                        
//                        if(ReasonerConfig.shareAlphaNodes == true){
//                            bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                            bpc++;
//                        }
                        
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
            
//            if(ReasonerConfig.shareAlphaNodes == true){
//                bindingIndicesList.add(new Byte(bindInstructions[--bpc]));
//                bpc++;
//            }            
            
            varList.add(n);
        }
        bindInstructions[bpc++] = END;
        
        // Pass 4 - Pack instructions
        byte[] packed = new byte[pc + bpc];
        System.arraycopy(instructions, 0, packed, 0, pc);
        System.arraycopy(bindInstructions, 0, packed, pc, bpc);
        Object[] packedArgs = args.toArray();
        
        return new RETEClauseFilter(packed, packedArgs);
//        return new RETEClauseFilter(packed, packedArgs, clause, bindingIndicesList);
    }
    
    /**
     * Set the continuation node for this node.
     */
    public void setContinuation(RETESinkNode continuation) {
        this.continuation = continuation;
    }

    /**
     * Insert or remove a triple into the network.
     * @param triple the triple to process.
     * @param isAdd true if the triple is being added to the working set.
     */
    public void fire(Triple triple, boolean isAdd) {

        Functor lastFunctor = null;     // bound by TESTFunctorName
        BindingVector env = null;       // bound by CREATEToken
        Node n = null;                  // Temp workspace
//        Coror.NoM_All++;
//        System.err.print(triple);
        for (int pc = 0; pc < instructions.length; ) {
            switch(instructions[pc++]) {
                
            case TESTValue: 
                // Check triple entry (arg1) against literal value (arg2)
//                System.err.println("Testing triple "+triple);
                if (! getTripleValue(triple, instructions[pc++], lastFunctor)
                                .sameValueAs(args[instructions[pc++]])) {
//                    System.err.println();
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
                env = new BindingVector(new Node[instructions[pc++]]);
                break;
                
            case BIND:
                // Bind a node (arg1) to a place in the rules token (arg2)
                n = getTripleValue(triple, instructions[pc++], lastFunctor);
                if ( !env.bind(instructions[pc++], n) ) return;
                break;
                
            case END:
                // Success, fire the continuation
            	
            	// Comment by Wei
            	// Actually there is no need to cache successfully matched triples here as 
            	// all matched triples are cached in the RETEQueue. The RETEQueue works as a combination
            	// of alpha memory and beta node. From here, matched triples will be propagated 
            	// to beta network one by one with each triple as an instance of BindingVector. 
//                Coror.NoSM_All++;
//                System.err.println(" : successful");
                continuation.fire(env, isAdd);
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
     * Clone this node in the network.
     * @param netCopy a map from RETENode to cloned instance
     * @param context the new context to which the network is being ported
     */
    public RETENode clone(Map netCopy, RETERuleContext context) {
        RETEClauseFilter clone = (RETEClauseFilter)netCopy.get(this);
        if (clone == null) {
            clone = new RETEClauseFilter(instructions, args);
            clone.setContinuation((RETESinkNode)continuation.clone(netCopy, context));
            netCopy.put(this, clone);
        }
        return clone;
    }
    
    // J2ME Version: code added below are for test use    
    
    /** Hold the triple pattern for this alpha node*/
//    public TriplePattern condition = null;
    
//    public RETEClauseFilter(byte[] instructions, Object[] args, TriplePattern pattern){
//        this(instructions, args);
//        condition = pattern;
//    }
    
//    protected RETEClauseFilter(byte[] instructions, Object[] args, TriplePattern pattern, List bindingIndices){
//        this(instructions, args, pattern);
//        if( bindingIndices.size() != 0){
//            this.bindingIndices = new byte[bindingIndices.size()];
//            for(byte i = 0; i < bindingIndices.size(); i++){
//                this.bindingIndices[i] = ((Byte)bindingIndices.get(i)).byteValue();
//            }
//
//        }
//    }
//    
//    public RETEClauseFilter(byte[] instructions, Object[] args, TriplePattern pattern, byte[] bindingIndices){
//        this(instructions, args, pattern);
//        this.bindingIndices = bindingIndices;
//    }
    
//    public String toString(){
//        return condition.toString();
//    }
//    
//    public boolean equals(Object o){
//        return o instanceof RETEClauseFilter && condition.equals(((RETEClauseFilter)o).condition);
//    }

    public RETESinkNode getContinuation() {
        return continuation;
    }

    public int getSize() {
        if(continuation instanceof RETEQueue)
            return ((RETEQueue)continuation).queue.size();
        else if(continuation instanceof RETETerminal)
            return 0;
        else{
            return -1;
        }
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
