package com.github.thed2lab.analysis;

import static com.github.thed2lab.analysis.Constants.FIXATION_ID;
import static com.github.thed2lab.analysis.Constants.FIXATION_VALIDITY;
import static com.github.thed2lab.analysis.Constants.FIXATION_X;
import static com.github.thed2lab.analysis.Constants.FIXATION_Y;
import static com.github.thed2lab.analysis.Constants.LEFT_PUPIL_DIAMETER;
import static com.github.thed2lab.analysis.Constants.LEFT_PUPIL_VALIDITY;
import static com.github.thed2lab.analysis.Constants.RIGHT_PUPIL_DIAMETER;
import static com.github.thed2lab.analysis.Constants.RIGHT_PUPIL_VALIDITY;

import java.util.ArrayList;
import java.util.List;

// Note: Order used for data filtering matters and can invalidate DataEntrys if used incorrectly
// Must first filter by fixations, then validate, then apply screen size; otherwise, fixation algorithm or validating algorithm will fail
public class DataFilter {
    static public DataEntry filterByFixations(DataEntry data) {
        DataEntry filtered = new DataEntry(data.getHeaders());

        List<String> lastValidFixation = null;
        int currFixation = 1;

        for (int row = 0; row < data.rowCount(); row++) {
            int fixationID = Integer.parseInt(data.getValue(FIXATION_ID, row));
            int fixationValidity = Integer.parseInt(data.getValue(FIXATION_VALIDITY, row));
            if (fixationID != currFixation) {
                if (lastValidFixation != null) filtered.process(lastValidFixation);
                if (fixationValidity == 1) lastValidFixation = data.getRow(row); // Edge case; check to see if the first line associated with a given fixation is valid
                currFixation = fixationID;
            } else if (fixationID == currFixation && fixationValidity == 1) {
                lastValidFixation = data.getRow(row);
            }
        }
        // Edge case: check last valid fixation is the last line of file or followed by only saccades
        if (filtered.rowCount() != 0 && lastValidFixation != filtered.getRow(filtered.rowCount()-1)) {
            filtered.process(lastValidFixation);    // add fixation if it wasn't already added
        }
        
        return filtered;
    }

    /**
     * Cleanses data by filtering out invalid data. Valid data entries must occur within
     * the bounds of the monitor and have humanly possible pupil dilation.
     * @param data data to be cleansed.
     */
    public static DataEntry filterByValidity(DataEntry data) {
        // GazePoint scales point of gaze location from 0 to 1 when on screen
        final int MAX_SCREEN_WIDTH = 1;
        final int MAX_SCREEN_HEIGHT = 1;
        return filterByValidity(data, MAX_SCREEN_WIDTH, MAX_SCREEN_HEIGHT);
    }

    /**
     * Cleanses data by filtering out invalid data. Valid data entries must occur within
     * the bounds of the monitor and have humanly possible pupil dilation.
     * @param data data to be cleansed.
     * @param screenWidth screen width scaler that was previously applied to the data.
     * @param screenHeight screen height scaler that was previously applied to the data.
     */
    public static DataEntry filterByValidity(DataEntry data, int screenWidth, int screenHeight) {
        // humanly possible pupil diameter is between 2 and 8 mm
        final int MIN_DIAMETER = 2;
        final int MAX_DIAMETER = 8;
        final int MAX_PUPIL_DIFF = 1;   
        // GazePoint scales point of gaze location from 0 to 1 when on screen. The data
        // may have been scaled so we only hardcode the 0
        final int MIN_SCREEN_DIM = 0;

        DataEntry filtered = new DataEntry(data.getHeaders());
        for (int rowNum = 0; rowNum < data.rowCount(); rowNum++) {
            // Note: It is extremely slow to parse a string over and over again
            // Check if Gazepoint could detect the pupils
            boolean leftValid = Integer.parseInt(data.getValue(LEFT_PUPIL_VALIDITY, rowNum)) == 1;
            boolean rightValid = Integer.parseInt(data.getValue(RIGHT_PUPIL_VALIDITY, rowNum)) == 1;
            if (!(leftValid && rightValid)) {
                continue;   // skip invalid entry
            } 
            // Check if POG is on the screen
            float xCoordinate = Float.parseFloat(data.getValue(FIXATION_X ,rowNum));
            float yCoordinate = Float.parseFloat(data.getValue(FIXATION_Y ,rowNum));
            if (xCoordinate < MIN_SCREEN_DIM || xCoordinate > screenWidth) {
                continue; // off screen in x-direction, invalid
            } else if (yCoordinate < MIN_SCREEN_DIM || yCoordinate > screenHeight) {
                continue; // off screen in y-direction, invalid entry
            }
            // Check if pupils are valid sizes individually and compared to each other.
            float leftDiameter = Float.parseFloat(data.getValue(LEFT_PUPIL_DIAMETER, rowNum));
            float rightDiameter = Float.parseFloat(data.getValue(RIGHT_PUPIL_DIAMETER, rowNum));
            if (
                leftDiameter >= MIN_DIAMETER
                && leftDiameter <= MAX_DIAMETER
                && rightDiameter >= MIN_DIAMETER
                && rightDiameter <= MAX_DIAMETER
                && (Math.abs(leftDiameter-rightDiameter) <= MAX_PUPIL_DIFF)
            ) {
                filtered.process(data.getRow(rowNum));
            }
        }
        return filtered;
    }

    static public DataEntry applyScreenSize(DataEntry data, int screenWidth, int screenHeight) {
        DataEntry filtered = new DataEntry(data.getHeaders());

        for (int row = 0; row < data.rowCount(); row++) {
            List<String> currentRow = data.getRow(row);
            List<String> newRow = new ArrayList<String>();
            newRow.addAll(currentRow);

            int fixationXIndex = data.getHeaderIndex(FIXATION_X);
            int fixationYIndex = data.getHeaderIndex(FIXATION_Y);

            newRow.set(fixationXIndex,String.valueOf(Double.valueOf(newRow.get(fixationXIndex)) * screenWidth));
            newRow.set(fixationYIndex,String.valueOf(Double.valueOf(newRow.get(fixationYIndex)) * screenHeight));

            filtered.process(newRow);
        }

        return filtered;
    }
}
