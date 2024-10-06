package com.github.thed2lab.analysis;

import static com.github.thed2lab.analysis.Constants.TIMESTAMP;
import static com.github.thed2lab.analysis.Constants.BLINK_RATE;
import static com.github.thed2lab.analysis.Constants.FIXATION_DURATION;
import static com.github.thed2lab.analysis.Constants.LEFT_PUPIL_DIAMETER;
import static com.github.thed2lab.analysis.Constants.RIGHT_PUPIL_DIAMETER;
import static com.github.thed2lab.analysis.Constants.SACCADE_DIR;
import static com.github.thed2lab.analysis.Constants.SACCADE_MAGNITUDE;
import static com.github.thed2lab.analysis.Constants.SCREEN_HEIGHT;
import static com.github.thed2lab.analysis.Constants.SCREEN_WIDTH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Windows {

    private final static String LEFT_RIGHT_DIAMETER = LEFT_PUPIL_DIAMETER + " + " + RIGHT_PUPIL_DIAMETER;

    // Set of supported events that utilize the fixation file
    final static Set<String> fixationEvents = new HashSet<String>(
        Arrays.asList(
        FIXATION_DURATION,
        SACCADE_MAGNITUDE,
        SACCADE_DIR
    ));

    // Set of supported events that utilize the allGaze file
    final static Set<String> allGazeEvents = new HashSet<String>(
        Arrays.asList(
        LEFT_PUPIL_DIAMETER,
        RIGHT_PUPIL_DIAMETER,
        BLINK_RATE,
        LEFT_RIGHT_DIAMETER
    ));

    public static void generateWindows(DataEntry allGaze, String outputDirectory, WindowSettings settings) {
        double time0 = Double.valueOf(allGaze.getValue(TIMESTAMP, 0));

        // Generate baseline file
        DataEntry baselineData = generateBaselineFiles(allGaze, outputDirectory + "/baseline", settings.eventBaselineDuration);

        // Tumbling Window
        if (settings.tumblingEnabled) {
            List<DataEntry> windows = spliceTumblingWindow(allGaze, settings.tumblingWindowSize);
            outputWindowFiles(windows, time0, outputDirectory + "/tumbling");
        }

        // Expanding Window
        if (settings.expandingEnabled) {
            List<DataEntry> windows = spliceExpandingWindow(allGaze, settings.expandingWindowSize);
            outputWindowFiles(windows, time0, outputDirectory + "/expanding");
        }

        // Hopping Window
        if (settings.hoppingEnabled) {
            List<DataEntry> windows = spliceHoppingWindow(allGaze, settings.hoppingWindowSize, settings.hoppingHopSize);
            outputWindowFiles(windows, time0, outputDirectory + "/hopping");
        }

        // Event Window
        if (settings.eventEnabled) {
            double baselineValue = getEventBaselineValue(baselineData, settings.event);
            List<DataEntry> windows  = spliceEventWindow(allGaze, settings, baselineValue);
            outputWindowFiles(windows, time0, outputDirectory + "/event");
        }
    }

    /**
     * Splices the all gaze data using the tumbling window pattern.
     * @param allGaze the gaze data to be spliced.
     * @param windowSize the size of the windows.
     * @return the gaze data split into windows.
     */
    static List<DataEntry> spliceTumblingWindow(DataEntry allGaze, double windowSize) {
        List<String> headers = allGaze.getHeaders();
        ArrayList<DataEntry> windows = new ArrayList<>();
        DataEntry window = new DataEntry(headers);
        double start = Double.valueOf(allGaze.getValue(TIMESTAMP, 0));
        double end = start + windowSize;

        for (int i = 0; i < allGaze.rowCount(); i++) {
            List<String> currRow = allGaze.getRow(i);
            Double t = Double.valueOf(allGaze.getValue(TIMESTAMP, i));
            
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
        return windows;
    }

    /**
     * Splices the all gaze data using the expanding window pattern.
     * @param allGaze the gaze data to be spliced.
     * @param windowSize the window increment size.
     * @return the gaze data split into windows.
     */
    static List<DataEntry> spliceExpandingWindow(DataEntry allGaze, double windowSize) {
        List<String> headers = allGaze.getHeaders();
        ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
        DataEntry window = new DataEntry(headers);
        double start = Double.valueOf(allGaze.getValue(TIMESTAMP, 0));
        double end = start + windowSize;

        for (int i = 0; i < allGaze.rowCount(); i++) {
            List<String> currRow = allGaze.getRow(i);
            Double t = Double.valueOf(allGaze.getValue(TIMESTAMP, i));

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
        return windows;
    }

    /**
     * Splices the all gaze data using the hopping window pattern.
     * @param allGaze the gaze data to be spliced.
     * @param windowSize the size of hopping windows.
     * @param hopSize the hop distance of a hopping window.
     * @return the gaze data split into windows.
     */
    static List<DataEntry> spliceHoppingWindow(DataEntry allGaze, double windowSize, double hopSize) {
        List<String> headers = allGaze.getHeaders();
        ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
        DataEntry window = new DataEntry(headers);
        double start = Double.valueOf(allGaze.getValue(TIMESTAMP, 0));
        double end = start + windowSize;

        for (int i = 0; i < allGaze.rowCount(); i++) {
            double time1 = Double.parseDouble(allGaze.getValue(TIMESTAMP, i));
            
            if (time1 >= start) {
                for (int j = i; j < allGaze.rowCount(); j++) {
                    List<String> row2 = allGaze.getRow(j);
                    double time2 = Double.parseDouble(allGaze.getValue(TIMESTAMP, j));

                    if (time2 > end) { // don't include row2
                        windows.add(window);
                        start += hopSize;
                        end = start + windowSize;
                        window = new DataEntry(headers);
                        break;
                    } else if (j == allGaze.rowCount() - 1) {   // include row
                        window.process(row2);
                        windows.add(window);
                        start += hopSize;
                        end = start + windowSize;
                        window = new DataEntry(headers);
                    } else {
                        window.process(row2);
                    }
                }
            }
        }
        return windows;
    }

    /**
     * Splices the all gaze data using the event window pattern.
     * @param allGaze the gaze data to be spliced.
     * @param settings window settings.
     * @param baseDirectory the directory containing baseline data.
     * @return the gaze data split into windows.
     */
    static List<DataEntry> spliceEventWindow(DataEntry allGaze, WindowSettings settings, double baselineValue) {
        List<String> headers = allGaze.getHeaders();
        ArrayList<DataEntry> windows = new ArrayList<DataEntry>();
        DataEntry window = new DataEntry(headers);
        boolean isEventWindow = false;

        String event = settings.event;
        double timeoutLength = settings.eventTimeout;
        double maxDuration = settings.eventMaxDuration;
        double eventEnd = 0;

        for (int i = 0; i < allGaze.rowCount(); i++) {
            double time = Double.valueOf(allGaze.getValue(TIMESTAMP, i));
            double windowValue =  getEventWindowValue(allGaze, event, i);

            if (windowValue > baselineValue) {
                if (!isEventWindow) { 
                    maxDuration = time + settings.eventMaxDuration;
                    isEventWindow = true;
                }
                eventEnd = time + timeoutLength;
            }

            if (isEventWindow) {
                if (time > eventEnd || time > maxDuration) {
                    windows.add(window);
                    window = new DataEntry(headers);
                    // check if the current line is an event
                    if (windowValue > baselineValue) {
                        window.process(allGaze.getRow(i));
                        maxDuration = time + settings.eventMaxDuration;
                        eventEnd = time + timeoutLength;
                    } else {
                        isEventWindow = false;
                    }
                } else {
                    window.process(allGaze.getRow(i));
                }
            }
        }

        if (window.rowCount() > 0) {    // add the last window
            windows.add(window);
        }
        return windows;
    }

    static void outputWindowFiles(List<DataEntry> windows, double time0, String outputDirectory) {
        int windowCount = 1;
        List<List<String>> allWindowDGMs = new ArrayList<List<String>>();
        for (DataEntry windowGaze : windows) {
            String fileName = "window" + windowCount;
            String windowDirectory = outputDirectory + "/" + fileName;

            windowGaze.writeToCSV(windowDirectory, fileName);

            // windows are continuous and raw, therefore fixation filtering will be valid
            DataEntry windowFixations = DataFilter.filterByFixations(windowGaze);
            List<List<String>> results = Analysis.generateResults(windowGaze, windowFixations);

            // Calculate beginning time stamp, ending timestamp, window duration, initial/final seconds elapsed since window start
            double time1 = Double.parseDouble(windowGaze.getValue(TIMESTAMP, 0));
            double time2 = Double.parseDouble(windowGaze.getValue(TIMESTAMP, windowGaze.rowCount() - 1));
            double windowDuration = time2 - time1;
            double initialDuration = time1 - time0;
            double finalDuration = time2 - time0;

            List<String> headers = results.get(0);
            headers.add("beginning_timestamp");
            headers.add("ending_timestamp");
            headers.add("window_duration");
            headers.add("initial_seconds_elapsed_since_start");
            headers.add("final_seconds_elapsed_since_start");

            // In the combined window folder, add headers if there are none
            if (allWindowDGMs.size() == 0) {
                allWindowDGMs.add(headers);
            }
            
            List<String> dgms = results.get(1);
            dgms.add(String.valueOf(time1));
            dgms.add(String.valueOf(time2));
            dgms.add(String.valueOf(windowDuration));
            dgms.add(String.valueOf(initialDuration));
            dgms.add(String.valueOf(finalDuration));
            allWindowDGMs.add(dgms);
            
            FileHandler.writeToCSV(results, windowDirectory, fileName + "_DGMs");
            AreaOfInterests.generateAOIs(windowGaze, windowFixations, windowDirectory, fileName);
            windowCount++;
        }

        FileHandler.writeToCSV(allWindowDGMs, outputDirectory, "all_window_DGMs");
    }

    static DataEntry generateBaselineFiles(DataEntry allGaze, String outputDirectory, double baselineDuration) {
        DataEntry baseline = new DataEntry(allGaze.getHeaders());
        double startTime = Double.valueOf(allGaze.getValue(TIMESTAMP, 0));
        double endTime = startTime + baselineDuration;

        for (int i = 0; i < allGaze.rowCount(); i++) {
            Double time = Double.parseDouble(allGaze.getValue(TIMESTAMP, i));

            if (time >= endTime) {
                break;
            } else {
                baseline.process(allGaze.getRow(i));
            }
        }

        // Since baseline is continuous, can filter by fixations
        baseline.writeToCSV(outputDirectory, "baseline");
        FileHandler.writeToCSV(Analysis.generateResults(baseline, DataFilter.filterByFixations(baseline)), outputDirectory, "baseline_DGMs");
        return baseline;
    }

    static double getRawEventBaselineValue(DataEntry baselineData, String event) {
        double eventValue = 0;

        baselineData = fixationEvents.contains(event) ? DataFilter.filterByFixations(baselineData) : baselineData; // Determine if we need to filter by fixations
        baselineData = DataFilter.filterByValidity(baselineData); // Filter by validity
        
        for (int i = 0; i < baselineData.rowCount(); i++) {
            eventValue += Double.parseDouble(baselineData.getValue(event, i));
        }

        eventValue /= baselineData.rowCount();
        
        return eventValue;
    }

    static double getAveragePupilDilationBaseline(DataEntry baselineData) {
        double eventValue = 0;
        baselineData = DataFilter.filterByValidity(baselineData); // Filter by validity
        
        for (int i = 0; i < baselineData.rowCount(); i++) {
            double left = Double.parseDouble(baselineData.getValue(LEFT_PUPIL_DIAMETER, i));
            double right = Double.parseDouble(baselineData.getValue(RIGHT_PUPIL_DIAMETER, i));
            eventValue += ((left + right) / 2);
        }

        eventValue /= baselineData.rowCount();
        
        return eventValue;
    }

    static double getEventBaselineValue(DataEntry baselineData, String event) {
        switch(event) {
            case LEFT_RIGHT_DIAMETER:
                return getAveragePupilDilationBaseline(baselineData);
            default:
                return getRawEventBaselineValue(baselineData, event);
        }
    }

    static double getEventWindowValue(DataEntry d, String event, int row) {
        switch (event) {
            case LEFT_RIGHT_DIAMETER:
                double left = Double.parseDouble(d.getValue(LEFT_PUPIL_DIAMETER, row));
                double right = Double.parseDouble(d.getValue(RIGHT_PUPIL_DIAMETER, row));
                return (left + right) / 2;
            default:
                return Double.parseDouble(d.getValue(event, row));
        }
    }
}
