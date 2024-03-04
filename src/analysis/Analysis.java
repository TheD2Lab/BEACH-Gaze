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
    final static int SCREEN_WIDTH = 1920;
	final static int SCREEN_HEIGHT = 1080;

    final static String TIME_INDEX = "TIME";

    private Parameters params;
    
    public Analysis(Parameters params) {
        this.params = params;
    }

    public boolean run() {
        try {
            File[] inputFiles = params.getInputFiles();
            for (int i = 0; i < inputFiles.length; i++) {
                File f = inputFiles[i];

                String pName = f.getName().replace("_all_gaze.csv", "");
                String pDirectory = params.getOutputDirectory() + "/" + pName;

                DataEntry rawGaze = FileHandler.buildDataEntry(f);
                DataEntry fixations = DataFilter.filterByFixations(rawGaze);
                DataEntry validGaze = DataFilter.filterByValidity(rawGaze);
                DataEntry validFixations = DataFilter.filterByValidity(fixations);

                fixations.writeToCSV(pDirectory, pName+"_FixationData"); //Writes filtered data to a new CSV

                ArrayList<List<String>> analytics = generateResults(validFixations);
                FileHandler.writeToCSV(analytics, pDirectory, pName + "_analytics");

                generateWindows(validGaze, pDirectory);
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
        int timeIndex = data.getHeaderIndex(TIME_INDEX);
        ArrayList<List<String>> rawGazeData = data.getAllData();
        List<String> headers = data.getHeaders();

        // Tumbling Window
        if (settings.tumblingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            int windowSize = settings.tumblingWindowSize;
            int end = windowSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
                Double t = Double.parseDouble(row.get(timeIndex));
                
                if (t > end) { 
                    end += windowSize;
                    windows.add(window);
                    window = new DataEntry(headers);
                    window.process(row);
                } else if (i == rawGazeData.size() - 1) { // Check to see if this is the last row of data in the list, if so append it to the last window
                    window.process(row);
                    windows.add(window);
                } else {
                    window.process(row);
                }
            }

            String subDirectory = outputDirectory + "/tumbling";
            int windowCount = 1;
            for (DataEntry w : windows) {
                String fileName = "window" + windowCount;
                w.writeToCSV(subDirectory, fileName);
                //DataEntry fixations = DataFilter.filterByFixations(w);
                //FileHandler.writeToCSV(generateResults(fixations), subDirectory, fileName + "_analytics");
                windowCount++;
            }
        }

        // Expanding Window
        if (settings.expandingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            int windowSize = settings.expandingWindowSize;
            int end = windowSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
                Double t = Double.parseDouble(row.get(timeIndex));

                if (t > end) { 
                    end += windowSize;
                    windows.add(window);
                    window = window.clone();
                    window.process(row);
                } else if (i == rawGazeData.size() - 1) { // Check to see if this is the last row of data in the list, if so append it to the last window
                    window.process(row);
                    windows.add(window);
                } else {
                    window.process(row);
                }
            }

            String subDirectory = outputDirectory + "/expanding";
            int windowCount = 1;
            for (DataEntry w : windows) {
                String fileName = "window" + windowCount;
                w.writeToCSV(subDirectory, fileName);
                //DataEntry fixations = DataFilter.filterByFixations(w);
                //FileHandler.writeToCSV(generateResults(fixations), subDirectory, fileName + "_analytics");
                windowCount++;
            }
        }

        // Hopping Window
        if (settings.hoppingEnabled) {
            int windowSize = settings.hoppingWindowSize;
            int hopSize = settings.hoppingHopSize;

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
            }
        }

        // Event-based Window
        if (settings.eventEnabled) {

            for (int i = 0; i < rawGazeData.size(); i++) {
                List<String> row = rawGazeData.get(i);
            }
        }

    }
}
