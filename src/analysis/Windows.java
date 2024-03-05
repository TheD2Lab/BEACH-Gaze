package analysis;

import java.util.ArrayList;
import java.util.List;

public class Windows {

    final static String TIME_INDEX = "TIME";

    public static void generateWindows(DataEntry data, String outputDirectory, WindowSettings settings) {
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

            outputWindowFiles(windows, outputDirectory + "/tumbling");
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

            outputWindowFiles(windows, outputDirectory + "/expanding");
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

    public static void outputWindowFiles(ArrayList<DataEntry> windows, String outputDirectory) {
        int windowCount = 1;
        for (DataEntry w : windows) {
            String fileName = "window" + windowCount;
            w.writeToCSV(outputDirectory, fileName);
            ArrayList<List<String>> results = Analysis.generateResults(w);
            FileHandler.writeToCSV(results, outputDirectory, fileName + "_analytics");
            windowCount++;
        }
    }
}
