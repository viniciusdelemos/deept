package testTwitter4j;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class AccountMethods {
	
	private String user;
	private String password;
	
	
	public AccountMethods(String user, String password){
		this.user = user;
		this.password = password;
	}
	
	public void verifyCredentials()
	{
		Twitter twitter = new Twitter(user, password);
		boolean bool = false;
		try {
			bool = twitter.verifyCredentials();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("Verify credentials\n");
		System.out.println(bool);
		
	}
	
	//
	// ARCHIVE ARCHIVE ARCHIVE ARCHIVE
	//
	// ARCHIVE ARCHIVE ARCHIVE ARCHIVE
	//
	// ARCHIVE ARCHIVE ARCHIVE ARCHIVE
	//
	
	public void updateLocation(String location)
	{
		Twitter twitter = new Twitter(user, password);
		User user = null;
			
		try {
			user = twitter.updateLocation(location);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("Update location "+location+"\n");
		
		printUser(user);
		
		
	}

	
	
	
	public void updateDeliverlyDevice(String device)
	{
		Twitter twitter = new Twitter(user, password);
		User user = null;
			
		
		try {
			if(device.equals("im")){
				user = twitter.updateDeliverlyDevice(Twitter.IM);
			}
			else if(device.equals("sms")){
				user = twitter.updateDeliverlyDevice(Twitter.SMS);
			}
			else if(device.equals("none")){
				user = twitter.updateDeliverlyDevice(Twitter.NONE);
			}
			else{
				System.err.println("Problemas aki");
			}
			
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		
		System.out.print("\n\n\n");
		System.out.println("Update delivery device by string "+device+"\n");
		printUser(user);
	}
	
	public void rateLimitStatus(){
		
		Twitter twitter = new Twitter(user, password);
		RateLimitStatus rateLimitStatus = null;
		
		try {
			rateLimitStatus = twitter.rateLimitStatus();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("Hourly Limit >"+rateLimitStatus.getHourlyLimit());
		System.out.println("Remaining Hits >"+rateLimitStatus.getRemainingHits());
		System.out.println("Reset time in seconds >"+rateLimitStatus.getResetTimeInSeconds());
		System.out.println("Date time >"+rateLimitStatus.getDateTime());
		
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
