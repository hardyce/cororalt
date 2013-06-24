/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.microjenaenh.graph.Node;

/**
 *
 * @author Wei Tai
 */
public class IntermediateBindingVector_deprecate{
    
    protected Node[] environment;
    
    protected int hash = 0;
    
    protected byte nextBindingPosition = 0;
    
    public Node[] getEnvironment(){
        return environment;
    }
    
    /**
     * Default constructor for IntermediateBindingVector. 
     */
    public IntermediateBindingVector(){
        environment = new Node[3];
    }
    
    public IntermediateBindingVector(byte size){
        environment = new Node[size];
    }
    
    public IntermediateBindingVector(Node[] env) {
        environment = env;
    }
    
    public boolean bind(Node n){
        for(byte i = 0; i < environment.length; i++){
            if(environment[i] == null){
                environment[i] = n;
                return true;
            }
        }
        return false;
        
//        environment[nextBindingPosition++] = n;
//        return true;
    }
    
    public void fromBindingVector(BindingVector env){
        byte envSize = 0;
        for(byte i = 0; i<env.environment.length; i++){
            if(env.environment[i] != null)
                envSize ++;
        }
        
        environment = new Node[envSize];
        byte j = 0;
        for(byte i = 0; i<env.environment.length; i++){
            if(env.environment[i] != null){
                Node n = env.environment[i];
                // shallow copy, can cause problems
                environment[j] = n;
            }
        }
    }
    
    public BindingVector toBindingVector(byte varNum, byte[] bindingIndices){
        BindingVector env = null;
        if(this.getClass().equals(TempIntermediateBindingVector.class)){
//            System.err.println("DEBUG (IntermediateBindingVector::toBindingVector): converting a TempIntermediateBindingVector to TempBindingVector.");
            env = new TempBindingVector(varNum, ((TempIntermediateBindingVector)this).updateID);
        }
        else {
            env = new BindingVector(varNum);
        }
        
        for(byte i = 0; i < bindingIndices.length; i++){
            if(bindingIndices[i] == -1)
                break;
            env.bind(bindingIndices[i], environment[i]);
        }
        return env;
    }

    public boolean equals(Object bv){
        
        if(this.hashCode() != bv.hashCode())
            return false;
        
        Node[] otherone = ((IntermediateBindingVector)bv).environment;
        Node[] mine = this.environment;
        if( otherone.length != mine.length) return false;
        for(byte i = (byte)(mine.length-1); i >= 0 ; i--){
            if(! mine[i].sameValueAs(otherone[i])) return false;
        }
        return true;
    }

    public int hashCode() {
        if(hash != 0)
            return hash;

        for (int i = 0; i < environment.length; i++) {
            Node n = environment[i];
            hash = (hash << 1) ^ (n == null ? 0x537c: n.hashCode());
        }

        return hash;
    }
}
