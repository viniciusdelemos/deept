package main;

import gui.GUILoginDeepTwitter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import controller.ControllerDeepTwitter;

public class DeepTwitter {		
	
	public static void main(String[] args) {		
		GUILoginDeepTwitter loginWindow = new GUILoginDeepTwitter();	
		
		try { 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {			
			e.printStackTrace();						
		}
		
		ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance(); 
		controller.setLoginWindow(loginWindow);	
		SwingUtilities.updateComponentTreeUI(loginWindow);
		loginWindow.setVisible(true);
		loginWindow.setLocationRelativeTo(null);
	}
}
