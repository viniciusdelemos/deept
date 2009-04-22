package model.threads;

import model.GraphicManager;
import model.MessageType;
import controller.ControllerDeepTwitter;
import prefuse.data.Node;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import twitter4j.TwitterException;
import twitter4j.User;


public class FollowUserThread extends Thread {			
		GraphicManager gManager;
		VisualItem targetItem;
		boolean follow;
		ControllerDeepTwitter controller;
		
	public FollowUserThread(GraphicManager gManager, VisualItem item, boolean follow) {
		controller = ControllerDeepTwitter.getInstance();
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
			
			boolean existsFriendship = controller.getTwitter().exists(mainUserId, targetUserId);
			
			if(follow) {		
				if(existsFriendship) return;
				controller.getTwitter().create(targetNode.getString("idTwitter"));
				//controller.getTwitter().follow(userId);						
				gManager.addEdge(mainUserNode, targetNode);
				controller.setStatusBarMessage("Agora seguindo "+targetNode.getString("name"));
			}
			else { //leave
				if(!existsFriendship) return;
				controller.getTwitter().destroy(targetNode.getString("idTwitter"));
				//controller.getTwitter().leave(userId);				
				gManager.removeEdge(mainUserNode, targetNode);
				controller.setStatusBarMessage("Deixando de seguir "+targetNode.getString("name"));
			}

		} catch (TwitterException e) {			
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
		}
	}
}
