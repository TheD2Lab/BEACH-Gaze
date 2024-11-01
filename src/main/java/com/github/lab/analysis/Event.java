/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.lab.analysis;

import static com.github.lab.analysis.Constants.CURSOR_EVENT;

import java.util.LinkedHashMap;

public class Event {
    
    static public LinkedHashMap<String,String> analyze(DataEntry data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
        int leftMouseClicks = 0;

        for (int row = 0; row < data.rowCount(); row++) {
            if (data.getValue(CURSOR_EVENT, row).equals("1")) {
                leftMouseClicks += 1;
            }
        }
        //Absolute Degrees
        results.put(
            "total_number_of_l_mouse_clicks", //Output Header
            String.valueOf(leftMouseClicks)
            );    
        return results;
    }
}
