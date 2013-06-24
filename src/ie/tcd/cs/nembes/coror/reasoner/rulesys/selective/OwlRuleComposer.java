/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.selective;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.ReasonerConfig;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.Map;
import ie.tcd.cs.nembes.coror.util.Map.Entry;
import ie.tcd.cs.nembes.coror.util.Set;
import ie.tcd.cs.nembes.coror.vocabulary.OWL;
import ie.tcd.cs.nembes.coror.vocabulary.RDF;
import ie.tcd.cs.nembes.coror.vocabulary.RDFS;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The OwlRuleComposer implements the selective rule loading algorithm. It 
 * reads in the rule-construct dependencies and decides if a rule needs 
 * to be loaded for reasoning the given ontology.
 * 
 * @author Wei Tai
 */
//TODO Automatic rule selection according to parsed Jena rules.
public class OwlRuleComposer {

    /** comments token for the rule-construct dependency file */
    private final static String commentsToken = "//";
    
    /** a map from each rule (key) to its selection status (value) */
    private Map ruleStatus = new Map();
    
//    // the model containing the ontology
//    private Model model = null;
    
    private Graph graph;
    
    //qname
    private final static String rdfs = "rdfs:";
    private final static String rdf = "rdf:";
    private final static String owl = "owl:";
    
    public OwlRuleComposer(Graph g){
        
        // how dare you pass in a null model. This makes no sense.
        if (g == null)
            throw new CororException("an null graph is cannot be used for rule selecting");
        
        // initialize
        graph = g;
        readRuleConstructs();
        composeRules(g);
    }
    
    /**
     * @return rules selected for reasoning the particular ontology
     */
    public List getSelectedRules(){
        return getRulesOfStatus(true);
    }
    
    /**
     * @return rules not selected for reasoning the particular ontology
     */
    public List getUnselectedRules(){
        return getRulesOfStatus(false);
    }
    
    /**
     * retrieve rules of a particular status (selected - true or unselected - false).
     * @param s true for selected rules and false for unselected rules.
     * @return 
     */
    private List getRulesOfStatus(boolean s){
        List retVal = new List();
        Set entrySet = ruleStatus.entrySet();
        for(int i = 0; i < entrySet.size(); i++){
            Entry entry = (Entry)entrySet.get(i);
            if(entry.getValue().equals(s)){
                retVal.add(entry.getKey());
            }
        }
        return retVal;
    }
    
    /**
     * Compose a selective rule set. In this algorithm only those constructs included
     * in the pD* rules are checked. The others such as owl:intersectionOf, owl:unionOf
     * owl:cardinality, owl:maxCardinality, owl:minCardinality are not checked.
     * 
     * @param model 
     */
    private void composeRules(Graph g){
        
        // here some OWL constructs such as intersectionOf, unionOf are not included
        List ontSignature = new List();        
        
        // check if the ontology contains the constructs
        if(g.contains(Node.ANY, RDFS.Nodes.domain, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+RDFS.Nodes.domain.getLocalName());
            ontSignature.add(rdfs+RDFS.Nodes.domain.getLocalName());
        }
        if(g.contains(Node.ANY, RDFS.Nodes.range, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+RDFS.Nodes.range.getLocalName());
            ontSignature.add(rdfs+RDFS.Nodes.range.getLocalName());
        }
        if(g.contains(Node.ANY, RDFS.Nodes.subClassOf, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+RDFS.Nodes.subClassOf.getLocalName());
            ontSignature.add(rdfs+RDFS.Nodes.subClassOf.getLocalName());
        }            
        if(g.contains(Node.ANY, RDFS.Nodes.subPropertyOf, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+rdfs+RDFS.Nodes.subPropertyOf.getLocalName());
            ontSignature.add(rdfs+RDFS.Nodes.subPropertyOf.getLocalName());
        }
        if(g.contains(Node.ANY, RDF.Nodes.type, RDF.Nodes.Property)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+rdf+RDF.Nodes.Property.getLocalName());
            ontSignature.add(rdf+RDF.Nodes.Property.getLocalName());
        }
        if(g.contains(Node.ANY, RDF.Nodes.type, RDFS.Nodes.ContainerMembershipProperty)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+rdfs+RDFS.Nodes.ContainerMembershipProperty.getLocalName());
            ontSignature.add(rdfs+RDFS.Nodes.ContainerMembershipProperty.getLocalName());
        }
        if(g.contains(Node.ANY, RDF.Nodes.type, RDFS.Nodes.Datatype)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+rdfs+RDFS.Nodes.Datatype.getLocalName());
            ontSignature.add(rdfs+RDFS.Nodes.Datatype.getLocalName());
        }
        if(g.contains(Node.ANY, RDFS.Nodes.Literal, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+rdfs+RDFS.Nodes.Literal.getLocalName());
            ontSignature.add(rdfs+RDFS.Nodes.Literal.getLocalName());
        }
        if(g.contains(Node.ANY, RDF.Nodes.type, OWL.Nodes.FunctionalProperty)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.FunctionalProperty.getLocalName());
            ontSignature.add(owl+OWL.Nodes.FunctionalProperty.getLocalName());
        }
        if(g.contains(Node.ANY, RDF.Nodes.type, OWL.Nodes.InverseFunctionalProperty)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.InverseFunctionalProperty.getLocalName());
            ontSignature.add(owl+OWL.Nodes.InverseFunctionalProperty.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.sameAs, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.sameAs.getLocalName());
            ontSignature.add(owl+OWL.Nodes.sameAs.getLocalName());
        }
        if(g.contains(Node.ANY, RDF.Nodes.type, OWL.Nodes.SymmetricProperty)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.SymmetricProperty.getLocalName());
            ontSignature.add(owl+OWL.Nodes.SymmetricProperty.getLocalName());
        }
        if(g.contains(Node.ANY, RDF.Nodes.type, OWL.Nodes.TransitiveProperty)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.TransitiveProperty.getLocalName());
            ontSignature.add(owl+OWL.Nodes.TransitiveProperty.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.inverseOf, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.inverseOf.getLocalName());
            ontSignature.add(owl+OWL.Nodes.inverseOf.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.equivalentClass, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.equivalentClass.getLocalName());
            ontSignature.add(owl+OWL.Nodes.equivalentClass.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.equivalentProperty, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.equivalentProperty.getLocalName());
            ontSignature.add(owl+OWL.Nodes.equivalentProperty.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.hasValue, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.hasValue.getLocalName());
            ontSignature.add(owl+OWL.Nodes.hasValue.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.onProperty, Node.ANY)){
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.onProperty.getLocalName());
            ontSignature.add(owl+OWL.Nodes.onProperty.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.someValuesFrom, Node.ANY)){
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.someValuesFrom.getLocalName());
            ontSignature.add(owl+OWL.Nodes.someValuesFrom.getLocalName());
        }
        if(g.contains(Node.ANY, OWL.Nodes.allValuesFrom, Node.ANY)) {
            System.err.println(" DEBUG (RuleSetComposer::composeRules) : detect "+owl+OWL.Nodes.allValuesFrom.getLocalName());
            ontSignature.add(owl+OWL.Nodes.allValuesFrom.getLocalName());
        }
        
        // new algorithm: less effectively
//        boolean foundDomain = false;
//        boolean foundRange = false;
//        boolean foundSubClassOf = false;
//        boolean foundSubPropertyOf = false;
//        boolean foundProperty = false;
//        boolean foundContainerMembershipProperty = false;
//        boolean foundDatatype = false;
//        boolean foundFuncitonalProperty = false;
//        boolean foundInverseFunctionalProperty = false;
//        boolean foundSameAs = false;
//        boolean foundSymmetricProperty = false;
//        boolean foundTransitiveProperty = false;
//        boolean foundInverseOf = false;
//        boolean foundEquivalentClass = false;
//        boolean foundEquivalentProperty = false;
//        boolean foundHasValue = false;
//        boolean foundOnProperty = false;
//        boolean foundSomeValuesFrom = false;
//        boolean foundAllValuesFrom = false;
//        
//        // this is a new algorithm to check constructs in OWL document
//        ExtendedIterator eit = model.getGraph().find(null, null, null);
//        while(eit.hasNext()){
//            Triple t = (Triple)eit.next();
//            if(!foundDomain && t.matches(Node.ANY, RDFS.domain.asNode(), Node.ANY)) {
//                foundDomain = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+RDFS.domain.getLocalName());
//                ontSignature.add(rdfs+RDFS.domain.getLocalName());
//            }
//            else if(!foundRange && t.matches(Node.ANY, RDFS.range.asNode(), Node.ANY)) {
//                foundRange = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+RDFS.range.getLocalName());
//                ontSignature.add(rdfs+RDFS.range.getLocalName());
//            }
//            else if(!foundSubClassOf && t.matches(Node.ANY, RDFS.subClassOf.asNode(), Node.ANY)) {
//                foundSubClassOf = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+RDFS.subClassOf.getLocalName());
//                ontSignature.add(rdfs+RDFS.subClassOf.getLocalName());
//            }            
//            else if(!foundSubPropertyOf && t.matches(Node.ANY, RDFS.subPropertyOf.asNode(), Node.ANY)) {
//                foundSubPropertyOf = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+rdfs+RDFS.subPropertyOf.getLocalName());
//                ontSignature.add(rdfs+RDFS.subPropertyOf.getLocalName());
//            }
//            else if(!foundProperty && t.matches(Node.ANY, RDF.type.asNode(), Node.ANY)) {
//                foundProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+rdf+RDF.Property.getLocalName());
//                ontSignature.add(rdf+RDF.Property.getLocalName());
//            }
//            else if(!foundContainerMembershipProperty && t.matches(Node.ANY, RDF.type.asNode(), RDFS.ContainerMembershipProperty.asNode())) {
//                foundContainerMembershipProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+rdfs+RDFS.ContainerMembershipProperty.getLocalName());
//                ontSignature.add(rdfs+RDFS.ContainerMembershipProperty.getLocalName());
//            }
//            else if(!foundDatatype && t.matches(Node.ANY, RDF.type.asNode(), RDFS.Datatype.asNode())) {
//                foundDatatype = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+rdfs+RDFS.Datatype.getLocalName());
//                ontSignature.add(rdfs+RDFS.Datatype.getLocalName());
//            }
//            else if(model.containsResource(RDFS.Literal)) {
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+rdfs+RDFS.Literal.getLocalName());
//                ontSignature.add(rdfs+RDFS.Literal.getLocalName());
//            }
//            else if(!foundFuncitonalProperty && t.matches(Node.ANY, RDF.type.asNode(), OWL.FunctionalProperty.asNode())) {
//                foundFuncitonalProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.FunctionalProperty.getLocalName());
//                ontSignature.add(owl+OWL.FunctionalProperty.getLocalName());
//            }
//            else if(!foundInverseFunctionalProperty && t.matches(Node.ANY, RDF.type.asNode(), OWL.InverseFunctionalProperty.asNode())) {
//                foundInverseFunctionalProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.InverseFunctionalProperty.getLocalName());
//                ontSignature.add(owl+OWL.InverseFunctionalProperty.getLocalName());
//            }
//            else if(!foundSameAs && t.matches(Node.ANY, OWL.sameAs.asNode(), Node.ANY)) {
//                foundSameAs = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.sameAs.getLocalName());
//                ontSignature.add(owl+OWL.sameAs.getLocalName());
//            }
//            else if(!foundSymmetricProperty && t.matches(Node.ANY, RDF.type.asNode(), OWL.SymmetricProperty.asNode())) {
//                foundSymmetricProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.SymmetricProperty.getLocalName());
//                ontSignature.add(owl+OWL.SymmetricProperty.getLocalName());
//            }
//            else if(!foundTransitiveProperty && t.matches(Node.ANY, RDF.type.asNode(), OWL.TransitiveProperty.asNode())) {
//                foundTransitiveProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.TransitiveProperty.getLocalName());
//                ontSignature.add(owl+OWL.TransitiveProperty.getLocalName());
//            }
//            else if(!foundInverseOf && t.matches(Node.ANY, OWL.inverseOf.asNode(), Node.ANY)) {
//                foundInverseOf = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.inverseOf.getLocalName());
//                ontSignature.add(owl+OWL.inverseOf.getLocalName());
//            }
//            else if(!foundEquivalentClass && t.matches(Node.ANY, OWL.equivalentClass.asNode(), Node.ANY)) {
//                foundEquivalentClass = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.equivalentClass.getLocalName());
//                ontSignature.add(owl+OWL.equivalentClass.getLocalName());
//            }
//            else if(!foundEquivalentProperty && t.matches(Node.ANY, OWL.equivalentProperty.asNode(), Node.ANY)) {
//                foundEquivalentProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.equivalentProperty.getLocalName());
//                ontSignature.add(owl+OWL.equivalentProperty.getLocalName());
//            }
//            else if(!foundHasValue && t.matches(Node.ANY, OWL.hasValue.asNode(), Node.ANY)) {
//                foundHasValue = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.hasValue.getLocalName());
//                ontSignature.add(owl+OWL.hasValue.getLocalName());
//            }
//            else if(!foundOnProperty && t.matches(Node.ANY, OWL.onProperty.asNode(), Node.ANY)){
//                foundOnProperty = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.onProperty.getLocalName());
//                ontSignature.add(owl+OWL.onProperty.getLocalName());
//            }
//            else if(!foundSomeValuesFrom && t.matches(Node.ANY, OWL.someValuesFrom.asNode(), Node.ANY)){
//                foundSomeValuesFrom = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.someValuesFrom.getLocalName());
//                ontSignature.add(owl+OWL.someValuesFrom.getLocalName());
//            }
//            else if(!foundAllValuesFrom && t.matches(Node.ANY, OWL.allValuesFrom.asNode(), Node.ANY)){
//                foundAllValuesFrom = true;
//                System.err.println(" DEBUG (OwlRuleComposer::composeRules) : detect "+owl+OWL.allValuesFrom.getLocalName());
//                ontSignature.add(owl+OWL.allValuesFrom.getLocalName());
//            }
//            // if all constructs are found then break
//        }
        
        
        boolean hasNewConstructs = false;
        int s = ruleStatus.size();
        Set ruleSignaturesSet = ruleStatus.entrySet();
        do{
            hasNewConstructs = false;
            for(int i = 0; i < s; i++){
                Entry entry = (Entry)ruleSignaturesSet.get(i);
                if(entry.getValue().equals(Boolean.FALSE)){
                    RuleInfo rSig = (RuleInfo)entry.getKey();

                    // to check if all lhs constructs are included in ontology signature.
                    // to mark the corresponding rule signature as true if it is contained.
                    boolean containsAll = true;
                    List lhsConstructs = rSig.getLHSConstructs();
                    if(lhsConstructs != null){
                        for(int j = 0; j < lhsConstructs.size(); j++){
                            if(!ontSignature.contains(lhsConstructs.get(j))){
                                containsAll = false;
                                break;
                            }
                        }
                    }
                    if(containsAll){
                        ruleStatus.put(rSig, new Boolean(true));
                    }

                    // to handle rhs constructs
                    List rhsConstructs = rSig.getRHSConstructs();
                    if(rhsConstructs != null){
                        for(int j = 0; j < rhsConstructs.size(); j++){
                            Object rhsConstruct = rhsConstructs.get(j);
                            if(!ontSignature.contains(rhsConstruct)){
                                hasNewConstructs = true;
                                ontSignature.add(rhsConstruct);
                            }
                        }
                    }
                }
            } 
        }while(hasNewConstructs);
    }
    
    /**
     * read the rule-construct dependencies as indicated in the reasoner configuration
     * file and parse the dependencies.
     */
    private void readRuleConstructs(){
        try {
            BufferedReader ruleConstructsR = new BufferedReader(new FileReader(ReasonerConfig.ruleConstructsDependencies));
            String ruleString;
            while ((ruleString = ruleConstructsR.readLine()) != null) {
              
                // ignore comments as "//", next line "\n" and break "\t"
                if (ruleString.equals(commentsToken) || ruleString.equals("\t") 
                        || ruleString.equals("\n")) {
                    continue;
                }
                
                ruleStatus.put(getRuleSignature(ruleString), new Boolean(false));
            }
        } catch (IOException ex) {
            System.err.println("Cannot locate the rule construct dependency file");
        }
    }
    
    /**
     * Parse each line of the rule-construct dependency file into a RuleInfo object.
     * @param ruleString
     * @return 
     */
    private RuleInfo getRuleSignature(String ruleString){
        // rule name
//        System.err.println(" DEBUG (OwlRuleComposer::getRuleSignature): rule string is "+ruleString);
        int offset = ruleString.indexOf(":");
        RuleInfo sig = new RuleInfo(ruleString.substring(0, offset));
        ruleString = ruleString.substring(offset+1);
        
        // rule level
        offset = ruleString.indexOf(":");
        sig.setSemanticLevel(ruleString.substring(0, offset));
        ruleString = ruleString.substring(offset+1);
        
        // lhs constructs
        offset = ruleString.indexOf("]");
        int a = 0;
        String lhsConstructs = ruleString.substring(1, offset);
        if(!lhsConstructs.equals("")){
            while((a = lhsConstructs.indexOf(",")) != -1){
                sig.addLHSConstruct(lhsConstructs.substring(0, a));
                lhsConstructs = lhsConstructs.substring(a+1);
            }
            sig.addLHSConstruct(lhsConstructs);     // the last owl construct
        }
        
        // rhs constructs
        String rhsConstructs = ruleString.substring(offset+4, ruleString.length()-1);
        a = 0;
        if(!rhsConstructs.equals("")){
            while((a = rhsConstructs.indexOf(",")) != -1){
                sig.addRHSConstruct(rhsConstructs.substring(0, a));
                rhsConstructs = rhsConstructs.substring(a+1);
            }
            sig.addRHSConstruct(rhsConstructs);     // the last owl construct    
        }
        return sig;
    }
}
