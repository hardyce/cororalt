/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.impl;

/**
 *
 * @author Wei Tai
 */
public class RETESibling_deprecated {

    public Integer ruleId;
    public Integer conditionId;
    public RETEQueueSharing sibling;
    
    public RETEQueueSharing me;
    public byte[] matchIndices;
    public RETESinkNode continuation;
    public byte[] myBindingIndices;
    public byte[] siblingBindingIndices;
    public boolean iAmleft;
    
    public byte[] myMatchPositions;
    public byte[] siblingMatchPositions;
    
    /** to mark whether this sibling need to be cross joined */
    public boolean needCrossJoin = false;

    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.ruleId != null ? this.ruleId.hashCode() : 0);
        hash = 67 * hash + (this.conditionId != null ? this.conditionId.hashCode() : 0);
        hash = 67 * hash + (this.sibling != null ? this.sibling.hashCode() : 0);
        hash = 67 * hash + (this.myBindingIndices != null ? this.myBindingIndices.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object o) {
        if( this == o) return true;
        if(! (o instanceof RETESibling)) return false;
        RETESibling temp = (RETESibling)o;
        if(! conditionId.equals(temp.conditionId)) return false;
        if( sibling != temp.sibling) return false;
        for(byte i = 0; i < myBindingIndices.length; i++){
            if(myBindingIndices[i] != temp.myBindingIndices[i])
                return false;
        }
        return true;        
    }
    
    /**
     * Create a new RETESibling structure for the sibling node. The new RETESibling
     * structure is constructed based on this RETESibling.
     * @return the corresponding RETESibling structure for the sibling node
     */
    public RETESibling reteSibling4Sibling(){
        RETESibling siblingCopy = new RETESibling();
        siblingCopy.ruleId = ruleId;
        siblingCopy.continuation = continuation;
        siblingCopy.matchIndices = matchIndices;
        siblingCopy.me = sibling;
        siblingCopy.sibling = me;
        siblingCopy.iAmleft = !iAmleft;
        siblingCopy.needCrossJoin = false; // can not be decided
        siblingCopy.myBindingIndices = siblingBindingIndices;
        siblingCopy.siblingBindingIndices = myBindingIndices;
        return siblingCopy;
    }
    
    public void initializeMatchPositions(){
        myMatchPositions = new byte[matchIndices.length];
        siblingMatchPositions = new byte[matchIndices.length];
        for(byte i = 0; i < matchIndices.length; i++){
            for(byte j = 0; j < myBindingIndices.length; j++){
                if(myBindingIndices[j] == matchIndices[i])
                    myMatchPositions[i] = j;
            }
            for(byte k = 0; k < siblingBindingIndices.length; k++){
                if(siblingBindingIndices[k] == matchIndices[i])
                        siblingMatchPositions[i] = k;
            }
        }
    }
}
