package analysis;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataEntry {
    
    ArrayList<String> headers;
    HashMap<String,Integer> headerToIndex; //Stores the index of all headers
    ArrayList<List<String>> gazeData;
    ArrayList<List<String>> fixationData;
    ArrayList<List<String>> rawGazeData;
    ArrayList<List<String>> rawFixationData;
    
    List<String> lastValidFixation;
    String[] currLine; //The current line being read
    int currFixation;

    public DataEntry(String[] headers) { //The constructor takes the first line of the CSV file so it can store the headers
        headerToIndex = new HashMap<String,Integer>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].contains("TIME(") ? "TIME" : headers[i];
            headerToIndex.put(header, i);
        }

        this.headers = new ArrayList<String>();
        this.headers.addAll(Arrays.asList(headers));

        this.gazeData = new ArrayList<List<String>>();
        this.fixationData = new ArrayList<List<String>>();
        this.rawGazeData = new ArrayList<List<String>>();
        this.rawFixationData = new ArrayList<List<String>>();
        
        this.currFixation = 1;
    }

    public DataEntry(ArrayList<String> headers) { //Allows constructing from an arrayList instead of just an array
        this((String[])headers.toArray());
    }

    public void process(String[] currLine) { 
        // Add this line to the list of total lines
        this.currLine = currLine;
        List<String> line = Arrays.asList(currLine);
        this.rawGazeData.add(line);

        // Determine if this line is representative of a fixation
        int fixationID = Integer.parseInt(getCurrentValue("FPOGID"));
        int fixationValidity = Integer.parseInt(getCurrentValue("FPOGV"));
        if (fixationID != currFixation) {
            if (lastValidFixation != null) this.rawFixationData.add(lastValidFixation);
            currFixation = fixationID;
        } else if (fixationID == currFixation && fixationValidity == 1) {
            lastValidFixation = line;
        }
        
        // Temporary line for now until cleansing process is finished
        this.fixationData = this.rawFixationData;
    }

    public void process(ArrayList<String> currLine) { //Allows inputting an arrayList instead of an array
        process((String[])currLine.toArray());
    }

    public String getCurrentValue(String header) { //Returns the value on the current line with the given header.
        return this.currLine[headerToIndex.get(header)];
    }

    public String getValue(String header, int row) {
        return this.fixationData.get(row).get(headerToIndex.get(header)); //Gets the value in the selected row under the desired header
    }

    public String getValue(String header, int row, boolean shortened) {
        return this.fixationData.get(row).get(headerToIndex.get(header)); //Gets the value in the selected row under the desired header
    }

    public int rowCount() {
        return this.fixationData.size();
    }

    public int columnCount() {
        if (fixationData.get(0) != null) {
            return this.fixationData.get(0).size();
        }
        return 0;
    }

    public List<String> getHeaders() {
        return this.headers;
    }

    public int getHeaderIndex(String header) {
        return headerToIndex.get(header);
    }

    public ArrayList<List<String>> getData(boolean raw, boolean allGaze) {
        if (raw) {
            if (allGaze) 
                return this.rawGazeData;
            else 
                return this.rawFixationData;
        } else {
            if (allGaze) 
                return this.gazeData;
            else 
                return this.fixationData;
        }
    }
}
