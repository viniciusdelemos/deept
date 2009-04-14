package controller;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

public class Teste {

	static ControllerDeepTwitter controller = ControllerDeepTwitter.getInstance();
		
	public Teste() {
		for(int i = 1; i<1000; i++) {
			try {
				List<Status> list = controller.getTwitter().getUserTimelineByPage(i);
				System.out.println(list.size());
			} catch (TwitterException e) {
				System.out.println("DONE. PAGE COUNT = "+(i-1));
				break;				
			}
		}
	}
	
}
