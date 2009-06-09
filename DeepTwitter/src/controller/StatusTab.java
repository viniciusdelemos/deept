package controller;

import gui.GUITimeline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

import model.StatusesType;
import model.threads.StatusesTableThread;

public class StatusTab {
	private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToggleButton buttonTurnOnOff;
    private javax.swing.JToggleButton buttonInbox;
    private javax.swing.JToggleButton buttonOutbox;
    private javax.swing.JButton buttonSearchUpdates;
    private javax.swing.JButton buttonNewDirectMessage;
    private javax.swing.JButton buttonNextUser;
    private javax.swing.JButton buttonPreviousUser;
    private javax.swing.JButton buttonCloseUpdates;
    private javax.swing.JButton buttonTimeline;
    private javax.swing.JTextField txtCurrentUser;
	private Map<String,StatusesTableThread> tablesMap;
	private StatusesType type;
	private TabListener tabListener;
	private int currentTable;
	private boolean hasMultiplePanels, isGroup;
	private ArrayList<String> idArray;
	private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
	
	public StatusTab(JTabbedPane pane, StatusesType type, String name) {
		initComponents();
		pane.addTab(name, createAndGetPanel(type));
		this.type = type;
		idArray = new ArrayList<String>();			
		currentTable = 0;
		if(type == StatusesType.UPDATES || type == StatusesType.FAVORITES) 		
			hasMultiplePanels = true;		
		tablesMap = new HashMap<String,StatusesTableThread>();
	}
	
	private JPanel createAndGetPanel(StatusesType type) {
		this.type = type;
		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new java.awt.BorderLayout());
		addToolBar(jPanel2,type);
		jPanel2.add(jScrollPane6, java.awt.BorderLayout.CENTER);		
		return jPanel2;
	}	
	
	public void setPanelContent(StatusesTableThread newTable) {
		String userId = newTable.getUserId();
		StatusesTableThread selectedTable = tablesMap.get(userId);	
		isGroup = newTable.isGroup();
		if(selectedTable == null) {
			JPanel content = newTable.getContent();
			jScrollPane6.setViewportView(content);
			if(newTable.getType() == StatusesType.DIRECT_MESSAGES_RECEIVED)
				userId = "received";
			else if(newTable.getType() == StatusesType.DIRECT_MESSAGES_SENT)
				userId = "sent";
			else if(isGroup)
				setCurrentUserName(userId);
			else 
				setCurrentUserName(controller.getUserName(userId));
			tablesMap.put(userId,newTable);
			idArray.add(userId);
			if(hasMultiplePanels) {	
				currentTable = idArray.size()-1;
				reconfigButtons();
			}
		}
		else {
			currentTable = idArray.indexOf(userId);
			jScrollPane6.setViewportView(selectedTable.getContent());
			if(selectedTable.getType()!=StatusesType.DIRECT_MESSAGES_RECEIVED &&
					selectedTable.getType()!=StatusesType.DIRECT_MESSAGES_SENT)
				reconfigButtons();
			jScrollPane6.revalidate();
		}
		reconfigOnOffButton();		
	}
	
	private void setPanelContent(String userId) {
		StatusesTableThread selectedTable = tablesMap.get(userId);
		jScrollPane6.setViewportView(selectedTable.getContent());
		jScrollPane6.revalidate();
		reconfigOnOffButton();
		if(selectedTable.getType()!=StatusesType.DIRECT_MESSAGES_RECEIVED &&
				selectedTable.getType()!=StatusesType.DIRECT_MESSAGES_SENT)
			setCurrentUserName(controller.getUserName(userId));		
	}
	
	public void setCurrentUserName(String name) {
		txtCurrentUser.setText(name);
	}
	
	public boolean isActive() {
		return tablesMap.size() > 0;
	}
	
	public void stopThreads() {
		Set<String> keys = tablesMap.keySet();
		Iterator<String> i = keys.iterator();
		while(i.hasNext()) {
			tablesMap.get(i.next()).interruptThread();	
		}
	}
	
	public StatusesType getType() {
		return type;
	}
	
	private void addToolBar(JPanel panel, StatusesType typeOfTab) {
		jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        
        buttonNewDirectMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("../mail_add.png"))); // NOI18N
        buttonNewDirectMessage.setToolTipText("Nova mensagem direta");
        buttonNewDirectMessage.setActionCommand("buttonNewDirectMessage");
        buttonNewDirectMessage.setFocusable(false);
        buttonNewDirectMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNewDirectMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonNewDirectMessage);
                
        buttonPreviousUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("../backward.png"))); // NOI18N
        buttonPreviousUser.setToolTipText("Usuário anterior");
        buttonPreviousUser.setActionCommand("buttonPreviousUser");
        buttonPreviousUser.setEnabled(false);
        buttonPreviousUser.setFocusable(false);
        buttonPreviousUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPreviousUser.setMinimumSize(new java.awt.Dimension(31, 31));
        buttonPreviousUser.setName("buttonPreviousUser"); // NOI18N
        buttonPreviousUser.setPreferredSize(new java.awt.Dimension(31, 31));
        buttonPreviousUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonPreviousUser);

        if(type == StatusesType.SEARCH) {
        	txtCurrentUser.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
            txtCurrentUser.setHorizontalAlignment(javax.swing.JTextField.LEFT);
            txtCurrentUser.setText("busca");
            txtCurrentUser.setAutoscrolls(false);
            txtCurrentUser.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
            txtCurrentUser.setMargin(new java.awt.Insets(0, 0, 0, 0));
            txtCurrentUser.setMaximumSize(new java.awt.Dimension(110, 16));
            txtCurrentUser.setMinimumSize(new java.awt.Dimension(110, 16));
            txtCurrentUser.setPreferredSize(new java.awt.Dimension(50, 16));
            txtCurrentUser.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                	System.out.println("FOCUS GAINED");
                    txtCurrentUser.selectAll();
                }
            });
        }
        else {
        	txtCurrentUser.setBackground(new java.awt.Color(240, 240, 240));
        	txtCurrentUser.setEditable(false);
        	txtCurrentUser.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        	txtCurrentUser.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        	txtCurrentUser.setText("username");
        	txtCurrentUser.setAutoscrolls(false);
        	txtCurrentUser.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(240, 240, 240), 0, true));
        	txtCurrentUser.setMargin(new java.awt.Insets(0, 0, 0, 0));
        	txtCurrentUser.setMaximumSize(new java.awt.Dimension(120, 14));
        	txtCurrentUser.setMinimumSize(new java.awt.Dimension(120, 14));
        	txtCurrentUser.setPreferredSize(new java.awt.Dimension(60, 14));        	
        }
        jToolBar1.add(txtCurrentUser);
        
        buttonNextUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("../forward.png"))); // NOI18N
        buttonNextUser.setToolTipText("Próximo usuário");
        buttonNextUser.setActionCommand("buttonNextUser");
        buttonNextUser.setEnabled(false);
        buttonNextUser.setFocusable(false);
        buttonNextUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNextUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonNextUser);
        
        buttonSearchUpdates.setIcon(new javax.swing.ImageIcon(getClass().getResource("../search.png"))); // NOI18N
        buttonSearchUpdates.setToolTipText("Buscar");
        buttonSearchUpdates.setActionCommand("buttonSearchUpdates");
        buttonSearchUpdates.setFocusable(false);
        buttonSearchUpdates.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSearchUpdates.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonSearchUpdates);

        buttonInbox.setIcon(new javax.swing.ImageIcon(getClass().getResource("../mail_inbox.png"))); // NOI18N
        buttonInbox.setSelected(true);
        buttonInbox.setToolTipText("Caixa de Entrada");
        buttonInbox.setActionCommand("buttonInbox");
        buttonInbox.setFocusable(false);
        buttonInbox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonInbox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonInbox);

        buttonOutbox.setIcon(new javax.swing.ImageIcon(getClass().getResource("../mail_outbox.png"))); // NOI18N
        buttonOutbox.setToolTipText("Caixa de Saída");
        buttonOutbox.setActionCommand("buttonOutbox");
        buttonOutbox.setFocusable(false);
        buttonOutbox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonOutbox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonOutbox);
        
        jToolBar1.add(jSeparator4);

        buttonCloseUpdates.setIcon(new javax.swing.ImageIcon(getClass().getResource("../remove.png"))); // NOI18N
        buttonCloseUpdates.setToolTipText("Fechar este usuário");
        buttonCloseUpdates.setActionCommand("buttonCloseUpdates");
        buttonCloseUpdates.setEnabled(true);
        buttonCloseUpdates.setFocusable(false);
        buttonCloseUpdates.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCloseUpdates.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonCloseUpdates);
        
        buttonTurnOnOff.setIcon(new javax.swing.ImageIcon(getClass().getResource("../turn_on.png"))); // NOI18N
        buttonTurnOnOff.setSelected(true);
        buttonTurnOnOff.setToolTipText("Ligar/desligar atualizar automaticamente");
        buttonTurnOnOff.setActionCommand("buttonTurnOnOff");
        buttonTurnOnOff.setFocusable(false);
        buttonTurnOnOff.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTurnOnOff.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("../turn_off.png"))); // NOI18N
        buttonTurnOnOff.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonTurnOnOff);
        
        jToolBar1.add(jSeparator5);

        buttonTimeline.setIcon(new javax.swing.ImageIcon(getClass().getResource("../eye.png"))); // NOI18N
        buttonTimeline.setToolTipText("Ver timeline de atualizações");
        buttonTimeline.setActionCommand("buttonTimeline");
        buttonTimeline.setFocusable(false);
        buttonTimeline.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTimeline.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonTimeline);
        
        reconfigToolBarBasedOnTabType();
        
		panel.add(jToolBar1, java.awt.BorderLayout.PAGE_START);
	}
	
	private void reconfigToolBarBasedOnTabType() {		
		switch(this.type) {
		case REPLIES:
		case PUBLIC_TIMELINE:
			txtCurrentUser.setVisible(false);
			buttonPreviousUser.setVisible(false);
			buttonNextUser.setVisible(false);
			jSeparator4.setVisible(false);
			buttonCloseUpdates.setVisible(false);
		case UPDATES:			
		case FAVORITES:
			buttonInbox.setVisible(false);
			buttonOutbox.setVisible(false);
			buttonSearchUpdates.setVisible(false);
			buttonNewDirectMessage.setVisible(false);
			break;
		case DIRECT_MESSAGES:
			buttonSearchUpdates.setVisible(false);
			txtCurrentUser.setVisible(false);
			buttonPreviousUser.setVisible(false);
			buttonNextUser.setVisible(false);
			buttonCloseUpdates.setVisible(false);		
			break;
		case SEARCH:
			buttonInbox.setVisible(false);
			buttonOutbox.setVisible(false);
			buttonNewDirectMessage.setVisible(false);	
			buttonPreviousUser.setVisible(false);
			buttonNextUser.setVisible(false);
			buttonCloseUpdates.setVisible(false);		
			break;
		}
	}
	
	private void initComponents() {
		jToolBar1 = new JToolBar();
		jScrollPane6 = new JScrollPane();
		jSeparator2 = new Separator();
		jSeparator4 = new Separator();
		jSeparator5 = new Separator();
		buttonTurnOnOff = new JToggleButton();
		buttonTimeline = new JButton();
	    buttonNextUser = new JButton();
	    buttonPreviousUser = new JButton();
	    buttonCloseUpdates = new JButton();
	    txtCurrentUser = new javax.swing.JTextField();
	    buttonSearchUpdates = new javax.swing.JButton();
        buttonInbox = new javax.swing.JToggleButton();
        buttonOutbox = new javax.swing.JToggleButton();
        buttonNewDirectMessage = new javax.swing.JButton();
	    
	    tabListener = new TabListener();	    
	    buttonCloseUpdates.addActionListener(tabListener);
        buttonPreviousUser.addActionListener(tabListener);
        buttonNextUser.addActionListener(tabListener);
        buttonTurnOnOff.addActionListener(tabListener);
        buttonTimeline.addActionListener(tabListener);
        buttonSearchUpdates.addActionListener(tabListener);
        buttonInbox.addActionListener(tabListener);
        buttonOutbox.addActionListener(tabListener);
        buttonNewDirectMessage.addActionListener(tabListener);
	}
	
	private void reconfigButtons() {
		if(idArray.size()<=1) {
			buttonPreviousUser.setEnabled(false);
			buttonNextUser.setEnabled(false);
		}
		else if(currentTable==idArray.size()-1) {
			buttonPreviousUser.setEnabled(true);
			buttonNextUser.setEnabled(false);
		}			
		else if(currentTable==0) {
			buttonPreviousUser.setEnabled(false);
			buttonNextUser.setEnabled(true);
		}
		else {
			buttonPreviousUser.setEnabled(true);
			buttonNextUser.setEnabled(true);
		}
		if(idArray.get(currentTable).equals(controller.getLoggedUserId()))
			buttonCloseUpdates.setEnabled(false);
		else
			buttonCloseUpdates.setEnabled(true);
	}
	
	private void reconfigOnOffButton() {
		String id = idArray.get(currentTable);
		buttonTurnOnOff.setSelected(!tablesMap.get(id).isThreadSuspended());		
	}
	
	class TabListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("buttonNewUpdate")) {
				controller.openGUINewUpdateWindow();
			}
			else if(cmd.equals("buttonPreviousUser")) {
				currentTable--;
				reconfigButtons();
				reconfigOnOffButton();
				String userId = idArray.get(currentTable);
				setPanelContent(userId);				
			}
			else if(cmd.equals("buttonNextUser")) {
				currentTable++;
				reconfigButtons();
				reconfigOnOffButton();
				String userId = idArray.get(currentTable);				
				setPanelContent(userId);
			}
			else if(cmd.equals("buttonInbox")) {
				currentTable = 0;
				if(!buttonOutbox.isSelected())
						buttonInbox.setSelected(true);
				else {
					setPanelContent("received");					
				}
				buttonOutbox.setSelected(false);				
			}
			else if(cmd.equals("buttonOutbox")) {
				currentTable=1;
				if(!buttonInbox.isSelected())
					buttonOutbox.setSelected(true);
				else {
					if(tablesMap.get("sent")==null)
						setPanelContent(new StatusesTableThread(StatusesType.DIRECT_MESSAGES_SENT));
					else
						setPanelContent("sent");					
				}
				buttonInbox.setSelected(false);				
			}
			else if(cmd.equals("buttonCloseUpdates")) {
				String id = idArray.get(currentTable);
				tablesMap.get(id).interruptThread();
				tablesMap.remove(id);				
				
				if(currentTable==idArray.size()-1) {
					idArray.remove(currentTable);
					currentTable--;
				}
				else 
					idArray.remove(currentTable);			
					
				if(idArray.size()>0) {
					String userId = idArray.get(currentTable);
					setPanelContent(userId);
					reconfigButtons();
				}
				else {
					jScrollPane6.setViewportView(new JPanel());
					buttonTurnOnOff.setEnabled(false);
					buttonCloseUpdates.setEnabled(false);
					setCurrentUserName("");
				}								
			}
			else if(cmd.equals("buttonTurnOnOff")) {
				String id = idArray.get(currentTable);
				
				if(buttonTurnOnOff.isSelected()) tablesMap.get(id).resumeThread();				
				else
					tablesMap.get(id).pauseThread();
			}			
			else if(cmd.equals("buttonTimeline")) {
				String userId = idArray.get(currentTable);
				StatusesTableThread table = tablesMap.get(userId);
				new GUITimeline(table.getStatusesList(),table.isGroup(),txtCurrentUser.getText());
			}
			else if(cmd.equals("buttonSearchUpdates")) {
				setPanelContent(new StatusesTableThread(StatusesType.SEARCH,txtCurrentUser.getText()));
			}
			else if(cmd.equals("buttonNewDirectMessage")) {
				controller.openGUINewUpdateWindow("",StatusesType.DIRECT_MESSAGES);
			}
			else
				System.out.println(cmd);
		}	
	}
}


