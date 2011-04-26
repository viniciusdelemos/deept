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
			
			//(ATUALIZA��O)
			//mudan�a de getFriends() para getFriendsStatuses()
			
			//lista que muda dependendo do 'cursor' definido
			//retorna no m�ximo 100 amigos
			//PagableResponseList<User> friendsWithPaging = controller.getTwitter().getFriendsStatuses(Long.parseLong(source.get("idTwitter").toString()), -1);
			
			//lista onde s�o armazenados todos os amigos
			//List<User> friends = friendsWithPaging;
			List<User> friends =  controller.getTwitter().getFriendsStatuses(Long.parseLong(source.get("idTwitter").toString()), -1);
			
			//while(friendsWithPaging.getNextCursor() != 0)
			//{
				//friendsWithPaging = controller.getTwitter().getFriendsStatuses(Long.parseLong(source.get("idTwitter").toString()), friendsWithPaging.getNextCursor());
				//friends.addAll(friendsWithPaging);
			//}
			
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
				controller.setStatusBarMessage("Adicionados "+(friends.size()-notAdded)+" amigos de "+source.getString("name") +" � rede. "+notAdded+" j� existentes.",MessageType.INFORMATION);
			else
				controller.setStatusBarMessage("Adicionados "+notAdded+" amigos de "+source.getString("name") +" � rede. "+(friends.size()-notAdded)+" j� existentes.",MessageType.INFORMATION);
			
			source.setBoolean("isShowingFriends",true);
			source.setBoolean("isOpen", true);
			//node count: botar no log
			System.out.println("Node Count: "+networkView.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			
			//N�o � poss�vel ver os amigos pois o usu�rio tem os tweets protegidos
			if(e.getExceptionCode().equals("85f8b96c-247d6c8e"))
			{
				controller.showMessageDialog("Este usu�rio tem seu perfil protegido.",MessageType.ERROR);
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
