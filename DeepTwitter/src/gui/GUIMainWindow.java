package gui;

import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

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
        jScrollPane1 = new javax.swing.JScrollPane();
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
        buttonUpdate.setName("buttonNewUpdate"); // NOI18N

        buttonNewGroup.setText("Novo grupo");
        buttonNewGroup.setName("buttonNewGroup"); // NOI18N

        buttonAddUser.setText("Adicionar usu�rio");
        buttonAddUser.setName("buttonAddUser"); // NOI18N

        buttonClearSelection.setText("Limpar sele��o");
        buttonClearSelection.setFocusable(false);
        buttonClearSelection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClearSelection.setName("buttonClearSelection"); // NOI18N
        buttonClearSelection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        checkBoxHighQuality.setBackground(new java.awt.Color(255, 255, 255));
        checkBoxHighQuality.setSelected(true);
        checkBoxHighQuality.setText("alta qualidade");
        checkBoxHighQuality.setName("checkBoxHighQuality"); // NOI18N

        labelFilter.setFont(new java.awt.Font("Tahoma", 0, 14));
        labelFilter.setText("Filtro:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFilter)
                    .addComponent(checkBoxHighQuality)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(buttonClearSelection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                        .addComponent(buttonNewGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                        .addComponent(buttonAddUser, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)))
                .addGap(55, 55, 55))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonNewGroup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonAddUser)
                .addGap(18, 18, 18)
                .addComponent(buttonClearSelection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxHighQuality)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelFilter)
                .addContainerGap(343, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Intera��es", jPanel1);
        jTabbedPane1.addTab("Atualiza��es", jScrollPane1);

        jSplitPane1.setLeftComponent(jTabbedPane1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Arquivo");
        jMenu1.setName("menuSaveNetworkAs"); // NOI18N

        menuLoadNetwork.setText("Carregar Rede");
        menuLoadNetwork.setName("menuLoadNetwork"); // NOI18N
        jMenu1.add(menuLoadNetwork);

        menuSaveNetwork.setText("Salvar Rede");
        menuSaveNetwork.setName("menuSaveNetwork"); // NOI18N
        jMenu1.add(menuSaveNetwork);

        menuSaveNetworkAs.setText("Salvar Rede Como...");
        menuSaveNetworkAs.setName("menuSaveNetworkAs"); // NOI18N
        jMenu1.add(menuSaveNetworkAs);
        jMenu1.add(jSeparator1);

        menuLogout.setText("Logout");
        menuLogout.setName("menuLogout"); // NOI18N
        jMenu1.add(menuLogout);

        menuExit.setText("Sair");
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
        menuCheckBoxStatusBar.setName("menuCheckBoxStatusBar"); // NOI18N
        jMenu3.add(menuCheckBoxStatusBar);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Ajuda");

        menuHelp.setText("T�picos de Ajuda");
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

        checkBoxHighQuality.addActionListener(listener);        
	}

    public boolean isHighQuality() {
        return checkBoxHighQuality.isSelected(); 
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


    // Variables declaration - do not modify
    private javax.swing.JButton buttonAddUser;
    private javax.swing.JButton buttonClearSelection;
    private javax.swing.JButton buttonNewGroup;
    private javax.swing.JButton buttonUpdate;
    private javax.swing.JCheckBox checkBoxHighQuality;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelFilter;
    private javax.swing.JLabel labelStatusBar;
    private javax.swing.JCheckBoxMenuItem menuCheckBoxStatusBar;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuHelp;
    private javax.swing.JMenuItem menuLoadNetwork;
    private javax.swing.JMenuItem menuLogout;
    private javax.swing.JMenuItem menuSaveNetwork;
    private javax.swing.JMenuItem menuSaveNetworkAs;
    // End of variables declaration

}
