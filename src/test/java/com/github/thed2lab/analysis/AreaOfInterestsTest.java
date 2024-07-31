package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
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
      DataEntry emptyData = new DataEntry(Arrays.asList(""));
      boolean isEmpty = 0 == AreaOfInterests.splitAoiData(emptyData).size();
      assertTrue(isEmpty);
   }

   @Test(expected = NullPointerException.class)
   public void testSplitAoiData_missingHeader_throwsNullPointerException() {
      DataEntry illFormedData = new DataEntry(Arrays.asList("header")) {{
         process(Arrays.asList("0", "0"));
      }};
      AreaOfInterests.splitAoiData(illFormedData);
   }

   @Test
   public void testSplitAoiData_populatedData() {
      final List<String> HEADERS = Arrays.asList("FPOGID", "FPOGD", "AOI");
      final Map<String, DataEntry> EXPECTED_AOI_MAP = Collections.unmodifiableMap(new HashMap<String, DataEntry>() {{
         put("AOI_A", new DataEntry(HEADERS) {{
            process(Arrays.asList("0", ".5", "AOI_A"));
            process(Arrays.asList("1", ".5", "AOI_A"));
            process(Arrays.asList("5", ".5", "AOI_A"));
         }});
         put("AOI_B", new DataEntry(HEADERS) {{
            process(Arrays.asList("2", ".5", "AOI_B"));
         }});
         put("Undefined Area", new DataEntry(HEADERS) {{
            process(Arrays.asList("3", ".5", ""));
            process(Arrays.asList("4", ".5", ""));
         }});
      }});
      DataEntry gazeData = new DataEntry(HEADERS) {{
         process(Arrays.asList("0", ".5", "AOI_A"));
         process(Arrays.asList("1", ".5", "AOI_A"));
         process(Arrays.asList("2", ".5", "AOI_B"));
         process(Arrays.asList("3", ".5", ""));
         process(Arrays.asList("4", ".5", ""));
         process(Arrays.asList("5", ".5", "AOI_A"));
      }};

      Map<String, DataEntry> actualAoiMap = AreaOfInterests.splitAoiData(gazeData);
      boolean sameNumKeys = EXPECTED_AOI_MAP.size() == actualAoiMap.size();
      assertTrue(sameNumKeys);

      for (String key : EXPECTED_AOI_MAP.keySet()) {
         if (
            !actualAoiMap.containsKey(key) ||
            !EXPECTED_AOI_MAP.get(key).equals(actualAoiMap.get(key))
         ) { fail(); }
      }
   }

   // TODO: test generate metrics once Analysis tests are done

   
   @Test(expected = NullPointerException.class)
   public void testGetDuration_missingHeader_throwsNullPointerException() {
      DataEntry durationData = new DataEntry(Arrays.asList("Header")) {{
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
      }};
      AreaOfInterests.getDuration(durationData); //throws null pointer
   }

   @Test
   public void testGetDuration_emptyData_returnZero() {
      DataEntry durationData = new DataEntry(Arrays.asList("FPOGD"));
      double actualDuration = AreaOfInterests.getDuration(durationData);
      Boolean isZero = actualDuration == 0;
      assertTrue(isZero);
   }
   
   @Test
   public void testGetDuration_populatedData() {
      final double EXPECTED_DURATION = 0.4;
      DataEntry durationData = new DataEntry(Arrays.asList("FPOGD")) {{
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
      }};
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
      final List<Double> EXPECTED_RESULTS = Collections.unmodifiableList(Arrays.asList(0.5, 0.5));
      DataEntry allData = new DataEntry(Arrays.asList("FPOGD")) {{
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
      }};
      DataEntry aoiData = new DataEntry(Arrays.asList("FPOGD")) {{
         process(Arrays.asList("0.1"));
         process(Arrays.asList("0.1"));
      }};

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
      final List<String> HEADERS = Arrays.asList("FPOGID", "AOI");
      DataEntry emptyFixations = new DataEntry(HEADERS);
      Map<String, DataEntry> aoiMap = new HashMap<>();
      List<List<String>> actualResults = AreaOfInterests.generatePairResults(emptyFixations, aoiMap);
      boolean isOnlyHeaders = actualResults.size() == 1; // headers is the only row
      assertTrue(isOnlyHeaders);
   }

   @Test 
   public void testGeneratePairResults_emptyFixations_returnsListSize1() {
      final List<String> HEADERS = Arrays.asList("FPOGID", "AOI");
      DataEntry emptyFixations = new DataEntry(HEADERS);
      Map<String, DataEntry> aoiMap = Collections.unmodifiableMap(new HashMap<String, DataEntry>() {{
         put("AOI_A", new DataEntry(HEADERS) {{
            process(Arrays.asList("0", ".5", "AOI_A"));
            process(Arrays.asList("1", ".5", "AOI_A"));
            process(Arrays.asList("5", ".5", "AOI_A"));
         }});
      }});
      List<List<String>> actualResults = AreaOfInterests.generatePairResults(emptyFixations, aoiMap);
      boolean isOnlyHeaders = actualResults.size() == 1; // headers is the only row
      assertTrue(isOnlyHeaders);
   }

   @Test
   public void testGeneratePairResults_populatedData() {
      // didn't bother making this unmodifiable but image it is
      final Map<String, List<Double>> EXPECTED_RESULT = new HashMap<>() {{
         put("AOI_A -> AOI_A", Arrays.asList(1., 0.5, 0.));
         put("AOI_A -> AOI_B", Arrays.asList(1., 0.5, 1.));
         put("AOI_A -> Undefined Area", Arrays.asList(0., 0., 0.));
         put("AOI_B -> AOI_A", Arrays.asList(0., 0., 0.));
         put("AOI_B -> AOI_B", Arrays.asList(0., 0., 0.));
         put("AOI_B -> Undefined Area", Arrays.asList(1., 1., 1.));
         put("Undefined Area -> AOI_A", Arrays.asList(1., .5, 1.));
         put("Undefined Area -> AOI_B", Arrays.asList(0., 0., 0.));
         put("Undefined Area -> Undefined Area", Arrays.asList(1., .5, 0.));
      }};
      final List<String> INPUT_HEADERS = Arrays.asList("FPOGID", "FPOGD", "AOI");
      final List<String> OUTPUT_HEADERS = Arrays.asList(
         "aoi_pair",
         "transition_count",
         "proportion_including_self_transitions",
         "proportion_excluding_self_transitions"
      );
      DataEntry gazeData = new DataEntry(INPUT_HEADERS) {{
         process(Arrays.asList("0", ".5", "AOI_A"));
         process(Arrays.asList("1", ".5", "AOI_A"));
         process(Arrays.asList("2", ".5", "AOI_B"));
         process(Arrays.asList("3", ".5", ""));
         process(Arrays.asList("4", ".5", ""));
         process(Arrays.asList("5", ".5", "AOI_A"));
      }};
      Map<String, DataEntry> aoiMap = Collections.unmodifiableMap(new HashMap<String, DataEntry>() {{
         put("AOI_A", new DataEntry(INPUT_HEADERS) {{
            process(Arrays.asList("0", ".5", "AOI_A"));
            process(Arrays.asList("1", ".5", "AOI_A"));
            process(Arrays.asList("5", ".5", "AOI_A"));
         }});
         put("AOI_B", new DataEntry(INPUT_HEADERS) {{
            process(Arrays.asList("2", ".5", "AOI_B"));
         }});
         put("Undefined Area", new DataEntry(INPUT_HEADERS) {{
            process(Arrays.asList("3", ".5", ""));
            process(Arrays.asList("4", ".5", ""));
         }});
      }});

      List<List<String>> actualResults = AreaOfInterests.generatePairResults(gazeData, aoiMap);
      boolean isCorrectSize = actualResults.size() == EXPECTED_RESULT.size() + 1; // add 1 for headers
      assertTrue(isCorrectSize);

      Iterator<List<String>> rowIter = actualResults.iterator();
      Iterator<String> valuesIter = rowIter.next().iterator(); // first row is headers
      for (String header : OUTPUT_HEADERS) {
         if (!header.equals(valuesIter.next())) { fail(); }
      }

      while (rowIter.hasNext()) {
         valuesIter = rowIter.next().iterator();
         String key = valuesIter.next();
         for (double expectedVal : EXPECTED_RESULT.get(key)) {
            double actualVal = Double.parseDouble(valuesIter.next());
            if (Math.abs(expectedVal - actualVal) > PRECISION) {
               fail();
            }
         }
      }
   }
}
