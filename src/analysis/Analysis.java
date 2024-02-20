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
            calculateResults(data, params.getOutputDirectory());
            generateWindows(data);
        }

        System.out.println("Analysis Complete.");
    }

    public void calculateResults(DataEntry data, String outputDirectory) {
        ArrayList<List<String>> results = new ArrayList<List<String>>();
        results.add(new ArrayList<String>()); //Headers
        results.add(new ArrayList<String>()); //Values

        LinkedHashMap<String,String> fixation = Fixations.analyze(data);
        results.get(0).addAll(fixation.keySet());
        results.get(1).addAll(fixation.values());

        FileHandler.writeToCSV(results, outputDirectory + "\\output.csv");
    }

    public void generateWindows(DataEntry data) {
        WindowSettings settings = params.getWindowSettings();
        String[] headers = data.getHeader();
        ArrayList<List<String>> rawGazeData = data.getData(true, true);
        int dataCount = rawGazeData.size();

        if (settings.tumblingEnabled) {
            int windowSize = settings.tumblingWindowSize;
            DataEntry windowData = new DataEntry(headers); 

            for (int i = 0; i < dataCount; i++) {
                List<String> row = rawGazeData.get(i);
            }
        }

        if (settings.expandingEnabled) {
            int windowSize = settings.expandingWindowSize;
            DataEntry windowData = new DataEntry(headers);

            for (int i = 0; i < dataCount; i++) {
                
            }
        }

        if (settings.hoppingEnabled) {
            int windowSize = settings.hoppingWindowSize;
            int overlapSize = settings.hoppingOverlapSize;
            DataEntry windowData = new DataEntry(headers);

            for (int i = 0; i < dataCount; i++) {
                
            }
        }

        if (settings.eventEnabled) {

        }

    }
}
