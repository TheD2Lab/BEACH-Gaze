package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Fixations {
    /* Taken from old fixations file to use as reference
     *      int fixationDurationIndex = list.indexOf("FPOGD");
            int fixationIDIndex = list.indexOf("FPOGID");
            int fixationXIndex = list.indexOf("FPOGX");
            int fixationYIndex = list.indexOf("FPOGY");
            int aoiIndex = list.indexOf("AOI");
            int timestampIndex = list.indexOf("FPOGS");
     */

    static public LinkedHashMap<String,String> analyze(DataEntry data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
        
        ArrayList<Double> allFixationDurations = new ArrayList<>();
        int fixationCount = data.rowCount();

        System.out.println("FixationCount: "+Integer.toString(fixationCount));

        for (int row = 0; row < data.rowCount(); row++) {
            Double fixationDurationSeconds = Double.valueOf(data.getValue("FPOGD", row));
            allFixationDurations.add(fixationDurationSeconds);
        }
        System.out.println("Fixations done");
        System.out.println(allFixationDurations.size());
        results.put(
            "Total Number of Fixations", //Output Header
            String.valueOf(fixationCount) //Output Value
            );

        results.put(
            "Sum of all fixation duration (s)",
            String.valueOf(DescriptiveStats.getSumOfDoubles(allFixationDurations))
            );

        results.put(
            "Mean fixation duration (s)",
            String.valueOf(DescriptiveStats.getMeanOfDoubles(allFixationDurations))
            );

        results.put(
            "Median fixation duration (s)",
            String.valueOf(DescriptiveStats.getMedianOfDoubles(allFixationDurations))
            );

        results.put(
            "St.Dev. of fixation durations (s)",
            String.valueOf(DescriptiveStats.getStDevOfDoubles(allFixationDurations))
            );

        results.put(
            "Min. fixation duration (s)",
            String.valueOf(DescriptiveStats.getMinOfDoubles(allFixationDurations))
            );

        results.put(
            "Max. fixation duration (s)",
            String.valueOf(DescriptiveStats.getMaxOfDoubles(allFixationDurations))
            );

        return results;
    }
}

/*
 *      x    headers.add("total number of fixations");
            data.add(String.valueOf(fixationCount));

			headers.add("sum of all fixation duration");
			data.add(String.valueOf(descriptiveStats.getSumOfDoubles(allFixationDurations)));

			headers.add("mean fixation duration (ms)");
			data.add(String.valueOf(descriptiveStats.getMeanOfDoubles(allFixationDurations)));

			headers.add("median fixation duration (ms)");
			data.add(String.valueOf(descriptiveStats.getMedianOfDoubles(allFixationDurations)));

			headers.add(" StDev of fixation durations (ms)");
			data.add(String.valueOf(descriptiveStats.getStDevOfDoubles(allFixationDurations)));

			headers.add("Min. fixation duration (ms)");
			data.add(String.valueOf(descriptiveStats.getMinOfDoubles(allFixationDurations)));

			headers.add("Max. fixation duration (ms)");
			data.add(String.valueOf(descriptiveStats.getMaxOfDoubles(allFixationDurations)));
 */