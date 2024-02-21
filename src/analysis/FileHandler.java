package analysis;

import java.io.File;
import java.io.FileNotFoundException; 
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap; 
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import javax.swing.text.html.HTMLDocument.Iterator;
import java.util.Iterator;
import java.util.ArrayList;
import org.json.simple.*;
import org.json.simple.parser.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

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

    static public String convertToCSV(ArrayList<String> data) {
        String converted = "";
        for (int i = 0; i < data.size(); i++) {
            converted = converted + data.get(i);
            if (i < data.size() - 1) {
                converted = converted + ",";
            }
        }

        return converted + " \n ";
    }

    static public void writeToCSV(ArrayList<List<String>> data, String outputDirectory, String fileName) {
        System.out.println("Writing to CSV");
        try {
            // Check to see if the output directory exists. If not, create it
            File dir = new File(outputDirectory);
            if (!dir.exists()) dir.mkdirs();

            // Append a .csv to the file name if it's missing
            String fString = fileName.contains(".csv") ? fileName : fileName + ".csv";
            
            File file = new File(outputDirectory + "\\" + fString);
            FileWriter fileWriter = new FileWriter(file);
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            for (int i = 0; i < data.size(); i++) {
                List<String> row = data.get(i);
                int rowLength = row.size();
                String[] csvData = row.toArray(new String[rowLength]);
                csvWriter.writeNext(csvData);
            }

            csvWriter.close();
            System.out.println("Written Succesfully to "+outputDirectory+"//"+fileName);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /*
    public static void main(String[] args) {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        data.add(new ArrayList<String>());
        data.add(new ArrayList<String>());
        data.add(new ArrayList<String>());

        data.get(0).add("Header 1");
        data.get(0).add("Header 2");
        data.get(0).add("Header 3");
        data.get(0).add("Header 4");

        data.get(1).add("Value 1a");
        data.get(1).add("Value 2a");
        data.get(1).add("Value 3a");
        data.get(1).add("Value 4a");

        data.get(2).add("Value 1b");
        data.get(2).add("Value 2b");
        data.get(2).add("Value 3b");
        data.get(2).add("Value 4b");
        System.out.println("Writing!");
        writeAnalytics(data, "C:/Users/Productivity/Documents/Testing/Doc1.csv");
    }
    */
    static public boolean saveParametersAsJSON(HashMap<String,String> data,String saveLocation) {
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
