package com.github.thed2lab.analysis;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.filechooser.FileNameExtensionFilter;


public class UserInterface {

    File[] analyticsInputFiles;
    String analyticsOutputDirectory;

    File[] datasetFiles;
    String predictionsOutputDirectory;
    boolean isClassification;

    WindowSettings windowSettings;

    private JPanel analysisPanel;
    private JPanel helpPanel;
    private JPanel predictionsPanel;

    // UI Components for analysis panel
    private BufferedImage tumblingWindowImage;
    private BufferedImage hoppingWindowImage;
    private BufferedImage eventWindowImage;
    private BufferedImage expandingWindowImage;
    private JComboBox<String> eventComboBox;
    private JFrame frame;
    private JTabbedPane tabs;
    private JButton selectFilesButton;
    private JLabel fileCountLabel;
    private JButton analyticsBrowseDirectoryButton;
    private JTextField analyticsDirectoryField;
    private JButton runAnalysisButton;
    private JCheckBox tumblingCheckBox;
    private JTextField tumblingWindowSizeField;
    private JCheckBox expandingCheckBox;
    private JTextField expandingWindowSizeField;
    private JCheckBox hoppingCheckBox;
    private JTextField hoppingWindowSizeField;
    private JTextField hoppingHopSizeField;
    private JCheckBox eventCheckBox;
    private JTextField eventTimeoutField;
    private JTextField eventMaxDurationField;
    private JTextField eventBaselineDurationField;

    // UI Components for predictions panel
    private JButton selectDatasetButton;
    private JLabel dataFilesCountLabel;
    private JButton predictionsBrowseDirectoryButton;
    private JTextField predictionsDirectoryField;
    private JCheckBox classificationCheckBox;
    private JCheckBox regressionCheckBox;
    private JButton runPredictionsButton;

    public UserInterface() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            String imageFilePath = System.getProperty("user.dir") + "/img/";
            
            tumblingWindowImage = ImageIO.read(new File(imageFilePath + "tumblingWindow.jpg"));
            hoppingWindowImage = ImageIO.read(new File(imageFilePath + "hoppingWindow.jpg"));
            eventWindowImage = ImageIO.read(new File(imageFilePath + "eventBasedWindow.jpg"));
            expandingWindowImage = ImageIO.read(new File(imageFilePath + "expandingWindow.jpg"));
        } catch (Exception e) {
            System.err.println(e);
        }

        // Default values
        analyticsInputFiles = new File[0];
        datasetFiles = new File[0];
        
        String defaultDirectory = new File(System.getProperty("user.dir")).getParent();
        analyticsOutputDirectory = defaultDirectory;
        predictionsOutputDirectory = defaultDirectory;

        isClassification = true;

        windowSettings = new WindowSettings();

        buildFrame();
        buildTabPanels();
        setEventHandlers();

        frame.add(tabs);
        frame.setVisible(true);
    }

    private void buildFrame() {
        frame = new JFrame("BEACH-Gaze");
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildGazePanel() {
        // Panel contains all components pertaining to gaze settings
        JPanel gazePanel = new JPanel();
        //gazePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        gazePanel.setLayout(new GridBagLayout());

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.insets = new Insets(0, 0, 0, 150);
        panelGBC.gridx = 0;
        panelGBC.gridy = 0;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 1;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        GridBagConstraints componentGBC = new GridBagConstraints();
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 5;
        componentGBC.ipady = 5;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;

        JLabel gazeLabel = new JLabel("Participant Settings");
        gazeLabel.setFont(gazeLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 120);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 8;
        gazePanel.add(gazeLabel, componentGBC);

        selectFilesButton = new JButton("Select File(s)");
        selectFilesButton.setToolTipText("Select all_gaze.csv files only");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 3;
        gazePanel.add(selectFilesButton, componentGBC);

        fileCountLabel = new JLabel(analyticsInputFiles.length + " files selected");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 2;
        gazePanel.add(fileCountLabel, componentGBC);

        JLabel directoryLabel = new JLabel("Output Directory");
        componentGBC.insets = new Insets(10, 0, 0, 0);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 3;
        gazePanel.add(directoryLabel, componentGBC);

        analyticsBrowseDirectoryButton = new JButton("Browse");
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 2;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 10;
        gazePanel.add(analyticsBrowseDirectoryButton, componentGBC);

        analyticsDirectoryField = new JTextField(analyticsOutputDirectory, 20);
        analyticsDirectoryField.setEditable(false);
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 2;
        gazePanel.add(analyticsDirectoryField, componentGBC);

        analysisPanel.add(gazePanel, panelGBC);
    }

    private void buildWindowsPanel() {
        JPanel windowsPanel = new JPanel();
        windowsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        windowsPanel.setLayout(new GridBagLayout());

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = 1;
        panelGBC.gridy = 0;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 2;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        GridBagConstraints componentGBC = new GridBagConstraints();
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 10;
        componentGBC.ipady = 10;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;

        JLabel windowsLabel = new JLabel("Window Settings");
        windowsLabel.setFont(windowsLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 200);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 10;
        windowsPanel.add(windowsLabel, componentGBC);
        
        tumblingCheckBox = new JCheckBox("Tumbling");
        String tumblingToolTip = "A scheduled digest view of the gaze data using a tumbling window that is non-overlapping and fixed in size";
        tumblingCheckBox.setToolTipText("<html><p width=\"500\">" + tumblingToolTip +"</p></html>");
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 3;
        windowsPanel.add(tumblingCheckBox, componentGBC);

        JLabel windowSizeLabel1 = new JLabel("Window Size (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 1;
        windowsPanel.add(windowSizeLabel1, componentGBC);

        tumblingWindowSizeField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 1;
        windowsPanel.add(tumblingWindowSizeField, componentGBC);
        
        expandingCheckBox = new JCheckBox("Expanding");
        String expandingToolTip = "A cumulative view of the gaze data using an expanding window that is non-overlapping and non-fixed in size.";
        expandingCheckBox.setToolTipText("<html><p width=\"500\">" + expandingToolTip + "</p></html>");
        componentGBC.gridx = 0;
        componentGBC.gridy = 3;
        componentGBC.gridwidth = 3;
        windowsPanel.add(expandingCheckBox, componentGBC);

        JLabel windowSizeLabel2 = new JLabel("Window Size (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        windowsPanel.add(windowSizeLabel2, componentGBC);

        expandingWindowSizeField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        windowsPanel.add(expandingWindowSizeField, componentGBC);

        hoppingCheckBox = new JCheckBox("Hopping");
        String hoppingToolTip = "The most recent snapshot view of the gaze data using a hopping window that is overlapping and fixed in size";
        hoppingCheckBox.setToolTipText("<html><p width=\"500\">" + hoppingToolTip + "</p></html>");
        componentGBC.gridx = 0;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 3;
        windowsPanel.add(hoppingCheckBox, componentGBC);

        JLabel windowSizeLabel3 = new JLabel("Window Size (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 6;
        componentGBC.gridwidth = 1;
        windowsPanel.add(windowSizeLabel3, componentGBC);
        
        hoppingWindowSizeField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 6;
        componentGBC.gridwidth = 1;
        windowsPanel.add(hoppingWindowSizeField, componentGBC);

        JLabel hopSizeLabel = new JLabel("Hop Size (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowsPanel.add(hopSizeLabel, componentGBC);

        hoppingHopSizeField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowsPanel.add(hoppingHopSizeField, componentGBC);

        eventCheckBox = new JCheckBox("Event-Based");
        String eventToolTip = "An event-based view of the gaze data using a session window that is non-overlapping and non-fixed in size";
        eventCheckBox.setToolTipText("<html><p width=\"500\">" + eventToolTip + "</p></html>");
        componentGBC.gridx = 0;
        componentGBC.gridy = 8;
        componentGBC.gridwidth = 3;
        windowsPanel.add(eventCheckBox, componentGBC);

        JLabel timeoutLabel = new JLabel("Timeout Length (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 9;
        componentGBC.gridwidth = 1;
        windowsPanel.add(timeoutLabel, componentGBC);

        eventTimeoutField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 9;
        componentGBC.gridwidth = 1;
        windowsPanel.add(eventTimeoutField, componentGBC);

        JLabel maxDurationLabel = new JLabel("Max Duration (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 10;
        componentGBC.gridwidth = 1;
        windowsPanel.add(maxDurationLabel, componentGBC);

        eventMaxDurationField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 10;
        componentGBC.gridwidth = 1;
        windowsPanel.add(eventMaxDurationField, componentGBC);

        JLabel eventLabel = new JLabel("Event");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 11;
        componentGBC.gridwidth = 1;
        windowsPanel.add(eventLabel, componentGBC);

        Set<String> itemSet = new HashSet<String>();
        itemSet.addAll(Windows.fixationEvents);
        itemSet.addAll(Windows.allGazeEvents);

        eventComboBox = new JComboBox<String>(itemSet.toArray(new String[itemSet.size()]));
        windowSettings.event = (String) eventComboBox.getSelectedItem();
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 11;
        componentGBC.gridwidth = 3;
        windowsPanel.add(eventComboBox, componentGBC);

        JLabel baselineDurationLabel = new JLabel("Baseline Duration (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 12;
        componentGBC.gridwidth = 1;
        windowsPanel.add(baselineDurationLabel, componentGBC);

        eventBaselineDurationField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 12;
        componentGBC.gridwidth = 1;
        windowsPanel.add(eventBaselineDurationField, componentGBC);

        analysisPanel.add(windowsPanel, panelGBC);
    }

    private void buildConsolePanel() {
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new GridBagLayout());

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.LAST_LINE_START;
        panelGBC.insets = new Insets(20, 0, 35, 0);
        panelGBC.gridx = 0;
        panelGBC.gridy = 2;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 1;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        GridBagConstraints componentGBC = new GridBagConstraints();
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 10;
        componentGBC.ipady = 10;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        
        runAnalysisButton = new JButton("Run Analysis");
        runAnalysisButton.setFont(runAnalysisButton.getFont().deriveFont(20f));
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        consolePanel.add(runAnalysisButton, componentGBC);

        analysisPanel.add(consolePanel, panelGBC);
    }

    private void buildHelpPagePanel() {
        JPanel windowsHelpPanel = new JPanel();
        windowsHelpPanel.setLayout(new GridBagLayout());

        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = 1;
        panelGBC.gridy = 0;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 1;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        GridBagConstraints componentGBC = new GridBagConstraints();
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 10;
        componentGBC.ipady = 10;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;

        JLabel windowsLabel = new JLabel("Understanding Windows");
        windowsLabel.setFont(windowsLabel.getFont().deriveFont(Font.BOLD, 16f));
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(windowsLabel, componentGBC);


        String windowsAbstract = "<html><p width=\"750\">" +
        "To further support predictive gaze analytics, we offer additional, optional approaches to analyzing gaze data with the use of discrete-timed windows. " +
        "This approach to predictive gaze analytics focuses on learning from digests of user gaze (tumbling window), snapshots of the most recent user gaze (hopping window), " +
        "gaze captured during significant events (event-based window), as well as all known gaze to date (expanding window). Note: in this interface, we make use of" +
        "JTextFields. To update state values, you MUST press ENTER after typing a value: for example, when using tumbling windows you must input a window size and " + 
        "that window size will ONLY register after you have pressed ENTER." +
        "</p></html>";
        JLabel windowsDescriptionLabel = new JLabel(windowsAbstract);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = GridBagConstraints.REMAINDER;
        componentGBC.gridheight = 3;
        windowsHelpPanel.add(windowsDescriptionLabel, componentGBC);

        JLabel tumblingLabel = new JLabel("Tumbling Window");
        tumblingLabel.setFont(tumblingLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(10, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        componentGBC.ipady = 2;
        windowsHelpPanel.add(tumblingLabel, componentGBC);

        String tumblingDescription = "<html><p width=\"700\">" +
        "Tumbling windows are used to generate scheduled digests of user gaze. The window size parameter determines the fixed length of each window." + 
        "For example, if a window size of 20 seconds is provided, the participant's gaze data will be bisected into increments of 20, with the first window ranging from 0-20 seconds, the 2nd window 20-40 seconds, and so forth." +
        "</p></html>";
        JLabel tumblingDescriptionLabel = new JLabel(tumblingDescription);
        componentGBC.insets = new Insets(0, 0, 10, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(tumblingDescriptionLabel, componentGBC);

        JLabel tumblingImageLabel = new JLabel(new ImageIcon(tumblingWindowImage));
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 6;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(tumblingImageLabel, componentGBC);

        JLabel hoppingLabel = new JLabel("Hopping Window");
        hoppingLabel.setFont(hoppingLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(hoppingLabel, componentGBC);

        String hoppingDescription = "<html><p width=\"700\">" +
        "Hopping windows are used to analyze recent gaze snapshots. It can be argued that taking a digest view on user gaze can ignore potential relations and dependencies " +
        "between chunks of data; as such, this approach aims to capture the most recent state of user attention. Two parameters are taken; window size and hop size. " +
        "Window size dictates the fixed length of the window, and hop size describes the size of the next scheduled hop relative to the previous window." +
        "For example, a window size of 120 seconds and a hop size of 60 seconds means that each window has a length 120 seconds, and each window will have a starting time " +
        "60 seconds later relative to the previous window. The 1st window will be from time intervals 0-120, the 2nd window will be from 60-180, the 3rd 120-240, and so forth." +
        "</p></html>";
        JLabel hoppingDescriptionLabel = new JLabel(hoppingDescription);
        componentGBC.insets = new Insets(0, 0, 10, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 8;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(hoppingDescriptionLabel, componentGBC);

        JLabel hoppingImageLabel = new JLabel(new ImageIcon(hoppingWindowImage));
        componentGBC.gridx = 0;
        componentGBC.gridy = 9;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(hoppingImageLabel, componentGBC);


        JLabel eventLabel = new JLabel("Event-Based Window");
        eventLabel.setFont(eventLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 10;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(eventLabel, componentGBC);

        String eventDescription = "<html><p width=\"700\">" +
        "During interactions with the presented stimuli, users may experience defining moments that are impactful enough to affect their performance. These may translate " +
        "to notable gaze behaviors, which are likely to be indicative of a user's performance. Events in this software are defined as an analytic that exceeds its baseline value (average value generated from the first two minutes of a participant's data) " +
        "Specifically, a session window begins when the first event is found, to which it then keeps searching for the next event within a specified time period. If nothing is found, the event window closes out after the timeout period; if an " +
        "event is found, the timeout period is renewed. Two parameters are needed: the event-defining analytic and the timeout length. As an example: if the fixation duration is chosen as the analytic and " +
        "a timeout period of 20 seconds is chosen, whenever the user's fixation duration exceeds the baseline value, a window session will be created until 20 seconds have elapsed, or the user's fixation duration once again exceeds the baseline value, thereby prolonging the session by another 20 seconds." +
        "</p></html>";
        JLabel eventDescriptionLabel = new JLabel(eventDescription);
        componentGBC.insets = new Insets(0, 0, 10, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 11;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(eventDescriptionLabel, componentGBC);

        JLabel eventImageLabel = new JLabel(new ImageIcon(eventWindowImage));
        componentGBC.gridx = 0;
        componentGBC.gridy = 12;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(eventImageLabel, componentGBC);

        String eventOptionsDescription = "<html><p width=\"700\">" +
        "Currently, events supports 7 different options: SACCADE_MAG, which is the magnitude of the saccade calculated as distance between each fixation (in pixels); " +
        "SACCADE_DIR, which is the angle between each fixation in degrees from horizontal; " +
        "LPMM and RPMM, which is the diameter of the left and right eye pupil respectively in millimeters; " +
        "LPMM + RPMM, which is the average value of LPMM and RPMM; " +
        "FPOGD, the duration of the fixation point of gaze in seconds; " +
        "and lastly BKPMIN, the number of blinks in the previous 60 second period of time." +
        "</p></html>";
        JLabel eventOptionsLabel = new JLabel(eventOptionsDescription);
        componentGBC.gridx = 0;
        componentGBC.gridy = 13;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(eventOptionsLabel, componentGBC);

        JLabel expandingLabel = new JLabel("Expanding Window");
        expandingLabel.setFont(expandingLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 14;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(expandingLabel, componentGBC);

        
        String expandingDescription = 
        """
        <html><p width=\"700\"> 
            Expanding windows are used to analyze the cumulation of gaze data as the user's data continues to grow until the end of the task, allowing for an insight into
            cumulative gaze behavior. The window size defines the length of each window and the amount each subsequent window grows. Each nth window will be a of length n * window size.
            For example, if a window size of 20 seconds is given, the 1st window will be of length 20, the 2nd window will be of length 40, the 3rd 60, and so on.
        </p></html>
        """;
        JLabel expandingDescriptionLabel = new JLabel(expandingDescription);
        componentGBC.insets = new Insets(0, 0, 10, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 15;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(expandingDescriptionLabel, componentGBC);

        JLabel expandingImageLabel = new JLabel(new ImageIcon(expandingWindowImage));
        componentGBC.gridx = 0;
        componentGBC.gridy = 16;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(expandingImageLabel, componentGBC);

        helpPanel.add(windowsHelpPanel, panelGBC);
    }

    private void buildPredictionsPanel() {
        // Create panel for dataset settings
        JPanel dataSettingsPanel = new JPanel();
        dataSettingsPanel.setLayout(new GridBagLayout());

        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.insets = new Insets(0, 0, 0, 150);
        panelGBC.gridx = 0;
        panelGBC.gridy = 0;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 1;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        GridBagConstraints componentGBC = new GridBagConstraints();
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 5;
        componentGBC.ipady = 5;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;

        JLabel dataLabel = new JLabel("Dataset Settings");
        dataLabel.setFont(dataLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 120);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 8;
        dataSettingsPanel.add(dataLabel, componentGBC);

        selectDatasetButton = new JButton("Select File(s)");
        selectDatasetButton.setToolTipText("Select training dataset files");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 3;
        dataSettingsPanel.add(selectDatasetButton, componentGBC);

        dataFilesCountLabel = new JLabel(datasetFiles.length + " files selected");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 2;
        dataSettingsPanel.add(dataFilesCountLabel, componentGBC);

        JLabel directoryLabel = new JLabel("Output Directory");
        componentGBC.insets = new Insets(10, 0, 0, 0);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 3;
        dataSettingsPanel.add(directoryLabel, componentGBC);

        predictionsBrowseDirectoryButton = new JButton("Browse");
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 2;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 10;
        dataSettingsPanel.add(predictionsBrowseDirectoryButton, componentGBC);

        predictionsDirectoryField = new JTextField(analyticsOutputDirectory, 20);
        predictionsDirectoryField.setEditable(false);
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 2;
        dataSettingsPanel.add(predictionsDirectoryField, componentGBC);

        predictionsPanel.add(dataSettingsPanel, panelGBC);

        // Create panel for classifier settings
        JPanel classifierSettingsPanel = new JPanel();
        classifierSettingsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        classifierSettingsPanel.setLayout(new GridBagLayout());

        panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = 1;
        panelGBC.gridy = 0;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 2;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        componentGBC = new GridBagConstraints();
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 5;
        componentGBC.ipady = 5;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;

        JLabel classificationLabel = new JLabel("Classifier Settings");
        classificationLabel.setFont(classificationLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 120);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 8;
        classifierSettingsPanel.add(classificationLabel, componentGBC);

        classificationCheckBox = new JCheckBox("Classification");
        classificationCheckBox.setSelected(isClassification);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        classifierSettingsPanel.add(classificationCheckBox, componentGBC);

        regressionCheckBox = new JCheckBox("Regression");
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        classifierSettingsPanel.add(regressionCheckBox, componentGBC);

        predictionsPanel.add(classifierSettingsPanel, panelGBC);

        // Create panel for predictions button
        JPanel experimentPanel = new JPanel();
        experimentPanel.setLayout(new GridBagLayout());

        panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.insets = new Insets(300, 0, 0, 0);
        panelGBC.gridx = 0;
        panelGBC.gridy = 1;
        panelGBC.gridwidth = 2;
        panelGBC.gridheight = 2;
        panelGBC.weightx = 2;
        panelGBC.weighty = 2;

        componentGBC = new GridBagConstraints();
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 10;
        componentGBC.ipady = 10;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;

        runPredictionsButton = new JButton("Run Predictions");
        runPredictionsButton.setFont(runPredictionsButton.getFont().deriveFont(20f));
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        experimentPanel.add(runPredictionsButton, componentGBC);

        predictionsPanel.add(experimentPanel, panelGBC);
    }

    private void setEventHandlers() {
        // Buttons
        selectFilesButton.addActionListener(e -> {
            selectAnalyticsInputFiles();
        });

        analyticsBrowseDirectoryButton.addActionListener(e -> {
            selectAnalyticsFileDirectory();
        });

        runAnalysisButton.addActionListener(e -> {
            runAnalysis();
        });

        selectDatasetButton.addActionListener(e -> {
            selectDatasetFiles();
        });

        predictionsBrowseDirectoryButton.addActionListener(e -> {
            selectPredictionsFileDirectory();
        });

        runPredictionsButton.addActionListener(e -> {
            runPredictions();
        });

        // Checkboxes
        tumblingCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) 
                windowSettings.tumblingEnabled = true;
            else 
                windowSettings.tumblingEnabled = false;
        });

        expandingCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) 
                windowSettings.expandingEnabled = true;
            else 
                windowSettings.expandingEnabled = false;
        });

        hoppingCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) 
                windowSettings.hoppingEnabled = true;
            else 
                windowSettings.hoppingEnabled = false;
        });

        eventCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) 
                windowSettings.eventEnabled = true;
            else 
                windowSettings.eventEnabled = false;
        });

        classificationCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                isClassification = true;
                regressionCheckBox.setSelected(false);
            } else {
                isClassification = false;
                regressionCheckBox.setSelected(true);
            }
        });

        regressionCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                isClassification = false;
                classificationCheckBox.setSelected(false);
            } else {
                isClassification = true;
                classificationCheckBox.setSelected(true);
            }
        });

        // Text fields
        tumblingWindowSizeField.addActionListener(e -> {
            String text = tumblingWindowSizeField.getText();
            if (isNumeric(text)) windowSettings.tumblingWindowSize = Double.parseDouble(text);
        });

        expandingWindowSizeField.addActionListener(e -> {
            String text = expandingWindowSizeField.getText();
            if (isNumeric(text)) windowSettings.expandingWindowSize = Double.parseDouble(text);
        });

        hoppingWindowSizeField.addActionListener(e -> {
            String text = hoppingWindowSizeField.getText();
            if (isNumeric(text)) windowSettings.hoppingWindowSize = Double.parseDouble(text);
        });

        hoppingHopSizeField.addActionListener(e -> {
            String text = hoppingHopSizeField.getText();
            if (isNumeric(text)) windowSettings.hoppingHopSize = Double.parseDouble(text);
        });

        eventTimeoutField.addActionListener(e -> {
            String text = eventTimeoutField.getText();
            if (isNumeric(text)) windowSettings.eventTimeout = Double.parseDouble(text);
        });

        eventMaxDurationField.addActionListener(e -> {
            String text = eventMaxDurationField.getText();
            if (isNumeric(text)) windowSettings.eventMaxDuration = Double.parseDouble(text);
        });

        eventBaselineDurationField.addActionListener(e -> {
            String text = eventBaselineDurationField.getText();
            if (isNumeric(text)) windowSettings.eventBaselineDuration = Double.parseDouble(text);
        });

        // Comboboxes
        eventComboBox.addItemListener(e -> {
            windowSettings.event = (String) eventComboBox.getSelectedItem();
        });
    }

    private void selectAnalyticsInputFiles() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(("CSV Files"), "csv");
        JFileChooser chooser = new JFileChooser();

        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            analyticsInputFiles = chooser.getSelectedFiles();
        }

        fileCountLabel.setText(analyticsInputFiles.length + " files selected");
    }

    private void selectAnalyticsFileDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            analyticsOutputDirectory = chooser.getSelectedFile().getAbsolutePath();
            analyticsDirectoryField.setText(analyticsOutputDirectory);
        }
    }

    private void selectDatasetFiles() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(("CSV Files"), "csv");
        JFileChooser chooser = new JFileChooser();

        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            datasetFiles = chooser.getSelectedFiles();
        }

        dataFilesCountLabel.setText(datasetFiles.length + " files selected");
    }

    private void selectPredictionsFileDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            predictionsOutputDirectory = chooser.getSelectedFile().getAbsolutePath();
            predictionsDirectoryField.setText(predictionsOutputDirectory);
        }
    }

    private void runAnalysis() {
        // Append a results folder to the output directory path
        String resultsDirectory = analyticsOutputDirectory + "/results";

        // Create a subfolder within the given output directory called "results" if one does not exist
        File f = new File(resultsDirectory);
        if (!f.exists()) {
            f.mkdirs();
        } else {
            int fileCount = 1;
            if (!resultsDirectory.contains ("results (")) resultsDirectory += (" (" + fileCount + ")");
            f = new File(resultsDirectory);
            while (f.exists()) {
                fileCount++;
                resultsDirectory = resultsDirectory.replace(String.valueOf(fileCount - 1), String.valueOf(fileCount));
                f = new File(resultsDirectory);
            }
        }

        Parameters params = new Parameters(analyticsInputFiles, resultsDirectory, windowSettings);
        Analysis analysis = new Analysis(params);
        analysis.run();
    }

    private void runPredictions() {
        String predictionsDirectory = predictionsOutputDirectory + "/predictions";

        // Create a subfolder within the given output directory called "predictions" if one does not exist
        File f = new File(predictionsDirectory);
        if (!f.exists()) {
            f.mkdirs();
        } else {
            int fileCount = 1;
            if (!predictionsDirectory.contains ("predictions (")) predictionsDirectory += (" (" + fileCount + ")");
            f = new File(predictionsDirectory);
            while (f.exists()) {
                fileCount++;
                predictionsDirectory = predictionsDirectory.replace(String.valueOf(fileCount - 1), String.valueOf(fileCount));
                f = new File(predictionsDirectory);
            }
        }

        try {
            WekaParameters params = new WekaParameters(datasetFiles, predictionsDirectory, isClassification);
            WekaExperiment experiment = new WekaExperiment(params);
            experiment.run();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void buildTabPanels() {
        tabs = new JTabbedPane();

        analysisPanel = new JPanel(new GridBagLayout());
        analysisPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buildGazePanel();
        buildWindowsPanel();
        buildConsolePanel();

        helpPanel = new JPanel(new GridBagLayout());
        helpPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buildHelpPagePanel();
        JScrollPane helpPanelScrollable = new JScrollPane(helpPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        predictionsPanel = new JPanel(new GridBagLayout());
        predictionsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buildPredictionsPanel();

        tabs.addTab("Descriptive Analytics", analysisPanel);
        tabs.addTab("Predictive Analytics", predictionsPanel);
        tabs.addTab("Help", helpPanelScrollable);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
