/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh;

import ie.tcd.cs.nembes.microjenaenh.graph.Triple;
import ie.tcd.cs.nembes.microjenaenh.util.List;

/**
 * Temporary updates. Use an ID to identify temporary updates in RETE network to
 * enable fast delete. At most 256 sets of updates can be inserted into RETE network
 * at the same time and users need to control this. Otherwise will cause inconsistency.
 * @author Wei Tai
 */
public class TempUpdate {
    /** updates. Should be in triple format */
    public List triples;

    /** unique id for this set of updates */
    public Byte updateID;
    
    public static byte nextID = (byte) 0x00;

    public TempUpdate(List triples){
        this.triples = new List();
        updateID = new Byte(nextID++);
        
        for(int i=0; i<triples.size(); i++){
            this.triples.add(new TempTriple((Triple) triples.get(i), updateID));
        }
    }
}
