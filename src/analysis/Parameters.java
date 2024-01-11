package analysis;

import java.io.File;
import java.util.HashMap;

public class Parameters {
    /*
     * Format of a config file
     * UI and QuickStart creates Parameters object
     */
    private String[] inputFiles;
    private String outputDirectory;

    /*
     * Constructor with variables as the parameters
     */
    public Parameters(String[] inputFiles, String outputDirectory, HashMap<String, Integer> windowSettings) {
        this.inputFiles = inputFiles;
        this.outputDirectory = outputDirectory;
    }

    /*
     * Constructor with a file containing the parameters
     */
    public Parameters(File parameters) { 
        /*
         * Still needs code
         */
    }

    public void saveToJSON(String saveLocation) {
        FileHandler.SaveParametersAsJSON(this, saveLocation);
    }
}
