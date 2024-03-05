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

                Windows.generateWindows(validGaze, pDirectory, params.getWindowSettings());
                AreaOfInterests.generateAOIs(validGaze, pDirectory, pName);
                Sequences.generateSequenceFiles(validGaze, pDirectory);
            }

            System.out.println("Analysis Complete.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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
