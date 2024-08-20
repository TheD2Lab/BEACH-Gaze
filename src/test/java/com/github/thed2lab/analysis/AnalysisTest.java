package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.opencsv.CSVReader;

public class AnalysisTest {

   private final double PRECISION = 0.000000001; // allowable floating point error
   private final String ALL_GAZE_PATH = "./src/test/resources/test_all_gaze.csv";
   private final DataEntry ALL_GAZE = FileHandler.buildDataEntry(new File(ALL_GAZE_PATH));
   private final String ALL_FIXATION_PATH = "./src/test/resources/filtered_by_fixation.csv";
   private final DataEntry ALL_FIXATION = FileHandler.buildDataEntry(new File(ALL_FIXATION_PATH));

   @Test
   public void testGenerateResults_wholeScreenData() {
      List<String[]> expected = new LinkedList<>();
      try (
         FileReader fileReader = new FileReader("./src/test/resources/test_DGMs.csv");
         CSVReader csvReader = new CSVReader(fileReader);
      ) {
         expected.add(csvReader.readNext());
         expected.add(csvReader.readNext());
      } catch (Exception e) {
         fail("Could not read file");
      }
      
      List<List<String>> actual = Analysis.generateResults(ALL_GAZE, ALL_FIXATION);
      assertEquals(2, actual.size()); // headers and 1 row of data
      
      // check headers
      Iterator<String> actualIter = actual.get(0).iterator();
      for (String expValue : expected.get(0)) {
         assertEquals(expValue.trim(), actualIter.next());
      }

      // check values
      actualIter = actual.get(1).iterator();
      for (String expValue : expected.get(1)) {
         var bloop = actualIter.next();
         if (!isEqual(expValue, bloop)) {
            fail();
         }
      }
   }

   /**
    * Checks if two numbers saved as strings are equal, within an allowable amount of floating point error.
    * @param double1 first number to compare
    * @param double2 second number to compare
    * @return if the two numbers are equal
    */
   private boolean isEqual(String double1, String double2) {
      return Math.abs(Double.parseDouble(double1) - Double.parseDouble(double2)) < PRECISION;
   }
}
