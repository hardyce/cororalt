/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Util;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.vocabulary.OWL;

/**
 *
 * @author Wei Tai
 */
public class AssertDisjointPairs extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "assertDisjointPairs";
    }
    
    /**
     * Return the expected number of arguments for this functor or 0 if the number is flexible.
     */
    public int getArgLength() {
        return 1;
    }
    
    /**
     * This method is invoked when the builtin is called in a rule head.
     * Such a use is only valid in a forward rule.
     * @param args the array of argument values for the builtin, this is an array 
     * of Nodes.
     * @param length the length of the argument list, may be less than the length of the args array
     * for some rule engines
     * @param context an execution context giving access to other relevant data
     */
    public void headAction(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        List l = Util.convertList(args[0], context);
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Node x = (Node)i.next();
            for (Iterator j = l.iterator(); j.hasNext(); ) {
                Node y = (Node)j.next();
                if (!x.sameValueAs(y)) {
                    context.add( new Triple(x, OWL.Nodes.differentFrom, y) );
                }
            }
        }
    }
    
}
