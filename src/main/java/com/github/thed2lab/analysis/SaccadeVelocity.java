package com.github.thed2lab.analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SaccadeVelocity {
    final static String TIME_INDEX = "TIME";
    final static String FIXATIONID_INDEX = "FPOGID";
    final static String FIXATIONX_INDEX = "FPOGX";
    final static String FIXATIONY_INDEX = "FPOGY";
    final static String FIXATION_VALIDITY_INDEX = "FPOGV";
    final static String FIXATION_DURATION_INDEX = "FPOGD";

    /**
     * Calculates the average peak saccade velocity. Iterates over all rows of a participant’s gaze.
     * If the current row’s fixation ID (“FID”) and the next consecutive fixation ID both appear in
     * the fixation data as well as if the fixation validity (“FPOGV”) is set to 0, then the row is
     * considered part of a saccade.
     * @param allGazeData all gaze data, filtered by validity with screen size applied. When calculating
     * for AOIs, use all gaze data, not just the AOI specific gaze data.
     * @param fixationData the gaze data, filtered by fixation and validity with screen size applied.
     * When calculating for AOIs, use only fixation data that occurs within the AOI.
     * @return average peak saccade velocity’s header mapped to the calculated value as a {@code String}.
     */
    static public LinkedHashMap<String,String> analyze(DataEntry allGazeData, DataEntry fixationData) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();

        List<List<Double[]>> positionProfiles = new ArrayList<List<Double[]>>();
        List<Double[]> positionProfile = new ArrayList<Double[]>();
        ArrayList<Double> peakSaccadeVelocities = new ArrayList<Double>();
        int fixDataIndex = 0;
        int gazeDataIndex = 0;
        int targetFixId = -1;

        while ((fixDataIndex < fixationData.rowCount() - 1) && (gazeDataIndex < allGazeData.rowCount())) {
            // Get the fixation Id of the next saccade that occurs completely within portion of screen (whole or AOI)
            while (fixDataIndex < fixationData.rowCount() - 1) {
                int curFixId = Integer.parseInt(fixationData.getValue(FIXATIONID_INDEX, fixDataIndex));
                int nextFixId = Integer.parseInt(fixationData.getValue(FIXATIONID_INDEX, fixDataIndex + 1));
                fixDataIndex++;
                if (nextFixId == curFixId + 1) {
                    targetFixId = curFixId;
                    break;
                }
            }

            while (gazeDataIndex < allGazeData.rowCount()) {
                int curId = Integer.parseInt(allGazeData.getValue(FIXATIONID_INDEX, gazeDataIndex));
                if (curId < targetFixId) {
                    gazeDataIndex++;
                    continue;
                } else if (curId > targetFixId) {
                    break; // could not find target, look for next fixation
                }

                boolean saccade = Integer.parseInt(allGazeData.getValue(FIXATION_VALIDITY_INDEX, gazeDataIndex)) == 0 ? true : false;
                // Check if not a saccade
                if (!saccade) {
                    gazeDataIndex++;
                    continue; // go to next data point
                }

                Double x = Double.parseDouble(allGazeData.getValue(FIXATIONX_INDEX, gazeDataIndex));
                Double y = Double.parseDouble(allGazeData.getValue(FIXATIONY_INDEX, gazeDataIndex));
                Double t = Double.parseDouble(allGazeData.getValue(TIME_INDEX, gazeDataIndex));
                positionProfile.add(new Double[] {x, y, t});
                gazeDataIndex++;
            }

            if (positionProfile.size() > 0) {
                positionProfiles.add(positionProfile);
                positionProfile = new ArrayList<Double[]>();
            }
            
        }

        for (int i = 0; i < positionProfiles.size(); i++) {
            List<Double[]> saccadePoints = positionProfiles.get(i);
            Double peakSaccadeVelocity = getPeakVelocity(saccadePoints);
            if (!Double.isNaN(peakSaccadeVelocity)) peakSaccadeVelocities.add(peakSaccadeVelocity);
        }

        results.put(
            "average_peak_saccade_velocity",
            String.valueOf(DescriptiveStats.getMeanOfDoubles(peakSaccadeVelocities))
        );

        return results;
    }

    /**
	 * Returns the peak velocity of a given saccade calculated using a two point central difference algorithm.
	 * 
	 * @param	saccadePoints	A list of saccade data points, where each data point is a double array. 
	 * 							[0] = X position
	 * 							[1] = Y position
	 * 							[2] = Time of data point
	 * 
	 * @return	The peak velocity of a saccade
	 */
	static double getPeakVelocity(List<Double[]> saccadePoints) {
		if (saccadePoints.size() == 0 || saccadePoints.size() == 1) {
			return Double.NaN;
		}
		
		final double PIXELS_TO_CM = 0.0264583333; // Convert from pixels to cms
		final double VELOCITY_THRESHOLD = 700; // Maximum possible saccadic velocity
        final double PARTICIPANT_DISTANCE = 65; // assume an average distance of 65cm from the participant to the screen
        final double RADIAN_TO_DEGREES = 180/Math.PI;
        double peakVelocity = 0;
		
		for (int i = 1; i < saccadePoints.size(); i++) {
			Double[] currPoint = saccadePoints.get(i);
			Double[] prevPoint = saccadePoints.get(i - 1);

			double x1 = currPoint[0];
			double y1 = currPoint[1];
			double x2 = prevPoint[0];
			double y2 = prevPoint[1];
			
			double dx = Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2)) * PIXELS_TO_CM;
			double dt = Math.abs(currPoint[2] - prevPoint[2]);
			double amplitude = RADIAN_TO_DEGREES * Math.atan(dx/PARTICIPANT_DISTANCE);
			
			double velocity = amplitude/dt;
			
			if (velocity > peakVelocity && velocity <= VELOCITY_THRESHOLD) {
				peakVelocity = velocity;
			}
		}
		
		return peakVelocity;
	}
}
