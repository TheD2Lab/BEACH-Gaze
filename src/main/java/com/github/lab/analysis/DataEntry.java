/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.lab.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataEntry {
    
    ArrayList<String> headers;
    HashMap<String,Integer> headerToIndex; //Stores the index of all headers
    ArrayList<List<String>> data;
    
    List<String> lastValidFixation;
    int currFixation;

    public DataEntry(List<String> headers) { //The constructor takes the first line of the CSV file so it can store the headers
        
        headerToIndex = new HashMap<String,Integer>(); 
        for (int i = 0; i < headers.size(); i++) { //Hardcoded patch to fix "Time(" header in fixation files.
            String header = headers.get(i).contains("TIME(") ? "TIME" : headers.get(i);
            headerToIndex.put(header, i);
        }

        this.headers = new ArrayList<String>();
        this.headers.addAll(headers);
        this.data = new ArrayList<List<String>>();
    }

    public DataEntry(String[] headers) { //Allows constructing from an arrayList instead of just an array
        this(Arrays.asList(headers));
    }

    public void writeToCSV(String outputDirectory, String fileName) {
        ArrayList<List<String>> outputData = new ArrayList<List<String>>();
        outputData.add(headers);
        outputData.addAll(data);

        FileHandler.writeToCSV(outputData, outputDirectory, fileName);
    }

    //Adds a line of data to the DataEntry object
    public void process(List<String> currLine) {
        this.data.add(currLine);
    }

    public void process(String[] currLine) {
        process(Arrays.asList(currLine));
    }

    public List<String> getRow(int row) {
        return this.data.get(row);
    }

    public String getValue(String header, int row) {
        return this.data.get(row).get(headerToIndex.get(header)); //Gets the value in the selected row under the desired header
    }

    public int rowCount() {
        return this.data.size();
    }

    public int columnCount() {
        if (data.get(0) != null) {
            return this.data.get(0).size();
        }
        return 0;
    }

    public List<String> getHeaders() {
        return this.headers;
    }

    public int getHeaderIndex(String header) {
        return headerToIndex.get(header);
    }

    public ArrayList<List<String>> getAllData() {
        return this.data;
    }

    public DataEntry clone() {
        DataEntry clone = new DataEntry(headers);
        for (int i = 0; i < data.size(); i++) {
            clone.process(data.get(i));
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataEntry)) { return false; }

        var other = (DataEntry) obj;
        var otherHeaders = other.getHeaders();
        if (
            this.headers.size() != otherHeaders.size() ||
            this.rowCount() != other.rowCount()
        ) { return false; } // different sizes

        for (int i = 0; i < this.headers.size(); i++) {
            if (!this.headers.get(i).equals(otherHeaders.get(i))) {
                return false; // mismatch headers
            }
        }

        var otherRowIter = other.getAllData().iterator();
        var thisRowIter = this.data.iterator();
        
        while(thisRowIter.hasNext()) {
            var otherLine = otherRowIter.next();
            var thisLine = thisRowIter.next();
            if (otherLine.size() != thisLine.size()) {
                return false;
            }
            var otherLineIter = otherLine.iterator();
            var thisLineIter = thisLine.iterator();
            while(thisLineIter.hasNext()) {
                if (!thisLineIter.next().equals(otherLineIter.next())) {
                    return false;
                }
            }
        }
        return true;
    }
}
