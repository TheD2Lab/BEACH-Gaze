package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import analysis.ConvexHull;

public class ConvexHullTest {
   
   final double PRECISION = 0.000000001; // allowable floating point error

   @Test
   public void testGetConvexHull_lessThan3() {
      final List<Point2D.Double> POINTS = new ArrayList<>() {{
         add(new Point2D.Double(0, 0));
         add(new Point2D.Double(1,1));
      }};

      int convexHullSize = ConvexHull.getConvexHull(POINTS).size();
      assertEquals("Expected empty list, but got non-empty list instead.", 0, convexHullSize);
   }

   @Test
   public void testGetConvexHull_allColinear() {
      final List<Point2D.Double> POINTS = new ArrayList<>() {{
         add(new Point2D.Double(0, 0));
         add(new Point2D.Double(0,1));
         add(new Point2D.Double(0, 2));
         add(new Point2D.Double(0,3));
         add(new Point2D.Double(0, 4));
         add(new Point2D.Double(0,5));
      }};

      int convexHullSize = ConvexHull.getConvexHull(POINTS).size();
      assertEquals("Expected empty list, but got non-empty list instead.", 0, convexHullSize);
   }

   @Test
   public void testGetPolygonArea_normalCase() {
      final List<Point2D.Double> CONVEX_HULL = Collections.unmodifiableList(new ArrayList<>() {{
         add(new Point2D.Double(1, -1));
         add(new Point2D.Double(3,0));
         add(new Point2D.Double(1,4));
         add(new Point2D.Double(-2,2));
         add(new Point2D.Double(1, -1));
      }});
      final double EXPECTED_AREA = 12.5;
      final double ACTUAL_AREA = ConvexHull.getPolygonArea(CONVEX_HULL);
      assertEquals(EXPECTED_AREA, ACTUAL_AREA, PRECISION);
   }

   @Test
   public void testGetConvexHull_removeClockwise() {
      final List<Point2D.Double> POINTS = new ArrayList<>() {{
         add(new Point2D.Double(1, 1)); // removed; clockwise
         add(new Point2D.Double(1,4));
         add(new Point2D.Double(1, 4));
         add(new Point2D.Double(3,0));
         add(new Point2D.Double(1, -1));
         add(new Point2D.Double(-2,2));
      }};
      final List<Point2D.Double> EXPECTED_CONVEX_HULL = Collections.unmodifiableList(new ArrayList<>() {{
         add(new Point2D.Double(1, -1));
         add(new Point2D.Double(3,0));
         add(new Point2D.Double(1,4));
         add(new Point2D.Double(-2,2));
         add(new Point2D.Double(1, -1));
      }});
      final List<Point2D.Double> ACTUAL_CONVEX_HULL = ConvexHull.getConvexHull(POINTS);

      assertEquals(EXPECTED_CONVEX_HULL.size(), ACTUAL_CONVEX_HULL.size());

      Iterator<Point2D.Double> expectedIter = EXPECTED_CONVEX_HULL.iterator();

      for(Point2D.Double actualPoint: ACTUAL_CONVEX_HULL) {
         Point2D.Double expectedPoint = expectedIter.next();
         if (!expectedPoint.equals(actualPoint)) {
            fail("Actual convex hull does not match expected.");
         }
      }
   }

   @Test
   public void testGetConvexHull_removeCollinear() {
      final List<Point2D.Double> POINTS = new ArrayList<>() {{
         add(new Point2D.Double(1,4));
         add(new Point2D.Double(1, 4));
         add(new Point2D.Double(3,0));
         add(new Point2D.Double(1, -1));
         add(new Point2D.Double(-2,2));
         add(new Point2D.Double(0,0)); // removed; collinear
      }};
      final List<Point2D.Double> EXPECTED_CONVEX_HULL = Collections.unmodifiableList(new ArrayList<>() {{
         add(new Point2D.Double(1, -1));
         add(new Point2D.Double(3,0));
         add(new Point2D.Double(1,4));
         add(new Point2D.Double(-2,2));
         add(new Point2D.Double(1, -1));
      }});
      final List<Point2D.Double> ACTUAL_CONVEX_HULL = ConvexHull.getConvexHull(POINTS);

      assertEquals(EXPECTED_CONVEX_HULL.size(), ACTUAL_CONVEX_HULL.size());

      Iterator<Point2D.Double> expectedIter = EXPECTED_CONVEX_HULL.iterator();

      for (Point2D.Double actualPoint: ACTUAL_CONVEX_HULL) {
         Point2D.Double expectedPoint = expectedIter.next();
         if (!expectedPoint.equals(actualPoint)) {
            fail("Actual convex hull does not match expected.");
         }
      }
   }

}
