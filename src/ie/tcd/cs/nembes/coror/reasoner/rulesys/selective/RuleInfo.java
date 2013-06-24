/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.selective;

import ie.tcd.cs.nembes.coror.util.List;

/**
 * Each of the RuleInfo objects represents the rule-construct dependency of a
 * rule. 
 * 
 * @author Wei Tai
 */
public class RuleInfo {
    
    /** the name of the rule */
    private String ruleName = null;
    
    /** the semantic level of the rule. It can be OWL, RDFS and RDF */
    private String semanticLevel = null;
    
    /** LHS constructs are those appears at the left hand side of the rule */
    private List LHSConstructs = null;
    
    /** RHS constructs are those appears at the right hand side of the rule */
    private List RHSConstructs = null;
    
    /**
     * Constructor
     * @param name 
     */
    public RuleInfo(String name){
        ruleName = name;
    }
    
    /**
     * Add a construct as a LHS construct.
     * @param construct
     */
    public void addLHSConstruct(String construct){
        if(LHSConstructs == null)
            LHSConstructs = new List();
        LHSConstructs.add(construct);
    }
    
    /**
     * Add a construct as a RHS construct.
     * @param construct
     */
    public void addRHSConstruct(String construct){
        if(RHSConstructs == null)
            RHSConstructs = new List();
        RHSConstructs.add(construct);
    }
    
    
    /**
     * @return the constructs on the right hand side of the rule
     */
    public List getRHSConstructs(){
        return RHSConstructs;
    }
    
    /**
     * @return the constructs on the left hand side of the rule
     */
    public List getLHSConstructs(){
        return LHSConstructs;
    }
    
    /**
     * Return the rule name in string
     * @return
     */
    public String getRuleName(){
        return ruleName;
    }
    
    /**
     * Set the semantic level of the RuleInfo object
     * @param level 
     */
    public void setSemanticLevel(String level){
        semanticLevel = level;
    }
    
    /**
     * @return a string form rule-construct dependencies
     */
    @Override
    public String toString(){
        String retVal = ruleName;
        retVal += "("+semanticLevel+"):";
        retVal += "[";
        if(LHSConstructs != null && !LHSConstructs.isEmpty()){
            retVal += (String)LHSConstructs.get(0);
            for(int i = 1; i<LHSConstructs.size(); i++){
                retVal += ", "+(String)LHSConstructs.get(i);
            }
        }
        retVal += "->";
        if(RHSConstructs != null && !RHSConstructs.isEmpty()){
            retVal += (String)RHSConstructs.get(0);
            for(int i = 1; i<RHSConstructs.size(); i++){
                retVal += ", "+(String)RHSConstructs.get(i);
            }
        }
        retVal += "]";
        return retVal;
    }
}
