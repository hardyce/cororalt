package ie.tcd.cs.nembes.coror.reasoner.rulesys;


import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.AssertDisjointPairs;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Difference;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Equal;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.GE;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.GreaterThan;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.IsFunctor;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.IsLiteral;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.LE;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.LessThan;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListContains;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListEntry;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListEqual;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListLength;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListMapAsObject;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListMapAsSubject;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListNotContains;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.ListNotEqual;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.MakeTemp;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Max;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Min;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.NoValue;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.NotEqual;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.NotFunctor;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.NotLiteral;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Print;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Product;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Quotient;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.Sum;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins.AssignAnon;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins.IsDLiteral;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins.IsPLiteral;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins.NotExistSomeValuesFromRestriction;
import ie.tcd.cs.nembes.coror.util.Map;

/******************************************************************
 * File:        BuildinRegistry.java
 * Created by:  Dave Reynolds
 * Created on:  11-Apr-2003
 * 
 * (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: BuiltinRegistry.java,v 1.26 2008/01/02 12:07:46 andy_seaborne Exp $
 *****************************************************************/


/** * A registry for mapping functor names on java objects (instances 
 * of subclasses of Builtin) which implement their behvaiour.
 * <p>
 * This is currently implemented as a singleton to simply any future
 * move to support different sets of builtins.
 * 
 * @see Builtin * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a> * @version $Revision: 1.26 $ on $Date: 2008/01/02 12:07:46 $ */
public class BuiltinRegistry {

    /** The single global static registry */
    public static BuiltinRegistry theRegistry;
    
    /** Mapping from functor name to Builtin implementing it */
    protected Map builtins = new Map();
    
    /** Mapping from URI of builtin to implementation */
    protected Map builtinsByURI = new Map();
    
    // Static initilizer for the singleton instance
    static {
        theRegistry = new BuiltinRegistry();
        
        theRegistry.register(new Print());
//        theRegistry.register(new AddOne());
        theRegistry.register(new LessThan());
        theRegistry.register(new GreaterThan());
        theRegistry.register(new LE());
        theRegistry.register(new GE());
        theRegistry.register(new Equal());
        theRegistry.register(new NotFunctor());
        theRegistry.register(new IsFunctor());
        theRegistry.register(new NotEqual());
        theRegistry.register(new MakeTemp());
        theRegistry.register(new NoValue());
//        theRegistry.register(new Remove());
//        theRegistry.register(new Drop());
        theRegistry.register(new Sum());
        theRegistry.register(new Difference());
        theRegistry.register(new Product());
        theRegistry.register(new Quotient());
//        theRegistry.register(new Bound());
//        theRegistry.register(new Unbound());
        theRegistry.register(new IsLiteral());
        theRegistry.register(new NotLiteral());
//        theRegistry.register(new IsBNode());
//        theRegistry.register(new NotBNode());
//        theRegistry.register(new IsDType());
//        theRegistry.register(new NotDType());
//        theRegistry.register(new CountLiteralValues());
        theRegistry.register(new Max());
        theRegistry.register(new Min());
        theRegistry.register(new ListLength());
        theRegistry.register(new ListEntry());
        theRegistry.register(new ListEqual());
        theRegistry.register(new ListNotEqual());
        theRegistry.register(new ListContains());
        theRegistry.register(new ListNotContains());
        theRegistry.register(new ListMapAsSubject());
        theRegistry.register(new ListMapAsObject());
//        
//        theRegistry.register(new MakeInstance());
//        theRegistry.register(new Table());
//        theRegistry.register(new TableAll());
//        
//        theRegistry.register(new Hide());
//        
//        theRegistry.register(new StrConcat());
//        theRegistry.register(new UriConcat());
//        theRegistry.register(new Regex());
//        
//        theRegistry.register(new Now());
//        
//        // Special purposes support functions for OWL
        theRegistry.register(new AssertDisjointPairs());
//        
//        // Wei Tai customized builtins
        theRegistry.register(new AssignAnon());
        theRegistry.register(new IsDLiteral());
        theRegistry.register(new IsPLiteral());
        theRegistry.register(new NotExistSomeValuesFromRestriction());
        
    }
    
    /**
     * Construct an empty registry
     */
    public BuiltinRegistry() {
    }
    
    /**
     * Register an implementation for a given builtin functor.
     * @param functor the name of the functor used to invoke the builtin
     * @param impl the implementation of the builtin
     */
    public void register(String functor, Builtin impl) {
        builtins.put(functor, impl);
        builtinsByURI.put(impl.getURI(), impl);
    }
   
    /**
     * Register an implementation for a given builtin using its default name.
     * @param impl the implementation of the builtin
     */
    public void register(Builtin impl) {
        builtins.put(impl.getName(), impl);
        builtinsByURI.put(impl.getURI(), impl);
    }
    
    /**
     * Find the implementation of the given builtin functor.
     * @param functor the name of the functor being invoked.
     * @return a Builtin or null if there is none registered under that name
     */
    public Builtin getImplementation(String functor) {
        return (Builtin)builtins.get(functor);
    }
    
    /**
     * Find the implementation of the given builtin functor.
     * @param uri the URI of the builtin to be retrieved
     * @return a Builtin or null if there is none registered under that name
     */
    public Builtin getImplementationByURI(String uri) {
        return (Builtin)builtinsByURI.get(uri);
    }
    
}

/*
    (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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

