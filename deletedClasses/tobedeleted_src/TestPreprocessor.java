/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.test;

import ie.tcd.cs.nembes.microjenaenh.graph.Graph;
import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.graph.Node_Literal;
import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import ie.tcd.cs.nembes.microjenaenh.reasoner.Finder;
import ie.tcd.cs.nembes.microjenaenh.reasoner.TriplePattern;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.BasicForwardRuleInfGraph;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.RulePreprocessHook;
import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.selective.LiteralsStore;
import ie.tcd.cs.nembes.microjenaenh.util.iterator.ExtendedIterator;
import ie.tcd.cs.nembes.microjenaenh.vocabulary.RDF;
import ie.tcd.cs.nembes.microjenaenh.vocabulary.RDFS;

/**
 *
 * @author Wei Tai
 */
public class TestPreprocessor_deprecated implements RulePreprocessHook{

    public void run(BasicForwardRuleInfGraph infGraph, Finder dataFind, Graph inserts) {
        ExtendedIterator it = dataFind.find(new TriplePattern(null, null, null));
        for(;it.hasNext();){
            Triple triple = (Triple)it.next();
            Node object = triple.getObject();
            if(object.isLiteral()){
                // Applying lg
                Node_Literal lObject = (Node_Literal)object;                
                Node anon = LiteralsStore.assignAnon(lObject);
                inserts.add(new Triple(triple.getSubject(), triple.getPredicate(), anon));
                
                
                String datatype = lObject.getLiteralDatatypeURI();
                if(datatype != null)
                    // Applying rdf2-D
                    inserts.add(new Triple(anon, RDF.type.asNode(), Node.createURI(datatype)));
                else
                    // Applying rdfs1
                    inserts.add(new Triple(anon, RDF.type.asNode(), RDFS.Literal.asNode()));
            }
            
        }
         
    }

    public boolean needsRerun(BasicForwardRuleInfGraph infGraph, Triple t) {
        return t.getObject().isLiteral();
    }

}
