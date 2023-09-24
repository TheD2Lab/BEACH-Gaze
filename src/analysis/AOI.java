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

import org.netlib.util.doubleW;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class AOI {

	private static final String filler = "~";
	
	public static void processAOIs(String inputFile, String outputLocation, String name, int SCREEN_WIDTH, int SCREEN_HEIGHT) throws IOException, CsvValidationException {		
		try {
			// Read input CSV file and initalize the column indexes for the data needed
			FileReader fileReader = new FileReader(inputFile);
			CSVReader csvReader = new CSVReader(fileReader);
			String[] nextLine = csvReader.readNext();
			
			// Locate the indexes for required fields
			Indexes csvIndexes = new Indexes();
			
			for (int i = 0; i < nextLine.length; i++) {
				String header = nextLine[i];
				
				if (header.contains("TIME") && !header.contains("TICK")) {
					csvIndexes.timeIndex = i;
					continue;
				}
				switch(header) {
					case "AOI":
						csvIndexes.aoiIndex = i;
						break;
					case "FPOGX":
						csvIndexes.xIndex = i;
						break;
					case "FPOGY":
						csvIndexes.yIndex = i;
						break;
					case "FPOGD":
						csvIndexes.fixDurIndex = i;
						break;
					case "FPOGID":
						csvIndexes.fixIdIndex = i;
						break;
					case "SACCADE_PV":
						csvIndexes.peakVelocityIndex = i;
						break;
					case "BKPMIN":
						csvIndexes.blinkRateIndex = i;
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
				totalFixDuration += Double.valueOf(nextLine[csvIndexes.fixDurIndex]);
				String aoi = nextLine[csvIndexes.aoiIndex];
				if (aoi.equals("")) {
					prevAoi = aoi;
					continue;
				}
				else {
					if (!map.containsKey(aoi))
						map.put(aoi, new ArrayList<String[]>());
					map.get(aoi).add(nextLine);
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

			// create AOI directory
			new File(outputLocation).mkdirs();
			String aoiFdxResults = outputLocation + name + "_aoi_graphFDXResults.csv";
			writeFDXResults(aoiFdxResults, map, SCREEN_WIDTH, SCREEN_HEIGHT, csvIndexes, totalFixations, totalFixDuration);

			String transFeatures = outputLocation + name + "_aoi_transitionFeatures.csv";
			writeTransitions(transFeatures, aoiTransitions);

			String proportionateFeatures = outputLocation + name + "_aoi_proportionateFeatures.csv";
			writeProportionate(proportionateFeatures, map, csvIndexes, totalFixations, totalFixDuration);

		}
		catch(FileNotFoundException e) {
			fileNotFoundMessage(inputFile, e);
	   }
		catch(IOException e) {
			ioExceptionMessage(inputFile, e);
	   }
	}

	private static void writeFDXResults(String outputFile, HashMap<String, ArrayList<String[]>> map, int SCREEN_WIDTH, int SCREEN_HEIGHT,
		Indexes csvIndexes, double totalFixations, double totalFixDuration) {
		
		try {
			// Initializing output writers
			FileWriter outputFileWriter = new FileWriter(outputFile);
			CSVWriter outputCSVWriter = new CSVWriter(outputFileWriter);
			
			// Initialize list of headers and write it to the output .csv file
			ArrayList<String> headers = new ArrayList<>();
			headers.add("AOI Name");
			
			// fixation columns
			headers.add("total number of fixations");
			headers.add("sum of all fixation duration");
			headers.add("mean fixation duration (ms)");
			headers.add("median fixation duration (ms)");
			headers.add(" StDev of fixation durations (ms)");
			headers.add("Min. fixation duration (ms)");
			headers.add("Max. fixation duration (ms)");

			// saccade columns
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

			// fixation v. saccade
			headers.add("scanpath duration");
			headers.add("fixation to saccade ratio");

			// absolute degrees
			headers.add("sum of all absolute degrees");
			headers.add("mean absolute degree");
			headers.add("median absolute degree");
			headers.add("StDev of absolute degrees");
			headers.add("min absolute degree");
			headers.add("max absolute degree");

			// relative degrees
			headers.add("sum of all relative degrees");
			headers.add("mean relative degree");
			headers.add("median relative degree");
			headers.add("StDev of relative degrees");
			headers.add("min relative degree");
			headers.add("max relative degree");


			headers.add("convex hull area");
			headers.add("Average Peak Saccade Velocity");
			
			outputCSVWriter.writeNext(headers.toArray(new String[headers.size()]));

			// Iterate through each AOI and calculate their gaze analytics
			for (String aoi : map.keySet()) {
				// Data row for output .csv file
				ArrayList<String> data = new ArrayList<>();
				data.add(aoi);
				
				ArrayList<String[]> aoiData = map.get(aoi);
				ArrayList<Point2D.Double> allPoints = new ArrayList<Point2D.Double>();
				ArrayList<Double> fixationDurations = new ArrayList<Double>();
				ArrayList<Object> allCoordinates = new ArrayList<Object>();
				ArrayList<Double[]> saccadeDetails = new ArrayList<Double[]>();
				
				// Iterate through each AOI data to populate the above lists
				for (int i = 0; i < aoiData.size(); i++) {
					String[] entry = aoiData.get(i);
					
					// Initalize details about each fixation
					double x = Double.valueOf(entry[csvIndexes.xIndex]) * SCREEN_WIDTH;
					double y = Double.valueOf(entry[csvIndexes.yIndex]) * SCREEN_HEIGHT;
					double id = Double.valueOf(entry[csvIndexes.fixIdIndex]);
					double duration = Double.valueOf(entry[csvIndexes.fixDurIndex]);
					double timestamp = Double.valueOf(csvIndexes.timeIndex);
					
					
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
					fixationDurations.add(duration);
					
				}
				Double[] allSaccadeLengths = saccade.getAllSaccadeLength(allCoordinates);
				ArrayList<Double> allSaccadeDurations = saccade.getAllSaccadeDurations(saccadeDetails);
				ArrayList<Double> allAbsoluteDegrees = angle.getAllAbsoluteAngles(allCoordinates);
				ArrayList<Double> allRelativeDegrees = angle.getAllRelativeAngles(allCoordinates);
				
				
				data.add(String.valueOf(fixationDurations.size()));
				// check that there were fixations
				if (fixationDurations.size() > 0) {
					double aoiTotalDuration = descriptiveStats.getSumOfDoubles(fixationDurations);
					data.add(String.valueOf(aoiTotalDuration));
					data.add(String.valueOf(descriptiveStats.getMeanOfDoubles(fixationDurations)));
					data.add(String.valueOf(descriptiveStats.getMedianOfDoubles(fixationDurations)));
					data.add(String.valueOf(descriptiveStats.getStDevOfDoubles(fixationDurations)));
					data.add(String.valueOf(descriptiveStats.getMinOfDoubles(fixationDurations)));
					data.add(String.valueOf(descriptiveStats.getMaxOfDoubles(fixationDurations)));
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

					data.add(String.valueOf(fixation.getScanpathDuration(fixationDurations, allSaccadeDurations)));
					data.add(String.valueOf(fixation.getFixationToSaccadeRatio(fixationDurations, allSaccadeDurations)));
				} else {
					for (int i = 0; i < 14; i++) {
						data.add(filler);
					}
				}
				
				// absolute degrees 
				if (allAbsoluteDegrees.size() > 0) {
					data.add(String.valueOf(descriptiveStats.getSumOfDoubles(allAbsoluteDegrees)));
					data.add(String.valueOf(descriptiveStats.getMeanOfDoubles(allAbsoluteDegrees)));
					data.add(String.valueOf(descriptiveStats.getMedianOfDoubles(allAbsoluteDegrees)));
					data.add(String.valueOf(descriptiveStats.getStDevOfDoubles(allAbsoluteDegrees)));
					data.add(String.valueOf(descriptiveStats.getMinOfDoubles(allAbsoluteDegrees)));
					data.add(String.valueOf(descriptiveStats.getMaxOfDoubles(allAbsoluteDegrees)));
				} else {
					for (int i = 0; i < 6; i++) {
						data.add(filler);
					}
				}

				// relative degrees
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

				// Calculate the convex hull and its area 
				if (allPoints.size() > 2 ) {
				List<Point2D.Double> boundingPoints = convexHull.getConvexHull(allPoints);
				Point2D[] points = fixation.listToArray(boundingPoints);
				data.add(String.valueOf(convexHull.getPolygonArea(points)));
				} else {
					data.add(filler);
				}
				
				data.add(String.valueOf(getAvgPeakSaccadeVelocity(map.get(aoi), csvIndexes.peakVelocityIndex)));
				
				
				outputCSVWriter.writeNext(data.toArray(new String[data.size()]));
			}
			
			outputCSVWriter.close();
			System.out.println("Done writing AOI fixation results to" + outputFile);
			systemLogger.writeToSystemLog(Level.INFO, WindowOperations.class.getName(), "Done writing AOI fixation results to " + outputFile);
		}
		catch(FileNotFoundException e) {
			fileNotFoundMessage(outputFile, e);
		}
		catch(IOException e) {
			ioExceptionMessage(outputFile, e);
		}
	}

	private static void writeProportionate(String outputFile, HashMap<String, ArrayList<String[]>> map,
	Indexes csvIndexes, double totalFixations, double totalFixDuration) {
			ArrayList<String> headers = new ArrayList<>();

		try {
			FileWriter outputFileWriter = new FileWriter(new File (outputFile));
			CSVWriter outputCSVWriter = new CSVWriter(outputFileWriter);

			headers.add("AOI name");
			headers.add("Proportion of fixations spent in AOI");
			headers.add("Proportion of fixations durations spent in AOI");

			for (String aoi : map.keySet()) {
				ArrayList<String> data = new ArrayList<>();
				ArrayList<String[]> aoiData = map.get(aoi);
				ArrayList<Double> fixationDurations = new ArrayList<Double>();
				double aoiTotalDuration = 0;				

				for (String[] entry : aoiData) {
					double duration = Double.valueOf(entry[csvIndexes.fixDurIndex]);
					fixationDurations.add(duration);
					aoiTotalDuration += duration;
				}

				data.add(aoi);
				data.add(String.valueOf(fixationDurations.size()/totalFixations));
				data.add(String.valueOf(aoiTotalDuration/totalFixDuration));

				// Write the data into the .csv file as a new row
				outputCSVWriter.writeNext(data.toArray(new String[data.size()]));
			}

			outputCSVWriter.close();
			System.out.println("Done writing AOI proportionate features to " + outputFile);
			systemLogger.writeToSystemLog(Level.INFO, WindowOperations.class.getName(), "Done writing AOI proportionate features to " + outputFile);

		}
		catch(FileNotFoundException e) {
			fileNotFoundMessage(outputFile, e);
	   }
		catch(IOException e) {
			ioExceptionMessage(outputFile, e);
	   }
	}
	
	private static void writeTransitions(String outputFile, HashMap<String, Integer> aoiTransitions) {

		double totalTrans = getTotalTransitions(aoiTransitions);
		ArrayList<String> headers = new ArrayList<>();

		try {
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
			System.out.println("Done writing AOI transition data to" + outputFile);
			systemLogger.writeToSystemLog(Level.INFO, WindowOperations.class.getName(), "Done writing AOI transition data to " + outputFile );
		}
		catch(FileNotFoundException e) {
			fileNotFoundMessage(outputFile, e);
	   }
		catch(IOException e) {
			ioExceptionMessage(outputFile, e);
	   }
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

	private static void ioExceptionMessage(String fileName, IOException e) {
		systemLogger.writeToSystemLog(Level.WARNING, WindowOperations.class.getName(), "Error reading file " + fileName + "\n" + e.toString());
	   System.out.println("Error reading file '" + fileName + "'");
	}

	private static void fileNotFoundMessage(String fileName, FileNotFoundException e) {
		systemLogger.writeToSystemLog(Level.WARNING, WindowOperations.class.getName(), "Unable to open file " + fileName + "\n" + e.toString());
		System.out.println("Unable to open file '" + fileName + "'");
	}

	static class Indexes {
		int aoiIndex = -1, xIndex = -1, yIndex = -1, fixDurIndex = -1, fixIdIndex = -1, timeIndex = -1, peakVelocityIndex = -1, blinkRateIndex = -1;
	}

}