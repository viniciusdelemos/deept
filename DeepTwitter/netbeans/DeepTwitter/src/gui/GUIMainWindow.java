package gui;

import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelStatusBar = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
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
        jSeparator5 = new javax.swing.JToolBar.Separator();
        buttonTimeline = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        mainToolBar = new javax.swing.JToolBar();
        buttonUpdate = new javax.swing.JButton();
        buttonAddUser = new javax.swing.JButton();
        buttonNewGroup = new javax.swing.JButton();
        buttonSettings = new javax.swing.JButton();
        buttonToolTipControl = new javax.swing.JToggleButton();
        buttonPlayPauseVisualization = new javax.swing.JToggleButton();
        buttonCenterUser = new javax.swing.JToggleButton();
        buttonMostActive = new javax.swing.JButton();
        buttonCategoryEditor = new javax.swing.JButton();
        buttonShowAll = new javax.swing.JButton();
        buttonClearSelection = new javax.swing.JButton();
        checkBoxHighQuality = new javax.swing.JCheckBox();
        checkBoxCurvedEdges = new javax.swing.JCheckBox();
        labelAPILimit = new javax.swing.JLabel();
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
        jSplitPane1.setDividerSize(6);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1100, 600));

        jPanel2.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        buttonNewDirectMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/mail_add.png"))); // NOI18N
        buttonNewDirectMessage.setToolTipText("Nova mensagem direta");
        buttonNewDirectMessage.setActionCommand("buttonNewDirectMessage");
        buttonNewDirectMessage.setFocusable(false);
        buttonNewDirectMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNewDirectMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonNewDirectMessage);
        jToolBar1.add(jSeparator2);

        buttonPreviousUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/backward.png"))); // NOI18N
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

        buttonNextUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/forward.png"))); // NOI18N
        buttonNextUser.setToolTipText("Próximo usuário");
        buttonNextUser.setActionCommand("buttonNextUser");
        buttonNextUser.setEnabled(false);
        buttonNextUser.setFocusable(false);
        buttonNextUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNextUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonNextUser);

        buttonSearchUpdates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/search.png"))); // NOI18N
        buttonSearchUpdates.setToolTipText("Buscar");
        buttonSearchUpdates.setActionCommand("buttonSearchUpdates");
        buttonSearchUpdates.setFocusable(false);
        buttonSearchUpdates.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSearchUpdates.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonSearchUpdates);

        buttonInbox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/mail_inbox.png"))); // NOI18N
        buttonInbox.setSelected(true);
        buttonInbox.setToolTipText("Caixa de Entrada");
        buttonInbox.setActionCommand("buttonInbox");
        buttonInbox.setFocusable(false);
        buttonInbox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonInbox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonInbox);

        buttonOutbox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/mail_outbox.png"))); // NOI18N
        buttonOutbox.setToolTipText("Caixa de Saída");
        buttonOutbox.setActionCommand("buttonOutbox");
        buttonOutbox.setFocusable(false);
        buttonOutbox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonOutbox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonOutbox);
        jToolBar1.add(jSeparator4);

        buttonCloseUpdates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/remove.png"))); // NOI18N
        buttonCloseUpdates.setToolTipText("Fechar este usuário");
        buttonCloseUpdates.setActionCommand("buttonCloseUpdates");
        buttonCloseUpdates.setFocusable(false);
        buttonCloseUpdates.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCloseUpdates.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonCloseUpdates);

        buttonTurnOnOff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/turn_on.png"))); // NOI18N
        buttonTurnOnOff.setSelected(true);
        buttonTurnOnOff.setToolTipText("Ligar/desligar atualizar automaticamente");
        buttonTurnOnOff.setActionCommand("buttonTurnOnOff");
        buttonTurnOnOff.setFocusable(false);
        buttonTurnOnOff.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTurnOnOff.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/turn_off.png"))); // NOI18N
        buttonTurnOnOff.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonTurnOnOff);
        jToolBar1.add(jSeparator5);

        buttonTimeline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/eye.png"))); // NOI18N
        buttonTimeline.setToolTipText("Configurações");
        buttonTimeline.setActionCommand("buttonTimeline");
        buttonTimeline.setFocusable(false);
        buttonTimeline.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTimeline.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(buttonTimeline);
        buttonTimeline.getAccessibleContext().setAccessibleDescription("Visualização do Timeline de Atualizações");

        jPanel2.add(jToolBar1, java.awt.BorderLayout.PAGE_START);
        jPanel2.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Atualizações", jPanel2);

        jSplitPane1.setLeftComponent(jTabbedPane1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);

        buttonUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/comment_add.png"))); // NOI18N
        buttonUpdate.setToolTipText("Novo update");
        buttonUpdate.setActionCommand("buttonNewUpdate");
        buttonUpdate.setName("buttonNewUpdate"); // NOI18N
        mainToolBar.add(buttonUpdate);

        buttonAddUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/user_search.png"))); // NOI18N
        buttonAddUser.setToolTipText("Buscar usuário");
        buttonAddUser.setActionCommand("buttonAddUser");
        buttonAddUser.setName("buttonAddUser"); // NOI18N
        mainToolBar.add(buttonAddUser);

        buttonNewGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/users_add.png"))); // NOI18N
        buttonNewGroup.setToolTipText("Novo grupo");
        buttonNewGroup.setActionCommand("buttonNewGroup");
        buttonNewGroup.setName("buttonNewGroup"); // NOI18N
        mainToolBar.add(buttonNewGroup);

        buttonSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/config.png"))); // NOI18N
        buttonSettings.setToolTipText("Configurações");
        buttonSettings.setActionCommand("buttonSettings");
        buttonSettings.setFocusable(false);
        buttonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(buttonSettings);

        buttonToolTipControl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/user_info.png"))); // NOI18N
        buttonToolTipControl.setSelected(true);
        buttonToolTipControl.setToolTipText("Exibir infos do usuário");
        buttonToolTipControl.setActionCommand("buttonToolTipControl");
        buttonToolTipControl.setFocusable(false);
        buttonToolTipControl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonToolTipControl.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(buttonToolTipControl);

        buttonPlayPauseVisualization.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/play.png"))); // NOI18N
        buttonPlayPauseVisualization.setSelected(true);
        buttonPlayPauseVisualization.setToolTipText("Play/Pause animar rede");
        buttonPlayPauseVisualization.setActionCommand("buttonPlayPauseVisualization");
        buttonPlayPauseVisualization.setFocusable(false);
        buttonPlayPauseVisualization.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPlayPauseVisualization.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/pause.png"))); // NOI18N
        buttonPlayPauseVisualization.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(buttonPlayPauseVisualization);

        buttonCenterUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/target.png"))); // NOI18N
        buttonCenterUser.setToolTipText("Centralizar usuário clicado");
        buttonCenterUser.setActionCommand("buttonCenterUser");
        buttonCenterUser.setFocusable(false);
        buttonCenterUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCenterUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(buttonCenterUser);

        buttonMostActive.setText("Mais ativos");
        buttonMostActive.setActionCommand("buttonMostActive");
        buttonMostActive.setFocusable(false);
        buttonMostActive.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonMostActive.setName("buttonShowAll"); // NOI18N
        buttonMostActive.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(buttonMostActive);

        buttonCategoryEditor.setText("Categorias");
        buttonCategoryEditor.setActionCommand("buttonCategoryEditor");
        buttonCategoryEditor.setFocusable(false);
        buttonCategoryEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCategoryEditor.setName("buttonShowAll"); // NOI18N
        buttonCategoryEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(buttonCategoryEditor);

        buttonShowAll.setText("Exibir todos");
        buttonShowAll.setActionCommand("buttonShowAll");
        buttonShowAll.setName("buttonShowAll"); // NOI18N
        mainToolBar.add(buttonShowAll);

        buttonClearSelection.setText("Limpar seleção");
        buttonClearSelection.setActionCommand("buttonClearSelection");
        buttonClearSelection.setFocusable(false);
        buttonClearSelection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClearSelection.setName("buttonClearSelection"); // NOI18N
        buttonClearSelection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(buttonClearSelection);

        checkBoxHighQuality.setSelected(true);
        checkBoxHighQuality.setText("alta qualidade");
        checkBoxHighQuality.setActionCommand("checkBoxHighQuality");
        checkBoxHighQuality.setName("checkBoxHighQuality"); // NOI18N
        mainToolBar.add(checkBoxHighQuality);

        checkBoxCurvedEdges.setText("arestas curvas");
        checkBoxCurvedEdges.setActionCommand("checkBoxCurvedEdges");
        checkBoxCurvedEdges.setName("checkBoxCurvedEdges"); // NOI18N
        mainToolBar.add(checkBoxCurvedEdges);

        labelAPILimit.setFont(new java.awt.Font("Tahoma", 0, 12));
        labelAPILimit.setText("Requisições API:");
        mainToolBar.add(labelAPILimit);

        getContentPane().add(mainToolBar, java.awt.BorderLayout.NORTH);

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

        menuHelp.setText("Tópicos de Ajuda");
        menuHelp.setActionCommand("menuHelp");
        menuHelp.setName("menuHelp"); // NOI18N
        jMenu2.add(menuHelp);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        System.exit(0);
}//GEN-LAST:event_menuExitActionPerformed

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
        buttonTimeline.addActionListener(listener);

        checkBoxHighQuality.addActionListener(listener);
        checkBoxCurvedEdges.addActionListener(listener);
        buttonToolTipControl.addActionListener(listener);
        buttonPlayPauseVisualization.addActionListener(listener);
        buttonCenterUser.addActionListener(listener);
        buttonMostActive.addActionListener(listener);
        buttonCategoryEditor.addActionListener(listener);
	}

    public boolean isHighQuality() {
        return checkBoxHighQuality.isSelected(); 
    }

    public boolean isCurvedEdges() {
        return checkBoxCurvedEdges.isSelected();
    }

    public boolean isToolTipControlOn() {
        return buttonToolTipControl.isSelected();
    }

    public boolean isVisualizationRunning() {
        return buttonPlayPauseVisualization.isSelected();
    }

    public boolean isCenterUserOn() {
        return buttonCenterUser.isSelected();
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

    public JToolBar getMainToolBar() {
        return mainToolBar;
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
        labelAPILimit.setText("Requisições API: "+left+"/"+total+" Reset às "+resetDate.getHours()+":"+resetDate.getMinutes());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddUser;
    private javax.swing.JButton buttonCategoryEditor;
    private javax.swing.JToggleButton buttonCenterUser;
    private javax.swing.JButton buttonClearSelection;
    private javax.swing.JButton buttonCloseUpdates;
    private javax.swing.JToggleButton buttonInbox;
    private javax.swing.JButton buttonMostActive;
    private javax.swing.JButton buttonNewDirectMessage;
    private javax.swing.JButton buttonNewGroup;
    private javax.swing.JButton buttonNextUser;
    private javax.swing.JToggleButton buttonOutbox;
    private javax.swing.JToggleButton buttonPlayPauseVisualization;
    private javax.swing.JButton buttonPreviousUser;
    private javax.swing.JButton buttonSearchUpdates;
    private javax.swing.JButton buttonSettings;
    private javax.swing.JButton buttonShowAll;
    private javax.swing.JButton buttonTimeline;
    private javax.swing.JToggleButton buttonToolTipControl;
    private javax.swing.JToggleButton buttonTurnOnOff;
    private javax.swing.JButton buttonUpdate;
    private javax.swing.JCheckBox checkBoxCurvedEdges;
    private javax.swing.JCheckBox checkBoxHighQuality;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
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
    private javax.swing.JLabel labelStatusBar;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JCheckBoxMenuItem menuCheckBoxStatusBar;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuHelp;
    private javax.swing.JMenuItem menuLoadNetwork;
    private javax.swing.JMenuItem menuLogout;
    private javax.swing.JMenuItem menuSaveNetwork;
    private javax.swing.JMenuItem menuSaveNetworkAs;
    private javax.swing.JTextField txtCurrentUser;
    // End of variables declaration//GEN-END:variables

}
