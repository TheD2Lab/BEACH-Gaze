package com.github.thed2lab.analysis;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class Parameters {
    /*
     * Format of a config file
     * UI and QuickStart creates Parameters object
     */
    private File[] inputFiles;
    private String outputDirectory;
    private WindowSettings windowSettings;

    /*
     * Constructor with variables as the parameters
     */
    public Parameters(File[] inputFiles, String outputDirectory, WindowSettings windowSettings) {
        this.inputFiles = inputFiles.clone();
        this.outputDirectory = outputDirectory;
        this.windowSettings = windowSettings;
    }

    /*
     * Constructor with a file containing the parameters
     */
    public Parameters(File config) { 
        HashMap<String,String> data = FileHandler.loadParametersFromJSON(config); 
        this.outputDirectory = data.get("OutputDirectory");
        String[] pathStrings = data.get("InputFiles").replace("[", "").replace("]", "").split(", ").clone(); //Converts the JSON string back to a regular array
        inputFiles = new File[pathStrings.length];
        for (int i = 0; i < pathStrings.length; i++) {
            inputFiles[i] = new File(pathStrings[i]);
        }
    }

    public void saveToJSON(String saveLocation, String fileName) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("InputFiles", Arrays.toString(inputFiles));
        data.put("OutputDirectory", outputDirectory);
        System.out.println("Saving: "+data.toString());
        FileHandler.saveParametersAsJSON(data, saveLocation + "/" + fileName);
    }

    public String toString() {
        return "--Parameters-- \n InputFiles: ["+inputFiles.length+"] "+Arrays.toString(inputFiles)+" \n OutputDirectory: "+outputDirectory +"\n --End of Parameters--";
    }

    public File[] getInputFiles() {
        return this.inputFiles.clone();
    }

    public String getOutputDirectory() {
        return this.outputDirectory;
    }    

    public WindowSettings getWindowSettings() {
        return this.windowSettings;
    }
}
