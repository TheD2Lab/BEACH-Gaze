/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.lab.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Patterns {
    public static List<List<String>> discoverPatterns(List<String> sequences, int minPatternLength, int maxPatternLength, int minFrequency, int minSequenceSize) {

        // ensure minimums are at least 1
        minFrequency = minFrequency > 0 ? minFrequency : 1;
        minPatternLength = minPatternLength > 0 ? minPatternLength : 1;
        minSequenceSize = minSequenceSize > 0 ? minSequenceSize : 1;

        ArrayList<List<String>> results = new ArrayList<List<String>>();
        results.add(Arrays.asList(new String[] {"pattern_string", "frequency", "sequence_support", "average_pattern_frequency", "proportional_pattern_frequency"})); 

        for (int patternLength = minPatternLength; patternLength <= maxPatternLength; patternLength++) {
            int totalPatternCount = 0;
            HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();
            HashMap<String, ArrayList<String>> sequenceMap = new HashMap<String, ArrayList<String>>();
            
            for (String seq: sequences) {
                for (int i = 0; i <= seq.length() - patternLength; i++) {
                    String patternString = seq.substring(i, i + patternLength);
                    totalPatternCount++;
    
                    int count = frequencyMap.getOrDefault(patternString, 0) + 1;
                    frequencyMap.put(patternString, count);
    
                    if (!sequenceMap.containsKey(patternString)) {
                        sequenceMap.put(patternString, new ArrayList<String>());
                    }
                    if (!sequenceMap.get(patternString).contains(seq)) {
                        sequenceMap.get(patternString).add(seq);
                    }
                }
            }
            
            for (String pattern: frequencyMap.keySet()) {
                int frequency = frequencyMap.get(pattern);
                double sequenceSupport = (double) sequenceMap.get(pattern).size()/sequences.size();
                double averagePatternFrequency = (double) frequencyMap.get(pattern)/sequences.size();
                double proportionalPatternFrequency = (double) frequencyMap.get(pattern)/totalPatternCount;
                
                if (frequency >= minFrequency && sequenceMap.get(pattern).size() >= minSequenceSize) {
                    List<String> patternData = new ArrayList<String>();
                    patternData.add(pattern);
                    patternData.add(String.valueOf(frequency));
                    patternData.add(String.valueOf(sequenceSupport));
                    patternData.add(String.valueOf(averagePatternFrequency));
                    patternData.add(String.valueOf(proportionalPatternFrequency));
                    results.add(patternData);
                }
            }
        }
        return results;
    }
}
