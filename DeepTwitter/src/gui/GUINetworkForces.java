/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GUINetworkForces.java
 *
 * Created on Jul 5, 2009, 10:32:29 PM
 */

package gui;

import java.awt.event.ActionListener;

import controller.ControllerDeepTwitter;

/**
 *
 * @author Vinicius
 */
public class GUINetworkForces extends javax.swing.JFrame {

    /** Creates new form GUINetworkForces */
    public GUINetworkForces() {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            javax.swing.SwingUtilities.updateComponentTreeUI( this );
        } catch (Exception ex) {
        }
        initComponents();
        ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
        prefuse.util.ui.JForcePanel fpanel = new prefuse.util.ui.JForcePanel(controller.getNetworkView().getForceSimulator());
        jPanel.add(fpanel);
        
        restorejButton.setEnabled(false);


    }

    public void addMainWindowListener(ActionListener listener){

        savejButton.setActionCommand("buttonOKNetworkForces");
    	canceljButton.setActionCommand("buttonCancelNetworkForces");
    	restorejButton.setActionCommand("buttonRestoreNetworkForces"); //TODO restore

    	savejButton.addActionListener(listener);
    	canceljButton.addActionListener(listener);
    	restorejButton.addActionListener(listener);


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
        jPanel = new javax.swing.JPanel();
        restorejButton = new javax.swing.JButton();
        canceljButton = new javax.swing.JButton();
        savejButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DeepTwitter - For�as da Rede Contatos");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel.setBackground(new java.awt.Color(255, 255, 255));
        jPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        jPanel.setLayout(new java.awt.GridLayout(1, 1));

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addComponent(savejButton)
                .addGap(18, 18, 18)
                .addComponent(canceljButton)
                .addGap(18, 18, 18)
                .addComponent(restorejButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restorejButton)
                    .addComponent(canceljButton)
                    .addComponent(savejButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>

    private void savejButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void canceljButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void restorejButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }


    // Variables declaration - do not modify
    private javax.swing.JButton canceljButton;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton restorejButton;
    private javax.swing.JButton savejButton;
    // End of variables declaration

}
