package model;

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
		
	public FollowUserThread(GraphicManager gManager, VisualItem item, boolean follow) {
		this.gManager = gManager;	
		targetItem = item;
		this.follow = follow;
	}
	
	public void run()
	{		
		try {
			Node mainUserNode = gManager.getNodeById(0); //user principal			
			Node targetNode = (Node)targetItem.getSourceTuple();
			String mainUserId = mainUserNode.getString("idTwitter");
			String targetUserId = targetNode.getString("idTwitter");
			
			if(follow) {		
				if(ControllerDeepTwitter.getTwitter().exists(mainUserId, targetUserId)) return;
				ControllerDeepTwitter.getTwitter().create(targetNode.getString("idTwitter"));
				//ControllerDeepTwitter.getTwitter().follow(userId);						
				gManager.addEdge(mainUserNode, targetNode);
			}
			else { //leave
				if(!ControllerDeepTwitter.getTwitter().exists(mainUserId, targetUserId)) return;
				ControllerDeepTwitter.getTwitter().destroy(targetNode.getString("idTwitter"));
				//ControllerDeepTwitter.getTwitter().leave(userId);				
				gManager.removeEdge(mainUserNode, targetNode);
			}

		} catch (TwitterException e) {			
			ControllerDeepTwitter.showMessageDialog(null, e.getMessage());
		}
	}
}
