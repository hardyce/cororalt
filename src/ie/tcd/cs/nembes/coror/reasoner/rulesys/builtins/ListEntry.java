/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BindingEnvironment;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Util;
import ie.tcd.cs.nembes.coror.vocabulary.RDF;

/**
 * listEntry(?list, ?index, ?val) will bind ?val to the ?index'th entry
 * in the RDF list ?list. If there is no such entry the variable will be unbound
 * and the call will fail. Only useable in rule bodies.
 * 
 * @author Wei Tai
 */
public class ListEntry extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "listEntry";
    }
    
    /**
     * Return the expected number of arguments for this functor or 0 if the number is flexible.
     */
    public int getArgLength() {
        return 3;
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
        BindingEnvironment env = context.getEnv();
        Node list = getArg(0, args, context);
        Node index = getArg(1, args, context);
        if ( ! Util.isNumeric(index) )  return false;
        Node elt = getEntry(list, Util.getIntValue(index), context);
        if (elt == null) {
            return false;
        } else {
            env.bind(args[2], elt);
            return true;
        }
    }
    
    /**
     * Return the i'th element of the list, starting from 0
     * @param list the start of the list
     * @param i the element to return
     * @param context the context through which the data values can be found
     * @return the entry or null if the there isn't such an entry
     */
    protected static Node getEntry(Node list, int i, RuleContext context ) {
        int count = 0;
        Node node = list;
        while (node != null && !node.equals(RDF.Nodes.nil)) {
            if (count == i) {
                return Util.getPropValue(node, RDF.Nodes.first, context);
            } else {
                node = Util.getPropValue(node, RDF.Nodes.rest, context);
                count++;
            }
        }
        return null;
    }
    
}
