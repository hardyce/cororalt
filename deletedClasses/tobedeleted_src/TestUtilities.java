/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.microjenaenh.reasoner.rulesys.test;

/**
 *
 * @author Wei Tai
 */
public class TestUtilities {
    protected static byte count = 0;
    protected static long lastTime = 0;
    
    public static void testPrintByteArray(byte[] tobePrint){
        for(int i = 0; i < tobePrint.length; i++){
            System.err.print((new Byte(tobePrint[i])).toString() + ", ");         
        }
        System.err.println();
    }

    public static void testPrintBooleanArray(boolean[] tobePrint){
        for(int i = 0; i < tobePrint.length; i++){
            System.err.print((new Boolean(tobePrint[i])).toString() + ", ");         
        }  
        System.err.println();
    }
    
    public static void testPrintObjArray(Object[] tobePrint){
        for(int i = 0; i < tobePrint.length; i++){
            System.err.print(tobePrint[i].toString() + ", ");         
        }   
        System.err.println();
    }
    
    public static String array2Str(byte[] tobePrint) {
        String ret = new String("{");
        for(int i = 0; i < tobePrint.length; i++){
            ret = ret.concat(new Byte(tobePrint[i])+", ");
        }
        return ret.concat("}");
    }
    
    public static String array2Str(boolean[] tobePrint) {
        String ret = new String("{");
        for(int i = 0; i < tobePrint.length; i++){
            ret = ret.concat(new Boolean(tobePrint[i])+", ");
        }
        return ret.concat("}");        
    }

    public static String array2Str(Object[] tobePrint) {
        String ret = new String("{");
        for(int i = 0; i < tobePrint.length; i++){
            ret = ret.concat(tobePrint[i]+", ");
        }
        return ret.concat("}");          
    }
    
    public static void printMemUsage(){
        System.err.print(" DEBUG (TestUtilities::printMemUsage): mem usage in CP"+ count++ +" is ");
        byte i = 0;
        while(i++<20){
            System.gc();
        }
        System.err.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }
    
    public static void printTime(){
        if(lastTime == 0){
            System.err.println(" DEBUG (TestUtilities::printMemUsage): start measuring time performance.");
            lastTime = System.currentTimeMillis();
        }
        else{
            long now = System.currentTimeMillis();
            System.err.println(now - lastTime);
            lastTime = now;
        }
            
    }
}
