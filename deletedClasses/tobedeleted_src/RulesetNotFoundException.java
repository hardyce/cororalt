package ie.tcd.cs.nembes.microjenaenh.shared;

public class RulesetNotFoundException_deprecated extends DoesNotExistException
{
protected String uri;

public RulesetNotFoundException( String uri )
    { super( uri );
    this.uri = uri; }

public String getURI()
    { return uri; }
}
