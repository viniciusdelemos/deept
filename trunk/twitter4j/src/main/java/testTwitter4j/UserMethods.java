package testTwitter4j;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserWithStatus;

public class UserMethods {
	
	private String user;
	private String pass;
	
	public UserMethods(String user, String pass)
	{
		this.user = user;
		this.pass = pass;
	}
	
	/** Friends **/
	
	public void getFriends()
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFriends();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("friends\n");
		
		printUsers(users);

	}
	
	public void getFriends(int page)
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFriends(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("friends By page "+page+"\n");
		
		printUsers(users);
	}
	
	public void getFriends(String id)
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFriends(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("friends By ID "+id+"\n");
		
		printUsers(users);
		
	}
	
	
	public void getFriends(String id, int page)
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFriends(id, page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("friends By ID "+id+" and page "+page+"\n");
		
		printUsers(users);
		
	}
	
	/** Followers **/
	
	public void getFollowers()
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFollowers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("followers\n");
		
		printUsers(users);
		
	}
	
	
	public void getFollowers(int page)
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFollowers(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("followers by page "+page+"\n");
		
		printUsers(users);
		
	}
	
	
	public void getFollowers(String id)
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFollowers(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("followers by ID "+id+"\n");
		
		printUsers(users);
		
	}
	
	public void getFollowers(String id, int page)
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFollowers(id,page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("followers by ID "+id+" and page "+page+"\n");
		
		printUsers(users);
	}
	
	/*
	public void getFeatured()
	{
		Twitter twitter = new Twitter(user, pass);
		List<User> users = null;
		
		try {
			users = twitter.getFeatured();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("featured\n");
		
		printUsers(users);
		
	}
	*/
	
	
	public void getUserDetail(String id)
	{
		Twitter twitter = new Twitter(user, pass);
		UserWithStatus user = null;
		
		try {
			user = twitter.getUserDetail(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("user detail\n");
		
		printUserDetail(user);
		
		
		
	}
	
	/*
	public void getUserDetailByEmail(String email)
	{
		Twitter twitter = new Twitter(user, pass);
		UserWithStatus user = null;
		
		try {
			user = twitter.getUserDetailByEmail(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("user detail by email\n");
		
		printUserDetail(user);
	}
	*/

	
	
	private void printUserDetail(UserWithStatus user)
	{
		System.out.println("\t id > "+user.getId());
		System.out.println("\t name > "+user.getName());
		System.out.println("\t screen_name > "+user.getScreenName());
		System.out.println("\t location > "+user.getLocation());
		System.out.println("\t description > "+user.getDescription());
		System.out.println("\t profile_image_url > "+user.getProfileImageURL());
		System.out.println("\t url > "+user.getURL());
		System.out.println("\t protected > "+user.isProtected());
		System.out.println("\t followers_count > "+user.getFollowersCount());
		
		System.out.println("\n\t profile_background_color > "+user.getProfileBackgroundColor());
		System.out.println("\t profile_text_color > "+user.getProfileTextColor());
		System.out.println("\t profile_link_color > "+user.getProfileLinkColor());
		System.out.println("\t profile_sidebar_fill_color > "+user.getProfileSidebarFillColor());
		System.out.println("\t profile_sidebar_border_color > "+user.getProfileSidebarBorderColor());
		System.out.println("\t friends_count > "+user.getFriendsCount());
		//System.out.println("\t created_at > "+user.);
		System.out.println("\t favourites_count > "+user.getFavouritesCount());
		//System.out.println("\t utc_offset > "+user.);
		//System.out.println("\t time_zone > "+user.);
		//System.out.println("\t following > "+user.getId());
		//System.out.println("\t notifications > "+user.getId());
		System.out.println("\t statuses_count > "+user.getStatusesCount());
		
		System.out.println("\t\t status");
		System.out.println("\t\t created_at > "+user.getStatusCreatedAt());
		System.out.println("\t\t id > "+user.getStatusId());
		System.out.println("\t\t text > "+user.getStatusText());
		//System.out.println("\t\t source > "+user.gets);
		//System.out.println("\t\t truncated > "+user.getId());
		//System.out.println("\t\t in_reply_to_status > "+user.getId());
		//System.out.println("\t\t in)reply_to_user_id > "+user.getId());
	}
	
	
	
	
	
	private void printUsers(List<User> users)
	{
		int cont=1;
		for (User user : users)
		{
			System.out.println(cont+++"-------------------------");
			
			System.out.println("\t id > "+user.getId());
			System.out.println("\t name > "+user.getName());
			System.out.println("\t screen_name > "+user.getScreenName());
			System.out.println("\t location > "+user.getLocation());
			System.out.println("\t description > "+user.getDescription());
			System.out.println("\t profile_image_url > "+user.getProfileImageURL());
			System.out.println("\t url > "+user.getURL());
			System.out.println("\t protected > "+user.isProtected());
			System.out.println("\t followers_count > "+user.getFollowersCount());
			
			
		}
		
	}
	

}
