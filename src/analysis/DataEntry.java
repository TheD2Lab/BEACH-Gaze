package analysis;

import java.util.HashMap;
import java.util.List;

public class DataEntry {
    HashMap<String,Integer> headerToIndex; //Stores the index of all headers
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
    }

    public String getValue(String header) { //Returns the value on the current line with the given header.
        return line[headerToIndex.get(header)];
    }
}
