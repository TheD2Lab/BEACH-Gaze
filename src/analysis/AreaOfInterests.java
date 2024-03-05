package analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AreaOfInterests {
    
    public static void generateAOIs(DataEntry data, String outputDirectory, String fileName) {
        LinkedHashMap<String, DataEntry> aoiMetrics = new LinkedHashMap<>();
        for (int i = 0; i < data.rowCount(); i++) {
            String aoi = data.getValue("AOI", i);
            if (!aoiMetrics.containsKey(aoi)) {
                DataEntry d = new DataEntry(data.getHeaders());
                aoiMetrics.put(aoi, d);
            }
            aoiMetrics.get(aoi).process(data.getRow(i));
        }
        
        // printing the elements of LinkedHashMap
        ArrayList<List<String>> metrics = new ArrayList<>();
        metrics.add(new ArrayList<String>());

        boolean isFirst = true;
        for (String key : aoiMetrics.keySet()) {
            DataEntry d = aoiMetrics.get(key);
            ArrayList<List<String>> results = Analysis.generateResults(d);
            results.get(1).add(0,key);
            if (isFirst) {
                isFirst = false;
                List<String> headers = results.get(0);
                headers.add(0, "AOI");
                metrics.get(0).addAll(headers);
            }
            metrics.add(results.get(1));
        }
        FileHandler.writeToCSV(metrics, outputDirectory, fileName + "_AOI_Analytics");
    }
}
