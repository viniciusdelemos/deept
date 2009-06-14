package model.threads;

import gui.visualizations.GraphicManager;
import model.MessageType;
import prefuse.data.Node;
import prefuse.visual.VisualItem;
import twitter4j.TwitterException;
import controller.ControllerDeepTwitter;


public class FollowUserThread extends Thread {			
		GraphicManager gManager;
		VisualItem targetItem;
		boolean follow;
		ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public FollowUserThread(GraphicManager gManager, VisualItem item, boolean follow) {
		this.gManager = gManager;	
		targetItem = item;
		this.follow = follow;
		this.start();
	}
	
	public void run()
	{		
		try {
			Node mainUserNode = gManager.getNodeById(0); //user principal			
			Node targetNode = (Node)targetItem.getSourceTuple();
			String mainUserId = mainUserNode.getString("idTwitter");
			String targetUserId = targetNode.getString("idTwitter");
			
			boolean existsFriendship = controller.getTwitter().existsFriendship(mainUserId, targetUserId);
			
			if(follow) {		
				if(existsFriendship) { 
					controller.showMessageDialog("Você já está seguindo esta pessoa.\nProvavelmente você ainda não adicionou seus amigos à rede.",MessageType.INFORMATION);
					return;
				}
				controller.getTwitter().createFriendship(targetNode.getString("idTwitter"));
				//controller.getTwitter().follow(userId);						
				gManager.addEdge(mainUserNode, targetNode);
				controller.setStatusBarMessage("Agora seguindo "+targetNode.getString("name"));
			}
			else { //leave
				if(!existsFriendship) return;
				controller.getTwitter().destroyFriendship(targetNode.getString("idTwitter"));
				//controller.getTwitter().leave(userId);				
				gManager.removeEdge(mainUserNode, targetNode);
				controller.setStatusBarMessage("Deixando de seguir "+targetNode.getString("name"));
			}

		} catch (TwitterException e) {	
			//TODO melhorar, inserior nome no titulo etc. e tentar pedir permissao
			if(e.getStatusCode()==403) {
				controller.showMessageDialog("O usuário é protegido e não lhe permite realizar esta ação",MessageType.ERROR);
				return;
			}			
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
			
		}
	}
}
