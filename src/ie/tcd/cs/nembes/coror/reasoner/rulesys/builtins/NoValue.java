/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BuiltinException;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;

/**
 *
 * @author Wei Tai
 */
public class NoValue extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "noValue";
    }
    
    /**
     * This method is invoked when the builtin is called in a rule body.
     * @param args the array of argument values for the builtin, this is an array 
     * of Nodes, some of which may be Node_RuleVariables.
     * @param length the length of the argument list, may be less than the length of the args array
     * for some rule engines
     * @param context an execution context giving access to other relevant data
     * @return return true if the buildin predicate is deemed to have succeeded in
     * the current environment
     */
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        if (length !=2 && length != 3) {
            throw new BuiltinException(this, context, "builtin " + getName() + " requires 2 or 3 arguments but saw " + length);
        }
        Node obj = null;
        if (length == 3) {
            obj = getArg(2, args, context);
        }
        Node subj = getArg(0, args, context);
        // Allow variables in subject position to correspond to wild cards
        if (subj.isVariable()) {
            subj = null;
        }
        Node pred = getArg(1, args, context);
        if (pred.isVariable()) {
            pred = null;
        }
        return !context.contains(subj, pred, obj);
    }
    
    /**
     * Flag as non-monotonic so the guard clause will get rerun after deferal
     * as part of a non-trivial conflict set.
     */
    public boolean isMonotonic() {
        return false;
    }
    
}
