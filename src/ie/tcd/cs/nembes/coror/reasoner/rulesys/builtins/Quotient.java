/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BindingEnvironment;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Util;
import ie.tcd.cs.nembes.coror.util.NumberUtil;

/**
 * Get the product of two (numeric) nodes, i.e. node1 / node2, and bind the 
 * result to the third nodes.
 * @author Wei Tai
 */
public class Quotient extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    public String getName() {
        return "quotient";
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
        Node n1 = getArg(0, args, context);
        Node n2 = getArg(1, args, context);
        if (n1.isLiteral() && n2.isLiteral()) {
            Object v1 = n1.getLiteralValue();
            Object v2 = n2.getLiteralValue();
            Node quo = null;
            if (NumberUtil.isNumber(v1) && NumberUtil.isNumber(v2)) {                
                if (v1 instanceof Float || v1 instanceof Double 
                ||  v2 instanceof Float || v2 instanceof Double) {
                    double dv1 = Double.parseDouble(v1.toString());
                    double dv2 = Double.parseDouble(v2.toString());
                    quo = Util.makeDoubleNode(dv1 / dv2);
                } else {
                    long lv1 = Long.parseLong(v1.toString());
                    long lv2 = Long.parseLong(v2.toString());
                    quo = Util.makeLongNode(lv1 / lv2);
                }
                return env.bind(args[2], quo);
            }
        }
        // Doesn't (yet) handle partially bound cases
        return false;
    }
    
}
