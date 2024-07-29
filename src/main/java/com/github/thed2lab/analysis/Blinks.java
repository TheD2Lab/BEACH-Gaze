package com.github.thed2lab.analysis;

import java.util.LinkedHashMap;

public class Blinks {
   
   final static String BLINK_ID_INDEX = "BKID";
   final static String TIME_INDEX = "TIME";
   final static String DATA_ID_INDEX = "CNT"; // unique for each line of raw data
   final static String DEFAULT_BKID = "0"; // BKID when no blink is detected

   static public LinkedHashMap<String,String> analyze(DataEntry allGazeData) {

      LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
      int blinkCnt = 0;
      double timeTotal = 0;
      String prevBlinkId = "";
      double prevTimestamp = 0;
      int prevDataId = -10; // IDs are always non-negative
      for (int i = 0; i < allGazeData.rowCount(); i++) {
         int curDataId = Integer.parseInt(allGazeData.getValue(DATA_ID_INDEX, i));
         double curTimestamp = Double.parseDouble(allGazeData.getValue(TIME_INDEX, i));
         // calculate time window between data records if they are consecutive
         if (curDataId == prevDataId + 1) {                    
            timeTotal += curTimestamp - prevTimestamp; 
         }

         String curBlinkId = allGazeData.getValue(BLINK_ID_INDEX, i);
         if (!curBlinkId.equals(DEFAULT_BKID) && !curBlinkId.equals(prevBlinkId)) {
            blinkCnt++; // new blink occurred
         }

         // update values each loop
         prevDataId = curDataId;
         prevTimestamp = curTimestamp;
         prevBlinkId = curBlinkId;
      }

      /*
         Note: Blink rate can be NaN while blinkCnt can be a non-zero number
         due to the time window calculations. This is unlikely at 150 Hz because
         blinks last 0.1-0.4 seconds and a sample is taken every 0.006 seconds. 
       */
      double blinkRate = timeTotal > 0 ? (blinkCnt / timeTotal) * 60 : Double.NaN;

      results.put(
         "total_number_of_blinks",
         String.valueOf(blinkCnt)
      );

      results.put(
         "average_blink_rate_per_minute", //Output Header
         String.valueOf(blinkRate)
      );  

      return results;
   }
}
