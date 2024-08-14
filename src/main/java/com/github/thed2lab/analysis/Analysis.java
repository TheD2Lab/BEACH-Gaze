package com.github.thed2lab.analysis;

import static com.github.thed2lab.analysis.Constants.SCREEN_HEIGHT;
import static com.github.thed2lab.analysis.Constants.SCREEN_WIDTH;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The Analysis class drives the entire analysis on one or multiple files of gaze data.
 * Its role is to iterate over each file, and process it into DataEntry objects for gaze,
 * validity, and fixations.
 */
public class Analysis {

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
            List<String> expandedSequences = new ArrayList<>();
            List<List<String>> allParticipantDGMs = new ArrayList<>();

            WindowSettings settings = params.getWindowSettings();

            for (int i = 0; i < inputFiles.length; i++) {
                File f = inputFiles[i];

                String pName = f.getName().replace("_all_gaze", "").replace(".csv", "");
                String pDirectory = params.getOutputDirectory() + "/" + pName;

                System.out.println("Analyzing " + pName);

                // Build DataEntrys
                DataEntry allGaze = DataFilter.applyScreenSize(FileHandler.buildDataEntry(f), SCREEN_WIDTH, SCREEN_HEIGHT);
                DataEntry validGaze = DataFilter.filterByValidity(allGaze, SCREEN_WIDTH, SCREEN_HEIGHT);
                DataEntry fixations = DataFilter.filterByFixations(allGaze);
                DataEntry validFixations = DataFilter.filterByValidity(fixations, SCREEN_HEIGHT, SCREEN_WIDTH);
                
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
                AreaOfInterests.generateAOIs(allGaze, fixations, pDirectory, pName);

                // Generate windows
                Windows.generateWindows(allGaze, pDirectory, settings);

                // Generate sequence files
                String expandedSeq = Sequences.generateSequenceFiles(validFixations, pDirectory);
                expandedSequences.add(expandedSeq);

                // Generate patterns
                List<List<String>> expandedPatterns = Patterns.discoverPatterns(
                    List.of(expandedSeq), 
                    MIN_PATTERN_LENGTH, 
                    MAX_PATTERN_LENGTH, 
                    1, 
                    1
                );
                
                List<List<String>> collapsedPatterns = Patterns.discoverPatterns(
                    List.of(Sequences.getCollapsedSequence(expandedSeq)), 
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
                List<String> collapsedSequences = new ArrayList<String>();

                for (String s : expandedSequences) {
                    collapsedSequences.add(Sequences.getCollapsedSequence(s));
                }

                System.out.println("Analyzing patterns");
                List<List<String>> expandedPatterns = Patterns.discoverPatterns(expandedSequences, MIN_PATTERN_LENGTH, MAX_PATTERN_LENGTH, MIN_PATTERN_FREQUENCY, MIN_SEQUENCE_SIZE);
                List<List<String>> collapsedPatterns = Patterns.discoverPatterns(collapsedSequences, MIN_PATTERN_LENGTH, MAX_PATTERN_LENGTH, MIN_PATTERN_FREQUENCY, MIN_SEQUENCE_SIZE);
                
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
        var results = generateResultsHelper(allGaze, allGaze, fixations);
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
        var results = generateResultsHelper(allGaze, aoiGaze, aoiFixations);
        return results;
    }

    /**
     * Helper methods that generates the descriptive gaze measures.
     * @param allGaze all gaze data for the whole screen, with screen size applied.
     * @param areaGaze the gaze data that ocurred within a target portion of screen, i.e., either the whole screen or an AOI, with screen
     * size applied.
     * @param areaFixations the gaze data that ocurred within a target portion of screen, i.e., either the whole screen or an AOI,
     * and filtered by fixation with screen size applied.
     * @return a {@code List<List<String>} where the first inner-list is the measure names, and second inner-list is the calculated values.
     */
    private static List<List<String>> generateResultsHelper(DataEntry allGaze, DataEntry areaGaze, DataEntry areaFixations) {
        // an argument could be made to filter before entering this function; there is a code smell due to blink rate and saccadeV changing
        DataEntry validAllGaze = DataFilter.filterByValidity(allGaze, SCREEN_WIDTH, SCREEN_HEIGHT); 
        DataEntry validAreaGaze = DataFilter.filterByValidity(areaGaze, SCREEN_WIDTH, SCREEN_HEIGHT);
        DataEntry validAreaFixations = DataFilter.filterByValidity(areaFixations, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        LinkedHashMap<String,String> resultsMap = new LinkedHashMap<String, String>();
        resultsMap.putAll(Fixations.analyze(validAreaFixations));
        resultsMap.putAll(Saccades.analyze(validAreaFixations));
        resultsMap.putAll(SaccadeVelocity.analyze(validAllGaze, validAreaFixations));
        resultsMap.putAll(Angles.analyze(validAreaFixations));
        resultsMap.putAll(ConvexHull.analyze(validAreaFixations));
        resultsMap.putAll(GazeEntropy.analyze(validAreaFixations));
        resultsMap.putAll(Blinks.analyze(areaGaze));
        resultsMap.putAll(Gaze.analyze(validAreaGaze));
        resultsMap.putAll(Event.analyze(validAreaGaze));

        var resultsList = new ArrayList<List<String>>(2);
        resultsList.add(new ArrayList<>(resultsMap.keySet()));
        resultsList.add(new ArrayList<>(resultsMap.values()));

        return resultsList;
    }
}
