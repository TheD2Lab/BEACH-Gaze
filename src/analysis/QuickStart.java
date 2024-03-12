package analysis;

import java.io.File;

public class QuickStart {
    // configDirectory should be initialized to a file path containing the config file
    static final String configDirectory = "";

    public static void main(String[] args) {
        System.out.println("Running Quickstart");
        //Parameters params = new Parameters(new File[]{new File("data//Esther Jung_all_gaze.csv")}, "C://Users//Angelo//Documents//Testing//OutputTest.csv",new WindowSettings());
        Parameters params = new Parameters(new File[]{new File("data//p20_all_gaze.csv")}, "C://Users//Productivity//Documents//Testing",new WindowSettings());
        Analysis analysis = new Analysis(params);
        analysis.run();
    }
}