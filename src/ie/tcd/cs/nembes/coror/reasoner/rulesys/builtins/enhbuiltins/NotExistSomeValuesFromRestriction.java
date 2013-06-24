/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.enhbuiltins;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RuleContext;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.builtins.BaseBuiltin;
import ie.tcd.cs.nembes.coror.vocabulary.RDF;

/**
 *
 * @author Wei Tai
 */
public class NotExistSomeValuesFromRestriction  extends BaseBuiltin{

    public String getName() {
        return "notExistSomeValuesFromRestriction";
    }

    public int getArgLength() {
        return 3;
    }
    
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        Node n0 = getArg(0, args, context);
        Node n1 = getArg(1, args, context);
        Node n2 = getArg(2, args, context);
        
        Triple t2 = new Triple(Node.ANY, RDF.Nodes.type, n2);
        Triple t1 = new Triple(n0, n1, Node.ANY);
        
        if(context.contains(t2)){
            if(context.contains(t1))
                return false;
        }
        return true;
        
    }     
    
}
