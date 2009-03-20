package controller;

import gui.GUIAddUser;
import gui.GUILoginDeepTwitter;
import gui.GUIMainWindow;
import gui.GUINewUpdate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ChartColor;
import model.GraphicManager;
import model.UserTimeline;
import prefuse.Display;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.GraphMLWriter;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.visual.VisualItem;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class ControllerDeepTwitter {
	private GUILoginDeepTwitter loginWindow;
	private static GUIMainWindow mainWindow;
	private GUIAddUser guiAddUser;
	private GUINewUpdate guiNewUpdate;
	private static Twitter twitter;
	private GraphicManager gManager;
	private boolean isTwitterUser;
	private static String loggedUserId;

	public ControllerDeepTwitter(GUILoginDeepTwitter loginWindow)
	{
		this.loginWindow = loginWindow;
		loginWindow.addLoginListener(new LoginListener());
	}
	
	public static Twitter getTwitter() {
		return twitter;
	}
	
	public static String getLoggedUserId() {
		return loggedUserId;
	}
	
	private static String getFormattedMessage(String message)
	{
		try {
			int index1 = message.indexOf("<error>");
			int index2 = message.indexOf("</error>");		
			return message.substring(index1+7, index2);
		} catch (Exception e) {
			return message;
		}
	}
	
	public static void showMessageDialog(String messageType, String message) {
		if(messageType!=null && messageType.equals("warning")) 
			JOptionPane.showMessageDialog(null, getFormattedMessage(message),"Atenção",JOptionPane.WARNING_MESSAGE,null);
		else 
			JOptionPane.showMessageDialog(null, getFormattedMessage(message),"Erro",JOptionPane.ERROR_MESSAGE,null);
	}
	
	public static void setStatusBarMessage(String message) {
		mainWindow.setStatusBarMessage(message);
	}
	
	class LoginListener implements ActionListener {				
		JScrollPane scrollUpdates;
		UserTimeline userTimeline;
		JTabbedPane jTabs;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean logInOK = false;
			isTwitterUser = loginWindow.isTwitterUser();
			try{
				if(isTwitterUser)
				{				
					if(loginWindow.getUser().compareTo("")>0 && loginWindow.getPassword().compareTo("")>0)
					{											
						twitter = new Twitter(loginWindow.getUser(),loginWindow.getPassword());
						if(twitter.verifyCredentials()) logInOK = true;
							else showMessageDialog(null, "Nome de usuário ou senha inválidos!");
					}
					else showMessageDialog("warning", "Por favor, preencha os campos de nome de usuário e senha!");					
				}
				else 
				{
					if(loginWindow.getUser().compareTo("")>0)
					{
						twitter = new Twitter(loginWindow.getUser(),"");
						logInOK = true;
					}
					else showMessageDialog("warning","Por favor, preencha o campo de nome de usuário!");					
				}

				if(logInOK)
				{
					User u = twitter.getAuthenticatedUser();
					loggedUserId = String.valueOf(u.getId());
					gManager = new GraphicManager(isTwitterUser);						
					//User u2 = twitter.getAuthenticatedUser();
					//gManager.addNode(u2);
					loginWindow.dispose();
					
					mainWindow = new GUIMainWindow("DeepTwitter");
					mainWindow.addMainWindowListener(new MainWindowListener());
										
					JSplitPane jSplitPane = mainWindow.getSplitPane();					
					jTabs = mainWindow.getTabs();
					ForceSimulator forceSimulator = gManager.getForceDirectedLayout().getForceSimulator();										
					JForcePanel jForcePanel = new JForcePanel(forceSimulator);					
					jTabs.addTab("Simulador de Forças", jForcePanel);		
					
					scrollUpdates = (JScrollPane) jTabs.getComponent(1);					
					userTimeline = null;
					
					jTabs.addChangeListener(new ChangeListener(){
						@Override
						public void stateChanged(ChangeEvent e) {
							if(jTabs.getSelectedIndex()!=1) return;	
							if(userTimeline!=null) return;
							userTimeline = new UserTimeline(loggedUserId,isTwitterUser);
							//padrao é baixar 100 atualizacoes (para userTimeline)
							//userTimeline.setNumberOfUpdatesToGet(200);
							scrollUpdates.setViewportView(userTimeline.getContent());		
						}						
					});
					
					mainWindow.addWindowListener(new WindowAdapter() {
						@Override
						 public void windowClosed(java.awt.event.WindowEvent arg0) {
							if(userTimeline!=null)
								userTimeline.stopThread();
						}
					});
					
					jSplitPane.setRightComponent((Display)gManager);		
					
					SwingUtilities.updateComponentTreeUI(mainWindow);
					mainWindow.setVisible(true);
					
					gManager.addNode(u);
					//gManager.getVisualization().run("layout"); 					
					//gManager.getVisualization().repaint();					
				}	
			} catch (TwitterException ex) {						
				if(ex.getStatusCode()==401)
					showMessageDialog(null, "Nome de usuário ou senha inválidos!");				
				else if(ex.getStatusCode()==404)
					showMessageDialog(null, "Usuário não encontrado!");
				else if(ex.getStatusCode()==406)
					showMessageDialog(null, "Nome de usuário inválido!");
				else if(ex.getStatusCode()==-1)
					showMessageDialog(null, "A conexão não pôde ser estabelecida.");
				else 
					showMessageDialog(null, ex.getMessage());
			}
		}
	}
	
	class MainWindowListener implements ActionListener {		
		@Override
		public void actionPerformed(ActionEvent e) {			
			if(e.getSource() instanceof JMenuItem)
			{
				String menuName = ((JMenuItem)e.getSource()).getName();
				
				if(menuName.equals("menuLoadNetwork") || menuName.equals("menuSaveNetwork")) {
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
					chooser.setFileFilter(filter);
					
					if(menuName.equals("menuLoadNetwork")) {
						GraphMLReader reader = new GraphMLReader();
						int returnVal = chooser.showOpenDialog(mainWindow);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							String fileName = chooser.getSelectedFile().getAbsolutePath();	
							try {
								Graph g = reader.readGraph(fileName);
								gManager.setGraph(g);
							} catch (DataIOException e1) {
								showMessageDialog(null, e1.getMessage());
							}
						}
					}
					else {
						GraphMLWriter writer = new GraphMLWriter();
						int returnVal = chooser.showSaveDialog(mainWindow);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							String fileName = chooser.getSelectedFile().getAbsolutePath();
							try {
								writer.writeGraph(gManager.getGraph(), fileName);
							} catch (DataIOException e1) {
								showMessageDialog(null, e1.getMessage());
							}
						}
					}					
				}				
				else if(menuName.equals("menuSaveNetworkAs")) {
					//TODO
					System.out.println("Save Network As...");
				}
				else if(menuName.equals("menuLogout")) {
					mainWindow.dispose();
					loginWindow.setVisible(true);
				}
				else if(menuName.equals("menuCheckBoxStatusBar")) {
					mainWindow.setStatusBarVisible(mainWindow.isStatusBarVisible());
				}
				else if(menuName.equals("menuHelp")) {
					//TODO
					System.out.println("Open Help");
				}
			}
			if(e.getSource() instanceof JButton)
			{
				String buttonName = ((JButton)e.getSource()).getName();
				
				if(buttonName.equals("buttonNewUpdate")) {					
					guiNewUpdate = new GUINewUpdate();
					guiNewUpdate.addMainWindowListener(this);
					guiNewUpdate.setVisible(true);
				}
				else if(buttonName.equals("buttonUpdate")) {
					try {
						twitter.update(guiNewUpdate.getStatus());
						guiNewUpdate.dispose();
						Date now = Calendar.getInstance().getTime();
						setStatusBarMessage("Update realizado com sucesso em "+now);
					} catch (TwitterException e1) {
						showMessageDialog(null,e1.getMessage());						
					}					
				}
				else if(buttonName.equals("buttonNewGroup")) {
					gManager.createGroup();
				}
				else if(buttonName.equals("buttonAddUser")) {
					guiAddUser = new GUIAddUser();
					guiAddUser.addMainWindowListener(this);
					guiAddUser.setVisible(true);
				}
				else if(buttonName.equals("buttonOKAddUser")) {
					try{
						String id = guiAddUser.getUser();
						if(!id.equals("")) {
							System.out.println("=> Requesting user to Twitter");
							User u = (User)twitter.getUserDetail(id);
							System.out.println("=> Got user");
							gManager.searchAndAddUserToNetwork(u);								
							guiAddUser.dispose();
						}
						else showMessageDialog("warning","Por favor, preencha o campo!");						
					}
					catch(TwitterException ex) {
						showMessageDialog(null, ex.getMessage());					
					}
				}
				else if(buttonName.equals("buttonClearSelection")) {
					gManager.clearSelection();
				}
			}
			else if (e.getSource() instanceof JCheckBox)
			{
				String checkBoxName = ((JCheckBox)e.getSource()).getName();
				
				if(checkBoxName.equals("checkBoxHighQuality")) {						
					gManager.setHighQuality(mainWindow.isHighQuality());					
				}				
			}
		}
		
	}
}
