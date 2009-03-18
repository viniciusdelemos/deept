package gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import model.UserTimeline;
import prefuse.visual.VisualItem;

public class GUIViewUpdates extends JFrame{
	UserTimeline timeline;
	
	public GUIViewUpdates(VisualItem user, boolean isTwitterUser) {
		timeline = new UserTimeline(user.getString("idTwitter"),isTwitterUser);
		setTitle("Últimos updates de "+user.getString("name"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(300,650));
		pack();
		setContentPane(new JScrollPane(timeline.getContent()));
		addWindowListener(new WindowAdapter() {
			@Override
			 public void windowClosed(java.awt.event.WindowEvent arg0) {
				timeline.stopThread();
			}
		});
		setVisible(true);		
	}
}
