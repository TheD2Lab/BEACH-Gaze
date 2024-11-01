package com.github.lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.lab.analysis.DataEntry;
import com.github.lab.analysis.FileHandler;
import com.github.lab.analysis.Fixations;

public class FixationsTests {

   private final double PRECISION = 0.000000001; // allowable floating point error
   
   @Test(expected = NullPointerException.class)
   public void testFixationsAnalyze_missingFixationDurationHeader_throwsNullPointerException() {
      final List<String> HEADERS = List.of(
         "CNT",
         "TIME(2023/02/22 14:26:24.897)",
         "FPOGID"
      );
      List<List<String>> data = List.of(
         List.of("0", "0", "1", "0.03382"),
         List.of("1", "0.00696", "1", "0.66125"),
         List.of("2", "0.01331", "1", "0.10718")
      );
      DataEntry dEntry = new DataEntry(HEADERS);
      for(List<String> line : data) {
         dEntry.process(line);
      }
      Fixations.analyze(dEntry);
   }

   @Test
   public void testFixationsAnalyze_realData_returnHeadersAndValues() {
      final String FIXATION_PATH = "./src/test/resources/valid_fixations.csv";
      Map<String, Double> expectedResults = new HashMap<String, Double>();
      expectedResults.put("total_number_of_fixations", 20.);
      expectedResults.put("sum_of_all_fixation_duration_s", 4.76282);
      expectedResults.put("mean_fixation_duration_s", 0.238141);
      expectedResults.put("median_fixation_duration_s", 0.16748);
      expectedResults.put("stdev_of_fixation_durations_s", 0.152435548482);
      expectedResults.put("min_fixation_duration_s", 0.08044);
      expectedResults.put("max_fixation_duration_s", 0.66125);
      DataEntry actual_data = FileHandler.buildDataEntry(new File(FIXATION_PATH));
      Map<String, String> actualResults = Collections.unmodifiableMap(Fixations.analyze(actual_data));

      assertEquals("Different number of fixation results", expectedResults.size(), actualResults.size());

      for(String key: expectedResults.keySet()) {
         if(Math.abs(expectedResults.get(key) - Double.valueOf(actualResults.get(key))) > PRECISION) {
            fail(String.format(
               "Different values for %s\n\tExpected: %f\n\tActual: %s",
               key,
               expectedResults.get(key),
               actualResults.get(key)
            ));
         }
      }
   }
}
