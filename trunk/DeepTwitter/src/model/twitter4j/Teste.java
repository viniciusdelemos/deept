package model.twitter4j;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class Teste {
	
	public static void main(String args []){
		
		
		TwitterDeepT twitterTeste = new TwitterDeepT("viniciusdelemos", "vslvsl");
		
		
		List<TwitterResponseDeepT> messages = null;
		
		try{
			messages = twitterTeste.getFriendsDeepT();
		} catch(TwitterException e){
			e.printStackTrace();
		}
		
		int i = 1;
		for(TwitterResponseDeepT t : messages){
			System.out.println((i++) + " " + t+"\n");
		}
		
		
		
		
	}

}
