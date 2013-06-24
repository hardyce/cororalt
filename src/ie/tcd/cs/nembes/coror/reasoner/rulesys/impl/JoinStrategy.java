/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.CororException;

/**
 * The join strategy is used by beta nodes (RETEQueueSharing_Test) to conduct
 * pairwise joins. Each strategy contains a pair of numbers indicating the 
 * positions of the variables to be joined in the left/right conditional binding
 * vector. 
 * 
 * @author WEI TAI
 */
class JoinStrategy {
    
    byte[] left = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    byte[] right = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};    
    
    // the amount of strategies. Also indicate the next empty position in the strategy array.
    byte count = 0;
    
    /**
     * Default constructor
     */    
    JoinStrategy(){}
    
    /**
     * put a new join strategy.
     * 
     * @param left the position in the left beta sibling to join
     * @param right the position in the right beta sibling to join
     */
    void putStrategy(byte left, byte right){
        if(count >= 10) throw new CororException("No more places for new join strategies");
        this.left[count] = left;
        this.right[count] = right;
        count ++;
    }
    
    /**
     * Check if this join strategy is the same as the target join strategy.
     */
    boolean sameStrategyAs(JoinStrategy target){
        if(count != target.count) return false;
        for(int i=0; i<left.length; i++){
            if(left[i] != target.left[i] || right[i] != target.right[i])
                return false;
        }
        return true;
    }
    
    public String toString(){
        String strategy = "(";
        for(int i=0; i<count; i++){
            strategy += "<"+left[i]+","+right[i]+">";
        }
        strategy += ")";
        return strategy;
    }
}
