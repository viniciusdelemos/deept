package controller;

import gui.GUIAddUser;
import gui.GUILoginDeepTwitter;
import gui.GUIMainWindow;
import gui.GUINewUpdate;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class ControllerDeepTwitter {
	private static GUILoginDeepTwitter loginWindow;
	private static GUIMainWindow mainWindow;
	private GUIAddUser guiAddUser;
	private static GUINewUpdate guiNewUpdate;
	private static Twitter twitter;
	private static GraphicManager gManager;
	private static boolean isTwitterUser;
	private static String loggedUserId;
	private JTabbedPane windowTabs;
	private static StatusTabManager tabManager;
	private static MainWindowListener mainWindowListener;
	
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
	
	public static boolean isTwitterUser() {
		return isTwitterUser;
	}
	
	public static String getUserName(String id) {
		return gManager.getUserName(Integer.parseInt(id));
	}
	
	public static void searchAndAddUserToNetwork(User u) {
		gManager.searchAndAddUserToNetwork(u);
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
	
	public static void selectTab(int index) {
		mainWindow.getTabs().setSelectedIndex(index);
	}
	
	class LoginListener implements ActionListener {	
		
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
					
					mainWindowListener = new MainWindowListener();
					mainWindow = new GUIMainWindow("DeepTwitter");
					mainWindow.addMainWindowListener(mainWindowListener);
										
					final JSplitPane jSplitPane = mainWindow.getSplitPane();					
					windowTabs = mainWindow.getTabs();
					System.out.println(windowTabs.getTabCount());
										
					tabManager = new StatusTabManager();
					tabManager.setTabbedPane(windowTabs);
					tabManager.addTab(StatusesType.UPDATES,"Atualizações"); //1
					tabManager.addTab(StatusesType.REPLIES,"@Replies"); //2
					tabManager.addTab(StatusesType.FAVORITES,"Favoritos"); //3
					tabManager.addTab(StatusesType.DIRECT_MESSAGES,"Mensagens"); //4
					tabManager.addTab(StatusesType.PUBLIC_TIMELINE, "Public Timeline"); //5		
					
					if(!isTwitterUser) {
						tabManager.setEnabledAt(2, false); //replies
						tabManager.setEnabledAt(3, false); //favoritos
						tabManager.setEnabledAt(4, false); //mensagens
					}						
					
					windowTabs.addChangeListener(new ChangeListener(){
						@Override
						public void stateChanged(ChangeEvent e) {
							StatusesTable table = null;
							int tabIndex = windowTabs.getSelectedIndex();							
							//index 0 != Tab, é a tab de menu
							if(tabIndex == 0) {
								jSplitPane.setDividerLocation(250);
								return;
							}
							
							StatusTab selectedTab = tabManager.getTab(tabIndex);	
							
							switch(selectedTab.getType()) {							
							case UPDATES:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;								
								if(isTwitterUser)
									table = new StatusesTable(StatusesType.UPDATES);
								else
									table = new StatusesTable(getLoggedUserId());
								break;
								
							case REPLIES:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
								table = new StatusesTable(StatusesType.REPLIES);
								break;
								
							case FAVORITES:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
								table = new StatusesTable(StatusesType.FAVORITES);
								break;
								
							case DIRECT_MESSAGES:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
								table = new StatusesTable(StatusesType.DIRECT_MESSAGES);
								break;
								
							case PUBLIC_TIMELINE:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
								table = new StatusesTable(StatusesType.PUBLIC_TIMELINE);
								break;								
							}		
							selectedTab.setPanelContent(table);
						}						
					});
					
					//adicionar tab de forças?
					
					mainWindow.addWindowListener(new WindowAdapter() {
						@Override
						 public void windowClosed(java.awt.event.WindowEvent arg0) {
							tabManager.getTab(StatusesType.UPDATES).stopThreads();
							tabManager.getTab(StatusesType.REPLIES).stopThreads();
							tabManager.getTab(StatusesType.FAVORITES).stopThreads();
							tabManager.getTab(StatusesType.DIRECT_MESSAGES).stopThreads();
							tabManager.getTab(StatusesType.PUBLIC_TIMELINE).stopThreads();
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
				if(ex.getStatusCode()==400)
					ControllerDeepTwitter.showMessageDialog("Você excedeu o número máximo de 100 requisições por hora permitido pelo Twitter. Aguarde e tente novamente.",MessageType.ERROR);
				else if(ex.getStatusCode()==401)
					showMessageDialog("Nome de usuário ou senha inválidos!",MessageType.ERROR);				
				else if(ex.getStatusCode()==404)
					showMessageDialog("Usuário não encontrado!",MessageType.ERROR);
				else if(ex.getStatusCode()==406)
					showMessageDialog("Nome de usuário inválido!",MessageType.ERROR);
				else if(ex.getStatusCode()==-1)
					showMessageDialog("A conexão não pôde ser estabelecida.",MessageType.ERROR);
				else 
					showMessageDialog(ex.getMessage(),MessageType.ERROR);
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			} catch(Exception e2) {
				showMessageDialog(e2.getMessage(), MessageType.ERROR);
				e2.printStackTrace();
			}			
		}
	}
	
	public static void openUpdateWindow() {
		if(guiNewUpdate == null) {
			guiNewUpdate = new GUINewUpdate();
			guiNewUpdate.addMainWindowListener(mainWindowListener);
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
	
	public static StatusTabManager getStatusTabManager() {
		return tabManager;
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
				openUpdateWindow();				
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
			else if(cmd.equals("buttonClearSelection")) {
				gManager.clearSelection();
			}
			else if(cmd.equals("checkBoxHighQuality")) {						
				gManager.setHighQuality(mainWindow.isHighQuality());					
			}	
		}
	}
}
