package testTwitter4j;

import java.util.Date;
import java.util.List;

import twitter4j.*;

public class Main {
	
	
	static String user1 = "viniciusdelemos"; // leave doido
	static String user = "viniciusdlemos"; //follow doido
	static String user2 = "viniciusedlemos";
	
	
	static String pass = "xyz";
	
	static String doido = "Scobleizer";
	
	public static void main(String args []){
		
		Date date = new Date(105,0,26);
		
		
		StatusMethods statusMethods = new StatusMethods(user1, pass);
		StatusMethods statusMethods1 = new StatusMethods(user, pass);
		StatusMethods statusMethods2 = new StatusMethods(user2, pass);
		
		UserMethods userMethods = new UserMethods(user1, pass);
		UserMethods userMethods1 = new UserMethods(user, pass);
		UserMethods userMethods2 = new UserMethods(user2, pass);
		
		DirectMessageMethods directMessageMethods = new DirectMessageMethods(user1, pass);
		DirectMessageMethods directMessageMethods1 = new DirectMessageMethods(user, pass);
		DirectMessageMethods directMessageMethods2 = new DirectMessageMethods(user2, pass);
		
		FriendshipMethods friendshipMethods = new FriendshipMethods(user1, pass);
		
		SocialGraphMethods socialGraphMethods = new SocialGraphMethods(user1, pass);
		
		AccountMethods accountMethods = new AccountMethods(user1, pass);
		AccountMethods accountMethods1 = new AccountMethods(user, pass);
		AccountMethods accountMethods2 = new AccountMethods(user2, pass);
		
		FavoriteMethods favoriteMethods = new FavoriteMethods(user1, pass);
		FavoriteMethods favoriteMethods1 = new FavoriteMethods(user, pass);
		
		NotificationMethods notificationMethods = new NotificationMethods(user1, pass);
		
		
		BlockMethods blockMethods = new BlockMethods(user1, pass);
		
		//blockMethods.unblock("viniciusdlemos");
		//blockMethods.block("viniciusdlemos");
		
		//statusMethods.getFriendsTimelineBySinceId(1371514306);
		//statusMethods.getFriendsTimelineBySinceId(1371514306);
		//statusMethods.getFriendsTimeline();
		
		//statusMethods.getFriendsTimeline(999);

		

		
		favoriteMethods.destroyFavorite(1413531386);
		
	
		
		
		

		

		
		
		
		
		
		
		
		
		
		
		//notificationMethods.leave("Scobleizer");
		//notificationMethods.leave("temporaryname");
		//notificationMethods.leave("twittervision");
		//notificationMethods.leave("felipeveiga");
		

		

		
		
		

		/*
		System.out.println("\n\n");
		accountMethods.rateLimitStatus();
		System.out.println("\n");
		accountMethods1.rateLimitStatus();
		*/
		
		
		
		
		
		
		
		
		

	
		
		
		/*
		statusMethods.getFriendsTimeline();
		
		System.out.println("Mais uma vez 0");
		
		statusMethods.getFriendsTimeline();
		
		System.out.println("Mais uma vez 1");
		
		statusMethods.getFriendsTimeline();
		
		System.out.println("Mais uma vez 2");
		
		statusMethods.getFriendsTimeline();
		
		*/
		
		
		
		
		
		//UserMethods userMethods = new UserMethods(user, pass);
		
		//DirectMessageMethods directMessageMethods = new DirectMessageMethods(user, pass);
		
		//directMessageMethods.sendDirectMessage(id, "enviando mensagem direta");
		
		
		/*
		Twitter twitter = new Twitter(user1, pass);
		
		try {

			twitter.leave(user);
			/*
			System.out.println(twitter.exists(user1, doido));
			System.out.println(twitter.exists(user, doido));
			System.out.println(twitter.exists(doido, user1));
			System.out.println(twitter.exists(doido, user));
			*/
		/*
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
}


