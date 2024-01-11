package analysis;


import java.io.File;
import java.io.FileNotFoundException; 
import java.io.PrintWriter; 
import java.util.LinkedHashMap; 
import java.util.HashMap; 
import org.json.*;

/*
 * Read and write xml, csv, txt files
 * Save into memory
 */

public class FileHandler {
    static public boolean SaveParametersAsJSON(Parameters parameters,String saveLocation) {
        try {
            PrintWriter pw = new PrintWriter(saveLocation);
            pw.write(buildJSON(parameters).toString()); //Save data as JSON to file

            pw.flush();
            pw.close();

            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    static private JSONObject buildJSON(Parameters parameters) { //Builds a JSON object from the Parameters
        try {
            JSONObject j = new JSONObject();        
            j.put("InputDirectory", parameters.inputDirectory);
            j.put("OutputDirectory", parameters.outputDirectory);

            return j;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }        
    }

    /*
     * Needs ability to read files still
     * Potentially make FileHandler specifically for Parameters only? If not, buildJSON might be better placed in the Parameters object
     */
}
