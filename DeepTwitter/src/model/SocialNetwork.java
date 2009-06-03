package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import twitter4j.User;
import twitter4j.User.TypeComparable;

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
	
			
	public void addUser(User u)
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
	
	/**
	 * Retorna os usuarios mais ativos para cada tipo de contador,
	 * followers, friends, statuses, favourites
	 * @return List<User> usuarios mais ativos
	 */
	public List<User> mostActiveUserForAllNetwork(){
		
		int mostByType = 15;
		
		List<User> users = new ArrayList<User>();
		List<User> mostActiveUsers = new ArrayList<User>();
		
		Iterator<User> usersIt = usersMap.values().iterator();
		while(usersIt.hasNext()){
			User u = usersIt.next();
			users.add(u);
		}
		
		int stop = (users.size() - 1) - mostByType;
		
		//10 most active by followers
		for(User u : users)
			u.setTypeComparable(TypeComparable.followers);
		
		
		Collections.sort(users);
		for(int i = users.size()-1; i >= stop; i--){
			if(mostActiveUsers.contains(users.get(i)) ==  false)
				mostActiveUsers.add(users.get(i));
		}
		
		//10 mostActive by friends
		for(User u : users)
			u.setTypeComparable(TypeComparable.friends);
		
		Collections.sort(users);
		for(int i = users.size()-1; i >= stop; i--){
			if(mostActiveUsers.contains(users.get(i)) ==  false)
				mostActiveUsers.add(users.get(i));
		}
		
		//10 most active by favourites
		for(User u : users)
			u.setTypeComparable(TypeComparable.favourites);
		
		Collections.sort(users);
		for(int i = users.size()-1; i >= stop; i--){
			if(mostActiveUsers.contains(users.get(i)) ==  false)
				mostActiveUsers.add(users.get(i));
		}
		
		//10 most active by statuses
		for(User u : users)
			u.setTypeComparable(TypeComparable.tweets);
		
		Collections.sort(users);
		for(int i = users.size()-1; i >= stop; i--){
			if(mostActiveUsers.contains(users.get(i)) ==  false)
				mostActiveUsers.add(users.get(i));
		}

		return mostActiveUsers;
		
	}
}