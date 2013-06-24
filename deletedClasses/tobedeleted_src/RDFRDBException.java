package ie.tcd.cs.nembes.microjenaenh.db;

import ie.tcd.cs.nembes.microjenaenh.shared.JenaException;


public class RDFRDBException_deprecated extends JenaException {

    /** Construct an exception with given error message */
    public RDFRDBException( String message ) {
        super( message );
    }

    /** Construct an exception with given error message */
    public RDFRDBException(String message, Exception e) {
        super( message, e ); 
    }

	
}
