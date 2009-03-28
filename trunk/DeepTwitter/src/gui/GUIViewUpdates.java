package gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import model.GraphicManager;
import model.StatusesTable;
import prefuse.visual.VisualItem;

public class GUIViewUpdates extends JFrame{
	StatusesTable timeline;
	
	public GUIViewUpdates(VisualItem user, GraphicManager gManager) {
		timeline = new StatusesTable(gManager,user.getString("idTwitter"));
		setTitle("Últimos updates de "+user.getString("name"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(439,650));
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
