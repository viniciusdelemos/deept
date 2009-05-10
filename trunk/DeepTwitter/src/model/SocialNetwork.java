package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.twitter4j.TwitterResponseDeepT;
import model.twitter4j.UserDeepT;

public class SocialNetwork {   
    private Map<Integer,TwitterResponseDeepT> usersMap;
    private List<UsersGroup> listUsersGroup;
    private int groupId;
    
    public SocialNetwork() {        
        usersMap = new HashMap<Integer, TwitterResponseDeepT>();     
        listUsersGroup = new ArrayList<UsersGroup>();
        groupId = 0;
    }
    
	public TwitterResponseDeepT getUser(int twitterId) {
		return usersMap.get(twitterId);
	}
		
	public void addUser(TwitterResponseDeepT u)
	{
		//UserDeepT userDeepT = new UserDeepT(u);
		usersMap.put(u.getUserDeepT().getId(), u);
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