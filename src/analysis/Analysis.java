package analysis;
import java.io.File;
import java.io.FileReader;

import java.util.Arrays;
import java.util.List;
import com.opencsv.CSVReader;

public class Analysis {
    /*
     * Accepts parameters object and initilizes the analysis
     */

    private Parameters params;
    
    public Analysis(Parameters params) {
        this.params = params;
    }

    public void Start() {
        String[] inputFiles = params.getInputFiles();
        for (int i = 0; i < inputFiles.length; i++) {
            String fileName = inputFiles[i];
            File f = new File(fileName);
            DataEntry data = FileHandler.BuildDataEntry(f);   
        }
    }

    public void updateCalculations(DataEntry data) {
        
    }

    public void processCalculations() {

    }
}
