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

    public boolean run() {
        try {
            File[] inputFiles = params.getInputFiles();
            for (int i = 0; i < inputFiles.length; i++) {
                File f = inputFiles[i];
                DataEntry rawData = FileHandler.buildDataEntry(f);
                DataEntry data = DataFiltering.FilterByFixations(rawData);

                String pName = f.getName().replace("_all_gaze.csv", "");
                String pDirectory = params.getOutputDirectory() + "\\" + pName;

                System.out.println(pName);
                System.out.println(pDirectory);
                
                //calculateResults(data, pDirectory)
                ArrayList<List<String>> fixationOutput = calculateResults(data, pDirectory);
                FileHandler.writeToCSV(fixationOutput, pDirectory, pName);
                generateWindows(data, pDirectory);
            }

            System.out.println("Analysis Complete.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //public void calculateResults(DataEntry data, String outputDirectory) {
    public ArrayList<List<String>> calculateResults(DataEntry data, String outputDirectory) {
        ArrayList<List<String>> results = new ArrayList<List<String>>();
        results.add(new ArrayList<String>()); //Headers
        results.add(new ArrayList<String>()); //Values

        //ArrayList<List<String>> fixationData = data.getData(false, false);
        ArrayList<List<String>> fixationData = data.getAllData();
        fixationData.add(0, data.getHeaders()); // Add headers to data list
        System.out.println(fixationData.get(0).toString());

        LinkedHashMap<String,String> fixation = Fixations.analyze(fixationData);
        results.get(0).addAll(fixation.keySet());
        results.get(1).addAll(fixation.values());

        LinkedHashMap<String,String> saccades = Saccades.analyze(fixationData);
        results.get(0).addAll(saccades.keySet());
        results.get(1).addAll(saccades.values());

        LinkedHashMap<String,String> entropy = GazeEntropy.analyze(fixationData);
        results.get(0).addAll(entropy.keySet());
        results.get(1).addAll(entropy.values());

        //FileHandler.writeToCSV(results, outputDirectory, "analytics.csv");
        return results;
    }

    //public void calculateResults(ArrayList<List<String>> data, String outputDirectory, String fileName) {
    public ArrayList<List<String>> calculateResults(ArrayList<List<String>> data, String outputDirectory, String fileName) {
        ArrayList<List<String>> results = new ArrayList<List<String>>();
        results.add(new ArrayList<String>()); //Headers
        results.add(new ArrayList<String>()); //Values

        LinkedHashMap<String,String> fixation = Fixations.analyze(data);
        results.get(0).addAll(fixation.keySet());
        results.get(1).addAll(fixation.values());

        LinkedHashMap<String,String> saccades = Saccades.analyze(data);
        results.get(0).addAll(saccades.keySet());
        results.get(1).addAll(saccades.values());

        LinkedHashMap<String,String> entropy = GazeEntropy.analyze(data);
        results.get(0).addAll(entropy.keySet());
        results.get(1).addAll(entropy.values());

        //FileHandler.writeToCSV(results, outputDirectory, fileName);
        return results;
    }

    public void generateWindows(DataEntry data, String outputDirectory) {
        WindowSettings settings = params.getWindowSettings();
        int timeIndex = data.getHeaderIndex("TIME");
        ArrayList<List<String>> rawGazeData = data.getAllData();//data.getData(true, true);
        
        List<String> headers = data.getHeaders();

        System.out.println(params.getWindowSettings());
        if (settings.tumblingEnabled) {
            List<ArrayList<List<String>>> windows = new  ArrayList<ArrayList<List<String>>>();
            ArrayList<List<String>> window = new ArrayList<List<String>>();
            window.add(headers);

            int windowSize = settings.tumblingWindowSize;
            int end = windowSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
                Double t = Double.parseDouble(row.get(timeIndex));
                
                if (t > end) {
                    end += windowSize;
                    windows.add(window);

                    window = new ArrayList<List<String>>();
                    window.add(headers);
                    window.add(row);
                } else if (i == rawGazeData.size() - 1) { 
                    window.add(row);
                    windows.add(window);
                } else {
                    window.add(row);
                }
            }

            String subDirectory = outputDirectory + "\\tumbling";
            int windowCount = 1;
            for (ArrayList<List<String>> w : windows) {
                String fileName = "window" + windowCount;
                FileHandler.writeToCSV(w, subDirectory, fileName);
                calculateResults(w, subDirectory, fileName + "_analytics.csv");
                windowCount++;
            }
            System.out.println(windows.size());
        }

        if (settings.expandingEnabled) {
            int windowSize = settings.expandingWindowSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
            }
        }


        if (settings.hoppingEnabled) {
            int windowSize = settings.hoppingWindowSize;
            int overlapSize = settings.hoppingOverlapSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
            }
        }

        if (settings.eventEnabled) {

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
            }
        }

    }
}
