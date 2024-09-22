package com.github.thed2lab.analysis;

import java.io.File;
import java.util.Arrays;

public class WekaParameters {

    private File[] dataset;
    private String outputDirectory;
    private boolean isClassification;

    public WekaParameters (File[] dataset, String outputDirectory, boolean isClassification) {
        this.dataset = dataset.clone();
        this.outputDirectory = outputDirectory;
        this.isClassification = isClassification;
    }

    public File[] getDataSet() {
        return this.dataset;
    }

    public String getDirectory() {
        return this.outputDirectory;
    }

    public boolean getIsClassification() {
        return this.isClassification;
    }

    public String toString() {
        return "--Parameters-- \n InputFiles: ["+dataset.length+"] "+Arrays.toString(dataset)+" \n OutputDirectory: "+outputDirectory +"\n isClassification: " + isClassification;
    }

}
