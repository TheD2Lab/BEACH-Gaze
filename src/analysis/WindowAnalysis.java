package analysis;

public class WindowAnalysis {

    private boolean tumblingEnabled;
    private boolean tumblingWindowSize;

    private boolean expandingEnabled;
    private boolean expandingWindowSize;

    private boolean hoppingEnabled;
    private boolean hoppingWindowSize;
    private boolean hoppingOverlapSize;
    
    private boolean eventEnabled;

    public WindowAnalysis() {
        this.tumblingEnabled = false;
        this.expandingEnabled = false;
        this.hoppingEnabled = false;
        this.eventEnabled = false;
    }
    
    public WindowAnalysis(boolean tumbling, boolean expanding, boolean hopping, boolean event) {
        this.tumblingEnabled = tumbling;
        this.expandingEnabled = expanding;
        this.hoppingEnabled = hopping;
        this.eventEnabled = event;
    }

    public boolean isTumblingEnabled() {
        return this.tumblingEnabled;
    }

    public boolean isExpandingEnabled() {
        return this.expandingEnabled;
    }

    public boolean isHoppingEnabled() {
        return this.hoppingEnabled;
    }

    public boolean isEventEnabled() {
        return this.eventEnabled;
    }

    public void setTumblingEnabled(boolean tumbling) {
        this.tumblingEnabled = tumbling;
    }

    public void setExpandingEnabled(boolean expanding) {
        this.expandingEnabled = expanding;
    }

    public void setHoppingEnabled(boolean hopping) {
        this.hoppingEnabled = hopping;
    }
    public void setEventEnabled(boolean event) {
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
