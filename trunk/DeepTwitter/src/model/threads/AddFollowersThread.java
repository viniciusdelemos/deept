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
			
			//(ATUALIZA��O)
			//mundan�a de getFollowers() para getFollowersStatuses(string, long cursor);
			
			//lista que muda dependendo do 'cursor' definido
			//retorna no m�ximo 100 followers
			//PagableResponseList<User> followersWithPaging = controller.getTwitter().getFollowersStatuses(Long.parseLong(source.get("idTwitter").toString()), -1);
			
			//lista onde s�o armazenados todos os followers
			List<User> followers = controller.getTwitter().getFollowersStatuses(Long.parseLong(source.get("idTwitter").toString()), -1);
			
			//while(followersWithPaging.getNextCursor() != 0)
			//{
				//retorna mais 100 followers
				//followersWithPaging = controller.getTwitter().getFollowersStatuses(Long.parseLong(source.get("idTwitter").toString()), followersWithPaging.getNextCursor());
				
				//adiciona esses novos followers a lista com todos os followers
				//followers.addAll(followersWithPaging);
			//}
			
			System.out.println("GOT FOLLOWERS FOR "+source.get("name"));
			
			int notAdded = 0;
			boolean isShowingFollowers = source.getBoolean("isShowingFollowers");
			
			for(User user : followers)
			{
				//(ATUALIZA��O)
				//mundan�a na classe NetworkView
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
				controller.setStatusBarMessage("Adicionados "+(followers.size()-notAdded)+" seguidores de "+source.getString("name") +" � rede. "+notAdded+" j� existentes.",MessageType.INFORMATION);
			else
				controller.setStatusBarMessage("Adicionados "+notAdded+" seguidores de "+source.getString("name") +" � rede. "+(followers.size()-notAdded)+" j� existentes.",MessageType.INFORMATION);
			
			source.setBoolean("isShowingFollowers",true);
			//node count: botar no log
			System.out.println("Node Count: "+networkView.getGraph().getNodeCount());
			
		} catch (TwitterException e) {
			
			//N�o � poss�vel ver os followers pois o usu�rio tem os tweets protegidos
			if(e.getExceptionCode().equals("d5cc896c-26006263"))
			{				
				controller.showMessageDialog("Este usu�rio tem seu perfil protegido.",MessageType.ERROR);
			}
			else
			{
				controller.showMessageDialog(e.getMessage(),MessageType.ERROR);
				System.out.println("%%%%TWITTER EXCEPTION%%%%\n");
				e.printStackTrace();
			}
		}
	}
}
