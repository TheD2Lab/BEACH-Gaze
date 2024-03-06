package analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.opencsv.CSVWriter;

public class Patterns {
    private static void discoverPatterns(ArrayList<String> sequences, int minPatternLength, int maxPatternLength, int minFrequency, int minSequenceSize, String outputFile, ArrayList<String> descriptions, ArrayList<String> codes) throws IOException {
        
        String[] aoiDescriptions = descriptions.toArray(new String[descriptions.size()]);
        String[] aoiCodes = codes.toArray(new String[codes.size()]);
        outputCSVWriter.writeNext(aoiDescriptions);
        outputCSVWriter.writeNext(aoiCodes);
        outputCSVWriter.writeNext(new String[0]);
        
        String[] headers = new String[] {"Pattern String", "Frequency", "Sequence Support", "Average Pattern Frequency", "Proportional Pattern Frequency"};
        outputCSVWriter.writeNext(headers);
            
        int totalPatternCount = 0;
        for (int patternLength = minPatternLength; patternLength <= maxPatternLength; patternLength++) {
            HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();
            HashMap<String, ArrayList<String>> sequenceMap = new HashMap<String, ArrayList<String>>();
            
            for (String s: sequences) {
                for (int i = 0; i < s.length() - patternLength; i++) {
                    String patternString = s.substring(i, i + patternLength);
                    totalPatternCount++;
    
                    int count = frequencyMap.containsKey(patternString) ? frequencyMap.get(patternString) + 1 : 1;
                    frequencyMap.put(patternString, count);
    
                    if (!sequenceMap.containsKey(patternString)) sequenceMap.put(patternString, new ArrayList<String>());
                    if (!sequenceMap.get(patternString).contains(s)) sequenceMap.get(patternString).add(s);
                }
            }
            
            for (String s: frequencyMap.keySet()) {
                int frequency = frequencyMap.get(s);
                double sequenceSupport = (double) sequenceMap.get(s).size()/sequences.size();
                double averagePatternFrequency = (double) frequencyMap.get(s)/sequences.size();
                double proportionalPatternFrequency = (double) frequencyMap.get(s)/totalPatternCount;
                
                if (frequency >= minFrequency && sequenceMap.get(s).size() >= minSequenceSize) {
                    ArrayList<String> data = new ArrayList<String>();
                    data.add(s);
                    data.add(String.valueOf(frequency));
                    data.add(String.valueOf(sequenceSupport));
                    data.add(String.valueOf(averagePatternFrequency));
                    data.add(String.valueOf(proportionalPatternFrequency));
                    outputCSVWriter.writeNext(data.toArray(new String[data.size()]));
                }
            }
        }
        
}
}
