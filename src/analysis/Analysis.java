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
            ReadFile(f);   
        }
    }

    public void ReadFile(File gaze) {
        try {
            FileReader fileReader = new FileReader(gaze);
            CSVReader csvReader = new CSVReader(fileReader);
            DataEntry data = new DataEntry(csvReader.readNext());
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                data.setLine(line);
                updateCalculations(data);
            }
            
            processCalculations();
            
        } catch (Exception e) {

        }
    }

    public void updateCalculations(DataEntry data) {
        
    }

    public void processCalculations() {

    }
}
