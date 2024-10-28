package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
      ArrayList<Coordinate> oneCoordinate = new ArrayList<>();
      oneCoordinate.add(new Coordinate(1, 1, 1));
      int actualSize = Saccades.getAllSaccadeLengths(oneCoordinate).length;
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeLengths_nonConsecutiveFid_returnEmptyArray() {
      ArrayList<Coordinate> coordinates = new ArrayList<>();
      // third parameter is fid; notice not consecutive
      coordinates.add(new Coordinate(1, 1, 1));
      coordinates.add(new Coordinate(1, 1, 3));
      coordinates.add(new Coordinate(1, 1, 5));
      int actualSize = Saccades.getAllSaccadeLengths(coordinates).length;
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeLengths_normalUseCase_returnLengthValues() {
      final List<Double> EXPECTED_LENGTHS = List.of(
         0.066976906,
         0.060254302,
         0.016724712,
         0.085343853
      );
      ArrayList<Coordinate> coordinates = new ArrayList<>();
      coordinates.add(new Coordinate(0.55532, 0.37966, 2));
      coordinates.add(new Coordinate(0.60302, 0.47807, 4));
      coordinates.add(new Coordinate(0.5417,	0.50501,	5));
      coordinates.add(new Coordinate(0.6017,	0.49948,	6));
      coordinates.add(new Coordinate(0.59532, 0.48402, 7));
      coordinates.add(new Coordinate(0.51246, 0.50446, 8));
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
      ArrayList<Double[]> oneSaccade = new ArrayList<>();
      oneSaccade.add(new Double[]{1., 1., 1.});
      int actualSize = Saccades.getAllSaccadeDurations(oneSaccade).size();
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeDurations_nonConsecutiveFid_returnEmptyArray() {
      ArrayList<Double[]> saccadeDetails = new ArrayList<>();
      // index 2 is fid; notice not consecutive
      saccadeDetails.add(new Double[]{1., 1., 1.});
      saccadeDetails.add(new Double[]{1., 1., 3.});
      saccadeDetails.add(new Double[]{1., 1., 5.});
      int actualSize = Saccades.getAllSaccadeDurations(saccadeDetails).size();
      assertEquals(0, actualSize);
   }

   @Test
   public void testGetAllSaccadeDurations_normalUseCase_returnsDurationValues() {
      final List<Double> EXPECTED_DURATIONS = List.of(
         0.00659,
         0.00696,
         0.00672,
         0.00671
      );
      ArrayList<Double[]> saccadeDetails = new ArrayList<>();
      saccadeDetails.add(new Double[]{0.04236, 0.66125, 2.});
      saccadeDetails.add(new Double[]{1.02502, 0.12061, 4.});
      saccadeDetails.add(new Double[]{1.15222, 0.16064, 5.});
      saccadeDetails.add(new Double[]{1.31982, 0.1272, 6.});
      saccadeDetails.add(new Double[]{1.45374, 0.34839, 7.});
      saccadeDetails.add(new Double[]{1.80884, 0.22095, 8.});
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
      ArrayList<Double> fixationDurations = new ArrayList<>();
      fixationDurations.add(0.66125);
      fixationDurations.add(0.12061);
      fixationDurations.add(0.16064);
      fixationDurations.add(0.12720);
      fixationDurations.add(0.34839);
      fixationDurations.add(0.22095);
      
      ArrayList<Double> saccadeDurations = new ArrayList<>();
      saccadeDurations.add(0.00659);
      saccadeDurations.add(0.00696);
      saccadeDurations.add(0.00672);
      saccadeDurations.add(0.00671);

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
      ArrayList<Double> fixationDurations = new ArrayList<>();
      fixationDurations.add(0.66125);
      fixationDurations.add(0.12061);
      fixationDurations.add(0.16064);
      fixationDurations.add(0.12720);
      fixationDurations.add(0.34839);
      fixationDurations.add(0.22095);
      ArrayList<Double> saccadeDurations = new ArrayList<>();
      boolean isNaN = Double.isNaN(
         Saccades.getFixationToSaccadeRatio(fixationDurations, saccadeDurations)
      );
      assertTrue(isNaN);
   }

   @Test
   public void testGetFixationToSaccadeRatio_normalUseCase_returnRatio() {
      final double EXPECTED_RATIO = 60.75018532246;
      ArrayList<Double> fixationDurations = new ArrayList<>();
      fixationDurations.add(0.66125);
      fixationDurations.add(0.12061);
      fixationDurations.add(0.16064);
      fixationDurations.add(0.12720);
      fixationDurations.add(0.34839);
      fixationDurations.add(0.22095);
      ArrayList<Double> saccadeDurations = new ArrayList<>(List.of(
         0.00659,
         0.00696,
         0.00672,
         0.00671
      ));
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
      final String DATA_PATH = "./src/test/resources/valid_fixation_screenApplied.csv";
      Map<String, Double> expectedResults = new LinkedHashMap<String, Double>();
      expectedResults.put("total_number_of_saccades", 16.);

      expectedResults.put("sum_of_all_saccade_lengths", 2530.8660814283);
      expectedResults.put("mean_saccade_length", 158.1791300893);
      expectedResults.put("median_saccade_length", 119.3467488446);
      expectedResults.put("stdev_of_saccade_lengths", 106.3517507880);
      expectedResults.put("min_saccade_length", 20.7083517065);
      expectedResults.put("max_saccade_length", 349.4148372122);

      expectedResults.put("sum_of_all_saccade_durations", 0.19091);
      expectedResults.put("mean_saccade_duration", 0.011931875);
      expectedResults.put("median_saccade_duration", 0.00671);
      expectedResults.put("stdev_of_saccade_durations", 0.0094964149);
      expectedResults.put("min_saccade_duration", 0.00598);
      expectedResults.put("max_saccade_duration", 0.03028);

      expectedResults.put("sum_of_all_saccade_amplitudes", 58.834398303511215);
      expectedResults.put("mean_saccade_amplitude", 3.677149893969451);
      expectedResults.put("median_saccade_amplitude", 2.781254831659127);
      expectedResults.put("stdev_of_saccade_amplitude", 2.4654446860609887);
      expectedResults.put("min_saccade_amplitude", 0.48295537661957966);
      expectedResults.put("max_saccade_amplitude", 8.094871649121);

      expectedResults.put("scanpath_duration", 4.95373);
      expectedResults. put("fixation_to_saccade_ratio", 24.9479859620);

      DataEntry dEntry = FileHandler.buildDataEntry(new File(DATA_PATH));
      Map<String, String> actualResults = Saccades.analyze(dEntry);

      assertEquals("Unexpected output size.", expectedResults.size(), actualResults.size());

      for (String key : expectedResults.keySet()) {
         if (Math.abs(expectedResults.get(key) - Double.valueOf(actualResults.get(key))) > PRECISION) {
            fail(String.format("Different values for %s\n\tExpected: %f\n\tActual: %s",
               key,
               expectedResults.get(key),
               actualResults.get(key)
            ));
         }
      }
   }
}
