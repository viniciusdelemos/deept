package main;

import gui.GUILoginDeepTwitter;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import controller.ControllerDeepTwitter;
import prefuse.Display;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class DeepTwitter {		
	
	public static void main(String[] args) {		
		GUILoginDeepTwitter loginWindow = new GUILoginDeepTwitter();	
		
		try { 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {			
			e.printStackTrace();						
		}
		
		ControllerDeepTwitter controller = new ControllerDeepTwitter(loginWindow);		
		SwingUtilities.updateComponentTreeUI(loginWindow);
		loginWindow.setVisible(true);
	}
}
