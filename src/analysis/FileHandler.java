package analysis;

import java.io.File;
import java.io.FileNotFoundException; 
import java.io.FileWriter;
import java.io.FileReader;
import java.util.LinkedHashMap; 
import java.util.HashMap; 
import java.util.Set;

//import javax.swing.text.html.HTMLDocument.Iterator;
import java.util.Iterator;
import java.util.ArrayList;
import org.json.simple.*;
import org.json.simple.parser.*;


/*
 * Read and write xml, csv, txt files
 * Save into memory
 */

public class FileHandler {
    static public boolean SaveParametersAsJSON(HashMap<String,String> data,String saveLocation) {
        try {
            System.out.println("Writing file!");
            FileWriter pw = new FileWriter(saveLocation);
            pw.write(buildJSON(data).toString()); //Save data as JSON to file
            pw.flush();
            pw.close();

            System.out.println("Success!");

            return true;
        } catch (Exception e) {

            System.out.println(e.toString());
            return false;

        }
    }

    static private JSONObject buildJSON(HashMap<String,String> data) { //Builds a JSON object from the Parameters
        try {

            Object[] keys = data.keySet().toArray();
            JSONObject j = new JSONObject();  

            for (int i = 0; i < data.size(); i++) {
                j.put(keys[i].toString(), data.get(keys[i]));
            }

            return j;
        } catch (Exception e) {

            System.out.println(e.toString());
            return null;

        }        
    }
    
    static public HashMap<String,String> loadParametersFromJSON(File filePath) {
        HashMap<String, String> data = new HashMap<String, String>();

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject)obj;
            Iterator<?> keys = jsonObject.keySet().iterator();

            while (keys.hasNext()) {
                String key = (String)keys.next();
                data.put(key, (String)jsonObject.get(key));
            }

            return data;
            /*
           String name = (String)jsonObject.get("Name");
           String course = (String)jsonObject.get("Course");
           JSONArray subjects = (JSONArray)jsonObject.get("Subjects");
           System.out.println("Name: " + name);
           System.out.println("Course: " + course);
           System.out.println("Subjects:");
           Iterator iterator = subjects.iterator();
           while (iterator.hasNext()) {
              System.out.println(iterator.next());
            */
        } catch(Exception e) {
           e.printStackTrace();

           return null;
        }              
    }

    /*
     * Needs ability to read files still
     * Potentially make FileHandler specifically for Parameters only? If not, buildJSON might be better placed in the Parameters object
     */
}
