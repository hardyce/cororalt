/*
 * OWLAxiomWriter.java
 *
 * Created on 30 gennaio 2008, 18.48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.ontology.impl;

import ie.tcd.cs.nembes.microjenaenh.graph.Axiom;
import ie.tcd.cs.nembes.microjenaenh.graph.Graph;
import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.vocabulary.OWL;
import ie.tcd.cs.nembes.microjenaenh.vocabulary.RDF;
import ie.tcd.cs.nembes.microjenaenh.vocabulary.RDFS;

/**
 *
 * @author ilBuccia
 */
public class OWLAxiomWriter {
    
    private static Node rdfType = RDF.type.asNode();
    private static Node rdfsResource = RDFS.Resource.asNode();
    private static Node rdfsClass = RDFS.Class.asNode();

    
    public static void writeAxioms(Graph graph) {
	writeClassResourceAxioms(OWL.Class.asNode(), graph);
	writeClassResourceAxioms(OWL.Restriction.asNode(), graph);
	writeClassResourceAxioms(OWL.ObjectProperty.asNode(), graph);
	writeClassResourceAxioms(OWL.SymmetricProperty.asNode(), graph);
	writeClassResourceAxioms(OWL.TransitiveProperty.asNode(), graph);
	writeClassResourceAxioms(OWL.InverseFunctionalProperty.asNode(), graph);
	writeClassResourceAxioms(OWL.AnnotationProperty.asNode(), graph);
	writeClassResourceAxioms(OWL.FunctionalProperty.asNode(), graph);
	writeClassResourceAxioms(OWL.AllDifferent.asNode(), graph);
	writeClassResourceAxioms(OWL.DataRange.asNode(), graph);
	writeClassResourceAxioms(OWL.Ontology.asNode(), graph);
	writeClassResourceAxioms(OWL.DatatypeProperty.asNode(), graph);	
    }
    
    private static void writeClassResourceAxioms(Node n, Graph graph) {
	graph.add(new Axiom(n, rdfType, rdfsResource));
	graph.add(new Axiom(n, rdfType, rdfsClass));
    }

}
