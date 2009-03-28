package model;

import java.util.List;

import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;

public class AddFollowersThread extends Thread {
		VisualItem source;	
		GraphicManager gManager;
		
	public AddFollowersThread(GraphicManager gManager, VisualItem s) {
		this.gManager = gManager;		
		this.source = s;
	}
	
	public void run()
	{
		try {			
			List<User> followers = ControllerDeepTwitter.getTwitter().getFollowers(source.get("idTwitter").toString());
			int notAdded = 0;
			boolean isShowingFollowers = source.getBoolean("isShowingFollowers");
			
			for(User user : followers)
			{
				User u = (User)gManager.getUser(user.getId());					
				
				if(u == null)
				{	
					double x = source.getX();					
					double y = source.getY();
					
					Node n = gManager.addNode(user);
					VisualItem newNode = gManager.getVisualization().getVisualItem(gManager.NODES, n);
					PrefuseLib.setX(newNode, null, x);
					PrefuseLib.setY(newNode, null, y);
					
					//AO TER UM VISUALITEM, USAR VisualItem.getSourceTuple() para pegar instancia de Nodo ou Edge.					
					gManager.addEdge(n,(Node)source.getSourceTuple()); 		
				}
				else if(!isShowingFollowers)
				{
					Node n = gManager.getNodeByTwitterId(u.getId());
					gManager.addEdge(n,(Node)source.getSourceTuple());
					notAdded++;
				}
			}
			if(!isShowingFollowers)
				ControllerDeepTwitter.setStatusBarMessage("Adicionados "+(followers.size()-notAdded)+" seguidores de "+source.getString("name") +" � rede. "+notAdded+" j� existentes.");
			else
				ControllerDeepTwitter.setStatusBarMessage("Adicionados "+notAdded+" seguidores de "+source.getString("name") +" � rede. "+(followers.size()-notAdded)+" j� existentes.");
			
			source.setBoolean("isShowingFollowers",true);
			//node count: botar no log
			System.out.println("Node Count: "+gManager.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			ControllerDeepTwitter.showMessageDialog(e.getMessage(),MessageType.ERROR);
		}
	}
}
