package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    public Parameters(String[] inputFiles, String outputDirectory, WindowSettings windowSettings) {
        this.inputFiles = inputFiles.clone();
        this.outputDirectory = outputDirectory;
    }

    /*
     * Constructor with a file containing the parameters
     */
    public Parameters(File config) { 
        HashMap<String,String> data = FileHandler.loadParametersFromJSON(config); 
        this.outputDirectory = data.get("OutputDirectory");
        inputFiles = data.get("InputFiles").replace("[", "").replace("]", "").split(", ").clone(); //Converts the JSON string back to a regular array
    }

    public void saveToJSON(String saveLocation, String fileName) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("InputFiles", Arrays.toString(inputFiles));
        data.put("OutputDirectory", outputDirectory);
        System.out.println("Saving: "+data.toString());
        FileHandler.SaveParametersAsJSON(data, saveLocation + "\\" + fileName);
    }

    public String toString() {
        return "--Parameters-- \n InputFiles: ["+inputFiles.length+"] "+Arrays.toString(inputFiles)+" \n OutputDirectory: "+outputDirectory +"\n --End of Parameters--";
    }

    /* public static void main(String[] args) {
        System.out.println("Creating and saving Parameters!");

        Parameters p = new Parameters(new String[]{"data\\Kayla_all_gaze.csv","data\\Esthe  r Jung_all_gaze.csv"},"data\\presets", new HashMap<>());
        p.saveToJSON("data\\presets","TestConfig.json");

        System.out.println(p.toString());
        System.out.println("Loading parameters!");

        Parameters p2 = new Parameters(new File("data\\presets\\TestConfig.json"));
        System.out.println(p2.toString());
    } */

    public String[] getInputFiles() {
        return inputFiles.clone();
    }
    public String getOutputFile() {
        return outputDirectory;
    }    
}
