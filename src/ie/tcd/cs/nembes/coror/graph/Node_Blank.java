/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: Node_Blank.java,v 1.10 2007/01/02 11:49:18 andy_seaborne Exp $
*/

package ie.tcd.cs.nembes.coror.graph;

//import ie.tcd.cs.nembes.microjenaenh.rdf.model.AnonId;

import ie.tcd.cs.nembes.coror.rdf.model.AnonId;


/**
    RDF blank nodes, ie nodes with identity but without URIs.
	@author kers
*/

public class Node_Blank extends Node_Concrete {    
    
    /* package */ Node_Blank( Object id ) {
	super( id );
    }

    public boolean isBlank() { return true; }

    public AnonId getBlankNodeId()  { return (AnonId) label; }
        
    public boolean matches(Node other) {
	if(other instanceof Node_ANY)
	    return true;
	else
	    return this.equals(other);
    }

    public boolean equals( Object other ) {
        if(this == other) return true;
        if(this.hashCode() != other.hashCode()) return false;
	return other instanceof Node_Blank && label.equals( ((Node_Blank) other).label );
    }
}

/*
    (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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