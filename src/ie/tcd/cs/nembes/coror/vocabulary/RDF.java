/*
    (c)  Copyright 2000, 2001, 2002, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
    [See end of file]
    $Id: RDF.java,v 1.13 2007/01/02 11:49:32 andy_seaborne Exp $
*/

package ie.tcd.cs.nembes.coror.vocabulary;

import ie.tcd.cs.nembes.coror.graph.Node;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.Property;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.Resource;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.ResourceFactory;

/**
    The standard RDF vocabulary.
    @author  bwm; updated by kers/daniel/christopher
    @version $Id: RDF.java,v 1.13 2007/01/02 11:49:32 andy_seaborne Exp $
*/

public class RDF{

    protected static final String uri ="http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /** returns the URI for this schema
        @return the URI for this schema
    */
    public static String getURI()
        { return uri; }

    
    
//    protected static final Resource resource( String local )
//        { return ResourceFactory.createResource( uri + local ); }
//
//    protected static final Property property( String local )
//        { return ResourceFactory.createProperty( uri, local ); }
//
//    public static Property li( int i )
//        { return property( "_" + i ); }

//    public static final Resource Alt = resource( "Alt" );
//    public static final Resource Bag = resource( "Bag" );
//    public static final Resource Property = resource( "Property" );
//    public static final Resource Seq = resource( "Seq" );
//    public static final Resource Statement = resource( "Statement" );
//    public static final Resource List = resource( "List" );
//    public static final Resource nil = resource( "nil" );
//
//    public static final Property first = property( "first" );
//    public static final Property rest = property( "rest" );
//    public static final Property subject = property( "subject" );
//    public static final Property predicate = property( "predicate" );
//    public static final Property object = property( "object" );
//    public static final Property type = property( "type" );
//    public static final Property value = property( "value" );

    /**
        The same items of vocabulary, but at the Node level, parked inside a
        nested class so that there's a simple way to refer to them.
    */
    public static final class Nodes {
    
	public static final Node Alt = Node.createURI(uri+"Alt");
        public static final Node Bag = Node.createURI(uri+"Bag");
        public static final Node Property = Node.createURI(uri+"Property");
        public static final Node Seq = Node.createURI(uri+"Seq");
        public static final Node Statement = Node.createURI(uri+"Statement");
        public static final Node List = Node.createURI(uri+"List");
        public static final Node nil = Node.createURI(uri+"nil");
        public static final Node first = Node.createURI(uri+"first");
        public static final Node rest = Node.createURI(uri+"rest");
        public static final Node subject = Node.createURI(uri+"subject");
        public static final Node predicate = Node.createURI(uri+"predicate");
        public static final Node object = Node.createURI(uri+"object");
        public static final Node type = Node.createURI(uri+"type");
        public static final Node value = Node.createURI(uri+"value");
    }

}

/*
 *  (c)   Copyright 2000, 2001, 2002, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 *   All rights reserved.
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
 * RDF.java
 *
 * Created on 28 July 2000, 18:12
 */
