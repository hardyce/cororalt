/*
 * InvalidCastException.java
 *
 * Created on 26 novembre 2007, 11.19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.rdf.model.impl;

import ie.tcd.cs.nembes.microjenaenh.shared.JenaException;

/**
 *
 * @author ilBuccia
 */
public class InvalidCastException extends JenaException {
    
    /** Creates a new instance of InvalidCastException */
    public InvalidCastException() {
	super();
    }
    
    public InvalidCastException(String destination, String original) {
	super("Cannot convert "+original+" instance in a "+destination+" instance.");
    }
    
}
