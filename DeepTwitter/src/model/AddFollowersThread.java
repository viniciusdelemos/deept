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
		NodeItem source;	
		GraphicManager gManager;
		
	public AddFollowersThread(GraphicManager gManager, NodeItem s) {
		this.gManager = gManager;		
		this.source = s;
	}
	
	public void run()
	{
		try {			
			List<User> followers = ControllerDeepTwitter.getTwitter().getFollowers(source.get("idTwitter").toString());
			int notAdded = 0;
			
			for(User user : followers)
			{
				User u = (User)gManager.getUser(user.getId());					
				
				if(u == null)
				{	
					double x = source.getX();					
					double y = source.getY();
					
					Node n = gManager.addNode(user);
					VisualItem newNode = (VisualItem)gManager.getVisualization().getVisualItem(gManager.NODES, n);
					PrefuseLib.setX(newNode, null, x);
					PrefuseLib.setY(newNode, null, y);
					
					//AO TER UM VISUALITEM, USAR VisualItem.getSourceTuple() para pegar instancia de Nodo ou Edge.					
					gManager.addEdge(n,(Node)source.getSourceTuple()); 		
				}
				else
				{
					Node n = gManager.getNodeByTwitterId(u.getId());
					gManager.addEdge(n,(Node)source.getSourceTuple());
					notAdded++;
				}
			}
			ControllerDeepTwitter.setStatusBarMessage("Adicionados "+(followers.size()-notAdded)+" seguidores de "+source.getString("name") +" à rede. "+notAdded+" já existentes.");
			System.out.println("Node Count: "+gManager.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			ControllerDeepTwitter.showMessageDialog(null,e.getMessage());
		}
	}
}
