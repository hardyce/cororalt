/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.reasoner.rulesys.selective;

/**
 * An general exception to throw when COROR is in an abnormal circumstance.
 * 
 * @author WEI TAI
 */
public class CororException extends RuntimeException{
    
    /**
     * Default constructor
     */
    public CororException(){
        super();
    }
    
    /**
     * Constructor
     * @param msg 
     */
    public CororException(String msg){
        super(msg);
    }
}
