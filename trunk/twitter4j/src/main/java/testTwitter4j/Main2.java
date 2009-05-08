package testTwitter4j;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class Main2 {
	
	public static void main(String args []){
		
		
		Twitter twitter = new Twitter("viniciusdelemos","vslvsl");
		
		int count = 1;
		try{
		for(int i=1;i<5;i++){
			
			List<Status> list = twitter.getUserTimeline("twittervision" ,new Paging(i));
			
			for(Status s: list){
				System.out.println("Pge: "+i+ " Cmsg: "+count+ " msg: "+s.getText());
				count++;
			}
		
		}
		}catch(TwitterException e){
			e.printStackTrace();
		}
		
		
	}

}
