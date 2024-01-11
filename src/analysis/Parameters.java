package analysis;

import java.io.File;

public class Parameters {
    /*
     * Format of a config file
     * UI and QuickStart creates Parameters object
     */
    private String inputDirectory;
    private String outputDirectory;


    public Parameters(String inputDirectory, String outputDirectory) { //Constructor with variables as the parameters
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public Parameters(File parameters) { //Constructor with a file containing the parameters

    }

    public String getInputDirectory() {
        return inputDirectory;
    }
    
    public String getOutputDirectory() {
        return outputDirectory;
    }
}
