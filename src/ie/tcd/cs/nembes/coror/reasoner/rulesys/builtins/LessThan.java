/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Util;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.LiteralsStore;

/**
 * Less than. This version only supports the comparison of numeric value 
 * and removes the support of xsd:instance. The comparison of anonnymous
 * nodes assigned to literals is also supported.
 * @author Wei Tai
 */
public class LessThan extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "lessThan";
    }
    
    /**
     * Return the expected number of arguments for this functor or 0 if the number is flexible.
     */
    public int getArgLength() {
        return 2;
    }

    /**
     * This method is invoked when the builtin is called in a rule body.
     * @param args the array of argument values for the builtin, this is an array 
     * of Nodes, some of which may be Node_RuleVariables.
     * @param context an execution context giving access to other relevant data
     * @return return true if the buildin predicate is deemed to have succeeded in
     * the current environment
     */
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        Node n0 = getArg(0, args, context);
        Node n1 = getArg(1, args, context);
        
        if(n0.isBlank())
            n0 = LiteralsStore.getLiteral(n0);
        if(n1.isBlank())
            n1 = LiteralsStore.getLiteral(n1);
        if(n0 == null || n1 == null){
            return false;
        }
       
        if ( Util.isNumeric(n0) && Util.isNumeric(n1) ) {
            return Util.compareNumbers(n0, n1) < 0;
        } 
        return false;
    }
    
}
