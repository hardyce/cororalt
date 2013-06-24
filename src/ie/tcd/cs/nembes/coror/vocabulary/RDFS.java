

package ie.tcd.cs.nembes.coror.vocabulary;

import ie.tcd.cs.nembes.coror.graph.Node;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.Property;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.Resource;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.ResourceFactory;

/**
 * RDFS vocabulary items
 * @author  bwm, updated by kers/daniel/christopher
 * @version $Id: RDFS.java,v 1.16 2007/01/02 11:49:32 andy_seaborne Exp $
 */
public class RDFS {
    
    protected static final String uri="http://www.w3.org/2000/01/rdf-schema#";
    
//    protected static final Resource resource( String local ) {
//	return ResourceFactory.createResource( uri + local );
//    }
//    
//    protected static final Property property( String local ) {
//	return ResourceFactory.createProperty( uri, local );
//    }
//    
//    public static final Resource Class = resource( "Class");
//    public static final Resource Datatype = resource( "Datatype");
//    
//    /**
//     * @deprecated obsolete: was removed by the most recent standard
//     */
//    public static final Resource ConstraintProperty  =  resource( "ConstraintProperty");
//    
//    public static final Resource Container  = resource( "Container");
//    
//    public static final Resource ContainerMembershipProperty = resource( "ContainerMembershipProperty");
//    
//    /**
//     * @deprecated obsolete: was removed by the most recent standard
//     */
//    public static final Resource ConstraintResource  = resource( "ConstraintResource");
//    
//    public static final Resource Literal = resource( "Literal");
//    public static final Resource Resource = resource( "Resource");
//    
//    public static final Property comment = property( "comment");
//    public static final Property domain = property( "domain");
//    public static final Property label = property( "label");
//    public static final Property isDefinedBy = property( "isDefinedBy");
//    public static final Property range = property( "range");
//    public static final Property seeAlso = property( "seeAlso");
//    public static final Property subClassOf  = property( "subClassOf");
//    public static final Property subPropertyOf  = property( "subPropertyOf");
//    public static final Property member  = property( "member");
    
    /**
     * The RDFS vocabulary, expressed for the SPI layer in terms of .graph Nodes.
     */
    public static class Nodes {
	public static final Node Class = Node.createURI(uri+"Class");
	public static final Node Datatype = Node.createURI(uri+"Datatype");
	public static final Node ConstraintProperty  = Node.createURI(uri+"ConstraintProperty");
	public static final Node Container  = Node.createURI(uri+"Container");
	public static final Node ContainerMembershipProperty = Node.createURI(uri+"ContainerMembershipProperty");
	public static final Node Literal = Node.createURI(uri+"Literal");
	public static final Node Resource = Node.createURI(uri+"Resource");
	public static final Node comment = Node.createURI(uri+"comment");
	public static final Node domain = Node.createURI(uri+"domain");
	public static final Node label = Node.createURI(uri+"label");
	public static final Node isDefinedBy = Node.createURI(uri+"isDefinedBy");
	public static final Node range = Node.createURI(uri+"range");
	public static final Node seeAlso = Node.createURI(uri+"seeAlso");
	public static final Node subClassOf  = Node.createURI(uri+"subClassOf");
	public static final Node subPropertyOf  = Node.createURI(uri+"subPropertyOf");
	public static final Node member  = Node.createURI(uri+"member");
    }
    
    /**
     * returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
	return uri;
    }
}

/*
 *  (c) Copyright 2000, 2001, 2002, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * RDFS.java
 *
 * Created on 28 July 2000, 18:13
 */