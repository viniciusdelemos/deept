package model;

import gui.GUINewUpdate;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import quicktime.app.image.JImagePainter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;

public class StatusesTable {
	private int rows;
	private String userId;
	private boolean isTwitterUser;
	private ArrayList<Status> statusList;
	private long interval;
	private JPanel panel;
	private KeepTableUpdated keepTableUpdated;
	private int updatesToGet;
	private GraphicManager gManager;
	private StatusesType statusesType;
	private GridBagConstraints gbc;
	
	public StatusesTable (GraphicManager gManager, String userId) {
		this(gManager,StatusesType.OTHERS_UPDATES);
		this.userId = userId;
	}
	
	public StatusesTable(GraphicManager gManager, StatusesType type) {
		this.gManager = gManager;
		this.statusesType = type;
		isTwitterUser = gManager.isTwitterUser();
		statusList = new ArrayList<Status>();		
		rows = 0;
		interval = 120000; //2 minutos
		updatesToGet = 100;
	}

	public JPanel getContent() {		
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
		final JLabel interactiveImage = new JLabel(new ImageIcon(u.getProfileImageURL()));
		interactiveImage.setSize(48, 48);
		interactiveImage.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
				interactiveImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				gManager.searchAndAddUserToNetwork(s.getUser());
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
	        			ControllerDeepTwitter.showMessageDialog(ex.getMessage(),MessageType.ERROR);
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
		ImageIcon replyIcon = new ImageIcon(getClass().getResource("../reply.jpg"));	
		replyButton.setIcon(replyIcon);		
		replyButton.setToolTipText("Reply");
		replyButton.addMouseListener(new MouseAdapter(){
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
				replyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				new GUINewUpdate(s.getUser().getScreenName()).setVisible(true);				
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
			starIcon = new ImageIcon(getClass().getResource("../star_on.png"));		
		else 
			starIcon = new ImageIcon(getClass().getResource("../star_off.png"));
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
						ControllerDeepTwitter.getTwitter().createFavorite(s.getId());
						favoriteButton.setIcon(new ImageIcon(getClass().getResource("../star_on.png")));
					}
					else {
						ControllerDeepTwitter.getTwitter().destroyFavorite(s.getId());
						favoriteButton.setIcon(new ImageIcon(getClass().getResource("../star_off.png")));
						if(statusesType == StatusesType.FAVORITES) {
							System.out.println("removing favorite panel");
							panel.remove(updatePanel);
							rows--;
						}
					}				
					panel.revalidate();
				}
				catch(TwitterException e) {
					ControllerDeepTwitter.showMessageDialog(e.getMessage(),MessageType.ERROR);
				}
			}
		});
		updatePanel.add(favoriteButton,gbc);
		
		int loggedUserId = Integer.parseInt(ControllerDeepTwitter.getLoggedUserId());
		if(loggedUserId == s.getUser().getId()) {
			gbc.weightx = 0;
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.insets = new Insets(0,0,4,0);
			
			final JLabel deleteButton = new JLabel();
			ImageIcon deleteIcon = new ImageIcon(getClass().getResource("../remove.png"));	
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
							ControllerDeepTwitter.getTwitter().destroyStatus(s.getId());
							panel.remove(updatePanel);
							rows--;
							panel.revalidate();
						}
					} catch (TwitterException e) {
						ControllerDeepTwitter.showMessageDialog(e.getMessage(),MessageType.ERROR);
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
	
	public void setUpdateTimelineInterval(long milliseconds) {
		interval = milliseconds;
	}
	
	public void stopThread() {
		keepTableUpdated.interrupt();
	}
	
	class KeepTableUpdated extends Thread {
		private Date lastStatusDate = null;
		private long lastStatusId = -1;
		private GridBagConstraints c = new GridBagConstraints();
		private JPanel empty;
		private Twitter twitter = ControllerDeepTwitter.getTwitter();
		
		public void run()
		{
			System.out.println("=> Starting thread");
			while(true) {
				try {
					switch(statusesType)
					{
					case MY_UPDATES:
						if(lastStatusDate == null)
							statusList = (ArrayList<Status>) twitter.getFriendsTimeline();
						else
							statusList = (ArrayList<Status>) twitter.getFriendsTimeline(lastStatusDate);
						break;

					case OTHERS_UPDATES:
						if(lastStatusDate == null)
							statusList = (ArrayList<Status>) twitter.getUserTimeline(userId,updatesToGet);
						else
							statusList = (ArrayList<Status>) twitter.getUserTimeline(userId,updatesToGet,lastStatusDate);
						break;

					case FAVORITES:
						statusList = (ArrayList<Status>) twitter.favorites();
						break;

					case REPLIES:
						statusList = (ArrayList<Status>) twitter.getReplies();
						break;

					case PUBLIC_TIMELINE:
						statusList = (ArrayList<Status>) twitter.getPublicTimeline();
						break;
					}

					if(!statusList.isEmpty()) {
						lastStatusDate = statusList.get(0).getCreatedAt();

						//checa se nova msg é igual à ultima já adicionada ao painel
						if(statusList.get(0).getId()>lastStatusId)
						{
							if(empty != null) panel.remove(empty);							
							//de trás para frente, para adicionar as mais recentes em cima
							for(int i=statusList.size()-1; i>=0; i--) {
								Status s = statusList.get(i);
								//para evitar bug da API do twitter
								if(s.getId()>lastStatusId) {
									c.weightx = 0.5;
									c.fill = GridBagConstraints.HORIZONTAL;
									c.gridx = 0;
									panel.add(getPanel(s),c,0);			
									rows++;
									panel.revalidate();
									lastStatusId = s.getId();
								}								
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
						statusList.clear();
					}				
					Thread.sleep(interval);	
					System.out.println("running");
				} catch (TwitterException e) {
					ControllerDeepTwitter.showMessageDialog(e.getMessage(),MessageType.ERROR);
					e.printStackTrace();
				}
				catch(InterruptedException ie) {
					System.out.println("interrupted exception");
					break;
				}
			}
		}
	}
}


