package model.threads;

import gui.visualizations.GraphicManager;

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
		GraphicManager gManager;
		ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public AddFriendsThread(GraphicManager gManager, VisualItem s) {
		this.gManager = gManager;		
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
					gManager.addEdge((Node)source.getSourceTuple(), n); 		
				}
				else if(!isShowingFriends)
				{
					Node n = gManager.getNodeByTwitterId(u.getId());
					gManager.addEdge((Node)source.getSourceTuple(), n);
					notAdded++;
				}
			}
			if(!isShowingFriends)
				controller.setStatusBarMessage("Adicionados "+(friends.size()-notAdded)+" amigos de "+source.getString("name") +" � rede. "+notAdded+" j� existentes.");
			else
				controller.setStatusBarMessage("Adicionados "+notAdded+" amigos de "+source.getString("name") +" � rede. "+(friends.size()-notAdded)+" j� existentes.");
			
			source.setBoolean("isShowingFriends",true);
			//node count: botar no log
			System.out.println("Node Count: "+gManager.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
			e.printStackTrace();
		} catch(Exception ex) {
			System.out.println("%%%%EXCEPTION%%%%\n");
			ex.printStackTrace();
		}
	}
}
