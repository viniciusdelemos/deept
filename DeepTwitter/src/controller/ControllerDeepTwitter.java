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
import java.util.List;

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
import model.threads.StatusesTableThread;
import model.twitter4j.ExtendedUserDeepT;
import model.twitter4j.TwitterDeepT;
import model.twitter4j.TwitterMod;
import model.twitter4j.UserDeepT;
import model.StatusesType;
import prefuse.Display;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.GraphMLWriter;
import twitter4j.ExtendedUser;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserWithStatus;
import twitter4j.examples.Update;

public class ControllerDeepTwitter {
	private GUILoginDeepTwitter loginWindow;
	private GUIMainWindow mainWindow;
	private GUIAddUser guiAddUser;
	private GUINewUpdate guiNewUpdate;
	private TwitterDeepT twitter;
	private GraphicManager gManager;
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
	
	public TwitterDeepT getTwitter() {
		return twitter;
	}
	
	public String getLoggedUserId() {
		return loggedUserId;
	}
	
	public boolean isTwitterUser() {
		return isTwitterUser;
	}
	
	public String getUserName(String id) {
		return gManager.getUserName(Integer.parseInt(id));
	}
	
	public void searchAndAddUserToNetwork(User u){
		try{
			ExtendedUser extendedUser = twitter.verifyCredentials();
			UserDeepT userDeepT = new UserDeepT(extendedUser);
			gManager.searchAndAddUserToNetwork(userDeepT);
		}catch(TwitterException e){
			e.printStackTrace();
			//nao é grave, só coloquei assim para poder ver no console se acontecer
			System.out.println("PROBLEMA MUIIIIIIIIIIIIIITO GRAVE");
		}
	}
	
	public void searchAndAddUserToNetwork(UserDeepT u) {
		gManager.searchAndAddUserToNetwork(u);
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
			
			ExtendedUser user = null;
			
			try{
				if(isTwitterUser)
				{				
					if(loginWindow.getUser().compareTo("")>0 && loginWindow.getPassword().compareTo("")>0)
					{
						twitter = new TwitterDeepT(loginWindow.getUser(),loginWindow.getPassword());
						
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
						twitter = new TwitterDeepT(loginWindow.getUser(),"");
						logInOK = true;
					}
					else showMessageDialog("Por favor, preencha o campo de nome de usuário!",MessageType.WARNING);					
				}

				if(logInOK)
				{
					UserDeepT u = new UserDeepT(user);
					
					loggedUserId = String.valueOf(u.getId());
					gManager = new GraphicManager();
				
					//User u2 = twitter.getAuthenticatedUser();
					//gManager.addNode(u2);
					loginWindow.dispose();
					
					mainWindowListener = new MainWindowListener();
					mainWindow = new GUIMainWindow("DeepTwitter");
					mainWindow.addMainWindowListener(mainWindowListener);
										
					final JSplitPane jSplitPane = mainWindow.getSplitPane();					
					windowTabs = mainWindow.getTabs();
					
					windowTabs.remove(1);
					
					tabManager = new StatusTabManager();
					tabManager.setTabbedPane(windowTabs);
					tabManager.addTab(StatusesType.UPDATES,"Atualizações"); //1
					tabManager.addTab(StatusesType.REPLIES,"@"+u.getScreenName()); //2
					tabManager.addTab(StatusesType.FAVORITES,"Favoritos"); //3
					tabManager.addTab(StatusesType.DIRECT_MESSAGES,"Mensagens"); //4
					tabManager.addTab(StatusesType.SEARCH, "Busca"); //5
					tabManager.addTab(StatusesType.PUBLIC_TIMELINE, "Public Timeline"); //6		
					
					if(!isTwitterUser) {
						tabManager.setEnabledAt(2, false); //replies
						//tabManager.setEnabledAt(3, false); //favoritos
						tabManager.setEnabledAt(4, false); //mensagens
					}
					
					windowTabs.addChangeListener(new ChangeListener(){
						@Override
						public void stateChanged(ChangeEvent e) {
							StatusesTableThread table = null;
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
									table = new StatusesTableThread(StatusesType.UPDATES);
								else
									table = new StatusesTableThread(StatusesType.UPDATES,getLoggedUserId());
								break;
								
							case REPLIES:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
								table = new StatusesTableThread(StatusesType.REPLIES);
								break;
								
							case FAVORITES:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
								table = new StatusesTableThread(StatusesType.FAVORITES);
								break;
								
							case DIRECT_MESSAGES:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
								table = new StatusesTableThread(StatusesType.DIRECT_MESSAGES_RECEIVED);
								break;
							case SEARCH:
								jSplitPane.setDividerLocation(431);
								return;
//								if(selectedTab.isActive()) return;
//								table = new StatusesTableThread(StatusesType.SEARCH);								
								
							case PUBLIC_TIMELINE:
								jSplitPane.setDividerLocation(431);
								if(selectedTab.isActive()) return;
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
					
					jSplitPane.setRightComponent((Display)gManager);		
					
					SwingUtilities.updateComponentTreeUI(mainWindow);
					mainWindow.setLocationRelativeTo(null);
					mainWindow.setVisible(true);					
					
					gManager.addNode(u);
					
					updateRateLimit = new UpdateRateLimitThread(rateLimitSleepTime);
					
//					UpdatePanel1 updatePanel1 = new UpdatePanel1();
//					new Thread(updatePanel1).start();
					
					//gManager.getVisualization().run("layout"); 					
					//gManager.getVisualization().repaint();	
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
	
	public StatusTabManager getStatusTabManager() {
		return tabManager;
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
				openGUINewUpdateWindow();				
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
						ExtendedUser extendedUser = twitter.getUserDetail(id);
						UserDeepT u = new UserDeepT(extendedUser);
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
			else if(cmd.equals("checkBoxCurvedEdges")) {
				gManager.setEdgeType(mainWindow.isCurvedEdges());
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
	
//	private class UpdatePanel1 extends Thread {
//
//		public UpdatePanel1(){
//		}
//
//		public void run(){
//
//			int timemillis = 50;
//
//			// sendo assim (50 * 60) = 3000
//			// vai ficar 3 segundos atualizando jpanel1
//			// para atualizar foto
//
//			for(int i=0 ; i<60;i++){
//
//				try {
//					Thread.sleep(timemillis);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				mainWindow.getJPanel1().updateUI();
//			}
//		}
//	}
}
