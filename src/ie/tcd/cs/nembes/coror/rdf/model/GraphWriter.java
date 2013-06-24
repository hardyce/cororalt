/*
 * GraphWriter.java
 *
 * Created on 21 gennaio 2008, 11.41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.rdf.model;

import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Axiom;
import ie.tcd.cs.nembes.coror.graph.GraphUtil;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
//import ie.tcd.cs.nembes.microjenaenh.ontology.OntModel;
import ie.tcd.cs.nembes.coror.vocabulary.OWL;
import ie.tcd.cs.nembes.coror.vocabulary.RDF;
import ie.tcd.cs.nembes.coror.vocabulary.RDFS;
import java.io.*;
import ie.tcd.cs.nembes.coror.datatypes.BaseDatatype;
import ie.tcd.cs.nembes.coror.datatypes.xsd.XSDDatatype;
import ie.tcd.cs.nembes.coror.graph.impl.LiteralLabel;
import ie.tcd.cs.nembes.coror.shared.InvalidPropertyURIException;
import ie.tcd.cs.nembes.coror.shared.SyntaxError;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;

/**
 *
 * @author ilBuccia
 */
public class GraphWriter {
    
//    private Model model;
    // model is replaced by graph and all classes related to model are removed from Coror
    private Graph graph;
    private int line, column;
    private boolean catchOwlAxioms;
    
    /** Creates a new instance of GraphWriter */
    public GraphWriter(Graph g) {
        graph = g;
//	catchOwlAxioms = m instanceof OntModel;
        catchOwlAxioms = false;
    }
    
    public void readNTriple(InputStream is) throws IOException {
	line=1;
	column=1;
	Node subject, predicate, object;
	while(true) {
	    subject = readNode(is, false, false, true);
	    if(subject != null) {
		predicate = readNode(is, true, false, false);
		object = readNode(is, false, true, false);
		Triple addTriple = new Triple(subject, predicate, object);
		graph.add(addTriple);
		if(catchOwlAxioms)
		    catchAxioms(addTriple);
		closeLine(is);
	    }
	    else
		//end of file
		break;
	}
    }
    
    private Node readNode(InputStream is, boolean requestURI, boolean allowLiteral, boolean allowEndOfFile) throws IOException {
	int aus = nextValidChar(is, !allowEndOfFile);
//        System.err.print(aus+"="+(char)aus);
	if(requestURI && aus != '<')
	    throw new InvalidPropertyURIException("");
	if(allowEndOfFile && aus == -1)
	    //no more triples
	    return null;
	else {
	    if(aus == '<') {
		//URI
		return Node.createURI(readURI(is));
	    }
	    else {
		if(aus == '_') {
		    if((aus = readChar(is, true)) != ':')
			throw new SyntaxError("Syntax error at line "+line+" position "+column+": expected \":\"");
		    if((aus = readChar(is, true)) != 'A') {
			return Node.createAnon(new AnonId(new String(new byte[] {Byte.parseByte(String.valueOf(aus))}).concat(parseString(is, ' '))));
		    }
		    else
			//Add an 'A' before the AnonId
			return Node.createAnon(new AnonId(parseString(is, ' ')));
		}
		else {
		    if( allowLiteral && aus == '"') {
			return Node.createLiteral(readLiteral(is));
		    }
		    else {
			throw new SyntaxError("Syntax error at line "+line+" position "+column+": unexpected input");
		    }
		}
	    }
	}
    }
    
    public void writeNTriple(OutputStream os) throws IOException {
	ExtendedIterator it = GraphUtil.findAll(graph);
	Triple t;
	Node subj, pred, obj;
	while(it.hasNext()) {
	    t = (Triple)it.next();
	    if(!(t instanceof Axiom)) {
		writeNode(os, t.getSubject());
		os.write((int)' ');
		writeNode(os, t.getPredicate());
		os.write((int)' ');
		writeNode(os, t.getObject());
		os.write((int)' ');
		os.write((int)'.');
		os.write((int)'\n');
	    }
	}
    }
    
    
    
    //PRIVATE IMPLEMENTATIONS
    
	//WRITER IMPLEMENTATION
    
    private void writeNode(OutputStream os, Node n) throws IOException {
	if(n.isURI()) {
	    writeURI(os, n.getURI());
	}
	else {
	    if(n.isBlank())
		os.write("_:A".concat(n.toString()).getBytes());
	    else {
		if(n.isLiteral()) {
		    writeLiteral(os, n);
		}
	    }
	}
    }
    
    private void writeURI(OutputStream os, String uri) throws IOException {
	os.write((int)'<');
	os.write(uri.getBytes());
	os.write((int)'>');
    }
    
    private void writeLiteral(OutputStream os, Node n) throws IOException {
	os.write((int)'"');
	os.write(n.getLiteralLexicalForm().getBytes());
	os.write((int)'"');
	String aus = n.getLiteralLanguage();
	if(aus != null) {
	    if(! aus.equals("")) {
		os.write((int)'@');
		os.write(aus.getBytes());
	    }
	}
	aus = n.getLiteralDatatypeURI();
	if(aus != null) {
	    if(! aus.equals("")) {
		os.write((int)'^');
		os.write((int)'^');
		writeURI(os, aus);
	    }
	}
    }
    
	//READER IMPLEMENTATION
    
    private String parseString(InputStream is, char term) throws IOException {
	StringBuffer result = new StringBuffer();
	int aus = readChar(is, true);
	while( (aus != -1) && (aus != (int)term)) {
	    result.append((char)aus);
	    aus = readChar(is, true);
	}
	return result.toString();
    }
    /**
     * J2ME Version
     * @param is
     * @param catchEndOfFile
     * @return
     * @throws java.io.IOException
     */
    private int nextValidChar(InputStream is, boolean catchEndOfFile) throws IOException {
	int result;
	result = readChar(is, catchEndOfFile);
	while( result == ' ' || result == '\n' || result == 13)
	    result = readChar(is, catchEndOfFile);
	return result;
    }
    
    private String readURI(InputStream is) throws IOException {
	return parseString(is, '>');
    }
    
    /** 
     *  returns the Literal read, and closes the line in the file
     *  Literals are supposed to be placed only as the Object of
     *  a Statement.
     *  The pointer of the InputStream is left at the fist char
     *  position of the next line (if there is one).
     * 
     * <br>J2ME Version: Version: Extended by Wei Tai to support different datatypes
     * rather than store all different datatypes as BaseDatatype.
     */
    private LiteralLabel readLiteral(InputStream is) throws IOException {
	int aus;
	String lexical=null, lang=null, datatypeURI=null;
	lexical = parseString(is, '"');
	aus = readChar(is, true);
	if(aus == '@') {
	    StringBuffer langBuffer = new StringBuffer();
	    aus = readChar(is, true);
	    while( (aus != '^') && (aus!=' ') && (aus != '.') ) {
		langBuffer.append((char)aus);
		aus = readChar(is, true);
	    }
	    lang = langBuffer.toString();
	}
	if(aus == '^') {
	    if((aus = readChar(is, true)) != '^')
		throw new SyntaxError("Syntax error at line "+line+" position "+column+": expected \"^^\"");
	    if((aus = readChar(is, true)) != '<')
		throw new SyntaxError("Syntax error at line "+line+" position "+column+": expected \"^^<\"");
	    datatypeURI = readURI(is);
            
	}

	//parameters are caught. 'lang' and 'datatypeURI' are allowed to be NULL
	//reader ignores LANG when Literal has a datatype
	if(datatypeURI == null && lang != null)
	    return new LiteralLabel(lexical, lang, false);
	else
	    if(datatypeURI != null){
                // J2ME Version: Extended by Wei Tai to support different datatypes
                // rather than store all different datatypes as BaseDatatype.
		if(datatypeURI.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#integer")){
                    return new LiteralLabel(lexical, null, XSDDatatype.XSDinteger);
                }
                else if(datatypeURI.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#int")){
                    return new LiteralLabel(lexical, null, XSDDatatype.XSDint);
                }
                else if(datatypeURI.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#double")){
                    return new LiteralLabel(lexical, null, XSDDatatype.XSDdouble);
                }
                else if(datatypeURI.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#float")){
                    return new LiteralLabel(lexical, null, XSDDatatype.XSDfloat);
                }
                else if(datatypeURI.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#boolean"))
                    return new LiteralLabel(lexical, null, XSDDatatype.XSDboolean);
                else if(datatypeURI.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#long"))
                    return new LiteralLabel(lexical, null, XSDDatatype.XSDlong);
                else if(datatypeURI.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#string")){
                    return new LiteralLabel(lexical, null, XSDDatatype.XSDstring);
                }
                else{
                    return new LiteralLabel(lexical, null, new BaseDatatype(datatypeURI));
                }
            }
	    else
		//both lang and datatype were NULL
		return new LiteralLabel(lexical);
    }
    
    private void closeLine(InputStream is) throws IOException {
	//catching new line or end of file
	if(!catchRequiredChar(is, '.'))
	    throw new SyntaxError("Syntax error at line "+line+" position "+column+": expected \".\"");
    }
    
    private boolean catchRequiredChar(InputStream is, char find) throws IOException {
	int aus = nextValidChar(is, false);
	return (char)aus == find;
    }
    
    private int readChar(InputStream is, boolean catchEndOfFile) throws IOException {
	int aus = is.read();
	if(aus != -1) {
	    if((char)aus == '\n') {
		line++;
		column = 1;
	    }
	    else {
		column++;
	    }
	}
	else {
	    if(catchEndOfFile)
		throw new SyntaxError("Syntax error at line "+line+" position "+column+": premature end of file");
	}
	return aus;
    }
    
    public void catchAxioms(Triple t) {
	Node subject = t.getSubject();
	if(t.matches(Node.ANY, RDF.Nodes.first, Node.ANY)) {
	    //resource is a list element
	    addResourceAxiom(subject);
	    addAxiom(subject, RDF.Nodes.type, RDF.Nodes.List);
	}
	else {
	    if(t.matches(Node.ANY, RDF.Nodes.type, RDF.Nodes.Property)) {
		//resource is an OntProperty
		addResourceAxiom(subject);
		addAxiom(subject, RDFS.Nodes.subPropertyOf, subject);
	    }
	    else {
		//resource is a class
		if(t.matches(Node.ANY, RDF.Nodes.type, OWL.Nodes.Class)) {
		    addResourceAxiom(subject);
		    addClassAxiom(subject);
		    addAxiom(subject, RDFS.Nodes.subClassOf, subject);
		}
	    }
	}
    }
    
    private void addAxiom(Node subject, Node predicate, Node object) {
	graph.add(new Axiom(subject, predicate, object));	
    }
    
    private void addResourceAxiom(Node value) {
	graph.add(new Axiom(value, RDF.Nodes.type, RDFS.Nodes.Resource));
    }

    private void addClassAxiom(Node value) {
	graph.add(new Axiom(value, RDF.Nodes.type, RDFS.Nodes.Class));
    }        

}
