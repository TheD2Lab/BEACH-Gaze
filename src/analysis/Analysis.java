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
    final static int SCREEN_WIDTH = 1920;
	final static int SCREEN_HEIGHT = 1080;

    private Parameters params;
    
    public Analysis(Parameters params) {
        this.params = params;
    }

    public boolean run() {
        try {
            File[] inputFiles = params.getInputFiles();
            for (int i = 0; i < inputFiles.length; i++) {
                File f = inputFiles[i];
                DataEntry rawGaze = FileHandler.buildDataEntry(f);
                DataEntry fixations = DataFilter.filterByFixations(rawGaze);

                String pName = f.getName().replace("_all_gaze.csv", "");
                String pDirectory = params.getOutputDirectory() + "\\" + pName;

                fixations.writeToCSV(pDirectory, pName+"_FixationData"); //Writes filtered data to a new CSV

                ArrayList<List<String>> fixationOutput = generateResults(fixations);
                FileHandler.writeToCSV(fixationOutput, pDirectory, pName);

                generateWindows(rawGaze, pDirectory);
            }

            System.out.println("Analysis Complete.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<List<String>> generateResults(DataEntry data) {
        data = DataFilter.applyScreenSize(data, SCREEN_WIDTH,SCREEN_HEIGHT);
        ArrayList<List<String>> results = new ArrayList<List<String>>();
        results.add(new ArrayList<String>()); //Headers
        results.add(new ArrayList<String>()); //Values

        LinkedHashMap<String,String> fixation = Fixations.analyze(data);
        results.get(0).addAll(fixation.keySet());
        results.get(1).addAll(fixation.values());

        LinkedHashMap<String,String> saccades = Saccades.analyze(data);
        results.get(0).addAll(saccades.keySet());
        results.get(1).addAll(saccades.values());
    
        LinkedHashMap<String,String> angles = Angles.analyze(data);
        results.get(0).addAll(angles.keySet());
        results.get(1).addAll(angles.values());

        LinkedHashMap<String,String> convexHull = ConvexHull.analyze(data);
        results.get(0).addAll(convexHull.keySet());
        results.get(1).addAll(convexHull.values());

        LinkedHashMap<String,String> entropy = GazeEntropy.analyze(data);
        results.get(0).addAll(entropy.keySet());
        results.get(1).addAll(entropy.values());

        LinkedHashMap<String,String> gaze = Gaze.analyze(data);
        results.get(0).addAll(gaze.keySet());
        results.get(1).addAll(gaze.values());

        LinkedHashMap<String,String> event = Event.analyze(data);
        results.get(0).addAll(event.keySet());
        results.get(1).addAll(event.values());


        return results;
    }

    public void generateWindows(DataEntry data, String outputDirectory) {
        WindowSettings settings = params.getWindowSettings();
        int timeIndex = data.getHeaderIndex("TIME");
        ArrayList<List<String>> rawGazeData = data.getAllData();
        
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
                FileHandler.writeToCSV(w, subDirectory, fileName); //Commented these sections out since analyze now takes a DataEntry.
                //generateResults(w, subDirectory, fileName + "_analytics.csv");
                //ArrayList<List<String>> windowOutput = generateResultsOld(w);//, pDirectory);
                //FileHandler.writeToCSV(windowOutput, subDirectory, fileName + "_analytics.csv");
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
