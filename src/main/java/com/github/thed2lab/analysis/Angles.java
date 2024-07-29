package com.github.thed2lab.analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Angles {
	final static String FIXATIONID_INDEX = "FPOGID";
	final static String FIXATIONX_INDEX = "FPOGX";
	final static String FIXATIONY_INDEX = "FPOGY";
	
	static public LinkedHashMap<String,String> analyze(DataEntry data) {
		LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
		ArrayList<Coordinate> allCoordinates = new ArrayList<>();

		for (int row = 0; row < data.rowCount(); row++) {
			Coordinate eachCoordinate = new Coordinate(
				Double.valueOf(data.getValue(FIXATIONX_INDEX, row)),
				Double.valueOf(data.getValue(FIXATIONY_INDEX, row)),
				Integer.valueOf(data.getValue(FIXATIONID_INDEX, row))
			);
			allCoordinates.add(eachCoordinate);
		}

		ArrayList<Double> allAbsoluteDegrees = getAllAbsoluteAngles(allCoordinates);
		ArrayList<Double> allRelativeDegrees = getAllRelativeAngles(allCoordinates);
		//Absolute Degrees
		results.put(
			"sum_of_all_absolute_degrees", //Output Header
			String.valueOf(DescriptiveStats.getSumOfDoubles(allAbsoluteDegrees))
			);

		results.put(
			"mean_absolute_degree", //Output Header
			String.valueOf(DescriptiveStats.getMeanOfDoubles(allAbsoluteDegrees))
			);

		results.put(
			"median_absolute_degree", //Output Header
			String.valueOf(DescriptiveStats.getMedianOfDoubles(allAbsoluteDegrees))
			);

		results.put(
			"stdev_of_absolute_degrees", //Output Header
			String.valueOf(DescriptiveStats.getStDevOfDoubles(allAbsoluteDegrees))
			);

		results.put(
			"min_absolute_degree", //Output Header
			String.valueOf(DescriptiveStats.getMinOfDoubles(allAbsoluteDegrees))
			);            

		results.put(
			"max_absolute_degree", //Output Header
			String.valueOf(DescriptiveStats.getMaxOfDoubles(allAbsoluteDegrees)) 
			);   
		//Relative Degrees
		results.put(
			"sum_of_all_relative_degrees", 
			String.valueOf(DescriptiveStats.getSumOfDoubles(allRelativeDegrees))
			);  

		results.put(
			"mean_relative_degree", //Output Header
			String.valueOf(DescriptiveStats.getMeanOfDoubles(allRelativeDegrees))
			);

		results.put(
			"median_relative_degree", //Output Header
			String.valueOf(DescriptiveStats.getMedianOfDoubles(allRelativeDegrees))
			);

		results.put(
			"stdev_of_relative_degrees", //Output Header
			String.valueOf(DescriptiveStats.getStDevOfDoubles(allRelativeDegrees))
			);

		results.put(
			"min_relative_degree", //Output Header
			String.valueOf(DescriptiveStats.getMinOfDoubles(allRelativeDegrees))
			);            

		results.put(
			"max_relative_degree", //Output Header
			String.valueOf(DescriptiveStats.getMaxOfDoubles(allRelativeDegrees)) 
			);   
		return results;
	}

	//given two points A & B on the screen with (X1, Y1) and (X2, Y2) respectively
	//the absolute saccade slope = |(Y2-Y1)|/|(X2-X1)|
	//and the absolute saccade arctangent = arctan(slope)
	//finally turn the arctangent into degrees
	//in the rare cases where the fixation B is a straight vertical line to fixation A,
	//in other words, A and B have the same X value,
	//90 degrees is returned as the absolute angle (with respect to X axis) since we cannot divide numbers by zero.
	public static ArrayList<Double> getAllAbsoluteAngles(ArrayList<Coordinate> allCoordinates){

		ArrayList<Double> allAbsoluteDegrees = new ArrayList<>();
		double absoluteDegree = 0.0;

		for(int i=0; i<allCoordinates.size(); i++){
			Coordinate earlyCoordinate = allCoordinates.get(i);

			if(i < allCoordinates.size()-1){

				Coordinate laterCoordinate = allCoordinates.get(i+1);

				// check fixations are consecutive
				if (earlyCoordinate.fid + 1 != laterCoordinate.fid) {
					continue;
				}

				double differenceInY = laterCoordinate.y - earlyCoordinate.y;
				double differenceInX = laterCoordinate.x - earlyCoordinate.x;

				if(differenceInX==0.0){
					//when A&B are in a straight vertical line
					absoluteDegree = 90.00;
				}else if(differenceInY==0.0){
					//when A&B are in a straight horizontal line
					absoluteDegree = 0.0;
				}else{
					//all other cases where A&B draw a sloppy line
					double absoluteSlope = Math.abs(differenceInY)/Math.abs(differenceInX);
					//returns the arctangent of a number as a value between -PI/2 and PI/2 radians
					double absoluteArctangent = Math.atan(absoluteSlope);
					absoluteDegree = Math.abs(Math.toDegrees(absoluteArctangent));
				}

				allAbsoluteDegrees.add(absoluteDegree);
			}
		}

		return allAbsoluteDegrees;
	}

	//given three points A, B and C with (X1, Y1) (X2, Y2) and (X3, Y3) respectively
	//the relative saccade angle = 180 degrees - ( arctan(|(Y2-Y1)/(X2-X1)|).toDegrees + arctan(|(Y3-Y2)/(X3-X2)|).toDegrees )
	//in cases where X1=X2=X3, the relative angle is 0 degree
	public static ArrayList<Double> getAllRelativeAngles(ArrayList<Coordinate> allCoordinates){

		ArrayList<Double> allRelativeDegrees = new ArrayList<>();
		double relativeDegree = 0.0;
		double firstDegree = 0.0;
		double secondDegree = 0.0;

		for(int i=1; i < allCoordinates.size()-1; i++){
			Coordinate previousCoordinate = allCoordinates.get(i-1);
			Coordinate currentCoordinate = allCoordinates.get(i);
			Coordinate nextCoordinate = allCoordinates.get(i+1);

			// check fixations are consecutive
			if (
				previousCoordinate.fid+1 != currentCoordinate.fid ||
				currentCoordinate.fid+1 != nextCoordinate.fid
			) { 
				continue; 
			}

			//degree between A and B
			double firstDifferenceInY = currentCoordinate.y-previousCoordinate.y;
			double firstDifferenceInX = currentCoordinate.x-previousCoordinate.x;

			//degree between B and C
			double secondDifferenceInY = nextCoordinate.y-currentCoordinate.y;
			double secondDifferenceInX = nextCoordinate.x-currentCoordinate.x;

			if ((firstDifferenceInX==0.0 && secondDifferenceInX==0.0)) {
				//when A, B and C are all in a straight line, either horizontally or vertically
				relativeDegree = 0.0;

			}else if(firstDifferenceInX==0.0 && secondDifferenceInY<0.0){
				//when A&B are in a straight vertial line, C is to the lower (left or right) of B
				double secondSlope = Math.abs(secondDifferenceInX)/Math.abs(secondDifferenceInY);
				//returns the arctangent of a number as a value between -PI/2 and PI/2 radians
				double secondArctangent = Math.atan(secondSlope);
				secondDegree = Math.abs(Math.toDegrees(secondArctangent));
				//System.out.println("second difference in Y: " + Math.abs(nextCoordinate[1]-currentCoordinate[1]) + "; second different in X: " + Math.abs(nextCoordinate[0]-currentCoordinate[0]));
				//System.out.println("i=" + i +"; second slope: " + secondSlope + "; second angle: " + secondArctangent + "; second degree: " + secondDegree);

				//finally, the relative degree between A, B and C
				relativeDegree =  180.0 - secondDegree;
				//System.out.println("i=" + i + "; relative degree: " + relativeDegree);

				if (Double.isNaN(relativeDegree)){
					System.out.println("i=" + i + "; relative degree: " + relativeDegree);
					System.out.println("first degree: " + firstDegree + "; second degree: " + secondDegree);
					System.out.println("first differnce in X: " + firstDifferenceInX + "; second difference in X: " + secondDifferenceInX);
				}

			}else if(firstDifferenceInX==0.0 && secondDifferenceInY>0.0){
				//when A&B are in a straight vertical line, C is to the upper (left or right) of B
				double secondSlope = Math.abs(secondDifferenceInX)/Math.abs(secondDifferenceInY);
				double secondArctangent = Math.atan(secondSlope);
				relativeDegree = Math.toDegrees(secondArctangent);

				if (Double.isNaN(relativeDegree)){
					System.out.println("i=" + i + "; relative degree: " + relativeDegree);
					System.out.println("first degree: " + firstDegree + "; second degree: " + secondDegree);
					System.out.println("first differnce in X: " + firstDifferenceInX + "; second difference in X: " + secondDifferenceInX);
				}

			}else if(secondDifferenceInX==0.0 && firstDifferenceInY<0.0){
				//when B&C are in a stright vertical line, A is to the upper (left or right) of B
				double firstSlope = Math.abs(firstDifferenceInX)/Math.abs(firstDifferenceInY);
				//returns the arctangent of a number as a value between -PI/2 and PI/2 radians
				double firstArctangent = Math.atan(firstSlope);
				firstDegree = Math.abs(Math.toDegrees(firstArctangent));
				//finally, the relative degree between A, B and C
				relativeDegree = 180.0 - firstDegree;

				if (Double.isNaN(relativeDegree)){
					System.out.println("i=" + i + "; relative degree: " + relativeDegree);
					System.out.println("first degree: " + firstDegree + "; second degree: " + secondDegree);
					System.out.println("first differnce in X: " + firstDifferenceInX + "; second difference in X: " + secondDifferenceInX);
				}

			}else if(secondDifferenceInX==0.0 && firstDifferenceInY>0.0){
				//when B&C are in a straight vertical line, A is to the lower (left or right) of B
				double firstSlope = Math.abs(firstDifferenceInX)/Math.abs(firstDifferenceInY);
				double firstArctangent = Math.atan(firstSlope);
				relativeDegree = Math.toDegrees(firstArctangent);

				if (Double.isNaN(relativeDegree)){
					System.out.println("i=" + i + "; relative degree: " + relativeDegree);
					System.out.println("first degree: " + firstDegree + "; second degree: " + secondDegree);
					System.out.println("first differnce in X: " + firstDifferenceInX + "; second difference in X: " + secondDifferenceInX);
				}

			}else if(firstDifferenceInY==0.0 && secondDifferenceInX<0.0){
				//when A&B are in a straight horizontal line, C is to the lower left of B (note if C is to the lower right of B, it is included in the last if-else statement below)
				double secondSlope = Math.abs(secondDifferenceInY)/Math.abs(secondDifferenceInX);
				double secondArctangent = Math.atan(secondSlope);
				relativeDegree = Math.toDegrees(secondArctangent);

				if (Double.isNaN(relativeDegree)){
					System.out.println("i=" + i + "; relative degree: " + relativeDegree);
					System.out.println("first degree: " + firstDegree + "; second degree: " + secondDegree);
					System.out.println("first differnce in X: " + firstDifferenceInX + "; second difference in X: " + secondDifferenceInX);
				}

			}else if(secondDifferenceInY==0.0 && firstDifferenceInX<0.0){
				//when B&C are in a straight horizontal line, A is to the upper right of B (note if A is to the upper left of B, it is included in the last if-else statement below)
				double firstSlop = Math.abs(firstDifferenceInY)/Math.abs(firstDifferenceInX);
				double firstArctangent = Math.atan(firstSlop);
				relativeDegree = Math.toDegrees(firstArctangent);

				if (Double.isNaN(relativeDegree)){
					System.out.println("i=" + i + "; relative degree: " + relativeDegree);
					System.out.println("first degree: " + firstDegree + "; second degree: " + secondDegree);
					System.out.println("first differnce in X: " + firstDifferenceInX + "; second difference in X: " + secondDifferenceInX);
				}

			}else{
				//all other regular cases where A, B and C are spread from one another; and
				//when A&B are in a straight horizontal line, C is to the lower right of B; and
				//when B&C are in a straight horizontal line, A is to the upper left of B.
				double firstSlope = Math.abs(firstDifferenceInY)/Math.abs(firstDifferenceInX);
				//returns the arctangent of a number as a value between -PI/2 and PI/2 radians
				double firstArctangent = Math.atan(firstSlope);
				firstDegree = Math.abs(Math.toDegrees(firstArctangent));

				double secondSlope = Math.abs(secondDifferenceInY)/Math.abs(secondDifferenceInX);
				//returns the arctangent of a number as a value between -PI/2 and PI/2 radians
				double secondArctangent = Math.atan(secondSlope);
				secondDegree = Math.abs(Math.toDegrees(secondArctangent));

				//finally, the relative degree between A, B and C
				relativeDegree = 180.0 - firstDegree - secondDegree;

				if (Double.isNaN(relativeDegree)){
					System.out.println("i=" + i + "; relative degree: " + relativeDegree);
					System.out.println("first degree: " + firstDegree + "; second degree: " + secondDegree);
					System.out.println("first arctangent: " + firstArctangent + "; second arctangent: " + secondArctangent);
					System.out.println("first slope: " + firstSlope + "; second slope: " + secondSlope);
					System.out.println("first differnce in X: " + firstDifferenceInX + "; second difference in X: " + secondDifferenceInX);
				}
			}
			allRelativeDegrees.add(relativeDegree);
		}
		return allRelativeDegrees;
	}

}