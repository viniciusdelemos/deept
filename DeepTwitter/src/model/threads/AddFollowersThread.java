package model.threads;

import java.util.List;

import model.GraphicManager;
import model.MessageType;
import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;

public class AddFollowersThread extends Thread {
		VisualItem source;	
		GraphicManager gManager;
		ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public AddFollowersThread(GraphicManager gManager, VisualItem s) {
		this.gManager = gManager;		
		this.source = s;
		this.start();
	}
	
	public void run()
	{
		try {			
			List<User> followers = controller.getTwitter().getFollowers(source.get("idTwitter").toString());
			int notAdded = 0;
			boolean isShowingFollowers = source.getBoolean("isShowingFollowers");
			
			for(User user : followers)
			{
				User u = gManager.getUser(user.getId());					
				
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
				controller.setStatusBarMessage("Adicionados "+(followers.size()-notAdded)+" seguidores de "+source.getString("name") +" à rede. "+notAdded+" já existentes.");
			else
				controller.setStatusBarMessage("Adicionados "+notAdded+" seguidores de "+source.getString("name") +" à rede. "+(followers.size()-notAdded)+" já existentes.");
			
			source.setBoolean("isShowingFollowers",true);
			//node count: botar no log
			System.out.println("Node Count: "+gManager.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
		}
	}
}
