package com.github.lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.github.lab.analysis.Constants;
import com.github.lab.analysis.DataEntry;
import com.github.lab.analysis.DataFilter;
import com.github.lab.analysis.FileHandler;
import com.github.lab.analysis.WindowSettings;
import com.github.lab.analysis.Windows;

public class WindowsTest {

   private final double PRECISION = 0.000000001; // allowable floating point error
   final String GAZE_PATH = "src/test/resources/test_all_gaze.csv";
   final DataEntry GAZE_DATA = FileHandler.buildDataEntry(new File(GAZE_PATH));

   @Test
   public void testSpliceTumblingWindow() {
      final double WINDOW_SIZE = 1;
      List<DataEntry> windows = Windows.spliceTumblingWindow(GAZE_DATA, WINDOW_SIZE);
      assertTrue(windows.size() == 7);
      // check the windows starts at the correct line
      assertEquals("0", windows.get(0).getValue(Constants.DATA_ID, 0));
      assertEquals("150", windows.get(1).getValue(Constants.DATA_ID, 0));
      assertEquals("297", windows.get(2).getValue(Constants.DATA_ID, 0));
      assertEquals("446", windows.get(3).getValue(Constants.DATA_ID, 0));
      assertEquals("596", windows.get(4).getValue(Constants.DATA_ID, 0));
      assertEquals("744", windows.get(5).getValue(Constants.DATA_ID, 0));
      assertEquals("892", windows.get(6).getValue(Constants.DATA_ID, 0));

      // test the last line in a window is included
      var singleWindow = windows.get(5);
      assertEquals("891", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
   }

   @Test
   public void testSpliceHoppingWindow() {
      final double WINDOW_SIZE = 3;
      final double HOP_SIZE = 2;
      List<DataEntry> windows = Windows.spliceHoppingWindow(GAZE_DATA, WINDOW_SIZE, HOP_SIZE);
      assertEquals(4, windows.size());

      var singleWindow = windows.get(0);
      assertEquals("0", singleWindow.getValue(Constants.DATA_ID, 0));
      assertEquals("445", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
      singleWindow = windows.get(1);
      assertEquals("297", singleWindow.getValue(Constants.DATA_ID, 0));
      assertEquals("743", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
      singleWindow = windows.get(2);
      assertEquals("596", singleWindow.getValue(Constants.DATA_ID, 0));
      assertEquals("970", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
      singleWindow = windows.get(3);
      assertEquals("892", singleWindow.getValue(Constants.DATA_ID, 0));
      assertEquals("970", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
   }

   @Test
   public void testSpliceExpandingWindow() {
      final double WINDOW_SIZE = 3;
      List<DataEntry> windows = Windows.spliceExpandingWindow(GAZE_DATA, WINDOW_SIZE);
      assertEquals(3, windows.size());
      
      var singleWindow = windows.get(0);
      assertEquals("0", singleWindow.getValue(Constants.DATA_ID, 0));
      assertEquals("445", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
      singleWindow = windows.get(1);
      assertEquals("0", singleWindow.getValue(Constants.DATA_ID, 0));
      assertEquals("891", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
      singleWindow = windows.get(2);
      assertEquals("0", singleWindow.getValue(Constants.DATA_ID, 0));
      assertEquals("970", singleWindow.getValue(Constants.DATA_ID, singleWindow.rowCount()-1));
   }

   @Test
   public void testSpliceEventWindow_LPMM() {
      WindowSettings settings = new WindowSettings();
      settings.eventEnabled = true;
      settings.event = Constants.LEFT_PUPIL_DIAMETER;
      settings.eventTimeout = 1;
      settings.eventMaxDuration = 3;
      double baseline = 4.523560729927;

      List<DataEntry> windows = Windows.spliceEventWindow(GAZE_DATA, settings, baseline);
      assertTrue(windows.size() == 3);
      assertTrue(windows.get(0).getValue(Constants.DATA_ID, 0).equals("6"));
      assertTrue(windows.get(1).getValue(Constants.DATA_ID, 0).equals("453"));
      assertTrue(windows.get(2).getValue(Constants.DATA_ID, 0).equals("901"));
   }

   @Test
   public void testGetBaselineValue_LPMM() {
      double baselineValue = Windows.getEventBaselineValue(DataFilter.filterByValidity(GAZE_DATA), Constants.LEFT_PUPIL_DIAMETER);
      assertEquals(4.523560729927, baselineValue, PRECISION);
   }

   @Test
   public void testGetBaselineValue_BothPMM() {
      final String EVENT = Constants.LEFT_PUPIL_DIAMETER + " + " + Constants.RIGHT_PUPIL_DIAMETER;
      double baselineValue = Windows.getEventBaselineValue(DataFilter.filterByValidity(GAZE_DATA), EVENT);
      assertEquals(4.456128552311, baselineValue, PRECISION);
   }
}
