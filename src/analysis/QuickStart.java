package analysis;

public class QuickStart {
    // configDirectory should be initialized to a file path containing the config file
    static final String configDirectory = "";

    public static void main(String[] args) {
         Parameters params = new Parameters(new String[]{"data//Esther Jung_all_gaze.csv"}, "C://Users//Productivity//Documents//Testing//OutputTest.csv",new WindowSettings());
         Analysis analysis = new Analysis(params);
        analysis.run();
    }
}