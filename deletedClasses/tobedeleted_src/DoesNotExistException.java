package ie.tcd.cs.nembes.microjenaenh.shared;

import ie.tcd.cs.nembes.microjenaenh.db.RDFRDBException;


public class DoesNotExistException_deprecated extends RDFRDBException
{
/**
     The entity <code>name</code> does not exist.
*/
public DoesNotExistException( String name )
    { super( name); }
}
