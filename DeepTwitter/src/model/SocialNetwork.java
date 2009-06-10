package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.User;

public class SocialNetwork {   
    private Map<Integer,User> usersMap;
    private List<GroupManager> listUsersGroup;
    private int groupId;
    
    public SocialNetwork() {        
        usersMap = new HashMap<Integer, User>();     
        listUsersGroup = new ArrayList<GroupManager>();
        groupId = 0;
    }
    
	public User getUser(int twitterId) {
		return usersMap.get(twitterId);
	}
	
//	public String getUserNameByScreenName(String screenName){
//		
//		Iterator<User> users = usersMap.values().iterator();
//		
//		while(users.hasNext()){
//			User u = users.next();
//			if(u.getScreenName().equals(screenName)){
//				return u.getName();
//			}
//		}
//		
//		return null;
//		
//	}
	
			
	public void addUser(User u) {
		usersMap.put(u.getId(), u);
	}
	
	public int getNumUsers() {
		return usersMap.size();
	}
	
	public User[] getUsers() {
		return usersMap.values().toArray(new User[0]);
	}
}