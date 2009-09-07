package gui;

import gui.visualizations.TimelineView;

import java.util.List;

import javax.swing.JFrame;

import twitter4j.Status;
import twitter4j.TwitterResponse;

public class GUITimeline extends JFrame{
	public GUITimeline(List<TwitterResponse> statusesList, boolean isGroup, String name) {
		super("DeepTwitter - Time Line");
		setContentPane(new TimelineView(statusesList,isGroup,name));
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
	}
}
