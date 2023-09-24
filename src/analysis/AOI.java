package analysis;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class AOI {

	private static final String filler = "N/A";
	
	public static void processAOIs(String inputFile, String outputLocation, int SCREEN_WIDTH, int SCREEN_HEIGHT) throws IOException, CsvValidationException {		
		try {
			System.out.println(inputFile);
			// Read input CSV file and initalize the column indexes for the data needed
			FileReader fileReader = new FileReader(inputFile);
			CSVReader csvReader = new CSVReader(fileReader);
			String[] nextLine = csvReader.readNext();
			
			// Locate the indexes for required fields
			int aoiIndex = -1, xIndex = -1, yIndex = -1, fixDurIndex = -1, fixIdIndex = -1, timeIndex = -1, peakVelocityIndex = -1, blinkRateIndex = -1;
			
			for (int i = 0; i < nextLine.length; i++) {
				String header = nextLine[i];
				
				if (header.contains("TIME") && !header.contains("TICK")) {
					timeIndex = i;
					continue;
				}
				switch(header) {
					case "AOI":
						aoiIndex = i;
						break;
					case "FPOGX":
						xIndex = i;
						break;
					case "FPOGY":
						yIndex = i;
						break;
					case "FPOGD":
						fixDurIndex = i;
						break;
					case "FPOGID":
						fixIdIndex = i;
						break;
					case "SACCADE_PV":
						peakVelocityIndex = i;
						break;
					case "BKPMIN":
						blinkRateIndex = i;
						break;
					default:
						break;
				}
			}
			
			// Iterate through input file and group points by AOI
			HashMap<String, ArrayList<String[]>> map = new HashMap<String, ArrayList<String[]>>();
			HashMap<String, Integer> aoiTransitions = new HashMap<>();
			double totalFixations = 0;
			double totalFixDuration = 0;
			String prevAoi = "";
			while ((nextLine = csvReader.readNext()) != null) {
				// If the data point is not part of an AOI, skip it
				// Else if we've already encountered this AOI, add it to it's corresponding list
				// Otherwise create a new list if it's a new AOI
				totalFixations++;
				totalFixDuration += Double.valueOf(nextLine[fixDurIndex]);
				String aoi = nextLine[aoiIndex];
				if (aoi.equals("")) {
					prevAoi = aoi;
					continue;
				}
				else if (map.containsKey(aoi))
					map.get(aoi).add(nextLine);
				else {
					String[] aois = aoi.split("-");
					
					for (int i = 0; i < aois.length; i++) {
						if (!map.containsKey(aois[i]))
							map.put(aois[i], new ArrayList<String[]>());
						map.get(aois[i]).add(nextLine);
					}
				}

				// count AOI transitions
				if (aoi.equals(prevAoi)) {
					continue;
				}
				else {
					String aoiPair = prevAoi + "-" + aoi;
					aoiTransitions.put(aoiPair, aoiTransitions.getOrDefault(aoiPair, 0)+1);
					prevAoi = aoi;
				}
			}

			csvReader.close();
			
			String aoiFdxResults = outputLocation; // + "_aoi_graphFDXResults.csv";
			writeFDXResults(aoiFdxResults, map, SCREEN_WIDTH, SCREEN_HEIGHT, xIndex, yIndex, fixDurIndex, fixIdIndex, timeIndex, peakVelocityIndex, totalFixations, totalFixDuration);

		}
		catch(FileNotFoundException e) {
			systemLogger.writeToSystemLog(Level.WARNING, WindowOperations.class.getName(), "Unable to open file " + inputFile + "\n" + e.toString());
			System.out.println("Unable to open file '" + inputFile + "'");
	   }
		catch(IOException e) {
			 systemLogger.writeToSystemLog(Level.WARNING, WindowOperations.class.getName(), "Error reading file " + inputFile + "\n" + e.toString());

	        System.out.println("Error reading file '" + inputFile + "'");
	   }
	}

private static void writeFDXResults(String outputFile, HashMap<String, ArrayList<String[]>> map, int SCREEN_WIDTH, int SCREEN_HEIGHT,
	int xIndex, int yIndex, int fixDurIndex, int fixIdIndex, int timeIndex, int peakVelocityIndex, double totalFixations, double totalFixDuration) throws IOException{
	
	// Initializing output writers
	FileWriter outputFileWriter = new FileWriter(new File (outputFile));
	CSVWriter outputCSVWriter = new CSVWriter(outputFileWriter);
	
	// Initialize list of headers and write it to the output .csv file
	ArrayList<String> headers = new ArrayList<>();
	headers.add("AOI Name");
	headers.add("Convex Hull Area");
	
	// Fixation columns
	headers.add("Fixation Count");
	headers.add("Proportion of fixations");
	headers.add("Total Duration");
	headers.add("Proportion of Duration");
	headers.add("mean fixation duration (ms)");
	headers.add("median fixation duration (ms)");
	headers.add("StDev of fixation durations (ms)");
	headers.add("Min. fixation duration (ms)");
	headers.add(" Max. fixation duration (ms)");
	
	// Saccade columns
	headers.add("total number of saccades");
	headers.add("sum of all saccade length");
	headers.add("mean saccade length");
	headers.add("median saccade length");
	headers.add("StDev of saccade lengths");
	headers.add("min saccade length");
	headers.add("max saccade length");
	headers.add("sum of all saccade durations");
	headers.add("mean saccade duration");
	headers.add("median saccade duration");
	headers.add("StDev of saccade durations");
	headers.add("Min. saccade duration");
	headers.add("Max. saccade duration");
	headers.add("scanpath duration");
	headers.add("fixation to saccade ratio");
	
	// Degree columns
	headers.add("sum of all absolute degrees");
	headers.add("mean absolute degree");
	headers.add("median absolute degree");
	headers.add("StDev of absolute degrees");
	headers.add("min absolute degree");
	headers.add("max absolute degree");
	headers.add("sum of all relative degrees");
	headers.add("mean relative degree");
	headers.add("median relative degree");
	headers.add("StDev of relative degrees");
	headers.add("min relative degree");
	headers.add("max relative degree");
	
	outputCSVWriter.writeNext(headers.toArray(new String[headers.size()]));

		// Iterate through each AOI and calculate their gaze analytics
		for (String aoi : map.keySet()) {
			// Data row for output .csv file
			ArrayList<String> data = new ArrayList<>();
			data.add(aoi);
			
			ArrayList<String[]> aoiData = map.get(aoi);
			ArrayList<Point2D.Double> allPoints = new ArrayList<Point2D.Double>();
			ArrayList<Double> allFixationDurations = new ArrayList<Double>();
			ArrayList<Object> allCoordinates = new ArrayList<Object>();
			ArrayList<Double[]> saccadeDetails = new ArrayList<Double[]>();
			
			// Iterate through each AOI data to populate the above lists
			for (int i = 0; i < aoiData.size(); i++) {
				String[] entry = aoiData.get(i);
				
				// Initalize details about each fixation
				double x = Double.valueOf(entry[xIndex]) * SCREEN_WIDTH;
				double y = Double.valueOf(entry[yIndex]) * SCREEN_HEIGHT;
				double id = Double.valueOf(entry[fixIdIndex]);
				double duration = Double.valueOf(entry[fixDurIndex]);
				double timestamp = Double.valueOf(timeIndex);
				
				
				// Add each point to a list
				Point2D.Double point = new Point2D.Double(x, y);
				allPoints.add(point);
				
				// Add each coordinate to a list
				Double[] coordinate = new Double[3];
				coordinate[0] = x;
				coordinate[1] = y;
				coordinate[2] = id;
				allCoordinates.add(coordinate);
				
				// Add each saccade detail into a list
				Double[] saccadeDetail = new Double[3];
				saccadeDetail[0] = timestamp;
				saccadeDetail[1] = duration;
				saccadeDetail[2] = id;
				saccadeDetails.add(saccadeDetail);
				
				// Add duration value to list
				allFixationDurations.add(duration);
				
			}
			Double[] allSaccadeLengths = saccade.getAllSaccadeLength(allCoordinates);
			ArrayList<Double> allSaccadeDurations = saccade.getAllSaccadeDurations(saccadeDetails);
			ArrayList<Double> allAbsoluteDegrees = angle.getAllAbsoluteAngles(allCoordinates);
			ArrayList<Double> allRelativeDegrees = angle.getAllRelativeAngles(allCoordinates);
			
			// Calculate the convex hull and its area 
			if (allPoints.size() > 2 ) {
			List<Point2D.Double> boundingPoints = convexHull.getConvexHull(allPoints);
			Point2D[] points = fixation.listToArray(boundingPoints);
			data.add(String.valueOf(convexHull.getPolygonArea(points)));
			} else {
				data.add(filler);
			}
			
			data.add(String.valueOf(allFixationDurations.size()));
			// check that there were fixations
			if (allFixationDurations.size() > 0) {
				data.add(String.valueOf(allFixationDurations.size()/totalFixations));
				double aoiTotalDuration = descriptiveStats.getSumOfDoubles(allFixationDurations);
				data.add(String.valueOf(aoiTotalDuration));
				data.add(String.valueOf(aoiTotalDuration/totalFixDuration));
				data.add(String.valueOf(descriptiveStats.getMeanOfDoubles(allFixationDurations)));
				data.add(String.valueOf(descriptiveStats.getMedianOfDoubles(allFixationDurations)));
				data.add(String.valueOf(descriptiveStats.getStDevOfDoubles(allFixationDurations)));
				data.add(String.valueOf(descriptiveStats.getMinOfDoubles(allFixationDurations)));
				data.add(String.valueOf(descriptiveStats.getMaxOfDoubles(allFixationDurations)));
			} else {
				for (int i = 0; i < 8; i++) {
					data.add(filler);
				}
			}
			
			// check that there were saccades
			data.add(String.valueOf(allSaccadeLengths.length));
			if (allSaccadeLengths.length > 0) {
				data.add(String.valueOf(descriptiveStats.getSum(allSaccadeLengths)));
				data.add(String.valueOf(descriptiveStats.getMean(allSaccadeLengths)));
				data.add(String.valueOf(descriptiveStats.getMedian(allSaccadeLengths)));
				data.add(String.valueOf(descriptiveStats.getStDev(allSaccadeLengths)));
				data.add(String.valueOf(descriptiveStats.getMin(allSaccadeLengths)));
				data.add(String.valueOf(descriptiveStats.getMax(allSaccadeLengths)));
				data.add(String.valueOf(descriptiveStats.getSumOfDoubles(allSaccadeDurations)));
				data.add(String.valueOf(descriptiveStats.getMeanOfDoubles(allSaccadeDurations)));
				data.add(String.valueOf(descriptiveStats.getMedianOfDoubles(allSaccadeDurations)));
				data.add(String.valueOf(descriptiveStats.getStDevOfDoubles(allSaccadeDurations)));
				data.add(String.valueOf(descriptiveStats.getMinOfDoubles(allSaccadeDurations)));
				data.add(String.valueOf(descriptiveStats.getMaxOfDoubles(allSaccadeDurations)));
				data.add(String.valueOf(fixation.getScanpathDuration(allFixationDurations, allSaccadeDurations)));
				data.add(String.valueOf(fixation.getFixationToSaccadeRatio(allFixationDurations, allSaccadeDurations)));
			} else {
				for (int i = 0; i < 14; i++) {
					data.add(filler);
				}
			}
			
			if (allAbsoluteDegrees.size() > 0) {
				data.add(String.valueOf(descriptiveStats.getSumOfDoubles(allAbsoluteDegrees)));
				data.add(String.valueOf(descriptiveStats.getMeanOfDoubles(allAbsoluteDegrees)));
				data.add(String.valueOf(descriptiveStats.getMedianOfDoubles(allAbsoluteDegrees)));
				data.add(String.valueOf(descriptiveStats.getStDevOfDoubles(allAbsoluteDegrees)));
				data.add(String.valueOf(descriptiveStats.getMinOfDoubles(allAbsoluteDegrees)));
			} else {
				for (int i = 0; i < 5; i++) {
					data.add(filler);
				}
			}

			if (allAbsoluteDegrees.size() > 0) {
				data.add(String.valueOf(descriptiveStats.getSumOfDoubles(allRelativeDegrees)));
				data.add(String.valueOf(descriptiveStats.getMeanOfDoubles(allRelativeDegrees)));
				data.add(String.valueOf(descriptiveStats.getMedianOfDoubles(allRelativeDegrees)));
				data.add(String.valueOf(descriptiveStats.getStDevOfDoubles(allRelativeDegrees)));
				data.add(String.valueOf(descriptiveStats.getMinOfDoubles(allRelativeDegrees)));
				data.add(String.valueOf(descriptiveStats.getMaxOfDoubles(allRelativeDegrees)));
			} else {
				for (int i = 0; i < 6; i++) {
					data.add(filler);
				}
			}
			
			data.add(String.valueOf(getAvgPeakSaccadeVelocity(map.get(aoi), peakVelocityIndex)));
			
			// Write the data into the .csv file as a new row
			outputCSVWriter.writeNext(data.toArray(new String[data.size()]));
		}
		
		outputCSVWriter.close();
		System.out.println("done writing AOI fixation results to" + outputFile);
		systemLogger.writeToSystemLog(Level.INFO, WindowOperations.class.getName(), "done writing AOI fixation results to " + outputFile);
	}
	
	private static void writeTransitions(String outputFile, HashMap<String, Integer> aoiTransitions) throws IOException{

		double totalTrans = getTotalTransitions(aoiTransitions);
		ArrayList<String> headers = new ArrayList<>();

		FileWriter outputFileWriter = new FileWriter(new File (outputFile));
		CSVWriter outputCSVWriter = new CSVWriter(outputFileWriter);

		headers.add("AOI Pair");
		headers.add("Transition Count");
		headers.add("Transition Proportion");

		outputCSVWriter.writeNext(headers.toArray(new String[headers.size()]));

		for (String key : aoiTransitions.keySet()) {
			ArrayList<String> data = new ArrayList<>();
			data.add(key);
			data.add(aoiTransitions.get(key).toString());
			String proportion = Double.toString(aoiTransitions.get(key)/ totalTrans);
			data.add(proportion);
			outputCSVWriter.writeNext(data.toArray(new String[data.size()]));
		}

		outputCSVWriter.close();
		outputFileWriter.close();
		System.out.println("done writing AOI transition data to" + outputFile);
      systemLogger.writeToSystemLog(Level.INFO, WindowOperations.class.getName(), "done writing AOI transition data to " + outputFile );
	}

	public static double getAvgPeakSaccadeVelocity(ArrayList<String[]> data, int peakVelocityIndex) {
		double total = 0;
		
		for (String[] entry : data) {
			total += Double.parseDouble(entry[peakVelocityIndex]);
		}
		
		return total / data.size();
	}


	private static int getTotalTransitions(HashMap<String, Integer> aoiTransitions) {
		int total = 0;
		for (int value : aoiTransitions.values()) {
			total += value;
		}
		return total;
	}

	private static double getTransProportions(int pairTransitions, int totalTransitions) {
		return pairTransitions / (double) totalTransitions;
	}	
}