package analysis;

import javax.swing.JFrame;

public class UserInterface{
    
    private JFrame frame;
    
    public UserInterface() {
        initComponents();
        setEventHandling();
        buildFrame();
    }

    private void initComponents() {
        frame = new JFrame("Gazepoint Data Analysis - The D2 Lab");
    }

    private void buildFrame() {
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setEventHandling() {
    }
}
