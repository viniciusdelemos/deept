package model;

import controller.ControllerDeepTwitter;
import prefuse.visual.NodeItem;
import twitter4j.TwitterException;


public class FollowUserThread extends Thread {			
		GraphicManager gManager;
		NodeItem userNode;
		boolean follow;
		
	public FollowUserThread(GraphicManager gManager, NodeItem item, boolean follow) {
		this.gManager = gManager;	
		userNode = item;
		this.follow = follow;
	}
	
	public void run()
	{		
		try {
			String userId = userNode.getString("idTwitter");
			if(follow) {				
				ControllerDeepTwitter.getTwitter().create(userId);
				ControllerDeepTwitter.getTwitter().follow(userId);						
				gManager.addEdge(gManager.getNodeById(0), gManager.getNodeByTwitterId(Integer.parseInt(userId)));
			}
			else { //leave
				ControllerDeepTwitter.getTwitter().destroy(userId);
				ControllerDeepTwitter.getTwitter().leave(userId);				
				gManager.removeEdge(gManager.getNodeById(0), gManager.getNodeByTwitterId(Integer.parseInt(userId)));
			}

		} catch (TwitterException e) {			
			ControllerDeepTwitter.showMessageDialog(null, e.getMessage());
		}
	}
}
