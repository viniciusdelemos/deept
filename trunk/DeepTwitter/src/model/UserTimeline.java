package model;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import twitter4j.Status;
import twitter4j.TwitterException;
import controller.ControllerDeepTwitter;

public class UserTimeline {
	private int rows;
	private String userId;
	private boolean isTwitterUser;
	private ArrayList<Status> statusList;
	private Dimension panelDimension;
	private static long interval;
	private JPanel panel;
	private KeepTimelineUpdated keepTimelineUpdated;
	private int updatesToGet;
	
	public UserTimeline (String userId, boolean isTwitterUser) {
		this.userId = userId;
		this.isTwitterUser = isTwitterUser;
		statusList = new ArrayList<Status>();
		panelDimension = new Dimension(250,60);
		rows = 0;
		interval = 300000; //5 minutos
		updatesToGet = 100;
	}

	public JPanel getContent() {		
		GridBagLayout layout = new GridBagLayout();        
		panel = new JPanel(layout);
		
		keepTimelineUpdated = new KeepTimelineUpdated();
		keepTimelineUpdated.start();
		
		return panel;
	}
	
	private JPanel getPanel(Status s) {
		GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        if(rows%2==0)
        	panel.setBackground(ChartColor.LIGHT_GRAY);       
        else
        	panel.setBackground(ChartColor.LIGHT_GRAY.brighter());
        
        gbc.weightx = 0;		
		gbc.gridx = 0;
		//gbc.gridy = 0;
        
        panel.add(new JLabel(new ImageIcon(s.getUser().getProfileImageURL())),gbc);
       
        gbc.weightx = 1;
        gbc.gridx = 1;
        //gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
       // panel.add(new JLabel(" "+s.getText()),gbc);
        
        JTextArea updateArea = new JTextArea(s.getText());
        updateArea.setColumns(40);
        updateArea.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        updateArea.setLineWrap(true);
        updateArea.setRows(2);
        updateArea.setTabSize(0);
        updateArea.setWrapStyleWord(true);       
        updateArea.setEditable(false);
        updateArea.setBackground(panel.getBackground());
        panel.add(updateArea,gbc);

        return panel;
    }
	
	public void setNumberOfUpdatesToGet(int x) {
		updatesToGet = x;
	}
	
	public static void setUpdateTimelineInterval(long milliseconds) {
		interval = milliseconds;
	}
	
	public void stopThread() {
		keepTimelineUpdated.interrupt();
	}
	
	class KeepTimelineUpdated extends Thread {
		private Date lastStatusDate = null;
		private GridBagConstraints c = new GridBagConstraints();
		private JPanel empty;
		private long millisecondsCheck = 0;
		
		public void run()
		{
			while(true) {
				try {
					if(lastStatusDate == null) {
						if(isTwitterUser && userId.equals(ControllerDeepTwitter.getLoggedUserId()))				
							statusList = (ArrayList<Status>) ControllerDeepTwitter.getTwitter().getFriendsTimeline();
						else {
							System.out.println("Retrieving statuses");
							statusList = (ArrayList<Status>) ControllerDeepTwitter.getTwitter().getUserTimeline(userId,updatesToGet);
							System.out.println("GOT statuses");
						}
					}
					else {						
						if(isTwitterUser && userId.equals(ControllerDeepTwitter.getLoggedUserId()))				
							statusList = (ArrayList<Status>) ControllerDeepTwitter.getTwitter().getFriendsTimeline(lastStatusDate);
						else {
							System.out.println("Retrieving statuses");
							statusList = (ArrayList<Status>) ControllerDeepTwitter.getTwitter().getUserTimeline(userId,updatesToGet,lastStatusDate);
							System.out.println("GOT statuses");
						}
					}
					
					if(!statusList.isEmpty()) {
						lastStatusDate = statusList.get(0).getCreatedAt();
						//checa se nova msg é igual à ultima já adicionada ao painel
						//MUDAR ESTRATEGIA => AUMENTAR 1S NA ULTIMA MSG
						if(lastStatusDate.getTime() != millisecondsCheck)
						{
							System.out.println("  LAST: "+lastStatusDate.toString());
							if(empty != null) panel.remove(empty);
							
							//de trás para frente, para adicionar as mais recentes em cima
							for(int i=statusList.size()-1; i>=0; i--) {
								Status s = statusList.get(i);
								System.out.println(s.getUser().getName()+" ==> "+s.getText());			
								c.weightx = 0.5;
								c.fill = GridBagConstraints.HORIZONTAL;
								c.gridx = 0;
								//c.gridy = rows;
								panel.add(getPanel(s),c,0);			
								rows++;
								//atualizar o painel de 5 em 5 para evitar lentidão
								//MUDAR DE LOCAL. PQ PODE PEGAR MENOS QUE 5 UPDATES.
								if(rows%5==0)panel.revalidate();
							}
							empty = new JPanel();
							empty.add(new JLabel(""));
							c.weightx = 0.5;
							c.weighty = 1;
							c.fill = GridBagConstraints.HORIZONTAL;
							c.gridx = 0;
							//c.gridy = rows;		
							c.anchor = GridBagConstraints.PAGE_END;
							panel.add(empty,c);
							panel.revalidate();
						}
						millisecondsCheck = lastStatusDate.getTime();
						statusList.clear();
					}				
					Thread.sleep(interval);					
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				catch(InterruptedException e2) {
					break;
				}
			}
		}
	}
}


