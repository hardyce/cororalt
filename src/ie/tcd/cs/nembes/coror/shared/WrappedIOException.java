package ie.tcd.cs.nembes.coror.shared;

import java.io.IOException;

public class WrappedIOException extends JenaException
{
public WrappedIOException( IOException cause ) 
    { super( cause ); }

public WrappedIOException( String message, IOException cause ) 
    { super( message, cause ); }
}
