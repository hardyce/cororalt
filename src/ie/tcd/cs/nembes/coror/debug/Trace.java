/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.debug;

import ie.tcd.cs.nembes.coror.reasoner.rulesys.Rule;
import ie.tcd.cs.nembes.coror.util.List;

/**
 *
 * @author WEI TAI
 */
public class Trace {
    String node;
    Pair join;
    
    public Trace(String node, Pair join){
        this.node = node;
        this.join = join;
    }
    
    public String toString(){
        if(join == null)
            return "{" + node + "}";
        else
            return "{" + node + ":" + join.toString() + "}";
    }
}

class Pair{
        Object A;
        Object B;
        
        public Pair(Object a, Object b){
            A = a;
            B = b;
        }
        
        public String toString(){
            if(A == null)
                return "("+B+")";
            else if(B == null)
                return "("+A+")";
            else
                return "("+A+", "+B+")";
        }
    }