package analysis;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface {

    File[] inputFiles;
    String outputDirectory;
    WindowAnalysis windowAnalysis;

    private JFrame frame;
    private JTabbedPane tabs;
    private JPanel analysisPanel;
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
        outputDirectory = "C:\\";
        windowAnalysis = new WindowAnalysis();

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
        gazePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
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
        String tumblingToolTip = "This approach to predictive gaze analytics takes a " +
        "digest view on the users’ gaze data and focuses on " +
        "providing scheduled non-overlapping updates in the " +
        "process of generating predictions.";
        tumblingCheckBox.setToolTipText("<html><p width=\"500\">" + tumblingToolTip +"</p></html>");
        componentGBC.insets = new Insets(0, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 3;
        windowsPanel.add(tumblingCheckBox, componentGBC);

        JLabel windowSizeLabel1 = new JLabel("Window Size");
        componentGBC.gridx = 0;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 1;
        windowsPanel.add(windowSizeLabel1, componentGBC);

        tumblingWindowSizeField = new JTextField("", 5);
        componentGBC.gridx = 1;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 1;
        windowsPanel.add(tumblingWindowSizeField, componentGBC);
        
        expandingCheckBox = new JCheckBox("Expanding");
        String expandingToolTip = "The goal of this approach is to emphasize on all gaze that is " +
        "known to the system when generating predictions on user success and failure.";
        expandingCheckBox.setToolTipText("<html><p width=\"500\">" + expandingToolTip + "</p></html>");
        componentGBC.gridx = 0;
        componentGBC.gridy = 3;
        componentGBC.gridwidth = 3;
        windowsPanel.add(expandingCheckBox, componentGBC);

        JLabel windowSizeLabel2 = new JLabel("Window Size");
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        windowsPanel.add(windowSizeLabel2, componentGBC);

        expandingWindowSizeField = new JTextField("", 5);
        componentGBC.gridx = 1;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        windowsPanel.add(expandingWindowSizeField, componentGBC);

        hoppingCheckBox = new JCheckBox("Hopping");
        String hoppingToolTip = "The goal of this approach is to capture a series of continuous " + 
                "snapshots that reflect the most recent gaze state of the user.";
        hoppingCheckBox.setToolTipText("<html><p width=\"500\">" + hoppingToolTip + "</p></html>");
        componentGBC.gridx = 0;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 3;
        windowsPanel.add(hoppingCheckBox, componentGBC);

        JLabel windowSizeLabel3 = new JLabel("Window Size");
        componentGBC.gridx = 0;
        componentGBC.gridy = 6;
        componentGBC.gridwidth = 1;
        windowsPanel.add(windowSizeLabel3, componentGBC);
        
        hoppingWindowSizeField = new JTextField("", 5);
        componentGBC.gridx = 1;
        componentGBC.gridy = 6;
        componentGBC.gridwidth = 1;
        windowsPanel.add(hoppingWindowSizeField, componentGBC);

        JLabel overlapSizeLabel = new JLabel("Overlap Size");
        componentGBC.gridx = 0;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowsPanel.add(overlapSizeLabel, componentGBC);

        hoppingOverlapSizeField = new JTextField("", 5);
        componentGBC.gridx = 1;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 1;
        windowsPanel.add(hoppingOverlapSizeField, componentGBC);

        eventCheckBox = new JCheckBox("Event");
        String eventToolTip = "The goal of this " + 
                "approach to gaze analytics is to emphasize on potentially " +
                "more informative chunks of gaze data in the " + 
                "predictions of user success and failure, where the " + 
                "weight is placed on notable events rather than taking " +
                "a scheduled view on gaze data stream";
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
        componentGBC.insets = new Insets(190, 10, 20, 10);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        consolePanel.add(runAnalysisButton, componentGBC);

        analysisPanel.add(consolePanel, panelGBC);
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

        tumblingCheckBox.addActionListener(e -> {
            windowAnalysis.setTumblingEnabled(expandingCheckBox.isSelected());
        });

        expandingCheckBox.addActionListener(e -> {
            windowAnalysis.setExpandingEnabled(expandingCheckBox.isSelected());
        });

        hoppingCheckBox.addActionListener(e -> {
            windowAnalysis.setHoppingEnabled(hoppingCheckBox.isEnabled());
        });

        eventCheckBox.addActionListener(e -> {
            windowAnalysis.setEventEnabled(eventCheckBox.isEnabled());
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
        Parameters params = new Parameters(inputFiles, outputDirectory, windowAnalysis);
        Analysis analysis = new Analysis(params);
        analysis.run();
    }

    private void buildTabPanels() {
        tabs = new JTabbedPane();

        analysisPanel = new JPanel(new GridBagLayout());
        buildGazePanel();
        buildWindowsPanel();
        buildConsolePanel();

        JPanel helpPanel = new JPanel(new GridBagLayout());

        tabs.addTab("Analysis", analysisPanel);
        tabs.addTab("Help", helpPanel);
    }
}
