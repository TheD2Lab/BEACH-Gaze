package analysis;

public class WindowSettings {

    public boolean continuous;
    public boolean cumulative;
    public boolean overlapping;
    public boolean event;

    public WindowSettings() {
        this.continuous = false;
        this.cumulative = false;
        this.overlapping = false;
        this.event = false;
    }
    
    public WindowSettings(boolean continuous, boolean cumulative, boolean overlapping, boolean event) {
        this.continuous = continuous;
        this.cumulative = cumulative;
        this.overlapping = overlapping;
        this.event = event;
    }
}
