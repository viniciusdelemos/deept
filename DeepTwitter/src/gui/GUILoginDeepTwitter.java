package gui;

import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author rotta
 */
public class GUILoginDeepTwitter extends javax.swing.JFrame {
    private boolean isTwitterUser;
    /** Creates new form GUILoginDeepTwitter */
    public GUILoginDeepTwitter() {
        initComponents();
        
        txtPassword.setVisible(false);
        labelPassword.setVisible(false);
        isTwitterUser = true;
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
        jLabel1 = new javax.swing.JLabel();
        labelUser = new javax.swing.JLabel();
        labelPassword = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        OkButton = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelTip = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DeepTwitter - Login");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(java.awt.Color.white);
        setName("LoginDeepTwitter"); // NOI18N
        setResizable(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo.jpg"))); // NOI18N

        labelUser.setFont(new java.awt.Font("Tahoma", 0, 14));
        labelUser.setText("Usu�rio:");

        labelPassword.setFont(new java.awt.Font("Tahoma", 0, 14));
        labelPassword.setText("PIN:");
        
        txtUser.setNextFocusableComponent(txtPassword);
        
        txtUser.setText("");

        txtPassword.setNextFocusableComponent(OkButton);
        
        txtPassword.setText("");

        OkButton.setText("OK");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Logar com meu usu�rio");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton2.setText("Encontrar outro usu�rio");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        panelTip.setBackground(java.awt.SystemColor.control);
        panelTip.setBorder(null);
        panelTip.setEditable(false);
        panelTip.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        panelTip.setText("Informe seu nome de usu�rio do Twitter.");
        panelTip.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane1.setViewportView(panelTip);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jRadioButton1)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton2))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(OkButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelPassword)
                            .addComponent(labelUser))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPassword, 0, 0, Short.MAX_VALUE)
                            .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelUser)
                            .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelPassword)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(OkButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        panelTip.setText("Informe o nome de um usu�rio do Twitter que deseje explorar.");
        labelPassword.setVisible(false);
        txtPassword.setVisible(false);
        isTwitterUser = false;
    }                                             

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        panelTip.setText("Informe seu nome de usu�rio do Twitter.");
        labelPassword.setVisible(false);
        txtPassword.setVisible(false);
        isTwitterUser = true;
    }                                             

    public void addLoginListener(ActionListener loginListener) {
		OkButton.addActionListener(loginListener);
	}

    public String getUser()
    {
    	return txtUser.getText();
    }

	public String getPassword()
    {
    	return txtPassword.getText();
    }

    public boolean isTwitterUser()
    {
    	return isTwitterUser;
    }
    
    //se o usu�rio que logar ou explorar algu�m no twitter
    public String getSelecao()
    {
    	if(jRadioButton1.isSelected())
    	{
    		return "Logar";
    	}
    	else
    		return "Explorar";
    }
    
    public void setPin()
    {
    	txtPassword.setVisible(true);
    	labelPassword.setVisible(true);
    	panelTip.setText("Informe seu PIN para efetuar o login no Twitter.");
    	
    	this.validate();
    }

    // Variables declaration - do not modify
    private javax.swing.JButton OkButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelPassword;
    private javax.swing.JLabel labelUser;
    private javax.swing.JTextPane panelTip;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUser;
    // End of variables declaration

}
