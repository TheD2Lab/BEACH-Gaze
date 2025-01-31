/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.thed2lab.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class DescriptiveStats {

	public static double getSumOfIntegers(ArrayList<Integer> allIntegers){
		if (allIntegers.size() == 0) return 0;

		double total = 0.0;
		for (Integer i:allIntegers){
			total = total + i;
		}
		return total;
	}

	public static double getSumOfDoubles(ArrayList<Double> allDoubles){
		if (allDoubles.size() == 0) return 0;

		double total = 0.0;
		for (Double value : allDoubles) {
			total += value;
		}

		return total;
	}

	public static double getSum(Double[] allDoubles){
		if (allDoubles.length == 0) return 0;

		double total = 0.0;
		for (Double i:allDoubles){
			total = total + i;
		}
		return total;

	}


	public static double getMeanOfIntegers(ArrayList<Integer> allIntegers){
		return (allIntegers.size() == 0) ? Double.NaN : getSumOfIntegers(allIntegers)/allIntegers.size();
	}

	public static double getMeanOfDoubles(ArrayList<Double> allDoubles){
		return (allDoubles.size() == 0) ? Double.NaN : getSumOfDoubles(allDoubles)/allDoubles.size();
	}

	public static double getMean(Double[] allDoubles){
		if (allDoubles.length == 0) return Double.NaN;

		double average = getSum(allDoubles)/allDoubles.length;
		return average;
	}

	public static double getMedianOfIntegers(ArrayList<Integer> allIntegers) {
		if (allIntegers.size() == 0) return Double.NaN;

		Collections.sort(allIntegers);
		int middle = allIntegers.size()/2;
		if(allIntegers.size()%2 == 1){
			return allIntegers.get(middle);
		}else{
			return (allIntegers.get(middle-1) + allIntegers.get(middle))/2.0;
		}
	}

	public static double getMedianOfDoubles(ArrayList<Double> allDoubles) {
		if (allDoubles.size() == 0) return Double.NaN;

		Collections.sort(allDoubles);
		int middle = allDoubles.size()/2;
		if(allDoubles.size()%2 == 1){
			return allDoubles.get(middle);
		}else{
			return (allDoubles.get(middle-1) + allDoubles.get(middle))/2.0;
		}
	}

	public static double getMedian(Double[] allDoubles){
		if (allDoubles.length == 0) return Double.NaN;

		Arrays.sort(allDoubles);
		if (allDoubles.length%2 == 1){
			return allDoubles[allDoubles.length/2];
		}else{
			return (allDoubles[allDoubles.length/2-1] + allDoubles[allDoubles.length/2])/2;
		}

	}

	public static double getStDevOfIntegers(ArrayList<Integer> allIntegers){
		if (allIntegers.size() == 0) return Double.NaN;

		double sum = 0;
		double mean = getMeanOfIntegers(allIntegers);

		for (double i : allIntegers){
			sum += Math.pow((i - mean), 2);
		}

		return Math.sqrt( sum / (allIntegers.size()-1) );
	}

	public static double getStDevOfDoubles(ArrayList<Double> allDoubles){
		if (allDoubles.size() == 0) return Double.NaN;

		double sum = 0;
		double mean = getMeanOfDoubles(allDoubles);
		for(double i:allDoubles){
			sum += Math.pow((i-mean), 2);
		}
		return Math.sqrt(sum/(allDoubles.size()-1));
	}

	public static double getStDev(Double[] allDoubles){
		if (allDoubles.length == 0) return Double.NaN;

		double sum = 0;
		double mean = getMean(allDoubles);

		for (double i : allDoubles){
			sum += Math.pow((i - mean), 2);
		}

		return Math.sqrt( sum / (allDoubles.length-1) );
	}

	public static double getMinOfIntegers(ArrayList<Integer> allIntegers){
		return (allIntegers.size() == 0) ? Double.NaN : Collections.min(allIntegers);
	}

	public static double getMinOfDoubles(ArrayList<Double> allDoubles){
		return (allDoubles.size() == 0) ? Double.NaN : Collections.min(allDoubles);
	}

	public static double getMin(Double[] allDoubles){
		return (allDoubles.length == 0) ? Double.NaN :Collections.min(Arrays.asList(allDoubles));
	}

	public static double getMaxOfIntegers(ArrayList<Integer> allIntegers){
		return (allIntegers.size() == 0) ? Double.NaN : Collections.max(allIntegers);
	}

	public static double getMaxOfDoubles(ArrayList<Double> allDoubles){
		return (allDoubles.size() == 0) ? Double.NaN : Collections.max(allDoubles);
	}

	public static double getMax(Double[] allDoubles){
		return (allDoubles.length == 0) ? Double.NaN : Collections.max(Arrays.asList(allDoubles));
	}

}
