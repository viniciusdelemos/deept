package testTwitter4j;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class BlockMethods {
	
	private String user;
	private String password;
	
	public BlockMethods(String user, String password)
	{
		this.user = user;
		this.password = password;
	}
	
	public void block(String id)
	{
		Twitter twitter = new Twitter(user,password);
		User user = null;
		
		try {
			user = twitter.block(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("block user by id "+id+"\n");
		
		printUser(user);
	}
	
	public void unblock(String id)
	{
		Twitter twitter = new Twitter(user,password);
		User user = null;
		
		try {
			user = twitter.unblock(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("unblock user by id "+id+"\n");
		
		printUser(user);
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
