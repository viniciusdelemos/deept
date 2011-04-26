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
	
	//(ATUALIZA��O)
	//usersMap de Integer para Long	
    private Map<Long,User> usersMap;
    
    //(ATUALIZA��O)
    //blocksSet para long
    private Set<Long> blocksSet; 
    
    private List<GroupManager> listUsersGroup;
    private int groupId;
    
    public SocialNetwork() {        
        usersMap = new HashMap<Long, User>();     
        listUsersGroup = new ArrayList<GroupManager>();
        blocksSet = new HashSet<Long>();
        groupId = 0;
    }
    
	public User getUser(long twitterId) {
		return usersMap.get(twitterId);
	}
	
	//(ATUALIZA��O)
	//de int[] para long[]
	public void addBlockedUsers(long[] ids)
	{
		for(long i : ids)
			blocksSet.add(i);
	}
	
	//(ATUALIZA��O)
	//de int para long
	public void removeBlockedUser(long id) {
		blocksSet.remove(id);
	}
	
	//(ATUALIZA��O)
	//de int para long
	public boolean isUserBlocked(long id) {
		return blocksSet.contains(id);
	}
			
	public void addUser(User u) {
		usersMap.put(u.getId(), u);
	}
	
	//(ATUALIZA��O)
	//de int para long
	public void removeUser(long id) {
		usersMap.remove(id);
	}
	
	public int getNumUsers() {
		return usersMap.size();
	}
	
	public User[] getUsers() {
		return usersMap.values().toArray(new User[0]);
	}
}