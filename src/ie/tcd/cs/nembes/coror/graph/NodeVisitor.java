package ie.tcd.cs.nembes.coror.graph;

import ie.tcd.cs.nembes.coror.rdf.model.AnonId;
import ie.tcd.cs.nembes.coror.graph.impl.LiteralLabel;
//import ie.tcd.cs.nembes.microjenaenh.rdf.model.AnonId;



public interface NodeVisitor
{
	Object visitAny( Node_ANY it );
	Object visitBlank( Node_Blank it, AnonId id );
	Object visitLiteral( Node_Literal it, LiteralLabel lit );
	Object visitURI( Node_URI it, String uri );
	Object visitVariable( Node_Variable it, String name );
}
