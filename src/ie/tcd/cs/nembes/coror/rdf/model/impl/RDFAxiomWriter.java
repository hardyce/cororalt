/*
 * RDFAxiomWriter.java
 *
 * Created on 30 gennaio 2008, 18.44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.rdf.model.impl;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Axiom;
import ie.tcd.cs.nembes.coror.vocabulary.RDF;
import ie.tcd.cs.nembes.coror.vocabulary.RDFS;

/**
 *
 * @author ilBuccia
 */
public class RDFAxiomWriter {

    public static void writeAxioms(Graph graph) {
	Node rdfType = RDF.Nodes.type;
	Node rdfsResource = RDFS.Nodes.Resource;
	Node rdfsClass = RDFS.Nodes.Class;
	graph.add(new Axiom(rdfsClass, rdfType, rdfsClass ));
	graph.add(new Axiom(rdfsClass, RDFS.Nodes.subClassOf, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.label, RDFS.Nodes.range, RDFS.Nodes.Literal ));
	graph.add(new Axiom(rdfsResource, rdfType, rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.subject, rdfType, RDF.Nodes.Property ));
	graph.add(new Axiom(RDF.Nodes.subject , RDFS.Nodes.domain , RDF.Nodes.Statement  ));
	graph.add(new Axiom(RDF.Nodes.subject , RDFS.Nodes.subPropertyOf , RDF.Nodes.subject  ));
	graph.add(new Axiom(RDFS.Nodes.subClassOf , RDFS.Nodes.domain , rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.subClassOf , RDFS.Nodes.range , rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.Statement , rdfType, rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.Statement , RDFS.Nodes.subClassOf , rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.predicate , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDF.Nodes.predicate , RDFS.Nodes.domain , RDF.Nodes.Statement  ));
	graph.add(new Axiom(RDF.Nodes.predicate , RDFS.Nodes.subPropertyOf , RDF.Nodes.predicate  ));
	graph.add(new Axiom(RDFS.Nodes.subPropertyOf , RDFS.Nodes.domain , RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.subPropertyOf , RDFS.Nodes.range , RDF.Nodes.Property  ));
	graph.add(new Axiom(RDF.Nodes.object , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDF.Nodes.object , RDFS.Nodes.domain , RDF.Nodes.Statement  ));
	graph.add(new Axiom(RDF.Nodes.object , RDFS.Nodes.subPropertyOf , RDF.Nodes.object  ));
	graph.add(new Axiom(Node.createURI(RDF.getURI() + "XMLLiteral"), rdfType, RDFS.Nodes.Datatype  ));
	graph.add(new Axiom(rdfType, RDFS.Nodes.range , rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.rest , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDF.Nodes.rest , RDFS.Nodes.domain , RDF.Nodes.List  ));
	graph.add(new Axiom(RDF.Nodes.rest , RDFS.Nodes.range , RDF.Nodes.List  ));
	graph.add(new Axiom(RDF.Nodes.rest , RDFS.Nodes.subPropertyOf , RDF.Nodes.rest  ));
	graph.add(new Axiom(RDFS.Nodes.Literal , rdfType, rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.Literal , RDFS.Nodes.subClassOf , rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Property , rdfType, rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.Property , RDFS.Nodes.subClassOf , rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.List , rdfType, rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.List , RDFS.Nodes.subClassOf , rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.domain , RDFS.Nodes.domain , RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.domain , RDFS.Nodes.range , rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.first , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDF.Nodes.first , RDFS.Nodes.domain , RDF.Nodes.List  ));
	graph.add(new Axiom(RDF.Nodes.first , RDFS.Nodes.subPropertyOf , RDF.Nodes.first  ));
	graph.add(new Axiom(RDF.Nodes.nil , rdfType, RDF.Nodes.List  ));
	graph.add(new Axiom(RDFS.Nodes.range , RDFS.Nodes.domain , RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.range , RDFS.Nodes.range , rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.comment , RDFS.Nodes.range , RDFS.Nodes.Literal  ));
	graph.add(new Axiom(RDFS.Nodes.Literal , RDFS.Nodes.subClassOf , RDFS.Nodes.Literal  ));
	graph.add(new Axiom(RDF.Nodes.List , RDFS.Nodes.subClassOf , RDF.Nodes.List  ));
	graph.add(new Axiom(RDF.Nodes.Bag , RDFS.Nodes.subClassOf , RDF.Nodes.Bag  ));
	graph.add(new Axiom(RDF.Nodes.Bag , RDFS.Nodes.subClassOf , RDFS.Nodes.Container  ));
	graph.add(new Axiom(rdfsResource, RDFS.Nodes.subClassOf , rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Seq , RDFS.Nodes.subClassOf , RDF.Nodes.Seq  ));
	graph.add(new Axiom(RDF.Nodes.Seq , RDFS.Nodes.subClassOf , RDFS.Nodes.Container  ));
	graph.add(new Axiom(RDFS.Nodes.ContainerMembershipProperty , RDFS.Nodes.subClassOf , RDFS.Nodes.ContainerMembershipProperty  ));
	graph.add(new Axiom(RDFS.Nodes.ContainerMembershipProperty , RDFS.Nodes.subClassOf , RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.ContainerMembershipProperty , RDFS.Nodes.subClassOf , rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Statement , RDFS.Nodes.subClassOf , RDF.Nodes.Statement  ));
	graph.add(new Axiom(rdfsClass, RDFS.Nodes.subClassOf , rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.Alt , RDFS.Nodes.subClassOf , RDF.Nodes.Alt  ));
	graph.add(new Axiom(RDF.Nodes.Alt , RDFS.Nodes.subClassOf , RDFS.Nodes.Container  ));
	graph.add(new Axiom(RDFS.Nodes.Container , RDFS.Nodes.subClassOf , RDFS.Nodes.Container  ));
	graph.add(new Axiom(RDF.Nodes.Property , RDFS.Nodes.subClassOf , RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.Datatype , RDFS.Nodes.subClassOf , RDFS.Nodes.Datatype  ));
	graph.add(new Axiom(RDFS.Nodes.Datatype , RDFS.Nodes.subClassOf , rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.Datatype , RDFS.Nodes.subClassOf , rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.seeAlso , RDFS.Nodes.subPropertyOf , RDFS.Nodes.seeAlso  ));
	graph.add(new Axiom(RDFS.Nodes.isDefinedBy , RDFS.Nodes.subPropertyOf , RDFS.Nodes.isDefinedBy  ));
	graph.add(new Axiom(RDFS.Nodes.isDefinedBy , RDFS.Nodes.subPropertyOf , RDFS.Nodes.seeAlso  ));
	graph.add(new Axiom(RDF.Nodes.Bag , rdfType, rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.Container , rdfType, rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.Seq , rdfType, rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.ContainerMembershipProperty , rdfType, rdfsClass ));
	graph.add(new Axiom(RDF.Nodes.Alt , rdfType, rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.Datatype , rdfType, rdfsClass ));
	graph.add(new Axiom(RDFS.Nodes.seeAlso , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.isDefinedBy , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.comment , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.range , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.domain , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(rdfType, rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.subPropertyOf , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.subClassOf , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(RDFS.Nodes.label , rdfType, RDF.Nodes.Property  ));
	graph.add(new Axiom(Node.createURI(RDF.getURI() + "XMLLiteral"), rdfType, rdfsResource ));
	graph.add(new Axiom(Node.createURI(RDF.getURI() + "XMLLiteral"), rdfType, rdfsClass ));
	graph.add(new Axiom(rdfsResource, rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Statement , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Property , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.Literal , rdfType, rdfsResource ));
	graph.add(new Axiom(rdfsClass, rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.List , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Bag , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.Container , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Seq , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.ContainerMembershipProperty , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.Alt , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.Datatype , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.first , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.rest , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.object , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.predicate , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.subject , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.seeAlso , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.isDefinedBy , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.comment , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.range , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.domain , rdfType, rdfsResource ));
	graph.add(new Axiom(rdfType, rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.subPropertyOf , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.subClassOf , rdfType, rdfsResource ));
	graph.add(new Axiom(RDFS.Nodes.label , rdfType, rdfsResource ));
	graph.add(new Axiom(RDF.Nodes.nil, rdfType, rdfsResource ));		
    }
}
