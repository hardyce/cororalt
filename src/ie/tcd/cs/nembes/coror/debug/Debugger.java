/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.tcd.cs.nembes.coror.debug;

import ie.tcd.cs.nembes.coror.reasoner.rulesys.selective.CororException;
import ie.tcd.cs.nembes.coror.reasoner.rulesys.impl.ReasonerConfig;
import ie.tcd.cs.nembes.coror.util.Iterator;

/**
 *
 * @author WEI TAI
 */
public class Debugger {
    
    public static final byte GENERAL_INFO = 0x01;
    
    public static final byte RULE_INFO = 0x02;
    
    /** time and memory */
    public static final byte PERFORMANCE_INFO = 0x03;
    
    /** No of Joins, No of matches, No of successful joins, No of successful matches */
    public static final byte PERFORMANCE_INFO_LV2 = 0x05;
    
    public static final byte RESULT_INFO = 0x04;
    
    public static final byte DEBUG_INFO = 0x06;
           
    /** number of joins performed */
    public static long NoJ_All;
    
    /** number of matches performed */
    public static long NoM_All;
    
    /** number of successful joins performed */
    public static long NoSJ_All;
    
    /** number of successful matches performed */
    public static long NoSM_All;
    
    public static void print(byte level, String str){
        boolean print = false;
        switch(level){
            case GENERAL_INFO:
                if(ReasonerConfig.printGeneralInfo) print = true;
                break;
            case RULE_INFO:
                if(ReasonerConfig.printRuleInfo) print = true;
                break;
            case PERFORMANCE_INFO:
                if(ReasonerConfig.printPerformanceInfo) print = true;   
                break;
            case PERFORMANCE_INFO_LV2:
                if(ReasonerConfig.printPerformanceInfoLv2) print = true;
                break;
            case RESULT_INFO:
                if(ReasonerConfig.printResultInfo) print = true;
                break;
            case DEBUG_INFO:
                if(ReasonerConfig.printDebugInfo) print = true;
                break;
            default:
                throw new CororException("Unsupported information level");
        }
        
        if(print)
            System.err.print(str);
    }
    
    public static void println(byte level, String str){
        print(level, str+"\n");
    }
    
    public static void printAll(byte level, String heading, Iterator it){
        boolean print = false;
        switch(level){
            case GENERAL_INFO:
                if(ReasonerConfig.printGeneralInfo) print = true;
                break;
            case RULE_INFO:
                if(ReasonerConfig.printRuleInfo) print = true;
                break;
            case PERFORMANCE_INFO:
                if(ReasonerConfig.printPerformanceInfo) print = true;   
                break;
            case PERFORMANCE_INFO_LV2:
                if(ReasonerConfig.printPerformanceInfoLv2) print = true;
                break;
            case RESULT_INFO:
                if(ReasonerConfig.printResultInfo) print = true;
                break;
            case DEBUG_INFO:
                if(ReasonerConfig.printDebugInfo) print = true;
                break;
            default:
                throw new CororException("Unsupported information level");            
        }
        if(print)
            printAll(heading, it);
    }
    
    private static void printAll(String heading, Iterator it){
        System.err.println();
        System.err.println("================= "+ heading+" =================");
        while(it.hasNext()) {
            System.err.println(it.next());
        }
        System.err.println("================================================");
        System.err.println();
    }
    
    public static long getMemUsage(){
        byte i = 0;
        while(i++<20){
            System.gc();
        }
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
