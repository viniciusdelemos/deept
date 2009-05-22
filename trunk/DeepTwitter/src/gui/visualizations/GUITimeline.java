package gui.visualizations;

import java.util.List;

import javax.swing.JFrame;

import twitter4j.Status;
import twitter4j.TwitterResponse;

public class GUITimeline extends JFrame{
	public GUITimeline(List<TwitterResponse> statusesList, boolean isGroup) {
		super("Timeline");
		setContentPane(new TimelinePanel(statusesList,isGroup));
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
	}
}
