/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.thed2lab.analysis;

import static com.github.thed2lab.analysis.Constants.FIXATION_ID;
import static com.github.thed2lab.analysis.Constants.FIXATION_VALIDITY;
import static com.github.thed2lab.analysis.Constants.FIXATION_X;
import static com.github.thed2lab.analysis.Constants.FIXATION_Y;
import static com.github.thed2lab.analysis.Constants.TIMESTAMP;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SaccadeVelocity {

    /**
     * Calculates the descriptive gaze measures for saccade velocity. Iterates over all rows of a participant’s gaze.
     * If the current row’s fixation ID (“FID”) and the next consecutive fixation ID both appear in
     * the fixation data as well as if the fixation validity (“FPOGV”) is set to 0, then the row is
     * considered part of a saccade.
     * @param allGazeData all gaze data, with screen size applied. When calculating for AOIs, use all
     * gaze data, not just the AOI specific gaze data.
     * @param fixationData the gaze data, filtered by fixation and validity with screen size applied.
     * When calculating for AOIs, use only fixation data that occurs within the AOI.
     * @return saccade velocity’s header mapped to the calculated value as a {@code String}.
     */
    static public LinkedHashMap<String,String> analyze(DataEntry allGazeData, DataEntry fixationData) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();

        List<List<Double[]>> positionProfiles = new ArrayList<List<Double[]>>();
        List<Double[]> positionProfile = new ArrayList<Double[]>();
        ArrayList<Double> peakSaccadeVelocities = new ArrayList<Double>();
        ArrayList<Double> meanSaccadeVelocities = new ArrayList<Double>();

        int fixDataIndex = 0;
        int gazeDataIndex = 0;
        int targetFixId = -1;

        while ((fixDataIndex < fixationData.rowCount() - 1) && (gazeDataIndex < allGazeData.rowCount())) {
            // Get the fixation Id of the next saccade that occurs completely within portion of screen (whole or AOI)
            while (fixDataIndex < fixationData.rowCount() - 1) {
                int curFixId = Integer.parseInt(fixationData.getValue(FIXATION_ID, fixDataIndex));
                int nextFixId = Integer.parseInt(fixationData.getValue(FIXATION_ID, fixDataIndex + 1));
                fixDataIndex++;
                if (nextFixId == curFixId + 1) {
                    targetFixId = curFixId;
                    break;
                }
            }

            while (gazeDataIndex < allGazeData.rowCount()) {
                int curId = Integer.parseInt(allGazeData.getValue(FIXATION_ID, gazeDataIndex));
                if (curId < targetFixId) {
                    gazeDataIndex++;
                    continue;
                } else if (curId > targetFixId) {
                    break; // could not find target, look for next fixation
                }

                boolean saccade = Integer.parseInt(allGazeData.getValue(FIXATION_VALIDITY, gazeDataIndex)) == 0 ? true : false;
                // Check if not a saccade
                if (!saccade) {
                    gazeDataIndex++;
                    continue; // go to next data point
                }

                Double x = Double.parseDouble(allGazeData.getValue(FIXATION_X, gazeDataIndex));
                Double y = Double.parseDouble(allGazeData.getValue(FIXATION_Y, gazeDataIndex));
                Double t = Double.parseDouble(allGazeData.getValue(TIMESTAMP, gazeDataIndex));
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
            Double meanSaccadeVelocity = getMeanVelocity(saccadePoints);

            if (!Double.isNaN(peakSaccadeVelocity)) peakSaccadeVelocities.add(peakSaccadeVelocity);
            if (!Double.isNaN(meanSaccadeVelocity)) meanSaccadeVelocities.add(meanSaccadeVelocity);
        }

        // Peak saccade velocity
        results.put(
            "sum_peak_saccade_velocity",
            String.valueOf(DescriptiveStats.getSumOfDoubles(peakSaccadeVelocities))
        );
        results.put(
            "mean_peak_saccade_velocity",
            String.valueOf(DescriptiveStats.getMeanOfDoubles(peakSaccadeVelocities))
        );
        results.put(
            "median_peak_saccade_velocity",
            String.valueOf(DescriptiveStats.getMedianOfDoubles(peakSaccadeVelocities))
        );
        results.put(
            "std_peak_saccade_velocity",
            String.valueOf(DescriptiveStats.getStDevOfDoubles(peakSaccadeVelocities))
        );
        results.put(
            "min_peak_saccade_velocity",
            String.valueOf(DescriptiveStats.getMinOfDoubles(peakSaccadeVelocities))
        );
        results.put(
            "max_peak_saccade_velocity",
            String.valueOf(DescriptiveStats.getMaxOfDoubles(peakSaccadeVelocities))
        );

        // Mean saccade velocity
        results.put(
            "sum_mean_saccade_velocity",
            String.valueOf(DescriptiveStats.getSumOfDoubles(meanSaccadeVelocities))
        );
        results.put(
            "mean_mean_saccade_velocity",
            String.valueOf(DescriptiveStats.getMeanOfDoubles(meanSaccadeVelocities))
        );
        results.put(
            "median_mean_saccade_velocity",
            String.valueOf(DescriptiveStats.getMedianOfDoubles(meanSaccadeVelocities))
        );
        results.put(
            "std_mean_saccade_velocity",
            String.valueOf(DescriptiveStats.getStDevOfDoubles(meanSaccadeVelocities))
        );
        results.put(
            "min_mean_saccade_velocity",
            String.valueOf(DescriptiveStats.getMinOfDoubles(meanSaccadeVelocities))
        );
        results.put(
            "max_mean_saccade_velocity",
            String.valueOf(DescriptiveStats.getMaxOfDoubles(meanSaccadeVelocities))
        );

        return results;
    }

    /**
	 * Returns the peak velocity of a given saccade calculated using a two point difference algorithm.
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
        boolean foundValidVelocity = false;

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

			if (velocity <= VELOCITY_THRESHOLD) {
				if (velocity > peakVelocity) {
					peakVelocity = velocity;
				}
				foundValidVelocity = true;
			}
		}

		return foundValidVelocity ? peakVelocity : Double.NaN;
	}

    /**
	 * Returns the mean velocity of a given saccade calculated using a two point difference algorithm.
	 * Note: n data points produce n-1 velocity measurements between consecutive points.
	 *
	 * @param	saccadePoints	A list of saccade data points, where each data point is a double array.
	 * 							[0] = X position
	 * 							[1] = Y position
	 * 							[2] = Time of data point
	 *
	 * @return	The mean velocity of a saccade, excluding velocities above threshold. Returns NaN if fewer than 2 points.
	 */
    static double getMeanVelocity(List<Double[]> saccadePoints) {
		if (saccadePoints.size() == 0 || saccadePoints.size() == 1) {
			return Double.NaN;
		}
		
		final double PIXELS_TO_CM = 0.0264583333; // Convert from pixels to cms
		final double VELOCITY_THRESHOLD = 700; // Maximum possible saccadic velocity
        final double PARTICIPANT_DISTANCE = 65; // assume an average distance of 65cm from the participant to the screen
        final double RADIAN_TO_DEGREES = 180/Math.PI;
        double totalVelocity = 0;
        double discardedDataCount = 0;
		
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
			
            if (velocity <= VELOCITY_THRESHOLD) {
                totalVelocity += velocity;
            } else {
                discardedDataCount++;
            }
		}
		
		return totalVelocity/(saccadePoints.size() - 1 - discardedDataCount);
	}
}
