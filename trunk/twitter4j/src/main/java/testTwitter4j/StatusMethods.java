package testTwitter4j;

import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;



public class StatusMethods {
	
	private String user;
	private String pass;
	
	public StatusMethods(String user, String pass){
		this.user = user;
		this.pass = pass;
	}
	
	/** Public Timeline **/

	public void getPublicTimeline(){
		
		
		Twitter twitter = new Twitter();
		List<Status> list = null;
		
		try {
			list = twitter.getPublicTimeline();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
	}
	
	public void getPublicTimeline(int sinceId){
		
		Twitter twitter = new Twitter();
		List<Status> list= null;
		
		try{
			list = twitter.getPublicTimeline(sinceId);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	
	/** Friends Timeline **/
	
	public void getFriendsTimeline(){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimeline();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimelineByPage(int page){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimelineByPage(page);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimelineBySinceId(long since_id){
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimelineBySinceId(since_id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimeline(int count){
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimeline(count);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimeline(String id){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimeline(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimelineByPage(String id, int page){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimelineByPage(id, page);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimeline(Date since){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimeline(since);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimeline(String id, Date since){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimeline(id, since);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimelineByMaxId(long max_id){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimelineByMaxId(max_id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getFriendsTimeline(long since_id, int count){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getFriendsTimeline(since_id, count);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	
	/** User Timeline **/
	
	public void getUserTimeline(String id, int count, Date since){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimeline(id, count, since);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getUserTimeline(String id, Date since){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimeline(id, since);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	
	public void getUserTimeline(String id, int count){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimeline(id, count);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getUserTimeline(int count, Date since){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimeline(count, since);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getUserTimeline(String id){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimeline(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getUserTimeline(){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimeline();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getUserTimeline(long since_id){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimeline(since_id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	public void getUserTimelineByPage(int page){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> list = null;
		
		try {
			list = twitter.getUserTimelineByPage(page);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(list);
		
	}
	
	
	
	public void show(long id){
		
		Twitter twitter = new Twitter(user, pass);
		Status status = null;
		
		try {
			status = twitter.show(id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(status);
		
	}
	
	public void update(String text){
		
		Twitter twitter = new Twitter(user, pass);
		Status status = null;
		
		try {
			status = twitter.update(text);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(status);
		
	}
	
	public void update(String status, long inReplyToStatusId){
		
		Twitter twitter = new Twitter(user, pass);
		Status list = null;
		
		try{
			list = twitter.update(status, inReplyToStatusId);
		}catch(TwitterException e){
			e.printStackTrace();
		}
		
		this.printStatus(list);

	}
	
	/**  Get Replies **/
	
	public void getReplies(){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> status = null;
		
		try {
			status = twitter.getReplies();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(status);
		
	}
	
	public void getRepliesByPage(int page){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> status = null;
		
		try {
			status = twitter.getRepliesByPage(page);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(status);
		
	}
	
	public void getRepliesByPageBySinceId(long since_id){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> status = null;
		
		try {
			status = twitter.getRepliesBySinceId(since_id);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(status);
		
	}
	
	public void getReplies(Date date){
		
		Twitter twitter = new Twitter(user, pass);
		List<Status> status = null;
		
		try {
			status = twitter.getReplies(date);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(status);
		
	}
	
	/** Destroy **/
	
	public void destroyStatus(long statusId){
		
		Twitter twitter = new Twitter(user, pass);
		Status status = null;
		
		try {
			status = twitter.destroyStatus(statusId);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		this.printStatus(status);
		
	}
	
	
	
	
	
	private void printStatus(List<Status> list)
	{
		int cont=1;
		for (Status status : list) {
			
			System.out.println(cont+++"-------------------------");
			
			System.out.println("\t create_at > "+status.getCreatedAt());
			System.out.println("\t id > "+ status.getId());
			System.out.println("\t text > "+ status.getText());
			System.out.println("\t source > "+status.getSource());
			System.out.println("\t trunceded > "+ status.isTruncated());
			System.out.println("\t in_reply_to_status_id > "+ status.getInReplyToStatusId());
			System.out.println("\t in_reply_to_user > "+ status.getInReplyToUserId());
			System.out.println("\t favorited > "+ status.isFavorited());
			
			System.out.println("\t user");
			System.out.println("\t\t id > "+status.getUser().getId());
			System.out.println("\t\t name > "+status.getUser().getName());
			System.out.println("\t\t screen_name > "+status.getUser().getScreenName());
			System.out.println("\t\t description > "+status.getUser().getDescription());
			System.out.println("\t\t location > "+status.getUser().getLocation());
			System.out.println("\t\t profile_image_url > "+status.getUser().getProfileImageURL());
			System.out.println("\t\t url > "+status.getUser().getURL());
			System.out.println("\t\t protected > "+status.getUser().isProtected());
			System.out.println("\t\t followers_count > "+status.getUser().getFollowersCount());
			
		}
		
	}
	
	private void printStatus(Status status)
	{
			
			System.out.println("-------------------------");
			
			System.out.println("\t create_at > "+status.getCreatedAt());
			System.out.println("\t id > "+ status.getId());
			System.out.println("\t text > "+ status.getText());
			System.out.println("\t source > "+status.getSource());
			System.out.println("\t trunceded > "+ status.isTruncated());
			System.out.println("\t in_reply_to_status_id > "+ status.getInReplyToStatusId());
			System.out.println("\t in_reply_to_user > "+ status.getInReplyToUserId());
			System.out.println("\t favorited > "+ status.isFavorited());
			
			System.out.println("\t user");
			System.out.println("\t\t id > "+status.getUser().getId());
			System.out.println("\t\t name > "+status.getUser().getName());
			System.out.println("\t\t screen_name > "+status.getUser().getScreenName());
			System.out.println("\t\t description > "+status.getUser().getDescription());
			System.out.println("\t\t location > "+status.getUser().getLocation());
			System.out.println("\t\t profile_image_url > "+status.getUser().getProfileImageURL());
			System.out.println("\t\t url > "+status.getUser().getURL());
			System.out.println("\t\t protected > "+status.getUser().isProtected());
			System.out.println("\t\t followers_count > "+status.getUser().getFollowersCount());
		
	}
	
	

}
