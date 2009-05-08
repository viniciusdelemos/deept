package testTwitter4j;

import java.util.Date;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.User;

public class DirectMessageMethods {
	
	private String user;
	private String password;
	
	
	public DirectMessageMethods(String user, String password)
	{
		this.user = user;
		this.password = password;
	}
	
	
	public void getDirectMessages()
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getDirectMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get direct messages\n");
		
		printDirectMessages(directMessages);
		
	}
	
	public void getDirectMessagesByPage(int page)
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getDirectMessagesByPage(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get direct messages by page "+page+"\n");
		
		printDirectMessages(directMessages);
		
	}
	
	/*
	public void getDirectMessages(long sinceId)
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getDirectMessages(sinceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get direct messages by sinceid "+sinceId+"\n");
		
		printDirectMessages(directMessages);
		
	}
	
	public void getDirectMessages(Date since)
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getDirectMessages(since);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get direct messages by date "+since+"\n");
		
		printDirectMessages(directMessages);
	}
	
	public void getSentDirectMessages()
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getSentDirectMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get send direct messages\n");
		
		printDirectMessages(directMessages);
	}
	
	public void getSentDirectMessages(Date since)
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getSentDirectMessages(since);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get send direct messages by date "+since+"\n");
		
		printDirectMessages(directMessages);
	}
	
	
	public void getSentDirectMessagesByPage(int page)
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getSentDirectMessagesByPage(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get send direct messages by page "+page+"\n");
		
		printDirectMessages(directMessages);
		
	}
	
	*/
	
	public void getSentDirectMessages(int sinceId)
	{
		Twitter twitter = new Twitter(user,password);
		List<DirectMessage> directMessages = null;
		
		try {
			directMessages = twitter.getSentDirectMessages(sinceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("get send direct messages by sinceId "+sinceId+"\n");
		
		printDirectMessages(directMessages);
	}
	
	
	
	public void sendDirectMessage(String id, String text)
	{
		Twitter twitter = new Twitter(user,password);
		DirectMessage directMessage = null;
		
		try {
			directMessage = twitter.sendDirectMessage(id, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("send direct messages by id "+id+" and text "+text+"\n");
		
		printDirectMessage(directMessage);
	}
	
	public void deleteDirectMessage(int id)
	{
		Twitter twitter = new Twitter(user,password);
		DirectMessage directMessage = null;
		
		try {
			directMessage = twitter.deleteDirectMessage(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\n\n");
		System.out.println("Delete direct messages by id "+id+"\n");
		
		printDirectMessage(directMessage);	
	}
	
	
	
	
	
	
	
	private void printDirectMessage(DirectMessage dir)
	{
		if(dir==null)
			System.out.println("Problems");
		else
		{

		
		System.out.println("direct_message");
		System.out.println("\t id > "+ dir.getId());
		System.out.println("\t sender_id > "+ dir.getSenderId());
		System.out.println("\t text > "+ dir.getText());
		System.out.println("\t recipient_id > "+ dir.getRecipientId());
		System.out.println("\t created_at > "+ dir.getCreatedAt());
		System.out.println("\t sender_screen_name > "+ dir.getSenderScreenName());
		System.out.println("\t recipient_screen_name > "+ dir.getRecipientScreenName());
		
		
		
		User user = dir.getSender();
		
		System.out.println("\tsender");
		System.out.println("\t\t id > "+user.getId());
		System.out.println("\t\t name > "+user.getName());
		System.out.println("\t\t screen_name > "+user.getScreenName());
		System.out.println("\t\t location > "+user.getLocation());
		System.out.println("\t\t description > "+user.getDescription());
		System.out.println("\t\t profile_image_url > "+user.getProfileImageURL());
		System.out.println("\t\t url > "+user.getURL());
		System.out.println("\t\t protected > "+user.isProtected());
		System.out.println("\t\t followers_count > "+user.getFollowersCount());
		
		user = dir.getRecipient();
		System.out.println("\trecipient");
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
	
	
	
	
	
	
	
	
	private void printDirectMessages(List<DirectMessage> directMes)
	{
		int cont=1;
		for (DirectMessage dir : directMes) {
			
			System.out.println(cont+++"-------------------------");
			
			System.out.println("direct_message");
			System.out.println("\t id > "+ dir.getId());
			System.out.println("\t sender_id > "+ dir.getSenderId());
			System.out.println("\t text > "+ dir.getText());
			System.out.println("\t recipient_id > "+ dir.getRecipientId());
			System.out.println("\t created_at > "+ dir.getCreatedAt());
			System.out.println("\t sender_screen_name > "+ dir.getSenderScreenName());
			System.out.println("\t recipient_screen_name > "+ dir.getRecipientScreenName());
			
			
			
			User user = dir.getSender();
			
			System.out.println("\tsender");
			System.out.println("\t\t id > "+user.getId());
			System.out.println("\t\t name > "+user.getName());
			System.out.println("\t\t screen_name > "+user.getScreenName());
			System.out.println("\t\t location > "+user.getLocation());
			System.out.println("\t\t description > "+user.getDescription());
			System.out.println("\t\t profile_image_url > "+user.getProfileImageURL());
			System.out.println("\t\t url > "+user.getURL());
			System.out.println("\t\t protected > "+user.isProtected());
			System.out.println("\t\t followers_count > "+user.getFollowersCount());
			
			user = dir.getRecipient();
			System.out.println("\trecipient");
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
