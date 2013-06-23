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
			
			//(ATUALIZAÇÃO)
			//mudança de getFriends() para getFriendsStatuses()
			
			//PagableResponseList<User> friends =  controller.getTwitter().getFriendsStatuses(Long.parseLong(source.get("idTwitter").toString()), Long.parseLong(source.get("friendsNextCursor").toString()));
			PagableResponseList<User> friends =  controller.getTwitter().getFriendsList(Long.parseLong(source.get("idTwitter").toString()), Long.parseLong(source.get("friendsNextCursor").toString()));
			
			if(friends.getNextCursor() != 0)
			{
				source.set("friendsNextCursor", friends.getNextCursor());
			}
			
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
			
			int addedFriends = Integer.parseInt(source.get("addedFriends").toString());
			
			if(!isShowingFriends)
			{
				controller.setStatusBarMessage("Adicionados "+(friends.size()-notAdded)+" amigos de "+source.getString("name") +" à rede. "+notAdded+" já existentes.",MessageType.INFORMATION);
				source.set("addedFriends", addedFriends + friends.size());
			}
			else
			{	
				//se adicionou todos os amigos
				if(addedFriends == Integer.parseInt(source.get("friendsCount").toString()))
				{
					controller.setStatusBarMessage("Todos amigos de " + source.getString("name") + " já foram adicionados à rede.", MessageType.INFORMATION);
				}
				else
				{	
					controller.setStatusBarMessage("Adicionados mais "+(friends.size()-notAdded)+" amigos de "+source.getString("name") +" à rede. " + addedFriends + " já existentes.",MessageType.INFORMATION);
					source.set("addedFriends", addedFriends + (friends.size()-notAdded));
				}
			}
			
			source.setBoolean("isShowingFriends",true);
			source.setBoolean("isOpen", true);
			//node count: botar no log
			System.out.println("Node Count: "+networkView.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			
			//Não é possível ver os amigos pois o usuário tem os tweets protegidos
			if(e.getExceptionCode().equals("85f8b96c-24810f86"))
			{
				controller.showMessageDialog("Este usuário tem seu perfil protegido.",MessageType.ERROR);
			}
			else
			{
				controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
				System.out.println("%%%%TWITTER EXCEPTION%%%%\n");
				e.printStackTrace();
			}
		} catch(Exception ex) {
			System.out.println("%%%%EXCEPTION%%%%\n");
			ex.printStackTrace();
		}
	}
}
