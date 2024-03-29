package analysis;

import java.util.ArrayList;
import java.util.List;

public class Windows {

    final static String TIME_INDEX = "TIME";
    final static int BASELINE_LENGTH = 120;

    public static void generateWindows(DataEntry allGaze, String outputDirectory, WindowSettings settings) {
        DataEntry fixations = DataFilter.filterByFixations(allGaze);

        generateBaselineFile(allGaze, outputDirectory + "/baseline");

        // Tumbling window
        if (settings.tumblingEnabled) {

        }

        // Expanding window
        if (settings.expandingEnabled) {

        }

        // Hopping window
        if (settings.hoppingEnabled) {

        }

        // Event window
        if (settings.eventEnabled) {

        }
    }

    public static void outputWindowFiles(List<DataEntry> windowGaze, List<DataEntry> windowFixations, String outputDirectory) throws Exception {
        if (windowGaze.size() != windowFixations.size()) throw new Exception("Invalid window paramters, please make sure lists are the same size");
    }

    public static void generateBaselineFile(DataEntry allGaze, String outputDirectory) {
        DataEntry baseline = new DataEntry(allGaze.getHeaders());
        double startTime = Double.valueOf(allGaze.getValue(TIME_INDEX, 0));
        double endTime = startTime + BASELINE_LENGTH;

        for (int i = 0; i < allGaze.rowCount(); i++) {
            Double t = Double.parseDouble(allGaze.getValue(TIME_INDEX, i));

            if (t >= endTime) {
                break;
            } else {
                baseline.process(allGaze.getRow(i));
            }
        }

        // Since baseline is continuous, can filter by fixations
        baseline.writeToCSV(outputDirectory, "baseline");
        FileHandler.writeToCSV(Analysis.generateResults(baseline, DataFilter.filterByFixations(baseline)), outputDirectory, "baseline_DGMs");
    }

    // public static void generateWindows(DataEntry data, String outputDirectory, WindowSettings settings) {
    //     int timeIndex = data.getHeaderIndex(TIME_INDEX);
    //     ArrayList<List<String>> dataList = data.getAllData();
    //     List<String> headers = data.getHeaders();

    //     // Create a baseline file
    //     generateBaselineFile(data, outputDirectory + "/baseline");

    //     // Tumbling Window
    //     if (settings.tumblingEnabled) {
    //         ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
    //         DataEntry window = new DataEntry(headers);
    //         double windowSize = settings.tumblingWindowSize;
    //         double start = Double.valueOf(data.getValue(TIME_INDEX, 0));
    //         double end = windowSize;

    //         for (int i = 0; i < dataList.size(); i++) {
    //             List<String> row = dataList.get(i);
    //             Double t = Double.parseDouble(row.get(timeIndex));
                
    //             if (t > end) { 
    //                 end += windowSize;
    //                 windows.add(window);
    //                 window = new DataEntry(headers);
    //                 window.process(row);
    //             } else if (i == dataList.size() - 1) { // Check to see if this is the last row of data in the list, if so append it to the last window
    //                 window.process(row);
    //                 windows.add(window);
    //             } else {
    //                 window.process(row);
    //             }
    //         }

    //         outputWindowFiles(windows, outputDirectory + "/tumbling");
    //     }

    //     // Expanding Window
    //     if (settings.expandingEnabled) {
    //         ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
    //         DataEntry window = new DataEntry(headers);
    //         double windowSize = settings.expandingWindowSize;
    //         double start = Double.valueOf(data.getValue(TIME_INDEX, 0));
    //         double end = start + windowSize;

    //         for (int i = 0; i < dataList.size(); i++) {
    //             List<String> row = dataList.get(i);
    //             double t = Double.parseDouble(row.get(timeIndex));

    //             if (t > end) { 
    //                 end += windowSize;
    //                 windows.add(window);
    //                 window = window.clone();
    //                 window.process(row);
    //             } else if (i == dataList.size() - 1) { // Check to see if this is the last row of data in the list, if so append it to the last window
    //                 window.process(row);
    //                 windows.add(window);
    //             } else {
    //                 window.process(row);
    //             }
    //         }

    //         outputWindowFiles(windows, outputDirectory + "/expanding");
    //     }

    //     // Hopping Window
    //     if (settings.hoppingEnabled) {
    //         ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
    //         DataEntry window = new DataEntry(headers);
    //         int windowSize = settings.hoppingWindowSize;
    //         int hopSize = settings.hoppingHopSize;
    //         double start = Double.valueOf(data.getValue(TIME_INDEX, 0));
    //         double end = start + windowSize;

    //         for (int i = 0; i < dataList.size(); i++) {
    //             List<String> row1 = dataList.get(i);
    //             double t1 = Double.parseDouble(row1.get(timeIndex));
                
    //             if (t1 >= start) {
    //                 for (int j = i; j < dataList.size(); j++) {
    //                     List<String> row2 = dataList.get(j);
    //                     double t2 = Double.parseDouble(row2.get(timeIndex));

    //                     if (t2 >= end || j == dataList.size()) {
    //                         window.process(row2);
    //                         windows.add(window);

    //                         start += hopSize;
    //                         end = start + windowSize;
    //                         window = new DataEntry(headers);

    //                         break;
    //                     } else {
    //                         window.process(row2);
    //                     }
    //                 }
    //             }
    //         }

    //         outputWindowFiles(windows, outputDirectory + "/hopping");
    //     }

    //     // Event-based Window
    //     if (settings.eventEnabled) {
    //         String event = settings.event;
    //         double timeout = settings.eventTimeout;

    //         for (int i = 0; i < dataList.size(); i++) {
    //             List<String> row = dataList.get(i);
    //         }
    //     }
    // }

    // public static void outputWindowFiles(ArrayList<DataEntry> windows, String outputDirectory) {
    //     int windowCount = 1;
    //     for (DataEntry w : windows) {
    //         String fileName = "window" + windowCount;
    //         w.writeToCSV(outputDirectory, fileName);
    //         ArrayList<List<String>> results = Analysis.generateResults(w);
    //         FileHandler.writeToCSV(results, outputDirectory, fileName + "_analytics");
    //         windowCount++;
    //     }
    // }

    // public static void generateBaselineFile(DataEntry data, String outputDirectory) {
    //     DataEntry baseline = new DataEntry(data.getHeaders());
    //     double startTime = Double.valueOf(data.getValue(TIME_INDEX, 0));
    //     double endTime = startTime + BASELINE_LENGTH;

    //     for (int i = 0; i < data.rowCount(); i++) {
    //         Double t = Double.parseDouble(data.getValue(TIME_INDEX, i));

    //         if (t >= endTime) {
    //             break;
    //         } else {
    //             baseline.process(data.getRow(i));
    //         }
    //     }

    //     baseline.writeToCSV(outputDirectory, "baseline");
    //     FileHandler.writeToCSV(Analysis.generateResults(baseline), outputDirectory, "baseline_DGMs");
    // }
}
