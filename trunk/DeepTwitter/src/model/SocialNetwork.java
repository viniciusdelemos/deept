package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import twitter4j.*;

public class SocialNetwork {   
    private Map<Integer,User> usersMap;
    private List<UsersGroup> listUsersGroup;
    private int groupId;
    
    public SocialNetwork() {        
        usersMap = new HashMap<Integer, User>();     
        listUsersGroup = new ArrayList<UsersGroup>();
        groupId = 0;
    }
    
	public User getUser(int twitterId) {
		return usersMap.get(twitterId);
	}
		
	public void addUser(User u)
	{
		usersMap.put(u.getId(), u);
	}
	
	public int getNumUsers()
	{
		return usersMap.size();
	}
	
	public void createUsersGroup()
	{
		//UsersGroup ug = new UsersGroup(groupId);
		//listUsersGroup.add(ug);
		//groupId++;
	}
}