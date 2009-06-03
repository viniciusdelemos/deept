package controller;

import gui.visualizations.MostActiveUsers;

import java.util.List;

import javax.swing.JFrame;

import twitter4j.User;

public class MostActiveUsersController {
	
	private ControllerDeepTwitter controller;
	private List<User> users;
	
	public MostActiveUsersController(){
		controller = ControllerDeepTwitter.getInstance();
	}
	
	public static class SingletonHolder{
		private final static MostActiveUsersController INSTANCE = new MostActiveUsersController();
	}
	
	public static MostActiveUsersController getInstance(){
		return SingletonHolder.INSTANCE;
	}
	
	private MostActiveUsers mostActiveUsers = null;
	
	
	
	private JFrame frame;
	
	public void setUsers(List<User> users){

		mostActiveUsers = new MostActiveUsers();
		
		mostActiveUsers.openFrame();
		
		for(User u: users){
			mostActiveUsers.addNode(u);
		}
		
		mostActiveUsers.calculateSizes();
		
	}

}
