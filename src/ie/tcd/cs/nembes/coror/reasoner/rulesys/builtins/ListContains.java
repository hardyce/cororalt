/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Util;
import ie.tcd.cs.nembes.coror.vocabulary.RDF;

/**
 * Returns true if the first argument is a list which contains the second argument.
 * Can't be used as a generator.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.9 $ on $Date: 2008/01/02 12:06:21 $
 */
public class ListContains extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "listContains";
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
        System.out.print("{"+n0+", "+n1+"}");
        boolean ret = listContains(n0, n1, context);
        System.out.println(" "+ret);
//      return listContains(n0, n1, context);
        return ret;
    }
    
    /**
     * Return true if the first argument is a list which contains
     * the second argument.
     */
    protected static boolean listContains(Node list, Node element, RuleContext context ) {
         if (list == null || list.equals(RDF.Nodes.nil)) {
             return false;
         } else {
             Node elt = Util.getPropValue(list, RDF.Nodes.first, context);
             if (elt.sameValueAs(element)) {
                 return true;
             } else {
                 Node next = Util.getPropValue(list, RDF.Nodes.rest, context);
                 return listContains(next, element, context);
             }
         }
    }
}
