package controller;

import gui.GUIAddUser;
import gui.GUICategoryEditor;
import gui.GUILoginDeepTwitter;
import gui.GUIMainWindow;
import gui.GUIMostPopularChoice;
import gui.GUIMostPopularUsers;
import gui.GUINetworkForces;
import gui.GUINewUpdate;
import gui.GUISettings;
import gui.visualizations.MostPopularUsersView;
import gui.visualizations.NetworkView;
import gui.visualizations.MostPopularUsersView.ShowingBy;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ChartColor;
import model.ConfigurationType;
import model.MessageType;
import model.Settings;
import model.StatusTab;
import model.StatusesType;
import model.threads.StatusesTableThread;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import prefuse.Display;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.GraphMLWriter;
import prefuse.data.tuple.TupleSet;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
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
	private GUISettings guiSettings;
	private MostPopularUsersView activeUsersDisplay;
	private NetworkView networkView;
	private GUINetworkForces guiNetworkForces;
	
	private Twitter twitter;
	private boolean isTwitterUser;
	private String loggedUserId, loggedUsername;
	private JTabbedPane windowTabs;
	private StatusTabManager tabManager;
	private MainWindowListener mainWindowListener;
	private UpdateRateLimitThread updateRateLimit;
	
	//intervals
	private int intervalRateLimitStatus = 60; //padrao 1min
	private int intervalUpdates = 60;
	private int intervalMentions = 60;
	private int intervalFavorites = 60;
	private int intervalDirectMessages = 60;
	private int intervalSearch = 60;
	private int intervalPublicTimeline = 60;
	private int intervalMostPopularUsers = 7;
	
	private int updatesToGet = 100;
	
	
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
	
	public synchronized void setStatusBarMessage(String message, MessageType type) {
		JLabel statusLabel = mainWindow.getStatusBarLabel();
		if(type == MessageType.NOTIFICATION)
			statusLabel.setForeground(ChartColor.DARK_GREEN.darker());
		else if(type == MessageType.ERROR)
			statusLabel.setForeground(Color.red);
		else
			statusLabel.setForeground(Color.black);
		
		statusLabel.setText(message);
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
					loggedUsername = user.getScreenName();
					loginWindow.dispose();					
										
					mainWindow = new GUIMainWindow("DeepTwitter");
					mainWindowListener = new MainWindowListener();
					mainWindow.addMainWindowListener(mainWindowListener);
					
					networkView = new NetworkView();
					loadSettings();
										
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
						//tabManager.setEnabledAt(2, false); //favoritos
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
					
					updateRateLimit = new UpdateRateLimitThread(intervalRateLimitStatus);				
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
				try {
					File manual = new File("files/Manual.pdf");
					Runtime.getRuntime().exec("cmd.exe /c \"" +manual.getAbsolutePath() +"\"");
				} catch (Exception e1) {
					showMessageDialog(e1.getMessage(), MessageType.ERROR);
				}				
			}
			else if(cmd.equals("buttonNewUpdate")) {					
				openGUINewUpdateWindow();				
			}
			else if(cmd.equals("buttonUpdate")) {
				try {
					twitter.updateStatus(guiNewUpdate.getStatus());
					guiNewUpdate.dispose();
					Date now = Calendar.getInstance().getTime();
					setStatusBarMessage("Update realizado com sucesso em "+now,MessageType.NOTIFICATION);
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
				if(guiSettings == null){
					guiSettings = new GUISettings(loggedUsername);
					guiSettings.addMainWindowListener(mainWindowListener);
					guiSettings.addWindowListener(new WindowAdapter(){
						@Override
		                public void windowClosed(java.awt.event.WindowEvent arg0) {
		                    guiSettings = null;
		                }
					});
					guiSettings.setLocationRelativeTo(null);
					guiSettings.setVisible(true);
				}
				else{
					guiSettings.requestFocus();
					guiSettings.setVisible(true);
				}				
			}
			else if(cmd.equals("buttonForces")) {
				if(guiNetworkForces == null){
					guiNetworkForces = new GUINetworkForces();
					guiNetworkForces.addMainWindowListener(mainWindowListener);
					guiNetworkForces.addWindowListener(new WindowAdapter(){
						@Override
						public void windowClosed(java.awt.event.WindowEvent arg0) {
							guiNetworkForces = null;
						}
					});					
					guiNetworkForces.setVisible(true);
				}
				else
					guiNetworkForces.requestFocus();
				guiNetworkForces.setLocationRelativeTo(mainWindow);	
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
			else if(cmd.equals("buttonOKCancelConfig")){
				if(guiSettings != null){
					guiSettings.setVisible(false);
					guiSettings = null;
				}
			}
			else if(cmd.equals("buttonOKNetworkForces")){
				
				saveSettings();
				
				if(guiNetworkForces != null){
					guiNetworkForces.setVisible(false);
					guiNetworkForces = null;
				}
			}
			else if(cmd.equals("buttonCancelNetworkForces")){
				if(guiNetworkForces != null){
					guiNetworkForces.setVisible(false);
					guiNetworkForces = null;
				}
			}
//			else if(cmd.equals("buttonRestoreNetworkForces")){
//				loadDefaultSettingsNetworkForces();
//			}
			else
				System.out.println(cmd);
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
					//System.out.println("UPDATING RATE LIMIT");					
					rlt = twitter.rateLimitStatus();
					mainWindow.setRateLimitStatus(rlt.getRemainingHits(),rlt.getHourlyLimit(),rlt.getDateTime());
					Thread.sleep(sleepTime);
				} catch (Exception e) {					
					break;
				} 
			}
		}
	}
	
	public NetworkView getNetworkView() {
		return networkView;
	}

	public int getProperty(ConfigurationType configurationType) {
		if(configurationType == ConfigurationType.intervalUpdates)
			return intervalUpdates * 60000;
		else if(configurationType == ConfigurationType.intervalMentions)
			return intervalMentions * 60000;
		else if(configurationType == ConfigurationType.intervalFavorites)
			return intervalFavorites * 60000;
		else if(configurationType == ConfigurationType.intervalDirectMessages)
			return intervalDirectMessages * 60000;
		else if(configurationType == ConfigurationType.intervalSearch)
			return intervalSearch * 60000;
		else if(configurationType == ConfigurationType.intervalPublicTimeline)
			return intervalPublicTimeline * 60000;
		else if(configurationType == ConfigurationType.intervalMostPopularUsers)
			return intervalMostPopularUsers * 1000;
		else if(configurationType == ConfigurationType.edgeColor)
			return networkView.getEdgeColor();
		else if(configurationType == ConfigurationType.textColor)
			return networkView.getTextColor();
		else if(configurationType == ConfigurationType.mainUserColor)
			return networkView.getMainUserColor();
		else if(configurationType == ConfigurationType.searchResultColor)
			return networkView.getSearchResultColor();
		else if(configurationType == ConfigurationType.friendsColor)
			return networkView.getFriendsColor();
		else if(configurationType == ConfigurationType.followersColor)
			return networkView.getFollowersColor();
		else if(configurationType == ConfigurationType.friendsAndFollowersColor)
			return networkView.getFriendsAndFollowersColor();
		else if(configurationType == ConfigurationType.selectedItemColor)
			return networkView.getSelectedItemColor();
		else if(configurationType == ConfigurationType.nodeStrokeColor)
			return networkView.getNodeStrokeColor();
		else if(configurationType == ConfigurationType.edgeType)
			return networkView.getEdgeType();
		else if(configurationType == ConfigurationType.updatesToGet)
			return updatesToGet;
		else return -1;
	}

	public void setProperty(ConfigurationType configurationType, int value) {
		if(configurationType == ConfigurationType.intervalUpdates)
			intervalUpdates = value;
		else if(configurationType == ConfigurationType.intervalMentions)
			intervalMentions = value;
		else if(configurationType == ConfigurationType.intervalFavorites)
			intervalFavorites = value;
		else if(configurationType == ConfigurationType.intervalDirectMessages)
			intervalDirectMessages = value;
		else if(configurationType == ConfigurationType.intervalSearch)
			intervalSearch = value;
		else if(configurationType == ConfigurationType.intervalPublicTimeline)
			intervalPublicTimeline = value;
		else if(configurationType == ConfigurationType.intervalMostPopularUsers)
			intervalMostPopularUsers = value;
		else if(configurationType == ConfigurationType.edgeColor)
			networkView.setEdgeColor(value);
		else if(configurationType == ConfigurationType.textColor)
			networkView.setTextColor(value);
		else if(configurationType == ConfigurationType.mainUserColor)
			networkView.setMainUserColor(value);
		else if(configurationType == ConfigurationType.searchResultColor)
			networkView.setSearchResultColor(value);
		else if(configurationType == ConfigurationType.friendsColor)
			networkView.setFriendsColor(value);
		else if(configurationType == ConfigurationType.followersColor)
			networkView.setFollowersColor(value);
		else if(configurationType == ConfigurationType.friendsAndFollowersColor)
			networkView.setFriendsAndFollowersColor(value);
		else if(configurationType == ConfigurationType.selectedItemColor)
			networkView.setSelectedItemColor(value);
		else if(configurationType == ConfigurationType.nodeStrokeColor)
			networkView.setNodeStrokeColor(value);
		else if(configurationType == ConfigurationType.edgeType)
			networkView.setEdgeType(value);
		else if(configurationType == ConfigurationType.updatesToGet)
			updatesToGet = value;
		
	}
	
	public void saveSettings(){		
		SAXBuilder builder = new SAXBuilder(); // Build a document ...
		Document doc = null; // ... from a file
		XMLOutputter output = new XMLOutputter(); // And output the document ...
		Format format = Format.getPrettyFormat();  
		format.setEncoding("ISO-8859-1");
		output.setFormat(format);

		try {
			doc = builder.build("files/config.xml");
		} catch (JDOMException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO adicionar modal sobre a tela
			return;
		} catch (IOException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO adicionar modal sobre a tela
			return;
		}

		// Root element
		Element root = doc.getRootElement();

		Element interval = null;
		Element color = null;
		Element networkForces = null;
		Element tweetsToGet = null;
		Element elem;
		
		try{
			interval = root.getChild("interval");
			color = root.getChild("color");
			networkForces = root.getChild("networkForces");
			tweetsToGet = root.getChild("tweetsToGet");
		}catch(Exception e){
		}
		
		//intervalUpdates
		try{
			elem = interval.getChild("updates");
			elem.setText(String.valueOf(intervalUpdates));
		}catch(Exception e){
		}
		
		//intervalMentions
		try{
			elem = interval.getChild("mentions");
			elem.setText(String.valueOf(intervalMentions));
		}catch(Exception e){
		}
		
		//intervalFavorites
		try{
			elem = interval.getChild("favorites");
			elem.setText(String.valueOf(intervalFavorites));
		}catch(Exception e){
		}
		
		//intervalDirectMessages
		try{
			elem = interval.getChild("directMessages");
			elem.setText(String.valueOf(intervalDirectMessages));
		}catch(Exception e){
		}
		
		//intervalSearch
		try{
			elem = interval.getChild("search");
			elem.setText(String.valueOf(intervalSearch));
		}catch(Exception e){
		}
		
		//intervalPublicTimeline
		try{
			elem = interval.getChild("publicTimeline");
			elem.setText(String.valueOf(intervalPublicTimeline));
		}catch(Exception e){
		}
		
		//intervalMostActiveUsers
		try{
			elem = interval.getChild("mostPopularUsers");
			elem.setText(String.valueOf(intervalMostPopularUsers));
		}catch(Exception e){
		}
		
		//edgeColor
		try{
			elem = color.getChild("edge");
			elem.setText(String.valueOf(networkView.getEdgeColor()));
		}catch(Exception e){
		}
		
		//textColor
		try{
			elem = color.getChild("text");
			elem.setText(String.valueOf(networkView.getTextColor()));
		}catch(Exception e){
		}
		
		//mainUserColor
		try{
			elem = color.getChild("mainUser");
			elem.setText(String.valueOf(networkView.getMainUserColor()));
		}catch(Exception e){
		}
		
		//searchResultColor
		try{
			elem = color.getChild("searchResult");
			elem.setText(String.valueOf(networkView.getSearchResultColor()));
		}catch(Exception e){
		}
		
		//friendsColor
		try{
			elem = color.getChild("friends");
			elem.setText(String.valueOf(networkView.getFriendsColor()));
		}catch(Exception e){
		}
		
		//followersColor
		try{
			elem = color.getChild("followers");
			elem.setText(String.valueOf(networkView.getFollowersColor()));
		}catch(Exception e){
		}
		
		//friendsAndFollowersColor
		try{
			elem = color.getChild("friendsAndFollowers");
			elem.setText(String.valueOf(networkView.getFriendsAndFollowersColor()));
		}catch(Exception e){
		}
		
		//selectedItemColor
		try{
			elem = color.getChild("selectedItem");
			elem.setText(String.valueOf(networkView.getSelectedItemColor()));
		}catch(Exception e){
		}
		
		//nodeStrokeColor
		try{
			elem = color.getChild("nodeStroke");
			elem.setText(String.valueOf(networkView.getNodeStrokeColor()));
		}catch(Exception e){
		}
		
		//edgeType
		try{
			elem = root.getChild("edgeType");
			elem.setText(String.valueOf(networkView.getEdgeType()));
		}catch(Exception e){
		}
		
		if(networkView.getEdgeType() == 0)
			mainWindow.getButtonCurvedEdges().setSelected(false);
		else
			mainWindow.getButtonCurvedEdges().setSelected(true);
		
		//tweets to get
		try{
			elem = tweetsToGet.getChild("updates");
			elem.setText(String.valueOf(updatesToGet));
		}catch(Exception e){
		}
		
		//forces
    	prefuse.util.force.Force[] forces = networkView.getForceSimulator().getForces();
    	
    	for(int i=0;i<forces.length;i++){
    		if(forces[i] instanceof NBodyForce){    			
    			NBodyForce n = (NBodyForce) forces[i];
    			
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("GravitationalConstant")){
    					try{
    						elem = networkForces.getChild("gravConstant");
    						elem.setText(String.valueOf(n.getParameter(j)));
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("Distance")){
    					try{
    						elem = networkForces.getChild("minDistance");
    						elem.setText(String.valueOf(n.getParameter(j)));
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("BarnesHutTheta")){
    					try{
    						elem = networkForces.getChild("theta");
    						elem.setText(String.valueOf(n.getParameter(j)));
    					}catch(Exception e){
    					}
    				}
    			}
    		}
    		else if(forces[i] instanceof SpringForce){

    			SpringForce n = (SpringForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){
    				
    				if(n.getParameterName(j).equals("SpringCoefficient")){
    					try{
    						elem = networkForces.getChild("springCoeff");
    						elem.setText(String.valueOf(n.getParameter(j)));
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("DefaultSpringLength")){
    					try{
    						elem = networkForces.getChild("defaultLength");
    						elem.setText(String.valueOf(n.getParameter(j)));
    					}catch(Exception e){
    					}
    				}
    			}
    			
    		}    		
    		else if(forces[i] instanceof DragForce){    			
    			DragForce n = (DragForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){
    				
    				if(n.getParameterName(j).equals("DragCoefficient")){
    					try{
    						elem = networkForces.getChild("drag");
    						elem.setText(String.valueOf(n.getParameter(j)));
    					}catch(Exception e){
    					}
    				}    				
    			}
    		}
    	}
    	
    	
    	
		FileWriter f = null;
		try {
			f = new FileWriter(new File("files/config.xml"));
		} catch (IOException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO colocar frame, ver problemas
		}
		try {
			output.output(doc, f);
			f.close();
		} catch (IOException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO colocar frame, ver problemas
		}
	}
	
	public void loadSettings(){		
		File file = new File("files/config.xml");
		SAXBuilder sb = new SAXBuilder();

		Document d = null;

		try {
			d = sb.build(file);
		} catch (JDOMException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO ver quais excecoes podem ocorrer aki
		} catch (IOException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO se nao tiver pasta, criar a pasta e arquivo, se nao tiver
			// arquivo, criar apenas ele
			// System.exit(0);
		}

		// Root element
		Element root = d.getRootElement();
		
		Element interval = null;
		Element color = null;
		Element networkForces = null;
		Element tweetsToGet = null;
		Element elem;
		
		try{
			interval = root.getChild("interval");
			color = root.getChild("color");
			networkForces = root.getChild("networkForces");
			tweetsToGet = root.getChild("tweetsToGet");
		}catch(Exception e){
		}
		
		//intervalUpdates
		try{
			elem = interval.getChild("updates");
			intervalUpdates = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			intervalUpdates = 1;
		}
		
		//intervalMentions
		try{
			elem = interval.getChild("mentions");
			intervalMentions = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			intervalMentions = 1;
		}
		
		//intervalFavorites
		try{
			elem = interval.getChild("favorites");
			intervalFavorites = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			intervalFavorites = 1;
		}
		
		//intervalDirectMessages
		try{
			elem = interval.getChild("directMessages");
			intervalDirectMessages = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			intervalDirectMessages = 1;
		}
		
		//intervalSearch
		try{
			elem = interval.getChild("search");
			intervalSearch = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			intervalSearch = 1;
		}
		
		//intervalPublicTimeline
		try{
			elem = interval.getChild("publicTimeline");
			intervalPublicTimeline = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			intervalPublicTimeline = 1;
		}
		
		//intervalMostActiveUsers
		try{
			elem = interval.getChild("mostPopularUsers");
			intervalMostPopularUsers = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			intervalMostPopularUsers = 7;
		}
		
		//edgeColor
		try{
			elem = color.getChild("edge");
			networkView.setEdgeColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setEdgeColor(-8355712);
		}
		
		//textColor
		try{
			elem = color.getChild("text");
			networkView.setTextColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setTextColor(-16777216);
		}
		
		//mainUserColor
		try{
			elem = color.getChild("mainUser");
			networkView.setMainUserColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setMainUserColor(-14336);
		}
		
		//searchResultColor
		try{
			elem = color.getChild("searchResult");
			networkView.setSearchResultColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setSearchResultColor(-10789889);
		}
		
		//friendsColor
		try{
			elem = color.getChild("friends");
			networkView.setFriendsColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setFriendsColor(-12517377);
		}
		
		//followersColor
		try{
			elem = color.getChild("followers");
			networkView.setFollowersColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setFollowersColor(-49088);
		}
		
		//friendsAndFollowersColor
		try{
			elem = color.getChild("friendsAndFollowers");
			networkView.setFriendsAndFollowersColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setFriendsAndFollowersColor(-12517568);
		}
		
		//selectedItemColor
		try{
			elem = color.getChild("selectedItem");
			networkView.setSelectedItemColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setSelectedItemColor(-192);
		}
		
		//nodeStrokeColor
		try{
			elem = color.getChild("nodeStroke");
			networkView.setNodeStrokeColor(Integer.parseInt(elem.getTextTrim()));
		}catch(Exception e){
			networkView.setNodeStrokeColor(-16777216);
		}
		
		//edgeType
		try{
			elem = root.getChild("edgeType");
			int edgeType = Integer.parseInt(elem.getTextTrim());
			
			if(edgeType == 0){
				networkView.setEdgeType(false);
				mainWindow.getButtonCurvedEdges().setSelected(false);
			}
			else{
				networkView.setEdgeType(true);
				mainWindow.getButtonCurvedEdges().setSelected(true);
			}
		}catch(Exception e){
			networkView.setEdgeType(false);
		}
		
		//tweets to get
		try{
			elem = tweetsToGet.getChild("updates");
			updatesToGet = Integer.parseInt(elem.getText());
		}catch(Exception e){
			updatesToGet = 100;
		}
			
		//networkForces
    	float gravConstant = -1f;
    	float minDistance = -1f;
    	float theta = 0.9f;
    	
    	float drag = 0.007f;
    	float springCoeff = 9.99E-6f;
    	float defaultLength = 180f;    	
		
		try{
			elem = networkForces.getChild("gravConstant");
			gravConstant = Float.parseFloat(elem.getText());
		}catch(Exception e){
			gravConstant = -1f;
		}
		
		try{
			elem = networkForces.getChild("minDistance");
			minDistance = Float.parseFloat(elem.getText());
		}catch(Exception e){
			minDistance = -1f;
		}
		
		try{
			elem = networkForces.getChild("theta");
			theta = Float.parseFloat(elem.getText());
		}catch(Exception e){
			theta = 0.9f;
		}
		
		try{
			elem = networkForces.getChild("drag");
			drag = Float.parseFloat(elem.getText());
		}catch(Exception e){
			drag = 0.007f;
		}
		
		try{
			elem = networkForces.getChild("springCoeff");
			springCoeff = Float.parseFloat(elem.getText());
		}catch(Exception e){
			springCoeff = 9.99E-6f;
		}
		
		try{
			elem = networkForces.getChild("defaultLength");
			defaultLength = Float.parseFloat(elem.getText());
		}catch(Exception e){
			defaultLength = 180f;
		}
		
    	prefuse.util.force.Force[] forces = networkView.getForceSimulator().getForces();
    	
    	for(int i=0;i<forces.length;i++){
    		if(forces[i] instanceof NBodyForce){    			
    			NBodyForce n = (NBodyForce) forces[i];
    			
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("GravitationalConstant")){
    					try{
    						n.setParameter(j, gravConstant);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("Distance")){
    					try{
    						n.setParameter(j, minDistance);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("BarnesHutTheta")){
    					try{
    						n.setParameter(j, theta);
    					}catch(Exception e){
    					}
    				}
    			}
    		}
    		else if(forces[i] instanceof SpringForce){
    			SpringForce n = (SpringForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("SpringCoefficient")){
    					try{
    						n.setParameter(j, springCoeff);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("DefaultSpringLength")){
    					try{
    						n.setParameter(j, defaultLength);
    					}catch(Exception e){
    					}
    				}
    			}    			
    		}    		
    		else if(forces[i] instanceof DragForce){    			
    			DragForce n = (DragForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("DragCoefficient")){
    					try{
    						n.setParameter(j, drag);
    					}catch(Exception e){
    					}
    				}    				
    			}
    		}
    	}
	}
	
	public Settings getDefaultSettingsNetworkForces(){		
		File file = new File("files/defaultConfigs.xml");
		SAXBuilder sb = new SAXBuilder();

		Document d = null;

		try {
			d = sb.build(file);
		} catch (JDOMException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO ver quais excecoes podem ocorrer aki
		} catch (IOException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO se nao tiver pasta, criar a pasta e arquivo, se nao tiver
			// arquivo, criar apenas ele
			// System.exit(0);
		}

		// Root element
		Element root = d.getRootElement();
		
		Element networkForces = null;
		Element elem;
		
		try{
			networkForces = root.getChild("networkForces");
		}catch(Exception e){
		}
		
		//networkForces
    	float gravConstant = -1f;
    	float minDistance = -1f;
    	float theta = 0.9f;
    	
    	float drag = 0.007f;
    	float springCoeff = 9.99E-6f;
    	float defaultLength = 180f;    	
		
		try{
			elem = networkForces.getChild("gravConstant");
			gravConstant = Float.parseFloat(elem.getText());
		}catch(Exception e){
			gravConstant = -1f;
		}
		
		try{
			elem = networkForces.getChild("minDistance");
			minDistance = Float.parseFloat(elem.getText());
		}catch(Exception e){
			minDistance = -1f;
		}
		
		try{
			elem = networkForces.getChild("theta");
			theta = Float.parseFloat(elem.getText());
		}catch(Exception e){
			theta = 0.9f;
		}
		
		try{
			elem = networkForces.getChild("drag");
			drag = Float.parseFloat(elem.getText());
		}catch(Exception e){
			drag = 0.007f;
		}
		
		try{
			elem = networkForces.getChild("springCoeff");
			springCoeff = Float.parseFloat(elem.getText());
		}catch(Exception e){
			springCoeff = 9.99E-6f;
		}
		
		try{
			elem = networkForces.getChild("defaultLength");
			defaultLength = Float.parseFloat(elem.getText());
		} catch (Exception e) {
			defaultLength = 180f;
		}
		
		Settings settings = new Settings(gravConstant, minDistance, theta, 
				springCoeff, defaultLength, drag);
		
		return settings;

	}

	public Settings getDefaultSettingsOther() {		
		File file = new File("files/defaultConfigs.xml");
		SAXBuilder sb = new SAXBuilder();

		Document d = null;

		try {
			d = sb.build(file);
		} catch (JDOMException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO ver quais excecoes podem ocorrer aki
		} catch (IOException ex) {
			showMessageDialog(ex.getMessage(),MessageType.ERROR);
			// TODO se nao tiver pasta, criar a pasta e arquivo, se nao tiver
			// arquivo, criar apenas ele
			// System.exit(0);
		}

		// Root element
		Element root = d.getRootElement();
		
		Element interval = null;
		Element color = null;
		Element tweetsToGet = null;
		Element elem;
		
		int rIntervalUpdates, rIntervalMentions, rIntervalFavorites,
		rIntervalDirectMessages, rIntervalSearch, rIntervalPublicTimeline,
		rIntervalMostPopularUsers, rEdgeColor, rTextColor, rMainUserColor,
		rSearchResultColor, rFriendsColor, rFollowersColor, rFriendsAndFollowersColor,
		rSelectedItemColor, rEdgeType, rNodeStrokeColor, rUpdatesToGet;
		
		
		
		try{
			interval = root.getChild("interval");
			color = root.getChild("color");
			tweetsToGet = root.getChild("tweetsToGet");
		}catch(Exception e){
		}
		
		//intervalUpdates
		try{
			elem = interval.getChild("updates");
			rIntervalUpdates = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rIntervalUpdates = 1;
		}
		
		//intervalMentions
		try{
			elem = interval.getChild("mentions");
			rIntervalMentions = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rIntervalMentions = 1;
		}
		
		//intervalFavorites
		try{
			elem = interval.getChild("favorites");
			rIntervalFavorites = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rIntervalFavorites = 1;
		}
		
		//intervalDirectMessages
		try{
			elem = interval.getChild("directMessages");
			rIntervalDirectMessages = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rIntervalDirectMessages = 1;
		}
		
		//intervalSearch
		try{
			elem = interval.getChild("search");
			rIntervalSearch = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rIntervalSearch = 1;
		}

		//intervalPublicTimeline
		try{
			elem = interval.getChild("publicTimeline");
			rIntervalPublicTimeline = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rIntervalPublicTimeline = 1;
		}
		
		//intervalMostActiveUsers
		try{
			elem = interval.getChild("mostPopularUsers");
			rIntervalMostPopularUsers = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rIntervalMostPopularUsers = 7;
		}
		
		//edgeColor
		try{
			elem = color.getChild("edge");
			rEdgeColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rEdgeColor = -8355712;
		}
		
		//textColor
		try{
			elem = color.getChild("text");
			rTextColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rTextColor = -16777216;
		}
		
		//mainUserColor
		try{
			elem = color.getChild("mainUser");
			rMainUserColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rMainUserColor = -14336;
		}
		
		//searchResultColor
		try{
			elem = color.getChild("searchResult");
			rSearchResultColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rSearchResultColor = -10789889;
		}
		
		//friendsColor
		try{
			elem = color.getChild("friends");
			rFriendsColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rFriendsColor = -12517377;
		}
		
		//followersColor
		try{
			elem = color.getChild("followers");
			rFollowersColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rFollowersColor = -49088;
		}
		
		//friendsAndFollowersColor
		try{
			elem = color.getChild("friendsAndFollowers");
			rFriendsAndFollowersColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rFriendsAndFollowersColor = -12517568;
		}
		
		//selectedItemColor
		try{
			elem = color.getChild("selectedItem");
			rSelectedItemColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rSelectedItemColor  = -192;
		}
		
		//nodeStrokeColor
		try{
			elem = color.getChild("nodeStroke");
			rNodeStrokeColor = Integer.parseInt(elem.getTextTrim());
		}catch(Exception e){
			rNodeStrokeColor = -16777216;
		}
		
		//edgeType
		try{
			elem = root.getChild("edgeType");
			rEdgeType = Integer.parseInt(elem.getTextTrim());
			
		}catch(Exception e){
			rEdgeType = 0;
		}
		
		//tweets to get
		try{
			elem = tweetsToGet.getChild("updates");
			rUpdatesToGet = Integer.parseInt(elem.getTextTrim());
			
		}catch(Exception e){
			rUpdatesToGet = 100;
		}
		
		
		Settings settings = new Settings(rIntervalUpdates, rIntervalMentions,
				rIntervalFavorites, rIntervalDirectMessages,
				rIntervalSearch, rIntervalPublicTimeline,
				rIntervalMostPopularUsers, rEdgeColor, rTextColor,
				rMainUserColor, rSearchResultColor, rFriendsColor,
				rFollowersColor, rFriendsAndFollowersColor,
				rSelectedItemColor, rNodeStrokeColor, rEdgeType,
				rUpdatesToGet);
		
		return settings;
	}
}
