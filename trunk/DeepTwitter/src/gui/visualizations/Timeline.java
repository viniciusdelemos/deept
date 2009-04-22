package gui.visualizations;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import prefuse.demos.Congress;

import twitter4j.Status;

public class Timeline extends JFrame{
	public Timeline(ArrayList<Status> statusesList) {
		super("Timeline");
		setContentPane(new TimelinePanel(statusesList));
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
	}
}
