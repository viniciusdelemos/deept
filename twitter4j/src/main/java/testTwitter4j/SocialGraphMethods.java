package testTwitter4j;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SocialGraphMethods {
	
	private String user;
	private String pass;
	
	public SocialGraphMethods(String user, String pass){
		this.user = user;
		this.pass = pass;
	}
	
	public void getFriendsIds(String id){
		
		Twitter twitter = new Twitter(user, pass);
		
		List<String> ids = null;
		
		try {
			ids = twitter.getFriendsIds(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("Friends of "+id);
		for(String str : ids){
			System.out.println(str);
		}
	}
	
	public void getFollowersIds(String id){
		
		Twitter twitter = new Twitter(user, pass);
		
		List<String> ids = null;
		
		try {
			ids = twitter.getFollowersIds(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("Followers of "+id);
		for(String str : ids){
			System.out.println(str);
		}
	}

}