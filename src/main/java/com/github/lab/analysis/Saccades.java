/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.lab.analysis;

import static com.github.lab.analysis.Constants.FIXATION_DURATION;
import static com.github.lab.analysis.Constants.FIXATION_ID;
import static com.github.lab.analysis.Constants.FIXATION_START;
import static com.github.lab.analysis.Constants.FIXATION_X;
import static com.github.lab.analysis.Constants.FIXATION_Y;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Saccades {
    
    static public LinkedHashMap<String,String> analyze(DataEntry data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();

        ArrayList<Double> allFixationDurations = new ArrayList<>();
        ArrayList<Double[]> saccadeDetails = new ArrayList<>();
        ArrayList<Coordinate> allCoordinates = new ArrayList<>();
        
        for (int row = 0; row < data.rowCount(); row++) {
            Double fixationDurationSeconds = Double.valueOf(data.getValue(FIXATION_DURATION, row));;

            Double[] eachSaccadeDetail = new Double[3];
            eachSaccadeDetail[0] = Double.valueOf(data.getValue(FIXATION_START, row));
            eachSaccadeDetail[1] = Double.valueOf(data.getValue(FIXATION_DURATION, row));
            eachSaccadeDetail[2] = Double.valueOf(data.getValue(FIXATION_ID, row));
            saccadeDetails.add(eachSaccadeDetail);

            Coordinate eachCoordinate = new Coordinate(
                Double.valueOf(data.getValue(FIXATION_X, row)),
                Double.valueOf(data.getValue(FIXATION_Y, row)),
                Integer.valueOf(data.getValue(FIXATION_ID, row))
            );
            allCoordinates.add(eachCoordinate);
            allFixationDurations.add(fixationDurationSeconds);
        }
        
        Double[] allSaccadeLengths = getAllSaccadeLengths(allCoordinates);
        ArrayList<Double> allSaccadeDurations = getAllSaccadeDurations(saccadeDetails);
        ArrayList<Double> allSaccadeAmplitudes = getAllSaccadeAmplitudes(allCoordinates);
        
        results.put(
            "total_number_of_saccades", //Output Header
            String.valueOf(allSaccadeLengths.length) //Output Value
            );

        results.put(
            "sum_of_all_saccade_lengths", 
            String.valueOf(DescriptiveStats.getSum(allSaccadeLengths))
            );
        
        results.put(
            "mean_saccade_length", 
            String.valueOf(DescriptiveStats.getMean(allSaccadeLengths))
            );
        
        results.put(
            "median_saccade_length", 
            String.valueOf(DescriptiveStats.getMedian(allSaccadeLengths))
            );
        
        results.put(
            "stdev_of_saccade_lengths", 
            String.valueOf(DescriptiveStats.getStDev(allSaccadeLengths))
            );
        
        results.put(
            "min_saccade_length", 
            String.valueOf(DescriptiveStats.getMin(allSaccadeLengths))
            );
        
        results.put(
            "max_saccade_length", 
            String.valueOf(DescriptiveStats.getMax(allSaccadeLengths))
            );

        results.put(
            "sum_of_all_saccade_durations", 
            String.valueOf(DescriptiveStats.getSumOfDoubles(allSaccadeDurations))
            );
        
        results.put(
            "mean_saccade_duration", 
            String.valueOf(DescriptiveStats.getMeanOfDoubles(allSaccadeDurations))
            );
        
        results.put(
            "median_saccade_duration", 
            String.valueOf(DescriptiveStats.getMedianOfDoubles(allSaccadeDurations))
            );

        results.put(
            "stdev_of_saccade_durations", 
            String.valueOf(DescriptiveStats.getStDevOfDoubles(allSaccadeDurations))
            );
                
        results.put(
            "min_saccade_duration", 
            String.valueOf(DescriptiveStats.getMinOfDoubles(allSaccadeDurations))
            );

        results.put(
            "max_saccade_duration", 
            String.valueOf(DescriptiveStats.getMaxOfDoubles(allSaccadeDurations))
            );
        
        results.put(
            "sum_of_all_saccade_amplitudes", 
            String.valueOf(DescriptiveStats.getSumOfDoubles(allSaccadeAmplitudes))
            );
        
        results.put(
            "mean_saccade_amplitude", 
            String.valueOf(DescriptiveStats.getMeanOfDoubles(allSaccadeAmplitudes))
            );
        
        results.put(
            "median_saccade_amplitude", 
            String.valueOf(DescriptiveStats.getMedianOfDoubles(allSaccadeAmplitudes))
            );

        results.put(
            "stdev_of_saccade_amplitude", 
            String.valueOf(DescriptiveStats.getStDevOfDoubles(allSaccadeAmplitudes))
            );
                
        results.put(
            "min_saccade_amplitude", 
            String.valueOf(DescriptiveStats.getMinOfDoubles(allSaccadeAmplitudes))
            );

        results.put(
            "max_saccade_amplitude", 
            String.valueOf(DescriptiveStats.getMaxOfDoubles(allSaccadeAmplitudes))
            );
            
        results.put(
            "scanpath_duration", 
            String.valueOf(getScanpathDuration(allFixationDurations, allSaccadeDurations))
            );

        results.put(
            "fixation_to_saccade_ratio", 
            String.valueOf(getFixationToSaccadeRatio(allFixationDurations, allSaccadeDurations))
            );

        return results;
    } 
    

    public static Double[] getAllSaccadeLengths(ArrayList<Coordinate> allCoordinates) {
        if (allCoordinates.size() == 0) return new Double[0];

		ArrayList<Double> allSaccadeLengths = new ArrayList<Double>();
		int objectSize = allCoordinates.size();
		Double[] allLengths = new Double[(objectSize-1)];
		for(int i=0; i<objectSize; i++){
			Coordinate earlyCoordinate = allCoordinates.get(i);

			if(i+1<objectSize){
				Coordinate laterCoordinate = allCoordinates.get(i+1);
				if (earlyCoordinate.fid == laterCoordinate.fid - 1)
					allSaccadeLengths.add(Math.sqrt(Math.pow((laterCoordinate.x - earlyCoordinate.x), 2) + Math.pow((laterCoordinate.y - earlyCoordinate.y), 2)));
			}
		}
		
		allLengths = new Double[allSaccadeLengths.size()];
		return allSaccadeLengths.toArray(allLengths);
	}

    public static ArrayList<Double> getAllSaccadeDurations(ArrayList<Double[]> saccadeDetails){
        if (saccadeDetails.size() == 0) return new ArrayList<Double>();

		ArrayList<Double> allSaccadeDurations = new ArrayList<>();
		for (int i=0; (i+1)<saccadeDetails.size(); i++){
			Double[] currentDetail = (Double[]) saccadeDetails.get(i);
			Double[] subsequentDetail = (Double[]) saccadeDetails.get(i+1);
			
			if (currentDetail[2] == subsequentDetail[2] - 1) {
				double currentTimestamp = currentDetail[0];
				double currentFixationDuration = currentDetail[1];
				double subsequentTimestamp = subsequentDetail[0];

				double eachSaccadeDuration = subsequentTimestamp - (currentTimestamp + currentFixationDuration);

				allSaccadeDurations.add(eachSaccadeDuration);
			}
		}
		return allSaccadeDurations;
	}

	public static double getScanpathDuration(ArrayList<Double> allFixationDurations, ArrayList<Double> allSaccadeDurations) {
		double fixationDuration = DescriptiveStats.getSumOfDoubles(allFixationDurations);
		double saccadeDuration = DescriptiveStats.getSumOfDoubles(allSaccadeDurations);
		return fixationDuration + saccadeDuration;
	}

	public static double getFixationToSaccadeRatio(ArrayList<Double> allFixationDurations, ArrayList<Double> allSaccadeDurations){
        if (allSaccadeDurations.size() == 0) return Double.NaN;

		double fixationDuration = DescriptiveStats.getSumOfDoubles(allFixationDurations);
		double saccadeDuration = DescriptiveStats.getSumOfDoubles(allSaccadeDurations);
		return fixationDuration/saccadeDuration;
	}

    public static ArrayList<Double> getAllSaccadeAmplitudes(ArrayList<Coordinate> allCoordinates) {
        if (allCoordinates.size() == 0 || allCoordinates.size() == 1) return new ArrayList<Double>();
        
        ArrayList<Double> allSaccadeAmplitudes = new ArrayList<>();

        final double PIXELS_TO_CM = 0.0264583333; // Convert from pixels to cms
        final double PARTICIPANT_DISTANCE = 65; // assume an average distance of 65cm from the participant to the screen
        final double RADIAN_TO_DEGREES = 180/Math.PI;

        for (int i = 1; i < allCoordinates.size(); i++) {
			Coordinate currCoordinate = allCoordinates.get(i);
			Coordinate prevCoordinate = allCoordinates.get(i - 1);

            if (prevCoordinate.fid == currCoordinate.fid - 1) {
                double x1 = currCoordinate.x;
                double y1 = currCoordinate.y;
                double x2 = prevCoordinate.x;
                double y2 = prevCoordinate.y;
                
                double dx = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) * PIXELS_TO_CM;
                double amplitude = RADIAN_TO_DEGREES * Math.atan(dx/PARTICIPANT_DISTANCE);
                
                allSaccadeAmplitudes.add(amplitude);
            }
		}
        
        return allSaccadeAmplitudes;
    }
}
