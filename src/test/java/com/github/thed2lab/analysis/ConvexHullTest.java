package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class ConvexHullTest {
   
   private final double PRECISION = 0.000000001; // allowable floating point error

   @Test
   public void testGetConvexHull_lessThan3() {
      List<Point2D.Double> points = List.of(
         new Point2D.Double(0, 0),
         new Point2D.Double(1,1)
      );

      int convexHullSize = ConvexHull.getConvexHull(points).size();
      assertEquals("Expected empty list, but got non-empty list instead.", 0, convexHullSize);
   }

   @Test
   public void testGetConvexHull_allColinear() {
      List<Point2D.Double> points = List.of(
         new Point2D.Double(0, 0),
         new Point2D.Double(0,1),
         new Point2D.Double(0, 2),
         new Point2D.Double(0,3),
         new Point2D.Double(0, 4),
         new Point2D.Double(0,5)
      );

      int convexHullSize = ConvexHull.getConvexHull(points).size();
      assertEquals("Expected empty list, but got non-empty list instead.", 0, convexHullSize);
   }

   @Test
   public void testGetPolygonArea_normalCase() {
      List<Point2D.Double> convexHull = List.of(
         new Point2D.Double(1, -1),
         new Point2D.Double(3,0),
         new Point2D.Double(1,4),
         new Point2D.Double(-2,2),
         new Point2D.Double(1, -1)
      );
      final double EXPECTED_AREA = 12.5;
      final double ACTUAL_AREA = ConvexHull.getPolygonArea(convexHull);
      assertEquals(EXPECTED_AREA, ACTUAL_AREA, PRECISION);
   }

   @Test
   public void testGetConvexHull_removeClockwise() {
      List<Point2D.Double> points = List.of(
         new Point2D.Double(1, 1), // removed; clockwise
         new Point2D.Double(1,4),
         new Point2D.Double(1, 4),
         new Point2D.Double(3,0),
         new Point2D.Double(1, -1),
         new Point2D.Double(-2,2)
      );
      List<Point2D.Double> expectedConvexHull = List.of(
         new Point2D.Double(1, -1),
         new Point2D.Double(3,0),
         new Point2D.Double(1,4),
         new Point2D.Double(-2,2),
         new Point2D.Double(1, -1)
      );
      List<Point2D.Double> actualConvexHull = ConvexHull.getConvexHull(points);

      assertEquals(expectedConvexHull.size(), actualConvexHull.size());

      Iterator<Point2D.Double> expectedIter = expectedConvexHull.iterator();

      for(Point2D.Double actualPoint: actualConvexHull) {
         Point2D.Double expectedPoint = expectedIter.next();
         if (!expectedPoint.equals(actualPoint)) {
            fail("Actual convex hull does not match expected.");
         }
      }
   }

   @Test
   public void testGetConvexHull_removeCollinear() {
      List<Point2D.Double> points = List.of(
         new Point2D.Double(1,4),
         new Point2D.Double(1, 4),
         new Point2D.Double(3,0),
         new Point2D.Double(1, -1),
         new Point2D.Double(-2,2),
         new Point2D.Double(0,0) // removed; collinear
      );
      List<Point2D.Double> expectedConvexHull = List.of(
         new Point2D.Double(1, -1),
         new Point2D.Double(3,0),
         new Point2D.Double(1,4),
         new Point2D.Double(-2,2),
         new Point2D.Double(1, -1)
      );
      List<Point2D.Double> actualConvexHull = ConvexHull.getConvexHull(points);

      assertEquals(expectedConvexHull.size(), actualConvexHull.size());

      Iterator<Point2D.Double> expectedIter = expectedConvexHull.iterator();

      for (Point2D.Double actualPoint: actualConvexHull) {
         Point2D.Double expectedPoint = expectedIter.next();
         if (!expectedPoint.equals(actualPoint)) {
            fail("Actual convex hull does not match expected.");
         }
      }
   }

}
