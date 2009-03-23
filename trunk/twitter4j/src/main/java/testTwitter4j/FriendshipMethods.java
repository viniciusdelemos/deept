package testTwitter4j;

import twitter4j.Twitter;
import twitter4j.User;

public class FriendshipMethods {
	
	private String user;
	private String password;
	
	public FriendshipMethods(String user, String password)
	{
		this.user = user;
		this.password = password;
	}
	
	
	public void create(String id)
	{
		Twitter twitter = new Twitter(user,password);
		User user = null;
		
		try {
			user = twitter.create(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("Create friendship by id "+id+"\n");
		
		printUser(user);	
	}
	
	public void destroy(String id)
	{
		Twitter twitter = new Twitter(user,password);
		User user = null;
		
		try {
			user = twitter.destroy(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("Destroy friendship by id "+id+"\n");
		
		printUser(user);		
	}
	
	public void existsFriendship(String user_a, String user_b)
	{
		Twitter twitter = new Twitter(user,password);
		boolean bool;
		
		try {
			bool = twitter.exists(user_a, user_b);
		} catch (Exception e) {
			System.out.print("\n\n\n");
			System.out.println("Exists friendship user_a "+user_a+" user_b"+user_b+"\n");
			e.printStackTrace();
			return;
		}
		
		System.out.print("\n\n\n");
		System.out.println("Exists friendship user_a "+user_a+" user_b"+user_b+"\n");
		System.out.println(bool);
	}
	
	
	private void printUser(User user)
	{
		if(user==null)
			System.out.println("Problems");
		else
		{
			System.out.println("\t\t id > "+user.getId());
			System.out.println("\t\t name > "+user.getName());
			System.out.println("\t\t screen_name > "+user.getScreenName());
			System.out.println("\t\t location > "+user.getLocation());
			System.out.println("\t\t description > "+user.getDescription());
			System.out.println("\t\t profile_image_url > "+user.getProfileImageURL());
			System.out.println("\t\t url > "+user.getURL());
			System.out.println("\t\t protected > "+user.isProtected());
			System.out.println("\t\t followers_count > "+user.getFollowersCount());
			

		}

	
	}


}
