package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Saccades {
    // static public HashMap<String,String> analyze(DataEntry data) {
    //     for i in 
    // }

    /*
     * 		Double[] allSaccadeLengths = saccade.getAllSaccadeLength(allCoordinates);

			headers.add("total number of saccades");
			data.add(String.valueOf(allSaccadeLengths.length));

			headers.add("sum of all saccade length");
			data.add(String.valueOf(DescriptiveStats.getSum(allSaccadeLengths)));

			headers.add("mean saccade length");
			data.add(String.valueOf(DescriptiveStats.getMean(allSaccadeLengths)));


			headers.add("median saccade length");
			data.add(String.valueOf(DescriptiveStats.getMedian(allSaccadeLengths)));

			headers.add("StDev of saccade lengths");
			data.add(String.valueOf(DescriptiveStats.getStDev(allSaccadeLengths)));

			headers.add("min saccade length");
			data.add(String.valueOf(DescriptiveStats.getMin(allSaccadeLengths)));


			headers.add("max saccade length");
			data.add(String.valueOf(DescriptiveStats.getMax(allSaccadeLengths)));

			ArrayList<Double> allSaccadeDurations = saccade.getAllSaccadeDurations(saccadeDetails);

			headers.add("sum of all saccade durations");
			data.add(String.valueOf(DescriptiveStats.getSumOfDoubles(allSaccadeDurations)));

			headers.add("mean saccade duration");
			data.add(String.valueOf(DescriptiveStats.getMeanOfDoubles(allSaccadeDurations)));

			headers.add("median saccade duration");
			data.add(String.valueOf(DescriptiveStats.getMedianOfDoubles(allSaccadeDurations)));


			headers.add("StDev of saccade durations");
			data.add(String.valueOf(DescriptiveStats.getStDevOfDoubles(allSaccadeDurations)));

			headers.add("Min. saccade duration");
			data.add(String.valueOf(DescriptiveStats.getMinOfDoubles(allSaccadeDurations)));

			headers.add("Max. saccade duration");
			data.add(String.valueOf(DescriptiveStats.getMaxOfDoubles(allSaccadeDurations)));

			headers.add("scanpath duration");
			data.add(String.valueOf(getScanpathDuration(allFixationDurations, allSaccadeDurations)));


			headers.add("fixation to saccade ratio");
			data.add(String.valueOf(getFixationToSaccadeRatio(allFixationDurations, allSaccadeDurations)));
     */
   
    static public LinkedHashMap<String,String> analyze(ArrayList<List<String>> data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
        List<String> headers = data.get(0);
        
        int durationIndex = headers.indexOf("FPOGD");
        int timestampIndex = headers.indexOf("FPOGS");
        int fixationIDIndex = headers.indexOf("FPOGID");
        int fixationXIndex = headers.indexOf("FPOGX");
        int fixationYIndex = headers.indexOf("FPOGY");

        ArrayList<Double> allFixationDurations = new ArrayList<>();
        ArrayList<Double[]> saccadeDetails = new ArrayList<>();
        ArrayList<Object> allCoordinates = new ArrayList<>();
        int fixationCount = data.size() - 1;

        System.out.println("FixationCount: "+Integer.toString(fixationCount));
        
        for (int row = 1; row < data.size(); row++) {
            Double fixationDurationSeconds = Double.valueOf(data.get(row).get(durationIndex));

            Double[] eachSaccadeDetail = new Double[3];
            eachSaccadeDetail[0] = Double.valueOf(data.get(row).get(timestampIndex));
            eachSaccadeDetail[1] = Double.valueOf(data.get(row).get(durationIndex));
            eachSaccadeDetail[2] = Double.valueOf(data.get(row).get(fixationIDIndex));
            saccadeDetails.add(eachSaccadeDetail);

            Double[] eachCoordinate = new Double[3];
            eachCoordinate[0] = Double.valueOf(data.get(row).get(fixationXIndex));
            eachCoordinate[1] = Double.valueOf(data.get(row).get(fixationYIndex));
            eachCoordinate[2] = Double.valueOf(data.get(row).get(fixationIDIndex));
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
}
