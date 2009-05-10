package gui.visualizations;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.twitter4j.StatusDeepT;
import model.twitter4j.TwitterResponseDeepT;

import prefuse.demos.Congress;

import twitter4j.Status;
import twitter4j.TwitterException;

public class GUITimeline extends JFrame{
	public GUITimeline(List<TwitterResponseDeepT> statusesList) {
		super("Timeline");
		setContentPane(new TimelinePanel(statusesList));
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
	}
}
