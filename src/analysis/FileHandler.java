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

import com.opencsv.CSVReader;

/*
 * Read and write xml, csv, txt files
 * Save into memory
 */

public class FileHandler {
   static public DataEntry buildDataEntry(File gazeFile) {
        try {
            FileReader fileReader = new FileReader(gazeFile);
            CSVReader csvReader = new CSVReader(fileReader);
            DataEntry data = new DataEntry(csvReader.readNext());
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                data.process(line);
            }

            csvReader.close();
            return data;
            
        } catch (Exception e) {
            System.out.println(e.toString());
            return  null;
        }
    }

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
                j.put(keys[i].toString(), (String)data.get(keys[i]));
            }
            return j;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }        
    }
    
    static public HashMap<String,String> loadParametersFromJSON(File filePath) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject)obj;
            Iterator<?> keys = jsonObject.keySet().iterator();
            HashMap<String, String> data = new HashMap<String, String>();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                data.put(key, (String)jsonObject.get(key));
            }
            return data;
        } catch(Exception e) {
           e.printStackTrace();
           return null;
        }              
    }
}
