package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SaccadeVelocityTest {

   final double PRECISION = 0.000000001; // allowable floating point error
   final static int SCREEN_WIDTH = 1920;
	final static int SCREEN_HEIGHT = 1080;
   
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
      
   }

   @Test
   public void testSaccadeVelocityAnalyze_nonContinuousEndsWithSaccadeData() {
      final String GAZE_PATH = "./src/test/resources/filtered_by_validity.csv";
      final double EXPECTED_AVG_PEAK = 169.15239533934;
      final String KEY = "average_peak_saccade_velocity";
      DataEntry gazeData = DataFilter.applyScreenSize(FileHandler.buildDataEntry(new File(GAZE_PATH)), SCREEN_WIDTH, SCREEN_HEIGHT);

      Map<String, String> actualMap = SaccadeVelocity.analyze(gazeData);
      assertEquals(1, actualMap.size());
      assertEquals(EXPECTED_AVG_PEAK, Double.parseDouble(actualMap.get(KEY)), PRECISION);

   }

   @Test
   public void testSaccadeVelocityAnalyze_nonContinuousEndsWithFixationData() {
      final String GAZE_PATH = "./src/test/resources/filtered_by_validity.csv";
      final double EXPECTED_AVG_PEAK = 169.15239533934;
      final String KEY = "average_peak_saccade_velocity";
      DataEntry gazeData = DataFilter.applyScreenSize(FileHandler.buildDataEntry(new File(GAZE_PATH)), SCREEN_WIDTH, SCREEN_HEIGHT);
      gazeData.process(Arrays.asList(
         "0","X-Plane","971","6.53005","5.88E+12","0.47815","0.45335",
         "6.53005","0.14856","25","1","0.46056","0.45713","1","0.60729",
         "0.47315","0"," ","0","","0.41066","0.36362","16.25439","1.08789",
         "1","0.61855","0.37037","15.69818","1.08789","1","0","0","11",
         "4.43149","1","4.03482","1","0","0","0","0","0","0","0","0","1",
         "1","1","1","1","1","1","0","0","0","AOI_A","0","0","0"
      ));

      Map<String, String> actualMap = SaccadeVelocity.analyze(gazeData);
      assertEquals(1, actualMap.size());
      assertEquals(EXPECTED_AVG_PEAK, Double.parseDouble(actualMap.get(KEY)), PRECISION);

   }
}
