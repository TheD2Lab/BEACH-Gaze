package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
                DataEntry validGaze = DataFilter.filterByValidity(rawGaze);
                DataEntry fixations = DataFilter.filterByFixations(validGaze);
                
                validGaze.writeToCSV(pDirectory, pName + "_cleansed");
                fixations.writeToCSV(pDirectory, pName + "_fixations");

                ArrayList<List<String>> analytics = generateResults(validGaze);
                FileHandler.writeToCSV(analytics, pDirectory, pName + "_analytics");

                generateWindows(validGaze, pDirectory);
                generateAOIs(validGaze, pDirectory, pName);
            }

            System.out.println("Analysis Complete.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<List<String>> generateResults(DataEntry data) {
        DataEntry allGaze = DataFilter.applyScreenSize(data, SCREEN_WIDTH, SCREEN_HEIGHT);
        DataEntry fixations = DataFilter.filterByFixations(allGaze);

        ArrayList<List<String>> results = new ArrayList<List<String>>();
        results.add(new ArrayList<String>()); //Headers
        results.add(new ArrayList<String>()); //Values

        LinkedHashMap<String,String> fixation = Fixations.analyze(fixations);
        results.get(0).addAll(fixation.keySet());
        results.get(1).addAll(fixation.values());

        LinkedHashMap<String,String> saccades = Saccades.analyze(fixations);
        results.get(0).addAll(saccades.keySet());
        results.get(1).addAll(saccades.values());

        LinkedHashMap<String, String> saccadeVelocity = SaccadeVelocity.analyze(allGaze);
        results.get(0).addAll(saccadeVelocity.keySet());
        results.get(1).addAll(saccadeVelocity.values());
    
        LinkedHashMap<String,String> angles = Angles.analyze(fixations);
        results.get(0).addAll(angles.keySet());
        results.get(1).addAll(angles.values());

        LinkedHashMap<String,String> convexHull = ConvexHull.analyze(fixations);
        results.get(0).addAll(convexHull.keySet());
        results.get(1).addAll(convexHull.values());

        LinkedHashMap<String,String> entropy = GazeEntropy.analyze(fixations);
        results.get(0).addAll(entropy.keySet());
        results.get(1).addAll(entropy.values());

        LinkedHashMap<String,String> gaze = Gaze.analyze(fixations);
        results.get(0).addAll(gaze.keySet());
        results.get(1).addAll(gaze.values());

        LinkedHashMap<String,String> event = Event.analyze(fixations);
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

    public void generateAOIs(DataEntry data, String outputDirectory, String fileName) {
        System.out.println("Building AOIs");
        LinkedHashMap<String, DataEntry> aoiMetrics = new LinkedHashMap<>();
        for (int i = 0; i < data.rowCount(); i++) {
            String aoi = data.getValue("AOI", i);
            if (!aoiMetrics.containsKey(aoi)) {
                DataEntry d = new DataEntry(data.getHeaders());
                aoiMetrics.put(aoi, d);
                System.out.println("New AOI found: "+aoi);
            }
            aoiMetrics.get(aoi).process(data.getRow(i));
        }
        
        // printing the elements of LinkedHashMap
        ArrayList<List<String>> metrics = new ArrayList<>();
        metrics.add(new ArrayList<String>());

        boolean isFirst = true;
        for (String key : aoiMetrics.keySet()) {
            DataEntry d = aoiMetrics.get(key);
            System.out.println("Analyzing: "+key +", rows: "+d.rowCount());
            ArrayList<List<String>> results = generateResults(d);
            results.get(1).add(0,key);
            if (isFirst) {
                isFirst = false;
                List<String> headers = results.get(0);
                headers.add(0, "AOI");
                metrics.get(0).addAll(headers);
            }
            metrics.add(results.get(1));
        }
        FileHandler.writeToCSV(metrics, outputDirectory, fileName + "_AOI_Metrics");
    }
}
