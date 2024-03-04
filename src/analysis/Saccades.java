package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Saccades {
    final static String DURATION_INDEX = "FPOGD";
    final static String TIMESTAMP_INDEX = "FPOGS";
    final static String FIXATIONID_INDEX = "FPOGID";
    final static String FIXATIONX_INDEX = "FPOGX";
    final static String FIXATIONY_INDEX = "FPOGY";
    
    static public LinkedHashMap<String,String> analyze(DataEntry data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();

        ArrayList<Double> allFixationDurations = new ArrayList<>();
        ArrayList<Double[]> saccadeDetails = new ArrayList<>();
        ArrayList<Object> allCoordinates = new ArrayList<>();
        ArrayList<List<Double[]>> positionProfiles = new ArrayList<List<Double[]>>();
        
        for (int row = 0; row < data.rowCount(); row++) {
            Double fixationDurationSeconds = Double.valueOf(data.getValue(DURATION_INDEX, row));;

            Double[] eachSaccadeDetail = new Double[3];
            eachSaccadeDetail[0] = Double.valueOf(data.getValue(TIMESTAMP_INDEX, row));
            eachSaccadeDetail[1] = Double.valueOf(data.getValue(DURATION_INDEX, row));
            eachSaccadeDetail[2] = Double.valueOf(data.getValue(FIXATIONID_INDEX, row));
            saccadeDetails.add(eachSaccadeDetail);

            Double[] eachCoordinate = new Double[3];
            eachCoordinate[0] = Double.valueOf(data.getValue(FIXATIONX_INDEX, row));
            eachCoordinate[1] = Double.valueOf(data.getValue(FIXATIONY_INDEX, row));
            eachCoordinate[2] = Double.valueOf(data.getValue(FIXATIONID_INDEX, row));
            allCoordinates.add(eachCoordinate);
            allFixationDurations.add(fixationDurationSeconds);
        }
        
        Double[] allSaccadeLengths = getAllSaccadeLength(allCoordinates);
        ArrayList<Double> allSaccadeDurations = getAllSaccadeDurations(saccadeDetails);

        
        results.put(
            "total number of saccades", //Output Header
            String.valueOf(allSaccadeLengths.length) //Output Value
            );

        results.put(
            "sum of all saccade length", 
            String.valueOf(DescriptiveStats.getSum(allSaccadeLengths))
            );
        
        results.put(
            "mean saccade length", 
            String.valueOf(DescriptiveStats.getMean(allSaccadeLengths))
            );
        
        results.put(
            "median saccade length", 
            String.valueOf(DescriptiveStats.getMedian(allSaccadeLengths))
            );
        
        results.put(
            "StDev of saccade lengths", 
            String.valueOf(DescriptiveStats.getStDev(allSaccadeLengths))
            );
        
        results.put(
            "min saccade length", 
            String.valueOf(DescriptiveStats.getMin(allSaccadeLengths))
            );
        
        results.put(
            "max saccade length", 
            String.valueOf(DescriptiveStats.getMax(allSaccadeLengths))
            );

        results.put(
            "sum of all saccade durations", 
            String.valueOf(DescriptiveStats.getSumOfDoubles(allSaccadeDurations))
            );
        
        results.put(
            "mean saccade duration", 
            String.valueOf(DescriptiveStats.getMeanOfDoubles(allSaccadeDurations))
            );
        
        results.put(
            "median saccade duration", 
            String.valueOf(DescriptiveStats.getMedianOfDoubles(allSaccadeDurations))
            );

        results.put(
            "StDev of saccade durations", 
            String.valueOf(DescriptiveStats.getStDevOfDoubles(allSaccadeDurations))
            );
                
        results.put(
            "Min. saccade duration", 
            String.valueOf(DescriptiveStats.getMinOfDoubles(allSaccadeDurations))
            );

        results.put(
            "Max. saccade duration", 
            String.valueOf(DescriptiveStats.getMaxOfDoubles(allSaccadeDurations))
            );
            
        results.put(
            "scanpath duration", 
            String.valueOf(getScanpathDuration(allFixationDurations, allSaccadeDurations))
            );

        results.put(
            "fixation to saccade ratio", 
            String.valueOf(getFixationToSaccadeRatio(allFixationDurations, allSaccadeDurations))
            );

        /*
         *  headers.add("Average Peak Saccade Velocity");
            data.add(avgPeakSaccadeVelocity(inputFile, outputFile));
         */

        return results;
    } 
    

    public static Double[] getAllSaccadeLength(ArrayList<Object> allCoordinates) {
		ArrayList<Double> allSaccadeLengths = new ArrayList<Double>();
		int objectSize = allCoordinates.size();
		Double[] allLengths = new Double[(objectSize-1)];
		for(int i=0; i<objectSize; i++){
			Double[] earlyCoordinate = (Double[]) allCoordinates.get(i);

			if(i+1<objectSize){
				Double[] laterCoordinate = (Double[]) allCoordinates.get(i+1);
				if (earlyCoordinate[2] == laterCoordinate[2] - 1)
					allSaccadeLengths.add(Math.sqrt(Math.pow((laterCoordinate[0] - earlyCoordinate[0]), 2) + Math.pow((laterCoordinate[1] - earlyCoordinate[1]), 2)));
			}
		}
		
		allLengths = new Double[allSaccadeLengths.size()];
		return allSaccadeLengths.toArray(allLengths);
		//return allLengths;
	}

    public static ArrayList<Double> getAllSaccadeDurations(ArrayList<Double[]> saccadeDetails){
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
		double fixationDuration = DescriptiveStats.getSumOfDoubles(allFixationDurations);
		double saccadeDuration = DescriptiveStats.getSumOfDoubles(allSaccadeDurations);
		return fixationDuration/saccadeDuration;
	}

    /*
	 * Returns the peak velocity of a given saccade calculated using a two point central difference algorithm.
	 * 
	 * @param	saccadePoints	A list of saccade data points, where each data point is a double array. 
	 * 							[0] = X position
	 * 							[1] = Y position
	 * 							[2] = Time of data point
	 * 
	 * @return	The peak velocity of a saccade
	 */
	public static double getPeakVelocity(List<Double[]> saccadePoints) {
		if (saccadePoints.size() == 0 || saccadePoints.size() == 1) {
			return 0;
		}
		
		double peakVelocity = 0;
		double conversionRate = 0.0264583333; // Convert from pixels to cms
		double velocityThreshold = 700; // Maximum possible saccadic velocity
        int participantDistance = 65; // assume an average distance of 65cm from the participant to the screen
		
		for (int i = 1; i < saccadePoints.size(); i++) {
			Double[] currPoint = saccadePoints.get(i);
			Double[] prevPoint = saccadePoints.get(i - 1);

			double x1 = currPoint[0];
			double y1 = currPoint[1];
			double x2 = prevPoint[0];
			double y2 = prevPoint[1];
			
			double dx = Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2)) * conversionRate;
			double dt = Math.abs(currPoint[2] - prevPoint[2]);
			double amplitude = 180/Math.PI * Math.atan(dx/participantDistance);
			
			double velocity = amplitude/dt;
			
			if (velocity > peakVelocity && velocity <= velocityThreshold) {
				peakVelocity = velocity;
			}
		}
		
		return peakVelocity;
	}
}
