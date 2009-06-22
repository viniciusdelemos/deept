package model.threads;

import gui.visualizations.NetworkView;
import model.MessageType;
import prefuse.visual.VisualItem;
import twitter4j.TwitterException;
import controller.ControllerDeepTwitter;

public class BlockUserThread extends Thread {
	NetworkView networkView;	
	VisualItem userNode;
	ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public BlockUserThread(NetworkView networkView, VisualItem user) {
		this.networkView = networkView;
		this.userNode = user;
		this.start();
	}
	
	public void run()
	{
		try {
			if(!networkView.getSocialNetwork().isUserBlocked(userNode.getInt("idTwitter"))) {
				controller.getTwitter().createBlock(userNode.getString("idTwitter"));				
				networkView.getSocialNetwork().addBlockedUsers(new int[]{userNode.getInt("idTwitter")});
				networkView.removeNode(userNode);
			}
			else { 
				controller.getTwitter().destroyBlock(userNode.getString("idTwitter"));
				networkView.getSocialNetwork().removeBlockedUser(userNode.getInt("idTwitter"));
			}
			
		} catch (TwitterException e) {
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
		}
	}
}
