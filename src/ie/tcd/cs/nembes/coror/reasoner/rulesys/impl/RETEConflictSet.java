package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.Coror;
import ie.tcd.cs.nembes.coror.graph.TraceTriple;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.graph.temporal.TemporalTriple;
import ie.tcd.cs.nembes.coror.reasoner.ReasonerException;
import ie.tcd.cs.nembes.coror.reasoner.TriplePattern;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BindingEnvironment;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Builtin;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.ForwardRuleInfGraphI;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Functor;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Rule;
//import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh.TempTriple;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.UnsupportedOperationException;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;

/**
 * Manages a set of ready-to-fire rules. For monotonic rule sets
 * we simply fire the rules as soon as they are triggered. For non-monotonic
 * rule sets we stack them up in a conflict set and fire them one-at-a-time,
 * propagating all changes between times.
 * <p>
 * Note, implementation is not thread-safe. Would be easy to make it so but 
 * concurrent adds to InfModel are not supported anyway.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.6 $
 */

public class RETEConflictSet {

	// Comments by Wei
	// All loggers are removed
//	protected static Log logger = LogFactory.getLog(FRuleEngine.class);

    /** the execution context for this conflict set */
    protected RETERuleContext gcontext;

    /** false if the overall rule system contains some non-montotonic rules */
    protected boolean isMonotonic;
    
    /** the list of rule activations left to fire */
    protected List conflictSet = new List();

    /** count the number of positive entries - optimization hack */
    protected int nPos = 0;
    
    /** count the number of negative entries - optimization hack */
    protected int nNeg = 0;
    
    /** Construct an empty conflict set, noting whether the overall rule system is monotonic or not */
    public RETEConflictSet(RETERuleContext context, boolean isMonotonic) {
        this.gcontext = context;
        this.isMonotonic = isMonotonic;
    }
    
    /**
     * Record a request for a rule firing. For monotonic rulesets it may be
     * actioned immediately, otherwise it will be stacked up.
     */
    public void add(Rule rule, BindingEnvironment env, boolean isAdd) {
        if (isMonotonic) {
            RETERuleContext context = new RETERuleContext((ForwardRuleInfGraphI)gcontext.getGraph(), gcontext.getEngine());
            context.setEnv(env);
            context.setRule(rule);
            execute(context, isAdd);
        } else {
            // Add to the conflict set, compressing +/- pairs
            boolean done = false;
            if ( (isAdd && nNeg > 0) || (!isAdd && nPos > 0) ) {
                for (Iterator i = conflictSet.iterator(); i.hasNext(); ) {
                    CSEntry cse = (CSEntry)i.next();
                    if (cse.rule != rule) continue;
                    if (cse.env.equals(env)) {
                        if (isAdd != cse.isAdd) {
                            try {
                                    i.remove();
                            } catch (UnsupportedOperationException e) {
                                    e.printStackTrace();
                            }
						
                            if (cse.isAdd) nPos--; else nNeg --;
                            done = true;
                        } else {
                            // Redundant insert? Probably leave in for side-effect cases like print
                        }
                    }
                }
            }
            if (!done) {
                conflictSet.add(new CSEntry(rule, env, isAdd));
                if (isAdd) nPos++; else nNeg++;
            }
        }
    }

    /**
     * Return true if there are no more rules awaiting firing.
     */
    public boolean isEmpty() {
        return conflictSet.isEmpty();
    }
    
    /**
     * Pick on pending rule from the conflict set and fire it.
     * Return true if there was a rule to fire.
     */
    public boolean fireOne() {
        
        if (isEmpty()) return false;
        int index = conflictSet.size() - 1;
        CSEntry cse = (CSEntry)conflictSet.remove(index);
        if (cse.isAdd) nPos--; else nNeg --;
        RETERuleContext context = new RETERuleContext((ForwardRuleInfGraphI)gcontext.getGraph(), gcontext.getEngine());
        context.setEnv(cse.env);
        context.setRule(cse.rule);
        if (context.shouldStillFire()) {
            execute(context, cse.isAdd);
        }
        
        return true; 
    }
    
    /**
     * Execute a single rule firing. 
     */
    public static void execute(RETERuleContext context, boolean isAdd) {

        Rule rule = context.getRule();
        BindingEnvironment env = context.getEnv();
        ForwardRuleInfGraphI infGraph = (ForwardRuleInfGraphI)context.getGraph();
        
        RETEEngine engine = context.getEngine();
        engine.incRuleCount();
        List matchList = null;
//        if (infGraph.shouldLogDerivations() && isAdd) {
//            // Create derivation record
//            matchList = new List(rule.bodyLength());
//            for (int i = 0; i < rule.bodyLength(); i++) {
//                Object clause = rule.getBodyElement(i);
//                if (clause instanceof TriplePattern) {
//                    matchList.add(env.instantiate((TriplePattern)clause));
//                } 
//            }
//        }
        for (int i = 0; i < rule.headLength(); i++) {
            Object hClause = rule.getHeadElement(i);
            if (hClause instanceof TriplePattern) {
                Triple t = env.instantiate((TriplePattern) hClause);
                if(env instanceof BindingVectorTemporal){
                    t = new TemporalTriple(t.getSubject(), t.getPredicate(), t.getObject(), ((BindingVectorTemporal)env).getTime());
                }

//                if (!t.getSubject().isLiteral()) {
//                    if(ReasonerConfig.truthMaintenance){
//                        if(isAdd) engine.addDeductionTriple(t);
//                        else engine.deleteDeductionTriple(t);
//                    }
//                    else{
                        // Only add the result if it is legal at the RDF level.
                        // E.g. RDFS rules can create assertions about literals
                        // that we can't record in RDF
                        if (isAdd) {
                            
                            if ( ! context.contains(t)) {
                               
                                engine.addTriple(t, true);
//                                System.err.println(" Triple inferred " + t);
                        }
                            else if(context.contains(t)&&t instanceof TemporalTriple){
                                
                                ExtendedIterator find = context.getGraph().find(t);
                                TemporalTriple temp=(TemporalTriple)t;
                                TemporalTriple next=null;
                                Triple late=null;
                                TemporalTriple latest=null;
                                
                                while(find.hasNext()){
                                    
                                if(latest==null){
                                    late=(Triple)find.next();
                                    if(late instanceof TemporalTriple ){
                                    latest=(TemporalTriple)late;
                                    
                                    }
                                }
                                else if(latest==null){}
                                else if(latest.getTime()<(next=(TemporalTriple)find.next()).getTime())
                                {latest=next;}
                               
                                }
                            if(latest==null){
                                //below are removed as assigning ts's to facts which orignally don't have one does not make sense
                                //engine.deleteTriple(late, true);
                                
                                //engine.addTriple(temp,true);
                            }    
                            else if(temp.getTime()>latest.getTime()){
                                //System.out.println(latest.toString());
                                //System.out.println("hereIam");
                                //engine.deleteTriple(latest, true);
                                
                                engine.addTriple(temp, true);
                                }
                            
                            
                            }
                        } else {
                            if ( context.contains(t)) {
                                // Remove the generated triple
                                engine.deleteTriple(t, true);
                                
                            }
                        }
//                    }
//                }
            } else if (hClause instanceof Functor && isAdd) {
                Functor f = (Functor)hClause;
                Builtin imp = f.getImplementor();
                if (imp != null) {
                    imp.headAction(f.getBoundArgs(env), f.getArgLength(), context);
                } else {
                    throw new ReasonerException("Invoking undefined Functor " + f.getName() +" in " + rule.toShortString());
                }
            } else if (hClause instanceof Rule) {
                Rule r = (Rule)hClause;
//                if (r.isBackward()) {
//                    if (isAdd) {
//                        infGraph.addBRule(r.instantiate(env));
//                    } else {
//                        infGraph.deleteBRule(r.instantiate(env));
//                    }
//                } else {
                    throw new ReasonerException("Found non-backward subrule : " + r); 
//                }
            }
        }        
    }
        
    // Inner class representing a conflict set entry 
    private static class CSEntry {
        protected Rule rule;
        protected BindingEnvironment env;
        protected boolean isAdd;
        
        CSEntry(Rule rule, BindingEnvironment env, boolean isAdd) {
            this.rule = rule;
            this.env = env;
            this.isAdd = isAdd;
        }
    }
}
