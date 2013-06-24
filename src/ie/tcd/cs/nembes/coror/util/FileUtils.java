package ie.tcd.cs.nembes.coror.util;

import ie.tcd.cs.nembes.coror.shared.WrappedIOException;
import java.io.*;


public class FileUtils {
    /**
    Answer a BufferedReader than reads from the named resource file as
    UTF-8, possibly throwing WrappedIOExceptions.
    */
   public static BufferedReader openResourceFile( String filename )  
   {
       try
       {
           InputStream is = FileUtils.openResourceFileAsStream( filename );
           return new BufferedReader(new InputStreamReader(is, "UTF-8"));
       }
       catch (IOException e)
       { throw new WrappedIOException( e ); }
   }
   
   /**
    * Open an resource file for reading.
    */
   public static InputStream openResourceFileAsStream(String filename)
   throws FileNotFoundException {
       InputStream is = FileUtils.class.getResourceAsStream(filename);
       if(is == null)
    	   throw new FileNotFoundException();
       return is;
   }
   
}
