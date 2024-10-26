/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.thed2lab.analysis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    // UI Components for help page
    private BufferedImage tumblingWindowImage;
    private BufferedImage hoppingWindowImage;
    private BufferedImage eventWindowImage;
    private BufferedImage expandingWindowImage;
    private  BufferedImage descriptiveAnalyticsImage;

    private File lastDirectory = new File(System.getProperty("user.dir"));

    private static final Font STANDARD_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 16);

    public UserInterface() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put("Button.font", STANDARD_FONT);
            defaults.put("Label.font", STANDARD_FONT);
            defaults.put("TabbedPane.font", STANDARD_FONT);

            String imageFilePath = System.getProperty("user.dir") + "/img/";

            tumblingWindowImage = ImageIO.read(new File(imageFilePath + "tumblingWindow.jpg"));
            hoppingWindowImage = ImageIO.read(new File(imageFilePath + "hoppingWindow.jpg"));
            eventWindowImage = ImageIO.read(new File(imageFilePath + "eventBasedWindow.jpg"));
            expandingWindowImage = ImageIO.read(new File(imageFilePath + "expandingWindow.jpg"));
            descriptiveAnalyticsImage = ImageIO.read(new File(imageFilePath + "ui-descriptive-analytics.png"));
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

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void buildFrame() {
        frame = new JFrame("BEACH-Gaze");
        frame.setSize(800, 600);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            String imageFilePath = System.getProperty("user.dir") + "/img/";
            frame.setIconImage(ImageIO.read(new File(imageFilePath + "d2logo.jpg")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildGazePanel() {
        // Panel contains all components pertaining to file selection
        JPanel gazePanel = new JPanel();
        gazePanel.setLayout(new GridBagLayout());
    
        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.insets = new Insets(0, 0, 0, 0);
        panelGBC.gridx = 0;
        panelGBC.gridy = 0; // Start at y = 0
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
        gazeLabel.setFont(BOLD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 2;
        gazePanel.add(gazeLabel, componentGBC);
    
        selectFilesButton = new JButton("Select File(s)");
        selectFilesButton.setToolTipText("Select all_gaze.csv files only");
        selectFilesButton.setFont(STANDARD_FONT);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 2;
        gazePanel.add(selectFilesButton, componentGBC);
    
        fileCountLabel = new JLabel(analyticsInputFiles.length + " files selected");
        fileCountLabel.setFont(STANDARD_FONT);
        componentGBC.gridx = 2;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 2;
        gazePanel.add(fileCountLabel, componentGBC);
    
        JLabel directoryLabel = new JLabel("Output Directory");
        directoryLabel.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(10, 0, 0, 0);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 3;
        gazePanel.add(directoryLabel, componentGBC);
    
        analyticsBrowseDirectoryButton = new JButton("Browse");
        analyticsBrowseDirectoryButton.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 3;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 2;
        gazePanel.add(analyticsBrowseDirectoryButton, componentGBC);
    
        analyticsDirectoryField = new JTextField(analyticsOutputDirectory, 20);
        analyticsDirectoryField.setFont(STANDARD_FONT);
        analyticsDirectoryField.setEditable(false);
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 3;
        gazePanel.add(analyticsDirectoryField, componentGBC);
    
        // Add gazePanel to the main analysis panel
        analysisPanel.add(gazePanel, panelGBC);
    }

    private void buildWindowsPanel() {
        // Adjust grid positioning for the Window Settings panel
        JPanel windowsPanel = new JPanel();
        windowsPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
        windowsPanel.setLayout(new GridBagLayout());
    
        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = 0;
        panelGBC.gridy = 1; // Set this to y = 1 so it comes below the file selection panel
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
    
        JLabel windowsLabel = new JLabel("Window Settings");
        windowsLabel.setFont(BOLD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 1;
        windowsPanel.add(windowsLabel, componentGBC);

        // Make two columns
        JPanel windowPanel1 = new JPanel();
        JPanel windowPanel2 = new JPanel();

        windowPanel1.setLayout(new GridBagLayout());
        windowPanel2.setLayout(new GridBagLayout());


        tumblingCheckBox = new JCheckBox("Tumbling");
        String tumblingToolTip = "A scheduled digest view of the gaze data using a tumbling window that is non-overlapping and fixed in size.";
        tumblingCheckBox.setToolTipText("<html><p width=\"500\">" + tumblingToolTip +"</p></html>");
        tumblingCheckBox.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 2;
        windowPanel1.add(tumblingCheckBox, componentGBC);

        JLabel windowSizeLabel1 = new JLabel("Window Size (s)");
        windowSizeLabel1.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 2;
        windowPanel1.add(windowSizeLabel1, componentGBC);

        tumblingWindowSizeField = new JTextField(windowSettings.tumblingWindowSize + "", 10); // Default value
        tumblingWindowSizeField.setToolTipText("Enter the window size in seconds for tumbling windows.");
        tumblingWindowSizeField.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 2;
        windowPanel1.add(tumblingWindowSizeField, componentGBC);

        expandingCheckBox = new JCheckBox("Expanding");
        String expandingToolTip = "A cumulative view of the gaze data using an expanding window that is non-overlapping and non-fixed in size.";
        expandingCheckBox.setToolTipText("<html><p width=\"500\">" + expandingToolTip + "</p></html>");
        expandingCheckBox.setFont(STANDARD_FONT);
        componentGBC.gridx = 0;
        componentGBC.gridy = 3;
        componentGBC.gridwidth = 2;
        windowPanel1.add(expandingCheckBox, componentGBC);

        JLabel windowSizeLabel2 = new JLabel("Window Size (s)");
        windowSizeLabel2.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        windowPanel1.add(windowSizeLabel2, componentGBC);

        expandingWindowSizeField = new JTextField(windowSettings.expandingWindowSize + "", 10); // Default value
        expandingWindowSizeField.setToolTipText("Enter the window size in seconds for expanding windows.");
        expandingWindowSizeField.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        windowPanel1.add(expandingWindowSizeField, componentGBC);

        hoppingCheckBox = new JCheckBox("Hopping");
        String hoppingToolTip = "The most recent snapshot view of the gaze data using a hopping window that is overlapping and fixed in size.";
        hoppingCheckBox.setToolTipText("<html><p width=\"500\">" + hoppingToolTip + "</p></html>");
        hoppingCheckBox.setFont(STANDARD_FONT);
        componentGBC.gridx = 0;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 2;
        windowPanel1.add(hoppingCheckBox, componentGBC);

        JLabel windowSizeLabel3 = new JLabel("Window Size (s)");
        windowSizeLabel3.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 6;
        componentGBC.gridwidth = 1;
        windowPanel1.add(windowSizeLabel3, componentGBC);

        hoppingWindowSizeField = new JTextField(windowSettings.hoppingWindowSize + "", 10); // Default value
        hoppingWindowSizeField.setToolTipText("Enter the window size in seconds for hopping windows.");
        hoppingWindowSizeField.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 6;
        componentGBC.gridwidth = 1;
        windowPanel1.add(hoppingWindowSizeField, componentGBC);

        JLabel hopSizeLabel = new JLabel("Hop Size (s)");
        hopSizeLabel.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowPanel1.add(hopSizeLabel, componentGBC);

        hoppingHopSizeField = new JTextField(windowSettings.hoppingHopSize + "", 10); // Default value
        hoppingHopSizeField.setToolTipText("Enter the hop size in seconds for hopping windows.");
        hoppingHopSizeField.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowPanel1.add(hoppingHopSizeField, componentGBC);

        eventCheckBox = new JCheckBox("Session");
        String eventToolTip = "An session view of the gaze data using a session window that is non-overlapping and non-fixed in size.";
        eventCheckBox.setToolTipText("<html><p width=\"500\">" + eventToolTip + "</p></html>");
        eventCheckBox.setFont(STANDARD_FONT);
        componentGBC.gridx = 0;
        componentGBC.gridy = 8;
        componentGBC.gridwidth = 2;
        windowPanel2.add(eventCheckBox, componentGBC);

        JLabel timeoutLabel = new JLabel("Timeout Length (s)");
        timeoutLabel.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 9;
        componentGBC.gridwidth = 1;
        windowPanel2.add(timeoutLabel, componentGBC);

        eventTimeoutField = new JTextField(windowSettings.eventTimeout + "", 10); // Default value
        eventTimeoutField.setToolTipText("Enter the timeout length in seconds for session windows.");
        eventTimeoutField.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 9;
        componentGBC.gridwidth = 1;
        windowPanel2.add(eventTimeoutField, componentGBC);

        JLabel maxDurationLabel = new JLabel("Max Duration (s)");
        maxDurationLabel.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 10;
        componentGBC.gridwidth = 1;
        windowPanel2.add(maxDurationLabel, componentGBC);

        eventMaxDurationField = new JTextField(windowSettings.eventMaxDuration + "", 10); // Default value
        eventMaxDurationField.setToolTipText("Enter the maximum duration in seconds for session windows.");
        eventMaxDurationField.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 10;
        componentGBC.gridwidth = 1;
        windowPanel2.add(eventMaxDurationField, componentGBC);

        JLabel baselineDurationLabel = new JLabel("Baseline Duration (s)");
        baselineDurationLabel.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 11;
        componentGBC.gridwidth = 1;
        windowPanel2.add(baselineDurationLabel, componentGBC);

        eventBaselineDurationField = new JTextField(windowSettings.eventBaselineDuration + "", 10); // Default value
        eventBaselineDurationField.setToolTipText("Enter the baseline duration in seconds for session windows.");
        eventBaselineDurationField.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 11;
        componentGBC.gridwidth = 1;
        windowPanel2.add(eventBaselineDurationField, componentGBC);

        JLabel eventLabel = new JLabel("Event");
        eventLabel.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 12;
        componentGBC.gridwidth = 1;
        windowPanel2.add(eventLabel, componentGBC);

        Set<String> itemSet = new HashSet<String>();
        itemSet.addAll(Windows.fixationEvents);
        itemSet.addAll(Windows.allGazeEvents);

        eventComboBox = new JComboBox<String>(itemSet.toArray(new String[itemSet.size()]));
        windowSettings.event = (String) eventComboBox.getSelectedItem();
        eventComboBox.setToolTipText("Select the event-defining analytic for session windows.");
        eventComboBox.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 12;
        componentGBC.gridwidth = 3;
        windowPanel2.add(eventComboBox, componentGBC);
        
        // Constraints dictate location of UI components
        GridBagConstraints subPanelGBC = new GridBagConstraints();
        subPanelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        subPanelGBC.gridx = 0;
        subPanelGBC.gridy = 1; 
        subPanelGBC.gridwidth = 1;
        subPanelGBC.gridheight = 1;
        subPanelGBC.weightx = 1;
        subPanelGBC.weighty = 1;

        windowsPanel.add(windowPanel1, subPanelGBC);

        subPanelGBC.gridx = 1;
        subPanelGBC.insets = new Insets(0, 20, 0, 0);
        windowsPanel.add(windowPanel2, subPanelGBC);

        analysisPanel.add(windowsPanel, panelGBC);
    }

    private void buildConsolePanel() {
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new GridBagLayout());

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.LAST_LINE_START;
        panelGBC.insets = new Insets(0, 0, 0, 0);
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
        runAnalysisButton.setToolTipText("Click to start the analysis with the selected settings.");
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        consolePanel.add(runAnalysisButton, componentGBC);

        analysisPanel.add(consolePanel, panelGBC);
    }

    private void buildHelpPagePanel() {
        JPanel windowsHelpPanel = new JPanel();
        windowsHelpPanel.setLayout(new BoxLayout(windowsHelpPanel, BoxLayout.PAGE_AXIS));

        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = GridBagConstraints.REMAINDER;
        panelGBC.gridy = GridBagConstraints.REMAINDER;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 1;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        JLabel windowsLabel = new JLabel("Understanding Windows");
        windowsLabel.setFont(BOLD_FONT);
        windowsHelpPanel.add(windowsLabel);

        String windowsAbstract = """
        <html><p width=\"750\">
            Window settings allow users to perform window-based analyses of the DGMs, wherein the participant’s gaze file can be analyzed over time by: <br/> <br/>
            •	taking a scheduled digest view of the gaze data using a tumbling window that is non-overlapping and fixed in size. <br/>
            •	taking the most recent snapshot view of the gaze data using a hopping window that is overlapping and fixed in size. <br/>
            •	taking an session view of the gaze data using a session window that is non-overlapping and non-fixed in size. <br/>
            •	taking a cumulative view of the gaze data using an expanding window that is overlapping and non-fixed in size. <br/> <br/>
            To enable a window, simply select the checkbox appearing next to the window’s name and enter the desired parameters corresponding to each window. 
            All window parameters are defined in seconds. Once users have selected one or more files and filled out their desired fields, they can press the “Run Analysis” 
            button to begin generating DGMs.
        </p></html>
        """;
        JLabel windowsDescriptionLabel = new JLabel(windowsAbstract);
        windowsHelpPanel.add(windowsDescriptionLabel);

        // Add space with invisible component
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel fig1ImageLabel = new JLabel(new ImageIcon(descriptiveAnalyticsImage.getScaledInstance(640, 480, 0)));
        windowsHelpPanel.add(fig1ImageLabel);
        
        String fig1Description = """
                Fig. 1: Descriptive Analytics
                """;
        JLabel fig1Label = new JLabel(fig1Description);
        windowsHelpPanel.add(fig1Label);
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel tumblingLabel = new JLabel("Tumbling Window");
        tumblingLabel.setFont(BOLD_FONT);
        windowsHelpPanel.add(tumblingLabel);

        String tumblingDescription = """
        <html><p width=\"750\">
            Gaze data is analyzed in a series of non-overlapping, fixed-size windows at scheduled contiguous time intervals, 
            shown in the figure below. The analysis process makes initial predictions based on user gaze found in a given time zone (i.e., window 1), 
            then moves on to the next bordering time zone to make subsequent predictions (i.e., window 2), and tumbles forward until reaching the end of an interaction. 
            The window size, or time zone, can be defined at any interval as appropriate depending on the given scenario. 
            For example, Fig. 1 shows a default value of 60s, whereby window 1 (containing gaze data between 0-60 seconds into an interaction), 
            2 (containing gaze data between 60-120 seconds into an interaction), 3 (containing gaze data between 120-180 seconds into an interaction), 
            and so on, are 60 seconds long. 
        </p></html>
        """;
        JLabel tumblingDescriptionLabel = new JLabel(tumblingDescription);
        windowsHelpPanel.add(tumblingDescriptionLabel);
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel tumblingImageLabel = new JLabel(new ImageIcon(tumblingWindowImage));
        windowsHelpPanel.add(tumblingImageLabel);

        String fig2Description = """
        <html><p width=\"750\">
            Fig. 2. Taking a scheduled digest view of the gaze data using a tumbling window that is non-overlapping and fixed in size.
        </p></html>
        """;
        JLabel fig2DescriptionLabel = new JLabel(fig2Description);
        windowsHelpPanel.add(fig2DescriptionLabel);

        // Add space with invisible component
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel hoppingLabel = new JLabel("Hopping Window");
        hoppingLabel.setFont(BOLD_FONT);
        windowsHelpPanel.add(hoppingLabel);

        String hoppingDescription = """
        <html><p width=\"750\">
            Taking a snapshot view of one’s gaze, it is possible to capture the most recent state of user’s visual attention by 
            utilizing an overlapping hopping window, shown in Fig. 3, whereby scheduled overlapping windows are utilized at a given interval.
            A hopping window analyzes gaze in a defined window size, then moves forward to the next scheduled hop relative to the previous one. 
            For example, Fig. 1 shows a default value of a 60s window size and a 30s hop size, meaning every 30 seconds, gaze over the last 60 
            seconds is analyzed, i.e., window 1 contains gaze data between 0-60 seconds, window 2 contains gaze data between 30-90 seconds, 
            window 3 contains data between 60-120 seconds, and so on.
        </p></html>
        """;
        JLabel hoppingDescriptionLabel = new JLabel(hoppingDescription);
        windowsHelpPanel.add(hoppingDescriptionLabel);
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel hoppingImageLabel = new JLabel(new ImageIcon(hoppingWindowImage));
        windowsHelpPanel.add(hoppingImageLabel);

        String fig3Description = """
        <html><p width=\"750\">
            Fig. 3. Taking the most recent snapshot view of the gaze data using a hopping window that is overlapping and fixed in size.
        </p></html>
        """;
        JLabel fig3DescriptionLabel = new JLabel(fig3Description);
        windowsHelpPanel.add(fig3DescriptionLabel);

        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel eventLabel = new JLabel("Session Window");
        eventLabel.setFont(BOLD_FONT);
        windowsHelpPanel.add(eventLabel);

        String eventDescription = """
        <html><p width=\"750\">
                During an interaction, users may experience defining moments that are impactful enough to affect their performance to a notable degree. 
                These defining moments may translate to notable gaze behaviors resembling phases of significant events. Within this context, gaze events 
                can be defined as anything unusual relative to what is already known of a user’s gaze. In other words, a user’s initial gaze values observed 
                at the beginning of an interaction are considered as a baseline, whereby future values can be compared against and deemed unusual if found 
                longer than the baseline. BEACH-Gaze currently supports the following seven definitions of a notable gaze event, including: <br/> <br/>

                •	SACCADE_MAG, also known as saccadic length, i.e., the magnitude of the saccade calculated as distance between each fixation (in pixels); <br/>
                •	SACCADE_DIR, also known as absolute angles, which is the angle between each fixation in degrees from horizontal (in degrees); <br/>
                •	LPMM, the diameter of the left eye pupil (in millimeters);<br/>
                •	RPMM, the diameter of the right eye pupil (in millimeters); <br/>
                •	LPMM + RPMM, which is the average value combining LPMM and RPMM (in millimeters);<br/>
                •	POGD, the duration of a fixation (in seconds); and <br/>
                •	BKPMIN, the number of blinks in the previous 60 second period of time (count). <br/> <br/>

                A non-overlapping, non-fixed-size session window is used to achieve session gaze analytics, shown in Fig. 4. 
                A session window begins when the first event is found; it then keeps searching for the next event within a specified time period. 
                If nothing is found, it would close at a specified timeout (e.g., window 1); if another event is found, the session window would extend the 
                search within another timeout period and repeat this process (e.g., window 3) until a specified maximum duration (e.g., window 2). For example, 
                Fig. 1 shows a default value of 4s timeout, 60s maximum duration, 120s baseline duration, and SACCADE_MAG, meaning that using the saccadic length 
                to define a notable gaze event, a baseline (i.e., the average saccadic length observed) is established for a user after 120 seconds, whereby subsequent 
                saccadic lengths that are higher in value are deemed as “events” for that person. If another event is found, the session window grows (i.e., the 4-second
                 timeout is renewed) until it meets the maximum duration set to 60 seconds. If no further events are found, the session window closes. 
        </p></html>
        """;
        JLabel eventDescriptionLabel = new JLabel(eventDescription);
        windowsHelpPanel.add(eventDescriptionLabel);

        JLabel eventImageLabel = new JLabel(new ImageIcon(eventWindowImage));
        windowsHelpPanel.add(eventImageLabel);

        String fig4Description = """
        <html><p width=\"750\">
                Fig. 4. Taking an session view of the gaze data using a session window that is non-overlapping and non-fixed in size.
        <html><p width=\"750\">
        """;
        JLabel fig4DescriptionLabel = new JLabel(fig4Description);
        windowsHelpPanel.add(fig4DescriptionLabel);
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add space with invisible component
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel expandingLabel = new JLabel("Expanding Window");
        expandingLabel.setFont(BOLD_FONT);
        windowsHelpPanel.add(expandingLabel);

        String expandingDescription = """
        <html><p width=\"750\">
            An expanding window takes a cumulative view to account for all that is known for a person’s gaze, shown in Fig. 5. 
            An initial set of gaze data collected from a user is analyzed (e.g., window 1), this can then be expanded to include
            new gaze data generated for that person at the next specified time interval (e.g., window 2 and 3). For example, 
            Fig. 1 shows a default value of 60s, meaning that every 60 seconds, the window grows (to 120 seconds, then 180 seconds, and so on) 
            to include new gaze data to what is already known about the person.
        <html><p width=\"750\">
        """;
        JLabel expandingDescriptionLabel = new JLabel(expandingDescription);
        windowsHelpPanel.add(expandingDescriptionLabel);
        windowsHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel expandingImageLabel = new JLabel(new ImageIcon(expandingWindowImage));
        windowsHelpPanel.add(expandingImageLabel);

        String fig5Description = """
        <html><p width=\"750\">
            Fig. 5. Taking a cumulative view of the gaze data using an expanding window that is overlapping and non-fixed in size.
        <html><p width=\"750\">
        """;
        JLabel fig5DescriptionLabel = new JLabel(fig5Description);
        windowsHelpPanel.add(fig5DescriptionLabel);

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
        dataLabel.setFont(BOLD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 120);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 8;
        dataSettingsPanel.add(dataLabel, componentGBC);

        selectDatasetButton = new JButton("Select File(s)");
        selectDatasetButton.setToolTipText("Select training dataset files");
        selectDatasetButton.setFont(STANDARD_FONT);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 3;
        dataSettingsPanel.add(selectDatasetButton, componentGBC);

        dataFilesCountLabel = new JLabel(datasetFiles.length + " files selected");
        dataFilesCountLabel.setFont(STANDARD_FONT);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 2;
        dataSettingsPanel.add(dataFilesCountLabel, componentGBC);

        JLabel directoryLabel = new JLabel("Output Directory");
        directoryLabel.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(10, 0, 0, 0);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 3;
        dataSettingsPanel.add(directoryLabel, componentGBC);

        predictionsBrowseDirectoryButton = new JButton("Browse");
        predictionsBrowseDirectoryButton.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 2;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 10;
        dataSettingsPanel.add(predictionsBrowseDirectoryButton, componentGBC);

        predictionsDirectoryField = new JTextField(analyticsOutputDirectory, 20);
        predictionsDirectoryField.setFont(STANDARD_FONT);
        predictionsDirectoryField.setEditable(false);
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 2;
        dataSettingsPanel.add(predictionsDirectoryField, componentGBC);

        predictionsPanel.add(dataSettingsPanel, panelGBC);

        // Create panel for classifier settings
        JPanel classifierSettingsPanel = new JPanel();
        classifierSettingsPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
        classifierSettingsPanel.setLayout(new GridBagLayout());

        panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = 0;
        panelGBC.gridy = 1;
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
        classificationLabel.setFont(BOLD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 120);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 8;
        classifierSettingsPanel.add(classificationLabel, componentGBC);

        classificationCheckBox = new JCheckBox("Classification");
        classificationCheckBox.setSelected(isClassification);
        classificationCheckBox.setFont(STANDARD_FONT);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        classifierSettingsPanel.add(classificationCheckBox, componentGBC);

        regressionCheckBox = new JCheckBox("Regression");
        regressionCheckBox.setFont(STANDARD_FONT);
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
        runPredictionsButton.setToolTipText("Click to start predictions with the selected settings.");
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

        // Text fields - Replace ActionListeners with DocumentListeners

        tumblingWindowSizeField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTumblingWindowSize(); }
            public void removeUpdate(DocumentEvent e) { updateTumblingWindowSize(); }
            public void changedUpdate(DocumentEvent e) { updateTumblingWindowSize(); }

            private void updateTumblingWindowSize() {
                String text = tumblingWindowSizeField.getText();
                if (isNumeric(text)) {
                    windowSettings.tumblingWindowSize = Double.parseDouble(text);
                    tumblingWindowSizeField.setBackground(Color.WHITE);
                } else {
                    tumblingWindowSizeField.setBackground(Color.PINK);
                }
            }
        });

        expandingWindowSizeField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateExpandingWindowSize(); }
            public void removeUpdate(DocumentEvent e) { updateExpandingWindowSize(); }
            public void changedUpdate(DocumentEvent e) { updateExpandingWindowSize(); }

            private void updateExpandingWindowSize() {
                String text = expandingWindowSizeField.getText();
                if (isNumeric(text)) {
                    windowSettings.expandingWindowSize = Double.parseDouble(text);
                    expandingWindowSizeField.setBackground(Color.WHITE);
                } else {
                    expandingWindowSizeField.setBackground(Color.PINK);
                }
            }
        });

        hoppingWindowSizeField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateHoppingWindowSize(); }
            public void removeUpdate(DocumentEvent e) { updateHoppingWindowSize(); }
            public void changedUpdate(DocumentEvent e) { updateHoppingWindowSize(); }

            private void updateHoppingWindowSize() {
                String text = hoppingWindowSizeField.getText();
                if (isNumeric(text)) {
                    windowSettings.hoppingWindowSize = Double.parseDouble(text);
                    hoppingWindowSizeField.setBackground(Color.WHITE);
                } else {
                    hoppingWindowSizeField.setBackground(Color.PINK);
                }
            }
        });

        hoppingHopSizeField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateHoppingHopSize(); }
            public void removeUpdate(DocumentEvent e) { updateHoppingHopSize(); }
            public void changedUpdate(DocumentEvent e) { updateHoppingHopSize(); }

            private void updateHoppingHopSize() {
                String text = hoppingHopSizeField.getText();
                if (isNumeric(text)) {
                    windowSettings.hoppingHopSize = Double.parseDouble(text);
                    hoppingHopSizeField.setBackground(Color.WHITE);
                } else {
                    hoppingHopSizeField.setBackground(Color.PINK);
                }
            }
        });

        eventTimeoutField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateEventTimeout(); }
            public void removeUpdate(DocumentEvent e) { updateEventTimeout(); }
            public void changedUpdate(DocumentEvent e) { updateEventTimeout(); }

            private void updateEventTimeout() {
                String text = eventTimeoutField.getText();
                if (isNumeric(text)) {
                    windowSettings.eventTimeout = Double.parseDouble(text);
                    eventTimeoutField.setBackground(Color.WHITE);
                } else {
                    eventTimeoutField.setBackground(Color.PINK);
                }
            }
        });

        eventMaxDurationField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateEventMaxDuration(); }
            public void removeUpdate(DocumentEvent e) { updateEventMaxDuration(); }
            public void changedUpdate(DocumentEvent e) { updateEventMaxDuration(); }

            private void updateEventMaxDuration() {
                String text = eventMaxDurationField.getText();
                if (isNumeric(text)) {
                    windowSettings.eventMaxDuration = Double.parseDouble(text);
                    eventMaxDurationField.setBackground(Color.WHITE);
                } else {
                    eventMaxDurationField.setBackground(Color.PINK);
                }
            }
        });

        eventBaselineDurationField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateEventBaselineDuration(); }
            public void removeUpdate(DocumentEvent e) { updateEventBaselineDuration(); }
            public void changedUpdate(DocumentEvent e) { updateEventBaselineDuration(); }

            private void updateEventBaselineDuration() {
                String text = eventBaselineDurationField.getText();
                if (isNumeric(text)) {
                    windowSettings.eventBaselineDuration = Double.parseDouble(text);
                    eventBaselineDurationField.setBackground(Color.WHITE);
                } else {
                    eventBaselineDurationField.setBackground(Color.PINK);
                }
            }
        });

        // Comboboxes
        eventComboBox.addItemListener(e -> {
            windowSettings.event = (String) eventComboBox.getSelectedItem();
        });
    }

    private void selectAnalyticsInputFiles() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(("CSV Files"), "csv");
        JFileChooser chooser = new JFileChooser(lastDirectory);

        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            lastDirectory = chooser.getCurrentDirectory();
            analyticsInputFiles = chooser.getSelectedFiles();
        }

        fileCountLabel.setText(analyticsInputFiles.length + " files selected");
    }

    private void selectAnalyticsFileDirectory() {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            lastDirectory = chooser.getCurrentDirectory();
            analyticsOutputDirectory = chooser.getSelectedFile().getAbsolutePath();
            analyticsDirectoryField.setText(analyticsOutputDirectory);
        }
    }

    private void selectDatasetFiles() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(("CSV Files"), "csv");
        JFileChooser chooser = new JFileChooser(lastDirectory);

        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            lastDirectory = chooser.getCurrentDirectory();
            datasetFiles = chooser.getSelectedFiles();
        }

        dataFilesCountLabel.setText(datasetFiles.length + " files selected");
    }

    private void selectPredictionsFileDirectory() {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            lastDirectory = chooser.getCurrentDirectory();
            predictionsOutputDirectory = chooser.getSelectedFile().getAbsolutePath();
            predictionsDirectoryField.setText(predictionsOutputDirectory);
        }
    }

    private void runAnalysis() {
        try {
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "An error occurred during analysis:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void runPredictions() {
        try {
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

            WekaParameters params = new WekaParameters(datasetFiles, predictionsDirectory, isClassification);
            WekaExperiment experiment = new WekaExperiment(params);
            experiment.run();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "An error occurred during predictions:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void buildTabPanels() {
        tabs = new JTabbedPane();

        analysisPanel = new JPanel(new GridBagLayout());
        analysisPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buildGazePanel();
        buildWindowsPanel();
        buildConsolePanel();
        JScrollPane analysisScrollPane = new JScrollPane(analysisPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        helpPanel = new JPanel(new GridBagLayout());
        helpPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buildHelpPagePanel();
        JScrollPane helpPanelScrollable = new JScrollPane(helpPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        predictionsPanel = new JPanel(new GridBagLayout());
        predictionsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buildPredictionsPanel();
        JScrollPane predictionsScrollPane = new JScrollPane(predictionsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        tabs.addTab("Descriptive Analytics", analysisScrollPane);
        tabs.addTab("Predictive Analytics", predictionsScrollPane);
        tabs.addTab("Help", helpPanelScrollable);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}