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
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import model.ChartColor;
import model.MessageType;
import model.StatusDeepT;
import model.StatusesType;
import model.URLLinkAction;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;
import demo.wordfreq.WordFreq;
import examples.Visualizacao1;
import gui.visualizations.GUITimeline;

public class StatusesTableThread {
	private int rows;
	private String userId;
	private boolean isTwitterUser;
	private ArrayList<Status> statusesList;
	private ArrayList<StatusDeepT> allStatusesList;
	private long interval;
	private JPanel panel;
	private KeepTableUpdated keepTableUpdated;
	private int updatesToGet;
	private StatusesType statusesType;
	private GridBagConstraints gbc;
	private ControllerDeepTwitter controller;
	
	public StatusesTableThread (String userId) {		
		this(StatusesType.UPDATES);
		this.userId = userId;		
	}
	
	public StatusesTableThread(StatusesType type) {
		this.statusesType = type;
		this.userId = null;
		controller = ControllerDeepTwitter.getInstance();
		isTwitterUser = controller.isTwitterUser();
		statusesList = new ArrayList<Status>();
		allStatusesList = new ArrayList<StatusDeepT>();
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
	
	private JPanel getPanel(final Status s) {		
        JPanel updatePanel = new JPanel(new GridBagLayout());        
        gbc = new GridBagConstraints();
        
        if(rows%2==0)
        	updatePanel.setBackground(ChartColor.LIGHT_GRAY);       
        else
        	updatePanel.setBackground(ChartColor.LIGHT_GRAY.brighter());
        
        gbc.weightx = 0;		
		gbc.gridx = 0;
		gbc.insets = new Insets(0,3,0,1);
		
		final User u = s.getUser();
		
		JLabel interactiveImageAux;
		ImageIcon userPicture = new ImageIcon(u.getProfileImageURL());
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
				controller.searchAndAddUserToNetwork(s.getUser());
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
		editorPane.setText("<b><a href=http://www.twitter.com/"+u.getScreenName()+">"
				+u.getScreenName()+"</a></b>"
				+": "+processText(s.getText()));
				//+"<br>"+s.getCreatedAt().toString());
		
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						new URLLinkAction(e.getURL().toString());
	        		} catch (Exception ex) {
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
	
	private JPanel addButtonsToPanel(final JPanel updatePanel, final Status s) {
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
				controller.openGUINewUpdateWindow(s.getUser().getScreenName());				
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
		if(s.isFavorited()) 
			starIcon = new ImageIcon(getClass().getResource("../../star_on.png"));		
		else 
			starIcon = new ImageIcon(getClass().getResource("../../star_off.png"));
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
						controller.getTwitter().createFavorite(s.getId());
						favoriteButton.setIcon(new ImageIcon(getClass().getResource("../../star_on.png")));
					}
					else {
						controller.getTwitter().destroyFavorite(s.getId());
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
		if(loggedUserId == s.getUser().getId()) {
			gbc.weightx = 0;
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.insets = new Insets(0,0,4,0);
			
			final JLabel deleteButton = new JLabel();
			ImageIcon deleteIcon = new ImageIcon(getClass().getResource("../../remove.png"));	
			deleteButton.setIcon(deleteIcon);	
			deleteButton.setToolTipText("Delete");
			deleteButton.addMouseListener(new MouseAdapter(){
				public void mouseEntered(java.awt.event.MouseEvent arg0) {
					deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				public void mouseClicked(java.awt.event.MouseEvent arg0) {
					try {
						int n = JOptionPane.showConfirmDialog(
	                            null, "Tem certeza que deseja deletar este update?","Aten��o",
	                            JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null);
						if (n == JOptionPane.YES_OPTION) {
							controller.getTwitter().destroyStatus(s.getId());
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
					htmlString = "<a href=http://www.twitter.com/"+url+">"+url+"</a>";
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
	
	public synchronized ArrayList<StatusDeepT> getStatusesList() {				
		return allStatusesList;
	}
	
	class KeepTableUpdated extends Thread {
		private long lastStatusId = -1;
		private GridBagConstraints c = new GridBagConstraints();
		private JPanel empty;
		private Twitter twitter = controller.getTwitter();
		private boolean threadSuspended = false;
		
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
					
					switch(statusesType)
					{
					case UPDATES:
						if(lastStatusId < 0) {
							System.out.println("last status ID < 0");
							if(userId==null)
								statusesList = (ArrayList<Status>) twitter.getFriendsTimeline();
							else
								statusesList = (ArrayList<Status>) twitter.getUserTimeline(userId,updatesToGet);
						}
						else {
							if(userId==null)
								statusesList = (ArrayList<Status>) twitter.getFriendsTimelineBySinceId(lastStatusId);
							else
								statusesList = (ArrayList<Status>) twitter.getUserTimelineBySinceId(userId, lastStatusId, updatesToGet);
						}						
						break;

					case FAVORITES:
						statusesList = (ArrayList<Status>) twitter.favorites();
						break;

					case REPLIES:
						statusesList = (ArrayList<Status>) twitter.getReplies();
						break;
					
					case DIRECT_MESSAGES:
						//TODO
						break;

					case PUBLIC_TIMELINE:
						statusesList = (ArrayList<Status>) twitter.getPublicTimeline();
						break;
					}
					
					if(!statusesList.isEmpty()) {
						//lastStatusId = statusesList.get(0).getId();
						System.out.println("LastStatusId: "+lastStatusId);
						//checa se nova msg � igual � ultima j� adicionada ao painel
						if(statusesList.get(0).getId()>lastStatusId)
						{
							System.out.println("Novo update com id maior = "+statusesList.get(0).getId());
							if(empty != null) panel.remove(empty);							
							//de tr�s para frente, para adicionar as mais recentes em cima
							for(int i=statusesList.size()-1; i>=0; i--) {
								System.out.println("ESTOU NO FOR");
								Status s = statusesList.get(i);								
								c.weightx = 0.5;
								c.fill = GridBagConstraints.HORIZONTAL;
								c.gridx = 0;
								panel.add(getPanel(s),c,0);			
								rows++;
								panel.revalidate();
								lastStatusId = s.getId();
								allStatusesList.add(0,new StatusDeepT(s));
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
					Thread.sleep(interval);	
					System.out.println("running " + statusesType + " para " + controller.getUserName(getUserId()));
				} catch (TwitterException e) {
					if(e.getStatusCode()==400) {
						controller.showMessageDialog("Voc� excedeu o limite de 100 requisi��es por hora permitido pelo Twitter. Aguarde e tente novamente.",MessageType.ERROR);
						break;
					}
					else if(e.getStatusCode()==401) {
						controller.showMessageDialog("Voc� n�o est� autorizado a ver os updates desta pessoa.",MessageType.ERROR);
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


