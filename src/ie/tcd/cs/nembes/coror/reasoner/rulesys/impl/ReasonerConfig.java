/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys.impl;

import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.CororException;
import ie.tcd.cs.nembes.coror.util.List;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reasoner configurations
 * @author Wei Tai
 */
public class ReasonerConfig {
    
    //////////////////////////////////////////////////
    // Constants
    //////////////////////////////////////////////////
    
    /** 
     * start an normal engine. A normal reasoner reasons over the loaded ontology
     * and then pause. The following addition will be 
     */
    public static final byte REASONER_MODE_NORMAL = 0x01;
    
    /** start an temporal engine */
    public static final byte REASONER_MODE_TEMPORAL = 0x02;
    
//    /** reasoner with node sharing only */
//    public static final byte OPTIMIZATION_SHARED_RETE = 0x02;
//    
//    /** reasoner with standard RETE */
//    public static final byte OPTIMIZATION_NONE = 0x03;   
        
    //////////////////////////////////////////////////
    // General configuration
    ////////////////////////////////////////////////// 
    
//    /** location of the reasoner configuration file */
//    public static String reasonerConfigFile = "/resources/reasoner.config";
//    
//    /** the folder for all resources */
//    private static String resourcesFolder = "/resources/";
    
//    /** the folder for all ontologies */
//    private static String ontologyFolder = resourcesFolder + "onts/";
//    
//    /** the folder for all rule sets */
//    private static String ruleSetFolder = resourcesFolder + "rules/";
    
    /** location of the ontology to be reasoned */
    public static String ontology;
    
    /** location of the rule set to be used in the reasoning */
    public static String ruleSet;
    
    /** the mode of reasoner. It can be NORMAL or TEMPORAL */
    public static byte reasonerMode;
    
    /** the format of the ontology to be loaded. At the moment COROR only accept N-TRIPLE */
    public static String ontFormat = "N-TRIPLE";

    //////////////////////////////////////////////////
    // Configurations for reasoning optimizations
    ////////////////////////////////////////////////// 
    
    
    // Optimizations for the OPTIMIZATION_COMPOSABLE_RETE mode
//    static boolean shareAlphaNodes = false;
//    static boolean reorderJoins = false;
//    static boolean connectivity = false;
    
    
    /** if rules will be loaded selectively */
    public static boolean selectiveRuleLoading = false;
    
    /** the location of the rule construct dependency file */
    public static String ruleConstructsDependencies;
    
    // Optimizations for the OPTIMIZATION_SHARED_RETE mode
    static boolean shareNodes = false;
    
    // general optimizations. OPTIMIZATION_SHARED_RETE Always use cache map
    static boolean useCacheMap = false;  
    
    // the word for entries in a configuration file
    private static final String entryOntology = "ontology";
    private static final String entryRuleSet = "ruleSet";
    private static String entryRuleConstructDependencies = "ruleConstructDependencies";
//    private static String entryReoderJoins = "reorderJoins";
//    private static String entryShareAlphaNodes = "shareAlphaNodes";
//    private static String entryConnectivity = "connectivity";
    private static String entryExcludeRules = "excludeRules";
//    private static String entryCacheMap = "cacheMap";
    private static String entrySelectiveLoading = "selectiveLoading";
    private static String entryShareAllNodes = "nodeSharing";
    private static String entryReasonerMode = "reasonerMode";
    private static String entryIgnoreAxioms = "ignoreAxioms";
    
    // fields using in the code but are not yet in the configuration file
    public static boolean truthMaintenance = false;
    
    ///////////////////////////////////////////////////////////////
    /// The reminder fields are for debug and test uses
    ///////////////////////////////////////////////////////////////
    
    /** ignore RDF and OWL axioms */
    public static boolean ignoreAxioms = true;
    
    // rules to be excluded from the rule set
    public static List excludeRules;
    
    /** print out general information */
    public static boolean printGeneralInfo = true;
    
    /** print out rule information: loaded rules, excluded rules */
    public static boolean printRuleInfo = false;
    
    /** print out performance information such as time and memory*/
    public static boolean printPerformanceInfo = true;
    
    /** print out level 2 performance information such as number of matches, joins */
    public static boolean printPerformanceInfoLv2 = true;
    
    /** print out reasoning results */
    public static boolean printResultInfo = true;
    
    /** print out debug information */
    public static boolean printDebugInfo = true;
    
    
    // fields used in old versions. 
    
    /** for test use. Print out test information */ 
    static boolean printTestInfo = false;
    
    /** for printJoin use*/
    static boolean printJoin = false;
    
    /** print out trace information for deduced triples */
    static boolean printTrace = false;
    
    /** print out trace information for binding vectors */
    static boolean printDetailedTrace = false;
    
    /** print out the result ontology */
    static boolean printResults = false;
    
    static long counter = 0;
    
    
    /**
     * A simpler way to set reasoner configurations
     */
    public static void setReasonerMode(byte mode){
//        if(mode == OPTIMIZATION_COMPOSABLE_RETE){
//            shareAlphaNodes = true;
//            reorderJoins = true;
//            connectivity = true;
//            selectiveRuleLoading = true;
//        }
//        else if(mode == OPTIMIZATION_SHARED_RETE){
//            shareNodes = true;
//        }
//        else if(mode == OPTIMIZATION_NONE){
//        }
//        else{
//            throw new CororException("Unsupported Coror mode "+mode);
//        }
    }
    
//    public static void setOntology(String ontName){
//        ontology = ontologyFolder + ontName;
//    }
//    
//    public static void setRuleSet(String ruleSetName){
//        ruleSet = ruleSetFolder + ruleSetName;
//    }
    
    /**
     * A finer way to set reasoner configurations 
     */
    public static void readConfig(BufferedReader reader){
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//") || line.startsWith("\n") || line.startsWith("\t") || line.trim().equals("")) {
                    continue;
                }
                if(line.startsWith(entryOntology)){
                    ontology = line.substring(line.indexOf(":")+1).trim();
                }
                else if(line.startsWith(entryRuleConstructDependencies)){
                    ruleConstructsDependencies = line.substring(line.indexOf(":")+1).trim();
                }
                else if(line.startsWith(entryRuleSet)){
                    ruleSet = line.substring(line.indexOf(":")+1).trim();
                }
//                else if(line.startsWith(entryReoderJoins)){
//                    String val = line.substring(line.indexOf(":")+1);
//                    if(val.equals("on")){
//                        reorderJoins = true;
//                    }
//                    else if(val.equals("off")){
//                        reorderJoins = false;
//                    }
//                }
//                else if(line.startsWith(entryShareAlphaNodes)){
//                    String val = line.substring(line.indexOf(":")+1);
//                    if(val.equals("on")){
//                        shareAlphaNodes = true;
//                    }
//                    else{
//                        shareAlphaNodes = false;
//                    }                    
//                }
                else if(line.startsWith(entryReasonerMode)){
                    String val = line.substring(line.indexOf(":")+1);
                    if(val.equals("temporal")){
                        reasonerMode = REASONER_MODE_TEMPORAL;
                    }
                    else if(val.equals("normal")){
                        reasonerMode = REASONER_MODE_NORMAL;
                    }
                    else throw new CororException("Unrecognized reasoner mode "+val);
                }
//                else if(line.startsWith(entryConnectivity)){
//                    String val = line.substring(line.indexOf(":")+1);
//                    if(val.equals("on")){
//                        connectivity = true;
//                    }
//                    else{
//                        connectivity = false;
//                    }  
//                }
                else if(line.startsWith(entryExcludeRules)){
                    excludeRules = new List();
                    String val = line.substring(line.indexOf(":")+1);
                    int end;
                    while((end = val.indexOf(",")) != -1){
                        excludeRules.add(val.substring(0, end));
                        val = val.substring(end+1);
                    }
                    if(!val.equals(""))
                    excludeRules.add(val);
                    if(excludeRules.isEmpty())
                        excludeRules = null;
                }
//                else if(line.startsWith(entryCacheMap)){
//                    String val = line.substring(line.indexOf(":")+1);
//                    if(val.equals("on")){
//                        useCacheMap = true;
//                    }
//                    else{
//                        useCacheMap = false;
//                    }  
//                }
                else if(line.startsWith(entryShareAllNodes)){
                    String val = line.substring(line.indexOf(":")+1);
                    if(val.equals("on")){
                        shareNodes = true;
                    }
                    else{
                        shareNodes = false;
                    }  
                }
                else if(line.startsWith(entrySelectiveLoading)){
                    if(line.substring(line.indexOf(":")+1).equals("on")){
                        selectiveRuleLoading = true;
                    }
                    else{
                        selectiveRuleLoading = false;
                    }
                }
                else if(line.startsWith(entryIgnoreAxioms)){
                    if(line.substring(line.indexOf(":")+1).equals("on")) ignoreAxioms = true;
                    else if(line.substring(line.indexOf(":")+1).equals("off")) ignoreAxioms = false;
                    else {
                        throw new CororException(("Supported value " + line.indexOf(":")+1) + " for "+entryIgnoreAxioms+", and it is diabled");                        
                    }
                }
                else{
                    throw new CororException(" Unsupported configuration entry "+line.substring(0, line.indexOf(":")));
                }
            }
        } catch (IndexOutOfBoundsException e){
        } 
        catch (IOException ex) {
        }
    } 
}
