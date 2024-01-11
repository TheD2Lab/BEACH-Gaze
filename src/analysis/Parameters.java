package analysis;

import java.io.File;

public class Parameters {
    /*
     * Format of a config file
     * UI and QuickStart creates Parameters object
     */
    public String inputDirectory;
    public String outputDirectory;


    public Parameters(String inputDirectory, String outputDirectory) { //Constructor with variables as the parameters
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public Parameters(File parameters) { //Constructor with a file containing the parameters
        /*
         * Still needs code
         */
    }

    public void SaveToJSON(String saveLocation) {
        FileHandler.SaveParametersAsJSON(this, saveLocation);
    }
}
