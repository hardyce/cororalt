/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.util.ext;

import ie.tcd.cs.nembes.microjenaenh.util.OneToManyMap;

/**
 *
 * @author Wei Tai
 */
public class OneToSetMap_deprecate extends OneToManyMap{
    public Object put(Object key, Object value){
        if(!contains(key, value))
            return super.put(key, value);
        return null;
    }
}
