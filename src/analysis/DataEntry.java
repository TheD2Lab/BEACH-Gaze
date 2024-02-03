package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataEntry {
    HashMap<String,Integer> headerToIndex; //Stores the index of all headers
    ArrayList<ArrayList<String>> fixationData;
    
    String[] line; //The current line being read

    public DataEntry(String[] headers) { //The constructor takes the first line of the CSV file so it can store the headers
        headerToIndex = new HashMap<String,Integer>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            headerToIndex.put(header, i);
        }
    }

    public void setLine(String[] line) { //Sets the currently read line of the CSV.
        this.line = line;
        fixationData.add(new ArrayList<String>()); //Create a new line in the fixationData list
        for (int i = 0; i < line.length; i++) {
            fixationData.get(fixationData.size() - 1).add(line[i]); //Add all rows to the new line in the fixationData list.
        }
    }

    public String getCurrentValue(String header) { //Returns the value on the current line with the given header.
        return line[headerToIndex.get(header)];
    }

    public String getValue(String header, int row) {
        return fixationData.get(row).get(headerToIndex.get(header)); //Gets the value in the selected row under the desired header
    }
}
