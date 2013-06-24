/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror;

import ie.tcd.cs.nembes.coror.debug.Debugger;
import ie.tcd.cs.nembes.coror.graph.Factory;
import ie.tcd.cs.nembes.coror.graph.Graph;
import ie.tcd.cs.nembes.coror.graph.Node;
import ie.tcd.cs.nembes.coror.graph.Triple;
import ie.tcd.cs.nembes.coror.rdf.model.GraphWriter;
import ie.tcd.cs.nembes.coror.rdf.model.impl.RDFAxiomWriter;
import ie.tcd.cs.nembes.coror.reasoner.InfGraph;
import ie.tcd.cs.nembes.coror.reasoner.Reasoner;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RETEReasoner;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.RETERuleInfGraph;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.Rule;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.TemporalRETERuleInfGraph;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.ReasonerConfig;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.CororException;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.OwlRuleComposer;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.RuleInfo;
import ie.tcd.cs.nembes.coror.util.List;
import ie.tcd.cs.nembes.coror.util.iterator.ExtendedIterator;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WEI TAI
 */
public class Coror {
    
    /** A model that allows ontology access */
//    private Model model;
    
    private InfGraph infGraph;
    
    private Graph ontGraph;
    
    private Reasoner reasoner;
    
    private List rules;
    
    private GraphWriter graphWriter;
    
    private GraphWriter SchemaWriter;
    
    private Graph SchemaGraph;
    
    /**
     * Start Coror from commandline
     */
    public Coror(){}
    public void beep(){
    
    System.out.println("fdfddfddfdfdfffffffff");
    
    }
    public void setOntology(Graph ontgraph){
    
    this.ontGraph=ontgraph;
    
    }
    public void setSchema() {
        try {
            SchemaGraph = Factory.createDefaultGraph();
            SchemaWriter = new GraphWriter(SchemaGraph);
            SchemaWriter.readNTriple(new BufferedInputStream(new FileInputStream("C:\\Users\\Colin\\Coror\\resources\\onts\\food.triples")));
            reasoner = reasoner.bindSchema(SchemaGraph);
        } catch (IOException ex) {
            Logger.getLogger(Coror.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public static void main(String[] args) {
        Coror reasoner = new Coror("D:/working/NetBeansProjects/Coror/resources/reasoner.config");         
        reasoner.startReasoner();
        
        for(ExtendedIterator it = reasoner.getInfGraph().find(Node.ANY, Node.ANY, Node.ANY); it.hasNext();){
            System.err.println(it.next());
        }
    }
    
    public Coror(String configFile){
        loadConfiguration(configFile);
        //loadOntology();
        rules = loadRules();
    }
    
    protected final void loadConfiguration(String configFile){
        // read reasoner configurations
//        BufferedReader configReader = new BufferedReader(new InputStreamReader(Coror.class.getResourceAsStream(configFile)));
        BufferedReader configReader;
        try {
            configReader = new BufferedReader(new FileReader(configFile));
        } catch (FileNotFoundException ex) {
            throw new CororException("No such file exist for "+configFile);
        }
        ReasonerConfig.readConfig(configReader);
        try {
            configReader.close();
        } catch (IOException ex) {}        
    }
    
    /**
     * startReasoner engine to do an once-off reasoning
     */
    protected void startNormal(){  
        System.out.println("Normal Mode Running");
        if(reasoner == null) {
            reasoner = new RETEReasoner(rules);
            Debugger.println(Debugger.GENERAL_INFO, " Rule set "+ReasonerConfig.ruleSet+" is loaded.");
        }       

        // bind a graph to the reasoner or rebind a changed graph       
        if(infGraph == null) infGraph = reasoner.bind(ontGraph);
        else infGraph.rebind(ontGraph);
                
        // Start reasoning
        long startTime = System.currentTimeMillis();
        infGraph.prepare();
        long endTime = System.currentTimeMillis();
        
        Debugger.println(Debugger.PERFORMANCE_INFO, " Reasoning time: "+(endTime-startTime)+" ms.");
        Debugger.println(Debugger.PERFORMANCE_INFO, " Memory usage: "+Debugger.getMemUsage()+" Byte.");
        Debugger.println(Debugger.GENERAL_INFO, " Original ontology size: "+(infGraph.size() -  infGraph.getDeductionsGraph().size())+" triples.");
        Debugger.println(Debugger.GENERAL_INFO, " Deduced triple size: "+infGraph.getDeductionsGraph().size()+" triples.");
        Debugger.println(Debugger.PERFORMANCE_INFO_LV2, " NoJ_All = "+ Debugger.NoJ_All + " NoSJ_All =  "+Debugger.NoSJ_All);
        Debugger.println(Debugger.PERFORMANCE_INFO_LV2, " NoM_All = "+ Debugger.NoM_All + " NoSM_All =  "+Debugger.NoSM_All); 
    }
    
    
    
    public final void loadOntology(){

        if(graphWriter == null) {
            ontGraph = Factory.createDefaultGraph();
            graphWriter = new GraphWriter(ontGraph);
        }
        if(!ReasonerConfig.ignoreAxioms) 
            RDFAxiomWriter.writeAxioms(ontGraph);
        
        if(ReasonerConfig.ontFormat.equals("N-TRIPLE")) {
            try {
                graphWriter.readNTriple(new BufferedInputStream(new FileInputStream(ReasonerConfig.ontology)));
            } catch (IOException ex) {
                throw new CororException("An error occured while reading the ontology " + ReasonerConfig.ontology);
            } 
        }
        else{
            throw new CororException("The language" + ReasonerConfig.ontFormat +" is supported for input");
        }
    }
    
    public final List loadRules(){
        
        // run selective rule loading algorithm and calculate the rules to be excluded
        if(ReasonerConfig.selectiveRuleLoading){
            OwlRuleComposer composer = new OwlRuleComposer(ontGraph);
            List unselectedRules = composer.getUnselectedRules();
            Debugger.printAll(Debugger.RULE_INFO, "unselected rules", unselectedRules.iterator());
            if(!unselectedRules.isEmpty()){
                if(ReasonerConfig.excludeRules == null) ReasonerConfig.excludeRules = new List();
                for(int i=0; i<unselectedRules.size(); i++){
                    ReasonerConfig.excludeRules.add(((RuleInfo)unselectedRules.get(i)).getRuleName());
                }
            }
        }
        
        // generate a selective rule set
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ReasonerConfig.ruleSet));
        } catch (FileNotFoundException ex) {
            System.err.println("No such a file exist for "+ReasonerConfig.ruleSet);
        }
        List rules;
        if(ReasonerConfig.excludeRules == null)
            rules = Rule.parseRules(Rule.rulesParserFromReader(br));
        else
            rules = Rule.parseRules(Rule.rulesParserFromReader(br), ReasonerConfig.excludeRules);

        try {
            br.close();
        } catch (IOException ex) {}
        
        return rules;
    }
    
    protected void startTemporal(){
        System.out.println("Temporal Mode Running");
        if(reasoner == null) {
            
            reasoner = new RETEReasoner(rules);
            Debugger.println(Debugger.GENERAL_INFO, " Rule set "+ReasonerConfig.ruleSet+" is loaded.");
            
        }       
        //setSchema();
        System.out.println("pastset");
        // bind a graph to the reasoner or rebind a changed graph       
        if(infGraph == null) infGraph = reasoner.bindTemporal(ontGraph);
        else infGraph.rebind(ontGraph);
           
        // Start reasoning
        long startTime = System.currentTimeMillis();
        infGraph.prepare();
        long endTime = System.currentTimeMillis();
        
        Debugger.println(Debugger.PERFORMANCE_INFO, " Reasoning time: "+(endTime-startTime)+" ms.");
        Debugger.println(Debugger.PERFORMANCE_INFO, " Memory usage: "+Debugger.getMemUsage()+" Byte.");
        Debugger.println(Debugger.GENERAL_INFO, " Original ontology size: "+(infGraph.size() -  infGraph.getDeductionsGraph().size())+" triples.");
        Debugger.println(Debugger.GENERAL_INFO, " Deduced triple size: "+infGraph.getDeductionsGraph().size()+" triples.");
        Debugger.println(Debugger.PERFORMANCE_INFO_LV2, " NoJ_All = "+ Debugger.NoJ_All + " NoSJ_All =  "+Debugger.NoSJ_All);
        Debugger.println(Debugger.PERFORMANCE_INFO_LV2, " NoM_All = "+ Debugger.NoM_All + " NoSM_All =  "+Debugger.NoSM_All); 
    }
    
    public void startReasoner(){
        switch (ReasonerConfig.reasonerMode){
            case ReasonerConfig.REASONER_MODE_NORMAL:
                startNormal();
                break;
            case ReasonerConfig.REASONER_MODE_TEMPORAL:
                startTemporal();
                break;
            default:
                throw new CororException("unsuppoted engine mode.");
        }
    }
    
    /**
     * add a statement into the reasoner. Inference will be performed automatically
     * if the reasoner is started.
     */
    public void addTriple(Triple t){
        if(infGraph != null){
            ((RETERuleInfGraph)infGraph).performAdd(t);
        }
        else
            ontGraph.add(t);        
    }
    
    /**
     * remove a triple from the reasoner. Inference will be performed automatically
     * if the reasoner is started.
     */
    public void removeTriple(Triple t){
        ((RETERuleInfGraph)infGraph).performDelete(t);
        if(infGraph != null) {
            infGraph.rebind(ontGraph);
            infGraph.prepare();
        }
     }
    
    public void setOntGraph(Graph graph){
        this.ontGraph=graph;
        
    }
    
    /**
     * Sweep the temporal triples as well as T-PBVs with a time stamp within the
     * given time slot. This method is only useful under the temporal mode.
     */
    public void sweepReasoner(long start, long end){
       // if(ReasonerConfig.reasonerMode == ReasonerConfig.REASONER_MODE_TEMPORAL){
            ((TemporalRETERuleInfGraph)infGraph).sweepGraph(start, end);
       // }
       // else
        //    throw new CororException ("sweep reasoner is only supported for temporal engine");
            
    }
    
    
    public InfGraph getInfGraph(){
        return infGraph;
    }
    public Graph getOntGraph(){
        return ontGraph;
    }
    /**
     * Stop a reasoner. 
     */
    public void stopReasoner(){
        infGraph.close();
        infGraph = null;
        reasoner = null;
        ontGraph = null;
        rules = null;
        graphWriter = null;
    }
    
    
    
}
