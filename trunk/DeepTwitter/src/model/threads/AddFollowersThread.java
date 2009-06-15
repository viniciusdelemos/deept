package model.threads;

import gui.visualizations.NetworkView;

import java.util.List;

import model.MessageType;
import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import twitter4j.TwitterException;
import twitter4j.User;
import controller.ControllerDeepTwitter;

public class AddFollowersThread extends Thread {
		VisualItem source;	
		NetworkView networkView;
		ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public AddFollowersThread(NetworkView networkView, VisualItem s) {
		this.networkView = networkView;		
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
				User u = networkView.getUser(user.getId());					
				
				if(u == null)
				{	
					double x = source.getX();					
					double y = source.getY();
					
					Node n = networkView.addNode(user);
					VisualItem newNode = networkView.getVisualization().getVisualItem(networkView.NODES, n);
					PrefuseLib.setX(newNode, null, x);
					PrefuseLib.setY(newNode, null, y);
					
					//AO TER UM VISUALITEM, USAR VisualItem.getSourceTuple() para pegar instancia de Nodo ou Edge.					
					networkView.addEdge(n,(Node)source.getSourceTuple()); 		
				}
				else if(!isShowingFollowers)
				{
					Node n = networkView.getNodeByTwitterId(u.getId());
					networkView.addEdge(n,(Node)source.getSourceTuple());
					notAdded++;
				}
			}
			if(!isShowingFollowers)
				controller.setStatusBarMessage("Adicionados "+(followers.size()-notAdded)+" seguidores de "+source.getString("name") +" à rede. "+notAdded+" já existentes.");
			else
				controller.setStatusBarMessage("Adicionados "+notAdded+" seguidores de "+source.getString("name") +" à rede. "+(followers.size()-notAdded)+" já existentes.");
			
			source.setBoolean("isShowingFollowers",true);
			//node count: botar no log
			System.out.println("Node Count: "+networkView.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
		}
	}
}
