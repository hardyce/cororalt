/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.graph;

/**
 * Triple with truth maintainence.
 * @author WEI TAI
 */
public class DeducedTriple extends Triple {
    private int justification;
    private boolean visited = false;
    
    public DeducedTriple(Node s, Node p, Node o){
        super(s, p, o);
        justification = 1;
    }

    public DeducedTriple(Triple t) {
        super(t.getSubject(), t.getPredicate(), t.getObject());
        justification = 1;        
    }
    
    public int getJustification(){
        return justification;
    }
    
    public int incJustification(){
        return ++justification;
    }
    
    public int decJustification(){
        visited = true;
        return --justification;
        
    }
    
    public boolean isVisited(){
        return visited;
    }
    
    public String toString(){
        return super.toString().concat(" : "+justification);
    }
}
