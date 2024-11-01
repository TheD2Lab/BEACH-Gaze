package com.github.lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.lab.analysis.DataEntry;
import com.github.lab.analysis.FileHandler;
import com.github.lab.analysis.Gaze;

public class GazeTest {
   
   private final double PRECISION = 0.000000001; // allowable floating point error

   @Test
   public void testGazeAnalyze() {
      final String FIXATION_PATH = "./src/test/resources/valid_fixations.csv";
      Map<String, String> expectedResults = new HashMap<>();
      expectedResults.put("total_number_of_valid_recordings", "20");
      expectedResults.put("average_pupil_size_of_left_eye", "4.5716335");
      expectedResults.put("average_pupil_size_of_right_eye", "4.476186");
      expectedResults.put("average_pupil_size_of_both_eyes", "4.52390975");
      DataEntry fixationData = FileHandler.buildDataEntry(new File(FIXATION_PATH));
      Map<String, String> actualResults = Gaze.analyze(fixationData);

      assertEquals("Unexpected number of gaze results", expectedResults.size(), actualResults.size());

      for (String key : expectedResults.keySet()) {
         if (Math.abs(Double.valueOf(expectedResults.get(key)) - Double.valueOf(actualResults.get(key))) > PRECISION) {
            fail(String.format(
               "Different values for %s\n\tExpected: %s\n\tActual: %s",
               key,
               expectedResults.get(key),
               actualResults.get(key)
            ));
         }
      }
   }
}
