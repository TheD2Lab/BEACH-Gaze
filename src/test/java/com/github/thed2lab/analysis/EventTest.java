package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class EventTest {
   final static List<String> INPUT_HEADERS = List.of("CS");
   final static String OUTPUT_HEADER = "total_number_of_l_mouse_clicks";

   @Test
   public void testEventAnalyze_twoEvents_twoLClicks() {
      DataEntry data = new DataEntry(INPUT_HEADERS);
      data.process(List.of("0"));
      data.process(List.of("1"));
      data.process(List.of("0"));
      data.process(List.of("1"));
      data.process(List.of("0"));

      Map<String, String> results = Event.analyze(data);
      assertTrue(results.size() == 1);
      assertEquals(2, Integer.parseInt(results.get(OUTPUT_HEADER)));
   }

   @Test
   public void testEventAnalyze_zeroEvents_zeroLClicks() {
      DataEntry data = new DataEntry(INPUT_HEADERS);
      data.process(List.of("0"));
      data.process(List.of("0"));
      data.process(List.of("0"));
      data.process(List.of("0"));
      data.process(List.of("0"));

      Map<String, String> results = Event.analyze(data);
      assertTrue(results.size() == 1);
      assertEquals(0, Integer.parseInt(results.get(OUTPUT_HEADER)));
   }

   @Test
   public void testEventAnalyze_non1Entries_zeroLClicks() {
      DataEntry data = new DataEntry(INPUT_HEADERS);
      data.process(List.of("0"));
      data.process(List.of("3"));
      data.process(List.of("0"));
      data.process(List.of("2"));
      data.process(List.of("0"));

      Map<String, String> results = Event.analyze(data);
      assertTrue(results.size() == 1);
      assertEquals(0, Integer.parseInt(results.get(OUTPUT_HEADER)));
   }
   
}
