package testTwitter4j;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class FavoriteMethods {
	
	private String user;
	private String password;
	
	public FavoriteMethods(String user, String password)
	{
		this.user = user;
		this.password = password;
	}
	
	public void favorites()
	{
		Twitter twitter = new Twitter(user,password);
		List<Status> status = null;
		
		try {
			status = twitter.favorites();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("favorites\n");
		
		printStatus(status);
		
	}
	
	public void favorites(int page)
	{
		Twitter twitter = new Twitter(user,password);
		List<Status> status = null;
		
		try {
			status = twitter.favorites(page);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("favorites by page "+page+"\n");
		
		printStatus(status);
	}
	
	public void favorites(String id)
	{
		Twitter twitter = new Twitter(user,password);
		List<Status> status = null;
		
		try {
			status = twitter.favorites(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("favorites by id "+id+"\n");
		
		printStatus(status);
	}
	
	public void favorites(String id, int page)
	{
		Twitter twitter = new Twitter(user,password);
		List<Status> status = null;
		
		try {
			status = twitter.favorites(id,page);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("favorites by id "+id+" and page "+page+"\n");
		
		printStatus(status);
	}
	
	
	public void createFavorite(long id)
	{
		Twitter twitter = new Twitter(user,password);
		Status status = null;
		
		try {
			status = twitter.createFavorite(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("create favorites by id "+id+"\n");
		
		printSimpleStatus(status);
	}
	
	public void destroyFavorite(int id)
	{
		Twitter twitter = new Twitter(user,password);
		Status status = null;
		
		try {
			status = twitter.destroyFavorite(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n");
		System.out.println("destroy favorites by id "+id+"\n");
		
		printSimpleStatus(status);
	}
	
	
	private void printSimpleStatus(Status status)
	{
		System.out.println("status");
		System.out.println("\tcreated_at > "+status.getCreatedAt());
		System.out.println("\tid > "+status.getId());
		System.out.println("\ttext > "+status.getText());
		System.out.println("\tsource > "+status.getSource());
		System.out.println("\ttruncated > "+status.isTruncated());
		System.out.println("\tin_reply_to_status_id > "+status.getInReplyToStatusId());
		System.out.println("\tin_replu_to_user_id > "+status.getInReplyToUserId());
		System.out.println("\tfavorited > "+status.isFavorited());
		
		User user = status.getUser();
		System.out.println("\tuser");
		System.out.println("\t\tid > "+user.getId());
		System.out.println("\t\tname > "+user.getName());
		System.out.println("\t\tscreen_name > "+user.getScreenName());
		System.out.println("\t\tlocation > "+user.getLocation());
		System.out.println("\t\tdescription > "+user.getDescription());
		System.out.println("\t\tprofile_image_url > "+user.getProfileImageURL());
		System.out.println("\t\turl > "+user.getURL());
		System.out.println("\t\tprotected > "+user.isProtected());
		System.out.println("\t\tfollowers_count > "+user.getFollowersCount());
	}
	
	private void printStatus(List<Status> status2)
	{
		int cont=1;
		for (Status status : status2) {
			
			System.out.println(cont+++"----------------------------");
			
			System.out.println("status");
			System.out.println("\tcreated_at > "+status.getCreatedAt());
			System.out.println("\tid > "+status.getId());
			System.out.println("\ttext > "+status.getText());
			System.out.println("\tsource > "+status.getSource());
			System.out.println("\ttruncated > "+status.isTruncated());
			System.out.println("\tin_reply_to_status_id > "+status.getInReplyToStatusId());
			System.out.println("\tin_replu_to_user_id > "+status.getInReplyToUserId());
			System.out.println("\tfavorited > "+status.isFavorited());
			
			User user = status.getUser();
			System.out.println("\tuser");
			System.out.println("\t\tid > "+user.getId());
			System.out.println("\t\tname > "+user.getName());
			System.out.println("\t\tscreen_name > "+user.getScreenName());
			System.out.println("\t\tlocation > "+user.getLocation());
			System.out.println("\t\tdescription > "+user.getDescription());
			System.out.println("\t\tprofile_image_url > "+user.getProfileImageURL());
			System.out.println("\t\turl > "+user.getURL());
			System.out.println("\t\tprotected > "+user.isProtected());
			System.out.println("\t\tfollowers_count > "+user.getFollowersCount());
			
		}
		
	}


}
