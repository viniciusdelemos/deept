package controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

import model.StatusesTable;
import model.StatusesType;

public class StatusTab {
	private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToggleButton buttonTurnOnOff;
    private javax.swing.JButton buttonSettings;
    private javax.swing.JButton buttonNextUser;
    private javax.swing.JButton buttonPreviousUser;
    private javax.swing.JButton buttonCloseUpdates;
    private javax.swing.JButton buttonAddUpdate;
    private javax.swing.JTextField txtCurrentUser;
	private Map<String,StatusesTable> tablesMap;
	private StatusesType type;
	private TabListener tabListener;
	private int currentTable;
	private boolean hasMultiplePanels;
	private ArrayList<String> idArray;
	private ControllerDeepTwitter controller;
	
	public StatusTab(JTabbedPane pane, StatusesType type, String name) {
		controller = ControllerDeepTwitter.getInstance();
		initComponents();
		pane.addTab(name, createAndGetPanel(type));
		this.type = type;
		idArray = new ArrayList<String>();			
		currentTable = 0;
		if(type == StatusesType.UPDATES || type == StatusesType.FAVORITES) 		
			hasMultiplePanels = true;		
		tablesMap = new HashMap<String,StatusesTable>();
	}
	
	private JPanel createAndGetPanel(StatusesType typeOfTab) {
		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new java.awt.BorderLayout());
		addToolBar(jPanel2,typeOfTab);
		jPanel2.add(jScrollPane6, java.awt.BorderLayout.CENTER);		
		return jPanel2;
	}	
	
	public void setPanelContent(StatusesTable newTable) {
		String userId = newTable.getUserId();
		StatusesTable selectedTable = tablesMap.get(userId);		
		if(selectedTable == null) {
			JPanel content = newTable.getContent();
			jScrollPane6.setViewportView(content);
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
			reconfigButtons();
			jScrollPane6.revalidate();
		}
		reconfigOnOffButton();
		setCurrentUserName(controller.getUserName(userId));
	}
	
	private void setPanelContent(String userId) {
		StatusesTable selectedTable = tablesMap.get(userId);		
		jScrollPane6.setViewportView(selectedTable.getContent());
		jScrollPane6.revalidate();
		reconfigOnOffButton();
		setCurrentUserName(controller.getUserName(userId));		
	}
	
	public void setCurrentUserName(String name) {
		//labelCurrentUser.setText(name);
		//Dimension d = labelCurrentUser.getPreferredSize();
		//labelCurrentUser.setPreferredSize(new Dimension(d.width+60,d.height));
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
		
//		POR ENQUANTO EH A MESMA TOOLBAR
//		switch(typeOfTab) {
//		case MY_UPDATES:
//		case OTHERS_UPDATES:
//
//			break;
//		case FAVORITES:
//
//			break;
//		case REPLIES:
//
//			break;
//		case DIRECT_MESSAGES:
//			
//			break;
//		case PUBLIC_TIMELINE:
//
//			break;
//		}
		
		jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        buttonAddUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("../new.png"))); // NOI18N
        buttonAddUpdate.setToolTipText("Novo update");
        buttonAddUpdate.setActionCommand("buttonNewUpdate");
        buttonAddUpdate.setFocusable(false);
        buttonAddUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonAddUpdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonAddUpdate);
        jToolBar1.add(jSeparator2);

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
        jToolBar1.add(txtCurrentUser);

        buttonNextUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("../forward.png"))); // NOI18N
        buttonNextUser.setToolTipText("Próximo usuário");
        buttonNextUser.setActionCommand("buttonNextUser");
        buttonNextUser.setEnabled(false);
        buttonNextUser.setFocusable(false);
        buttonNextUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNextUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonNextUser);
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

        buttonSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("../config.png"))); // NOI18N
        buttonSettings.setToolTipText("Configurações");
        buttonSettings.setActionCommand("buttonSettings");
        buttonSettings.setFocusable(false);
        buttonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonSettings);
        
		panel.add(jToolBar1, java.awt.BorderLayout.PAGE_START);
	}
	
	private void initComponents() {
		jToolBar1 = new JToolBar();
		jScrollPane6 = new JScrollPane();
		jSeparator2 = new Separator();
		jSeparator4 = new Separator();
		buttonTurnOnOff = new JToggleButton();
		buttonSettings = new JButton();
	    buttonNextUser = new JButton();
	    buttonPreviousUser = new JButton();
	    buttonCloseUpdates = new JButton();
	    buttonAddUpdate = new JButton();
	    txtCurrentUser = new javax.swing.JTextField();
	    
	    tabListener = new TabListener();	    
	    buttonCloseUpdates.addActionListener(tabListener);
        buttonPreviousUser.addActionListener(tabListener);
        buttonNextUser.addActionListener(tabListener);
        buttonTurnOnOff.addActionListener(tabListener);
        buttonSettings.addActionListener(tabListener);
        buttonAddUpdate.addActionListener(tabListener);
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
			else if(cmd.equals("buttonSettings")) {
				//TODO
				System.out.println("Settings");
			}
		}	
	}
}


