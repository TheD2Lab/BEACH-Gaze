package analysis;

import java.awt.*;
import javax.swing.*;
import java.util.HashMap;


public class UserInterface {
    
    private JFrame frame;
    private HashMap<String, Integer> windowSettings;
    private String[] inputFiles;
    private String outputDirectory;
    
    public UserInterface() {
        initComponents();
        buildFrame();
        setEventHandling();
    }

    private void initComponents() {
        frame = new JFrame("Gazepoint Data Analysis");
        frame.setLayout(new GridBagLayout());

        // 
        JPanel analysisPanel = new JPanel(new GridBagLayout());
        GridBagConstraints analysisPanelConstraints = new GridBagConstraints();
        
        analysisPanel.setBackground(Color.yellow);

        JCheckBox batchAnalysisCheckBox = new JCheckBox("Batch Analysis");
        JButton selectFilesButton = new JButton("Select Files");
        GridBagConstraints analysisComponentConstraints = new GridBagConstraints();

        analysisPanel.add(batchAnalysisCheckBox, analysisComponentConstraints);
        analysisPanel.add(selectFilesButton, analysisComponentConstraints);

        frame.add(analysisPanel, analysisPanelConstraints);

        // 
        JPanel windowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints windowConstraints = new GridBagConstraints();
        JCheckBox continuousCheckBox = new JCheckBox("Continuous");
        JCheckBox cumulativeCheckBox = new JCheckBox("Cumulative");
        JCheckBox overlappingCheckBox = new JCheckBox("Overlapping");
        JCheckBox eventCheckBox = new JCheckBox("Event");

        windowPanel.add(continuousCheckBox, windowConstraints);
        windowPanel.add(cumulativeCheckBox, windowConstraints);
        windowPanel.add(overlappingCheckBox, windowConstraints);
        windowPanel.add(eventCheckBox, windowConstraints);

        frame.add(windowPanel);

        // 
        JPanel consolePanel = new JPanel(new GridBagLayout());
        GridBagConstraints consoleConstraints = new GridBagConstraints();
        JButton runAnalysisButton = new JButton("Run Analysis");

        consolePanel.add(runAnalysisButton, consoleConstraints);
        
        frame.add(consolePanel);

        // 
        JPanel contactInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints contactInfoConstraints = new GridBagConstraints();
        
        frame.add(contactInfoPanel);
    }

    private void buildFrame() {
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setEventHandling() {

    }

    public Parameters getParameters() {
        return new Parameters(inputFiles, outputDirectory, windowSettings);
    }
}
