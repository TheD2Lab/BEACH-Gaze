/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
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
        this.tumblingWindowSize = 60;

        this.expandingEnabled = false;
        this.expandingWindowSize = 60;

        this.hoppingEnabled = false;
        this.hoppingWindowSize = 60;
        this.hoppingHopSize = 30;

        this.eventEnabled = false;
        this.eventTimeout = 10;
        this.eventMaxDuration = 90;
        this.eventBaselineDuration = 30;
        this.event = "";
    }

    @Override
    public String toString() {
        return "Tumbling: " + tumblingEnabled + " Window Size: " +  tumblingWindowSize + "\n" +
                "Expanding: " + expandingEnabled + " Window Size: " +  expandingWindowSize +"\n" +
                "Hopping: " + hoppingEnabled + " Window Size: " + hoppingWindowSize + " Hop Size: " + hoppingHopSize + "\n" +
                "Session (Event): " + eventEnabled + ", " + event + " Timeout: " + eventTimeout + " Max Duration: " + eventMaxDuration + " Baseline Duration: " + eventBaselineDuration;
    }
}
