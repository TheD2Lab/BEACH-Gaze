package com.github.thed2lab.analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Fixations {
    final static String DURATION_INDEX = "FPOGD";

    static public LinkedHashMap<String,String> analyze(DataEntry data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();

        ArrayList<Double> allFixationDurations = new ArrayList<>();
        int fixationCount = data.rowCount();
        
        for (int row = 0; row < data.rowCount(); row++) {    
            Double fixationDurationSeconds = Double.valueOf(data.getValue(DURATION_INDEX, row));
            allFixationDurations.add(fixationDurationSeconds);
        }
        
        results.put(
            "total_number_of_fixations", //Output Header
            String.valueOf(fixationCount) //Output Value
            );

        results.put(
            "sum_of_all_fixation_duration_s",
            String.valueOf(DescriptiveStats.getSumOfDoubles(allFixationDurations))
            );

        results.put(
            "mean_fixation_duration_s",
            String.valueOf(DescriptiveStats.getMeanOfDoubles(allFixationDurations))
            );

        results.put(
            "median_fixation_duration_s",
            String.valueOf(DescriptiveStats.getMedianOfDoubles(allFixationDurations))
            );

        results.put(
            "stdev_of_fixation_durations_s",
            String.valueOf(DescriptiveStats.getStDevOfDoubles(allFixationDurations))
            );

        results.put(
            "min_fixation_duration_s",
            String.valueOf(DescriptiveStats.getMinOfDoubles(allFixationDurations))
            );

        results.put(
            "max_fixation_duration_s",
            String.valueOf(DescriptiveStats.getMaxOfDoubles(allFixationDurations))
            );

        return results;
    }
}