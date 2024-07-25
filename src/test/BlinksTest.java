package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import analysis.Blinks;
import analysis.DataEntry;

public class BlinksTest {
   // Note: test values are picked for easy hand calculations, not from from real data.

   final double PRECISION = 0.000000001; // allowable floating point error
   static final List<String> HEADERS = Collections.unmodifiableList(
      Arrays.asList(new String[]{"TIME", "CNT", "BKID"})
   );
   static final String KEY = "Average Blink Rate per Minute";

   @Test
   public void testBlinksAnalyze_emptyData_BlinkRateOfNaN() {
      DataEntry emptyData = new DataEntry(HEADERS);
      Map<String, String> actual = Blinks.analyze(emptyData);
      if (!actual.containsKey(KEY)) {
         fail(String.format("Does not contain \"%s\"", KEY));
      }
      assertTrue(actual.get(KEY).equals("NaN"));
   }

   @Test
   public void testBlinksAnalyze_noBlinks_BlinkRateOfZero() {
      DataEntry emptyData = new DataEntry(HEADERS) {{
         process(Arrays.asList(new String[]{"0.0", "0", "0"}));
         process(Arrays.asList(new String[]{"0.1", "1", "0"}));
         process(Arrays.asList(new String[]{"0.2", "2", "0"}));
         process(Arrays.asList(new String[]{"0.3", "3", "0"}));
      }};
      Map<String, String> actualMap = Blinks.analyze(emptyData);
      if (!actualMap.containsKey(KEY)) {
         fail(String.format("Does not contain \"%s\"", KEY));
      }
      double actualBlinkRate = Double.parseDouble(actualMap.get(KEY));
      assertEquals(0.0, actualBlinkRate, PRECISION);
   }

   @Test
   public void testBlinksAnalyze_noConsecutiveDataRecords_BlinkRateOfNaN() {
      DataEntry data = new DataEntry(HEADERS) {{
         process(Arrays.asList(new String[]{"0.0", "0", "0"}));
         process(Arrays.asList(new String[]{"0.2", "2", "1"}));
         process(Arrays.asList(new String[]{"0.4", "4", "0"}));
         process(Arrays.asList(new String[]{"0.6", "6", "0"}));
      }};
      Map<String, String> actualMap = Blinks.analyze(data);
      if (!actualMap.containsKey(KEY)) {
         fail(String.format("Does not contain \"%s\"", KEY));
      }
      assertTrue(actualMap.get(KEY).equals("NaN"));
   }
   
   @Test
   public void testBlinksAnalyze_duplicateBlinkIdContinuousData() {
      // 2 blinks in 1 second => (2/1)*60 = 120 blinks per minute
      final double EXPECTED_BLINK_RATE = 120;
      DataEntry data = new DataEntry(HEADERS) {{
         process(Arrays.asList(new String[]{"0.0", "0", "0"}));
         process(Arrays.asList(new String[]{"0.1", "1", "0"}));
         process(Arrays.asList(new String[]{"0.2", "2", "1"}));
         process(Arrays.asList(new String[]{"0.3", "3", "1"}));
         process(Arrays.asList(new String[]{"0.4", "4", "1"}));
         process(Arrays.asList(new String[]{"0.5", "5", "1"}));
         process(Arrays.asList(new String[]{"0.6", "6", "0"}));
         process(Arrays.asList(new String[]{"0.7", "7", "2"}));
         process(Arrays.asList(new String[]{"0.8", "8", "2"}));
         process(Arrays.asList(new String[]{"0.9", "9", "2"}));
         process(Arrays.asList(new String[]{"1.0", "10", "0"}));
      }};
      Map<String, String> actualMap = Blinks.analyze(data);
      if (!actualMap.containsKey(KEY)) {
         fail(String.format("Does not contain \"%s\"", KEY));
      }
      double actualBlinkRate = Double.parseDouble(actualMap.get(KEY));
      assertEquals(EXPECTED_BLINK_RATE, actualBlinkRate, PRECISION);
   }

   @Test
   public void testBlinksAnalyze_duplicateBlinkIdNonContinuousData() {
      // 2 blinks in 0.5 second => (2/0.6)*60 = 200 blinks per minute
      final double EXPECTED_BLINK_RATE = 200;
      DataEntry data = new DataEntry(HEADERS) {{
         process(Arrays.asList(new String[]{"0.0", "0", "0"}));
         process(Arrays.asList(new String[]{"0.2", "2", "1"}));
         process(Arrays.asList(new String[]{"0.3", "3", "1"}));
         process(Arrays.asList(new String[]{"0.4", "4", "1"}));
         process(Arrays.asList(new String[]{"0.5", "5", "1"}));
         process(Arrays.asList(new String[]{"0.6", "6", "0"}));
         process(Arrays.asList(new String[]{"0.7", "7", "2"}));
         process(Arrays.asList(new String[]{"0.9", "9", "2"}));
         process(Arrays.asList(new String[]{"1.0", "10", "0"}));
      }};
      Map<String, String> actualMap = Blinks.analyze(data);
      if (!actualMap.containsKey(KEY)) {
         fail(String.format("Does not contain \"%s\"", KEY));
      }
      double actualBlinkRate = Double.parseDouble(actualMap.get(KEY));
      assertEquals(EXPECTED_BLINK_RATE, actualBlinkRate, PRECISION);
   }
}
