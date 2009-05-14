package gui;

import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author rotta
 */
public class GUIMainWindow extends javax.swing.JFrame {    
    /** Creates new form GUIMainWindow */
    public GUIMainWindow(String windowName) {
        super(windowName);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        labelStatusBar = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        buttonUpdate = new javax.swing.JButton();
        buttonNewGroup = new javax.swing.JButton();
        buttonAddUser = new javax.swing.JButton();
        buttonClearSelection = new javax.swing.JButton();
        checkBoxHighQuality = new javax.swing.JCheckBox();
        labelFilter = new javax.swing.JLabel();
        buttonHideSelection = new javax.swing.JButton();
        buttonRemoveSelection = new javax.swing.JButton();
        buttonShowAll = new javax.swing.JButton();
        checkBoxCurvedEdges = new javax.swing.JCheckBox();
        labelAPILimit = new javax.swing.JLabel();
        checkBoxToolTipControl = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        buttonAddUpdate = new javax.swing.JButton();
        buttonNewDirectMessage = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        buttonPreviousUser = new javax.swing.JButton();
        txtCurrentUser = new javax.swing.JTextField();
        buttonNextUser = new javax.swing.JButton();
        buttonSearchUpdates = new javax.swing.JButton();
        buttonInbox = new javax.swing.JToggleButton();
        buttonOutbox = new javax.swing.JToggleButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        buttonCloseUpdates = new javax.swing.JButton();
        buttonTurnOnOff = new javax.swing.JToggleButton();
        buttonSettings = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        buttonTimeline = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuLoadNetwork = new javax.swing.JMenuItem();
        menuSaveNetwork = new javax.swing.JMenuItem();
        menuSaveNetworkAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        menuLogout = new javax.swing.JMenuItem();
        menuExit = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuCheckBoxStatusBar = new javax.swing.JCheckBoxMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuHelp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("DeepTwitter"); // NOI18N

        labelStatusBar.setFont(new java.awt.Font("Tahoma", 0, 12));
        labelStatusBar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelStatusBar.setText(" ");
        labelStatusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelStatusBar.setName("labelStatusBar"); // NOI18N
        getContentPane().add(labelStatusBar, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1000, 600));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        buttonUpdate.setText("Novo Update");
        buttonUpdate.setActionCommand("buttonNewUpdate");
        buttonUpdate.setName("buttonNewUpdate"); // NOI18N

        buttonNewGroup.setText("Novo grupo");
        buttonNewGroup.setActionCommand("buttonNewGroup");
        buttonNewGroup.setName("buttonNewGroup"); // NOI18N

        buttonAddUser.setText("Adicionar usu�rio");
        buttonAddUser.setActionCommand("buttonAddUser");
        buttonAddUser.setName("buttonAddUser"); // NOI18N

        buttonClearSelection.setText("Limpar sele��o");
        buttonClearSelection.setActionCommand("buttonClearSelection");
        buttonClearSelection.setFocusable(false);
        buttonClearSelection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClearSelection.setName("buttonClearSelection"); // NOI18N
        buttonClearSelection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        checkBoxHighQuality.setBackground(new java.awt.Color(255, 255, 255));
        checkBoxHighQuality.setSelected(true);
        checkBoxHighQuality.setText("alta qualidade");
        checkBoxHighQuality.setActionCommand("checkBoxHighQuality");
        checkBoxHighQuality.setName("checkBoxHighQuality"); // NOI18N

        labelFilter.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        labelFilter.setText("Filtro:");

        buttonHideSelection.setText("Esconder sele��o");
        buttonHideSelection.setActionCommand("buttonHideSelection");
        buttonHideSelection.setName("buttonHideSelection"); // NOI18N

        buttonRemoveSelection.setText("Remover sele��o");
        buttonRemoveSelection.setActionCommand("buttonRemoveSelection");
        buttonRemoveSelection.setName("buttonRemoveSelection"); // NOI18N

        buttonShowAll.setText("Exibir todos");
        buttonShowAll.setActionCommand("buttonShowAll");
        buttonShowAll.setName("buttonShowAll"); // NOI18N

        checkBoxCurvedEdges.setBackground(new java.awt.Color(255, 255, 255));
        checkBoxCurvedEdges.setText("arestas curvas");
        checkBoxCurvedEdges.setActionCommand("checkBoxCurvedEdges");
        checkBoxCurvedEdges.setName("checkBoxCurvedEdges"); // NOI18N

        labelAPILimit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelAPILimit.setText("Requisi��es API:");

        checkBoxToolTipControl.setBackground(new java.awt.Color(255, 255, 255));
        checkBoxToolTipControl.setText("infos de usu�rio");
        checkBoxToolTipControl.setActionCommand("checkBoxToolTipControl");
        checkBoxToolTipControl.setName("checkBoxToolTipControl"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxToolTipControl)
                    .addComponent(labelAPILimit)
                    .addComponent(labelFilter)
                    .addComponent(checkBoxCurvedEdges)
                    .addComponent(checkBoxHighQuality)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(buttonShowAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonRemoveSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonHideSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonAddUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonUpdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                        .addComponent(buttonNewGroup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                        .addComponent(buttonClearSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonAddUser)
                .addGap(18, 18, 18)
                .addComponent(buttonNewGroup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonHideSelection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRemoveSelection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonShowAll)
                .addGap(7, 7, 7)
                .addComponent(buttonClearSelection)
                .addGap(18, 18, 18)
                .addComponent(checkBoxHighQuality)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxCurvedEdges)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxToolTipControl)
                .addGap(7, 7, 7)
                .addComponent(labelFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelAPILimit)
                .addContainerGap(172, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Intera��es", jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        buttonAddUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("../new.png"))); // NOI18N
        buttonAddUpdate.setToolTipText("Novo update");
        buttonAddUpdate.setActionCommand("buttonNewUpdate");
        buttonAddUpdate.setFocusable(false);
        buttonAddUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonAddUpdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonAddUpdate);

        buttonNewDirectMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("../mail_add.png"))); // NOI18N
        buttonNewDirectMessage.setToolTipText("Nova mensagem direta");
        buttonNewDirectMessage.setActionCommand("buttonNewDirectMessage");
        buttonNewDirectMessage.setFocusable(false);
        buttonNewDirectMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNewDirectMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonNewDirectMessage);
        jToolBar1.add(jSeparator2);

        buttonPreviousUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("../backward.png"))); // NOI18N
        buttonPreviousUser.setToolTipText("Usu�rio anterior");
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
        txtCurrentUser.setFont(new java.awt.Font("Tahoma", 1, 12));
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
        buttonNextUser.setToolTipText("Pr�ximo usu�rio");
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
        buttonOutbox.setToolTipText("Caixa de Sa�da");
        buttonOutbox.setActionCommand("buttonOutbox");
        buttonOutbox.setFocusable(false);
        buttonOutbox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonOutbox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonOutbox);
        jToolBar1.add(jSeparator4);

        buttonCloseUpdates.setIcon(new javax.swing.ImageIcon(getClass().getResource("../remove.png"))); // NOI18N
        buttonCloseUpdates.setToolTipText("Fechar este usu�rio");
        buttonCloseUpdates.setActionCommand("buttonCloseUpdates");
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
        buttonSettings.setToolTipText("Configura��es");
        buttonSettings.setActionCommand("buttonSettings");
        buttonSettings.setFocusable(false);
        buttonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonSettings);
        jToolBar1.add(jSeparator5);

        buttonTimeline.setIcon(new javax.swing.ImageIcon(getClass().getResource("../eye.png"))); // NOI18N
        buttonTimeline.setToolTipText("Ver timeline de atualiza��es");
        buttonTimeline.setActionCommand("buttonTimeline");
        buttonTimeline.setFocusable(false);
        buttonTimeline.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTimeline.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonTimeline);
        buttonTimeline.getAccessibleContext().setAccessibleDescription("Visualiza��o do Timeline de Atualiza��es");

        jPanel2.add(jToolBar1, java.awt.BorderLayout.PAGE_START);
        jPanel2.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Atualiza��es", jPanel2);

        jSplitPane1.setLeftComponent(jTabbedPane1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Arquivo");
        jMenu1.setName("menuSaveNetworkAs"); // NOI18N

        menuLoadNetwork.setText("Carregar Rede");
        menuLoadNetwork.setActionCommand("menuLoadNetwork");
        menuLoadNetwork.setName("menuLoadNetwork"); // NOI18N
        jMenu1.add(menuLoadNetwork);

        menuSaveNetwork.setText("Salvar Rede");
        menuSaveNetwork.setActionCommand("menuSaveNetwork");
        menuSaveNetwork.setName("menuSaveNetwork"); // NOI18N
        jMenu1.add(menuSaveNetwork);

        menuSaveNetworkAs.setText("Salvar Rede Como...");
        menuSaveNetworkAs.setActionCommand("menuSaveNetworkAs");
        menuSaveNetworkAs.setName("menuSaveNetworkAs"); // NOI18N
        jMenu1.add(menuSaveNetworkAs);
        jMenu1.add(jSeparator1);

        menuLogout.setText("Logout");
        menuLogout.setActionCommand("menuLogout");
        menuLogout.setName("menuLogout"); // NOI18N
        jMenu1.add(menuLogout);

        menuExit.setText("Sair");
        menuExit.setActionCommand("menuExit");
        menuExit.setName("menuExit"); // NOI18N
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        jMenu1.add(menuExit);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Exibir");

        menuCheckBoxStatusBar.setSelected(true);
        menuCheckBoxStatusBar.setText("Barra de Status");
        menuCheckBoxStatusBar.setActionCommand("menuCheckBoxStatusBar");
        menuCheckBoxStatusBar.setName("menuCheckBoxStatusBar"); // NOI18N
        jMenu3.add(menuCheckBoxStatusBar);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Ajuda");

        menuHelp.setText("T�picos de Ajuda");
        menuHelp.setActionCommand("menuHelp");
        menuHelp.setName("menuHelp"); // NOI18N
        jMenu2.add(menuHelp);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {                                         
        System.exit(0);
}                                        

    public void addMainWindowListener(ActionListener listener) {
		menuLoadNetwork.addActionListener(listener);
        menuSaveNetwork.addActionListener(listener);
        menuSaveNetworkAs.addActionListener(listener);
        menuLogout.addActionListener(listener);
        menuCheckBoxStatusBar.addActionListener(listener);
        menuHelp.addActionListener(listener);

        buttonAddUser.addActionListener(listener);
        buttonNewGroup.addActionListener(listener);
        buttonUpdate.addActionListener(listener);
        buttonClearSelection.addActionListener(listener);

        buttonCloseUpdates.addActionListener(listener);
        buttonPreviousUser.addActionListener(listener);
        buttonNextUser.addActionListener(listener);
        buttonTurnOnOff.addActionListener(listener);
        buttonSettings.addActionListener(listener);
        buttonAddUpdate.addActionListener(listener);
        buttonTimeline.addActionListener(listener);

        checkBoxHighQuality.addActionListener(listener);
        checkBoxCurvedEdges.addActionListener(listener);
        checkBoxToolTipControl.addActionListener(listener);
	}

    public boolean isHighQuality() {
        return checkBoxHighQuality.isSelected(); 
    }

    public boolean isCurvedEdges() {
        return checkBoxCurvedEdges.isSelected();
    }
    
    public boolean isToolTipControlOn() {
        return checkBoxToolTipControl.isSelected();
    }

    public boolean isStatusBarVisible() {
        return menuCheckBoxStatusBar.isSelected();
    }

    public void setStatusBarMessage(String message) {
        labelStatusBar.setText(message);
    }

    public void setStatusBarVisible(boolean b)  {
        labelStatusBar.setVisible(b);
    }

    public JSplitPane getSplitPane() {
        return jSplitPane1;
    }

    public JTabbedPane getTabs() {
        return jTabbedPane1;
    }

    public void setPreviousUserEnabled(boolean b) {
        buttonPreviousUser.setEnabled(b);
    }

    public void setNextUserEnabled(boolean b) {
        buttonNextUser.setEnabled(b);
    }

    public void setCurrentUserName(String s) {
        txtCurrentUser.setText(s);
    }

    public void setCloseUserEnabled(boolean b) {
        buttonCloseUpdates.setEnabled(b);
    }

    public void setRateLimitStatus(int left, int total, Date resetDate) {
        labelAPILimit.setText("Requisi��es API: "+left+"/"+total+" Reset �s "+resetDate.getHours()+":"+resetDate.getMinutes());
    }

    // Variables declaration - do not modify
    private javax.swing.JButton buttonAddUpdate;
    private javax.swing.JButton buttonAddUser;
    private javax.swing.JButton buttonClearSelection;
    private javax.swing.JButton buttonCloseUpdates;
    private javax.swing.JButton buttonHideSelection;
    private javax.swing.JToggleButton buttonInbox;
    private javax.swing.JButton buttonNewDirectMessage;
    private javax.swing.JButton buttonNewGroup;
    private javax.swing.JButton buttonNextUser;
    private javax.swing.JToggleButton buttonOutbox;
    private javax.swing.JButton buttonPreviousUser;
    private javax.swing.JButton buttonRemoveSelection;
    private javax.swing.JButton buttonSearchUpdates;
    private javax.swing.JButton buttonSettings;
    private javax.swing.JButton buttonShowAll;
    private javax.swing.JButton buttonTimeline;
    private javax.swing.JToggleButton buttonTurnOnOff;
    private javax.swing.JButton buttonUpdate;
    private javax.swing.JCheckBox checkBoxCurvedEdges;
    private javax.swing.JCheckBox checkBoxHighQuality;
    private javax.swing.JCheckBox checkBoxToolTipControl;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelAPILimit;
    private javax.swing.JLabel labelFilter;
    private javax.swing.JLabel labelStatusBar;
    private javax.swing.JCheckBoxMenuItem menuCheckBoxStatusBar;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuHelp;
    private javax.swing.JMenuItem menuLoadNetwork;
    private javax.swing.JMenuItem menuLogout;
    private javax.swing.JMenuItem menuSaveNetwork;
    private javax.swing.JMenuItem menuSaveNetworkAs;
    private javax.swing.JTextField txtCurrentUser;
    // End of variables declaration

}
