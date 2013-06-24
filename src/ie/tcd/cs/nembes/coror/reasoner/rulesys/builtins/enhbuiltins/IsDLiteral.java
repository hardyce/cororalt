/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins;

import ie.tcd.cs.nembes.coror.datatypes.RDFDatatype;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BindingEnvironment;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * To check whether the given node is a typed literal. If it is then return true
 * otherwise false. A second argument can be supplied which will be bound to the
 * datatype. It has to be a new variable that never been bound before.
 * @author Wei Tai
 */
public class IsDLiteral extends BaseBuiltin{

    public String getName() {
        return "isDLiteral";
    }   
    
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        Node n0 = getArg(0, args, context);
        BindingEnvironment env = context.getEnv();
        if(n0.isLiteral()){
            String dtURI = n0.getLiteralDatatypeURI();
            if(dtURI == null)
                // if n0 is plain literal
                return false;
            else{
                // if n0 is typed literal
                if(length == 2){
                    // there is another argument for binding the datatype URI
                    env.bind(args[1], Node.createURI(dtURI));
                    return true;
                }
                return true;
            }                
        }
        else
            // if n0 is not literal
            return false;
    }    

}
