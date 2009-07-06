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

import prefuse.util.force.DragForce;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;

import controller.ControllerDeepTwitter;

/**
 *
 * @author Vinicius
 */
public class GUINetworkForces extends javax.swing.JFrame {
	
	
	private float gravConstant = -1f;
	private float minDistance = -1f;
	private float theta = 0.9f;
	
	private float drag = 0.007f;
	private float springCoeff = 9.99E-6f;
	private float defaultLength = 180f;
	
	private ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();

    /** Creates new form GUINetworkForces */
    public GUINetworkForces() {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            javax.swing.SwingUtilities.updateComponentTreeUI( this );
        } catch (Exception ex) {
        }
        initComponents();
        init();
    
    }
    
    private void init(){
    	
        prefuse.util.ui.JForcePanel fpanel = new prefuse.util.ui.JForcePanel(controller.getNetworkView().getForceSimulator());
        jPanel.add(fpanel);
        
        restorejButton.setEnabled(false);
        
    	prefuse.util.force.Force[] forces = controller.getNetworkView().getForceSimulator().getForces();
    	
    	for(int i=0;i<forces.length;i++){
    		if(forces[i] instanceof NBodyForce){    			
    			NBodyForce n = (NBodyForce) forces[i];
    			
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("GravitationalConstant")){
    					try{
    						gravConstant = n.getParameter(j);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("Distance")){
    					try{
    						minDistance = n.getParameter(j);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("BarnesHutTheta")){
    					try{
    						theta = n.getParameter(j);
    					}catch(Exception e){
    					}
    				}
    			}
    		}
    		else if(forces[i] instanceof SpringForce){
    			SpringForce n = (SpringForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("SpringCoefficient")){
    					try{
    						springCoeff = n.getParameter(j);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("DefaultSpringLength")){
    					try{
    						defaultLength = n.getParameter(j);
    					}catch(Exception e){
    					}
    				}
    			}    			
    		}    		
    		else if(forces[i] instanceof DragForce){    			
    			DragForce n = (DragForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("DragCoefficient")){
    					try{
    						drag = n.getParameter(j);
    					}catch(Exception e){
    					}
    				}    				
    			}
    		}
    	}
    	
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
        
    	prefuse.util.force.Force[] forces = controller.getNetworkView().getForceSimulator().getForces();
    	
    	for(int i=0;i<forces.length;i++){
    		if(forces[i] instanceof NBodyForce){    			
    			NBodyForce n = (NBodyForce) forces[i];
    			
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("GravitationalConstant")){
    					try{
    						n.setParameter(j, gravConstant);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("Distance")){
    					try{
    						n.setParameter(j, minDistance);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("BarnesHutTheta")){
    					try{
    						n.setParameter(j, theta);
    					}catch(Exception e){
    					}
    				}
    			}
    		}
    		else if(forces[i] instanceof SpringForce){
    			SpringForce n = (SpringForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("SpringCoefficient")){
    					try{
    						n.setParameter(j, springCoeff);
    					}catch(Exception e){
    					}
    				}
    				else if(n.getParameterName(j).equals("DefaultSpringLength")){
    					try{
    						n.setParameter(j, defaultLength);
    					}catch(Exception e){
    					}
    				}
    			}    			
    		}    		
    		else if(forces[i] instanceof DragForce){    			
    			DragForce n = (DragForce) forces[i];
    			for(int j=0 ; j<n.getParameterCount(); j++){    				
    				if(n.getParameterName(j).equals("DragCoefficient")){
    					try{
    						n.setParameter(j, drag);
    					}catch(Exception e){
    					}
    				}    				
    			}
    		}
    	}

    	
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
