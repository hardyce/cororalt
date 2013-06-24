/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.debug;

import ie.tcd.cs.nembes.coror.Coror;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.graph.temporal.TemporalTriple;
import ie.tcd.cs.nembes.coror.vocabulary.RDF;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WEI TAI
 */
public class StreamGenerator implements Runnable{

    public String uri = "http://www.example.com/";
    
    boolean stop = false;
    
    Coror reasoner;
    
    public static void main(String[] args){
        // reasoner over ontology
        
        // startReasoner generator
        StreamGenerator g = new StreamGenerator();
        Thread t = new Thread(g);
        t.start();
        
        try {
            Thread.sleep(200000);
        } catch (InterruptedException ex) {
            Logger.getLogger(StreamGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        g.stop();
    }
    
    public StreamGenerator(){
        this.reasoner = new Coror("D:/working/NetBeansProjects/Coror/resources/reasoner.config");
        reasoner.loadOntology();
    }
    
    @Override
    public synchronized void run() {
        reasoner.startReasoner();
        int i = 0;
        while(!stop){ 
            Triple t = new TemporalTriple(Node.create("http://www.example.com/ontology/acar"+i), RDF.Nodes.type, Node.create("http://www.example.com/Car"), System.currentTimeMillis());
            reasoner.addTriple(t);
            System.err.println(t);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(StreamGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
    }
    
    public synchronized void stop() {
        stop = true;
    }
    
}
