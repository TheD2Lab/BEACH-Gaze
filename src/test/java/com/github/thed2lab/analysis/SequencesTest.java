package com.github.thed2lab.analysis;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class SequencesTest {

   @Test
   public void testGetExpandedSequenceDetails_emptyData_returnEmptyStringPair() {
      DataEntry fixations = new DataEntry(new String[]{"AOI"});
      Pair<String, String> actualDetails = Sequences.getExpandedSequenceDetails(fixations);
      boolean isEmpty = actualDetails.getRight().length() == 0 && actualDetails.getLeft().length() == 0;
      assertTrue(isEmpty);
   }

   @Test
   public void testGetExpandedSequenceDetails_ABBBAAAC() {
      final String EXPECTED_EXPANDED = "ABBBAAAC";
      final String EXPECTED_ASCII = "A, AOI\nB, BOI\nC, COI\n";
      
      DataEntry fixations = new DataEntry(new String[]{"AOI"});
      fixations.process(List.of("AOI"));
      fixations.process(List.of("BOI"));
      fixations.process(List.of("BOI"));
      fixations.process(List.of("BOI"));
      fixations.process(List.of("AOI"));
      fixations.process(List.of("AOI"));
      fixations.process(List.of("AOI"));
      fixations.process(List.of("COI"));

      var results = Sequences.getExpandedSequenceDetails(fixations);
      assertTrue(results.getLeft().equals(EXPECTED_EXPANDED));
      assertTrue(results.getRight().equals(EXPECTED_ASCII));
   }

   @Test
   public void testGetExpandedSequenceDetails_containsUndefinedArea() {
      final String EXPECTED_EXPANDED = "BAB";
      final String EXPECTED_ASCII = "A, Undefined Area\nB, AOI\n";
      
      DataEntry fixations = new DataEntry(new String[]{"AOI"});
      fixations.process(List.of("AOI"));
      fixations.process(List.of(""));
      fixations.process(List.of("AOI"));

      var results = Sequences.getExpandedSequenceDetails(fixations);
      assertTrue(results.getLeft().equals(EXPECTED_EXPANDED));
      assertTrue(results.getRight().equals(EXPECTED_ASCII));
   }

   @Test
   public void testGetCollapsedSequence_emptyString_returnEmptyString() {
      String result = Sequences.getCollapsedSequence("");
      assertTrue(result.equals(""));
   }

   @Test
   public void testGetCollapsedSequence_ABBBAAAC() {
      final String EXPECTED_COLLAPSED = "ABAC";
      String result = Sequences.getCollapsedSequence("ABBBAAAC");
      assertTrue(result.equals(EXPECTED_COLLAPSED));
   }
}
