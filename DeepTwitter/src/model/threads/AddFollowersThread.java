package model.threads;

import gui.visualizations.NetworkView;

import java.util.List;

import model.MessageType;
import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import twitter4j.PagableResponseList;
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
			
			//(ATUALIZAÇÃO)
			//mundança de getFollowers() para getFollowersStatuses(string, long cursor);
			
			PagableResponseList<User> followers = controller.getTwitter().getFollowersStatuses(Long.parseLong(source.get("idTwitter").toString()), Long.parseLong(source.get("followersNextCursor").toString()));
			if(followers.getNextCursor() != 0)
			{
				source.set("followersNextCursor", followers.getNextCursor());
			}
			
			System.out.println("GOT FOLLOWERS FOR "+source.get("name"));
			
			int notAdded = 0;
			boolean isShowingFollowers = source.getBoolean("isShowingFollowers");
			
			for(User user : followers)
			{
				//(ATUALIZAÇÃO)
				//mundança na classe NetworkView
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
			int addedFollowers = Integer.parseInt(source.get("addedFollowers").toString());
			if(!isShowingFollowers)
			{
				controller.setStatusBarMessage("Adicionados "+(followers.size()-notAdded)+" seguidores de "+source.getString("name") +" à rede. "+notAdded+" já existentes.",MessageType.INFORMATION);
				source.set("addedFollowers", addedFollowers + followers.size());
			}
			else
			{
				//se adicionou todos os followers
				if(addedFollowers == Integer.parseInt(source.get("followersCount").toString()))
				{
					controller.setStatusBarMessage("Todos seguidores de " + source.getString("name") + " já foram adicionados à rede.", MessageType.INFORMATION);
				}
				else
				{	
					controller.setStatusBarMessage("Adicionados mais "+(followers.size()-notAdded)+" seguidores de "+source.getString("name") +" à rede. " + addedFollowers + " já existentes.",MessageType.INFORMATION);
					source.set("addedFollowers", addedFollowers + (followers.size()-notAdded));
				}
			}
			
			source.setBoolean("isShowingFollowers",true);
			//node count: botar no log
			System.out.println("Node Count: "+networkView.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			
			//Não é possível ver os followers pois o usuário tem os tweets protegidos
			if(e.getExceptionCode().equals("d5cc896c-2604055b"))
			{				
				System.out.println(source.getString("name"));
				controller.showMessageDialog("Este usuário tem seu perfil protegido.",MessageType.ERROR);
			}
			else
			{
				System.out.println(e.getExceptionCode());
				controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
				System.out.println("%%%%TWITTER EXCEPTION%%%%\n");
				e.printStackTrace();
			}
		}
	}
}
