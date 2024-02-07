package analysis;

public class WindowSettings {

    private boolean continuousEnabled;
    private boolean continuousWindowSize;

    private boolean cumulativeEnabled;
    private boolean cumulativeWindowSize;

    private boolean overlappingEnabled;
    private boolean overlappingWindowSize;
    private boolean overlappingOverlapSize;
    
    private boolean eventEnabled;

    public WindowSettings() {
        this.continuousEnabled = false;
        this.cumulativeEnabled = false;
        this.overlappingEnabled = false;
        this.eventEnabled = false;
    }
    
    public WindowSettings(boolean continuous, boolean cumulative, boolean overlapping, boolean event) {
        this.continuousEnabled = continuous;
        this.cumulativeEnabled = cumulative;
        this.overlappingEnabled = overlapping;
        this.eventEnabled = event;
    }

    public boolean getContinuousEnabled() {
        return this.continuousEnabled;
    }

    public boolean getCumulativeEnabled() {
        return this.cumulativeEnabled;
    }

    public boolean getOverlappingEnabled() {
        return this.overlappingEnabled;
    }

    public boolean getEventEnabled() {
        return this.eventEnabled;
    }

    public void setContinuousEnabled(boolean continuous) {
        this.continuousEnabled = continuous;
    }

    public void setCumulativeEnabled(boolean cumulative) {
        this.cumulativeEnabled = cumulative;
    }

    public void setOverlappingEnabled(boolean overlapping) {
        this.overlappingEnabled = overlapping;
    }
    public void setEventEnabled(boolean event) {
        this.eventEnabled = event;
    }

    @Override
    public String toString() {
        return "Continuous: " + continuousEnabled + "\n" +
                "Cumulative: " + cumulativeEnabled + "\n" +
                "Overlapping: " + overlappingEnabled + "\n" +
                "Event: " + eventEnabled;
    }
}
