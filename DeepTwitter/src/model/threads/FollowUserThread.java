package model.threads;

import gui.visualizations.NetworkView;
import model.MessageType;
import prefuse.data.Node;
import prefuse.visual.VisualItem;
import twitter4j.Relationship;
import twitter4j.TwitterException;
import controller.ControllerDeepTwitter;


public class FollowUserThread extends Thread {			
		NetworkView networkView;
		VisualItem targetItem;
		boolean follow;
		ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public FollowUserThread(NetworkView networkView, VisualItem item, boolean follow) {
		this.networkView = networkView;	
		targetItem = item;
		this.follow = follow;
		this.start();
	}
	
	public void run()
	{		
		try {
			
			Node mainUserNode = networkView.getNodeById(0); //user principal			
			Node targetNode = (Node)targetItem.getSourceTuple();
			String mainUserId = mainUserNode.getString("idTwitter");
			String targetUserId = targetNode.getString("idTwitter");
			
			String mainScreenName = mainUserNode.getString("screenName");
			String targetScreenName = targetNode.getString("screenName");
			
			
			//boolean existsFriendship = controller.getTwitter(). existsFriendship(mainUserId, targetUserId);
			
			//N�o estava funcionando consultando pelo ID
			//Relationship relationship = controller.getTwitter().showFriendship(mainUserId, targetUserId);
			Relationship relationship = controller.getTwitter().showFriendship(mainScreenName, targetScreenName);
			boolean existsFriendship = relationship.isSourceFollowingTarget();
			
			//controller.getTwitter().showFriendship(mainUserId, targetUserId)
			
			if(follow) {
				
				if(existsFriendship) {
					
					controller.showMessageDialog("Voc� j� est� seguindo esta pessoa.\nProvavelmente voc� ainda n�o adicionou seus amigos � rede.",MessageType.INFORMATION);
					return;
				}
				controller.getTwitter().createFriendship(Long.parseLong(targetNode.getString("idTwitter")));
				//controller.getTwitter().follow(userId);						
				networkView.addEdge(mainUserNode, targetNode);
				controller.setStatusBarMessage("Agora seguindo "+targetNode.getString("name"),MessageType.INFORMATION);
			}
			
			else { //leave
				
				if(!existsFriendship) return;
				controller.getTwitter().destroyFriendship(Long.parseLong(targetNode.getString("idTwitter")));
				//controller.getTwitter().leave(userId);				
				networkView.removeEdge(mainUserNode, targetNode);
				controller.setStatusBarMessage("Deixando de seguir "+targetNode.getString("name"),MessageType.INFORMATION);
			}

		} catch (TwitterException e) {	

			//TODO melhorar, inserior nome no titulo etc. e tentar pedir permissao
			
			if(e.getStatusCode()==403) {
				
				controller.showMessageDialog("O usu�rio � protegido e n�o lhe permite realizar esta a��o",MessageType.ERROR);
				
				return;
			}			
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
		}
	}
}
