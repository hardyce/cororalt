package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.Coror;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.ForwardRuleInfGraphI;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Rule;
//import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.test.TestUtilities;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.Map;

/******************************************************************
 * File:        RETETerminal.java
 * Created by:  Dave Reynolds
 * Created on:  09-Jun-2003
 * 
 * (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: RETETerminal.java,v 1.18 2008/01/02 12:06:16 andy_seaborne Exp $
 *****************************************************************/

/**
 * The final node in a RETE graph. It runs the builtin guard clauses
 * and then, if the token passes, executes the head operations.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.18 $ on $Date: 2008/01/02 12:06:16 $
 */
public class RETETerminal implements RETESinkNode {

    
    
    /** Context containing the specific rule and parent graph */
    protected RETERuleContext context;
//	Comment by Wei
//	All loggers are removed
//    protected static Log logger = LogFactory.getLog(FRuleEngine.class);
    protected byte[] bindingIndices;
    
    /** 
     * The number of variables of this rule. This field is only used by the old
     * two phase RETE algorithm. This field is not necessary. It can be replaced
     * by method call context.getRule().getNumVars().
     */
    protected byte varNum;
    
    
    /**
     * Constructor.
     * @param rule the rule which this terminal should fire.
     * @param engine the parent rule engine through which the deductions and recursive network can be reached.
     * @param graph the wider encompasing infGraph needed to for the RuleContext
     */
    public RETETerminal(Rule rule, RETEEngine engine, ForwardRuleInfGraphI graph) {
        context = new RETERuleContext(graph, engine);
        context.rule = rule;
    }
    
    /**
     * Constructor. Used internally for cloning.
     * @param rule the rule which this terminal should fire.
     * @param engine the parent rule engine through which the deductions and recursive network can be reached.
     * @param graph the wider encompasing infGraph needed to for the RuleContext
     */
    protected RETETerminal(RETERuleContext context) {
        this.context = context;
    }
    
    /**
     * Constructor for sharable RETE network
     * @param rule
     * @param engine
     * @param graph
     * @param bindingIndices
     */
    public RETETerminal (Rule rule, RETEEngine engine, ForwardRuleInfGraphI graph, byte[] bindingIndices, byte varNum){
        this(rule, engine, graph);
        this.varNum = varNum;
        this.bindingIndices = bindingIndices;
        
    }
    
    
    /**
     * Change the engine/graph to which this terminal should deliver its results.
     */
    public void setContext(RETEEngine engine, ForwardRuleInfGraphI graph) {
        Rule rule = context.getRule();
        context = new RETERuleContext(graph, engine);
        context.setRule(rule);
    }
    
    /** 
     * Propagate a token to this node.
     * @param env a set of variable bindings for the rule being processed. 
     * @param isAdd distinguishes between add and remove operations.
     */
    public void fire(BindingVector env, boolean isAdd) {
        Rule rule = context.getRule();
        context.setEnv(env);
        
        if (! context.shouldFire(isAdd)) return;

        // Now fire the rule
        context.getEngine().requestRuleFiring(rule, env, isAdd);
    }
   
    
    /**
     * Clone this node in the network.
     * @param netCopy a map from RETENode to cloned instance
     * @param context the new context to which the network is being ported
     */
    public RETENode clone(Map netCopy, RETERuleContext contextIn) {
        RETETerminal clone = (RETETerminal)netCopy.get(this);
        if (clone == null) {
            RETERuleContext newContext = new RETERuleContext((ForwardRuleInfGraphI)contextIn.getGraph(), contextIn.getEngine());
            newContext.setRule(context.getRule());
            clone = new RETETerminal(newContext);
            netCopy.put(this, clone);
        }
        return clone;
    }
    
    public String toString(){
        return context.getRule().getName();
    }

//    /**
//     * Fire for intermediate binding vectors
//     * @param env
//     * @param isAdd
//     */
//    public void fire(IntermediateBindingVector env, boolean isAdd) {
//
//        if(this.bindingIndices != null){
//            fire(env.toBindingVector(varNum, bindingIndices), isAdd);
//        }
//        else
//            System.err.println("ERROR (RETETerminal::fire): bindingIndices needed to be able to fire");
//    }
    
    
    
    public void setBindingIndices(byte[] bindingIndices){
        this.bindingIndices = bindingIndices;
    }
    
    public void setVarNum(byte ruleId){
        this.varNum = ruleId;
    }

    /**
     * Fire PBV. The last partial binding vector should be 
     * a full initialization of the rule with bindings arranged in the right sequence.
     * 
     * @param env
     * @param isAdd 
     */
    @Override
    public void fire(PBV pEnv, boolean isAdd) {        
        // convert partial binding vector to binding vector
        BindingVector env;
        if(pEnv instanceof TemporalPBV)
            env = new BindingVectorTemporal(context.getRule().getNumVars(), ((TemporalPBV)pEnv).getTimeStamp());
        else
            env = new BindingVector(context.getRule().getNumVars());
//        if(!Coror.printTrace) env = new BindingVector(numVars);
//        else {
//            
//            List newTrace = new List(((PartialTraceBindingVector_Test)pEnv).trace);
//            newTrace.add(new Trace(context.rule.getName()+"_Terminal", new Pair(pEnv, null)));
//            env = new TraceBindingVector(numVars, newTrace);
//        }
//        
//        if(Coror.printTrace && ReasonerConfig.testAlgorithms && Coror.printDetailedTrace) System.err.println(" pEnv: "+pEnv+"\n    "+this.context.rule.getName()+"\n    "+((PartialTraceBindingVector_Test)pEnv).getTraceAsString());
//        
        System.arraycopy(pEnv.pEnvironment, 0, env.environment, 0, pEnv.pEnvironment.length);
        
        // normal fire
        fire(env, isAdd);   
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
