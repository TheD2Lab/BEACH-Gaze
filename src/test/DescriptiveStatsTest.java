package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;

import analysis.DescriptiveStats;

/**
 * Unit Tests for the DescriptiveStats Class.
 */
public class DescriptiveStatsTest {

   final double PRECISION = 0.000000001; // allowable floating point error
   final ArrayList<Integer> INTEGERS = new ArrayList<>() {
      {
         add(5);
         add(6);
         add(7);
      }
   };
   final double INTEGERS_SUM = 18.0;
   final double INTEGERS_MEAN = 6.0;
   final double INTEGERS_MEDIAN = 6.0;
   final double INTEGERS_ST_DEV = 1.0;
   final double INTEGERS_MIN = 5.0;
   final double INTEGERS_MAX = 7.0;

   final Double[] DOUBLES = {5.5, 5.678, 0.014578, 101.0578972};
   final double DOUBLES_SUM = 112.2504752;
   final double DOUBLES_MEAN = 28.0626188;
   final double DOUBLES_MEDIAN = 5.589;
   final double DOUBLES_ST_DEV = 48.7344716716597;
   final double DOUBLES_MAX = 101.0578972;
   final double DOUBLES_MIN = 0.014578;

   @Test
   public void testGetSumOfIntegers() {
      assertEquals(INTEGERS_SUM, DescriptiveStats.getSumOfIntegers(INTEGERS), PRECISION);
      assertEquals(0.0, DescriptiveStats.getSumOfIntegers(new ArrayList<>()), 0);
   }

   @Test
   public void testGetSumOfDoubles() {
      ArrayList<Double> doubles = new ArrayList<>();
      Collections.addAll(doubles, DOUBLES);
      assertEquals(DOUBLES_SUM, DescriptiveStats.getSumOfDoubles(doubles), PRECISION);
      assertEquals(0.0, DescriptiveStats.getSumOfDoubles(new ArrayList<>()), 0);
   }

   @Test
   public void testGetSum() {
      assertEquals(DOUBLES_SUM, DescriptiveStats.getSum(DOUBLES), PRECISION);
      assertEquals(0.0, DescriptiveStats.getSum(new Double[0]), 0);
   }

   @Test
   public void testGetMeanOfIntegers() {
      assertEquals(INTEGERS_MEAN, DescriptiveStats.getMeanOfIntegers(INTEGERS), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMeanOfIntegers(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMeanOfDoubles() {
      ArrayList<Double> doubles = new ArrayList<>();
      Collections.addAll(doubles, DOUBLES);
      assertEquals(DOUBLES_MEAN, DescriptiveStats.getMeanOfDoubles(doubles), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMeanOfDoubles(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMedianOfIntegers() {
      assertEquals(INTEGERS_MEDIAN, DescriptiveStats.getMedianOfIntegers(INTEGERS), 0);
      ArrayList<Integer> twoMiddles = new ArrayList<>() {
         {
            add(5);
            add(6);
            add(7);
            add(8);
         }
      };
      double twoMidsMedian = 6.5;
      assertEquals(twoMidsMedian, DescriptiveStats.getMedianOfIntegers(twoMiddles), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMedianOfIntegers(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMedianOfDoubles() {
      ArrayList<Double> doubles = new ArrayList<>();
      Collections.addAll(doubles, DOUBLES);
      assertEquals(DOUBLES_MEDIAN, DescriptiveStats.getMedianOfDoubles(doubles), PRECISION);

      doubles.add(5.6);
      assertEquals(5.6, DescriptiveStats.getMedianOfDoubles(doubles), 0);
      assertEquals(Double.NaN, DescriptiveStats.getMedianOfDoubles(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMedian() {
      assertEquals(DOUBLES_MEDIAN, DescriptiveStats.getMedian(DOUBLES), PRECISION);

      Double[] oneMiddle = {1.9, 2.444, 3.5};
      assertEquals(2.444, DescriptiveStats.getMedian(oneMiddle), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMedian(new Double[0]), 0);
   }

   @Test
   public void testGetStDevOfIntegers() {
      assertEquals(INTEGERS_ST_DEV, DescriptiveStats.getStDevOfIntegers(INTEGERS), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getStDevOfIntegers(new ArrayList<>()), PRECISION);
   }

   @Test
   public void testGetStDevOfDoubles() {
      ArrayList<Double> doubles = new ArrayList<>();
      Collections.addAll(doubles, DOUBLES);
      assertEquals(DOUBLES_ST_DEV, DescriptiveStats.getStDevOfDoubles(doubles), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getStDevOfDoubles(new ArrayList<>()), 0);
   }

   @Test
   public void testGetStDev() {
      assertEquals(DOUBLES_ST_DEV, DescriptiveStats.getStDev(DOUBLES), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getStDev(new Double[0]), 0);
   }

   @Test
   public void testGetMinOfIntegers() {
      assertEquals(INTEGERS_MIN, DescriptiveStats.getMinOfIntegers(INTEGERS), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMinOfIntegers(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMinOfDoubles() {
      ArrayList<Double> doubles = new ArrayList<>();
      Collections.addAll(doubles, DOUBLES);
      assertEquals(DOUBLES_MIN, DescriptiveStats.getMinOfDoubles(doubles), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMinOfDoubles(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMin() {
      assertEquals(DOUBLES_MIN, DescriptiveStats.getMin(DOUBLES), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMin(new Double[0]), 0);
   }

   @Test
   public void testGetMaxOfIntegers() {
      assertEquals(INTEGERS_MAX, DescriptiveStats.getMaxOfIntegers(INTEGERS), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMaxOfIntegers(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMaxOfDoubles() {
      ArrayList<Double> doubles = new ArrayList<>();
      Collections.addAll(doubles, DOUBLES);
      assertEquals(DOUBLES_MAX, DescriptiveStats.getMaxOfDoubles(doubles), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMaxOfDoubles(new ArrayList<>()), 0);
   }

   @Test
   public void testGetMax() {
      assertEquals(DOUBLES_MAX, DescriptiveStats.getMax(DOUBLES), PRECISION);
      assertEquals(Double.NaN, DescriptiveStats.getMax(new Double[0]), 0);
   }
}
