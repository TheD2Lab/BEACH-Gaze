package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class AreaOfInterestsTest {

   private final double PRECISION = 0.000000001; // allowable floating point error

   @Test
   public void testSplitAoiData_emptyData_returnEmptyMap() {
      DataEntry emptyData = new DataEntry(List.of(""));
      boolean isEmpty = 0 == AreaOfInterests.splitAoiData(emptyData).size();
      assertTrue(isEmpty);
   }

   @Test(expected = NullPointerException.class)
   public void testSplitAoiData_missingHeader_throwsNullPointerException() {
      DataEntry illFormedData = new DataEntry(List.of("header"));
      illFormedData.process(List.of("0", "0"));
      AreaOfInterests.splitAoiData(illFormedData);
   }

   @Test
   public void testSplitAoiData_populatedData() {
      final List<String> HEADERS = List.of("FPOGID", "FPOGD", "AOI");
      Map<String, DataEntry> expectedAoiMap = new HashMap<String, DataEntry>();

      // Add AOI_A to map
      DataEntry singleAoiData = new DataEntry(HEADERS);
      singleAoiData.process(List.of("0", ".5", "AOI_A"));
      singleAoiData.process(List.of("1", ".5", "AOI_A"));
      singleAoiData.process(List.of("5", ".5", "AOI_A"));
      expectedAoiMap.put("AOI_A", singleAoiData);

      // Add AOI_B to map
      singleAoiData = new DataEntry(HEADERS);
      singleAoiData.process(List.of("2", ".5", "AOI_B"));
      expectedAoiMap.put("AOI_B", singleAoiData);

      // Add "Undefined Area" to map
      singleAoiData = new DataEntry(HEADERS);
      singleAoiData.process(List.of("3", ".5", ""));
      singleAoiData.process(List.of("4", ".5", ""));
      expectedAoiMap.put("Undefined Area", singleAoiData);

      DataEntry gazeData = new DataEntry(HEADERS);
      gazeData.process(List.of("0", ".5", "AOI_A"));
      gazeData.process(List.of("1", ".5", "AOI_A"));
      gazeData.process(List.of("2", ".5", "AOI_B"));
      gazeData.process(List.of("3", ".5", ""));
      gazeData.process(List.of("4", ".5", ""));
      gazeData.process(List.of("5", ".5", "AOI_A"));


      Map<String, DataEntry> actualAoiMap = AreaOfInterests.splitAoiData(gazeData);
      boolean sameNumKeys = expectedAoiMap.size() == actualAoiMap.size();
      assertTrue(sameNumKeys);

      for (String key : expectedAoiMap.keySet()) {
         if (
            !actualAoiMap.containsKey(key) ||
            !expectedAoiMap.get(key).equals(actualAoiMap.get(key))
         ) { fail(); }
      }
   }

   // TODO: test generate metrics once Analysis tests are done

   
   @Test(expected = NullPointerException.class)
   public void testGetDuration_missingHeader_throwsNullPointerException() {
      DataEntry durationData = new DataEntry(List.of("Header"));
      durationData.process(List.of("0.1"));
      durationData.process(List.of("0.1"));
      durationData.process(List.of("0.1"));
      durationData.process(List.of("0.1"));
      AreaOfInterests.getDuration(durationData); //throws null pointer
   }

   @Test
   public void testGetDuration_emptyData_returnZero() {
      DataEntry durationData = new DataEntry(List.of("FPOGD"));
      double actualDuration = AreaOfInterests.getDuration(durationData);
      Boolean isZero = actualDuration == 0;
      assertTrue(isZero);
   }
   
   @Test
   public void testGetDuration_populatedData() {
      final double EXPECTED_DURATION = 0.4;
      DataEntry durationData = new DataEntry(List.of("FPOGD"));
      durationData.process(List.of("0.1"));
      durationData.process(List.of("0.1"));
      durationData.process(List.of("0.1"));
      durationData.process(List.of("0.1"));
      double actualDuration = AreaOfInterests.getDuration(durationData);
      assertEquals(EXPECTED_DURATION, actualDuration, PRECISION);
   }

   @Test
   public void testGetProportions_emptyData_NaNValues() {
      String[] headers = {"header1", "header2"};
      DataEntry fixationData = new DataEntry(headers);
      List<String> actualResults = AreaOfInterests.getProportions(fixationData, fixationData, 0);
      Boolean isNaN = actualResults.get(0).equals("NaN") && actualResults.get(1).equals("NaN");
      assertTrue(isNaN);
   }

   @Test
   public void testGetProportions_populatedData() {
      final double ALL_DURATION = 0.4;
      final List<Double> EXPECTED_RESULTS = Collections.unmodifiableList(List.of(0.5, 0.5));
      DataEntry allData = new DataEntry(List.of("FPOGD"));
      allData.process(List.of("0.1"));
      allData.process(List.of("0.1"));
      allData.process(List.of("0.1"));
      allData.process(List.of("0.1"));
      DataEntry aoiData = new DataEntry(List.of("FPOGD"));
      aoiData.process(List.of("0.1"));
      aoiData.process(List.of("0.1"));


      List<String> actualResults = AreaOfInterests.getProportions(allData, aoiData, ALL_DURATION);
      boolean isSameSize = EXPECTED_RESULTS.size() == actualResults.size();
      assertTrue(isSameSize);
      Iterator<Double> expIter = EXPECTED_RESULTS.iterator();
      Iterator<String> actIter = actualResults.iterator();
      while(expIter.hasNext()) {
         if (Math.abs(expIter.next() - Double.parseDouble(actIter.next())) > PRECISION) {
            fail();
         }
      }
   }

   @Test
   public void testGeneratePairResults_emptyData_returnsListSize1() {
      final List<String> HEADERS = List.of("FPOGID", "AOI");
      DataEntry emptyFixations = new DataEntry(HEADERS);
      Map<String, DataEntry> aoiMap = new HashMap<>();
      List<List<String>> actualResults = AreaOfInterests.generatePairResults(emptyFixations, aoiMap);
      boolean isOnlyHeaders = actualResults.size() == 1; // headers is the only row
      assertTrue(isOnlyHeaders);
   }

   @Test 
   public void testGeneratePairResults_emptyFixations_returnsListSize1() {
      final List<String> HEADERS = List.of("FPOGID", "AOI");
      DataEntry emptyFixations = new DataEntry(HEADERS);
      Map<String, DataEntry> aoiMap = new HashMap<String, DataEntry>();

      // Add AOI gaze data
      DataEntry aoiAData = new DataEntry(HEADERS);
      aoiAData.process(List.of("0", ".5", "AOI_A"));
      aoiAData.process(List.of("1", ".5", "AOI_A"));
      aoiAData.process(List.of("5", ".5", "AOI_A"));
      aoiMap.put("AOI_A", aoiAData);

      // Get and check actual results
      List<List<String>> actualResults = AreaOfInterests.generatePairResults(emptyFixations, aoiMap);
      boolean isOnlyHeaders = actualResults.size() == 1; // result headers is the only row
      assertTrue(isOnlyHeaders);
   }

   @Test
   public void testGeneratePairResults_populatedData() {
      final List<String> INPUT_HEADERS = List.of("FPOGID", "FPOGD", "AOI");
      final List<String> OUTPUT_HEADERS = List.of(
         "aoi_pair",
         "transition_count",
         "proportion_including_self_transitions",
         "proportion_excluding_self_transitions"
      );
      // Not ideal to build the expected object but i'd rather loop with one fail than assert each key
      // maybe this should broken up into smaller tests if time permits
      Map<String, List<Double>> expectedResults = new HashMap<>();
      expectedResults.put("AOI_A -> AOI_A", List.of(1., 0.5, 0.));
      expectedResults.put("AOI_A -> AOI_B", List.of(1., 0.5, 1.));
      expectedResults.put("AOI_A -> Undefined Area", List.of(0., 0., 0.));
      expectedResults.put("AOI_B -> AOI_A", List.of(0., 0., 0.));
      expectedResults.put("AOI_B -> AOI_B", List.of(0., 0., 0.));
      expectedResults.put("AOI_B -> Undefined Area", List.of(1., 1., 1.));
      expectedResults.put("Undefined Area -> AOI_A", List.of(1., .5, 1.));
      expectedResults.put("Undefined Area -> AOI_B", List.of(0., 0., 0.));
      expectedResults.put("Undefined Area -> Undefined Area", List.of(1., .5, 0.));
      
      DataEntry gazeData = new DataEntry(INPUT_HEADERS);
      gazeData.process(List.of("0", ".5", "AOI_A"));
      gazeData.process(List.of("1", ".5", "AOI_A"));
      gazeData.process(List.of("2", ".5", "AOI_B"));
      gazeData.process(List.of("3", ".5", ""));
      gazeData.process(List.of("4", ".5", ""));
      gazeData.process(List.of("5", ".5", "AOI_A"));

      Map<String, DataEntry> aoiMap = new HashMap<String, DataEntry>();
      // Add AOI_A
      DataEntry singleAoi = new DataEntry(INPUT_HEADERS);
      singleAoi.process(List.of("0", ".5", "AOI_A"));
      singleAoi.process(List.of("1", ".5", "AOI_A"));
      singleAoi.process(List.of("5", ".5", "AOI_A"));
      aoiMap.put("AOI_A", singleAoi);
      // Add AOI_B
      singleAoi = new DataEntry(INPUT_HEADERS);
      singleAoi.process(List.of("2", ".5", "AOI_B"));
      aoiMap.put("AOI_B", singleAoi);
      //Add undefined Area
      singleAoi = new DataEntry(INPUT_HEADERS);
      singleAoi.process(List.of("3", ".5", ""));
      singleAoi.process(List.of("4", ".5", ""));
      aoiMap.put("Undefined Area", singleAoi);

      List<List<String>> actualResults = AreaOfInterests.generatePairResults(gazeData, aoiMap);
      boolean isCorrectSize = actualResults.size() == expectedResults.size() + 1; // add 1 for headers
      assertTrue(isCorrectSize);

      Iterator<List<String>> rowIter = actualResults.iterator();
      Iterator<String> valuesIter = rowIter.next().iterator(); // first row is headers
      for (String header : OUTPUT_HEADERS) {
         if (!header.equals(valuesIter.next())) { fail(); }
      }
      
      while (rowIter.hasNext()) {
         valuesIter = rowIter.next().iterator();
         String key = valuesIter.next();
         for (double expectedVal : expectedResults.get(key)) {
            double actualVal = Double.parseDouble(valuesIter.next());
            if (Math.abs(expectedVal - actualVal) > PRECISION) {
               fail();
            }
         }
      }
   }
}
