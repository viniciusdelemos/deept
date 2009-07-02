package controller;

import gui.GUIAddUser;
import gui.GUICategoryEditor;
import gui.GUILoginDeepTwitter;
import gui.GUIMainWindow;
import gui.GUIMostPopularChoice;
import gui.GUIMostPopularUsers;
import gui.GUINewUpdate;
import gui.visualizations.MostPopularUsersView;
import gui.visualizations.NetworkView;
import gui.visualizations.MostPopularUsersView.ShowingBy;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.MessageType;
import model.StatusTab;
import model.StatusesType;
import model.threads.StatusesTableThread;
import prefuse.Display;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.GraphMLWriter;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class ControllerDeepTwitter {
	private GUILoginDeepTwitter loginWindow;
	private GUIMainWindow mainWindow;
	private GUIAddUser guiAddUser;
	private GUINewUpdate guiNewUpdate;
	private GUIMostPopularUsers guiMostPopular;
	private GUIMostPopularChoice choiceFrame;
	private Twitter twitter;
	private NetworkView networkView;
	private MostPopularUsersView activeUsersDisplay;
	private boolean isTwitterUser;
	private String loggedUserId;
	private JTabbedPane windowTabs;
	private StatusTabManager tabManager;
	private MainWindowListener mainWindowListener;
	private UpdateRateLimitThread updateRateLimit;
	private long rateLimitSleepTime = 60000; //padrao 1min
	
	private ControllerDeepTwitter(){
		//construtor private previne chamadas nao autorizadas ao construtor.		
	}
	
	private static class SingletonHolder { 
		private final static ControllerDeepTwitter INSTANCE = new ControllerDeepTwitter();
	}

	public static ControllerDeepTwitter getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void setLoginWindow(GUILoginDeepTwitter loginWindow) {
		this.loginWindow = loginWindow;		
		loginWindow.addLoginListener(new LoginListener());				
	}
	
	public Twitter getTwitter() {
		return twitter;
	}
	
	public String getLoggedUserId() {
		return loggedUserId;
	}
	
	public boolean isTwitterUser() {
		return isTwitterUser;
	}
	
	public String getUserName(String id) {
		try{
			return networkView.getUserName(Integer.parseInt(id));
		}
		catch(NumberFormatException e) {
			return id;
		}
	}
	
	public Node getNode(int id) {
		return networkView.getNodeByTwitterId(id);
	}
	
	public VisualItem getNodeItem(Node node) {
		return networkView.getNodeItem(node);
	}
	
	public void searchAndAddUserToNetwork(User u) {
		networkView.searchAndAddUserToNetwork(u);
	}
	
	public void searchAndAddUserToNetwork(int id) {
		try {
			User u = networkView.getUser(id);
			if(u == null) 
				searchAndAddUserToNetwork(getTwitter().getUserDetail(String.valueOf(id)));			
			else
				searchAndAddUserToNetwork(u);	
			
		} catch (TwitterException e) {
			showMessageDialog("Usuário não encontrado!",MessageType.WARNING);
		}
	}
	
	private static String getFormattedMessage(String message)
	{
		try {
			int index1 = message.indexOf("<error>"); //tam=7
			int index2 = message.indexOf("</error>");		
			return message.substring(index1+7, index2);
		} catch (Exception e) {
			return message;
		}
	}
	
	public void showMessageDialog(String message, MessageType type) {
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
	
	public synchronized void setStatusBarMessage(String message) {
		mainWindow.setStatusBarMessage(message);
	}
	
	public void selectTab(int index) {
		mainWindow.getTabs().setSelectedIndex(index);
	}
	
	class LoginListener implements ActionListener {	
		
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean logInOK = false;
			isTwitterUser = loginWindow.isTwitterUser();
			
			User user = null;
			
			try{
				if(isTwitterUser)
				{				
					if(loginWindow.getUser().compareTo("")>0 && loginWindow.getPassword().compareTo("")>0)
					{
						twitter = new Twitter(loginWindow.getUser(),loginWindow.getPassword());						
						user = twitter.verifyCredentials(); //Se usuario ou senha invalido,
						//gerado excecao com statusCode = 401						
						logInOK = true;
						//	else showMessageDialog("Nome de usuário ou senha inválidos!",MessageType.ERROR);
					}
					else showMessageDialog("Por favor, preencha os campos de nome de usuário e senha!",MessageType.WARNING);					
				}
				else 
				{
					if(loginWindow.getUser().compareTo("")>0)
					{
						twitter = new Twitter(loginWindow.getUser(),"");
						user = twitter.getUserDetail(twitter.getUserId());
						logInOK = true;
					}
					else showMessageDialog("Por favor, preencha o campo de nome de usuário!",MessageType.WARNING);					
				}

				if(logInOK)
				{
					loggedUserId = String.valueOf(user.getId());
					loginWindow.dispose();					
										
					mainWindow = new GUIMainWindow("DeepTwitter");
					mainWindowListener = new MainWindowListener();
					mainWindow.addMainWindowListener(mainWindowListener);
					
					networkView = new NetworkView();	
										
					final JSplitPane jSplitPane = mainWindow.getSplitPane();					
					windowTabs = mainWindow.getTabs();
					
					windowTabs.remove(0);
					
					tabManager = new StatusTabManager();
					tabManager.setTabbedPane(windowTabs);
					tabManager.addTab(StatusesType.UPDATES,"Atualizações"); //0
					tabManager.addTab(StatusesType.REPLIES,"@"+user.getScreenName()); //1
					tabManager.addTab(StatusesType.FAVORITES,"Favoritos"); //2
					tabManager.addTab(StatusesType.DIRECT_MESSAGES,"Mensagens"); //3
					tabManager.addTab(StatusesType.SEARCH, "Busca"); //4
					tabManager.addTab(StatusesType.PUBLIC_TIMELINE, "Public Timeline"); //5		
					
					if(!isTwitterUser) {
						tabManager.setEnabledAt(1, false); //replies
						tabManager.setEnabledAt(2, false); //favoritos
						tabManager.setEnabledAt(3, false); //mensagens
					}
					
					windowTabs.addChangeListener(new ChangeListener(){
						@Override
						public void stateChanged(ChangeEvent e) {
							jSplitPane.setDividerLocation(431);
							StatusesTableThread table = null;
							int tabIndex = windowTabs.getSelectedIndex();							
							StatusTab selectedTab = tabManager.getTab(tabIndex);	
							if(selectedTab.isActive()) return;
							
							switch(selectedTab.getType()) {							
							case UPDATES:																
								if(isTwitterUser)
									table = new StatusesTableThread(StatusesType.UPDATES);
								else
									table = new StatusesTableThread(StatusesType.UPDATES,getLoggedUserId());
								break;
								
							case REPLIES:
								table = new StatusesTableThread(StatusesType.REPLIES);
								break;
								
							case FAVORITES:
								table = new StatusesTableThread(StatusesType.FAVORITES);
								break;
								
							case DIRECT_MESSAGES:
								table = new StatusesTableThread(StatusesType.DIRECT_MESSAGES_RECEIVED);
								break;
							case SEARCH:
								return;							
							case PUBLIC_TIMELINE:
								table = new StatusesTableThread(StatusesType.PUBLIC_TIMELINE);
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
							tabManager.getTab(StatusesType.SEARCH).stopThreads();
							tabManager.getTab(StatusesType.PUBLIC_TIMELINE).stopThreads();
							updateRateLimit.interrupt();
						}
					});
					
					jSplitPane.setRightComponent((Display)networkView);	
					jSplitPane.setDividerLocation(431);
					
					SwingUtilities.updateComponentTreeUI(mainWindow);
					mainWindow.setLocationRelativeTo(null);
					mainWindow.setVisible(true);					
					
					networkView.addNode(user);
					
					if(isTwitterUser)
						tabManager.getTab(0).setPanelContent(new StatusesTableThread(StatusesType.UPDATES));
					else
						tabManager.getTab(0).setPanelContent(new StatusesTableThread(StatusesType.UPDATES,getLoggedUserId()));
					
					updateRateLimit = new UpdateRateLimitThread(rateLimitSleepTime);				
				}	
			} catch (TwitterException ex) {
				if(ex.getStatusCode()==400)
					showMessageDialog("Você excedeu o número máximo de 100 requisições por hora permitido pelo Twitter. Aguarde e tente novamente.",MessageType.ERROR);
				else if(ex.getStatusCode()==401)
					showMessageDialog("Nome de usuário ou senha inválidos!",MessageType.ERROR);				
				else if(ex.getStatusCode()==404)
					showMessageDialog("Usuário não encontrado!",MessageType.ERROR);
				else if(ex.getStatusCode()==406)
					showMessageDialog("Nome de usuário inválido!",MessageType.ERROR);
				else if(ex.getStatusCode()==-1)
					showMessageDialog("A conexão não pôde ser estabelecida.",MessageType.ERROR);
				else {
					System.out.println("Status Code: "+ex.getStatusCode()+"\nMessage: "+ex.getMessage());
					ex.printStackTrace();
					showMessageDialog(ex.getMessage(),MessageType.ERROR);
				}
			} catch(Exception e2) {
				showMessageDialog(e2.getMessage(), MessageType.ERROR);
				e2.printStackTrace();
			}			
		}
	}
	
	public void openGUINewUpdateWindow() {
		openGUINewUpdateWindow(null,null);
	}
	
	public void openGUINewUpdateWindow(String username, StatusesType type) {
		if(guiNewUpdate == null) {
			if(username==null) 
				guiNewUpdate = new GUINewUpdate();
			else
				guiNewUpdate = new GUINewUpdate(username,type);
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
	
	public void openGUIMostPopularUsersWindow(Node[] userArray) {
		guiMostPopular = new GUIMostPopularUsers("DeepTwitter - Usuários mais populares");
		if(userArray == null)
			userArray = networkView.getNodes();
		
		guiMostPopular.addListener(mainWindowListener);
		guiMostPopular.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent arg0) {
				guiMostPopular = null;
				//TODO matar a thread
			}
		});
		final JComboBox maxUsersComboBox = guiMostPopular.getComboBoxMaxUsers();
		for(int i=userArray.length; i>=1; i--) {
			maxUsersComboBox.addItem(i);
		}
		maxUsersComboBox.setSelectedIndex(0);
		maxUsersComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getItem()==maxUsersComboBox.getSelectedItem()) {
					activeUsersDisplay.setMaxUsers(Integer.parseInt(arg0.getItem().toString()));
				}			
			}			
		});
		activeUsersDisplay = new MostPopularUsersView(userArray,guiMostPopular.getEditor());
		JSplitPane splitPane = guiMostPopular.getSplitPane();
		splitPane.setTopComponent(activeUsersDisplay);		
		guiMostPopular.setVisible(true);
		splitPane.setDividerLocation(510);
	}
	
	public StatusTabManager getStatusTabManager() {
		return tabManager;
	}
	
	public JToolBar getMainToolBar() {
		return mainWindow.getMainToolBar();
	}
	
	public void setRateLimitSleepTime(long time) {
		rateLimitSleepTime = time;
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
							networkView.setGraph(g);
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
							writer.writeGraph(networkView.getGraph(), fileName);
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
			else if(cmd.equals("buttonLogout")) {
				mainWindow.dispose();
				mainWindow = null;
				loginWindow.setVisible(true);
			}
			else if(cmd.equals("buttonShowStatusBar")) {
				mainWindow.setStatusBarVisible(mainWindow.isStatusBarVisible());
			}
			else if(cmd.equals("buttonHelp")) {
				//TODO
				System.out.println("Open Help");
			}
			else if(cmd.equals("buttonNewUpdate")) {					
				openGUINewUpdateWindow();				
			}
			else if(cmd.equals("buttonUpdate")) {
				try {
					twitter.updateStatus(guiNewUpdate.getStatus());
					guiNewUpdate.dispose();
					Date now = Calendar.getInstance().getTime();
					setStatusBarMessage("Update realizado com sucesso em "+now);
				} catch (TwitterException e1) {
					showMessageDialog(e1.getMessage(),MessageType.ERROR);						
				}					
			}
			else if(cmd.equals("buttonNewGroup")) {
				networkView.getGroupManager().addGroup();
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
						User u = twitter.getUserDetail(id);
						System.out.println("=> Got user");
						networkView.searchAndAddUserToNetwork(u);								
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
				networkView.clearSelection();				
			}
			else if(cmd.equals("buttonHighQuality")) {						
				networkView.setHighQuality(mainWindow.isHighQuality());				
			}
			else if(cmd.equals("buttonPlayPauseVisualization")) {
				if(mainWindow.isVisualizationRunning())
					networkView.getVisualization().run("layout");
				else
					networkView.getVisualization().cancel("layout");
			}
			else if(cmd.equals("buttonCurvedEdges")) {
				networkView.setEdgeType(mainWindow.isCurvedEdges());
			}
			else if(cmd.equals("buttonToolTipControl")) {
				networkView.setToolTipControlOn(mainWindow.isToolTipControlOn());
			}
			else if(cmd.equals("buttonCenterUser")) {
				networkView.setCenterUserControlOn(mainWindow.isCenterUserOn());
			}
			else if(cmd.equals("buttonSettings")) {
				//TODO
				System.out.println("Settings");
			}
			else if(cmd.equals("orderByFriends")) {
				activeUsersDisplay.setSizeActionDataField(ShowingBy.friendsCount);
			}
			else if(cmd.equals("orderByFollowers")) {
				activeUsersDisplay.setSizeActionDataField(ShowingBy.followersCount);
			}
			else if(cmd.equals("orderByTweets")) {
				activeUsersDisplay.setSizeActionDataField(ShowingBy.statusesCount);
			}
			else if(cmd.equals("orderByFavorites")) {
				activeUsersDisplay.setSizeActionDataField(ShowingBy.favoritesCount);
			}
			else if(cmd.equals("buttonMostActive")) {
				if(networkView.getTupleSet(NetworkView.SELECTED_NODES).getTupleCount()>0) {
					choiceFrame = new GUIMostPopularChoice();
					choiceFrame.setLocationRelativeTo(mainWindow);
					choiceFrame.addMainWindowListener(mainWindowListener);
					choiceFrame.setVisible(true);
				}
				else
					openGUIMostPopularUsersWindow(null);
			}
			else if(cmd.equals("buttonOKMostPopularUsersChoice")) {
				if(choiceFrame.forSelectedUsersOnly()) {
					TupleSet tuples = networkView.getTupleSet(NetworkView.SELECTED_NODES);
					Node[] groupArray = new Node[tuples.getTupleCount()];
					Iterator<NodeItem> nodes = tuples.tuples();
					int cont = 0;
					while(nodes.hasNext()) {
						NodeItem next = nodes.next();
						groupArray[cont] = next;
						cont++;
					}
					choiceFrame.dispose();
					openGUIMostPopularUsersWindow(groupArray);					
				}
				else
					openGUIMostPopularUsersWindow(null);
			}
			else if(cmd.equals("buttonCategoryEditor")) {
				GUICategoryEditor.openFrame();
			}
		}
	}
	
	private class UpdateRateLimitThread extends Thread {
		long sleepTime;
		RateLimitStatus rlt;
		
		public UpdateRateLimitThread(long sleepTime) {
			this.sleepTime = sleepTime;
			start();
		}
		
		public void run() {
			while(true) {
				try {
					System.out.println("UPDATING RATE LIMIT");					
					rlt = twitter.rateLimitStatus();
					mainWindow.setRateLimitStatus(rlt.getRemainingHits(),rlt.getHourlyLimit(),rlt.getDateTime());
					Thread.sleep(sleepTime);
				} catch (Exception e) {					
					break;
				} 
			}
		}
	}	
}
