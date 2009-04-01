package controller;

import gui.GUIAddUser;
import gui.GUILoginDeepTwitter;
import gui.GUIMainWindow;
import gui.GUINewUpdate;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
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
	private static GUILoginDeepTwitter loginWindow;
	private static GUIMainWindow mainWindow;
	private GUIAddUser guiAddUser;
	private GUINewUpdate guiNewUpdate;
	private static Twitter twitter;
	private static GraphicManager gManager;
	private static boolean isTwitterUser;
	private static String loggedUserId;
	private static JTabbedPane windowTabs;
	private static JScrollPane scrollUpdates;
	private static Map<String,JPanel> panelsMap;
	private static int currentPanel;
	private static ArrayList<String> idArray;

	public ControllerDeepTwitter(GUILoginDeepTwitter loginWindow)
	{
		this.loginWindow = loginWindow;		
		loginWindow.addLoginListener(new LoginListener());
		panelsMap = new HashMap<String,JPanel>();
		currentPanel = 0;
		idArray = new ArrayList<String>();
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
	
	public static void showMessageDialog(String message, MessageType type) {
		Component parent = mainWindow;
		if(parent == null)
			parent = loginWindow;
		
		switch(type) {
		case WARNING:
			JOptionPane.showMessageDialog(parent, getFormattedMessage(message),"Atenção",JOptionPane.WARNING_MESSAGE,null);
			break;
		case ERROR:
			JOptionPane.showMessageDialog(parent, getFormattedMessage(message),"Erro",JOptionPane.ERROR_MESSAGE,null);
			break;
		case INFORMATION: 
			JOptionPane.showMessageDialog(parent, getFormattedMessage(message),"Informação",JOptionPane.INFORMATION_MESSAGE,null);
			break;
		default:
			JOptionPane.showMessageDialog(parent, getFormattedMessage(message),"Atenção",JOptionPane.QUESTION_MESSAGE,null);
		}	
	}
	
	public static void setStatusBarMessage(String message) {
		mainWindow.setStatusBarMessage(message);
	}
	
	public static void setPanelContent(String userId) {
		StatusesTable updatesTable;
		scrollUpdates = (JScrollPane)(((JPanel)windowTabs.getComponent(1)).getComponent(1));
		JPanel selectedPanel = panelsMap.get(userId);
		if(selectedPanel == null) {
			if(isTwitterUser && userId.equals(loggedUserId))
				updatesTable = new StatusesTable(gManager,StatusesType.MY_UPDATES);				
			else
				updatesTable = new StatusesTable(gManager,userId);
			
			selectedPanel = updatesTable.getContent();
			panelsMap.put(userId,selectedPanel);						 
			scrollUpdates.setViewportView(selectedPanel);
			
			idArray.add(userId);
			currentPanel = idArray.size()-1;
			reconfigButtons();
		}
		else {
			scrollUpdates.setViewportView(selectedPanel);
			scrollUpdates.revalidate();
		}
		mainWindow.setCurrentUserLabel(gManager.getUserName(Integer.parseInt(userId)));
	}
	
	private static void reconfigButtons() {
		if(idArray.size()<=1) {
			mainWindow.setPreviousUserEnabled(false);
			mainWindow.setNextUserEnabled(false);
			mainWindow.setCloseUserEnabled(false);
		}
		else if(currentPanel==idArray.size()-1) {
			mainWindow.setPreviousUserEnabled(true);
			mainWindow.setNextUserEnabled(false);
			mainWindow.setCloseUserEnabled(true);
		}			
		else if(currentPanel==0) {
			mainWindow.setPreviousUserEnabled(false);
			mainWindow.setNextUserEnabled(true);
			mainWindow.setCloseUserEnabled(false);
		}
		else {
			mainWindow.setPreviousUserEnabled(true);
			mainWindow.setNextUserEnabled(true);
			mainWindow.setCloseUserEnabled(true);
		}
	}
	
	
	
	class LoginListener implements ActionListener {	
		StatusesTable userTimeline, userReplies, userFavorites, publicTimeline;		
		
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
					windowTabs = mainWindow.getTabs();
					
					if(!isTwitterUser) {
						windowTabs.setEnabledAt(2, false); //replies
						windowTabs.setEnabledAt(3, false); //favoritos
						windowTabs.setEnabledAt(4, false); //mensagens
					}						
					
					windowTabs.addChangeListener(new ChangeListener(){
						@Override
						public void stateChanged(ChangeEvent e) {
							int tabIndex = windowTabs.getSelectedIndex();
							
							if(tabIndex==0) {
								jSplitPane.setDividerLocation(250);
								return;
							}
							else if(tabIndex==6) {
								jSplitPane.setDividerLocation(300);
								return;
							}
							
							if(tabIndex==1) {
								jSplitPane.setDividerLocation(431);
								setPanelContent(getLoggedUserId());	
								return;
							}
							//TODO
							scrollUpdates = (JScrollPane) windowTabs.getComponent(tabIndex);							
							
							if(tabIndex==2) {
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

			String cmd = e.getActionCommand();

			if(cmd.equals("menuLoadNetwork") || cmd.equals("menuSaveNetwork")) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
				chooser.setFileFilter(filter);

				if(cmd.equals("menuLoadNetwork")) {
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
			else if(cmd.equals("menuSaveNetworkAs")) {
				//TODO
				System.out.println("Save Network As...");
			}
			else if(cmd.equals("menuLogout")) {
				mainWindow.dispose();
				mainWindow = null;
				loginWindow.setVisible(true);
			}
			else if(cmd.equals("menuCheckBoxStatusBar")) {
				mainWindow.setStatusBarVisible(mainWindow.isStatusBarVisible());
			}
			else if(cmd.equals("menuHelp")) {
				//TODO
				System.out.println("Open Help");
			}
			else if(cmd.equals("buttonNewUpdate")) {					
				if(guiNewUpdate == null) {
					guiNewUpdate = new GUINewUpdate();
					guiNewUpdate.addMainWindowListener(this);
					guiNewUpdate.addWindowListener(new WindowAdapter() {
						@Override
						 public void windowClosed(java.awt.event.WindowEvent arg0) {
							guiNewUpdate = null;
						}
					});
					guiNewUpdate.setVisible(true);
				}
				else
					guiNewUpdate.requestFocus();
				guiNewUpdate.setLocationRelativeTo(mainWindow);				
			}
			else if(cmd.equals("buttonUpdate")) {
				try {
					twitter.update(guiNewUpdate.getStatus());
					guiNewUpdate.dispose();
					Date now = Calendar.getInstance().getTime();
					setStatusBarMessage("Update realizado com sucesso em "+now);
				} catch (TwitterException e1) {
					showMessageDialog(e1.getMessage(),MessageType.ERROR);						
				}					
			}
			else if(cmd.equals("buttonNewGroup")) {
				gManager.createGroup();
			}
			else if(cmd.equals("buttonAddUser")) {
				if(guiAddUser == null) {
					guiAddUser = new GUIAddUser();
					guiAddUser.addMainWindowListener(this);
					guiAddUser.addWindowListener(new WindowAdapter() {
						@Override
						 public void windowClosed(java.awt.event.WindowEvent arg0) {
							guiAddUser = null;
						}
					});
					guiAddUser.setVisible(true);
				}
				else
					guiAddUser.requestFocus();
				guiAddUser.setLocationRelativeTo(mainWindow);
			}
			else if(cmd.equals("buttonOKAddUser")) {
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
				catch(NullPointerException ex) {
					showMessageDialog("Usuário não encontrado!",MessageType.WARNING);					
				}
			}
			else if(cmd.equals("buttonPreviousUser")) {
				currentPanel--;
				reconfigButtons();
				String userId = idArray.get(currentPanel);
				setPanelContent(userId);
			}
			else if(cmd.equals("buttonNextUser")) {
				currentPanel++;
				reconfigButtons();
				String userId = idArray.get(currentPanel);				
				setPanelContent(userId);
			}
			else if(cmd.equals("buttonCloseUpdates")) {
				panelsMap.remove(idArray.get(currentPanel));
				if(currentPanel==idArray.size()-1) {
					idArray.remove(currentPanel);
					currentPanel--;
				}
				else
					idArray.remove(currentPanel);
				
				String userId = idArray.get(currentPanel);
				setPanelContent(userId);								
				reconfigButtons();								
			}
			else if(cmd.equals("buttonTurnOnOff")) {
				//TODO
				if(((JToggleButton)e.getSource()).isSelected()) System.out.println("TURNED ON");
				else System.out.println("TURNED OFF");
			}
			else if(cmd.equals("buttonSettings")) {
				//TODO
			}
			else if(cmd.equals("buttonClearSelection")) {
				gManager.clearSelection();
			}
			else if(cmd.equals("checkBoxHighQuality")) {						
				gManager.setHighQuality(mainWindow.isHighQuality());					
			}	
		}
	}
}
