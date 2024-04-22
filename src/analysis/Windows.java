package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Windows {

    final static String TIME_INDEX = "TIME";
    final static int BASELINE_LENGTH = 120;

    // Set of supported events that utilize the fixation file
    final static Set<String> fixationEvents = new HashSet<String>(
        Arrays.asList(
        "FPOGD",
        "SACCADE_MAG",
        "SACCADE_DUR" 
    ));

    // Set of supported events that utilize the allGaze file
    final static Set<String> allGazeEvents = new HashSet<String>(
        Arrays.asList(
        "LPMM",
        "RPMM",
        "BKPMIN"
    ));

    public static void generateWindows(DataEntry allGaze, String outputDirectory, WindowSettings settings) {
        List<String> headers = allGaze.getHeaders();

        // Generate baseline file
        generateBaselineFile(allGaze, outputDirectory + "/baseline");

        // Tumbling Window
        if (settings.tumblingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            double windowSize = settings.tumblingWindowSize;
            double start = Double.valueOf(allGaze.getValue(TIME_INDEX, 0));
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

            outputWindowFiles(windows, outputDirectory + "/tumbling");
        }

        // Expanding Window
        if (settings.expandingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            double windowSize = settings.expandingWindowSize;
            double start = Double.valueOf(allGaze.getValue(TIME_INDEX, 0));
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

            outputWindowFiles(windows, outputDirectory + "/expanding");
        }

        // Hopping Window
        if (settings.hoppingEnabled) {
            ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
            DataEntry window = new DataEntry(headers);
            double windowSize = settings.hoppingWindowSize;
            double hopSize = settings.hoppingHopSize;
            double start = Double.valueOf(allGaze.getValue(TIME_INDEX, 0));
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

            outputWindowFiles(windows, outputDirectory + "/hopping");
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
                Double windowValue =  Double.parseDouble(allGaze.getValue(event, i));

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

            outputWindowFiles(windows, outputDirectory + "/event");
        }
    }

    public static void generateWindowsForBatch(Map<String, DataEntry> allGazes, String outputDirectory, WindowSettings settings) {
        if (settings.eventThresholdEnabled) {
            String event = settings.thresholdEvent;
            double windowSize = settings.eventWindowSize;
            int threshold = settings.eventThreshold;

            LinkedHashMap<Integer, List<DataEntry>> windowMap = new LinkedHashMap<Integer, List<DataEntry>>();
            
            for (String pName: allGazes.keySet()) {
                DataEntry d = allGazes.get(pName);
                DataEntry window = new DataEntry(d.getHeaders());
                Integer windowCount = 0;
                
                double baselineValue = getEventBaselineValue(outputDirectory + "/" + pName, event);

                double start = Double.valueOf(d.getValue(TIME_INDEX, 0));
                double end = start + windowSize;
                boolean isEvent = false;

                for (int i = 0; i < d.rowCount(); i++) {
                    List<String> currRow = d.getRow(i);
                    Double t = Double.valueOf(d.getValue(TIME_INDEX, i));
                    Double windowValue =  Double.parseDouble(d.getValue(event, i));

                    if (windowValue > baselineValue) isEvent = true;

                    if (t >= end || i == d.rowCount() - 1) { 
                        if (isEvent) {
                            if (!windowMap.containsKey(windowCount)) windowMap.put(windowCount, new ArrayList<DataEntry>());
                            List<DataEntry> list = windowMap.get(windowCount);
                            list.add(window);
                        }

                        end += windowSize;
                        windowCount++;
                        isEvent = false;
                        window = new DataEntry(window.getHeaders());
                        window.process(currRow);
                    } else {
                        window.process(currRow);
                    }
                }
            }

            String directory = outputDirectory + "/eventDGMs";
            for (Integer windowID : windowMap.keySet()) {
                List<DataEntry> list = windowMap.get(windowID);

                System.out.println(list.size());
                System.out.print(threshold);

                if (list.size() >= threshold) {
                    List<List<String>> allParticipantDGMs = new ArrayList<List<String>>();

                    for (DataEntry d : list) {
                        List<List<String>> dgms = Analysis.generateResults(d, DataFilter.filterByFixations(d));
                        if (allParticipantDGMs.size() == 0) allParticipantDGMs.add(dgms.get(0));
                        allParticipantDGMs.add(dgms.get(1));
                    }

                    FileHandler.writeToCSV(allParticipantDGMs, directory, "window" + windowID);
                }
            }
        }
    }

    public static void outputWindowFiles(ArrayList<DataEntry> windows, String outputDirectory) {
        int windowCount = 1;
        for (DataEntry w : windows) {
            String fileName = "window" + windowCount;
            w.writeToCSV(outputDirectory, fileName);
            ArrayList<List<String>> results = Analysis.generateResults(w, DataFilter.filterByFixations(w)); // windows are continuous and raw, therefore fixation filtering will be valid
            FileHandler.writeToCSV(results, outputDirectory, fileName + "_DGMs");
            windowCount++;
        }
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

    public static double getEventBaselineValue(String fileDirectory, String event) {
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
}
