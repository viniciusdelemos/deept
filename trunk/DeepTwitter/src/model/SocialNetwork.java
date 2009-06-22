package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.GroupManager;

import twitter4j.User;

public class SocialNetwork {   
    private Map<Integer,User> usersMap;
    private Set<Integer> blocksSet; 
    private List<GroupManager> listUsersGroup;
    private int groupId;
    
    public SocialNetwork() {        
        usersMap = new HashMap<Integer, User>();     
        listUsersGroup = new ArrayList<GroupManager>();
        blocksSet = new HashSet<Integer>();
        groupId = 0;
    }
    
	public User getUser(int twitterId) {
		return usersMap.get(twitterId);
	}
	
	public void addBlockedUsers(int[] ids) {
		for(int i : ids)
			blocksSet.add(i);
	}
	
	public void removeBlockedUser(int id) {
		blocksSet.remove(id);
	}
	
	public boolean isUserBlocked(int id) {
		return blocksSet.contains(id);
	}
			
	public void addUser(User u) {
		usersMap.put(u.getId(), u);
	}
	
	public void removeUser(int id) {
		usersMap.remove(id);
	}
	
	public int getNumUsers() {
		return usersMap.size();
	}
	
	public User[] getUsers() {
		return usersMap.values().toArray(new User[0]);
	}
}