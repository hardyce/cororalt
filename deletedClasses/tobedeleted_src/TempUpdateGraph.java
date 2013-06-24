/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.graph.impl;

import ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.enh.TempTriple;
import ie.tcd.cs.nembes.microjenaenh.shared.ReificationStyle;

/**
 * Extends GraphImpl with the ability to delete triples by update.
 * @author Wei Tai
 */
public class TempUpdateGraph extends GraphImpl{

    public TempUpdateGraph(ReificationStyle style){
        super(style);
    }

    public TempUpdateGraph(){
        super();
    }

//    /**
//     * NOTE for update graph
//     * @param updateID
//     */
//    public void deleteTempUpdate(Byte updateID){
//        int n = countTriples.size();
//        for(int i=0; i<n; i++){
//            if(countTriples.ElementAt(i) instanceof TempTriple){
//                TempTriple tt = (TempTriple)countTriples.ElementAt(i);
//                if(tt.updateID.equals(updateID)){
////                    System.err.println("DEBUG (TempUpdateGraph::deleteTempUpdate): deleting temp update "+tt);
//                    countTriples.removeElementAt(i);
//                    i--;
//                    n--;
//                }
//            }
//        }
//    }

    public void deleteTempUpdate(Byte updateID){
        int n = triples.size();
        for(int i=0; i<n; i++){
            if(triples.elementAt(i) instanceof TempTriple){
                TempTriple tt = (TempTriple)triples.elementAt(i);
                if(tt.updateID.equals(updateID)){
//                    System.err.println("DEBUG (TempUpdateGraph::deleteTempUpdate): deleting temp update "+tt);
                    triples.removeElementAt(i);
                    i--;
                    n--;
                }
            }
        }
    }


    
}
