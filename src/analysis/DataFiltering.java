package analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javafx.scene.chart.PieChart.Data;

public class DataFiltering {
    static public DataEntry filterByFixations(DataEntry data) { //Cleanses the data by filtering out repeated fixations
        System.out.println("Filtering now");
        DataEntry filtered = new DataEntry(data.getHeaders());

        List<String> lastValidFixation = null;
        int currFixation = -1;

        for (int row = 0; row < data.rowCount(); row++) {
            int fixationID = Integer.parseInt(data.getValue("FPOGID", row));
            int fixationValidity = Integer.parseInt(data.getValue("FPOGV", row));
            if (fixationID != currFixation) {
                if (lastValidFixation != null) filtered.process(lastValidFixation);
                currFixation = fixationID;
            } else if (fixationID == currFixation && fixationValidity == 1) {
                lastValidFixation = data.getRow(row);
            }
        }
        if (lastValidFixation != null) filtered.process(lastValidFixation);
        
        System.out.println("Filtered from "+data.rowCount()+" rows to "+filtered.rowCount()+" rows.");
        return filtered;
    }

    static public LinkedHashMap<String, Data> filterByAOI(DataEntry data ){
        
        return new LinkedHashMap<String, Data>();
    }
}
