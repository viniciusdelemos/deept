/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Settings.java
 *
 * Created on Jul 3, 2009, 5:19:54 PM
 */

package gui;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;

import model.ConfigurationType;
import model.Settings;

import controller.ControllerDeepTwitter;

/**
 *
 * @author Vinicius
 */
public class GUISettings extends javax.swing.JFrame {

    /** Creates new form Settings */
    public GUISettings(String mainUser) {

        initComponents();
        initSettings(mainUser);
    }

    public void addMainWindowListener(ActionListener listener){

    	savejButton.setActionCommand("buttonOKCancelConfig");
    	canceljButton.setActionCommand("buttonOKCancelConfig");
    	//restorejButton.setActionCommand("buttonRestoreConfig"); //TODO restore

    	savejButton.addActionListener(listener);
    	canceljButton.addActionListener(listener);
    	//restorejButton.addActionListener(listener);

    }


    private void initSettings(String mainUser){

        mentionsUserjLabel.setText("@"+mainUser);

        int interval;
        Color color;

        //interval updates
        interval = controller.getProperty(ConfigurationType.intervalUpdates)/60000;
        updatesjSlider.setValue(interval);

        //interval mentions
        interval = controller.getProperty(ConfigurationType.intervalMentions)/60000;
        mentionsjSlider.setValue(interval);

        //interval favorites
        interval = controller.getProperty(ConfigurationType.intervalFavorites)/60000;
        favoritesjSlider.setValue(interval);

        //interval direct messages
        interval = controller.getProperty(ConfigurationType.intervalDirectMessages)/60000;
        directMessagesjSlider.setValue(interval);

        //interval search
        interval = controller.getProperty(ConfigurationType.intervalSearch)/60000;
        searchjSlider.setValue(interval);

        //interval public timeline
        interval = controller.getProperty(ConfigurationType.intervalPublicTimeline)/60000;
        publicTimelinejSlider.setValue(interval);

        //color mainUser
        color = new Color(controller.getProperty(ConfigurationType.mainUserColor));
        mainUserjButton.setBorder(BorderFactory.createLineBorder(color, 50));
        mainUserjButton.setBackground(color);

        //color friends
        color = new Color(controller.getProperty(ConfigurationType.friendsColor));
        friendsjButton.setBorder(BorderFactory.createLineBorder(color, 50));
        friendsjButton.setBackground(color);

        //color followers
        color = new Color(controller.getProperty(ConfigurationType.followersColor));
        followersjButton.setBorder(BorderFactory.createLineBorder(color, 50));
        followersjButton.setBackground(color);

        //color friends and followers
        color = new Color(controller.getProperty(ConfigurationType.friendsAndFollowersColor));
        friendsAndFollowersjButton.setBorder(BorderFactory.createLineBorder(color, 50));
        friendsAndFollowersjButton.setBackground(color);

        //color selected item
        color = new Color(controller.getProperty(ConfigurationType.selectedItemColor));
        selectedItemjButton.setBorder(BorderFactory.createLineBorder(color, 50));
        selectedItemjButton.setBackground(color);

        //color text
        color = new Color(controller.getProperty(ConfigurationType.textColor));
        textjButton.setBorder(BorderFactory.createLineBorder(color, 50));
        textjButton.setBackground(color);

        //color node stroke
        color = new Color(controller.getProperty(ConfigurationType.nodeStrokeColor));
        nodeStrokejButton.setBorder(BorderFactory.createLineBorder(color, 50));
        nodeStrokejButton.setBackground(color);

        //color edge
        color = new Color(controller.getProperty(ConfigurationType.edgeColor));
        edgejButton.setBorder(BorderFactory.createLineBorder(color, 50));
        edgejButton.setBackground(color);

        //color search result
        color = new Color(controller.getProperty(ConfigurationType.searchResultColor));
        searchResultjButton.setBorder(BorderFactory.createLineBorder(color, 50));
        searchResultjButton.setBackground(color);

        //edge type
        interval = controller.getProperty(ConfigurationType.edgeType);
        edgeTypejComboBox.setSelectedIndex(interval);

        //interval most popular users
        interval = controller.getProperty(ConfigurationType.intervalMostPopularUsers)/1000;
        mostPopularUsersjSlider.setValue(interval);
        
        interval = controller.getProperty(ConfigurationType.updatesToGet);
        updatesToGetjSlider.setValue(interval);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        mainUserjButton = new javax.swing.JButton();
        friendsjButton = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        followersjButton = new javax.swing.JButton();
        nodeStrokejButton = new javax.swing.JButton();
        edgejButton = new javax.swing.JButton();
        searchResultjButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        friendsAndFollowersjButton = new javax.swing.JButton();
        selectedItemjButton = new javax.swing.JButton();
        textjButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        updatesjSlider = new javax.swing.JSlider();
        updatesjLabel = new javax.swing.JLabel();
        mentionsUserjLabel = new javax.swing.JLabel();
        mentionsjSlider = new javax.swing.JSlider();
        mentionsjLabel = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        favoritesjSlider = new javax.swing.JSlider();
        favoritesjLabel = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        directMessagesjSlider = new javax.swing.JSlider();
        directMessagesjLabel = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        searchjSlider = new javax.swing.JSlider();
        searchjLabel = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        publicTimelinejSlider = new javax.swing.JSlider();
        publicTimelinejLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        edgeTypejComboBox = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        mostPopularUsersjSlider = new javax.swing.JSlider();
        mostPopularUsersjLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        updatesToGetjSlider = new javax.swing.JSlider();
        updatesToGetjLabel = new javax.swing.JLabel();
        restorejButton = new javax.swing.JButton();
        canceljButton = new javax.swing.JButton();
        savejButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DeepTwitter - Configurações");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cores"));

        jLabel7.setText("Usuário Principal");

        jLabel9.setText("Amigos");

        mainUserjButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        mainUserjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainUserjButtonActionPerformed(evt);
            }
        });

        friendsjButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        friendsjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                friendsjButtonActionPerformed(evt);
            }
        });

        jLabel10.setText("Seguidores");

        followersjButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        followersjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                followersjButtonActionPerformed(evt);
            }
        });

        nodeStrokejButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        nodeStrokejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeStrokejButtonActionPerformed(evt);
            }
        });

        edgejButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        edgejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgejButtonActionPerformed(evt);
            }
        });

        searchResultjButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        searchResultjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchResultjButtonActionPerformed(evt);
            }
        });

        jLabel14.setText("<html>Resultados da Busca da<br>barra de ferramentas</html>");

        jLabel16.setText("Arestas entre Usuários");

        jLabel19.setText("Borda dos Usuários");

        jLabel21.setText("Amigos e Seguidores");

        jLabel23.setText("Usuários Selecionados");

        jLabel24.setText("Nome dos Usuários");

        friendsAndFollowersjButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        friendsAndFollowersjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                friendsAndFollowersjButtonActionPerformed(evt);
            }
        });

        selectedItemjButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        selectedItemjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedItemjButtonActionPerformed(evt);
            }
        });

        textjButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 50));
        textjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textjButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(followersjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(friendsjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainUserjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectedItemjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(friendsAndFollowersjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchResultjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nodeStrokejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(mainUserjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(friendsjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(followersjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(friendsAndFollowersjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(selectedItemjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(textjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(nodeStrokejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(edgejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(searchResultjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Intervalos de requisição"));

        jLabel1.setText("Atualizações");

        updatesjSlider.setMaximum(60);
        updatesjSlider.setMinimum(1);
        updatesjSlider.setValue(5);
        updatesjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                updatesjSliderStateChanged(evt);
            }
        });

        updatesjLabel.setText("5 minutos");

        mentionsUserjLabel.setText("@@@");

        mentionsjSlider.setMaximum(60);
        mentionsjSlider.setMinimum(1);
        mentionsjSlider.setValue(5);
        mentionsjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mentionsjSliderStateChanged(evt);
            }
        });

        mentionsjLabel.setText("5 minutos");

        jLabel15.setText("Favoritos");

        favoritesjSlider.setMaximum(60);
        favoritesjSlider.setMinimum(1);
        favoritesjSlider.setValue(5);
        favoritesjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                favoritesjSliderStateChanged(evt);
            }
        });

        favoritesjLabel.setText("5 minutos");

        jLabel17.setText("Mensagens Diretas");

        directMessagesjSlider.setMaximum(60);
        directMessagesjSlider.setMinimum(1);
        directMessagesjSlider.setValue(5);
        directMessagesjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                directMessagesjSliderStateChanged(evt);
            }
        });

        directMessagesjLabel.setText("5 minutos");

        jLabel20.setText("Busca");

        searchjSlider.setMaximum(60);
        searchjSlider.setMinimum(1);
        searchjSlider.setValue(5);
        searchjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                searchjSliderStateChanged(evt);
            }
        });

        searchjLabel.setText("5 minutos");

        jLabel22.setText("Public Timeline");

        publicTimelinejSlider.setMaximum(60);
        publicTimelinejSlider.setMinimum(1);
        publicTimelinejSlider.setValue(5);
        publicTimelinejSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                publicTimelinejSliderStateChanged(evt);
            }
        });

        publicTimelinejLabel.setText("5 minutos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(mentionsUserjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mentionsjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mentionsjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(favoritesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(favoritesjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(updatesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(updatesjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(directMessagesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(directMessagesjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(publicTimelinejSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(publicTimelinejLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(updatesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatesjLabel)
                    .addComponent(jLabel17)
                    .addComponent(directMessagesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(directMessagesjLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mentionsUserjLabel)
                    .addComponent(mentionsjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mentionsjLabel)
                    .addComponent(jLabel20)
                    .addComponent(searchjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchjLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(favoritesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favoritesjLabel)
                    .addComponent(jLabel22)
                    .addComponent(publicTimelinejSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(publicTimelinejLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Outras opções"));

        jLabel5.setText("Tipo de aresta entre os usuários");

        edgeTypejComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Reta", "Curva" }));

        jLabel18.setText("<html>Intervalo da troca de mensagem<br>para usuários mais populares</html>");

        mostPopularUsersjSlider.setMaximum(60);
        mostPopularUsersjSlider.setMinimum(1);
        mostPopularUsersjSlider.setValue(7);
        mostPopularUsersjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mostPopularUsersjSliderStateChanged(evt);
            }
        });

        mostPopularUsersjLabel.setText("5 segundos");

        jLabel2.setText("Número de Tweets por requisição");

        updatesToGetjSlider.setMaximum(200);
        updatesToGetjSlider.setMinimum(50);
        updatesToGetjSlider.setValue(100);
        updatesToGetjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                updatesToGetjSliderStateChanged(evt);
            }
        });

        updatesToGetjLabel.setText("100 Tweets");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edgeTypejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(mostPopularUsersjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mostPopularUsersjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(212, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(updatesToGetjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updatesToGetjLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                        .addGap(184, 184, 184))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(edgeTypejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(updatesToGetjLabel)
                    .addComponent(updatesToGetjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mostPopularUsersjLabel)
                    .addComponent(mostPopularUsersjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap())
        );

        restorejButton.setText("Restaurar");
        restorejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restorejButtonActionPerformed(evt);
            }
        });

        canceljButton.setText("Cancelar");
        canceljButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canceljButtonActionPerformed(evt);
            }
        });

        savejButton.setText("Salvar");
        savejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savejButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(savejButton)
                        .addGap(18, 18, 18)
                        .addComponent(canceljButton)
                        .addGap(18, 18, 18)
                        .addComponent(restorejButton))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restorejButton)
                    .addComponent(canceljButton)
                    .addComponent(savejButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void updatesjSliderStateChanged(javax.swing.event.ChangeEvent evt) {                                            
        if(updatesjSlider.getValue() == 1)
            updatesjLabel.setText(updatesjSlider.getValue() + minuto);
        else
            updatesjLabel.setText(updatesjSlider.getValue() + minutos);
    }                                           

    private void mentionsjSliderStateChanged(javax.swing.event.ChangeEvent evt) {                                             
        if(mentionsjSlider.getValue() == 1)
            mentionsjLabel.setText(mentionsjSlider.getValue() + minuto);
        else
            mentionsjLabel.setText(mentionsjSlider.getValue() + minutos);
    }                                            

    private void favoritesjSliderStateChanged(javax.swing.event.ChangeEvent evt) {                                              
        if(favoritesjSlider.getValue() == 1)
            favoritesjLabel.setText(favoritesjSlider.getValue() + minuto);
        else
            favoritesjLabel.setText(favoritesjSlider.getValue() + minutos);
    }                                             

    private void directMessagesjSliderStateChanged(javax.swing.event.ChangeEvent evt) {                                                   
        if(directMessagesjSlider.getValue() == 1)
            directMessagesjLabel.setText(directMessagesjSlider.getValue() + minuto);
        else
            directMessagesjLabel.setText(directMessagesjSlider.getValue() + minutos);
    }                                                  

    private void searchjSliderStateChanged(javax.swing.event.ChangeEvent evt) {                                           
        if(searchjSlider.getValue() == 1)
            searchjLabel.setText(searchjSlider.getValue() + minuto);
        else
            searchjLabel.setText(searchjSlider.getValue() + minutos);
    }                                          

    private void publicTimelinejSliderStateChanged(javax.swing.event.ChangeEvent evt) {                                                   
        if(publicTimelinejSlider.getValue() == 1)
            publicTimelinejLabel.setText(publicTimelinejSlider.getValue() + minuto);
        else
            publicTimelinejLabel.setText(publicTimelinejSlider.getValue() + minutos);
    }                                                  

    private void mainUserjButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para o Usuário Principal",
                mainUserjButton.getBackground());

        if(newColor != null){
            mainUserjButton.setBackground(newColor);
            mainUserjButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                               

    private void friendsjButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para os Amigos",
                friendsjButton.getBackground());
        if(newColor != null){
            friendsjButton.setBackground(newColor);
            friendsjButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                              

    private void followersjButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para os Seguidores",
                followersjButton.getBackground());

        if(newColor != null){
            followersjButton.setBackground(newColor);
            followersjButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                                

    private void friendsAndFollowersjButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                           
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para os Amigos e Seguidores",
                friendsAndFollowersjButton.getBackground());

        if(newColor != null){
            friendsAndFollowersjButton.setBackground(newColor);
            friendsAndFollowersjButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                                          

    private void selectedItemjButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para os Usuários Selecionados",
                selectedItemjButton.getBackground());

        if(newColor != null){
            selectedItemjButton.setBackground(newColor);
            selectedItemjButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                                   

    private void textjButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para o Nome dos Usuários",
                textjButton.getBackground());

        if(newColor != null){
            textjButton.setBackground(newColor);
            textjButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                           

    private void nodeStrokejButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para a Borda dos Usuários",
                nodeStrokejButton.getBackground());

        if(newColor != null){
            nodeStrokejButton.setBackground(newColor);
            nodeStrokejButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                                 

    private void edgejButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para as Arestas entre os Usuários",
                edgejButton.getBackground());

        if(newColor != null){
            edgejButton.setBackground(newColor);
            edgejButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                           

    private void searchResultjButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        //"<html>Resultados da Busca da<br>barra de ferramentas</html>"
        Color newColor =
                JColorChooser.showDialog(this, "Escolha uma cor para os Resultados da Busca da Barra de ferramentas",
                searchResultjButton.getBackground());

        if(newColor != null){
            searchResultjButton.setBackground(newColor);
            searchResultjButton.setBorder(BorderFactory.createLineBorder(newColor, 50));
        }
    }                                                   

    private void mostPopularUsersjSliderStateChanged(javax.swing.event.ChangeEvent evt) {                                                     
        if(mostPopularUsersjSlider.getValue() == 1)
            mostPopularUsersjLabel.setText(mostPopularUsersjSlider.getValue() + segundo);
        else
            mostPopularUsersjLabel.setText(mostPopularUsersjSlider.getValue() + segundos);
    }                                                    

    private void canceljButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
        this.setVisible(false);
        //TODO dar um jeito se anular
    }                                             

    private void savejButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            

        //interval updates
        controller.setProperty(ConfigurationType.intervalUpdates, updatesjSlider.getValue());

        //interval mentions
        controller.setProperty(ConfigurationType.intervalMentions, mentionsjSlider.getValue());

        //interval favorites
        controller.setProperty(ConfigurationType.intervalFavorites, favoritesjSlider.getValue());

        //interval direct messages
        controller.setProperty(ConfigurationType.intervalDirectMessages, directMessagesjSlider.getValue());

        //interval search
        controller.setProperty(ConfigurationType.intervalSearch, searchjSlider.getValue());

        //interval public timeline
        controller.setProperty(ConfigurationType.intervalPublicTimeline, publicTimelinejSlider.getValue());

        //color mainUser
        controller.setProperty(ConfigurationType.mainUserColor, mainUserjButton.getBackground().getRGB());

        //color friends
        controller.setProperty(ConfigurationType.friendsColor, friendsjButton.getBackground().getRGB());

        //color followers
        controller.setProperty(ConfigurationType.followersColor, followersjButton.getBackground().getRGB());

        //color friends and followers
        controller.setProperty(ConfigurationType.friendsAndFollowersColor, friendsAndFollowersjButton.getBackground().getRGB());

        //color selected item
        controller.setProperty(ConfigurationType.selectedItemColor, selectedItemjButton.getBackground().getRGB());

        //color text
        controller.setProperty(ConfigurationType.textColor, textjButton.getBackground().getRGB());

        //color node stroke
        controller.setProperty(ConfigurationType.nodeStrokeColor, nodeStrokejButton.getBackground().getRGB());

        //color edge
        controller.setProperty(ConfigurationType.edgeColor, edgejButton.getBackground().getRGB());

        //color search result
        controller.setProperty(ConfigurationType.searchResultColor, searchResultjButton.getBackground().getRGB());

        //edge type
        controller.setProperty(ConfigurationType.edgeType, edgeTypejComboBox.getSelectedIndex());

        //interval most popular users
        controller.setProperty(ConfigurationType.intervalMostPopularUsers, mostPopularUsersjSlider.getValue());
        
        //updates to get
        controller.setProperty(ConfigurationType.updatesToGet, updatesToGetjSlider.getValue());

        controller.saveSettings();

    }                                           

    private void restorejButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               

        Settings settings = controller.getDefaultSettingsOther();
    	
    	Color color;

        //interval updates
        updatesjSlider.setValue(settings.getIntervalUpdates());

        //interval mentions
        mentionsjSlider.setValue(settings.getIntervalMentions());

        //interval favorites
        favoritesjSlider.setValue(settings.getIntervalFavorites());

        //interval direct messages
        directMessagesjSlider.setValue(settings.getIntervalDirectMessages());

        //interval search
        searchjSlider.setValue(settings.getIntervalSearch());

        //interval public timeline
        publicTimelinejSlider.setValue(settings.getIntervalPublicTimeline());

        //color mainUser
        color = new Color(settings.getMainUserColor());
        mainUserjButton.setBackground(color);
        mainUserjButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color friends
        color = new Color(settings.getFriendsColor());
        friendsjButton.setBackground(color);
        friendsjButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color followers
        color = new Color(settings.getFollowersColor());
        followersjButton.setBackground(color);
        followersjButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color friends and followers
        color = new Color(settings.getFriendsAndFollowersColor());
        friendsAndFollowersjButton.setBackground(color);
        friendsAndFollowersjButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color selected item
        color = new Color(settings.getSelectedItemColor());
        selectedItemjButton.setBackground(color);
        selectedItemjButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color text
        color = new Color(settings.getTextColor());
        textjButton.setBackground(color);
        textjButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color node stroke
        color = new Color(settings.getNodeStrokeColor());
        nodeStrokejButton.setBackground(color);
        nodeStrokejButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color edge
        color = new Color(settings.getEdgeColor());
        edgejButton.setBackground(color);
        edgejButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //color search result
        color = new Color(settings.getSearchResultColor());
        searchResultjButton.setBackground(color);
        searchResultjButton.setBorder(BorderFactory.createLineBorder(color, 50));

        //edge type
        edgeTypejComboBox.setSelectedIndex(settings.getEdgeType());

        //interval most popular users
        mostPopularUsersjSlider.setValue(settings.getIntervalMostPopularUsers());
        
        //updates to get
        updatesToGetjSlider.setValue(settings.getUpdatesToGet());

    }                                              

    private void updatesToGetjSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        if(updatesToGetjSlider.getValue() == 1)
            updatesToGetjLabel.setText(updatesToGetjSlider.getValue() + tweet);
        else
            updatesToGetjLabel.setText(updatesToGetjSlider.getValue() + tweets);
    }


    private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();

    private final String minuto = " minuto";
    private final String minutos = " minutos";
    private final String segundo = " segundo";
    private final String segundos = " segundos";
    private final String tweet = " Tweet";
    private final String tweets = " Tweets";

    // Variables declaration - do not modify
    private javax.swing.JButton canceljButton;
    private javax.swing.JLabel directMessagesjLabel;
    private javax.swing.JSlider directMessagesjSlider;
    private javax.swing.JComboBox edgeTypejComboBox;
    private javax.swing.JButton edgejButton;
    private javax.swing.JLabel favoritesjLabel;
    private javax.swing.JSlider favoritesjSlider;
    private javax.swing.JButton followersjButton;
    private javax.swing.JButton friendsAndFollowersjButton;
    private javax.swing.JButton friendsjButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton mainUserjButton;
    private javax.swing.JLabel mentionsUserjLabel;
    private javax.swing.JLabel mentionsjLabel;
    private javax.swing.JSlider mentionsjSlider;
    private javax.swing.JLabel mostPopularUsersjLabel;
    private javax.swing.JSlider mostPopularUsersjSlider;
    private javax.swing.JButton nodeStrokejButton;
    private javax.swing.JLabel publicTimelinejLabel;
    private javax.swing.JSlider publicTimelinejSlider;
    private javax.swing.JButton restorejButton;
    private javax.swing.JButton savejButton;
    private javax.swing.JButton searchResultjButton;
    private javax.swing.JLabel searchjLabel;
    private javax.swing.JSlider searchjSlider;
    private javax.swing.JButton selectedItemjButton;
    private javax.swing.JButton textjButton;
    private javax.swing.JLabel updatesToGetjLabel;
    private javax.swing.JSlider updatesToGetjSlider;
    private javax.swing.JLabel updatesjLabel;
    private javax.swing.JSlider updatesjSlider;
    // End of variables declaration

}
