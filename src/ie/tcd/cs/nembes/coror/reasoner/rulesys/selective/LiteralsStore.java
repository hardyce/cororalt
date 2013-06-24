/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.selective;

import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.util.Iterator;
import ie.tcd.cs.nembes.coror.util.Map;
import ie.tcd.cs.nembes.coror.util.Set;

/**
 * This class holds mappings between literal values and assigned 
 * anonnymous node.
 * @author Wei Tai
 */
public class LiteralsStore {
    /** The map holding mappings*/
    protected static Map literalcache;
    
    /**
     * Map the literal with its assigned anonnymous node.
     * @param literal the literal node to be assigned
     * @param anno the anonnymous node assigned to literal
     */
    public static void addMapping(Node literal, Node anon){
        if(literalcache == null)
            literalcache = new Map();
        
        literalcache.put(anon, literal);
    }
    
    /**
     * Get the literal from given anonnymous node.
     * @param anno the anonnymous node to be checked 
     * @return the literal node correspond to the given anonnymous node. If this
     * anonnymous node is not assigned to any literal then return null, or the 
     * mapping has not been initialized.
     */
    public static Node getLiteral(Node anon){
        if(literalcache == null)
            return null;
        
        return (Node) literalcache.get(anon);
    }
    
    /**
     * Get the annonymous node from given literal.
     * @param literal the literal to be checked
     * @return the corresponding anonnymous node. Null if this literal is not 
     * assigned to any anonnymous node yet, or the mapping has not been initialized.
     */
    public static Node getAnon(Node literal){
        if(literalcache == null)
            return null;
        
        Iterator it = literalcache.entrySet().iterator();
        for(;it.hasNext();){
            Map.Entry cur = (Map.Entry)it.next();
            if(cur.getValue() == literal)
                return (Node) cur.getKey();
        }
        return null;
    }
    
    /**
     * Retrieve all literal/anno mappings.
     * @return the mapping set of the type Map.Entry. Null if nothing has been
     * added.
     */
    public static Set getMappings(){
        if(literalcache == null)
            return null;
        
        return literalcache.entrySet();
    }
    
    public static Node assignAnon(Node literal){
        Node anon = null;
        if((anon = getAnon(literal)) == null){
            // No anonnymous node assigned yet
            anon = Node.createAnon();
            addMapping(literal, anon);
        }
        return anon;
    }
}
