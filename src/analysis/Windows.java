package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Windows {

    final static String TIME_INDEX = "TIME";
    final static int BASELINE_LENGTH = 120;

    // Set of supported events that utilize the fixation file
    final static Set<String> fixationEvents = new HashSet<String>(
        Arrays.asList(
        "FPOGD",
        "SACCADE_MAG",
        "SACCADE_DIR" 
    ));

    // Set of supported events that utilize the allGaze file
    final static Set<String> allGazeEvents = new HashSet<String>(
        Arrays.asList(
        "LPMM",
        "RPMM",
        "BKPMIN",
        "LPMM + RPMM"
    ));

    public static void generateWindows(DataEntry allGaze, String outputDirectory, WindowSettings settings) {
        List<String> headers = allGaze.getHeaders();
        double t0 = Double.valueOf(allGaze.getValue(TIME_INDEX, 0));

        // Generate baseline file
        generateBaselineFile(allGaze, outputDirectory + "/baseline");

        // Tumbling Window
        if (settings.tumblingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            double windowSize = settings.tumblingWindowSize;
            double start = t0;
            double end = start + windowSize;

            for (int i = 0; i < allGaze.rowCount(); i++) {
                List<String> currRow = allGaze.getRow(i);
                Double t = Double.valueOf(allGaze.getValue(TIME_INDEX, i));
                
                if (t > end) { 
                    end += windowSize;
                    windows.add(window);
                    window = new DataEntry(headers);
                    window.process(currRow);
                } else if (i == allGaze.rowCount() - 1) { // Check to see if this is the last row of data in the list, if so append it to the last window
                    window.process(currRow);
                    windows.add(window);
                } else {
                    window.process(currRow);
                }
            }

            outputWindowFiles(windows, t0, outputDirectory + "/tumbling");
        }

        // Expanding Window
        if (settings.expandingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            double windowSize = settings.expandingWindowSize;
            double start = t0;
            double end = start + windowSize;

            for (int i = 0; i < allGaze.rowCount(); i++) {
                List<String> currRow = allGaze.getRow(i);
                Double t = Double.valueOf(allGaze.getValue(TIME_INDEX, i));

                if (t > end) { 
                    end += windowSize;
                    windows.add(window);
                    window = window.clone();
                    window.process(currRow);
                } else if (i == allGaze.rowCount() - 1) { // Check to see if this is the last row of data in the list, if so append it to the last window
                    window.process(currRow);
                    windows.add(window);
                } else {
                    window.process(currRow);
                }
            }

            outputWindowFiles(windows, t0, outputDirectory + "/expanding");
        }

        // Hopping Window
        if (settings.hoppingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            double windowSize = settings.hoppingWindowSize;
            double hopSize = settings.hoppingHopSize;
            double start = t0;
            double end = start + windowSize;

            for (int i = 0; i < allGaze.rowCount(); i++) {
                double t1 = Double.parseDouble(allGaze.getValue(TIME_INDEX, i));
                
                if (t1 >= start) {
                    for (int j = i; j < allGaze.rowCount(); j++) {
                        List<String> row2 = allGaze.getRow(j);
                        double t2 = Double.parseDouble(allGaze.getValue(TIME_INDEX, j));

                        if (t2 >= end || j == allGaze.rowCount() - 1) {
                            window.process(row2);
                            windows.add(window);

                            start += hopSize;
                            end = start + windowSize;
                            window = new DataEntry(headers);

                            break;
                        } else {
                            window.process(row2);
                        }
                    }
                }
            }

            outputWindowFiles(windows, t0, outputDirectory + "/hopping");
        }

        // Event Window
        if (settings.eventEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            boolean isEventWindow = false;

            String event = settings.event;
            double timeoutLength = settings.eventTimeout;
            double maxDuration = settings.eventMaxDuration;
            double eventEnd = 0;

            double baselineValue = getEventBaselineValue(outputDirectory, event);

            for (int i = 0; i < allGaze.rowCount(); i++) {
                Double t = Double.valueOf(allGaze.getValue(TIME_INDEX, i));
                Double windowValue =  getEventWindowValue(allGaze, event, i);

                // Get the initial timestamp
                if (i == 0) {
                    t0 = t;
                }

                if (windowValue > baselineValue) {
                    if (!isEventWindow) maxDuration = t + settings.eventMaxDuration;
                    isEventWindow = true;
                    eventEnd = t + timeoutLength;
                }

                if (isEventWindow) {
                    if (t > eventEnd || t > maxDuration || i == allGaze.rowCount() - 1) {
                        windows.add(window);
                        window = new DataEntry(headers);
                        isEventWindow = false;
                    } else {
                        window.process(allGaze.getRow(i));
                    }
                }
            }

            outputWindowFiles(windows, t0, outputDirectory + "/event");
        }
    }

    public static void outputWindowFiles(ArrayList<DataEntry> windows, double t0, String outputDirectory) {
        int windowCount = 1;
        List<List<String>> allWindowDGMs = new ArrayList<List<String>>();
        for (DataEntry w : windows) {
            String fileName = "window" + windowCount;
            String windowDirectory = outputDirectory + "/" + fileName;

            w.writeToCSV(windowDirectory, fileName);

            // windows are continuous and raw, therefore fixation filtering will be valid
            ArrayList<List<String>> results = Analysis.generateResults(w, DataFilter.filterByFixations(w));

            // Calculate beginning time stamp, ending timestamp, window duration, initial/final seconds elapsed since window start
            double t1 = Double.parseDouble(w.getValue(TIME_INDEX, 0));
            double t2 = Double.parseDouble(w.getValue(TIME_INDEX, w.rowCount() - 1));
            double windowDuration = t2 - t1;
            double initialDuration = t1 - t0;
            double finalDuration = t2 - t0;

            List<String> headers = results.get(0);
            headers.add("beginning_timestamp");
            headers.add("ending_timestamp");
            headers.add("window_duration");
            headers.add("initial_seconds_elapsed_since_start");
            headers.add("final_seconds_elapsed_since_start");
            
            List<String> dgms = results.get(1);
            dgms.add(String.valueOf(t1));
            dgms.add(String.valueOf(t2));
            dgms.add(String.valueOf(windowDuration));
            dgms.add(String.valueOf(initialDuration));
            dgms.add(String.valueOf(finalDuration));
            allWindowDGMs.add(dgms);

            // In the combined window folder, add headers if there are none
            if (allWindowDGMs.size() == 0) {
                allWindowDGMs.add(headers);
            }
            
            FileHandler.writeToCSV(results, windowDirectory, fileName + "_DGMs");
            AreaOfInterests.generateAOIs(w, windowDirectory, fileName);
            
            windowCount++;
        }

        FileHandler.writeToCSV(allWindowDGMs, outputDirectory, "all_window_DGMs");
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

    public static double getRawEventBaselineValue(String fileDirectory, String event) {
        double eventValue = 0;

        File baselineFile = new File(fileDirectory + "/baseline/baseline.csv");
        DataEntry baseline = FileHandler.buildDataEntry(baselineFile);
        baseline = fixationEvents.contains(event) ? DataFilter.filterByFixations(baseline) : baseline; // Determine if we need to filter by fiaxtions
        baseline = DataFilter.filterByValidity(baseline); // Filter by validity
        
        for (int i = 0; i < baseline.rowCount(); i++) {
            eventValue += Double.parseDouble(baseline.getValue(event, i));
        }

        eventValue /= baseline.rowCount();
        
        return eventValue;
    }

    public static double getAveragePupilDilationBaseline(String fileDirectory) {
        double eventValue = 0;

        File baselineFile = new File(fileDirectory + "/baseline/baseline.csv");
        DataEntry baseline = FileHandler.buildDataEntry(baselineFile);
        baseline = DataFilter.filterByValidity(baseline); // Filter by validity
        
        for (int i = 0; i < baseline.rowCount(); i++) {
            double left = Double.parseDouble(baseline.getValue("LPMM", i));
            double right = Double.parseDouble(baseline.getValue("RPMM", i));
            eventValue += ((left + right) / 2);
        }

        eventValue /= baseline.rowCount();
        
        return eventValue;
    }

    public static double getEventBaselineValue(String fileDirectory, String event) {
        switch(event) {
            case "LPMM + RPMM":
                return getAveragePupilDilationBaseline(fileDirectory);
            default:
                return getRawEventBaselineValue(fileDirectory, event);
        }
    }

    public static double getEventWindowValue(DataEntry d, String event, int row) {
        switch (event) {
            case "LPMM + RPMM":
                double left = Double.parseDouble(d.getValue("LPMM", row));
                double right = Double.parseDouble(d.getValue("RPMM", row));
                return (left + right) / 2;
            default:
                return Double.parseDouble(d.getValue(event, row));
        }
    }
}
