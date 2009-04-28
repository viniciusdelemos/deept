package gui.visualizations;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.StatusDeepT;

import prefuse.demos.Congress;

import twitter4j.Status;

public class GUITimeline extends JFrame{
	public GUITimeline(ArrayList<StatusDeepT> statusesList) {
		super("Timeline");
		setContentPane(new TimelinePanel(statusesList));
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
	}
}
