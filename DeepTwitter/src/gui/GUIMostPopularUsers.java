package gui;

import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author rotta
 */
public class GUIMostPopularUsers extends javax.swing.JFrame {
    /** Creates new form GUIMainWindow */
    public GUIMostPopularUsers(String windowName) {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        menuPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        orderByFriendsButton = new javax.swing.JToggleButton();
        orderByFavoritesButton = new javax.swing.JToggleButton();
        orderByTweetsButton = new javax.swing.JToggleButton();
        orderByFollowersButton = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        comboBoxMaxUsers = new javax.swing.JComboBox();
        buttonPlayPauseTweetView = new javax.swing.JToggleButton();
        buttonSettings = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("DeepTwitter - Usu�rios mais populares"); // NOI18N

        jSplitPane1.setDividerLocation(510);
        jSplitPane1.setDividerSize(6);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setMaximumSize(new java.awt.Dimension(102, 131));
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(800, 600));

        menuPanel.setBackground(new java.awt.Color(102, 204, 255));
        menuPanel.setMaximumSize(new java.awt.Dimension(100, 100));
        menuPanel.setMinimumSize(new java.awt.Dimension(100, 90));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jEditorPane1.setBackground(new java.awt.Color(102, 204, 255));
        jEditorPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240), 0));
        jEditorPane1.setContentType("text/html");
        jEditorPane1.setEditable(false);
        jEditorPane1.setFont(new java.awt.Font("Arial", 1, 14));
        jScrollPane1.setViewportView(jEditorPane1);

        buttonGroup1.add(orderByFriendsButton);
        orderByFriendsButton.setFont(new java.awt.Font("Tahoma", 1, 12));
        orderByFriendsButton.setText("Amigos");
        orderByFriendsButton.setActionCommand("orderByFriends");
        orderByFriendsButton.setBorder(null);
        orderByFriendsButton.setMaximumSize(new java.awt.Dimension(57, 15));
        orderByFriendsButton.setMinimumSize(new java.awt.Dimension(57, 15));
        orderByFriendsButton.setPreferredSize(new java.awt.Dimension(101, 23));

        buttonGroup1.add(orderByFavoritesButton);
        orderByFavoritesButton.setFont(new java.awt.Font("Tahoma", 1, 12));
        orderByFavoritesButton.setText("Favoritos");
        orderByFavoritesButton.setActionCommand("orderByFavorites");
        orderByFavoritesButton.setBorder(null);
        orderByFavoritesButton.setPreferredSize(new java.awt.Dimension(101, 23));

        buttonGroup1.add(orderByTweetsButton);
        orderByTweetsButton.setFont(new java.awt.Font("Tahoma", 1, 12));
        orderByTweetsButton.setText("Tweets");
        orderByTweetsButton.setActionCommand("orderByTweets");
        orderByTweetsButton.setBorder(null);
        orderByTweetsButton.setMaximumSize(new java.awt.Dimension(57, 15));
        orderByTweetsButton.setMinimumSize(new java.awt.Dimension(57, 15));
        orderByTweetsButton.setPreferredSize(new java.awt.Dimension(101, 23));

        buttonGroup1.add(orderByFollowersButton);
        orderByFollowersButton.setFont(new java.awt.Font("Tahoma", 1, 12));
        orderByFollowersButton.setText("Seguidores");
        orderByFollowersButton.setActionCommand("orderByFollowers");
        orderByFollowersButton.setBorder(null);
        orderByFollowersButton.setMaximumSize(new java.awt.Dimension(57, 15));
        orderByFollowersButton.setMinimumSize(new java.awt.Dimension(57, 15));
        orderByFollowersButton.setPreferredSize(new java.awt.Dimension(101, 23));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Classificar por n�mero de");

        buttonPlayPauseTweetView.setBackground(new java.awt.Color(102, 204, 255));
        buttonPlayPauseTweetView.setIcon(new javax.swing.ImageIcon(getClass().getResource("../play.png"))); // NOI18N
        buttonPlayPauseTweetView.setSelected(true);
        buttonPlayPauseTweetView.setToolTipText("Executar/Parar exibi��o de tweets");
        buttonPlayPauseTweetView.setActionCommand("buttonPlayPauseTweetView");
        buttonPlayPauseTweetView.setFocusable(false);
        buttonPlayPauseTweetView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPlayPauseTweetView.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("../pause.png"))); // NOI18N
        buttonPlayPauseTweetView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        buttonSettings.setBackground(new java.awt.Color(102, 204, 255));
        buttonSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("../configure.png"))); // NOI18N
        buttonSettings.setToolTipText("Configura��es");
        buttonSettings.setActionCommand("buttonSettings");
        buttonSettings.setFocusable(false);
        buttonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setText("Exibir");

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPlayPauseTweetView, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuPanelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(comboBoxMaxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuPanelLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel2)))
                .addGap(37, 37, 37)
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPanelLayout.createSequentialGroup()
                        .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(menuPanelLayout.createSequentialGroup()
                                .addComponent(orderByFriendsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(orderByFavoritesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(menuPanelLayout.createSequentialGroup()
                                .addComponent(orderByTweetsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(orderByFollowersButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(37, 37, 37))))
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(orderByFriendsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(orderByFavoritesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(orderByTweetsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(orderByFollowersButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(menuPanelLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(menuPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboBoxMaxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(menuPanelLayout.createSequentialGroup()
                                .addComponent(buttonPlayPauseTweetView, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                .addComponent(buttonSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))))
                .addGap(33, 33, 33))
        );

        jSplitPane1.setBottomComponent(menuPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    public void addListener(ActionListener listener) {
		orderByFavoritesButton.addActionListener(listener);
        orderByFriendsButton.addActionListener(listener);
        orderByFollowersButton.addActionListener(listener);
        orderByTweetsButton.addActionListener(listener);
        buttonPlayPauseTweetView.addActionListener(listener);
        buttonSettings.addActionListener(listener);
	}    

    public JSplitPane getSplitPane() {
        return jSplitPane1;
    }

    public JEditorPane getEditor() {
        return jEditorPane1;
    }

    public JComboBox getComboBoxMaxUsers() {
        return comboBoxMaxUsers;
    }

    // Variables declaration - do not modify
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JToggleButton buttonPlayPauseTweetView;
    private javax.swing.JButton buttonSettings;
    private javax.swing.JComboBox comboBoxMaxUsers;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JToggleButton orderByFavoritesButton;
    private javax.swing.JToggleButton orderByFollowersButton;
    private javax.swing.JToggleButton orderByFriendsButton;
    private javax.swing.JToggleButton orderByTweetsButton;
    // End of variables declaration

}
