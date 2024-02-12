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
    WindowSettings windowSettings;

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
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 4;
        gazePanel.add(gazeLabel, componentGBC);

        batchAnalysisCheckBox = new JCheckBox("Batch Analysis (Select to analyze multiple participants)");
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 5;
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
        componentGBC.gridx = 0;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 2;
        gazePanel.add(browseDirectoryButton, componentGBC);

        directoryField = new JTextField(outputDirectory, 20);
        directoryField.setEditable(false);
        componentGBC.gridx = 2;
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
        windowsLabel.setFont(windowsLabel.getFont().deriveFont(Font.BOLD, 14f));
        componentGBC.insets = new Insets(0, 0, 0, 150);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 10;
        windowsPanel.add(windowsLabel, componentGBC);

        tumblingCheckBox = new JCheckBox("Tumbling");
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
        componentGBC.gridx = 0;
        componentGBC.gridy = 8;
        componentGBC.gridwidth = 3;
        windowsPanel.add(eventCheckBox, componentGBC);

        analysisPanel.add(windowsPanel, panelGBC);
    }

    private void buildConsolePanel() {
        JPanel windowsPanel = new JPanel();
        Border border = new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
                                           BorderFactory.createEmptyBorder(10,10,10,10));
        windowsPanel.setBorder(border);
        windowsPanel.setLayout(new GridBagLayout());

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

        JLabel consoleLabel = new JLabel("Console Errors");
        componentGBC.insets = new Insets(0, 0, 0, 350);
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsPanel.add(consoleLabel, componentGBC);
        
        runAnalysisButton = new JButton("Run Analysis");
        componentGBC.insets = new Insets(180, 0, 0, 0);
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        windowsPanel.add(runAnalysisButton, componentGBC);

        analysisPanel.add(windowsPanel, panelGBC);
    }

    private void buildContactPanel() {
        JPanel contactPanel = new JPanel();
        contactPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        contactPanel.setLayout(new GridBagLayout());

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.LAST_LINE_START;
        panelGBC.gridx = 1;
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

        JLabel contactLabel = new JLabel("Contact Info");
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        contactPanel.add(contactLabel, componentGBC);

        analysisPanel.add(contactPanel, panelGBC);
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
            windowSettings.setTumblingEnabled(expandingCheckBox.isSelected());
        });

        expandingCheckBox.addActionListener(e -> {
            windowSettings.setExpandingEnabled(expandingCheckBox.isSelected());
        });

        hoppingCheckBox.addActionListener(e -> {
            windowSettings.setHoppingEnabled(hoppingCheckBox.isEnabled());
        });

        eventCheckBox.addActionListener(e -> {
            windowSettings.setEventEnabled(eventCheckBox.isEnabled());
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
        Parameters params = new Parameters(inputFiles, outputDirectory, windowSettings);
        Analysis analysis = new Analysis(params);
        analysis.run();
    }

    private void buildTabPanels() {
        tabs = new JTabbedPane();

        analysisPanel = new JPanel(new GridBagLayout());
        buildGazePanel();
        buildWindowsPanel();
        buildConsolePanel();
        buildContactPanel();

        JPanel helpPanel = new JPanel(new GridBagLayout());

        tabs.addTab("Analysis", analysisPanel);
        tabs.addTab("Help", helpPanel);
    }
}
