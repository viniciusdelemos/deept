package controller;

import gui.GUIAddUser;
import gui.GUILoginDeepTwitter;
import gui.GUIMainWindow;
import gui.GUINewUpdate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.GraphicManager;
import model.MessageType;
import model.StatusesTable;
import model.StatusesType;
import prefuse.Display;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.GraphMLWriter;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
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
	
	public static void showMessageDialog(String message,MessageType type) {
		if(type==MessageType.WARNING)
			JOptionPane.showMessageDialog(null, getFormattedMessage(message),"Atenção",JOptionPane.WARNING_MESSAGE,null);
		else if(type==MessageType.ERROR) 
			JOptionPane.showMessageDialog(null, getFormattedMessage(message),"Erro",JOptionPane.ERROR_MESSAGE,null);
		else if(type==MessageType.INFORMATION)
			JOptionPane.showMessageDialog(null, getFormattedMessage(message),"Informação",JOptionPane.INFORMATION_MESSAGE,null);
		else
			JOptionPane.showMessageDialog(null, getFormattedMessage(message),"Atenção",JOptionPane.QUESTION_MESSAGE,null);
	}
	
	public static void setStatusBarMessage(String message) {
		mainWindow.setStatusBarMessage(message);
	}
	
	class LoginListener implements ActionListener {				
		JScrollPane scrollUpdates;
		StatusesTable userTimeline, userReplies, userFavorites, publicTimeline;
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
							else showMessageDialog("Nome de usuário ou senha inválidos!",MessageType.ERROR);
					}
					else showMessageDialog("Por favor, preencha os campos de nome de usuário e senha!",MessageType.WARNING);					
				}
				else 
				{
					if(loginWindow.getUser().compareTo("")>0)
					{
						twitter = new Twitter(loginWindow.getUser(),"");
						logInOK = true;
					}
					else showMessageDialog("Por favor, preencha o campo de nome de usuário!",MessageType.WARNING);					
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
										
					final JSplitPane jSplitPane = mainWindow.getSplitPane();					
					jTabs = mainWindow.getTabs();
					if(!isTwitterUser) {
						jTabs.setEnabledAt(2, false); //replies
						jTabs.setEnabledAt(3, false); //favoritos
						jTabs.setEnabledAt(4, false); //mensagens
					}
					
					ForceSimulator forceSimulator = gManager.getForceDirectedLayout().getForceSimulator();										
					JForcePanel jForcePanel = new JForcePanel(forceSimulator);					
					jTabs.addTab("Simulador de Forças", jForcePanel);		
					
					jTabs.addChangeListener(new ChangeListener(){
						@Override
						public void stateChanged(ChangeEvent e) {
							int tabIndex = jTabs.getSelectedIndex();
							
							if(tabIndex==0) {
								jSplitPane.setDividerLocation(250);
								return;
							}
							else if(tabIndex==6) {
								jSplitPane.setDividerLocation(300);
								return;
							}
							
							scrollUpdates = (JScrollPane) jTabs.getComponent(tabIndex);
							
							if(tabIndex==1) {
								jSplitPane.setDividerLocation(431);
								if(userTimeline!=null) return;
								if(isTwitterUser)
									userTimeline = new StatusesTable(gManager,StatusesType.MY_UPDATES);
								else
									userTimeline = new StatusesTable(gManager,loggedUserId);
								scrollUpdates.setViewportView(userTimeline.getContent());
							}
							else if(tabIndex==2) {
								jSplitPane.setDividerLocation(431);
								if(userReplies!=null) return;
								userReplies = new StatusesTable(gManager,StatusesType.REPLIES);
								scrollUpdates.setViewportView(userReplies.getContent());
							}
							else if(tabIndex==3) {
								jSplitPane.setDividerLocation(431);
								if(userFavorites!=null) return;
								userFavorites = new StatusesTable(gManager,StatusesType.FAVORITES);
								scrollUpdates.setViewportView(userFavorites.getContent());
							}
							else if(tabIndex==4) {
								//TODO msgs
							}
							else if(tabIndex==5) {
								jSplitPane.setDividerLocation(431);
								if(publicTimeline!=null) return;
								publicTimeline = new StatusesTable(gManager,StatusesType.PUBLIC_TIMELINE);
								scrollUpdates.setViewportView(publicTimeline.getContent());
							}														
						}						
					});
					
					mainWindow.addWindowListener(new WindowAdapter() {
						@Override
						 public void windowClosed(java.awt.event.WindowEvent arg0) {
							if(userTimeline!=null) {
								userTimeline.stopThread();
								userTimeline = null;
							}								
							if(userReplies!=null) {
								userReplies.stopThread();
								userReplies = null;
							}
							if(userFavorites!=null) {
								userFavorites.stopThread();
								userFavorites = null;
							}
							if(publicTimeline!=null) {
								publicTimeline.stopThread();
								publicTimeline = null;
							}
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
					showMessageDialog("Nome de usuário ou senha inválidos!",MessageType.ERROR);				
				else if(ex.getStatusCode()==404)
					showMessageDialog("Usuário não encontrado!",MessageType.ERROR);
				else if(ex.getStatusCode()==406)
					showMessageDialog("Nome de usuário inválido!",MessageType.ERROR);
				else if(ex.getStatusCode()==-1)
					showMessageDialog("A conexão não pôde ser estabelecida.",MessageType.ERROR);
				else 
					showMessageDialog(ex.getMessage(),MessageType.ERROR);
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
								showMessageDialog(e1.getMessage(),MessageType.ERROR);
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
								showMessageDialog(e1.getMessage(),MessageType.ERROR);
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
						showMessageDialog(e1.getMessage(),MessageType.ERROR);						
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
						else showMessageDialog("Por favor, preencha o campo!",MessageType.WARNING);						
					}
					catch(TwitterException ex) {
						showMessageDialog(ex.getMessage(),MessageType.WARNING);					
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
