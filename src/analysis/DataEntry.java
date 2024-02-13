package analysis;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataEntry {
    
    HashMap<String,Integer> headerToIndex; //Stores the index of all headers
    ArrayList<List<String>> allGazeData;
    ArrayList<List<String>> fixationData;
    ArrayList<List<String>> cleanedAllGazeData;
    ArrayList<List<String>> cleanedFixationData;
    
    List<String> lastValidFixation;
    String[] currLine; //The current line being read
    int currFixation;

    public DataEntry(String[] headers) { //The constructor takes the first line of the CSV file so it can store the headers
        headerToIndex = new HashMap<String,Integer>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            headerToIndex.put(header, i);
        }

        allGazeData = new ArrayList<List<String>>();
        fixationData = new ArrayList<List<String>>();
        cleanedAllGazeData = new ArrayList<List<String>>();
        cleanedFixationData = new ArrayList<List<String>>();
        
        currFixation = 1;
    }

    public void process(String[] currLine) { 
        // Add this line to the list of total lines
        this.currLine = currLine;
        List<String> line = Arrays.asList(currLine);
        allGazeData.add(line);

        // Determine if this line is representative of a fixation
        int fixationID = Integer.parseInt(getCurrentValue("FPOGID"));
        int fixationValidity = Integer.parseInt(getCurrentValue("FPOGV"));
        if (fixationID != currFixation) {
            if (lastValidFixation != null) fixationData.add(lastValidFixation);
            currFixation = fixationID;
        } else if (fixationID == currFixation && fixationValidity == 1) {
            lastValidFixation = line;
        }
    }

    public String getCurrentValue(String header) { //Returns the value on the current line with the given header.
        return currLine[headerToIndex.get(header)];
    }

    public String getValue(String header, int row) {
        return fixationData.get(row).get(headerToIndex.get(header)); //Gets the value in the selected row under the desired header
    }

    public String getValue(String header, int row, boolean shortened) {
        return fixationData.get(row).get(headerToIndex.get(header)); //Gets the value in the selected row under the desired header
    }

    public int rowCount() {
        return fixationData.size();
    }

    public int columnCount() {
        if (fixationData.get(0) != null) {
            return fixationData.get(0).size();
        }
        return 0;
    }
}
