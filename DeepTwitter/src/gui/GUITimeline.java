package gui;

import gui.visualizations.TimelinePanel;

import java.util.List;

import javax.swing.JFrame;

import twitter4j.Status;
import twitter4j.TwitterResponse;

public class GUITimeline extends JFrame{
	public GUITimeline(List<TwitterResponse> statusesList, boolean isGroup, String name) {
		super("Timeline");
		setContentPane(new TimelinePanel(statusesList,isGroup,name));
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
	}
}
