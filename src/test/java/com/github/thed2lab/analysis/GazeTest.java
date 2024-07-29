package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class GazeTest {
   
   final double PRECISION = 0.000000001; // allowable floating point error

   @Test
   public void testGazeAnalyze() {
      final Map<String, String> EXPECTED_RESULTS = Collections.unmodifiableMap(new HashMap<String, String>(){{
         put("total_number_of_valid_recordings", "20");
         put("average_pupil_size_of_left_eye", "4.5716335");
         put("average_pupil_size_of_right_eye", "4.476186");
         put("average_pupil_size_of_both_eyes", "4.52390975");
      }});
      final String FIXATION_PATH = "./src/test/test_files/valid_fixations.csv";
      DataEntry fixationData = FileHandler.buildDataEntry(new File(FIXATION_PATH));
      Map<String, String> actualResults = Gaze.analyze(fixationData);

      assertEquals("Unexpected number of gaze results", EXPECTED_RESULTS.size(), actualResults.size());

      for (String key : EXPECTED_RESULTS.keySet()) {
         if (Math.abs(Double.valueOf(EXPECTED_RESULTS.get(key)) - Double.valueOf(actualResults.get(key))) > PRECISION) {
            fail(String.format(
               "Different values for %s\n\tExpected: %s\n\tActual: %s",
               key,
               EXPECTED_RESULTS.get(key),
               actualResults.get(key)
            ));
         }
      }
   }

}
