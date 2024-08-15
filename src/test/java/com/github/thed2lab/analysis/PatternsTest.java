package com.github.thed2lab.analysis;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class PatternsTest {

   private final double PRECISION = 0.000000001; // allowable floating point error

   @Test
   public void testDiscoverPatterns_noSequences_returnHeadersOnly() {
      final int MIN_LEN = 2;
      final int MAX_LEN = 3;
      final int MIN_FEQ = 1;
      final int MIN_SIZE = 1;
      List<String> sequence = List.of();
      var results = Patterns.discoverPatterns(sequence, MIN_LEN, MAX_LEN, MIN_FEQ, MIN_SIZE);
      assertTrue(results.size() == 1); // headers only
   }

   @Test
   public void testDiscoverPatterns_noSequences() {
      final int MIN_LEN = 2;
      final int MAX_LEN = 3;
      final int MIN_FEQ = 1;
      final int MIN_SIZE = 1;
      List<String> sequence = List.of();
      var results = Patterns.discoverPatterns(sequence, MIN_LEN, MAX_LEN, MIN_FEQ, MIN_SIZE);
      assertTrue(results.size() == 1); // headers only
   }

   @Test
   public void testDiscoverPatterns_negative() {
      final int MIN_LEN = 2;
      final int MAX_LEN = 3;
      final int MIN_FEQ = 1;
      final int MIN_SIZE = 1;
      List<String> sequence = List.of();
      var results = Patterns.discoverPatterns(sequence, MIN_LEN, MAX_LEN, MIN_FEQ, MIN_SIZE);
      assertTrue(results.size() == 1); // headers only
   }

   @Test
   public void testDiscoverPatterns_singlePattern() {
      final int MIN_LEN = 2;
      final int MAX_LEN = 3;
      final int MIN_FEQ = 1;
      final int MIN_SIZE = 1;
      List<String> sequence = List.of("ABBABBC");
      var results = Patterns.discoverPatterns(sequence, MIN_LEN, MAX_LEN, MIN_FEQ, MIN_SIZE);
      assertTrue(results.size() == 9); // Header + 8 patterns

      // many asserts because bad method design
      for (var singleSeq : results.subList(1, results.size())) {
         String pattern = singleSeq.get(0);
         boolean isCorrect = true;
         if (pattern.equals("AB") || pattern.equals("BB")) {
            isCorrect = compareSeqData(singleSeq, 2, 1, 2.0, 2/6.0);
         } else if (pattern.equals("BA") || pattern.equals("BC")) {
            isCorrect = compareSeqData(singleSeq, 1, 1, 1.0, 1/6.0);
         } else if (pattern.equals("ABB")) {
            isCorrect = compareSeqData(singleSeq, 2, 1, 2.0, 2/5.0);
         } else if (pattern.equals("BBA") || pattern.equals("BAB") || pattern.equals("BBC")) {
            isCorrect = compareSeqData(singleSeq, 1, 1, 1.0, 1/5.0);
         } else {
            isCorrect = false; // fail all other cases
         }
         if(!isCorrect) {
            fail();
         }
      }
   }

   @Test
   public void testDiscoverPatterns_twoPatternsMinFreq1MinSize1_includeAllResults() {
      final int MIN_LEN = 2;
      final int MAX_LEN = 3;
      final int MIN_FEQ = 1;
      final int MIN_SIZE = 1;
      List<String> sequence = List.of("ABBABBC", "AB");
      var results = Patterns.discoverPatterns(sequence, MIN_LEN, MAX_LEN, MIN_FEQ, MIN_SIZE);

      // many asserts because bad method design
      for (var singleSeq : results.subList(1, results.size())) {
         String pattern = singleSeq.get(0);
         boolean isCorrect = true;
         if (pattern.equals("BB")) {
            isCorrect = compareSeqData(singleSeq, 2, .5, 1.0, 2/7.0);
         } else if (pattern.equals("BA") || pattern.equals("BC")) {
            isCorrect = compareSeqData(singleSeq, 1, 0.5, 0.5, 1/7.0);
         } else if (pattern.equals("AB")) {
            isCorrect = compareSeqData(singleSeq, 3, 1.0, 3/2.0, 3/7.0);
         } else if (pattern.equals("ABB")) {
            isCorrect = compareSeqData(singleSeq, 2, 0.5, 1.0, 2/5.0);
         } else if (pattern.equals("BBA") || pattern.equals("BAB") || pattern.equals("BBC")) {
            isCorrect = compareSeqData(singleSeq, 1, 0.5, 0.5, 1/5.0);
         } else {
            isCorrect = false; // fail all other cases
         }
         if(!isCorrect) {
            fail();
         }
      }
   }

   @Test
   public void testDiscoverPatterns_twoPatternsMinSeqSize2_excludePatternsNotInTwoSequences() {
      final int MIN_LEN = 2;
      final int MAX_LEN = 3;
      final int MIN_FEQ = 1;
      final int MIN_SIZE = 2;
      List<String> sequence = List.of("ABBABBC", "AB");
      var results = Patterns.discoverPatterns(sequence, MIN_LEN, MAX_LEN, MIN_FEQ, MIN_SIZE);
      assertTrue(results.size() == 2); // Header + 1 pattern

      // many asserts because bad method design
      for (var singleSeq : results.subList(1, results.size())) {
         String pattern = singleSeq.get(0);
         boolean isCorrect = true;
         if (pattern.equals("AB")) {
            isCorrect = compareSeqData(singleSeq, 3, 1.0, 3/2.0, 3/7.0);
         } else {
            isCorrect = false; // fail all other cases
         }
         if(!isCorrect) {
            fail();
         }
      }
   }

   @Test
   public void testDiscoverPatterns_twoPatternsMinFreq2_excludePatternsWithFreqLessThan2() {
      final int MIN_LEN = 2;
      final int MAX_LEN = 3;
      final int MIN_FEQ = 2;
      final int MIN_SIZE = 1;
      List<String> sequence = List.of("ABBABBC", "AB");
      var results = Patterns.discoverPatterns(sequence, MIN_LEN, MAX_LEN, MIN_FEQ, MIN_SIZE);
      assertTrue(results.size() == 4); // header + 3 patterns

      // many asserts because bad method design
      for (var singleSeq : results.subList(1, results.size())) {
         String pattern = singleSeq.get(0);
         boolean isCorrect = true;
         if (pattern.equals("BB")) {
            isCorrect = compareSeqData(singleSeq, 2, .5, 1.0, 2/7.0);
         } else if (pattern.equals("AB")) {
            isCorrect = compareSeqData(singleSeq, 3, 1.0, 3/2.0, 3/7.0);
         } else if (pattern.equals("ABB")) {
            isCorrect = compareSeqData(singleSeq, 2, 0.5, 1.0, 2/5.0);
         } else {
            isCorrect = false; // fail all other cases
         }
         if(!isCorrect) {
            fail();
         }
      }
   }

   /** Returns true if doubles are equal */
   private boolean compareDouble(double d1, double d2) {
      return Math.abs(d1 - d2) < PRECISION;
   }

   /**
    * Returns true if data matches.
    * @param actualData single line of pattern results
    * @param expFreq expected frequency
    * @param expSupport expected sequence support
    * @param expAvg expected average pattern frequency
    * @param expProp expected proportional pattern frequency
    * @return whether the actual data matches the expected results
    */
   private boolean compareSeqData(List<String> actualData, int expFreq, double expSupport, double expAvg, double expProp) {
      boolean freqMatch = expFreq == Integer.valueOf(actualData.get(1));
      boolean supportMatch = compareDouble(expSupport, Double.valueOf(actualData.get(2)));
      boolean avgMatch = compareDouble(expAvg, Double.valueOf(actualData.get(3)));
      boolean propMatch = compareDouble(expProp, Double.valueOf(actualData.get(4)));
      return freqMatch && supportMatch && avgMatch && propMatch;
   }
      
}
