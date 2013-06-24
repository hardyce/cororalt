/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Util;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.UnsupportedOperationException;

/**
 * Test if the two argument lists contain the same semantic elements.
 * 
 * @author Wei Tai
 */
public class ListEqual extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "listEqual";
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
     * @param length the length of the argument list, may be less than the length of the args array
     * for some rule engines
     * @param context an execution context giving access to other relevant data
     * @return return true if the buildin predicate is deemed to have succeeded in
     * the current environment
     */
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        Node n0 = getArg(0, args, context);
        Node n1 = getArg(1, args, context);
        return listEqual(n0, n1, context);
    }
    
    /**
     * Test two RDF lists for semantic equality. Expensive.
     */
    protected static boolean listEqual(Node list1, Node list2, RuleContext context ) {
        List elts1 = Util.convertList(list1, context);
        List elts2 = Util.convertList(list2, context);
        if (elts1.size() != elts2.size()) return false;
        for (Iterator i = elts1.iterator(); i.hasNext(); ) {
            Node elt = (Node)i.next();
            boolean matched = false;
            for (Iterator j = elts2.iterator(); j.hasNext(); ) {
                Node elt2 = (Node)j.next();
                if (elt.sameValueAs(elt2)) {
                    try {
                        // Found match, consume it
                        j.remove();
                    } catch (UnsupportedOperationException ex) {
                        ex.printStackTrace();
                    }
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }
}
