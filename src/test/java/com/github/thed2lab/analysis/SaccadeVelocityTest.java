package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SaccadeVelocityTest {

   private final double PRECISION = 0.000000001; // allowable floating point error
   private final static int SCREEN_WIDTH = 1920;
	private final static int SCREEN_HEIGHT = 1080;
   
   @Test
   public void testGetPeakVelocity_emptyPoints_returnNaN() {
      List<Double[]> emptyList = new ArrayList<>();
      boolean isNaN = Double.isNaN(SaccadeVelocity.getPeakVelocity(emptyList));
      assertTrue(isNaN);
   }

   @Test
   public void testGetPeakVelocity_onePoint_returnNaN() {
      List<Double[]> onePoint = new ArrayList<>() {{
         add(new Double[]{1., 1., 0.001});
      }};
      boolean isNaN = Double.isNaN(SaccadeVelocity.getPeakVelocity(onePoint));
      assertTrue(isNaN);
   }

   @Test
   public void testGetPeakVelocity_aboveThreshold_return0() {
      List<Double[]> saccadePoints = new ArrayList<>() {{
         add(new Double[]{0., 0., 0.});
         add(new Double[]{0., 10., 0.0001});
      }};
      // 2332.21914 > 700 => return 0
      boolean isZero = SaccadeVelocity.getPeakVelocity(saccadePoints) == 0;
      assertTrue(isZero);
   }

   @Test
   public void testGetPeakVelocity_normalUseCase_returnVelocityValue() {
      final double EXPECTED_VELOCITY = 78.3228634680;
      List<Double[]> saccadePoints = new ArrayList<>() {{
         add(new Double[]{1027.2576, 431.892, 0.70996});
         add(new Double[]{1014.1824, 412.5168, 0.71692});
         add(new Double[]{1008.096, 391.2408, 0.72363});
      }};

      double actualVelocity = SaccadeVelocity.getPeakVelocity(saccadePoints);
      assertEquals(EXPECTED_VELOCITY, actualVelocity, PRECISION);
   }

   @Test
   public void testSaccadeVelocityAnalyze_nonContinuousWholeScreen() {
      final String GAZE_PATH = "./src/test/resources/filtered_by_validity.csv";
      final String FIXATION_PATH = "./src/test/resources/valid_fixations.csv";
      final double EXPECTED_AVG_PEAK = 179.96919273273;
      final String KEY = "average_peak_saccade_velocity";
      DataEntry gazeData = DataFilter.applyScreenSize(FileHandler.buildDataEntry(new File(GAZE_PATH)), SCREEN_WIDTH, SCREEN_HEIGHT);
      DataEntry fixationData = DataFilter.applyScreenSize(FileHandler.buildDataEntry(new File(FIXATION_PATH)), SCREEN_WIDTH, SCREEN_HEIGHT);

      double actualAvgPeak = Double.parseDouble(SaccadeVelocity.analyze(gazeData, fixationData).get(KEY));
      assertEquals(EXPECTED_AVG_PEAK, actualAvgPeak, PRECISION);
   }

   @Test
   public void testSaccadeVelocityAnalyze_nonContinuousAoiA() {
      final String GAZE_PATH = "./src/test/resources/filtered_by_validity.csv";
      final String FIXATION_PATH = "./src/test/resources/aoi_a_fixation.csv";
      final double EXPECTED_AVG_PEAK = 133.30276061352;
      final String KEY = "average_peak_saccade_velocity";
      DataEntry gazeData = DataFilter.applyScreenSize(FileHandler.buildDataEntry(new File(GAZE_PATH)), SCREEN_WIDTH, SCREEN_HEIGHT);
      DataEntry fixationData = FileHandler.buildDataEntry(new File(FIXATION_PATH));
      double actualAvgPeak = Double.parseDouble(SaccadeVelocity.analyze(gazeData, fixationData).get(KEY));
      assertEquals(EXPECTED_AVG_PEAK, actualAvgPeak, PRECISION);
   }
}
