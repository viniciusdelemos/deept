package model.threads;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import model.ChartColor;
import model.MessageType;
import model.StatusesType;
import model.URLLinkAction;
import model.twitter4j.DirectMessageDeepT;
import model.twitter4j.StatusDeepT;
import model.twitter4j.TwitterDeepT;
import model.twitter4j.TwitterMod;
import model.twitter4j.TwitterResponseDeepT;
import model.twitter4j.UserDeepT;
import twitter4j.ExtendedUser;
import twitter4j.Paging;
import twitter4j.DirectMessage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;

public class StatusesTableThread {
	private int rows;
	private String userId, searchQuery;
	private boolean isTwitterUser;	
	private long interval;
	private JPanel panel;
	private KeepTableUpdated keepTableUpdated;
	private int updatesToGet;
	private StatusesType statusesType;
	private GridBagConstraints gbc;
	private ControllerDeepTwitter controller;
	
	public StatusesTableThread (StatusesType type, String userIdOrSearchQuery) {		
		this(type);
		if(type == StatusesType.SEARCH)
			searchQuery = userIdOrSearchQuery;
		else
			userId = userIdOrSearchQuery;		
	}
	
	public StatusesTableThread(StatusesType type) {
		this.statusesType = type;
		this.userId = null;
		this.searchQuery = null;
		controller = ControllerDeepTwitter.getInstance();
		isTwitterUser = controller.isTwitterUser();		
		rows = 0;
		interval = 2000;//120000; //2 minutos
		updatesToGet = 100;
	}

	public JPanel getContent() {		
		if(panel!=null) return panel;
		GridBagLayout layout = new GridBagLayout();        
		panel = new JPanel(layout);		
		keepTableUpdated = new KeepTableUpdated();
		keepTableUpdated.start();		
		return panel;
	}
	
	private JPanel getPanel(final TwitterResponseDeepT s) {		
        JPanel updatePanel = new JPanel(new GridBagLayout());        
        gbc = new GridBagConstraints();
        
        if(rows%2==0)
        	updatePanel.setBackground(ChartColor.LIGHT_GRAY);       
        else
        	updatePanel.setBackground(ChartColor.LIGHT_GRAY.brighter());
        
        gbc.weightx = 0;		
		gbc.gridx = 0;
		gbc.insets = new Insets(0,3,0,1);
		
		JLabel interactiveImageAux;
		ImageIcon userPicture = null;		
		
//		final ExtendedUser u1 = s.getExtendedUser();
//		final User u2 = s.getUser();
//		if(u1 != null) {
//			userPicture = new ImageIcon(u1.getProfileImageURL());
//			screenName = u1.getScreenName();
//			}
//		else {
//			userPicture = new ImageIcon(u2.getProfileImageURL());
//			screenName = u2.getScreenName();
//			}
		UserDeepT u = null;
		if(s.getDirectMessageDeepT() != null)
			u = s.getDirectMessageDeepT().getSender();
		else
			u = s.getUserDeepT();
		
		userPicture = new ImageIcon(u.getProfileImageUrl());		

		if(userPicture.getIconHeight()>48 || userPicture.getIconWidth()>48) {
			Image image = userPicture.getImage().getScaledInstance(48, 48, Image.SCALE_DEFAULT);
			interactiveImageAux = new JLabel(new ImageIcon(image));
		}
		else
			interactiveImageAux = new JLabel(userPicture);

		final JLabel interactiveImage = interactiveImageAux;
		
		interactiveImage.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
				interactiveImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				controller.searchAndAddUserToNetwork(s);//s.getUser());
			}
		});
		
        updatePanel.add(interactiveImage,gbc);
       
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        
        JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		editorPane.setPreferredSize(new Dimension(330,70));
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		editorPane.setFont(new Font("Calibri",0,13));
		editorPane.setBackground(updatePanel.getBackground());
		
		if(s.getDirectMessageDeepT() != null){
		editorPane.setText("<b><a href=http://www.twitter.com/"+u.getScreenName()+">"
				+u.getScreenName()+"</a></b>"
				+": "+processText(s.getDirectMessageDeepT().getText()));
				//+"<br>"+s.getCreatedAt().toString());
		}
		else{
			editorPane.setText("<b><a href=http://www.twitter.com/"+u.getScreenName()+">"
					+u.getScreenName()+"</a></b>"
					+": "+processText(s.getStatusDeepT().getText()));
					//+"<br>"+s.getCreatedAt().toString());			
		}
		
		
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {						
						new URLLinkAction(e.getURL().toString());
	        		} catch (Exception ex) {
	        			//controller.searchAndAddUserToNetwork(e.getDescription());
	        			controller.showMessageDialog(ex.getMessage(),MessageType.ERROR);
	        			ex.printStackTrace();
	        		} 
				}
			}
		});
		
		updatePanel.add(editorPane,gbc);
		
		if(isTwitterUser)
			updatePanel = addButtonsToPanel(updatePanel, s);
		        
        return updatePanel;
    }
	
	private JPanel addButtonsToPanel(final JPanel updatePanel, final TwitterResponseDeepT s) {
		gbc.weightx = 0;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(4,0,0,0);
        gbc.anchor = GridBagConstraints.NORTH;
        
		final JLabel replyButton = new JLabel();	
		ImageIcon replyIcon = new ImageIcon(getClass().getResource("../../reply.jpg"));	
		replyButton.setIcon(replyIcon);		
		replyButton.setToolTipText("Reply");
		replyButton.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
				replyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				controller.openGUINewUpdateWindow(s.getUserDeepT().getScreenName(),StatusesType.REPLIES);//s.getUser().getScreenName(),StatusesType.REPLIES);				
			}
		});
		updatePanel.add(replyButton,gbc);
		
		gbc.weightx = 0;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(1,0,0,0);
        gbc.anchor = GridBagConstraints.CENTER;
        
		final JLabel favoriteButton = new JLabel();
		ImageIcon starIcon;
		
		//Pois se for Direct Message, tem que fazer outra consulta para ver
		//se é favorito, pois esta informacao nao tem na direct message
		if(s.getStatusDeepT() != null){
		
			if(s.getStatusDeepT().isFavorited()) 
				starIcon = new ImageIcon(getClass().getResource("../../star_on.png"));		
			else 
				starIcon = new ImageIcon(getClass().getResource("../../star_off.png"));
		}
		else
			starIcon = new ImageIcon(getClass().getResource("../../star_off.png"));
			//TODO talvez fosse bom nao carregar o icone
		
		favoriteButton.setIcon(starIcon);	
		favoriteButton.setToolTipText("Favorite");
		favoriteButton.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
				favoriteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				try{
					int isOff = favoriteButton.getIcon().toString().indexOf("star_off");
					if(isOff>0) {
						controller.getTwitter().createFavorite(s.getStatusDeepT().getId());
						favoriteButton.setIcon(new ImageIcon(getClass().getResource("../../star_on.png")));
					}
					else {
						controller.getTwitter().destroyFavorite(s.getStatusDeepT().getId());
						favoriteButton.setIcon(new ImageIcon(getClass().getResource("../../star_off.png")));
						if(statusesType == StatusesType.FAVORITES) {
							System.out.println("removing favorite panel");
							panel.remove(updatePanel);
							rows--;
						}
					}				
					panel.revalidate();
				}
				catch(TwitterException e) {
					controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
				}
			}
		});
		updatePanel.add(favoriteButton,gbc);
		
		int loggedUserId = Integer.parseInt(controller.getLoggedUserId());
		
//		int id = -1;
//		if(s.getUser() != null)
//			id = s.getUser().getId();
//		else
//			id = s.getExtendedUser().getId();
//		id = s.getUserTeste().getId();
		
		int IdUser;
		final long IdMessage;
		
		if(s.getDirectMessageDeepT() != null){
			
			IdUser = s.getDirectMessageDeepT().getSender_id();
			IdMessage = s.getDirectMessageDeepT().getId();
			
		}else{
			
			IdUser = s.getUserDeepT().getId();
			IdMessage = s.getStatusDeepT().getId();
			
		}
		
		if(loggedUserId == IdUser) {
			gbc.weightx = 0;
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.insets = new Insets(0,0,4,0);
			
			final JLabel deleteButton = new JLabel();
			ImageIcon deleteIcon = new ImageIcon(getClass().getResource("../../trash.png"));	
			deleteButton.setIcon(deleteIcon);	
			deleteButton.setToolTipText("Delete");
			deleteButton.addMouseListener(new MouseAdapter(){
				public void mouseEntered(java.awt.event.MouseEvent arg0) {
					deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				public void mouseClicked(java.awt.event.MouseEvent arg0) {
					try {
						int n = JOptionPane.showConfirmDialog(
	                            null, "Tem certeza que deseja deletar este update?","Atenção",
	                            JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null);
						if (n == JOptionPane.YES_OPTION) {
							controller.getTwitter().destroyStatusDeepT(IdMessage);
							panel.remove(updatePanel);
							rows--;
							panel.revalidate();
						}
					} catch (TwitterException e) {
						controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
					}
				}
			});
			updatePanel.add(deleteButton,gbc);
		}		
		return updatePanel;
	}
	
	private String processText(String status) {
		StringBuilder sb = new StringBuilder(status);
		int indexHttpBegin = 0;
		int indexHttpEnd = 0;
		String url, htmlString;
		
		while(indexHttpBegin>=0 && indexHttpBegin<9999){
			int httpLink = sb.indexOf("http://",indexHttpEnd);
			if(httpLink<0) httpLink = 9999;
			int wwwLink = sb.indexOf("www.",indexHttpEnd);
			if(wwwLink<0) wwwLink = 9999;
			int atLink = sb.indexOf("@",indexHttpEnd);
			if(atLink<0) atLink = 9999;
			
			indexHttpBegin = Math.min(httpLink, Math.min(wwwLink, atLink));
			
			if(indexHttpBegin>=0 && indexHttpBegin<9999) {
				indexHttpEnd = sb.indexOf(" ", indexHttpBegin);				
				if(indexHttpEnd<0) indexHttpEnd = sb.length();	
								
				if(indexHttpBegin==atLink) {
					indexHttpBegin++;
					url = sb.subSequence(indexHttpBegin, indexHttpEnd).toString();
					int i;
					for(i=0; i<url.length(); i++) {
						if(!Character.isLetter(url.charAt(i))
								&& !Character.isDigit(url.charAt(i))
								&& url.charAt(i)!='_') {
							url = url.substring(0, i);
							break;
						}
					}
					htmlString = "<a href=http://www.twitter.com/"+url+">"+url+"</a>";//"<a href="+url+">"+url+"</a>";
					sb.replace(indexHttpBegin, indexHttpBegin+i, htmlString);
				}
				else {
					url = sb.subSequence(indexHttpBegin, indexHttpEnd).toString();
					if(indexHttpBegin==wwwLink) 
						htmlString = "<a href=http://"+url+">"+url+"</a>";
					else
						htmlString = "<a href="+url+">"+url+"</a>";
					sb.replace(indexHttpBegin, indexHttpEnd, htmlString);
				}				
				indexHttpEnd=indexHttpBegin+htmlString.length();
			}
		}
		return sb.toString();
	}
	
	public void setNumberOfUpdatesToGet(int x) {
		updatesToGet = x;
	}
	
	public String getUserId() {
		if(userId==null)
			return controller.getLoggedUserId();
		return userId;
	}
	
	public void setUpdateTimelineInterval(long milliseconds) {
		interval = milliseconds;
	}
	
	public void pauseThread() {
		keepTableUpdated.pauseThread();
	}
	
	public void resumeThread() {
		keepTableUpdated.resumeThread();
	}
	
	public void interruptThread() {
		keepTableUpdated.interruptThread();
	}
	
	public boolean isThreadSuspended() {
		return keepTableUpdated.isThreadSuspended();
	}
	
	public List<TwitterResponseDeepT> getStatusesList() {				
		return keepTableUpdated.getStatusesList();
	}
	
	class KeepTableUpdated extends Thread {
		private long lastStatusId;
		private GridBagConstraints c;
		private JPanel empty;
		private TwitterDeepT twitter = controller.getTwitter();
		private boolean threadSuspended;
		private Map<Long,Status> favoritesMap;// = new HashMap<Long,Status>();
		
		private List<TwitterResponseDeepT> directMessagesList; //TROQUEI O TIPO SO PARA TESTAR				
		private List<TwitterResponseDeepT> statusesList, allStatusesList;		

		
		public KeepTableUpdated() {			
			twitter = controller.getTwitter();
			c = new GridBagConstraints();
			threadSuspended = false;
			lastStatusId = -1;
			statusesList = new ArrayList<TwitterResponseDeepT>();
			allStatusesList = new ArrayList<TwitterResponseDeepT>();
		}
		
		public synchronized void pauseThread() {
			threadSuspended = true;
		}
		
		public synchronized void resumeThread() {
			threadSuspended = false;
			notify();
		}
		
		public synchronized void interruptThread() {
			interrupt();
		}
		
		public boolean isThreadSuspended() {
			return threadSuspended;
		}
		
		public List<TwitterResponseDeepT> getStatusesList() {				
			return allStatusesList;
		}
		
		public void run()
		{	
			System.out.println("STARTING " + statusesType + " para " + controller.getUserName(getUserId()));			
			while(true) {
				try {					
					if (threadSuspended) {
						System.out.println("PAUSING " + statusesType + " para " + controller.getUserName(getUserId()));
						synchronized(this) {
							while (threadSuspended) {
								try {
									wait();
								} catch (InterruptedException e) {
									System.out.println("INTERRUPTED " + statusesType + " para " + controller.getUserName(getUserId()));
									break;
								}
							}
							System.out.println("RESUMING " + statusesType + " para " + controller.getUserName(getUserId()));
						}
					}
					
					statusesList = new ArrayList<TwitterResponseDeepT>();
					
					switch(statusesType)
					{
					case UPDATES:
						if(lastStatusId < 0) {
							if(userId==null)
								statusesList = twitter.getFriendsTimelineDeepT(new Paging().count(updatesToGet));
							else
								statusesList = twitter.getUserTimelineDeepT(userId, new Paging().count(updatesToGet));
						}
						else {
							if(userId==null)
								statusesList = twitter.getFriendsTimelineDeepT(new Paging().sinceId(lastStatusId));
							else
								statusesList = twitter.getUserTimelineDeepT(userId, updatesToGet, lastStatusId);
								//talvez seja melhor fazer userId, new Paging().count(updatesToGet).sinceId(lastStatusId)
								//testar
						}						
						break;

					case FAVORITES:
						//TODO
//						if(lastStatusId < 0) {
							if(userId==null)
								statusesList = twitter.getFavoritesDeepT();
							else
								statusesList = twitter.getFavoritesDeepT(userId);
//						}
//						else {System.out.println("user id = "+userId);
//							if(userId==null)
//								statusesList = (ArrayList<Status>) twitter.favoritesBySinceId(lastStatusId);
//							else
//								statusesList = (ArrayList<Status>) twitter.favoritesBySinceId(userId, lastStatusId);
//						}					
						break;

					case REPLIES:
						if(lastStatusId < 0) 
							statusesList = twitter.getMentionsDeepT();
						else
							statusesList = twitter.getMentionsDeepT(new Paging(lastStatusId));
						break;
					
					case DIRECT_MESSAGES_RECEIVED:
						if(lastStatusId < 0) 
							statusesList = twitter.getDirectMessagesDeepT();
						else
							statusesList = twitter.getDirectMessagesDeepT(new Paging(lastStatusId));						
						break;
					
					case DIRECT_MESSAGES_SENT:
						if(lastStatusId < 0) 
							statusesList = twitter.getSentDirectMessagesDeepT();
						else
							statusesList = twitter.getSentDirectMessagesDeepT(new Paging(lastStatusId));
						break;
						
					case SEARCH:
						//TODO: POSSIBILITAR MULTIPLAS PESQUISAS
						//SALVAR PESQUISAS/UPDATES NO COMPUTADOR PARA CARREGAR MAIS RAPIDO DEPOIS?
						Query query = new Query(searchQuery);
						if(lastStatusId >= 0) 
							query.setSinceId(lastStatusId);
						query.setRpp(updatesToGet);
						
						
						statusesList =
							twitter.searchDeepT(query);
						
						//Voce adicionava os tweets em lista de status, agora nao precisa mais
//						for(int i=0; i<results.getTweets().size(); i++) {
//							Tweet t = results.getTweets().get(i);
//							statusesList.add(new TwitterResponseDeepT(t));
//						}
						break;

					case PUBLIC_TIMELINE:
						if(lastStatusId<0)							
							statusesList = twitter.getPublicTimelineDeepT();
						else
							statusesList = twitter.getPublicTimelineDeepT(lastStatusId);
						break;
					}
					

					/*
					for(DirectMessage m : directMessagesList) {
						System.out.println(m);//statusesList.add(new StatusDeepT(m));
					}
					*/					
					
					//TODO
					if(statusesType == StatusesType.FAVORITES) {
						//adicionar/remover do mapa e do painel!
					}
					
					if(!statusesList.isEmpty()) {
						
						if(statusesList.get(0).getDirectMessageDeepT() != null){
							
							//checa se nova msg é mais nova do que ultima adicionada ao painel
							System.out.println("update mais novo: "+statusesList.get(0).getDirectMessageDeepT().getId());
							System.out.println("last status id: "+lastStatusId);
							if(statusesList.get(0).getDirectMessageDeepT().getId() > lastStatusId)
							{
								lastStatusId = statusesList.get(0).getDirectMessageDeepT().getId();
								if(empty != null) panel.remove(empty);							
								//de trás para frente, para adicionar as mais recentes em cima
								for(int i=statusesList.size()-1; i>=0; i--) {
								//for(int i=0; i<statusesList.size(); i++) {
									TwitterResponseDeepT s = statusesList.get(i);
									System.out.println(s);
									c.weightx = 0.5;
									c.fill = GridBagConstraints.HORIZONTAL;
									c.gridx = 0;
									panel.add(getPanel(s),c,0);			
									rows++;
									panel.revalidate();
									allStatusesList.add(0,s);								
								}
								empty = new JPanel();
								empty.add(new JLabel(""));
								c.weightx = 0.5;
								c.weighty = 1;
								c.fill = GridBagConstraints.HORIZONTAL;
								c.gridx = 0;
								c.anchor = GridBagConstraints.PAGE_END;
								panel.add(empty,c);
								panel.revalidate();
							}
							
						}
						else{
						//checa se nova msg é mais nova do que ultima adicionada ao painel
						System.out.println("update mais novo: "+statusesList.get(0).getStatusDeepT().getId());
						System.out.println("last status id: "+lastStatusId);
						if(statusesList.get(0).getStatusDeepT().getId()>lastStatusId)
						{
							lastStatusId = statusesList.get(0).getStatusDeepT().getId();
							if(empty != null) panel.remove(empty);							
							//de trás para frente, para adicionar as mais recentes em cima
							for(int i=statusesList.size()-1; i>=0; i--) {
							//for(int i=0; i<statusesList.size(); i++) {
								TwitterResponseDeepT s = statusesList.get(i);
								System.out.println(s);
								c.weightx = 0.5;
								c.fill = GridBagConstraints.HORIZONTAL;
								c.gridx = 0;
								panel.add(getPanel(s),c,0);			
								rows++;
								panel.revalidate();
								allStatusesList.add(0,s);								
							}
							empty = new JPanel();
							empty.add(new JLabel(""));
							c.weightx = 0.5;
							c.weighty = 1;
							c.fill = GridBagConstraints.HORIZONTAL;
							c.gridx = 0;
							c.anchor = GridBagConstraints.PAGE_END;
							panel.add(empty,c);
							panel.revalidate();
						}
					}
					}
					Thread.sleep(interval);	
					System.out.println("running " + statusesType + " para " + controller.getUserName(getUserId()));
				} catch (TwitterException e) {
					if(e.getStatusCode()==400) {
						controller.showMessageDialog("Você excedeu o limite de 100 requisições por hora permitido pelo Twitter. Aguarde e tente novamente.",MessageType.ERROR);
						break;
					}
					else if(e.getStatusCode()==401) {
						controller.showMessageDialog("Você não está autorizado a ver os updates desta pessoa.",MessageType.ERROR);
						break;
					}
					else if(e.getStatusCode()==-1) {
						controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
						break;
					}
					else
						controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
						
					System.out.println("Status code: "+e.getStatusCode());
					e.printStackTrace();
				}
				catch(InterruptedException ie) {
					System.out.println("INTERRUPTED " + statusesType + " para " + controller.getUserName(getUserId()));
					break;
				}
			}
		}
	}
}


