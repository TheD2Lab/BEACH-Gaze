package com.github.thed2lab.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Analysis {
    /**
     * The analysis class drives the entire analysis on one or multiple files of gaze data.
     * Its role is to iterate over each file, and process it into DataEntry objects for gaze,
     * validity, and fixations.
     */
    private final static int SCREEN_WIDTH = 1920;
	private final static int SCREEN_HEIGHT = 1080;

    private final static int MIN_PATTERN_LENGTH = 3;
    private final static int MAX_PATTERN_LENGTH = 7;
    private final static int MIN_PATTERN_FREQUENCY = 2;
    private final static int MIN_SEQUENCE_SIZE = 3;

    private Parameters params;
    
    /**
     * Used to construct a single Analysis object.
     * @param params information about the files to analyze and the types of analysis run.
     */
    public Analysis(Parameters params) {
        this.params = params;
    }

    /**
     * Runs all data analysis and writes the results to files. The method iterates over all the provided files,
     * create DataEntry objects to represent them, and call methods to analyze those DataEntry objects.
     * Finally, the results of these methods are output to CSV files.
     * @return {@code Boolean} indicating if the run was successful.
     */
    public boolean run() {
        try {
            File[] inputFiles = params.getInputFiles();
            List<String> sequences = new ArrayList<String>();
            List<List<String>> allParticipantDGMs = new ArrayList<List<String>>();
            LinkedHashMap<String, Integer> aoiMap = new LinkedHashMap<String, Integer>();

            WindowSettings settings = params.getWindowSettings();

            for (int i = 0; i < inputFiles.length; i++) {
                File f = inputFiles[i];

                String pName = f.getName().replace("_all_gaze", "").replace(".csv", "");
                String pDirectory = params.getOutputDirectory() + "/" + pName;

                System.out.println("Analyzing " + pName);

                // Build DataEntrys
                DataEntry allGaze = FileHandler.buildDataEntry(f);
                DataEntry validGaze = DataFilter.filterByValidity(allGaze);
                DataEntry fixations = DataFilter.filterByFixations(allGaze);
                DataEntry validFixations = DataFilter.filterByValidity(fixations);
                
                // Write DataEntrys to file
                validGaze.writeToCSV(pDirectory, pName + "_valid_all_gaze");
                validFixations.writeToCSV(pDirectory, pName + "_valid_fixations");
                fixations.writeToCSV(pDirectory, pName + "_fixations");
                
                // Generate DGMs
                List<List<String>> descriptiveGazeMeasures = generateResults(allGaze, fixations);
                FileHandler.writeToCSV(descriptiveGazeMeasures, pDirectory, pName + "_DGMs");

                // If empty, add header row
                if (allParticipantDGMs.size() == 0) {
                    List<String> headers = descriptiveGazeMeasures.get(0);
                    headers.add(0, "Participant ID"); // Add a participant ID colum to headers
                    allParticipantDGMs.add(headers);
                }

                // Populate allParticipantDGMs with the DGMs generated for a participant
                List<String> dgms = descriptiveGazeMeasures.get(1);
                dgms.add(0, pName);
                allParticipantDGMs.add(dgms);

                // Generate AOIs
                AreaOfInterests.generateAOIs(allGaze, pDirectory, pName);

                // Generate windows
                Windows.generateWindows(allGaze, pDirectory, settings);

                // Generate sequence files
                Sequences.generateSequenceFiles(validFixations, pDirectory, sequences, aoiMap);
                
                // Generate patterns
                ArrayList<List<String>> expandedPatterns = Patterns.discoverPatterns(
                    Arrays.asList(sequences.get(i)), 
                    MIN_PATTERN_LENGTH, 
                    MAX_PATTERN_LENGTH, 
                    1, 
                    1
                );
                
                ArrayList<List<String>> collapsedPatterns = Patterns.discoverPatterns(
                    Arrays.asList(Sequences.getCollapsedSequence(sequences.get(i))), 
                    MIN_PATTERN_LENGTH,
                    MAX_PATTERN_LENGTH,
                    1,
                    1
                );

                FileHandler.writeToCSV(expandedPatterns, pDirectory, pName + "_expandedPatterns");
                FileHandler.writeToCSV(collapsedPatterns, pDirectory, pName + "_collapsedPatterns");
            }

            // Batch analysis
            if (inputFiles.length > 1) {
                // Generate patterns
                List<String> expandedSequences = sequences;
                List<String> collapsedSequences = new ArrayList<String>();

                for (String s : expandedSequences) {
                    collapsedSequences.add(Sequences.getCollapsedSequence(s));
                }

                System.out.println("Analyzing patterns");
                ArrayList<List<String>> expandedPatterns = Patterns.discoverPatterns(expandedSequences, MIN_PATTERN_LENGTH, MAX_PATTERN_LENGTH, MIN_PATTERN_FREQUENCY, MIN_SEQUENCE_SIZE);
                ArrayList<List<String>> collapsedPatterns = Patterns.discoverPatterns(collapsedSequences, MIN_PATTERN_LENGTH, MAX_PATTERN_LENGTH, MIN_PATTERN_FREQUENCY, MIN_SEQUENCE_SIZE);
                
                // Root directory
                String directory = params.getOutputDirectory();

                // Output files
                FileHandler.writeToCSV(expandedPatterns, directory, "expandedPatterns");
                FileHandler.writeToCSV(collapsedPatterns, directory, "collapsedPatterns");
                FileHandler.writeToCSV(allParticipantDGMs, directory, "combinedDGMs");
            }

            System.out.println("Analysis Complete.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // This function should only take in raw gaze data as a parameter, otherwise derived DataEntrys will be produced with incorrect data
    /**
     * Generates descriptive gaze measures from a single participant’s raw gaze data for the whole screen.
     * @param allGaze all the participant's gaze data.
     * @param fixations the participant gaze data, filtered by fixations.
     * @return a {@code List<List<String>} where the first inner-list is the measure names, and second inner-list is the calculated values.
     */
    static List<List<String>> generateResults(DataEntry allGaze, DataEntry fixations) {
        DataEntry validGaze = DataFilter.applyScreenSize(DataFilter.filterByValidity(allGaze), SCREEN_WIDTH, SCREEN_HEIGHT);
        DataEntry validAoiFixations = DataFilter.applyScreenSize(DataFilter.filterByValidity(fixations), SCREEN_WIDTH, SCREEN_HEIGHT);
        var results = generateResultsHelper(validGaze, validGaze, validAoiFixations);
        return results;
    }

    // This function should only take in raw gaze data as a parameter, otherwise derived DataEntrys will be produced with incorrect data
    /**
     * Generates descriptive gaze measures from a single participant’s raw gaze data for a single AOI.
     * @param allGaze all the of participant gaze data.
     * @param aoiGaze the participant gaze data that occurred inside the target AOI.
     * @param aoiFixations the participant gaze data, filtered by fixations that occurred inside the target AOI.
     * @return a {@code List<List<String>} where the first inner-list is the measure names, and second inner-list is the calculated values.
     */
    static List<List<String>> generateResults(DataEntry allGaze, DataEntry aoiGaze, DataEntry aoiFixations) {
        DataEntry validAllGaze = DataFilter.applyScreenSize(DataFilter.filterByValidity(allGaze), SCREEN_WIDTH, SCREEN_HEIGHT);
        DataEntry validAoiGaze = DataFilter.applyScreenSize(DataFilter.filterByValidity(allGaze), SCREEN_WIDTH, SCREEN_HEIGHT);
        DataEntry validAoiFixations = DataFilter.applyScreenSize(DataFilter.filterByValidity(aoiFixations), SCREEN_WIDTH, SCREEN_HEIGHT);
        var results = generateResultsHelper(validAllGaze, validAoiGaze, validAoiFixations);
        return results;
    }

    /**
     * Helper methods that generates the descriptive gaze measures.
     * @param validAllGaze all gaze data, filtered by validity.
     * @param validAoiGaze the gaze data that ocurred within an aoi, filtered by validity. For the whole screen, this is the same
     * data as validAllGaze.
     * @param validAoiFixation the gaze data that ocurred within an aoi, filtered by fixation and validity.
     * @return a list the descriptive gaze measures where the first row is the headers and the second row is the values.
     */
    private static List<List<String>> generateResultsHelper(DataEntry validAllGaze, DataEntry validAoiGaze, DataEntry validAoiFixation) {
        
        LinkedHashMap<String,String> resultsMap = new LinkedHashMap<String, String>();
        resultsMap.putAll(Fixations.analyze(validAoiFixation));
        resultsMap.putAll(Saccades.analyze(validAoiFixation));
        resultsMap.putAll(SaccadeVelocity.analyze(validAllGaze, validAoiFixation));
        resultsMap.putAll(Angles.analyze(validAoiFixation));
        resultsMap.putAll(ConvexHull.analyze(validAoiFixation));
        resultsMap.putAll(GazeEntropy.analyze(validAoiFixation));
        resultsMap.putAll(Blinks.analyze(validAoiGaze));
        resultsMap.putAll(Gaze.analyze(validAoiGaze));
        resultsMap.putAll(Event.analyze(validAoiGaze));

        var resultsList = new ArrayList<List<String>>(2);
        resultsList.add(new ArrayList<>(resultsMap.keySet()));
        resultsList.add(new ArrayList<>(resultsMap.values()));

        return resultsList;
    }
}
