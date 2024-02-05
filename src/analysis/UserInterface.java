package analysis;

import java.awt.*;
import javax.swing.*;
import java.util.HashMap;


public class UserInterface {

    private JFrame frame;

    public UserInterface() {
        initFrame();
        initGazePanel();
        initWindowsPanel();
        initConsolePanel();
        initContactPanel();

        frame.setVisible(true);
    }

    private void initFrame() {
        frame = new JFrame("Gazepoint Analysis Tool");
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initGazePanel() {
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

        JLabel gazeLabel = new JLabel("Gaze Analysis Settings");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = 3;
        gazePanel.add(gazeLabel, componentGBC);

        JCheckBox batchAnalysisCheckBox = new JCheckBox();
        componentGBC.gridwidth = 1;
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        gazePanel.add(batchAnalysisCheckBox, componentGBC);

        JLabel batchAnalysisLabel = new JLabel("Batch Analysis (Select to analyze multiple participants)");
        componentGBC.gridx = 1;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 5;
        gazePanel.add(batchAnalysisLabel, componentGBC);

        JButton selectFilesButton = new JButton("Select File(s)");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 3;
        gazePanel.add(selectFilesButton, componentGBC);

        JLabel fileCountLabel = new JLabel("# files selected");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 3;
        componentGBC.gridwidth = 2;
        gazePanel.add(fileCountLabel, componentGBC);

        JLabel directoryLabel = new JLabel("Output Directory");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 4;
        gazePanel.add(directoryLabel, componentGBC);

        JButton directoryButton = new JButton("Browse");
        componentGBC.gridx = 0;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 2;
        gazePanel.add(directoryButton, componentGBC);

        JTextField directoryField = new JTextField("Output Directory", 10);
        componentGBC.gridx = 2;
        componentGBC.gridy = 5;
        componentGBC.gridwidth = 2;
        gazePanel.add(directoryField, componentGBC);

        JLabel participantLabel = new JLabel("Participant ID");
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 6;
        gazePanel.add(participantLabel, componentGBC);

        JTextField participantField = new JTextField("p1", 19);
        componentGBC.gridx = GridBagConstraints.REMAINDER;
        componentGBC.gridy = 7;
        componentGBC.gridwidth = 3;
        gazePanel.add(participantField, componentGBC);

        frame.add(gazePanel, panelGBC);
    }

    private void initWindowsPanel() {
        // Panel contains all components pertaining to gaze settings
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
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        windowsPanel.add(windowsLabel, componentGBC);

        frame.add(windowsPanel, panelGBC);
    }

    private void initConsolePanel() {
        // Panel contains all components pertaining to gaze settings
        JPanel windowsPanel = new JPanel();
        windowsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        windowsPanel.setLayout(new GridBagLayout());

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
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
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        windowsPanel.add(consoleLabel, componentGBC);

        frame.add(windowsPanel, panelGBC);
    }

    private void initContactPanel() {
        // Panel contains all components pertaining to gaze settings
        JPanel contactPanel = new JPanel();
        contactPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        contactPanel.setLayout(new GridBagLayout());

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
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

        frame.add(contactPanel, panelGBC);
    }
}
