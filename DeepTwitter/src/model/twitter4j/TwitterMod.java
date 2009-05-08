package model.twitter4j;

import java.util.Date;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterMod extends Twitter{
	
	public TwitterMod(){
		super();
	}
	
	public TwitterMod(String baseURL){
		super(baseURL);
	}
	
	public TwitterMod(String id, String password){
		super(id, password);
	}
	
	public TwitterMod(String id, String password, String baseURL){
		super(id, password, baseURL);
	}
	
	
	/**
     * Returns only public statuses with an ID greater than (that is, more recent than) the specified ID.
     * <br>calls http://twitter.com/statuses/public_timeline 
     *
     * @param sinceID returns only public statuses with an ID greater than (that is, more recent than) the specified ID
     * @return the 20 most recent statuses
     * @throws TwitterException when Twitter service or network is unavailable
     * @deprecated see getPublicTimeline(long sinceID)
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-public_timeline">Twitter API Wiki / Twitter REST API Method: statuses public_timeline</a>
     */
    public List<Status> getPublicTimeline(int sinceID) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL +
                "statuses/public_timeline.xml", null, new Paging((long)sinceID), false).
                asDocument(), this);
    }
    
    /**
     * Returns only public statuses with an ID greater than (that is, more recent than) the specified ID.
     * <br>calls http://twitter.com/statuses/public_timeline 
     *
     * @param sinceID returns only public statuses with an ID greater than (that is, more recent than) the specified ID
     * @return the 20 most recent statuses
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-public_timeline">Twitter API Wiki / Twitter REST API Method: statuses public_timeline</a>
     */
    public List<Status> getPublicTimeline(long sinceID) throws
            TwitterException {
        return Status.constructStatuses(get(baseURL +
                "statuses/public_timeline.xml", null, new Paging(sinceID), false).
                asDocument(), this);
    }
    

    
    

    
    
    

}
