package ie.tcd.cs.nembes.coror.reasoner.rulesys;


//import ie.tcd.cs.nembes.microjenaenh.graph.Capabilities;
import ie.tcd.cs.nembes.coror.graph.Graph;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.Model;
import ie.tcd.cs.nembes.coror.reasoner.InfGraph;
import ie.tcd.cs.nembes.coror.reasoner.Reasoner;
import ie.tcd.cs.nembes.coror.reasoner.ReasonerException;
import ie.tcd.cs.nembes.coror.reasoner.RuleReasoner;
import ie.tcd.cs.nembes.coror.util.List;

/**
 * This is an optimized version of the original RETE engine that other Jena reasoner adopted.
 * @author Wei Tai
 *
 */
public class RETEReasoner implements RuleReasoner {

    /** The rules to be used by this instance of the forward engine */
    protected List rules = new List();
    
    /** A precomputed set of schema deductions */
    protected Graph schemaGraph;
    
    /**
     * Constructor. This is the raw version that does not reference a ReasonerFactory
     * and so has no capabilities description. 
     * @param rules a list of Rule instances which defines the ruleset to process
     */
    public RETEReasoner(List rules) {
        if (rules == null) throw new NullPointerException( "null rules" );
        this.rules = rules;
    }
    
    /**
     * Internal constructor, used to generated a partial binding of a schema
     * to a rule reasoner instance.
     */
    protected RETEReasoner(List rules, Graph schemaGraph) {
        this(rules);
        this.schemaGraph = schemaGraph;
    }

    /**
         Add the given rules to the current set and answer this Reasoner. Provided 
         so that the Factory can deal out reasoners with specified rulesets. 
         There may well be a better way to arrange this.
         TODO review & revise
    */
    public RETEReasoner addRules(List rules) {
        List combined = new List( this.rules );
        combined.addAll( rules );
        setRules( combined );
        return this;
        }
    
    /**
     * Precompute the implications of a schema graph. The statements in the graph
     * will be combined with the data when the final InfGraph is created.
     */
    @Override
    public Reasoner bindSchema(Graph tbox) throws ReasonerException {
        if (schemaGraph != null) {
            throw new ReasonerException("Can only bind one schema at a time to a GenericRuleReasoner");
        }
        Graph graph = new RETERuleInfGraph(this, rules, null, tbox);
        ((InfGraph)graph).prepare();
//        RETEReasoner efr = new RETEReasoner(rules, graph, factory);
        RETEReasoner efr = new RETEReasoner(rules, graph);
        
        
        return efr;
    }

    /**
     * Attach the reasoner to a set of RDF data to process.
     * The reasoner may already have been bound to specific rules or ontology
     * axioms (encoded in RDF) through earlier bindRuleset calls.
     * 
     * @param data the RDF data to be processed, some reasoners may restrict
     * the range of RDF which is legal here (e.g. syntactic restrictions in OWL).
     * @return an inference graph through which the data+reasoner can be queried.
     * @throws ReasonerException if the data is ill-formed according to the
     * constraints imposed by this reasoner.
     */
    @Override
    public InfGraph bind( Graph data ) throws ReasonerException {
        RETERuleInfGraph graph = new RETERuleInfGraph(this, rules, schemaGraph, data);
        return graph;
    }
    public InfGraph bindTemporal( Graph data ) throws ReasonerException {
        TemporalRETERuleInfGraph graph = new TemporalRETERuleInfGraph(this, rules, schemaGraph, data);
        return graph;
    }
    /**
     * Return the list of Rules used by this reasoner
     * @return a List of Rule objects
     */
    @Override
    public List getRules() {
        return rules;
    } 

    /**
     * Set (or change) the rule set that this reasoner should execute.
     * @param rules a list of Rule objects
     */
    @Override
    public void setRules(List rules) {
        this.rules = rules;
        if (schemaGraph != null) {
            // The change of rules invalidates the existing precomputed schema graph
            // This might be recoverable but for now simply flag the error and let the
            // user reorder their code to set the rules before doing a bind!
            throw new ReasonerException("Cannot change the rule set for a bound rule reasoner.\nSet the rules before calling bindSchema");
        }
    }
}
