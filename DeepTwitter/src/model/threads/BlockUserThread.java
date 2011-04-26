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
			if(!networkView.getSocialNetwork().isUserBlocked(userNode.getLong("idTwitter"))) {
				controller.getTwitter().createBlock(Long.parseLong(userNode.getString("idTwitter")));
				
				controller.setStatusBarMessage("Blocked "+userNode.getString("name"),MessageType.INFORMATION);
				
				//(ATUALIZAÇÃO)
				//mudança de int para long
				networkView.getSocialNetwork().addBlockedUsers(new long[]{userNode.getLong("idTwitter")});
				networkView.removeNode(userNode);
			}
			else { 
				controller.getTwitter().destroyBlock(Long.parseLong(userNode.getString("idTwitter")));
				
				controller.setStatusBarMessage("Unblocked "+userNode.getString("name"),MessageType.INFORMATION);
				
				networkView.getSocialNetwork().removeBlockedUser(userNode.getInt("idTwitter"));
			}
			
		} catch (TwitterException e) {
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
		}
	}
}
