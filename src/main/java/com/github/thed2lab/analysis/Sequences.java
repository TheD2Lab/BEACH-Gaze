package com.github.thed2lab.analysis;

import static com.github.thed2lab.analysis.Constants.AOI_LABEL;

import java.util.HashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The role of the Sequences class is to convert a participant’s fixation data into a sequence.
 * A sequence is the order in which a user’s fixation moved between AOIs. An AOI may have a
 * multi-character name (e.g., TI_HSI), which will be shortened to a single letter when
 * generating sequences. For example, the sequence ABCAC indicates the user moved from
 * AOI A to B to C, back to A, and finally to C again.
 * <br></br>
 * A participant may have multiple consecutive fixations within a single AOI, resulting in a
 * sequence like ABBBAAAC. Because of this, we also generate a collapsed sequence, which removes
 * adjacent pairs of the same letter. The previous sequence would be collapsed to ABAC.
 */
public class Sequences {

    /**
     * Generates the sequence from a single participant’s fixation data, then outputs 3 text files:
     * the full sequence (“Expanded Sequence”), collapsed sequence, and a map to indicate which
     * letter refers to which full AOI name.
     * @param fixations a user’s gaze data, filtered by fixations, and validity. Screen size will not affect the output.
     * @param outputDirectory the location to output the sequence files to.
     * @return the full, expanded sequence.
     */
    public static String generateSequenceFiles(DataEntry fixations, String outputDirectory) {

        var seqDetails = getExpandedSequenceDetails(fixations);
        // separated out for readability 
        String sequence = seqDetails.getLeft();
        String aoiDescriptions = seqDetails.getRight();

        String collapsedSequence = getCollapsedSequence(sequence);

        FileHandler.writeToText(sequence, outputDirectory, "expandedSequence");
        FileHandler.writeToText(collapsedSequence, outputDirectory, "collapsedSequence");
        FileHandler.writeToText(aoiDescriptions, outputDirectory, "aoiDescriptions");

        return  sequence;
    }

    /**
     * Extracts the expanded sequence from the fixation data and creates a text string of the mappings
     * between AOIs and ascii character.
     * @param fixations a user’s gaze data, filtered by fixations, and validity. Screen size will not affect the output.
     * @return (left) the full, expanded sequence and (right) a string with the ascii character and the AOI name in a comma separated list.
     */
    static Pair<String, String> getExpandedSequenceDetails(DataEntry fixations) {
        final int ASCII_OFFSET = 65; // Capital "A"
        String aoiDescriptions = "";
        String sequence = "";
        HashMap<String, Integer> aoiLetters = new HashMap<>();

        // Generate sequence
        for (int i = 0; i < fixations.rowCount(); i++) {
            String aoi = fixations.getValue(AOI_LABEL, i);

            if (!aoiLetters.containsKey(aoi)) {
                aoiLetters.put(aoi, aoiLetters.size() + ASCII_OFFSET);

                String description = aoi == "" ? "Undefined Area" : aoi;
                aoiDescriptions += (char)(aoiLetters.size() + ASCII_OFFSET - 1) + ", " +  description  + "\n";
            }
            
            int asciiValue = aoiLetters.get(aoi);
            char c = (char)asciiValue;
            sequence += c;
        }

        return new ImmutablePair<String, String>(sequence, aoiDescriptions);
    }

    /**
     * Converts an expanded sequence into a collapsed sequence.
     * @param expSeq the expanded sequence to collapse.
     * @return the collapsed sequence.
     */
    public static String getCollapsedSequence(String expSeq) {
        String collapsedSequence = "";
        char current = ' ';

        for (int i = 0; i < expSeq.length(); i++) {
            char next = expSeq.charAt(i);
            if (current != next) collapsedSequence += next;
            current = next;
        }

        return collapsedSequence;
    };
}
