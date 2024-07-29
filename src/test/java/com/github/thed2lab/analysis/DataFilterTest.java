package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class DataFilterTest {
   // Note: final only prevents reassigning variables, not modifying objects.
   final String TEST_DIR = "./src/test/test_files/";

   final String ALL_GAZE_PATH = TEST_DIR + "test_all_gaze.csv";
   final DataEntry ALL_GAZE_DATA = FileHandler.buildDataEntry(new File(ALL_GAZE_PATH));

   final String EXPECTED_FIXATION_PATH = TEST_DIR + "filtered_by_fixation.csv";
   final int EXPECTED_NUM_VALID = 850;

   @Test
   public void testFilterByFixation() {
      DataEntry actual = DataFilter.filterByFixations(ALL_GAZE_DATA);
      DataEntry expected = FileHandler.buildDataEntry(new File(EXPECTED_FIXATION_PATH));
      compareDataEntries(expected, actual);
   }

   @Test
   public void testFilterByValidity() {
      DataEntry filtered = DataFilter.filterByValidity(ALL_GAZE_DATA);
      assertEquals(
         "Numbers of rows after filtering by validity does not match expected.",
         EXPECTED_NUM_VALID, filtered.rowCount()
      );
   }

   private void compareDataEntries(DataEntry expected, DataEntry actual) {
      assertEquals(expected.rowCount(), actual.rowCount());
      assertEquals(expected.columnCount(), actual.columnCount());
      List<String> actual_headers = actual.getHeaders();
      List<String> expected_headers = expected.getHeaders();

      for (int i = 0; i < actual.rowCount(); i++) {
         if (!actual_headers.get(i).equals(expected_headers.get(i))) {
            fail(String.format(
               "Mismatch headers at index %d. \n\tExpected: %s \n\tActual: %s",
               i,
               expected_headers.get(i),
               actual_headers.get(i)
            ));
         }
      }

      for (String header : actual_headers) {
         if (header.contains("TIME(")) {
            header = "TIME";
         }
         for (int i = 0; i < actual.rowCount(); i++) {
            if (!actual.getValue(header, i).equals(expected.getValue(header, i))) {
               fail(String.format("Different %s values in row %d", header, i));
            }
         }
      }
   }
}
