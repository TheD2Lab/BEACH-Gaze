package com.github.thed2lab.analysis;

import java.util.HashMap;
import java.util.List;

public class Sequences {
    
    final static String AOI_INDEX = "AOI";

    public static void generateSequenceFiles(DataEntry data, String outputDirectory, List<String> sequences, HashMap<String, Integer> map) {
        String aoiDescriptions = "";
        String sequence = "";
        int ascii = 65;

        // Build aoiDescriptions string 
        for (String s: map.keySet()) {
            int asciiValue = map.get(s);
            String description = s == "" ? "Undefined Area" : s;
            aoiDescriptions += (char)asciiValue + ", " + description + "\n";
        }

        // Generate sequence
        for (int i = 0; i < data.rowCount(); i++) {
            String aoi = data.getValue(AOI_INDEX, i);

            if (!map.containsKey(aoi)) {
                map.put(aoi, map.size() + ascii);

                String description = aoi == "" ? "Undefined Area" : aoi;
                aoiDescriptions += (char)(map.size() + ascii - 1) + ", " +  description  + "\n";
            }
            
            int asciiValue = map.get(aoi);
            char c = (char)asciiValue;
            sequence += c;
        }

        sequences.add(sequence);
        String collapsedSequence = getCollapsedSequence(sequence);

        FileHandler.writeToText(sequence, outputDirectory, "expandedSequence");
        FileHandler.writeToText(collapsedSequence, outputDirectory, "collapsedSequence");
        FileHandler.writeToText(aoiDescriptions, outputDirectory, "aoiDescriptions");
    }

    public static String getCollapsedSequence(String s) {
        String collapsedSequence = "";
        char current = ' ';

        for (int i = 0; i < s.length(); i++) {
            char next = s.charAt(i);
            if (current != next) collapsedSequence += next;
            current = next;
        }

        return collapsedSequence;
    };
}
