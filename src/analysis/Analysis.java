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

        ArrayList<List<String>> fixationData = data.getData(false, false);
        fixationData.add(0, data.getHeaders()); // Add headers to data list
        System.out.println(fixationData.get(0).toString());

        LinkedHashMap<String,String> fixation = Fixations.analyze(fixationData);
        results.get(0).addAll(fixation.keySet());
        results.get(1).addAll(fixation.values());

        LinkedHashMap<String,String> entropy = GazeEntropy.analyze(fixationData);
        results.get(0).addAll(entropy.keySet());
        results.get(1).addAll(entropy.values());

        FileHandler.writeToCSV(results, outputDirectory + "\\analytics.csv");
    }

    public void generateWindows(DataEntry data) {
        WindowSettings settings = params.getWindowSettings();
        ArrayList<List<String>> rawGazeData = data.getData(true, true);


        if (settings.tumblingEnabled) {
            int windowSize = settings.tumblingWindowSize;
            int start = 0;
            int end = windowSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
                
            }
        }

        if (settings.expandingEnabled) {
            int windowSize = settings.expandingWindowSize;
            DataEntry window1 = new DataEntry((String[])data.getHeaders().toArray());
            for (int i = 0; i < rawGazeData.size(); i++) {
                
            }
        }

        if (settings.hoppingEnabled) {
            int windowSize = settings.hoppingWindowSize;
            int overlapSize = settings.hoppingOverlapSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                
            }
        }

        if (settings.eventEnabled) {

        }

    }
}
