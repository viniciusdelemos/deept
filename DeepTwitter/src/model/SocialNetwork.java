package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.twitter4j.UserDeepT;

public class SocialNetwork {   
    private Map<Integer,UserDeepT> usersMap;
    private List<UsersGroup> listUsersGroup;
    private int groupId;
    
    public SocialNetwork() {        
        usersMap = new HashMap<Integer, UserDeepT>();     
        listUsersGroup = new ArrayList<UsersGroup>();
        groupId = 0;
    }
    
	public UserDeepT getUser(int twitterId) {
		return usersMap.get(twitterId);
	}
		
	public void addUser(UserDeepT u)
	{
		//UserDeepT userDeepT = new UserDeepT(u);
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