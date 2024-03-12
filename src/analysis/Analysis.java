package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Analysis {
    final static int SCREEN_WIDTH = 1920;
	final static int SCREEN_HEIGHT = 1080;

    final static int MIN_PATTERN_LENGTH = 3;
    final static int MAX_PATTERN_LENGTH = 7;
    final static int MIN_PATTERN_FREQUENCY = 2;
    final static int MIN_SEQUENCE_SIZE = 3;

    final static String TIME_INDEX = "TIME";

    private Parameters params;
    
    public Analysis(Parameters params) {
        this.params = params;
    }

    public boolean run() {
        try {
            File[] inputFiles = params.getInputFiles();
            List<String> sequences = new ArrayList<String>();
            ArrayList<List<String>> allParticipantDGMs = new ArrayList<List<String>>();

            for (int i = 0; i < inputFiles.length; i++) {
                File f = inputFiles[i];

                String pName = f.getName().replace("_all_gaze", "").replace(".csv", "");
                String pDirectory = params.getOutputDirectory() + "/" + pName;

                System.out.println("Analyzing " + pName);

                DataEntry rawGaze = FileHandler.buildDataEntry(f);
                DataEntry validGaze = DataFilter.filterByValidity(rawGaze);
                DataEntry fixations = DataFilter.filterByFixations(validGaze);
                
                validGaze.writeToCSV(pDirectory, pName + "_cleansed");
                fixations.writeToCSV(pDirectory, pName + "_fixations");

                ArrayList<List<String>> descriptiveGazeMeasures = generateResults(validGaze);
                FileHandler.writeToCSV(descriptiveGazeMeasures, pDirectory, pName + "_DGMs");

                // If empty, add header row
                if (allParticipantDGMs.size() == 0)
                    allParticipantDGMs.add(descriptiveGazeMeasures.get(0));

                // Populate allParticipantDGMs with the DGMs generated for a participant
                allParticipantDGMs.add(descriptiveGazeMeasures.get(0));

                Windows.generateWindows(validGaze, pDirectory, params.getWindowSettings());
                AreaOfInterests.generateAOIs(validGaze, pDirectory, pName);
                Sequences.generateSequenceFiles(validGaze, pDirectory, sequences);
            }

            // Batch analysis
            if (inputFiles.length > 1) {
                List<String> expandedSequences = sequences;
                List<String> collapsedSequences = new ArrayList<String>();

                for (String s : expandedSequences) {
                    collapsedSequences.add(Sequences.getCollapsedSequence(s));
                }

                ArrayList<List<String>> expandedPatterns = Patterns.discoverPatterns(expandedSequences, MIN_PATTERN_LENGTH, MAX_PATTERN_LENGTH, MIN_PATTERN_FREQUENCY, MIN_SEQUENCE_SIZE);
                ArrayList<List<String>> collapsedPatterns = Patterns.discoverPatterns(expandedSequences, MIN_PATTERN_LENGTH, MAX_PATTERN_LENGTH, MIN_PATTERN_FREQUENCY, MIN_SEQUENCE_SIZE);
                
                String directory = params.getOutputDirectory();
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

    // Generate should only take in all gaze data otherwise calculates will be incorrect
    public static ArrayList<List<String>> generateResults(DataEntry data) {
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
}
