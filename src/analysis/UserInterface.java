package analysis;

import java.awt.*;
import java.awt.event.ItemEvent;

import javax.swing.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface {

    File[] inputFiles;
    String outputDirectory;
    WindowSettings windowSettings;

    private JFrame frame;
    private JTabbedPane tabs;
    private JPanel analysisPanel;
    private JPanel helpPanel;
    private JCheckBox batchAnalysisCheckBox;
    private JButton selectFilesButton;
    private JLabel fileCountLabel;
    private JButton browseDirectoryButton;
    private JTextField directoryField;
    private JButton runAnalysisButton;
    private JCheckBox tumblingCheckBox;
    private JTextField tumblingWindowSizeField;
    private JCheckBox expandingCheckBox;
    private JTextField expandingWindowSizeField;
    private JCheckBox hoppingCheckBox;
    private JTextField hoppingWindowSizeField;
    private JTextField hoppingOverlapSizeField;
    private JCheckBox eventCheckBox;

    public UserInterface() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e);
        }

        // Default values
        inputFiles = new File[0];
        outputDirectory = System.getProperty("user.dir");
        windowSettings = new WindowSettings();

        buildFrame();
        buildTabPanels();
        setEventHandlers();

        frame.add(tabs);
        frame.setVisible(true);
    }

    private void buildFrame() {
        frame = new JFrame("Gazepoint Analysis Tool");
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

        batchAnalysisCheckBox = new JCheckBox("Batch Analysis (Select to analyze multiple participants)");
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 6;
        gazePanel.add(batchAnalysisCheckBox, componentGBC);

        selectFilesButton = new JButton("Select File(s)");
        selectFilesButton.setToolTipText("Select all_gaze.csv files only");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 3;
        gazePanel.add(selectFilesButton, componentGBC);

        fileCountLabel = new JLabel(inputFiles.length + " files selected");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 3;
        componentGBC.gridwidth = 2;
        gazePanel.add(fileCountLabel, componentGBC);

        JLabel directoryLabel = new JLabel("Output Directory");
        componentGBC.insets = new Insets(10, 0, 0, 0);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 4;
        gazePanel.add(directoryLabel, componentGBC);

        browseDirectoryButton = new JButton("Browse");
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 2;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 2;
        gazePanel.add(browseDirectoryButton, componentGBC);

        directoryField = new JTextField(outputDirectory, 20);
        directoryField.setEditable(false);
        componentGBC.gridx = 0;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 2;
        gazePanel.add(directoryField, componentGBC);

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
        String expandingToolTip = "A cumulative view of the gaze data using an expanding window that is overlapping and non-fixed in size.";
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

        JLabel overlapSizeLabel = new JLabel("Overlap Size (s)");
        componentGBC.insets = new Insets(0, 20, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowsPanel.add(overlapSizeLabel, componentGBC);

        hoppingOverlapSizeField = new JTextField("", 10);
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 1;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowsPanel.add(hoppingOverlapSizeField, componentGBC);

        eventCheckBox = new JCheckBox("Event-Based");
        String eventToolTip = "An event-based view of the gaze data using a session window that is non-overlapping and non-fixed in size";
        eventCheckBox.setToolTipText("<html><p width=\"500\">" + eventToolTip + "</p></html>");
        componentGBC.gridx = 0;
        componentGBC.gridy = 8;
        componentGBC.gridwidth = 3;
        windowsPanel.add(eventCheckBox, componentGBC);

        analysisPanel.add(windowsPanel, panelGBC);
    }

    private void buildConsolePanel() {
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new GridBagLayout());
        // Border border = new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
        //                                    BorderFactory.createEmptyBorder(10,10,10,10));
        // consolePanel.setBorder(border);

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.LAST_LINE_START;
        panelGBC.gridx = 0;
        panelGBC.gridy = 1;
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

        // JLabel consoleLabel = new JLabel("Console Errors");
        // componentGBC.insets = new Insets(0, 0, 0, 350);
        // componentGBC.gridx = 0;
        // componentGBC.gridy = 0;
        // componentGBC.gridwidth = 1;
        // componentGBC.gridheight = 1;
        // consolePanel.add(consoleLabel, componentGBC);
        
        runAnalysisButton = new JButton("Run Analysis");
        runAnalysisButton.setFont(runAnalysisButton.getFont().deriveFont(20f));
        componentGBC.insets = new Insets(190, 0, 20, 0);
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
        windowsLabel.setFont(windowsLabel.getFont().deriveFont(Font.BOLD, 12f));
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsHelpPanel.add(windowsLabel, componentGBC);

        JTextArea windowsAbstract = new JTextArea(
            "To further support predictive gaze analytics, we offer additional, optional approaches to analyzing gaze data with the use of discrete-timed windows. " +
            "This approach to predictive gaze analytics focuses on learning from digests of user gaze (tumbling window), snapshots of the most recent user gaze (hopping window)," +
            "gaze captured during significant events (event-based window), as well as all known gaze to date (expanding window). "
        );
        windowsAbstract.setFont(windowsLabel.getFont().deriveFont(Font.PLAIN));
        windowsAbstract.setLineWrap(true);
        windowsAbstract.setWrapStyleWord(true);
        windowsAbstract.setEditable(false);

        JLabel tumblingLabel = new JLabel("Tumbling Window");

        componentGBC.insets = new Insets(0, 0, 0, 20);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = GridBagConstraints.REMAINDER;
        componentGBC.gridheight = 10;
        windowsHelpPanel.add(windowsAbstract, componentGBC);

        helpPanel.add(windowsHelpPanel, panelGBC);
    }

    private void setEventHandlers() {
        selectFilesButton.addActionListener(e -> {
            selectInputFiles();
        });

        browseDirectoryButton.addActionListener(e -> {
            selectFileDirectory();
        });

        runAnalysisButton.addActionListener(e -> {
            runAnalysis();
        });

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

        tumblingWindowSizeField.addActionListener(e -> {
            windowSettings.tumblingWindowSize = Integer.parseInt(tumblingWindowSizeField.getText());
        });

        expandingWindowSizeField.addActionListener(e -> {
            windowSettings.expandingWindowSize = Integer.parseInt(expandingWindowSizeField.getText());
        });

        hoppingWindowSizeField.addActionListener(e -> {
            windowSettings.hoppingWindowSize = Integer.parseInt(hoppingWindowSizeField.getText());
        });

        hoppingOverlapSizeField.addActionListener(e -> {
            windowSettings.hoppingOverlapSize = Integer.parseInt(hoppingOverlapSizeField.getText());
        });
    }

    private void selectInputFiles() {
        boolean batchAnalysis = batchAnalysisCheckBox.isSelected();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(("CSV Files"), "csv");
        JFileChooser chooser = new JFileChooser();

        chooser.setMultiSelectionEnabled(batchAnalysis);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            inputFiles = batchAnalysis ? chooser.getSelectedFiles() : new File[] {chooser.getSelectedFile()};
        }

        fileCountLabel.setText(inputFiles.length + " files selected");
    }

    private void selectFileDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            outputDirectory = chooser.getSelectedFile().getAbsolutePath();
            directoryField.setText(outputDirectory);
        }
    }

    private void runAnalysis() {
        // Create a subfolder within the given output directory called "analysis"
        File f = new File(outputDirectory + "\\results");
        if (!f.exists()) {
            f.mkdirs();
            outputDirectory += "\\results";
        } else {
            int fileCount = 1;
            outputDirectory += "\\results (" + fileCount + ")";
            f = new File(outputDirectory);
            while (f.exists()) {
                fileCount++;
                outputDirectory.replace(String.valueOf(fileCount - 1), String.valueOf(fileCount));
            }
        }

        Parameters params = new Parameters(inputFiles, outputDirectory, windowSettings);
        Analysis analysis = new Analysis(params);
        boolean isSuccessful = analysis.run();
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

        tabs.addTab("Analysis", analysisPanel);
        tabs.addTab("Help", helpPanel);
    }

    private boolean validateFields() {
        return false;
    }
}
