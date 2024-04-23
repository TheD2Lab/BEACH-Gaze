package analysis;

public class WindowSettings {

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

    public boolean eventThresholdEnabled;
    public double eventWindowSize;
    public int eventThreshold;
    public String thresholdEvent;

    public WindowSettings() {
        this.tumblingEnabled = false;
        this.expandingEnabled = false;
        this.hoppingEnabled = false;
        this.eventEnabled = false;
        this.eventThresholdEnabled = false;
    }

    @Override
    public String toString() {
        return "Tumbling: " + tumblingEnabled + "\n" +
                "Expanding: " + expandingEnabled + "\n" +
                "Hopping: " + hoppingEnabled + "\n" +
                "Event: " + eventEnabled;
    }
}
