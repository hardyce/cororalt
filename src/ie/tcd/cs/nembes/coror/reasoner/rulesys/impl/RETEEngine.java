package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.graph.temporal.TemporalTriple;
import ie.tcd.cs.nembes.coror.reasoner.Finder;
import ie.tcd.cs.nembes.coror.reasoner.ReasonerException;
import ie.tcd.cs.nembes.coror.reasoner.TriplePattern;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BindingEnvironment;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Builtin;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.ForwardRuleInfGraphI;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Functor;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Node_RuleVariable;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Rule;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.Map;
import ie.tcd.cs.nembes.coror.util.OneToManyMap;
import ie.tcd.cs.nembes.coror.util.Set;
import ie.tcd.cs.nembes.coror.util.UnsupportedOperationException;
import ie.tcd.cs.nembes.coror.util.iterator.ConcatenatedIterator;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 * A RETE version of the the forward rule system engine. It neeeds to reference
 * an enclosing ForwardInfGraphI which holds the raw data and deductions.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.30 $ on $Date: 2008/01/02 12:06:16 $
 */
public class RETEEngine implements TemporalFRuleEngineI {
    
    /** The parent InfGraph which is employing this engine instance */
    protected ForwardRuleInfGraphI infGraph;
    
    
    /** 
     * Set of rules being used 
     */
    protected List rules;
    
    /** Map from predicate node to clause processor, Node_ANY is used for wildcard predicates */
    protected OneToManyMap clauseIndex;
    
    /** 
     * Queue of newly added triples waiting to be processed 
     * </br><b>Commented by Wei:</b>
     * Field type ArrayList replaced by java.util.Vector
     */
    protected List addsPending = new List();
    
    /** 
     * Queue of newly deleted triples waiting to be processed 
     * </br><b>Commented by Wei:</b>
     * Field type ArrayList replaced by java.util.Vector
     * */
    protected List deletesPending = new List();
    
    /** The conflict set of rules waiting to fire */
    protected RETEConflictSet conflictSet;
    
    /** 
     * List of predicates used in rules to assist in fast data loading 
     * </br><b>Commented by Wei:</b>
     * Replace HashSet by microjena.util.Set
     */
    protected Set predicatesUsed;
    
    /** Flag, if true then there is a wildcard predicate in the rule set so that selective insert is not useful */
    protected boolean wildcardRule;
    
    /** performance stats - number of rules fired */
    long nRulesFired = 0;
    
    /** True if we have processed the axioms in the rule set */
    boolean processedAxioms = false;
    
    /** 
     * True if all the rules are monotonic, so we short circuit the conflict set processing.
     * The functors (builtins) contained in the head of a rule determine whether
     * that rule is monotonic. If non-monotonic functors such as noValue or 
     * remove are used then the rule is not monotonic and so it is with this engine.
     */
    boolean isMonotonic = true;
    
//  =======================================================================
//  Fields for new shared RETE
//  =======================================================================  
    
    /** key is rule id and each entry of a value list is a variable list for a clause/alpha node */
    private OneToManyMap ruleVariableIndex;
    
    /** rule with a list of conditions */
    protected OneToManyMap ruleConditionIndex = new OneToManyMap();
    

    /**
     * Constructor.
     * @param parent the F or FB infGraph that it using this engine, the parent graph
     * holds the deductions graph and source data.
     * @param rules the rule set to be processed
     */
    public RETEEngine(ForwardRuleInfGraphI parent, List rules) {
        infGraph = parent;
        this.rules = rules;
        // Check if this is a monotonic rule set
        isMonotonic = true;
        // Java iterator been replaced by microJena Iterator
        for (Iterator i = rules.iterator(); i.hasNext(); ) {
            Rule r = (Rule)i.next();
            if ( ! r.isMonotonic() ) {
                isMonotonic = false;
                break;
            }
        }
    }

    /**
     * Constructor. Build an empty engine to which rules must be added
     * using setRuleStore().
     * @param parent the F or FB infGraph that it using this engine, the parent graph
     * holds the deductions graph and source data.
     */
    public RETEEngine(ForwardRuleInfGraphI parent) {
        infGraph = parent;
    }
    
    
//  =======================================================================
//  Control methods

    /**
     * Process all available data. This should be called once a deductions graph
     * has be prepared and loaded with any precomputed deductions. It will process
     * the rule axioms and all relevant existing exiting data entries.
     * @param ignoreBrules set to true if rules written in backward notation should be ignored
     * @param inserts the set of triples to be processed, normally this is the
     * raw data graph but may include additional deductions made by preprocessing hooks
     */
    public void init(boolean ignoreBrules, Finder inserts, boolean first) {
        /** the commented out lines below were from before where the network would be recompiled, in which case
         * I tried to clear every thing, I had problems with continuations continually being added though, so I
         * decided to change it to reuse the rete network, which is more sensical anyway**/
        long startex=System.currentTimeMillis();
        if(ruleConditionIndex!=null){
            //ruleConditionIndex.clear();
    System.out.println("rci "+ruleConditionIndex.size());
        }
    
    if(ruleVariableIndex!=null){
    //ruleVariableIndex.clear();
        System.out.println("rvi "+ruleVariableIndex.size());
    }
    if(predicatesUsed!=null){
    //predicatesUsed.clear();
        System.out.println("pu "+predicatesUsed.size());
    }
    if(clauseIndex!=null){
    //clauseIndex.clear();
        System.out.println("ci "+clauseIndex.size());
    }
    
    
    
   
    wildcardRule=false;
        if(ReasonerConfig.shareNodes == true){
            // RETE with shared nodes
            /**first is true for initial reasoning, false for the rest of the time, called in prepare()**/
            if(first){
            compileAlpha(rules, ignoreBrules);
            
            compileBeta();
            
            }
            findAndProcessAxioms();
            /** i had to set this to true in order for the inserts to be read correctly after the initial reasoning, im
             * not sure why a wildcardrule is in reteengine I will have to ask**/
            wildcardRule=true;
            
            
long endex = System.currentTimeMillis();
System.out.println("burn this "+(endex-startex));
            fastInit(inserts);
            
//            if(CororReasoner.printTestInfo){
//                System.err.println();
//                System.err.println("======================================================");
//                System.err.println("=============== Execution Statistics =================");
//                System.err.println("======================================================");
//                System.err.println();               
//                
//                // for each rule
//                Iterator keyIt = ruleConditionIndex.keySet().iterator();
//                List joinNodes = new List();
//                for(;keyIt.hasNext();){
//                    Rule rule = (Rule)keyIt.next();
//                    System.err.println("==========="+rule.getName()+"============");
//                    Iterator conditionIt = ruleConditionIndex.getAll(rule);
//                    while(conditionIt.hasNext()){
//                        RETEClauseFilterNS condition = (RETEClauseFilterNS)conditionIt.next();
////                        System.err.println(condition.id + " noM :" + condition.noM);
////                        System.err.println(condition.id + " noSM :" + condition.noSM);
//                        System.err.print(condition.id+" : ");
//                        for(int i=0; i<condition.continuations.size(); i++){
//                            Object node = condition.continuations.get(i);
//                            if(node instanceof RETETerminal)
//                                System.err.print("Terminal_Node_" + ((RETETerminal)node).context.rule.getName()+" ");
//                            else if(node instanceof RETEQueueNS)
//                                System.err.print("["+((RETEQueueNS)node).id +" :: "+((RETEQueueNS)node).strategies +"]");
//                        }
//                        System.err.println();
//                    }
//                }
//            }
            
            return;
        }
        else {
            // normal RETE
            compile(rules, ignoreBrules);
            findAndProcessAxioms();
            fastInit(inserts);
            preMatch();
        }
    }
    
    /**
     * Process all available data. This version expects that all the axioms 
     * have already be preprocessed and the clause index already exists.
     * @param inserts the set of triples to be processed, normally this is the
     * raw data graph but may include additional deductions made by preprocessing hooks
     */
    public void fastInit(Finder inserts) {
        int j=0;
        
        conflictSet = new RETEConflictSet(new RETERuleContext(infGraph, this), isMonotonic);
        // Below is used during testing to ensure that all ruleset work (if less efficiently) if marked as non-monotonic
//        conflictSet = new RETEConflictSet(new RETERuleContext(infGraph, this), false);
        findAndProcessActions();
        if (infGraph.getRawGraph() != null) {
            // Insert the data
            if (wildcardRule) {
                for (Iterator i = inserts.find(new TriplePattern(null, null, null)); i.hasNext(); ) {
                    addTriple((Triple)i.next(), false);
                    j++;
                }
                System.out.println(j+" inserts");
                
            } else {
                
                for (Iterator p = predicatesUsed.iterator(); p.hasNext(); ) {
                    System.out.println(p.toString());
                    Node predicate = (Node)p.next();
                    for (Iterator i = inserts.find(new TriplePattern(null, predicate, null)); i.hasNext(); ) {
                        Triple t = (Triple)i.next();
                        addTriple(t, false);
                    }
                }
            }
        }
        // Run the engine
        long runst=System.currentTimeMillis();
        runAll();
        long runend=System.currentTimeMillis();
        System.out.println("run tim "+(runend-runst));
    }
    
  
    
    /**
     * Add one triple to the data graph, run any rules triggered by
     * the new data item, recursively adding any generated triples.
     */
    public synchronized void add(Triple t) {
        addTriple(t, false);
        runAll();
    }
    
    /**
     * Remove one triple to the data graph.
     * @return true if the effects could be correctly propagated or
     * false if not (in which case the entire engine should be restarted).
     */
    public synchronized boolean delete(Triple t) {
        deleteTriple(t, false);
        runAll();
        return true;
    }
    
    /**
     * Return the number of rules fired since this rule engine instance
     * was created and initialized
     */
    public long getNRulesFired() {
        return nRulesFired;
    }
    
    /**
     * Access the precomputed internal rule form. Used when precomputing the
     * internal axiom closures.
     */
    public Object getRuleStore() {
        return new RuleStore(clauseIndex, predicatesUsed, wildcardRule, isMonotonic);
    }
    
    /**
     * Set the internal rule from from a precomputed state.
     */
    public void setRuleStore(Object ruleStore) {
        RuleStore rs = (RuleStore)ruleStore;
        predicatesUsed = rs.predicatesUsed;
        wildcardRule = rs.wildcardRule;
        isMonotonic = rs.isMonotonic;
        
        // Clone the RETE network to this engine
        RETERuleContext context = new RETERuleContext(infGraph, this);
        Map netCopy = new Map();
        clauseIndex = new OneToManyMap();
        for (Iterator i = rs.clauseIndex.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            clauseIndex.put(entry.getKey(), ((RETENode)entry.getValue()).clone(netCopy, context));
        }
    }
    
    /**
     * Add a rule firing request to the conflict set.
     */
    public void requestRuleFiring(Rule rule, BindingEnvironment env, boolean isAdd) {
        conflictSet.add(rule, env, isAdd);
    }
    
    /**
     * Compile a node for the loaded rules. The reasoner the compilation of alpha
     * network and beta network is separated is that optimization can be easily
     * added. Conditions for each rule are kept in the ruleConditionIndex. 
     */
    public void compileAlpha(List rules, boolean ignoreBrules) {
        
        if(clauseIndex == null) clauseIndex = new OneToManyMap();
        if(ruleVariableIndex == null) ruleVariableIndex = new OneToManyMap();
        predicatesUsed = new Set();
        wildcardRule = false;
        
        for (Iterator it = rules.iterator(); it.hasNext(); ) {
            
            Rule rule = (Rule)it.next();
            
            if (ignoreBrules && rule.isBackward()) continue;
            
            
            // conditionLength is to record how many conditions are contained by
            // this rule. If only one, then attach it a RETETerminal instead
            // of a RETEQueue.
            
            int bodyLength = rule.bodyLength();
            for (int i = 0; i < bodyLength; i++) {

                Object clause = rule.getBodyElement(i);
                
                // an indicator for whether the clause share alpha node with the others.
                boolean isShared = false;
                // Create the filter node for this pattern
                if (clause instanceof TriplePattern) {
                    
                    TriplePattern pattern = (TriplePattern)clause;
                    byte varSize = 0;
                    if(pattern.getSubject().isVariable()) varSize ++;
                    if(pattern.getPredicate().isVariable()) varSize ++;
                    if(pattern.getObject().isVariable()) varSize ++;
                    // a clause can at most have three variables
                    
                    List varList = new List(varSize);
                    Node predicate = pattern.getPredicate();
                    
                    if (predicate.isVariable()) {
                        RETEClauseFilterNS clauseNode = RETEClauseFilterNS.compileOrShare((TriplePattern)clause, clauseIndex, varList, isShared, generateFilterNodeName(rule,i));
                        ruleConditionIndex.put(rule, clauseNode);
                        ruleVariableIndex.put(rule, varList);
                        addToClauseIndex(Node.ANY, clauseNode);
                        //System.out.println(clauseNode.toString());
                        wildcardRule = true;                        
                    } else {
                        RETEClauseFilterNS clauseNode = RETEClauseFilterNS.compileOrShare((TriplePattern)clause, clauseIndex, varList, isShared, generateFilterNodeName(rule,i));                                                                        
                        ruleConditionIndex.put(rule, clauseNode);
                        ruleVariableIndex.put(rule, varList);
                        addToClauseIndex(predicate, clauseNode);
   // System.out.println("pred "+clauseNode.toString());
                        if (! wildcardRule) {
                            predicatesUsed.add(predicate);
                        }
                    }
                }
                else{
                    // non-triple pattern clause, e.g. function, built-ins
                }
            }                              
        }
         
        // dont understand why need predicatesUsed: it is used to selectively match triples with used predicates
        //if (wildcardRule) predicatesUsed = null;        
    }
    
    /**
     * Add a condition to the clauseIndex only when it is not already existent.
     */
    private void addToClauseIndex(Node node, RETEClauseFilterNS clause){
        Iterator i = clauseIndex.getAll(node);
        while(i.hasNext()){
            if(i.next() == clause)
                return;
        }
        clauseIndex.put(node, clause);
    }
    
    /**
     * Generate an ID for an alpha node
     * @return 
     */
    private String generateFilterNodeName(Rule rule, int index){
        return rule.getName()+"."+String.valueOf(index);
    }
    
    /**
     * Generate an ID for a beta node
     */
    private String generateQueueNodeName(String left, String right){
        return "("+left+"+"+right+")";
    }
    
    private void cloneBooleanArray( boolean[] backupSeenVar, boolean[] seenVar){
        for(byte i=0; i<seenVar.length; i++){
            backupSeenVar[i] = seenVar[i];
        }
    }
    
    private void compileBeta(){

        Iterator ruleIt = ruleConditionIndex.keySet().iterator();

        while(ruleIt.hasNext()){
            
            Object rule = ruleIt.next();
            Iterator conditionIt = ruleConditionIndex.getAll(rule);
            Iterator varListIt = ruleVariableIndex.getAll(rule);
            SharedNodeI prior = null;
            List priorVarList = null;
//            if(CororReasoner.printTestInfo) {
//                System.err.println();
//                System.err.println("==========="+((Rule)rule).getName()+"===========");
//            }

            while(conditionIt.hasNext() && varListIt.hasNext()){
                RETEClauseFilterNS condition = (RETEClauseFilterNS)conditionIt.next();
                List varList = (List)varListIt.next();
                
//                if(CororReasoner.printTestInfo) {
//                    System.err.print("Filter Node "+condition.getNodeID()+" varList ");
//                    for(int i=0; i<varList.size(); i++)
//                        System.err.print(((Node_RuleVariable)varList.get(i)).getIndex()+" ");
//                    System.err.println();
//                }
                
                if(prior == null ) {
                    prior = condition;
                    priorVarList = varList;
                    continue;
                }                

                // create join strategy. VarList and PriorVarList are made with the exact the same size as the number of variables in the condition, see compileAlpha
                JoinStrategy strategy = new JoinStrategy();
                for(int i=0; i<priorVarList.size(); i++){
                    
                    Node_RuleVariable priorVar = (Node_RuleVariable)priorVarList.get(i);
                    Node_RuleVariable currentVar;
                    for(int j=0; j<varList.size(); j++){
                        currentVar = (Node_RuleVariable)varList.get(j);
                        if(Node_RuleVariable.sameNodeAs(priorVar, currentVar)){
                            strategy.putStrategy((byte)i, (byte)j);
                        }                        
                    }
                }    
                
                List newPriorVarList = new List(priorVarList.size()+varList.size()-strategy.count);
                newPriorVarList.addAll(priorVarList);
                for(int i=0; i<varList.size();i++){
                    boolean exist = false;
                    for(int j=0; j<priorVarList.size(); j++){
                        if(Node_RuleVariable.sameNodeAs((Node_RuleVariable)varList.get(i), (Node_RuleVariable)priorVarList.get(j))){
                            exist = true;
                            break;
                        }
                    }
                    if(!exist) newPriorVarList.add(varList.get(i));
                }
                priorVarList = newPriorVarList;
                
                
                // looking for share
                boolean shareJoin = false;
                Map sharedBuffer = null;
                OneToManyMap otm =null;
                RETEQueueNS leftQ = null;
                List priorConts = prior.getContinuations();
                for(int i=0; i<priorConts.size(); i++){                    
                    if(priorConts.get(i) instanceof RETEQueueNS){
                        RETEQueueNS continuation = (RETEQueueNS)priorConts.get(i);
                        sharedBuffer = continuation.queue;   
                        otm=continuation.otm;
                        if(continuation.sibling.parent == condition && 
                                continuation.getJoinStrategies().sameStrategyAs(strategy)){
                            /** 
                             * Prior(always leftQ or filter node) already has continuations. 
                             * The sibling's parent of this continuation (must be a Filter Node)
                             * is the same as this condition. The join strategy are the same.
                             * Then share
                             */
                            leftQ = continuation;
                            shareJoin = true;
                            break;
                        }
                    }
                }
                if(leftQ == null && sharedBuffer == null)
                    leftQ = new RETEQueueNS(strategy, true, priorVarList.size(), generateQueueNodeName(prior.getNodeID(), condition.getNodeID())); // now the priorVarList is the newPriorVarList
                else if(leftQ == null && sharedBuffer != null)
                    leftQ = new RETEQueueNS(strategy, sharedBuffer, true, priorVarList.size(), generateQueueNodeName(prior.getNodeID(), condition.getNodeID()),otm);
                
                RETEQueueNS rightQ = null;
                if(shareJoin){
                    rightQ = leftQ.sibling;
                    //rightQ.sibling=leftQ;
                }
                else{
                    List continuations = condition.getContinuations();
                    for(int i=0; i<continuations.size(); i++)
                        if(continuations.get(i) instanceof RETEQueueNS){
                            // construct a rightQ with shared buffer
                            rightQ = new RETEQueueNS(strategy, ((RETEQueueNS)continuations.get(i)).queue, false, priorVarList.size(), generateQueueNodeName(prior.getNodeID(), condition.getNodeID()),((RETEQueueNS)continuations.get(i)).otm);
                            break;
                        }
                    if(rightQ == null) // construct a rightQ with new buffer
                        rightQ = new RETEQueueNS(strategy, false, priorVarList.size(), generateQueueNodeName(prior.getNodeID(), condition.getNodeID()));  
                }
                
//                if(CororReasoner.printTestInfo) {
//                    System.err.println("    join node "+leftQ.getNodeID());
//                    System.err.print("    join strategy ");
//                    for(int i=0; i<strategy.left.length; i++){
//                        if(strategy.left[i] == -1) break;
//                        System.err.print(strategy.left[i]+"-"+strategy.right[i]+" ");
//                    }
//                    System.err.println();
//                    System.err.print("    join node output varList ");
//                    for(int i=0; i<priorVarList.size(); i++)
//                        System.err.print(((Node_RuleVariable)priorVarList.get(i)).getIndex()+" ");
//                    System.err.println();
//                }
                                
                if(!shareJoin){
                    leftQ.setSibling(rightQ);
                    rightQ.setSibling(leftQ);
                    prior.addContinuation(leftQ);  
                    condition.addContinuation(rightQ);
                }
                prior = leftQ; 
            }
            if ( prior != null ) {
                RETETerminal terminal = new RETETerminal((Rule)rule, this, infGraph);
                prior.addContinuation(terminal);
//                System.err.println("installing rule head "+ruleId+(RETETerminal)ruleTerminalMap.get(ruleId));
            }
            
//            if(CororReasoner.printTestInfo) {
//                System.err.println("=============================");
//            }
        }
    }

    /**
     * Compile a list of rules into the internal rule store representation.
     * @param rules the list of Rule objects
     * @param ignoreBrules set to true if rules written in backward notation should be ignored
     */
    public void compile(List rules, boolean ignoreBrules) {
        
        clauseIndex = new OneToManyMap();
        predicatesUsed = new Set();
        wildcardRule = false;
            
        for (Iterator it = rules.iterator(); it.hasNext(); ) {
            Rule rule = (Rule)it.next();
            if (ignoreBrules && rule.isBackward()) continue;
            
            int numVars = rule.getNumVars();
            boolean[] seenVar = new boolean[numVars];
            RETESourceNode prior = null;
        
            for (int i = 0; i < rule.bodyLength(); i++) {
                Object clause = rule.getBodyElement(i);
                if (clause instanceof TriplePattern) {
                    // Create the filter node for this pattern
                	// Comment by Wei:
                	// ArrayList is replaced by Vector
                    List clauseVars = new List(numVars);
                    RETEClauseFilter clauseNode = RETEClauseFilter.compile((TriplePattern)clause, numVars, clauseVars);
                    Node predicate = ((TriplePattern)clause).getPredicate();
                    if (predicate.isVariable()) {
                        clauseIndex.put(Node.ANY, clauseNode);
                        wildcardRule = true;
                    } else {
                        clauseIndex.put(predicate, clauseNode);
                        if (! wildcardRule) {
                            predicatesUsed.add(predicate);
                        }
                    }
                
                    // Create list of variables which should be cross matched between the earlier clauses and this one
                    List matchIndices = new List(numVars);
                    for (Iterator iv = clauseVars.iterator(); iv.hasNext(); ) {
                        int varIndex = ((Node_RuleVariable)iv.next()).getIndex();
                        if (seenVar[varIndex]) matchIndices.add(new Byte((byte)varIndex));
                        seenVar[varIndex] = true;
                    }
                    
                    // Build the join node
                    if (prior == null) {
                        // First clause, no joins yet
                        prior = clauseNode;
                    } else {
                        RETEQueue leftQ = new RETEQueue(matchIndices);
                        RETEQueue rightQ = new RETEQueue(matchIndices);
                        leftQ.setSibling(rightQ);
                        rightQ.setSibling(leftQ);
                        clauseNode.setContinuation(rightQ);
                        prior.setContinuation(leftQ);
                        prior = leftQ;
                    }
                }
            }
            
            // Finished compiling a rule - add terminal 
            if (prior != null) {
                RETETerminal term = createTerminal(rule);
                prior.setContinuation(term);
            }
                    
        }
            
        if (wildcardRule) predicatesUsed = null;
    }    

    /**
     * Create a terminal node for the RETE network. Normally this is RETETerminal
     * but some people have experimented with alternative delete handling by
     * creating RETETerminal subclasses so this hook simplifies use of that
     * approach.
     */
    protected RETETerminal createTerminal(Rule rule) {
        return new RETETerminal(rule, this, infGraph);
    }
    
//  =======================================================================
//  Internal implementation methods

    /**
     * Add a new triple to the network. 
     * @param triple the new triple
     * @param deduction true if the triple has been generated by the rules and so should be 
     * added to the deductions graph.
     */
    public synchronized void addTriple(Triple triple, boolean deduction) {
        
        //infGraph.getRawGraph().delete(triple);
        if (deletesPending.size() > 0) deletesPending.remove(triple);
        
        addsPending.add(triple);
        if (deduction) {
            infGraph.addDeduction(triple);
            
        }
        
    }
    
    /**
     * Remove a new triple from the network. 
     * @param triple the new triple
     * @param deduction true if the remove has been generated by the rules 
     */
    public synchronized void deleteTriple(Triple triple, boolean deduction) {
        
//        boolean delete = true;
        addsPending.remove(triple);
        deletesPending.add(triple);
        if (deduction) {
            
            infGraph.getDeductionsGraph().delete(triple);
            Graph raw = infGraph.getRawGraph();
            // deduction retractions should not remove asserted facts, so commented out next line
             raw.delete(triple);
            if (raw.contains(triple)) {
                // Built in a graph which can't delete this triple
                // so block further processing of this delete to avoid loops
                
                //deletesPending.remove(triple);
//                delete = false;
            }
        }
    }
    
    /**
     * Remove up the T-PBVs in the RETE network within the given time slot. 
     * @param start start of the time slot.
     * @param end end of the time slot.
     */
    public void readRETE(long j){
        Iterator keyIt = ruleConditionIndex.keySet().iterator();
        System.out.println(ruleConditionIndex.size());
        // remove T-PBV within a time slot
        for(;keyIt.hasNext();){
            Rule rule = (Rule)keyIt.next();
            Iterator conditionIt = ruleConditionIndex.getAll(rule);
            
            while(conditionIt.hasNext()){
                RETEClauseFilterNS condition = (RETEClauseFilterNS)conditionIt.next();
                //System.out.println(condition.continuations.size());
                for(int i=0; i<condition.continuations.size(); i++){
                    Object node = condition.continuations.get(i);
                    if(node instanceof RETEQueueNS){
                        
                        ((RETEQueueNS)node).readBuffer(j);
                    }
                    
                }
            }
        }
    }
    public void sweepRETE(long start, long end){       
        // delete partial match
     //   System.out.println(ruleVariableIndex.size());
        Iterator keyIt = ruleConditionIndex.keySet().iterator();
        System.out.println(ruleConditionIndex.size());
        // remove T-PBV within a time slot
        for(;keyIt.hasNext();){
            Rule rule = (Rule)keyIt.next();
            Iterator conditionIt = ruleConditionIndex.getAll(rule);
            
            while(conditionIt.hasNext()){
                RETEClauseFilterNS condition = (RETEClauseFilterNS)conditionIt.next();
                //System.out.println(condition.continuations.size());
                for(int i=0; i<condition.continuations.size(); i++){
                    Object node = condition.continuations.get(i);
                    if(node instanceof RETEQueueNS)
                        try {
                        ((RETEQueueNS)node).sweepBuffer(start, end,false);
                    } catch (UnsupportedOperationException ex) {
                        Logger.getLogger(RETEEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        // reset the tidied field to false after tidied up is finished
        keyIt = ruleConditionIndex.keySet().iterator();
        for(;keyIt.hasNext();){
            Rule rule = (Rule)keyIt.next();
            Iterator conditionIt = ruleConditionIndex.getAll(rule);
            while(conditionIt.hasNext()){
                RETEClauseFilterNS condition = (RETEClauseFilterNS)conditionIt.next();
                for(int i=0; i<condition.continuations.size(); i++){
                    Object node = condition.continuations.get(i);
                    if(node instanceof RETEQueueNS)
                        ((RETEQueueNS)node).finishSweep();
                }
            }
        }
    }
    
    /**
     * Increment the rule firing count, called by the terminal nodes in the
     * network.
     */
    protected void incRuleCount() {
        nRulesFired++;
    }
    
    /**
     * Find the next pending add triple.
     * @return the triple or null if there are none left.
     */
    protected synchronized Triple nextAddTriple() {
        int size = addsPending.size(); 
        if (size > 0) {         
            return (Triple)addsPending.remove(size - 1);
        }
        return null;
    }
    
    /**
     * Find the next pending add triple.
     * @return the triple or null if there are none left.
     */
    protected synchronized Triple nextDeleteTriple() {
        int size = deletesPending.size();
        //System.out.println("del pending "+size);
        if (size > 0) {
        	return (Triple)deletesPending.remove(size - 1);
        }
        return null;
    }

    // NOTE state is for printJoin use, should be remove in release
//    public int state = 0;
    /**
     * Process the queue of pending insert/deletes until the queues are empty.
     * Public to simplify unit tests - not normally called directly.
     */
    public void runAll() {
//        state ++;
        int x=0;
        System.out.println("Adds Pending "+addsPending.size());
            System.out.println("Deletes Pending "+deletesPending.size());
        while(true) {
            
            boolean isAdd = false;
            Triple next = nextDeleteTriple();
            //if(next!=null){
            //System.out.println("del this "+next.toString());}
            if (next == null) {
                next = nextAddTriple();
                isAdd = true;
            }
//            if(!isAdd){
//                System.err.println(" DEBUG (RETEEngine::runAll) : size of deletePending is "+deletesPending.size());
//            }
            if (next == null) {
                // Nothing more to inject, if this is a non-mon rule set now process one rule from the conflict set
                if (conflictSet.isEmpty()) {
                    System.out.println("run iterations "+x);
                    return; // Finished
                } 
                System.out.println("fireone");
                // Wei: Never reach this in monotonic reasoning. RETEConflictSet.execute()
                // is called in RETEConflictSet.add().
                conflictSet.fireOne();
            } else {
                //System.out.println("inject "+isAdd+" "+next.toString());
//                if(state > 1){
//                    boolean temp = next instanceof TempTriple? true:false;
//                    System.err.println( (isAdd? "add ":"remove ")+(temp? "TempTriple: ":"Triple: ")+next);
//                }
                inject(next, isAdd);
            }
            x++;
        }
        
    }
    
    /**
     * Wei: Singleton rules (rules with only one condition) are attached with terminal
     * node during alpha network compilation. Those rules will be fired in prematch
     * by inject(). Results will be populated into addsPendings and matched against
     * other conditions until no singleton rules can be fired.
     */
    public void preMatch(){
        while(true) {
            boolean isAdd = false;
            Triple next = nextDeleteTriple();
            
            if (next == null) {
                next = nextAddTriple();
                isAdd = true;
            }
            if (next == null) {
                // Nothing more to inject during prematch, then finish prematch
                return;
            } else {
                inject(next, isAdd);
            }
        }         
    }

    /**
     * Inject a single triple into the RETE network
     */
    private void inject(Triple t, boolean isAdd) {
        /*if(isAdd){
System.out.println("add "+t.toString());
        }
        else {System.out.println("del "+t.toString());} */
        //System.out.println("inject "+isAdd+" "+t.toString());
        Iterator i1 = clauseIndex.getAll(t.getPredicate());
        
        Iterator i2 = clauseIndex.getAll(Node.ANY);

        Iterator i = new ConcatenatedIterator(i1, i2);
        
        while (i.hasNext()) {   
            ((FireTripleI)i.next()).fire(t, isAdd);      
        }
    }
    
    /**
     * This fires a triple into the current RETE network. 
     * This format of call is used in the unit testing but needs to be public
     * because the tester is in another package.
     */
    public void testTripleInsert(Triple t) {
        Iterator i1 = clauseIndex.getAll(t.getPredicate());
        Iterator i2 = clauseIndex.getAll(Node.ANY);
        Iterator i = new ConcatenatedIterator(i1, i2);
        while (i.hasNext()) {
            RETEClauseFilter cf = (RETEClauseFilter) i.next();
            cf.fire(t, true);
        }
    }
    
    /**
     * Scan the rules for any axioms and insert those
     */
    protected void findAndProcessAxioms() {
        for (Iterator i = rules.iterator(); i.hasNext(); ) {
            Rule r = (Rule)i.next();
            if (r.isAxiom()) {
                // An axiom
                RETERuleContext context = new RETERuleContext(infGraph, this);
                context.setEnv(new BindingVector(new Node[r.getNumVars()]));
                context.setRule(r);
                if (context.shouldFire(true)) {
                    RETEConflictSet.execute(context, true);
                }
                /*   // Old version, left during printJoin and final checks
                for (int j = 0; j < r.headLength(); j++) {
                    Object head = r.getHeadElement(j);
                    if (head instanceof TriplePattern) {
                        TriplePattern h = (TriplePattern) head;
                        Triple t = new Triple(h.getSubject(), h.getPredicate(), h.getObject());
                        addTriple(t, true);
                    }
                }
                */
            }
        }
        processedAxioms = true;
    }
    
    /**
     * Scan the rules for any run actions and run those
     */
    protected void findAndProcessActions() {
        RETERuleContext tempContext = new RETERuleContext(infGraph, this);
        for (Iterator i = rules.iterator(); i.hasNext(); ) {
            Rule r = (Rule)i.next();
            if (r.bodyLength() == 0) {
                for (int j = 0; j < r.headLength(); j++) {
                    Object head = r.getHeadElement(j);
                    if (head instanceof Functor) {
                        Functor f = (Functor)head;
                        Builtin imp = f.getImplementor();
                        if (imp != null) {
                            tempContext.setRule(r);
                            tempContext.setEnv(new BindingVector( r.getNumVars() ));
                            imp.headAction(f.getArgs(), f.getArgLength(), tempContext);
                        } else {
                            throw new ReasonerException("Invoking undefined Functor " + f.getName() +" in " + r.toShortString());
                        }
                    }
                }
            }
        }
    }

    public ForwardRuleInfGraphI getInfGraph(){
        return infGraph;
    }
 
//=======================================================================
// Inner classes

    /**
     * Structure used in the clause index to indicate a particular
     * clause in a rule. This is used purely as an internal data
     * structure so we just use direct field access.
     */
    protected static class ClausePointer {
        
        /** The rule containing this clause */
        protected Rule rule;
        
        /** The index of the clause in the rule body */
        protected int index;
        
        /** constructor */
        ClausePointer(Rule rule, int index) {
            this.rule = rule;
            this.index = index;
        }
        
        /** Get the clause pointed to */
        TriplePattern getClause() {
            return (TriplePattern)rule.getBodyElement(index);
        }
    }
    
    /**
     * Structure used to wrap up processed rule indexes.
     */
    public static class RuleStore {
    

		/** Map from predicate node to rule + clause, Node_ANY is used for wildcard predicates */
        protected OneToManyMap clauseIndex;
    
        /** List of predicates used in rules to assist in fast data loading */
        public Set predicatesUsed;
        
        /** Flag, if true then there is a wildcard predicate in the rule set so that selective insert is not useful */
        protected boolean wildcardRule;
        
        /** True if all the rules are monotonic, so we short circuit the conflict set processing */
        protected boolean isMonotonic = true;
        
        /** Constructor */
        RuleStore(OneToManyMap clauseIndex, Set predicatesUsed, boolean wildcardRule, boolean isMonotonic) {
            this.clauseIndex = clauseIndex;
            this.predicatesUsed = predicatesUsed;
            this.wildcardRule = wildcardRule;
            this.isMonotonic = isMonotonic;
        }
    }  
}