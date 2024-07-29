package com.github.thed2lab.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit Tests for the DataEntry class. Honestly, DataEntry's design is hard to test.
 */
public class DataEntryTest {
   final static List<String> GOOD_HEADERS = Collections.unmodifiableList(new ArrayList<>() {
      {
         add("CNT");
         add("TIME(2023/02/22 14:26:24.897)");
         add("FPOGID");
      }
   });
   final static List<String> BAD_HEADERS = Collections.unmodifiableList(new ArrayList<>() {
      {
         add("CNT");
         add("TIME(2023/02/22 14:26:24.897)");
      }
   });

   final static ArrayList<List<String>> GOOD_DATA = new ArrayList<>() {
      {
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("0");
               add("0");
               add("1");
            }
         }));
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("1");
               add("0.00696");
               add("1");
            }
         }));
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("2");
               add("0.01331");
               add("1");
            }
         }));
      }
   };

   final static ArrayList<List<String>> BAD_DATA = new ArrayList<>() {
      {
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("0");
               add("0");
               add("1");
            }
         }));
         add(Collections.unmodifiableList(new ArrayList<>() {
            {
               add("1");
            }
         }));
      }
   };

   final static String TEST_FILE_DIR = "./src/test/resources/";

   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   @Test
   public void testProcess_missingHeader() {
      DataEntry dEntry = new DataEntry(BAD_HEADERS);
      for (List<String> line : GOOD_DATA) {
         dEntry.process(line);
      }
   }

   @Test
   public void testWriteToCSV() throws IOException {
      DataEntry dEntry = new DataEntry(GOOD_HEADERS);
      for (List<String> line : GOOD_DATA) {
         dEntry.process(line);
      }
      String fileName = "/writeCSV.csv";
      String tempPath = tempFolder.getRoot().toPath().toString();
      File expectedFile = new File(TEST_FILE_DIR + fileName);
      dEntry.writeToCSV(tempPath, fileName);
      File actualFile = new File(tempPath + fileName);
      assertTrue("writeCSV.csv contents differ", FileUtils.contentEquals(expectedFile, actualFile));
   }

   @Test
   public void testGetValue() {
      DataEntry dEntry = new DataEntry(GOOD_HEADERS);
      for (List<String> line : GOOD_DATA) {
         dEntry.process(line);
      }
      String header = GOOD_HEADERS.getFirst();
      String actualValue = dEntry.getValue(header, 1);
      assertTrue("Data Entry values do not match!","1".equals(actualValue));
   }

   // Leaving this test here to point out that DataEntry relies that the the data passed in
   // does not have missing values.
   @Test(expected = IndexOutOfBoundsException.class)
   public void testGetValue_missingValue_throwsIndexOutOfBounds() {
      DataEntry dEntry = new DataEntry(GOOD_HEADERS);
      for (List<String> line : BAD_DATA) {
         dEntry.process(line);
      }
      String header = GOOD_HEADERS.getLast();
      dEntry.getValue(header, 2);
   }

   @Test
   public void testCounts() {
      DataEntry dEntry = new DataEntry(GOOD_HEADERS);
      for (List<String> line : GOOD_DATA) {
         dEntry.process(line);
      }
      assertEquals(3, dEntry.rowCount());
      assertEquals(3, dEntry.columnCount());
   }
}
