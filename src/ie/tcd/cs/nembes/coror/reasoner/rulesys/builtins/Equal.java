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
 * Compare the equality of two values. The two opponants need not to be only
 * numbers. Nodes of other type (e.g. two URIs) can also be as opponants.
 * @author Wei Tai
 */
public class Equal extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "equal";
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
        
        if(!Util.isNumeric(n0) && !Util.isNumeric(n1) && !n0.isBlank() && !n1.isBlank())
            // both n0 and n1 are two URIs or non-number literals or their combinations
            return n0.sameValueAs(n1);
        else if(n0.isBlank() || n1.isBlank()){
            
            // to check whether n0 or n1 is a anonnymous node assigned to literal
            if(n0.isBlank())
                n0 = LiteralsStore.getLiteral(n0);
            if(n1.isBlank())
                n1 = LiteralsStore.getLiteral(n1);
            
            if(n0 == null || n1 == null){
                // at least one of n0 or n1 is normal anonnymous node
                return n0.sameValueAs(n1);
            }
            else if ( Util.isNumeric(n0) && Util.isNumeric(n1) ) 
                // n0 or n1 are two anonnymous nodes assigned to numbers
                 return Util.compareNumbers(n0, n1) == 0;
            else
                // one of n1 or n0 is anonnymous nodes assigned two non-number literals
                return n0.sameValueAs(n1);
        }
        else if(Util.isNumeric(n0) && Util.isNumeric(n1)){
            // n0 and n1 are two numbers
            return Util.compareNumbers(n0, n1) == 0;
        }
        else 
            // other cases
            return n0.sameValueAs(n1);
    }
    
}
