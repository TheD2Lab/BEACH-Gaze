package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class GazeEntropyTest {

   private final double PRECISION = 0.000000001; // allowable floating point error
   private final String STATIONARY_ENTROPY = "stationary_entropy";
   private final String TRANSITION_ENTROPY = "transition_entropy";

   @Test
   public void testGazeEntropyAnalyze_singleAoi_0entropy() {
      final double EXPECTED_STATIONARY = 0.0;
      final double EXPECTED_TRANSITION = 0.0;
      DataEntry data = new DataEntry(Arrays.asList("FPOGID","AOI")) {{
         process(Arrays.asList("0", "A"));
         process(Arrays.asList("1", "A"));
         process(Arrays.asList("1", "A"));
         process(Arrays.asList("1", "A"));
      }};
      var results = GazeEntropy.analyze(data);
      assertEquals(
         "Unexpected stationary entropy.",
         EXPECTED_STATIONARY,
         Double.parseDouble(results.get(STATIONARY_ENTROPY)),
         PRECISION
      );
      assertEquals(
         "Unexpected transition entropy.",
         EXPECTED_TRANSITION, Double.parseDouble(results.get(TRANSITION_ENTROPY)),
         PRECISION
      );
   }

   @Test
   public void testGazeEntropyAnalyze_threeAoi() {
      final double EXPECTED_STATIONARY = 0.4699915470362;
      final double EXPECTED_TRANSITION = 0.282583442123752;
      DataEntry data = new DataEntry(Arrays.asList("FPOGID", "AOI")) {{
         process(Arrays.asList("1", "A"));
         process(Arrays.asList("2", "B"));
         process(Arrays.asList("3", "B"));
         process(Arrays.asList("4", "A"));
         process(Arrays.asList("5", "A"));
         process(Arrays.asList("6", "B"));
         process(Arrays.asList("7", "C"));
         process(Arrays.asList("8", "C"));
      }};
      var results = GazeEntropy.analyze(data);
      assertEquals(
         "Unexpected stationary entropy.",
         EXPECTED_STATIONARY,
         Double.parseDouble(results.get(STATIONARY_ENTROPY)),
         PRECISION
      );
      assertEquals(
         "Unexpected transition entropy.",
         EXPECTED_TRANSITION, Double.parseDouble(results.get(TRANSITION_ENTROPY)),
         PRECISION
      );
   }

   @Test
   public void testGazeEntropyAnalyze_undefinedAoi() {
      final double EXPECTED_STATIONARY = 0.301029995663981;
      final double EXPECTED_TRANSITION = 0.288732293303828;
      DataEntry data = new DataEntry(Arrays.asList("FPOGID", "AOI")) {{
         process(Arrays.asList("0", "A"));
         process(Arrays.asList("1", ""));
         process(Arrays.asList("2", ""));
         process(Arrays.asList("3", "A"));
         process(Arrays.asList("4", "A"));
         process(Arrays.asList("5", ""));
      }};
      var results = GazeEntropy.analyze(data);
      assertEquals(
         "Unexpected stationary entropy.",
         EXPECTED_STATIONARY,
         Double.parseDouble(results.get(STATIONARY_ENTROPY)),
         PRECISION
      );
      assertEquals(
         "Unexpected transition entropy.",
         EXPECTED_TRANSITION, Double.parseDouble(results.get(TRANSITION_ENTROPY)),
         PRECISION
      );
   }

   @Test
   public void testGazeEntropyAnalyze_nonConsecutiveFixations() {
      final double EXPECTED_STATIONARY = 0.301029995663981;
      final double EXPECTED_TRANSITION = 0.138217295471837;
      DataEntry data = new DataEntry(Arrays.asList("FPOGID", "AOI")) {{
         process(Arrays.asList("0", "A"));
         process(Arrays.asList("1", ""));
         process(Arrays.asList("2", ""));
         process(Arrays.asList("4", "A"));
         process(Arrays.asList("5", "A"));
         process(Arrays.asList("6", ""));
      }};
      var results = GazeEntropy.analyze(data);
      assertEquals(
         "Unexpected stationary entropy.",
         EXPECTED_STATIONARY,
         Double.parseDouble(results.get(STATIONARY_ENTROPY)),
         PRECISION
      );
      assertEquals(
         "Unexpected transition entropy.",
         EXPECTED_TRANSITION, Double.parseDouble(results.get(TRANSITION_ENTROPY)),
         PRECISION
      );
   }
}
