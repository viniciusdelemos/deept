package model;

import java.util.List;

import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;

public class AddFriendsThread extends Thread {
		VisualItem source;	
		GraphicManager gManager;
		
	public AddFriendsThread(GraphicManager gManager, VisualItem s) {
		this.gManager = gManager;		
		this.source = s;
	}
	
	public void run()
	{
		try {
			source.set("isOpen", true);
			List<User> friends = ControllerDeepTwitter.getTwitter().getFriends(source.get("idTwitter").toString());
			int notAdded = 0;
			
			for(User user : friends)
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
					gManager.addEdge((Node)source.getSourceTuple(), n); 		
				}
				else
				{
					Node n = gManager.getNodeByTwitterId(u.getId());
					gManager.addEdge((Node)source.getSourceTuple(), n);
					notAdded++;
				}
			}
			ControllerDeepTwitter.setStatusBarMessage("Adicionados "+(friends.size()-notAdded)+" amigos de "+source.getString("name") +" à rede. "+notAdded+" já existentes.");
			System.out.println("Node Count: "+gManager.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			ControllerDeepTwitter.showMessageDialog(null,e.getMessage());
		}
	}
}
