package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SaccadesTest {

   private final double PRECISION = 0.000000001; // allowable floating point error
   
   @Test
   public void testGetAllSaccadeLengths_emptyCoordinates_returnEmptyArray() {
      ArrayList<Coordinate> emptyArray = new ArrayList<>();
      int actualLength = Saccades.getAllSaccadeLengths(emptyArray).length;
      assertEquals(0, actualLength);
   }

   @Test
   public void testGetAllSaccadeLengths_oneCoordinate_returnEmptyArray() {
      ArrayList<Coordinate> oneCoordinate = new ArrayList<>() {{
         add(new Coordinate(1, 1, 1));
      }};
      int actualSize = Saccades.getAllSaccadeLengths(oneCoordinate).length;
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeLengths_nonConsecutiveFid_returnEmptyArray() {
      ArrayList<Coordinate> coordinates = new ArrayList<>() {{
         // third parameter is fid; notice not consecutive
         add(new Coordinate(1, 1, 1));
         add(new Coordinate(1, 1, 3));
         add(new Coordinate(1, 1, 5));
      }};
      int actualSize = Saccades.getAllSaccadeLengths(coordinates).length;
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeLengths_normalUseCase_returnLengthValues() {
      final List<Double> EXPECTED_LENGTHS = Collections.unmodifiableList(new ArrayList<>() {{
         add(0.066976906);
         add(0.060254302);
         add(0.016724712);
         add(0.085343853);
      }});
      ArrayList<Coordinate> coordinates = new ArrayList<>() {{
         add(new Coordinate(0.55532, 0.37966, 2));
         add(new Coordinate(0.60302, 0.47807, 4));
         add(new Coordinate(0.5417,	0.50501,	5));
         add(new Coordinate(0.6017,	0.49948,	6));
         add(new Coordinate(0.59532, 0.48402, 7));
         add(new Coordinate(0.51246, 0.50446, 8));
      }};
      Double[] actualLengths = Saccades.getAllSaccadeLengths(coordinates);
      assertEquals("Unexpected number of saccade lengths", EXPECTED_LENGTHS.size(), actualLengths.length);

      Iterator<Double> expIter = EXPECTED_LENGTHS.iterator();

      for (Double aLength : actualLengths) {
         if (Math.abs(expIter.next() - aLength) > PRECISION) {
            fail("Actual saccade length does not match expected.");
         }
      }
   }

   @Test
   public void testGetAllSaccadeDurations_emptyCoordinates_returnEmptyArray() {
      ArrayList<Double[]> emptyArray = new ArrayList<>();
      int actualSize = Saccades.getAllSaccadeDurations(emptyArray).size();
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeDurations_oneCoordinate_returnEmptyArray() {
      ArrayList<Double[]> oneSaccade = new ArrayList<>() {{
         add(new Double[]{1., 1., 1.});
      }};
      int actualSize = Saccades.getAllSaccadeDurations(oneSaccade).size();
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeDurations_nonConsecutiveFid_returnEmptyArray() {
      ArrayList<Double[]> saccadeDetails = new ArrayList<>() {{
         // index 2 is fid; notice not consecutive
         add(new Double[]{1., 1., 1.});
         add(new Double[]{1., 1., 3.});
         add(new Double[]{1., 1., 5.});
      }};
      int actualSize = Saccades.getAllSaccadeDurations(saccadeDetails).size();
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeDurations_normalUseCase_returnsDurationValues() {
      final List<Double> EXPECTED_DURATIONS = Collections.unmodifiableList(new ArrayList<>() {{
         add(0.00659);
         add(0.00696);
         add(0.00672);
         add(0.00671);
      }});
      ArrayList<Double[]> saccadeDetails = new ArrayList<>() {{
         add(new Double[]{0.04236, 0.66125, 2.});
         add(new Double[]{1.02502, 0.12061, 4.});
         add(new Double[]{1.15222, 0.16064, 5.});
         add(new Double[]{1.31982, 0.1272, 6.});
         add(new Double[]{1.45374, 0.34839, 7.});
         add(new Double[]{1.80884, 0.22095, 8.});
      }};
      ArrayList<Double> actualDurations = Saccades.getAllSaccadeDurations(saccadeDetails);
      
      assertEquals(
         "Unexpected number of saccade durations.",
         EXPECTED_DURATIONS.size(),
         actualDurations.size()
      );

      Iterator<Double> expIter = EXPECTED_DURATIONS.iterator();

      for(Double aDuration: actualDurations) {
         if(Math.abs(expIter.next() - aDuration) > PRECISION) {
            fail("Actual saccade duration does not match expected.");
         }
      }
   }

   @Test
   public void testGetScanpathDuration_emptyData_return0() {
      ArrayList<Double> emptyData = new ArrayList<>();
      assertEquals(0.0, Saccades.getScanpathDuration(emptyData, emptyData), 0);
   }

   @Test
   public void testGetScanpathDurations_normalUseCase_returnDurationValue() {
      final double EXPECTED_DURATION = 1.666020;
      ArrayList<Double> fixationDurations = new ArrayList<>() {{
         add(0.66125);
         add(0.12061);
         add(0.16064);
         add(0.12720);
         add(0.34839);
         add(0.22095);
      }};
      ArrayList<Double> saccadeDurations = new ArrayList<>() {{
         add(0.00659);
         add(0.00696);
         add(0.00672);
         add(0.00671);
      }};
      double actualDuration = Saccades.getScanpathDuration(fixationDurations, saccadeDurations);
      assertEquals(
         "Actual scanpath duration does not match expected.",
         EXPECTED_DURATION,
         actualDuration,
         PRECISION
      );
   }

   @Test
   public void testGetFixationToSaccadeRatio_emptySaccadeDurations_returnNaN() {
      ArrayList<Double> fixationDurations = new ArrayList<>() {{
         add(0.66125);
         add(0.12061);
         add(0.16064);
         add(0.12720);
         add(0.34839);
         add(0.22095);
      }};
      ArrayList<Double> saccadeDurations = new ArrayList<>();
      boolean isNaN = Double.isNaN(
         Saccades.getFixationToSaccadeRatio(fixationDurations, saccadeDurations)
      );
      assertTrue(isNaN);
   }

   @Test
   public void testGetFixationToSaccadeRatio_normalUseCase_returnRatio() {
      final double EXPECTED_RATIO = 60.75018532246;
      ArrayList<Double> fixationDurations = new ArrayList<>() {{
         add(0.66125);
         add(0.12061);
         add(0.16064);
         add(0.12720);
         add(0.34839);
         add(0.22095);
      }};
      ArrayList<Double> saccadeDurations = new ArrayList<>() {{
         add(0.00659);
         add(0.00696);
         add(0.00672);
         add(0.00671);
      }};
      double actualRatio = Saccades.getFixationToSaccadeRatio(fixationDurations, saccadeDurations);
      assertEquals(
         "Actual fixation to saccade ratio does not match expected.",
         EXPECTED_RATIO,
         actualRatio,
         PRECISION
      );
   }

   @Test
   public void testSaccadeAnalyze_validFixations_returnHeadersAndValues() {
      final String DATA_PATH = "./src/test/resources/valid_fixations.csv";
      final Map<String, String> EXPECTED_RESULTS = Collections.unmodifiableMap(new HashMap<String, String>() {{
         put("total_number_of_saccades", "16");

         put("sum_of_all_saccade_lengths", "1.4804786014");
         put("mean_saccade_length", "0.0925299126");
         put("median_saccade_length", "0.0761603796");
         put("stdev_of_saccade_lengths", "0.0572082421");
         put("min_saccade_length", "0.0167247123");
         put("max_saccade_length", "0.2016258416");

         put("sum_of_all_saccade_durations", "0.19091");
         put("mean_saccade_duration", "0.0119318750");
         put("median_saccade_duration", "0.0067100000");
         put("stdev_of_saccade_durations", "0.0094964149");
         put("min_saccade_duration", "0.0059800000");
         put("max_saccade_duration", "0.0302800000");

         put("scanpath_duration", "4.95373");
         put("fixation_to_saccade_ratio", "24.9479859620");
      }});
      DataEntry dEntry = FileHandler.buildDataEntry(new File(DATA_PATH));
      Map<String, String> actualResults = Saccades.analyze(dEntry);

      assertEquals("Unexpected output size.", EXPECTED_RESULTS.size(), actualResults.size());

      for (String key : EXPECTED_RESULTS.keySet()) {
         if (Math.abs(Double.valueOf(EXPECTED_RESULTS.get(key)) - Double.valueOf(actualResults.get(key))) > PRECISION) {
            fail(String.format("Different values for %s\n\tExpected: %s\n\tActual: %s",
               key,
               EXPECTED_RESULTS.get(key),
               actualResults.get(key)
            ));
         }
      }
   }
}
