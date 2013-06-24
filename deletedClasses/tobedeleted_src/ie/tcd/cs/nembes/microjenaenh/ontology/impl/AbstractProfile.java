/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            02-Apr-2003
 * Filename           $RCSfile: AbstractProfile.java,v $
 * Revision           $Revision: 1.9 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2007/01/02 11:49:49 $
 *               by   $Author: andy_seaborne $
 *
 * (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

package ie.tcd.cs.nembes.microjenaenh.ontology.impl;

import ie.tcd.cs.nembes.microjenaenh.enhanced.EnhGraph;
import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.ontology.Profile;
import ie.tcd.cs.nembes.microjenaenh.rdf.model.Property;
import ie.tcd.cs.nembes.microjenaenh.util.Iterator;
import ie.tcd.cs.nembes.microjenaenh.util.Set;

/**
 * <p>
 * Abstract base class to provide shared implementation for ontology language profiles.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: AbstractProfile.java,v 1.9 2007/01/02 11:49:49 andy_seaborne Exp $
 */
public abstract class AbstractProfile implements Profile {
    // this class is almost empty. It has not been removed to keep Jena
    // class hierarchy

    public static boolean containsSome( EnhGraph g, Node n, Property p )  {
	return g.asGraph().contains( n, p.asNode(), Node.ANY );
    }
    
    protected Iterator arrayToIterator(Object[] array) {
	Set result = new Set();
	for(int i = 0; i<array.length; i++) {
	    result.add(array[i]);
	}
	return result.iterator();
    }

}


/*
    (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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

