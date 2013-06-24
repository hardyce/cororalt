/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * To check whether the given node is a plain literal. If it is then return true
 * otherwise false. 
 * @author Wei Tai
 */
public class IsPLiteral extends BaseBuiltin{

    public String getName() {
        return "isPLiteral";
    }
    
    /**
     * Return the expected number of arguments for this functor or 0 if the number is flexible.
     */
    public int getArgLength() {
        return 1;
    }
    
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        Node n0 = getArg(0, args, context);
        
        if(n0.isLiteral()){
            String dtURI = n0.getLiteralDatatypeURI();
            if(dtURI == null)
                // if n0 is plain literal
                return true;
            else
                // if n0 is typed literal
                return false;                
        }
        else
            // if n0 is not literal
            return false;
    } 
}
