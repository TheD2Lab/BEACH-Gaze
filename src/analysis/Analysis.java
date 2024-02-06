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
    private DataEntry data;
    
    public Analysis(Parameters params) {
        this.params = params;
    }

    public void run() {
        File[] inputFiles = params.getInputFiles();
        for (int i = 0; i < inputFiles.length; i++) {
            File f = inputFiles[i];
            data = FileHandler.buildDataEntry(f);   
        }
    }

    public void updateCalculations(DataEntry data) {
        
    }

    public void processCalculations() {

    }
}
