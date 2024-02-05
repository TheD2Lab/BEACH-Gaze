package analysis;

import java.awt.*;
import javax.swing.*;
import java.util.HashMap;


public class UserInterface {

    private JFrame frame;

    public UserInterface() {
        // Initialize UI frame
        frame = new JFrame("Gazepoint Analysis Tool");
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Constraints dictate location of UI components
        GridBagConstraints panelGBC = new GridBagConstraints(); 
        GridBagConstraints componentGBC = new GridBagConstraints();

        // Panel contains all components pertaining to gaze settings
        JPanel gazePanel = new JPanel();
        gazePanel.setSize(400, 300);
        gazePanel.setLayout(new GridBagLayout());
        panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = 0;
        panelGBC.gridy = 0;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 1;
        panelGBC.weightx = 1;
        panelGBC.weighty = 1;

        JLabel gazeLabel = new JLabel("Gaze Analysis Settings");
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.ipadx = 10;
        componentGBC.ipady = 10;
        componentGBC.gridx = 0;
        componentGBC.gridy = 0;
        componentGBC.gridwidth = GridBagConstraints.REMAINDER;
        componentGBC.gridheight = 1;
        gazePanel.add(gazeLabel, componentGBC);

        JButton selectFiles = new JButton("Select File(s)");
        componentGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        componentGBC.gridx = 0;
        componentGBC.gridy = 1;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        gazePanel.add(selectFiles, componentGBC);

        JLabel fileCount = new JLabel("# files selected");
        componentGBC.gridx = 0;
        componentGBC.gridy = 2;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        gazePanel.add(fileCount, componentGBC);

        JLabel directoryLabel = new JLabel("Output Directory");
        componentGBC.gridx = 0;
        componentGBC.gridy = 3;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        gazePanel.add(directoryLabel, componentGBC);

        JButton button2 = new JButton("Select Directory");
        componentGBC.gridx = 0;
        componentGBC.gridy = 4;
        componentGBC.gridwidth = 1;
        componentGBC.gridheight = 1;
        gazePanel.add(button2, componentGBC);

        frame.add(gazePanel, panelGBC);

        // Wipe constraints
        panelGBC = new GridBagConstraints(); 
        componentGBC = new GridBagConstraints();

        // Panel contains all components pertaining to window analysis
        JPanel windowsPanel = new JPanel();
        windowsPanel.setLayout(new GridBagLayout());
        //panelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
        panelGBC.gridx = 1;
        panelGBC.gridy = 0;
        panelGBC.gridwidth = 1;
        panelGBC.gridheight = 1;

        frame.add(windowsPanel, panelGBC);

        frame.setVisible(true);
    }
}
