package analysis;

import java.util.HashMap;

public class Sequences {
    
    final static String AOI_INDEX = "AOI";

    public static void generateSequenceFiles(DataEntry data, String outputDirectory) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        String sequence = "";
        int ascii = 65;

        for (int i = 0; i < data.rowCount(); i++) {
            String aoi = data.getValue(AOI_INDEX, i);

            if (!map.containsKey(aoi)) map.put(aoi, map.size() + ascii);
            
            int asciiValue = map.get(aoi);
            char c = (char)asciiValue;
            sequence += c;
        }

        String collapsedSequence = getCollapsedSequence(sequence);
        FileHandler.writeToText(sequence, outputDirectory, "expandedSequence");
        FileHandler.writeToText(collapsedSequence, outputDirectory, "collapsedSequence");
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
