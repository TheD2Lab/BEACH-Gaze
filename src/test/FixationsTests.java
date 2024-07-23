package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import analysis.DataEntry;
import analysis.FileHandler;
import analysis.Fixations;

public class FixationsTests {

   final double PRECISION = 0.000000001; // allowable floating point error
   
   @Test(expected = NullPointerException.class)
   public void testAnalyze_MissingFixationDurationHeader_ThrowsNullPointerException() {
      final ArrayList<String> HEADERS = new ArrayList<>() {{
         add("CNT");
         add("TIME(2023/02/22 14:26:24.897)");
         add("FPOGID");
      }};
      final ArrayList<List<String>> DATA = new ArrayList<>() {
      {
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("0");
               add("0");
               add("1");
               add("0.03382");
            }
         }));
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("1");
               add("0.00696");
               add("1");
               add("0.66125");

            }
         }));
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("2");
               add("0.01331");
               add("1");
               add("0.10718");
            }
         }));
      }};

      final DataEntry DATA_ENTRY = new DataEntry(HEADERS);
      for(List<String> line : DATA) {
         DATA_ENTRY.process(line);
      }
      Fixations.analyze(DATA_ENTRY);
   }

   @Test
   public void testAnalyze_RealData() {
      final Map<String, String> EXPECTED_RESULTS = Collections.unmodifiableMap(new HashMap<String, String>() {{
         put("Total Number of Fixations", "24");
         put("Sum of all fixation duration (s)", "5.63392");
         put("Mean fixation duration (s)", "0.234746667");
         put("Median fixation duration (s)", "0.16748");
         put("St.Dev. of fixation durations (s)", "0.154818618");
         put("Min. fixation duration (s)", "0.03382");
         put("Max. fixation duration (s)", "0.66125");
      }});
      final String FIXATION_PATH = "src/test/test_files/filtered_by_fixation.csv";
      DataEntry actual_data = FileHandler.buildDataEntry(new File(FIXATION_PATH));
      final Map<String, String> ACTUAL_RESULTS = Collections.unmodifiableMap(Fixations.analyze(actual_data));

      assertEquals("Different number of fixation results", EXPECTED_RESULTS.size(), ACTUAL_RESULTS.size());

      for(String key: EXPECTED_RESULTS.keySet()) {
         if(Math.abs(Double.valueOf(EXPECTED_RESULTS.get(key)) - Double.valueOf(ACTUAL_RESULTS.get(key))) > PRECISION) {
            fail(String.format(
               "Different values for %s\n\tExpected: %s\n\tActual: %s",
               key,
               EXPECTED_RESULTS.get(key),
               ACTUAL_RESULTS.get(key)
            ));
         }
      }

   }

}
