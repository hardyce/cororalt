/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.rdf.model.ehn;

import java.util.Vector;

/**
 *
 * @author Wei Tai
 */
public class URIStore_deprecated {
    private static Vector store = new Vector();
    
    public static String get(String uri){
        for(int i=0; i<store.capacity(); i++){
            String uri_in = (String)store.elementAt(i);
            if(uri_in.hashCode() != uri.hashCode()){
                continue;
            }
            if(uri_in.equals(uri)){
                return uri_in;
            }
        }

        store.addElement(uri);
        return uri;
    }
}
