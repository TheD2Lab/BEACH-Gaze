package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import analysis.Angles;
import analysis.Coordinate;;

public class AnglesTest {
   
   final ArrayList<Coordinate> TEST_COORDINATES = new ArrayList<>() {{
      add(new Coordinate(0.515, 0.33556, 1));
      add(new Coordinate(0.55532, 0.37966, 2));
      add(new Coordinate(0.60302, 0.47807, 4));
      add(new Coordinate(0.5417, 0.50501, 5));
      add(new Coordinate(0.6017, 0.49948, 6));
      add(new Coordinate(0.59532, 0.48402, 7));
      add(new Coordinate(0.51246, 0.50446, 8));
      add(new Coordinate(0.48937, 0.55112, 9));
      add(new Coordinate(0.43229, 0.57458, 10));
      add(new Coordinate(0., 0.57458, 13));
      add(new Coordinate(0., 0.57458, 14));
      add(new Coordinate(0., 0.57458, 15));
      add(new Coordinate(0., 0, 20));
      add(new Coordinate(0., 3, 21));
      add(new Coordinate(4., 0, 22));
      add(new Coordinate(0., 0, 100));
      add(new Coordinate(0., -3, 101));
      add(new Coordinate(4., 0, 102));
      add(new Coordinate(0., 1, 200));
      add(new Coordinate(1., 1, 201));
      add(new Coordinate(4., 1, 202));
   }};

   final double PRECISION = 0.0000001;

   @Test
   public void testGetAbsoluteAngles() {
      final double[] EXPECTED_ABS_ANGLES = {
         47.56377021465,
         23.71754762183,
         5.26588394708,
         67.57514339122,
         13.85713368003,
         63.67119844326,
         22.34276539021,
         90.0,
         90.0,
         90.0,
         36.86989764584,
         90.0,
         36.86989764584,
         0,
         0
      };

      ArrayList<Double> actual = Angles.getAllAbsoluteAngles(TEST_COORDINATES);
      assertEquals(
         "Different number of absolute angles returned",
         EXPECTED_ABS_ANGLES.length,
         actual.size()
      );

      for (int i = 0; i < actual.size(); i++) {
         if(Math.abs(EXPECTED_ABS_ANGLES[i] - actual.get(i)) > PRECISION) {
            fail(String.format(
               "Incorrect absolute angle at index %d\n\tExpected: %f\n\tActual: %f",
               i,
               EXPECTED_ABS_ANGLES[i],
               actual.get(i))
            );
         }
      }
   }

   @Test
   public void testGetAllRelativeAngles() {
      final double[] EXPECTED_REL_ANGLES = {
         151.0165684310730,
         107.1589726616900,
         98.5677229287483,
         102.4716678767030,
         93.9860361665230,
         0.0,
         126.869897645844,
         53.130102354156,
         180
      };
      ArrayList<Double> actual = Angles.getAllRelativeAngles(TEST_COORDINATES);
      assertEquals(
         "Different number of relative angles returned",
         EXPECTED_REL_ANGLES.length,
         actual.size()
      );

      for (int i = 0; i < actual.size(); i++) {
         if(Math.abs(EXPECTED_REL_ANGLES[i] - actual.get(i)) > PRECISION) {
            fail(String.format(
               "Incorrect relative angle at index %d\n\tExpected: %f\n\tActual: %f",
               i,
               EXPECTED_REL_ANGLES[i],
               actual.get(i))
            );
         }
      }
   }

}