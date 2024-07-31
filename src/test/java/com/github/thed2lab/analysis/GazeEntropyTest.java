package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class GazeEntropyTest {

   private final double PRECISION = 0.000000001; // allowable floating point error
   private final String STATIONARY_ENTROPY = "stationary_entropy";
   private final String TRANSITION_ENTROPY = "transition_entropy";

   
   @Test
   public void testAnalyze_twoAoi() {
      final double EXPECTED_STATIONARY = 0.301029995663981;
      final double EXPECTED_TRANSITION = 0.288732293303828;
      DataEntry data = new DataEntry(Arrays.asList("AOI")) {{
         process(Arrays.asList("A"));
         process(Arrays.asList("B"));
         process(Arrays.asList("B"));
         process(Arrays.asList("A"));
         process(Arrays.asList("A"));
         process(Arrays.asList("B"));
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
