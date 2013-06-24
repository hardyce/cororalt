package ie.tcd.cs.nembes.microjenaenh.graph.impl;

import ie.tcd.cs.nembes.microjenaenh.graph.Capabilities;


public class AllCapabilities_deprecated implements Capabilities
{
public boolean sizeAccurate() { return true; }
public boolean addAllowed() { return addAllowed( false ); }
public boolean addAllowed( boolean every ) { return true; } 
public boolean deleteAllowed() { return deleteAllowed( false ); }
public boolean deleteAllowed( boolean every ) { return true; } 
public boolean canBeEmpty() { return true; }
public boolean iteratorRemoveAllowed() { return true; }
public boolean findContractSafe() { return true; }
public boolean handlesLiteralTyping() { return true; }
}