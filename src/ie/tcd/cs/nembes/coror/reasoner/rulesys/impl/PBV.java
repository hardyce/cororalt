/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Node_ANY;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.reasoner.TriplePattern;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.BindingEnvironment;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Functor;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Node_RuleVariable;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.CororException;
import ie.tcd.cs.nembes.coror.util.List;

/**
 * A partial binding vector.
 * 
 * @author WEI TAI
 */
public class PBV {
    
    /** A partial binding vector. The order of binding in a full instantiation is the same as the sequence variables appear in rule */
    protected Node[] pEnvironment;
    
    /** Hash code */
    int hash;
    
    /** 
     * The current size of the partial binding vector. Used as an indicator for
     * the next position in the partial binding vector to be bound.
     */
//    byte size;
    
    /** 
     * An indicator for if the partial binding vector is a full instantiation 
     * of all the triple patterns 
     */
//    boolean fullInstantiation = false;
    
    /**
     * Construct a new empty partial binding vector of a specified size
     * @param num 
     */
    protected PBV(int num){
        pEnvironment = new Node[num];
    }

    /**
     * Construct a partial binding vector with an existing pEnvironment
     * @param pEnvironment 
     */
    protected PBV(Node[] environment){
        this.pEnvironment = environment;
    }

    Node[] getEnvironment() {
        return pEnvironment;
    }

    /**
     * Bind a node at the next position in the binding vector
     */
    boolean bind(Node n, byte pos) {
        if(pos >= pEnvironment.length)
            return false;      
        pEnvironment[pos] = n;
        return true;
    }

    /**
     * If the node is a variable then return the current binding (null if not bound)
     * otherwise return the node itself.
     */
    public Node getBinding(Node node) {
        if (node instanceof Node_RuleVariable) {
//            if(fullInstantiation == false)
//                throw new CororException("A partial binding vector cannot return a binding when it is not a full instantiation of the rule.");
            int index = ((Node_RuleVariable)node).getIndex();
            
            // if the node does not have a position in the binding vector. In cases when the node is a new variable assigned by builtin such as AssignAnon, IsDLiteral, and makeTemp
            if(index > pEnvironment.length)
                return null;
            
            Node val = pEnvironment[((Node_RuleVariable)node).getIndex()];
            if (val instanceof Node_RuleVariable) {
                return getBinding(val);
            } else {
                return val;
            }
        } else if (node instanceof Node_ANY) {
            return null;
        } else if (Functor.isFunctor(node)) {
            throw new CororException("getBinding is not supported for Functors in PartialBindingVector_Test");
        } else {
            return node;
        }
    }
    
    @Override
    public String toString(){
        String str = "[";
        for(int i=0; i<pEnvironment.length; i++){
            str += pEnvironment[i].getURI()+" ";
        }
        str += "]";
        return str;
    }
    
    @Override
    public boolean equals(Object o) {

        // use hash code to check for equals is still problematic 
//        if(hash != o.hashCode()) return false;
//        if(this == o) return true;
//        if (! (o instanceof PBV) ) return false;
        Node[] other = ((PBV)o).pEnvironment;
//        if (pEnvironment.length != other.length) return false;
        for (byte i = 0; i < pEnvironment.length; i++) {
            // this commented line could be an optimization because all euqal nodes are basically identical (when an ontology is read, the same URI share a node)
//            if(pEnvironment[i] != other[i])
            if(!pEnvironment[i].sameValueAs(other[i]))
                return false;
        }
        return true;
    }
    
    /**
     * To speed up comparison in join
     * @return 
     */
    @Override
    public int hashCode() {
        if(hash != 0)
            return hash;

        for (int i = 0; i < pEnvironment.length; i++) {
            Node n = pEnvironment[i];
            hash = (hash << 1) ^ (n == null ? 0x537c: n.hashCode());
        }

        return hash;
    }
}
