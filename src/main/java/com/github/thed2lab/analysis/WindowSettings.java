package com.github.thed2lab.analysis;

public class WindowSettings {

    /*
     * Data class to store user window settings
     * All time based metrics are in seconds
     */

    public boolean tumblingEnabled;
    public double tumblingWindowSize;

    public boolean expandingEnabled;
    public double expandingWindowSize;

    public boolean hoppingEnabled;
    public double hoppingWindowSize;
    public double hoppingHopSize;
    
    public boolean eventEnabled;
    public String event;
    public double eventTimeout;
    public double eventMaxDuration;
    public double eventBaselineDuration;

    public WindowSettings() {
        this.tumblingEnabled = false;
        this.expandingEnabled = false;
        this.hoppingEnabled = false;
        this.eventEnabled = false;
    }

    @Override
    public String toString() {
        return "Tumbling: " + tumblingEnabled + "\n" +
                "Expanding: " + expandingEnabled + "\n" +
                "Hopping: " + hoppingEnabled + "\n" +
                "Event: " + eventEnabled;
    }
}
