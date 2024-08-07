package com.github.thed2lab.analysis;

/**
 * Hardcoded constants that are used throughout the package so there are fewer duplicated
 * constants throughout the files.
 */
final class Constants {
   /*
    * Notice the access modifier is default (package-private).
    * We could make this an injectable if we wanted to be better OO programmers, but I think
    * this package is closely coupled enough that we don't care anymore.
    */
   
   private Constants() {
      // do not instantiate this class EVER!!!
   }

   /** Screen width in pixels */
   final static int SCREEN_WIDTH = 1920;
   /** Screen height in pixels */
	final static int SCREEN_HEIGHT = 1080;

   /** Header for timestamp of when the data line was recorded since the start of the recording in seconds */
   final static String TIMESTAMP = "TIME";
   /** Header for the unique ID given to each line of data */
   final static String DATA_ID = "CNT";

   /** Header for fixation ID. */
   final static String FIXATION_ID = "FPOGID";
   /** Header for the fixations starting timestamp */
   final static String FIXATION_START = "FPOGS";
   /** Header for fixation validity. 1 for true and 2 for false */
   final static String FIXATION_VALIDITY = "FPOGV";
   /** Header for the x-coordinate of the fixation point of gaze */
   final static String FIXATION_X = "FPOGX";
   /** Header for the y-coordinate of the fixation point of gaze */
   final static String FIXATION_Y = "FPOGY";
   /** Header for the duration of a fixation */
   final static String FIXATION_DURATION = "FPOGD";

   /** Header for the diameter of the left pupil in mm */
   final static String LEFT_PUPIL_DIAMETER = "LPMM";
   /** Header for the valid flag for the left pupil. A value of 1 is valid */
   final static String LEFT_PUPIL_VALIDITY = "LPMMV";
   /** Header for the diameter of the right pupil in mm */
   final static String RIGHT_PUPIL_DIAMETER = "RPMM";
   /** Header for the valid flag for the right pupil. A value of 1 is valid */
   final static String RIGHT_PUPIL_VALIDITY = "RPMMV";

   /** Header for cursor events */
   final static String CURSOR_EVENT = "CS";
   /** Header for the blink ID */
   final static String BLINK_ID = "BKID";
   /** Header for the blink rate per minute */
   final static String BLINK_RATE = "BKPMIN";
   /** Header for the AOI Label */
   final static String AOI_LABEL = "AOI";

   /** Header for the saccade magnitude */
   final static String SACCADE_MAGNITUDE = "SACCADE_MAG";
   /** Header for the saccade direction */
   final static String SACCADE_DIR = "SACCADE_DIR";

}
