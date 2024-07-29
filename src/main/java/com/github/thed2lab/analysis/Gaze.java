package com.github.thed2lab.analysis;

import java.util.LinkedHashMap;

public class Gaze {
    final static String PUPIL_LEFT_DIAMETER_INDEX = "LPMM";
    final static String PUPIL_RIGHT_DIAMETER_INDEX = "RPMM";

    static public LinkedHashMap<String,String> analyze(DataEntry data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
        double leftSum = 0.0;
        double rightSum = 0.0;
        double bothSum = 0.0;
        int count = data.rowCount();

        for (int row = 0; row < data.rowCount(); row++) {
            double leftSize = Double.valueOf(data.getValue(PUPIL_LEFT_DIAMETER_INDEX, row));
            double rightSize = Double.valueOf(data.getValue(PUPIL_RIGHT_DIAMETER_INDEX, row));
            String id = data.getValue("FPOGID", row);
            leftSum += leftSize;
            rightSum += rightSize;
            bothSum += (leftSize + rightSize) / 2.0;
        }
        double leftAverage = leftSum / count;
        double rightAverage = rightSum / count;
        double bothAverage = bothSum / count;

        results.put(
            "total_number_of_valid_recordings", //Output Header
            String.valueOf(data.rowCount())
            );

        results.put(
            "average_pupil_size_of_left_eye", //Output Header
            String.valueOf(leftAverage)
            );    

        results.put(
            "average_pupil_size_of_right_eye", //Output Header
            String.valueOf(rightAverage)
            );    

        results.put(
            "average_pupil_size_of_both_eyes", //Output Header
            String.valueOf(bothAverage)
            );    
        return results;
    }
}