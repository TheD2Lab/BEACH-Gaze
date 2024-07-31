package com.github.thed2lab.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GazeEntropy {
    private final static String AOI_INDEX = "AOI";

    /**
     * Iterates over all rows of a participant’s fixations and calculates the probability of transition to each AOI, as well as calculating
     * the probability that, given a fixation is in some AOI<sub>A</sub>, the probability of transitioning to some AOI<sub>B</sub>.
     * @param fixations the users gaze data, filtered by fixations and validity with screen size applied
     * @return a map with the key as the outputted variable’s name and the value as a string containing the result
     */
    static public LinkedHashMap<String,String> analyze(DataEntry fixations) {
        var aoiProbability = new HashMap<String, Double>();
        var transitionProbability = new HashMap<String, Map<String, Double>>();
        var aoiSequence = new ArrayList<String>();
        String lastAoi = "";
        
        int fixationCount = fixations.rowCount();
        
        for (int row = 0; row < fixations.rowCount(); row++) {
            String aoi = fixations.getValue(AOI_INDEX, row);
            aoiSequence.add(aoi);
            
            if (aoi.equals(""))
                continue;
            aoiProbability.put(aoi, aoiProbability.getOrDefault(aoi, 0.0) + 1);
            if (!lastAoi.equals("")) {
                Map<String, Double> relationMatrix = transitionProbability.getOrDefault(lastAoi, new HashMap<String,Double>());
                double count = relationMatrix.getOrDefault(aoi, 0.0);
                relationMatrix.put(aoi, count + 1);
                transitionProbability.put(lastAoi, relationMatrix);
            }  
            lastAoi = aoi;
        }

        for (Map.Entry<String, Double> entry : aoiProbability.entrySet()) {
            Double AOIFixationCount = entry.getValue();
            Double probability = AOIFixationCount/fixationCount;
            entry.setValue(probability);
        }
        
        
        for (Map.Entry<String, Map<String, Double>> entry : transitionProbability.entrySet()) {
            int aoiTransitions = 0;
            for (Map.Entry<String, Double> edge : entry.getValue().entrySet()) {
                aoiTransitions += edge.getValue();
            }
            for (Map.Entry<String, Double> edge : entry.getValue().entrySet()) {
                edge.setValue(edge.getValue()/aoiTransitions);
            }
        }

        var results = new LinkedHashMap<String,String>();

        results.put(
            "stationary_entropy", //Output Header
            String.valueOf(getStationaryEntropy(aoiProbability)) //Output Value
            );

        results.put(
            "transition_entropy", //Output Header
            String.valueOf(getTransitionEntropy(aoiProbability, transitionProbability)) //Output Value
            );
    
        return results;
    }

    /**
     * Calculates the stationary entropy score
     * @param aoiProbability a map between an AOI name and probability that any given transition is to that AOI
     * @return the stationary entropy score
     */
    static double getStationaryEntropy(Map<String, Double> aoiProbability) {
		double stationaryEntropy = 0;
		for (Map.Entry<String, Double> entry : aoiProbability.entrySet()) {
			double probability = entry.getValue();
			stationaryEntropy += -probability * Math.log10(probability);
		};
		
		return stationaryEntropy;
	}
	
    /**
     * Calculates the transition entropy score.
     * @param aoiProbability the AOI name mapped to the probability that a transition is to that AOI
     * @param transitionMatrix the outer matrix has a key for each AOI (A). The value for each AOI is another Hashmap containing all AOIs (inclusive) (B).
     * The value for each of those is the probability of transitioning from A to B.
     * @return the stationary entropy score
     */
	static double getTransitionEntropy(Map<String, Double> aoiProbability, Map<String, Map<String,Double>> transitionMatrix){	
    	double transitionEntropy = 0;
		for (Map.Entry<String,Map<String,Double>> entry : transitionMatrix.entrySet()) {
    		double pijSum = 0;
    		for (Map.Entry<String, Double> edge : entry.getValue().entrySet()) {
    			pijSum += edge.getValue() * Math.log10(edge.getValue());
    		}
    		transitionEntropy += pijSum * -aoiProbability.get(entry.getKey());
    	}
		
		return transitionEntropy;
	}
}
