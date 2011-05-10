package model.threads;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import model.ChartColor;
import model.ConfigurationType;
import model.MessageType;
import model.StatusesType;
import model.URLLinkAction;
import prefuse.data.Node;
import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;
import twitter4j.User;
import controller.ControllerDeepTwitter;

public class StatusesTableThread {
	private int rows;
	private String searchQuery;
	private boolean isTwitterUser, isGroup;	
	private long interval;
	private JPanel panel;
	private JLabel counterLabel;
	private KeepTableUpdated keepTableUpdated;
	private int updatesToGet;
	private StatusesType statusesType;
	private GridBagConstraints gbc;
	private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();

	private String text, screenName, groupName;
	private String[] userId;
	private URL profileImageURL;
	
	//(ATUALIZAÇÃO)
	//mudança de int para long
	private long senderId;
	private long responseId;
	private long[] lastResponseId;
	private Date date;
	private TwitterResponse currentResponse;

	public StatusesTableThread(String[] group, String groupName) {
		this(StatusesType.UPDATES);
		this.userId = group;
		if(group.length>1) {
			isGroup = true;
			this.groupName = groupName;
			this.lastResponseId = new long[group.length];
			for(int i=0; i<lastResponseId.length; i++)
				lastResponseId[i] = -1;
		}
	}

	public StatusesTableThread(StatusesType type, String userIdOrSearchQuery) {		
		this(type);
		if(type == StatusesType.SEARCH)
			searchQuery = userIdOrSearchQuery;
		else 
			userId = new String[]{userIdOrSearchQuery};		
	}

	public StatusesTableThread(StatusesType type) {
		this.statusesType = type;
		this.userId = null;
		this.searchQuery = null;
		isTwitterUser = controller.isTwitterUser();		
		rows = 0;
		interval = getUpdateInterval();
		updatesToGet = 100;
		senderId = -1;
		responseId = -1;
		lastResponseId = new long[]{-1};
		date = null;
		isGroup = false;
		counterLabel = new JLabel();
		counterLabel.setFont(new Font("Tahoma",1,11));
	}
	
	public long getUpdateInterval() {
		switch(statusesType) {
		case UPDATES: 
			return controller.getProperty(ConfigurationType.intervalUpdates);			
		case REPLIES:
			return controller.getProperty(ConfigurationType.intervalMentions);
		case FAVORITES:
			return controller.getProperty(ConfigurationType.intervalFavorites);
		case SEARCH:
			return controller.getProperty(ConfigurationType.intervalSearch);
		case PUBLIC_TIMELINE:
			return controller.getProperty(ConfigurationType.intervalPublicTimeline);
		case DIRECT_MESSAGES:
		case DIRECT_MESSAGES_RECEIVED:
		case DIRECT_MESSAGES_SENT:
			return controller.getProperty(ConfigurationType.intervalDirectMessages);
		default:
			return controller.getProperty(ConfigurationType.intervalUpdates);
		}		
	}

	public StatusesType getType() {
		return statusesType;
	}
	
	public JLabel getCounterLabel() {
		return counterLabel;
	}

	public JPanel getContent() {		
		if(panel!=null) return panel;
		GridBagLayout layout = new GridBagLayout();        
		panel = new JPanel(layout);		
		keepTableUpdated = new KeepTableUpdated();
		keepTableUpdated.start();		
		return panel;
	}

	private JPanel getStatusesPanel() {
		JPanel updatePanel = new JPanel(new GridBagLayout());        
		gbc = new GridBagConstraints();

		if(rows%2==0)
			updatePanel.setBackground(ChartColor.LIGHT_GRAY);       
		else
			updatePanel.setBackground(ChartColor.LIGHT_GRAY.brighter());

		gbc.weightx = 0;		
		gbc.gridx = 0;
		gbc.insets = new Insets(0,3,0,1);
		
		//(ATUALIZAÇÃO)
		//mudança de int para long
		final long senderIdAux = senderId;
		
		final JLabel interactiveImage = new JLabel();
		interactiveImage.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		interactiveImage.setPreferredSize(new Dimension(48,48));
		
		interactiveImage.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
				interactiveImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				controller.searchAndAddUserToNetwork(senderIdAux);
			}
		});
		updatePanel.add(interactiveImage,gbc);
		
		new LoadUserImage(senderId,profileImageURL,interactiveImage);

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

		editorPane.setText("<b><a href=http://www.twitter.com/"+screenName+">"
				+screenName+"</a></b>"
				+": "+text);
		//+"<br>"+s.getCreatedAt().toString());			


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
			updatePanel = addButtonsToPanel(updatePanel);
		
		return updatePanel;
	}

	private JPanel addButtonsToPanel(final JPanel updatePanel) {
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(4,0,0,0);
		gbc.anchor = GridBagConstraints.NORTH;

		final JLabel replyButton = new JLabel();	
		ImageIcon replyIcon = new ImageIcon(getClass().getResource("/reply.png"));	
		replyButton.setIcon(replyIcon);		
		replyButton.setToolTipText("Reply");
		final String screenNameAux = screenName;
		final long responseIdAux = responseId;
		replyButton.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
				replyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				if(currentResponse instanceof DirectMessage) 
					controller.openGUINewUpdateWindow(screenNameAux,StatusesType.DIRECT_MESSAGES);
				else
					controller.openGUINewUpdateWindow(screenNameAux,StatusesType.REPLIES);
			}
		});
		updatePanel.add(replyButton,gbc);

		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(1,0,0,0);
		gbc.anchor = GridBagConstraints.CENTER;

		//final long responseIdAux = responseId;

		if(currentResponse instanceof Status) {
			final JLabel favoriteButton = new JLabel();
			ImageIcon starIcon;

			if(((Status)currentResponse).isFavorited())
				starIcon = new ImageIcon((getClass().getResource("/star_on.png")));		
			else 
				starIcon = new ImageIcon((getClass().getResource("/star_off.png")));		
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
							controller.getTwitter().createFavorite(responseIdAux);
							favoriteButton.setIcon(new ImageIcon((getClass().getResource("/star_on.png"))));
						}
						else {
							controller.getTwitter().destroyFavorite(responseIdAux);
							favoriteButton.setIcon(new ImageIcon((getClass().getResource("/star_off.png"))));
							if(statusesType == StatusesType.FAVORITES) {
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
		}

		//(ATUALIZAÇÃO)
		//mudança de int para long
		//mudança de Integer.parseInt para Long.parseLong
		long loggedUserId = Long.parseLong(controller.getLoggedUserId());

		if(loggedUserId == senderId || currentResponse instanceof DirectMessage) {
			gbc.weightx = 0;
			gbc.gridx = 2;
			gbc.gridy = 0;
			if(currentResponse instanceof DirectMessage)
				gbc.anchor = GridBagConstraints.CENTER;
			else
				gbc.anchor = GridBagConstraints.SOUTH;
			gbc.insets = new Insets(0,0,4,0);

			final JLabel deleteButton = new JLabel();
			ImageIcon deleteIcon = new ImageIcon((getClass().getResource("/trash.png")));	
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
							if(currentResponse instanceof DirectMessage)
								controller.getTwitter().destroyDirectMessage(responseIdAux);
							else
								controller.getTwitter().destroyStatus(responseIdAux);
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

	private void loadData(TwitterResponse response) {	
		currentResponse = response;
		if(response instanceof Status) {
			Status s = (Status) response;
			responseId = s.getId();
			text = processText(s.getText());
			//para poder exibir links
			//TODO s.setText(text);
			screenName = s.getUser().getScreenName();
			profileImageURL = s.getUser().getProfileImageURL();
			senderId = s.getUser().getId();
			date = s.getCreatedAt();			
			
			//setando no nodo a ultima atualizacao
			Node userNode = controller.getNode(senderId);
			if(userNode!=null)
				synchronized(userNode) {
					userNode.set("latestStatus", text);
				}
		}
		else if (response instanceof DirectMessage) {
			DirectMessage dm = (DirectMessage) response;
			responseId = dm.getId();
			text = processText(dm.getText());
			User u;
			if(statusesType==StatusesType.DIRECT_MESSAGES_SENT)
				u = dm.getRecipient();
			else
				u = dm.getSender();
			screenName = u.getScreenName();
			profileImageURL = u.getProfileImageURL();
			senderId = u.getId();
			date = dm.getCreatedAt();
		}
		/**
		else if (response instanceof Tweet) {
			Tweet t = (Tweet) response;
			responseId = t.getId();
			text = processText(t.getText());
			screenName = t.getFromUser();
			senderId = t.getFromUserId();
			try {
				profileImageURL = new URL(t.getProfileImageUrl());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			date = t.getCreatedAt();
		}
		*/
		else
			throw new IllegalArgumentException("Objeto inválido dentro da lista");
		
		if(!isGroup)
			lastResponseId[0] = responseId;
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
		if(isGroup)
			return groupName;
		return userId[0];
	}

	public boolean isGroup() {
		return isGroup;
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

	public List<TwitterResponse> getStatusesList() {				
		return  keepTableUpdated.getStatusesList();
	}

	class KeepTableUpdated extends Thread {		
		private GridBagConstraints c;
		private Twitter twitter = controller.getTwitter();
		private boolean threadSuspended;		
		private List<TwitterResponse> statusesList, allStatusesList;
		private List<? extends TwitterResponse> aux;
		private Set<Long> favoritesSet;

		public KeepTableUpdated() {			
			twitter = controller.getTwitter();
			c = new GridBagConstraints();
			threadSuspended = false;
			//here
			statusesList = new ArrayList<TwitterResponse>();
			allStatusesList = new ArrayList<TwitterResponse>();
			favoritesSet = new HashSet<Long>();
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

		public List<TwitterResponse> getStatusesList() {				
			return allStatusesList;
		}
		
		String notification = null;		
		public void run()
		{	
			System.out.println("STARTING " + statusesType + " para " + controller.getUserName(getUserId()));
			JPanel emptyPanel = null;			
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

					switch(statusesType)
					{
					case UPDATES:	
						if(!isGroup) {
							if(lastResponseId[0] < 0) {
								if(userId==null)
									aux = twitter.getFriendsTimeline(new Paging().count(updatesToGet));
								else
									aux = twitter.getUserTimeline(Long.parseLong(userId[0]), new Paging().count(updatesToGet));
							}
							else {
								if(userId==null)
									aux = twitter.getFriendsTimeline(new Paging().sinceId(lastResponseId[0]));
								else
									
									//(ATUALIZAÇÃO)
									//aux = twitter.getUserTimeline(userId[0], updatesToGet, lastResponseId[0]);
									//aux = twitter.getUserTimeline(Long.parseLong(userId[0]), new Paging().count(updatesToGet).sinceId(lastResponseId[0]));
									
									//Aparentemente, assim não ocorrem mais NullPointerExceptions como ocorriam	 algumas vezes
									//quando aux era igual ao último comentário acima
									aux = twitter.getUserTimeline(Long.parseLong(userId[0]), new Paging(1, updatesToGet, lastResponseId[0]));
							}
						}
						else {
							List<Status> group = new ArrayList<Status>();
							List<Status> l;
							for(int i=0; i<userId.length; i++) {
								if(lastResponseId[i] < 0) 
									l = twitter.getUserTimeline(Long.parseLong(userId[i]), new Paging().count(updatesToGet));									
								else
									//(ATUALIZAÇÃO)
									//seguindo a sugestão, código antigo comentado
									
									l = twitter.getUserTimeline(Long.parseLong(userId[i]), new Paging().count(updatesToGet).sinceId(lastResponseId[i]));
								//l = twitter.getUserTimeline(userId[i], updatesToGet, lastResponseId[i]);									
								//talvez seja melhor fazer userId, new Paging().count(updatesToGet).sinceId(lastStatusId)
								//testar
								if(l.size()>0)
									lastResponseId[i] = l.get(0).getId();
								group.addAll(l);
							}
							Collections.sort(group);
							
							for(int i=group.size()-1; i>=0; i--) {
								statusesList.add((TwitterResponse)group.get(i));
							}
						}					
						notification = "tweet";
						System.out.println("GOT UPDATES");
						break;

					case FAVORITES:						
						if(userId==null)
							aux = twitter.getFavorites();
						else
							//TODO erro aqui quando está explorando usuário sem fazer login
							aux = twitter.getFavorites(userId[0]);		
						notification = "favorito";
						break;

					case REPLIES:
						if(lastResponseId[0] < 0) 
							aux = twitter.getMentions();
						else
							aux = twitter.getMentions(new Paging(lastResponseId[0]));
						notification = "reply";
						break;

					case DIRECT_MESSAGES_RECEIVED:
						if(lastResponseId[0] < 0) 
							aux = twitter.getDirectMessages();
						else
							aux = twitter.getDirectMessages(new Paging(lastResponseId[0]));	
						notification = "mensagem";
						break;

					case DIRECT_MESSAGES_SENT:
						if(lastResponseId[0] < 0) 
							aux = twitter.getSentDirectMessages();
						else
							aux = twitter.getSentDirectMessages(new Paging(lastResponseId[0]));
						break;

					case SEARCH:
						//TODO: POSSIBILITAR MULTIPLAS PESQUISAS
						//SALVAR PESQUISAS/UPDATES NO COMPUTADOR PARA CARREGAR MAIS RAPIDO DEPOIS?
						Query query = new Query(searchQuery);
						if(lastResponseId[0] >= 0) 
							query.setSinceId(lastResponseId[0]);
						query.setRpp(updatesToGet);

						QueryResult results = twitter.search(query);

						//TODO arrumar esse erro maldito
						/**
						List<Tweet> tweets = results.getTweets();
						statusesList = new ArrayList<TwitterResponse>(tweets.size());
						for(Tweet t : tweets) {
							statusesList.add(t);
						}
						
						notification = "resultado";
						break;
						*/

					case PUBLIC_TIMELINE:
						if(lastResponseId[0]<0)							
							aux = twitter.getPublicTimeline();
						else
							//(ATUALIZAÇÃO)
							//removido o argumento lastResponseId[0], pois não
							//há mais o método getPublicTimeline(int sinceId)
							aux = twitter.getPublicTimeline();	
						notification = "tweet na Public Timeline";
						break;
					}

					if(statusesType != StatusesType.SEARCH && !isGroup) {
						statusesList = new ArrayList<TwitterResponse>(aux.size());
						for(Object x : aux) {								
							statusesList.add((TwitterResponse)x);
						}					
					}
					int contNewUpdates = 0;
					if(!statusesList.isEmpty()) {						
						if(emptyPanel != null)
							panel.remove(emptyPanel); 							
						//de trás para frente, para adicionar as mais recentes em cima
						for(int i=statusesList.size()-1; i>=0; i--) {							
							if(statusesType == StatusesType.FAVORITES) {
								Status latestFavorite = ((Status)statusesList.get(i));
								if(favoritesSet.contains(latestFavorite.getId()))
										continue;
								else {
									favoritesSet.add(latestFavorite.getId());
									contNewUpdates++;
								}
							}
							else
								contNewUpdates++;
							loadData(statusesList.get(i));								
							c.weightx = 0.5;
							c.fill = GridBagConstraints.HORIZONTAL;
							c.gridx = 0;
							panel.add(getStatusesPanel(),c,0);			
							rows++;
							allStatusesList.add(0,statusesList.get(i));							
						}
						if(contNewUpdates>0)
							setStatusBarMessage(contNewUpdates);
						notification = null;
						//counterLabel.setText(String.valueOf(rows) + " "+getType().toString().toLowerCase());						
						c.weightx = 0.5;
						c.weighty = 1;
						c.fill = GridBagConstraints.HORIZONTAL;
						c.gridx = 0;
						c.anchor = GridBagConstraints.PAGE_END;	
						panel.revalidate();
					}
					else {
						if(allStatusesList.size()==0 && panel.getComponentCount() == 0) {
							emptyPanel = new JPanel();
							JLabel emptyLabel = new JLabel("Não há tweets para exibir.");
							emptyLabel.setFont(new Font("Tahoma",1,12));
							emptyPanel.add(emptyLabel);
							panel.add(emptyPanel,c);
							panel.revalidate();
						}
					}
						
					if(isGroup)
						statusesList.clear();

					System.out.println("sleeping for "+interval);
					Thread.sleep(interval);	
					controller.liberaMemoria();
					System.out.println("running " + statusesType + " para " + controller.getUserName(getUserId()));
				} catch (TwitterException e) {
					System.out.println("Status code: "+e.getStatusCode());
					e.printStackTrace();
					if(e.getStatusCode()==400) {
						controller.showMessageDialog("Você excedeu o limite de requisições por hora permitido pelo Twitter. Aguarde e tente novamente.",MessageType.ERROR);
						break;
					}
					else if(e.getStatusCode()==401) {
						emptyPanel = new JPanel();
						JLabel emptyLabel = new JLabel("Você não está autorizado a ver os tweets deste usuário.");
						emptyLabel.setFont(new Font("Tahoma",1,12));
						emptyLabel.setForeground(Color.RED);
						emptyPanel.add(emptyLabel);
						panel.add(emptyPanel,c);		
						panel.revalidate();
						break;
					}
					else if(e.getStatusCode()==-1) {
						//controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
					}
					else
						controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
				}
				catch(InterruptedException ie) {
					System.out.println("INTERRUPTED " + statusesType + " para " + controller.getUserName(getUserId()));
					break;
				}
				interval = getUpdateInterval();
			}			
		}
		
		void setStatusBarMessage(int contNewUpdates) {
			String novo = " novo ";
			if(notification != null) {
				if(contNewUpdates>1) {
					novo = " novos ";
					if(notification.equals("mensagem")) { notification = "mensagens"; novo = " novas "; }
					else if(notification.equals("reply")) notification = "replies";
					else if(notification.equals("tweet na Public Timeline")) notification = "tweets na Public Timeline"; 
					else 
						notification +="s";
				} 
				controller.setStatusBarMessage(contNewUpdates + novo + notification + " para " +
						controller.getUserName(getUserId()), MessageType.NOTIFICATION);
			}	
		}
	}
	
	class LoadUserImage extends Thread {
		private URL url;
		private long senderId;
		private JLabel interactiveImage;
		
		public LoadUserImage(long senderId, URL url, JLabel interactiveImage) {
			this.senderId = senderId;
			this.url = url;
			this.interactiveImage = interactiveImage;
			start();
		}
		
		public void run() {			
			if(controller.getUserImageMap().containsKey(senderId)) 
				interactiveImage.setIcon((ImageIcon)controller.getUserImageMap().get(senderId));				
			else {			
				ImageIcon userPicture = new ImageIcon(url);
				if(userPicture.getIconHeight()>48 || userPicture.getIconWidth()>48) {
					Image image = userPicture.getImage().getScaledInstance(48, 48, Image.SCALE_DEFAULT);					
					userPicture.setImage(image);						
				}							
				interactiveImage.setIcon(userPicture);
				if(statusesType != StatusesType.PUBLIC_TIMELINE && statusesType != StatusesType.SEARCH)
					controller.getUserImageMap().put(senderId, userPicture);
			}			
		}
	}
}


