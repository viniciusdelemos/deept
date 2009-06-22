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

public class AddFriendsThread extends Thread {
		VisualItem source;	
		NetworkView networkView;
		ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public AddFriendsThread(NetworkView networkView, VisualItem s) {
		this.networkView = networkView;		
		this.source = s;
		this.start();
	}
	
	public void run()
	{
		try {
			System.out.println("GETTING FRIENDS FOR "+source.get("name"));
			List<User> friends = controller.getTwitter().getFriends(source.get("idTwitter").toString());
			System.out.println("GOT FRIENDS FOR "+source.get("name"));

			int notAdded = 0;
			boolean isShowingFriends = source.getBoolean("isShowingFriends");
			
			for(User user : friends)
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
					networkView.addEdge((Node)source.getSourceTuple(), n); 		
				}
				else if(!isShowingFriends)
				{
					Node n = networkView.getNodeByTwitterId(u.getId());
					networkView.addEdge((Node)source.getSourceTuple(), n);
					notAdded++;
				}
			}
			if(!isShowingFriends)
				controller.setStatusBarMessage("Adicionados "+(friends.size()-notAdded)+" amigos de "+source.getString("name") +" à rede. "+notAdded+" já existentes.");
			else
				controller.setStatusBarMessage("Adicionados "+notAdded+" amigos de "+source.getString("name") +" à rede. "+(friends.size()-notAdded)+" já existentes.");
			
			source.setBoolean("isShowingFriends",true);
			source.setBoolean("isOpen", true);
			//node count: botar no log
			System.out.println("Node Count: "+networkView.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
			e.printStackTrace();
		} catch(Exception ex) {
			System.out.println("%%%%EXCEPTION%%%%\n");
			ex.printStackTrace();
		}
	}
}
