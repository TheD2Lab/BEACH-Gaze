package analysis;

import java.util.ArrayList;

public class Fixations {
    private ArrayList<Double> allDurations;
    private DataEntry data;

    public Fixations(DataEntry data) {
        this.data = data;
        this.allDurations = new ArrayList<Double>();
    }

    /*
     *      int fixationDurationIndex = list.indexOf("FPOGD");
            int fixationIDIndex = list.indexOf("FPOGID");
            int fixationXIndex = list.indexOf("FPOGX");
            int fixationYIndex = list.indexOf("FPOGY");
            int aoiIndex = list.indexOf("AOI");
            int timestampIndex = list.indexOf("FPOGS");
     */
    public void update() {
        double fixationDuration = Double.valueOf(data.getValue("FPOGD"));
        allDurations.add(fixationDuration);
    }

    public void process() {
        
    }
}
