package analysis;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import com.opencsv.CSVReader;
import java.io.File;

public class Analysis {
    /*
     * Accepts parameters object and initilizes the analysis
     */

    private Parameters params;
    
    public Analysis(Parameters params) {
        this.params = params;
    }

    public void run() {
        File[] inputFiles = params.getInputFiles();
        for (int i = 0; i < inputFiles.length; i++) {
            File f = inputFiles[i];
            DataEntry data = FileHandler.buildDataEntry(f);   
            calculateResults(data);
        }
    }

    public void calculateResults(DataEntry data) {
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        results.add(new ArrayList<String>()); //Headers
        results.add(new ArrayList<String>()); //Values

        LinkedHashMap<String,String> fixation = Fixations.analyze(data);
        results.get(0).addAll(fixation.keySet());
        results.get(1).addAll(fixation.values());

        FileHandler.writeAnalytics(results, params.getOutputFile());
    }
}
