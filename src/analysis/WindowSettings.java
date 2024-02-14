package analysis;

public class WindowSettings {

    public boolean tumblingEnabled;
    public int tumblingWindowSize;

    public boolean expandingEnabled;
    public int expandingWindowSize;

    public boolean hoppingEnabled;
    public int hoppingWindowSize;
    public int hoppingOverlapSize;
    
    public boolean eventEnabled;

    public WindowSettings() {
        this.tumblingEnabled = false;
        this.expandingEnabled = false;
        this.hoppingEnabled = false;
        this.eventEnabled = false;
    }
    
    public WindowSettings(boolean tumbling, boolean expanding, boolean hopping, boolean event) {
        this.tumblingEnabled = tumbling;
        this.expandingEnabled = expanding;
        this.hoppingEnabled = hopping;
        this.eventEnabled = event;
    }

    @Override
    public String toString() {
        return "Tumbling: " + tumblingEnabled + "\n" +
                "Expanding: " + expandingEnabled + "\n" +
                "Hopping: " + hoppingEnabled + "\n" +
                "Event: " + eventEnabled;
    }
}
