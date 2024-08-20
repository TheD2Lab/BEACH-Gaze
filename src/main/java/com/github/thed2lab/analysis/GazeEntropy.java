package com.github.thed2lab.analysis;

import static com.github.thed2lab.analysis.Constants.AOI_LABEL;
import static com.github.thed2lab.analysis.Constants.FIXATION_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GazeEntropy {

    /**
     * Calculates the stationary entropy and transition entropy measures. The method iterates over the participant’s
     * fixations, determining the probability of viewing each AOI and the probability of transitioning from one AOI
     * to another. All lines of data not labeled with an AOI are treated as if they appear in a single AOI.
     * Therefore, n in the entropy formula will be the total number of labeled AOIs plus one. .
     * @param fixations the users gaze data, filtered by fixations and validity with screen size applied.
     * @return an ordered map containing the stationary and transition entropy headers as keys mapped to their computed values.
     */
    static public LinkedHashMap<String,String> analyze(DataEntry fixations) {
        var aoiProbability = new HashMap<String, Double>();
        var transitionProbability = new HashMap<String, Map<String, Double>>();
        var aoiSequence = new ArrayList<String>();
        String lastAoi = null;
        int lastId = -1; // arbitrary number that will never be the ID
        
        int fixationCount = fixations.rowCount();
        
        for (int row = 0; row < fixations.rowCount(); row++) {
            String aoi = fixations.getValue(AOI_LABEL, row);
            int curId = Integer.valueOf(fixations.getValue(FIXATION_ID, row));
            aoiSequence.add(aoi);
            aoiProbability.put(aoi, aoiProbability.getOrDefault(aoi, 0.0) + 1);
            if (lastAoi != null && curId == lastId + 1) {  // skips the first loop and non-consecutive fixations
                Map<String, Double> relationMatrix = transitionProbability.getOrDefault(lastAoi, new HashMap<String,Double>());
                double count = relationMatrix.getOrDefault(aoi, 0.0);
                relationMatrix.put(aoi, count + 1);
                transitionProbability.put(lastAoi, relationMatrix);
            }
            lastAoi = aoi;
            lastId = curId;
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
     * Calculates the stationary entropy score.
     * @param aoiProbability the AOI labels mapped to the probability of viewing the AOI.
     * @return the stationary entropy score.
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
     * @param aoiProbability the AOI labels mapped to the probability of viewing the AOI.
     * @param transitionMatrix the outer map has a key for each AOI (A). The value for each AOI is another map
     * containing all AOIs (inclusive) (B). The value for each of those is the probability of transitioning
     * from A to B.
     * @return the stationary entropy score.
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
