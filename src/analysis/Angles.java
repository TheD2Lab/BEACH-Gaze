package analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Angles {
    final static String FIXATIONID_INDEX = "FPOGID";
    final static String FIXATIONX_INDEX = "FPOGX";
    final static String FIXATIONY_INDEX = "FPOGY";
    
    static public LinkedHashMap<String,String> analyze(DataEntry data) {
        LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
        ArrayList<Object> allCoordinates = new ArrayList<>();

        for (int row = 0; row < data.rowCount(); row++) {
            Double[] eachCoordinate = new Double[3];
            eachCoordinate[0] = Double.valueOf(data.getValue(FIXATIONX_INDEX, row));
            eachCoordinate[1] = Double.valueOf(data.getValue(FIXATIONY_INDEX, row));
            eachCoordinate[2] = Double.valueOf(data.getValue(FIXATIONID_INDEX, row));
            allCoordinates.add(eachCoordinate);

        }

        ArrayList<Double> allAbsoluteDegrees = getAllAbsoluteAngles(allCoordinates);
        ArrayList<Double> allRelativeDegrees = getAllRelativeAngles(allCoordinates);
        //Absolute Degrees
        results.put(
            "sum of all absolute degrees", //Output Header
            String.valueOf(DescriptiveStats.getSumOfDoubles(allAbsoluteDegrees))
            );

        results.put(
            "mean absolute degree", //Output Header
            String.valueOf(DescriptiveStats.getMeanOfDoubles(allAbsoluteDegrees))
            );

        results.put(
            "median absolute degree", //Output Header
            String.valueOf(DescriptiveStats.getMedianOfDoubles(allAbsoluteDegrees))
            );

        results.put(
            "StDev of absolute degrees", //Output Header
            String.valueOf(DescriptiveStats.getStDevOfDoubles(allAbsoluteDegrees))
            );

        results.put(
            "min absolute degree", //Output Header
            String.valueOf(DescriptiveStats.getMinOfDoubles(allAbsoluteDegrees))
            );            

        results.put(
            "max absolute degree", //Output Header
            String.valueOf(DescriptiveStats.getMaxOfDoubles(allAbsoluteDegrees)) 
            );   
        //Relative Degrees
        results.put(
            "sum of all relative degrees", 
            String.valueOf(DescriptiveStats.getSumOfDoubles(allRelativeDegrees))
            );  

        results.put(
            "mean relative degree", //Output Header
            String.valueOf(DescriptiveStats.getMeanOfDoubles(allRelativeDegrees))
            );

        results.put(
            "median relative degree", //Output Header
            String.valueOf(DescriptiveStats.getMedianOfDoubles(allRelativeDegrees))
            );

        results.put(
            "StDev of relative degrees", //Output Header
            String.valueOf(DescriptiveStats.getStDevOfDoubles(allRelativeDegrees))
            );

        results.put(
            "min relative degree", //Output Header
            String.valueOf(DescriptiveStats.getMinOfDoubles(allRelativeDegrees))
            );            

        results.put(
            "max relative degree", //Output Header
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
	public static ArrayList<Double> getAllAbsoluteAngles(ArrayList<Object> allCoordinates){

		ArrayList<Double> allAbsoluteDegrees = new ArrayList<>();
		double absoluteDegree = 0.0;

		for(int i=0; i<allCoordinates.size(); i++){
			Double[] earlyCoordinate = (Double[]) allCoordinates.get(i);

			if((i+1)<allCoordinates.size()){

				Double[] laterCoordinate = (Double[]) allCoordinates.get(i+1);

				double differenceInY = laterCoordinate[1] - earlyCoordinate[1];
				double differenceInX = laterCoordinate[0] - earlyCoordinate[0];

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
	public static ArrayList<Double> getAllRelativeAngles(ArrayList<Object> allCoordinates){

		ArrayList<Double> allRelativeDegrees = new ArrayList<>();
		double relativeDegree = 0.0;
		double firstDegree = 0.0;
		double secondDegree = 0.0;

		for(int i=1; (i+1)<allCoordinates.size(); i++){
			Double[] previousCoordinate = (Double[]) allCoordinates.get(i-1);
			Double[] currentCoordinate = (Double[]) allCoordinates.get(i);
			Double[] nextCoordinate = (Double[]) allCoordinates.get(i+1);

			//System.out.println("i=" + i + "; previous X: " + previousCoordinate[0] + "; current X: " + currentCoordinate[0] + "; next X: " + nextCoordinate[0]);


			//degree between A and B
			double firstDifferenceInY = currentCoordinate[1]-previousCoordinate[1];
			double firstDifferenceInX = currentCoordinate[0]-previousCoordinate[0];

			//degree between B and C
			double secondDifferenceInY = nextCoordinate[1]-currentCoordinate[1];
			double secondDifferenceInX = nextCoordinate[0]-currentCoordinate[0];

			if((firstDifferenceInX==0.0 && secondDifferenceInX==0.0) || (firstDifferenceInY==0.0 && secondDifferenceInY==0.0)){
				//when A, B and C are all in a straight line, either horizontally or vertically
				relativeDegree = 180.0;

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