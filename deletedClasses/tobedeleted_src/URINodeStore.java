/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.rdf.model.ehn;

import ie.tcd.cs.nembes.microjenaenh.graph.Node;
import ie.tcd.cs.nembes.microjenaenh.graph.Node_URI;
import java.util.Vector;

/**
 *
 * @author Wei Tai
 */
public class URINodeStore_deprecated {
    private static Vector store = new Vector();
    
    public static Node_URI getNode(String uri){
        Node_URI node = null;
        for(int i = 0; i < store.capacity(); i++){
            node = (Node_URI)store.elementAt(i);
            if(node.hasURI(uri))
                return node;
        }
        node = (Node_URI)Node.createURI(uri);
        store.addElement(node);
        return node;
    }
}
