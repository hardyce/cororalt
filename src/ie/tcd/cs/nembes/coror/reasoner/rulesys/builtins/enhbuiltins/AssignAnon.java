/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BindingEnvironment;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.BaseBuiltin;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.LiteralsStore;

/**
 *
 * @author Wei Tai
 */
public class AssignAnon extends BaseBuiltin{

    public String getName() {
        return "assignAnon";
    }
    
    public int getArgLength(){
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
        BindingEnvironment env = context.getEnv();
        if(n0.isLiteral()){
            return env.bind(args[1], LiteralsStore.assignAnon(n0));
        }
        return false;   
    }

}
