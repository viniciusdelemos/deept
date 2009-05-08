package model.twitter4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import twitter4j.DirectMessage;
import twitter4j.ExtendedUser;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.Twitter.Device;
import twitter4j.http.PostParameter;

public class TwitterDeepT extends TwitterMod{
	
	
	public TwitterDeepT(){
		super();
	}
	
	public TwitterDeepT(String baseURL){
		super(baseURL);
	}
	
	public TwitterDeepT(String id, String password){
		super(id, password);
	}
	
	public TwitterDeepT(String id, String password, String baseURL){
		super(id, password, baseURL);
	}
	
	/***** Public Timeline *****/
	
	public List<StatusDeepT> getPublicTimelineDeepT() throws TwitterException {
		
		ConvertStatusDeepT convertStatusDeepT = 
			new ConvertStatusDeepT(super.getPublicTimeline());
		
		return convertStatusDeepT.convert();		
		
	}
	
    public List<StatusDeepT> getPublicTimelineDeepT(long sinceID) throws TwitterException {
    	
    	ConvertStatusDeepT convertStatusDeepT = 
			new ConvertStatusDeepT(super.getPublicTimeline(sinceID));
		
		return convertStatusDeepT.convert();
    }
    
    
    /***** Friends Timeline *****/
    
    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the authenticating1 user and that user's friends.
     * It's also possible to request another user's friends_timeline via the id parameter below.
     * <br>calls http://twitter.com/statuses/friends_timeline
     *
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<StatusDeepT> getFriendsTimelineDeepT() throws
            TwitterException {
        return StatusDeepT.constructStatusesDeepT(get(baseURL 
        		+ "statuses/friends_timeline.xml", true).asDocument(), this);
    }
    
    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>calls http://twitter.com/statuses/friends_timeline
     *
     * @param paging controls pagination
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<StatusDeepT> getFriendsTimelineDeepT(Paging paging) throws TwitterException {
    	
    	return StatusDeepT.constructStatusesDeepT(get(baseURL + 
    			"statuses/friends_timeline.xml",null, paging, true).asDocument(), this);
    }
    
    /**
     * Returns the 20 most recent statuses posted in the last 24 hours from the specified userid.
     * <br>calls http://twitter.com/statuses/friends_timeline
     *
     * @param id   specifies the ID or screen name of the user for whom to return the friends_timeline
     * @param paging controls pagination
     * @return list of the Friends Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<StatusDeepT> getFriendsTimelineDeepT(String id, Paging paging) throws
            TwitterException {
    	
    	 return StatusDeepT.constructStatusesDeepT(get(baseURL + 
    			 "statuses/friends_timeline/" + id + ".xml",null, paging, true).asDocument(), this);
    }
    
    
    /***** User Timeline *****/
    
    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>calls http://twitter.com/statuses/user_timeline 
     *
     * @param id    specifies the ID or screen name of the user for whom to return the user_timeline
     * @param count specifies the number of statuses to retrieve.  May not be greater than 200 for performance purposes
     * @param sinceId Returns only statuses with an ID greater than (that is, more recent than) the specified ID
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.8
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<StatusDeepT> getUserTimelineDeepT(String id, int count,
                                                     long sinceId) throws TwitterException {
        
    	return getUserTimelineDeepT(id, new Paging(sinceId).count(count));
    }
    
    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>calls http://twitter.com/statuses/user_timeline 
     *
     * @param id    specifies the ID or screen name of the user for whom to return the user_timeline
     * @param paging controls pagenation
     * @return list of the user Timeline
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<StatusDeepT> getUserTimelineDeepT(String id, Paging paging) throws TwitterException {

        return StatusDeepT.constructStatusesDeepT(get(baseURL + "statuses/user_timeline/" + id + ".xml",
                null, paging, true).asDocument(), this);
    }
    
    /**
     * Returns the most recent statuses posted in the last 24 hours from the specified userid.
     * <br>calls http://twitter.com/statuses/user_timeline 
     *
     * @param id specifies the ID or screen name of the user for whom to return the user_timeline
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<StatusDeepT> getUserTimelineDeepT(String id) throws TwitterException {

        return StatusDeepT.constructStatusesDeepT(get(baseURL + 
        		"statuses/user_timeline/" + id + ".xml", true).asDocument(), this);
    }
    
    /**
     * Returns the most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>calls http://twitter.com/statuses/user_timeline 
     *
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     */
    public List<StatusDeepT> getUserTimelineDeepT() throws TwitterException {

        return StatusDeepT.constructStatusesDeepT(get(baseURL + "statuses/user_timeline.xml"
                , true).asDocument(), this);
    }
    
    /**
     * Returns the most recent statuses posted in the last 24 hours from the authenticating user.
     * <br>calls http://twitter.com/statuses/user_timeline 
     *
     * @param paging controls pagination
     * @return the 20 most recent statuses posted in the last 24 hours from the user
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-friends_timeline">Twitter API Wiki / Twitter REST API Method: statuses friends_timeline</a>
     * @since Twitter4J 2.0.1
     */
    public List<StatusDeepT> getUserTimelineDeepT(Paging paging) throws TwitterException {

        return StatusDeepT.constructStatusesDeepT(get(baseURL + "statuses/user_timeline.xml"
                , null, paging, true).asDocument(), this);
    }
    
    
    /***** Mentions (Replies) *****/
    
    /**
     * Returns the 20 most recent mentions (status containing @username) for the authenticating user.
     * <br>calls http://twitter.com/statuses/mentions
     *
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<StatusDeepT> getMentionsDeepT() throws TwitterException {

        return StatusDeepT.constructStatusesDeepT(get(baseURL + "statuses/mentions.xml",
                null, true).asDocument(), this);
    }
    
    /**
     * Returns the 20 most recent mentions (status containing @username) for the authenticating user.
     * <br>calls http://twitter.com/statuses/mentions
     *
     * @param paging controls pagination
     * @return the 20 most recent replies
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses-mentions">Twitter API Wiki / Twitter REST API Method: statuses mentions</a>
     */
    public List<StatusDeepT> getMentionsDeepT(Paging paging) throws TwitterException {
    	
        return StatusDeepT.constructStatusesDeepT(get(baseURL + "statuses/mentions.xml",
                null, paging, true).asDocument(), this);
    }
    
    
    /***** Status *****/
    
    /**
     * Returns a single status, specified by the id parameter. The status's author will be returned inline.
     * <br>calls http://twitter.com/statuses/show
     *
     * @param id the numerical ID of the status you're trying to retrieve
     * @return a single status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses show">Twitter API Wiki / Twitter REST API Method: statuses show</a>
     */
    public StatusDeepT showStatusDeepT(long id) throws TwitterException {
    	
        return new StatusDeepT(get(baseURL + "statuses/show/" + id + ".xml", false).asDocument().getDocumentElement(), this);
    }
    
    /**
     * Updates the user's status.
     * The text will be trimed if the length of the text is exceeding 160 characters.
     * <br>calls http://twitter.com/statuses/update
     *
     * @param status the text of your status update
     * @return the latest status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses update">Twitter API Wiki / Twitter REST API Method: statuses update</a>
     */
    public StatusDeepT updateStatusDeepT(String status) throws TwitterException {
    	
    	return new StatusDeepT(http.post(baseURL + "statuses/update.xml",
                new PostParameter[]{new PostParameter("status", status), new PostParameter("source", source)}, true).asDocument().getDocumentElement(), this);
    }
    
    /**
     * Updates the user's status.
     * The text will be trimed if the length of the text is exceeding 160 characters.
     * <br>calls http://twitter.com/statuses/update
     *
     * @param status            the text of your status update
     * @param inReplyToStatusId The ID of an existing status that the status to be posted is in reply to.  This implicitly sets the in_reply_to_user_id attribute of the resulting status to the user ID of the message being replied to.  Invalid/missing status IDs will be ignored.
     * @return the latest status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses update">Twitter API Wiki / Twitter REST API Method: statuses update</a>
     */
    public StatusDeepT updateStatusDeepT(String status, long inReplyToStatusId) throws TwitterException {

    	 return new StatusDeepT(http.post(baseURL + "statuses/update.xml",
                 new PostParameter[]{new PostParameter("status", status), new PostParameter("in_reply_to_status_id", String.valueOf(inReplyToStatusId)), new PostParameter("source", source)}, true).asDocument().getDocumentElement(), this);
    }
    
    /**
     * Destroys the status specified by the required ID parameter.  The authenticating user must be the author of the specified status.
     * <br>calls http://twitter.com/statuses/destroy 
     *
     * @param statusId The ID of the status to destroy.
     * @return the deleted status
     * @throws TwitterException when Twitter service or network is unavailable
     * @since 1.0.5
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses destroy">Twitter API Wiki / Twitter REST API Method: statuses destroy</a>
     */
    public StatusDeepT destroyStatusDeepT(long statusId) throws TwitterException {
    	
        return new StatusDeepT(http.post(baseURL + "statuses/destroy/" + statusId + ".xml",
                new PostParameter[0], true).asDocument().getDocumentElement(), this);
    }
    
    
    /***** User *****/
    
    /**
     * Returns the specified user's friends, each with current status inline.
     * <br>calls http://twitter.com/statuses/friends
     *
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<UserDeepT> getFriendsDeepT() throws TwitterException {
    	
    	
    	List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/friends.xml", true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }

    /**
     * Returns the specified user's friends, each with current status inline.
     * <br>calls http://twitter.com/statuses/friends
     *
     * @param paging controls pagination
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<UserDeepT> getFriendsDeepT(Paging paging) throws TwitterException {

    	List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/friends.xml", null,
                    paging, true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    /**
     * Returns the user's friends, each with current status inline.
     * <br>calls http://twitter.com/statuses/friends
     *
     * @param id the ID or screen name of the user for whom to request a list of friends
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<UserDeepT> getFriendsDeepT(String id) throws TwitterException {
        
    	List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/friends/" + id + ".xml"
                    , true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    /**
     * Returns the user's friends, each with current status inline.
     * <br>calls http://twitter.com/statuses/friends
     *
     * @param id the ID or screen name of the user for whom to request a list of friends
     * @param paging controls pagination
     * @return the list of friends
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses friends">Twitter API Wiki / Twitter REST API Method: statuses friends</a>
     */
    public List<UserDeepT> getFriendsDeepT(String id, Paging paging) throws TwitterException {
        
        List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/friends/" + id + ".xml"
                    , null, paging, true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>calls http://twitter.com/statuses/followers
     *
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<UserDeepT> getFollowersDeepT() throws TwitterException {

        List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/followers.xml", true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>calls http://twitter.com/statuses/followers
     *
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<UserDeepT> getFollowersDeepT(Paging paging) throws TwitterException {

        List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/followers.xml", null
                    , paging, true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>calls http://twitter.com/statuses/followers
     *
     * @param id The ID or screen name of the user for whom to request a list of followers.
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.1.0
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<UserDeepT> getFollowersDeepT(String id) throws TwitterException {
        
        List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/followers/" + id + ".xml", true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    /**
     * Returns the authenticating user's followers, each with current status inline. They are ordered by the order in which they joined Twitter (this is going to be changed).
     * <br>calls http://twitter.com/statuses/followers
     *
     * @param id   The ID or screen name of the user for whom to request a list of followers.
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-statuses followers">Twitter API Wiki / Twitter REST API Method: statuses followers</a>
     */
    public List<UserDeepT> getFollowersDeepT(String id, Paging paging) throws TwitterException {

        List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/followers/" + id +
                    ".xml", null, paging, true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    /**
     * Returns a list of the users currently featured on the site with their current statuses inline.
     *
     * @return List of User
     * @throws TwitterException when Twitter service or network is unavailable
     */
    public List<UserDeepT> getFeaturedDeepT() throws TwitterException {

        List<ExtendedUserDeepT> users = 
    		ExtendedUserDeepT.constructExtendedUserDeepT(get(baseURL + "statuses/featured.xml", true).asDocument(), this);
    	
    	ConvertUserDeepT convertUserDeepT  = 
			new ConvertUserDeepT(users);
		
		return convertUserDeepT.convert();
    }
    
    
    /***** Direct Messages *****/
    
    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>calls http://twitter.com/direct_messages
     *
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessageDeepT> getDirectMessagesDeepT() throws TwitterException {
    	
    	return 
    		DirectMessageDeepT.constructDirectMessagesDeepT(get(baseURL
    				+ "direct_messages.xml", true).asDocument(), this);
    }
    
    /**
     * Returns a list of the direct messages sent to the authenticating user.
     * <br>calls http://twitter.com/direct_messages
     *
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-direct_messages">Twitter API Wiki / Twitter REST API Method: direct_messages</a>
     */
    public List<DirectMessageDeepT> getDirectMessagesDeepT(Paging paging) throws TwitterException {

        return 
        	DirectMessageDeepT.constructDirectMessagesDeepT(get(baseURL
        			+ "direct_messages.xml", null, paging, true).asDocument(), this);
    }
    
    /**
     * Returns a list of the direct messages sent by the authenticating user.
     * <br>calls http://twitter.com/direct_messages/sent
     *
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-direct_messages sent">Twitter API Wiki / Twitter REST API Method: direct_messages sent</a>
     */
    public List<DirectMessageDeepT> getSentDirectMessagesDeepT() throws
            TwitterException {
        return DirectMessageDeepT.constructDirectMessagesDeepT(get(baseURL +
                "direct_messages/sent.xml", new PostParameter[0], true).asDocument(), this);
    }
    
    /**
     * Returns a list of the direct messages sent by the authenticating user.
     * <br>calls http://twitter.com/direct_messages/sent
     *
     * @param paging controls pagination
     * @return List
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-direct_messages sent">Twitter API Wiki / Twitter REST API Method: direct_messages sent</a>
     */
    public List<DirectMessageDeepT> getSentDirectMessagesDeepT(Paging paging) throws
            TwitterException {
        return DirectMessageDeepT.constructDirectMessagesDeepT(get(baseURL +
                "direct_messages/sent.xml", new PostParameter[0],paging, true).asDocument(), this);
    }
    
    /**
     * Sends a new direct message to the specified user from the authenticating user.  Requires both the user and text parameters below.
     * The text will be trimed if the length of the text is exceeding 140 characters.
     * <br>calls http://twitter.com/direct_messages/new
     *
     * @param id   the ID or screen name of the user to whom send the direct message
     * @param text String
     * @return DirectMessage
     * @throws TwitterException when Twitter service or network is unavailable
     @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-direct_messages new">Twitter API Wiki / Twitter REST API Method: direct_messages new</a>
     */
    public DirectMessageDeepT sendDirectMessageDeepT(String id,
                                                        String text) throws TwitterException {
        return new DirectMessageDeepT(http.post(baseURL + "direct_messages/new.xml",
                new PostParameter[]{new PostParameter("user", id),
                        new PostParameter("text", text)}, true).
                asDocument().getDocumentElement(), this);
    }
    
    /**
     * Destroys the direct message specified in the required ID parameter.  The authenticating user must be the recipient of the specified direct message.
     * <br>calls http://twitter.com/direct_messages/destroy 
     *
     * @param id the ID of the direct message to destroy
     * @return the deleted direct message
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-direct_messages destroy">Twitter API Wiki / Twitter REST API Method: direct_messages destroy</a>
     * @since Twitter4J 2.0.1
     */
    public DirectMessageDeepT destroyDirectMessageDeepT(int id) throws
            TwitterException {
        return new DirectMessageDeepT(http.post(baseURL +
                "direct_messages/destroy/" + id + ".xml", new PostParameter[0], true).asDocument().getDocumentElement(), this);
    }
    
    
    /***** Follow *****/
    
    /**
     * Befriends the user specified in the ID parameter as the authenticating user.  Returns the befriended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user to be befriended
     * @return the befriended user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-friendships create">Twitter API Wiki / Twitter REST API Method: friendships create</a>
     */
    public UserDeepT createFriendshipDeepT(String id) throws TwitterException {
    	
    	
        ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT(http.post(baseURL 
        		+ "friendships/create/" + id + ".xml", new PostParameter[0], true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
    }
    
    /**
     * Befriends the user specified in the ID parameter as the authenticating user.  Returns the befriended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user to be befriended
     * @param follow Enable notifications for the target user in addition to becoming friends.
     * @return the befriended user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.2
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-friendships create">Twitter API Wiki / Twitter REST API Method: friendships create</a>
     */
    public UserDeepT createFriendshipDeepT(String id, boolean follow) throws TwitterException {
        
    	ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT(http.post(baseURL 
    			+ "friendships/create/" + id + ".xml"
                , new PostParameter[]{new PostParameter("follow"
                        , String.valueOf(follow))}, true).asDocument()
                .getDocumentElement(), this);
    	
    	UserDeepT udt = new UserDeepT(extendedUserDeepT);
    	
    	return udt;
    }
    
    
    /***** Stop Follow *****/
    
    /**
     * Discontinues friendship with the user specified in the ID parameter as the authenticating user.  Returns the un-friended user in the requested format when successful.  Returns a string describing the failure condition when unsuccessful.
     *
     * @param id the ID or screen name of the user for whom to request a list of friends
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-friendships destroy">Twitter API Wiki / Twitter REST API Method: friendships destroy</a>
     */
    public UserDeepT destroyFriendshipDeepT(String id) throws TwitterException {
        ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT(http.post(baseURL + "friendships/destroy/" + id + ".xml", new PostParameter[0], true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
    }
    
    /**
     * Update the location
     *
     * @param location the current location of the user
     * @return the updated user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @see <a href="http://apiwiki.twitter.com/REST%20API%20Documentation#account/updatelocation">Twitter REST API Documentation &gt; Account Methods &gt; account/update_location</a>
     */
    public UserDeepT updateLocationDeepT(String location) throws TwitterException {
        ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT(http.post(baseURL + "account/update_location.xml", new PostParameter[]{new PostParameter("location", location)}, true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
    }
    
    /**
     * Sets which device Twitter delivers updates to for the authenticating user.  Sending none as the device parameter will disable IM or SMS updates.
     *
     * @param device new Delivery device. Must be one of: IM, SMS, NONE.
     * @return the updated user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 1.0.4
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-account update_delivery_device">Twitter API Wiki / Twitter REST API Method: account update_delivery_device</a>
     */
    public UserDeepT updateDeliverlyDeviceDeepT(Device device) throws TwitterException {
        ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT(http.post(baseURL + "account/update_delivery_device.xml", new PostParameter[]{new PostParameter("device", device.DEVICE)}, true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
    }
    
    
    /***** Favorites *****/
    
    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @since Twitter4J 2.0.1
     */
    public List<StatusDeepT> getFavoritesDeepT() throws TwitterException {
        
        return StatusDeepT.constructStatusesDeepT(get(baseURL 
        		+ "favorites.xml", new PostParameter[0], true).
                asDocument(), this);
    }
    
    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param page the number of page
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @since Twitter4J 2.0.1
     */
    public List<StatusDeepT> getFavoritesDeepT(int page) throws TwitterException {
        
        return StatusDeepT.constructStatusesDeepT(get(baseURL 
        		+ "favorites.xml", "page", String.valueOf(page), true).
                asDocument(), this);
    }
    
    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param id the ID or screen name of the user for whom to request a list of favorite statuses
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     * @since Twitter4J 2.0.1
     */
    public List<StatusDeepT> getFavoritesDeepT(String id) throws TwitterException {
    	
        return StatusDeepT.constructStatusesDeepT(get(baseURL 
        		+ "favorites/" + id + ".xml", new PostParameter[0], true).
                asDocument(), this);
    }
    
    /**
     * Returns the 20 most recent favorite statuses for the authenticating user or user specified by the ID parameter in the requested format.
     *
     * @param id   the ID or screen name of the user for whom to request a list of favorite statuses
     * @param page the number of page
     * @return List<Status>
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-favorites">Twitter API Wiki / Twitter REST API Method: favorites</a>
     */
    public List<StatusDeepT> getFavoritesDeepT(String id, int page) throws TwitterException {
    	
        return StatusDeepT.constructStatusesDeepT(get(baseURL 
        		+ "favorites/" + id + ".xml", "page", String.valueOf(page), true).
                asDocument(), this);
    }
    
    /**
     * Favorites the status specified in the ID parameter as the authenticating user.  Returns the favorite status when successful.
     *
     * @param id the ID of the status to favorite
     * @return Status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-favorites create">Twitter API Wiki / Twitter REST API Method: favorites create</a>
     */
    public StatusDeepT createFavoriteDeepT(long id) throws TwitterException {
    	
        return new StatusDeepT(http.post(baseURL + "favorites/create/" + id + ".xml", true).
                asDocument().getDocumentElement(), this);
    }
    
    /**
     * Un-favorites the status specified in the ID parameter as the authenticating user.  Returns the un-favorited status in the requested format when successful.
     *
     * @param id the ID of the status to un-favorite
     * @return Status
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-favorites destroy">Twitter API Wiki / Twitter REST API Method: favorites destroy</a>
     */
    public StatusDeepT destroyFavoriteDeepT(long id) throws TwitterException {
       
    	return new StatusDeepT(http.post(baseURL + "favorites/create/" + id + ".xml", true).
                asDocument().getDocumentElement(), this);
    }
    
    
    /***** Notifications *****/
    
    /**
     * Enables notifications for updates from the specified user to the authenticating user.  Returns the specified user when successful.
     *
     * @param id String
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-notifications follow">Twitter API Wiki / Twitter REST API Method: notifications follow</a>
     */
    public UserDeepT enableNotificationDeepT(String id) throws TwitterException {
        ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT (http.post(baseURL + "notifications/follow/" + id + ".xml", true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
    }
    
    /**
     * Disables notifications for updates from the specified user to the authenticating user.  Returns the specified user when successful.
     *
     * @param id String
     * @return User
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-notifications leave">Twitter API Wiki / Twitter REST API Method: notifications leave</a>
     */
    public UserDeepT disableNotificationDeepT(String id) throws TwitterException {
        ExtendedUserDeepT extendedUserDeepT  = new ExtendedUserDeepT (http.post(baseURL + "notifications/leave/" + id + ".xml", true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
       
    }
    
    
    /* Block Methods */

    /**
     * Blocks the user specified in the ID parameter as the authenticating user.  Returns the blocked user in the requested format when successful.
     *
     * @param id the ID or screen_name of the user to block
     * @return the blocked user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-blocks create">Twitter API Wiki / Twitter REST API Method: blocks create</a>
     */
    public UserDeepT createBlockDeepT(String id) throws TwitterException {
        ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT(http.post(baseURL + "blocks/create/" + id + ".xml", true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
    }
    
    /**
     * Un-blocks the user specified in the ID parameter as the authenticating user.  Returns the un-blocked user in the requested format when successful.
     *
     * @param id the ID or screen_name of the user to block
     * @return the unblocked user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 2.0.1
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method:-blocks destroy">Twitter API Wiki / Twitter REST API Method: blocks destroy</a>
     */
    public UserDeepT destroyBlockDeepT(String id) throws TwitterException {
        ExtendedUserDeepT extendedUserDeepT = new ExtendedUserDeepT(http.post(baseURL + "blocks/destroy/" + id + ".xml", true).
                asDocument().getDocumentElement(), this);
        
        UserDeepT udt = new UserDeepT(extendedUserDeepT);
        
        return udt;
    }
    
    
    
    /**
     * class to convert statuses list of Status to statuses list of StatusDeepT 
     * @author Vinicius
     *
     */
    private class ConvertStatusDeepT{
    	
    	List<Status> statuses;
    	List<StatusDeepT> statusesDeepT;
    	
    	
    	public ConvertStatusDeepT(List<Status> statuses){
    		this.statuses = statuses;
    		
    		statusesDeepT = new ArrayList<StatusDeepT>();
    	}
    	
    	public List<StatusDeepT> convert() throws TwitterException{
    		for(Status s : statuses){
    			
    			//TODO fazer construtor receber status (status + mais user normal)
    			StatusDeepT sdt = new StatusDeepT(s);
    			
    			statusesDeepT.add(sdt);
    		}
    		
    		return statusesDeepT;
    	}
    }
    
    /**
     * class to convert statuses list of Status to statuses list of StatusDeepT 
     * @author Vinicius
     *
     */
    private class ConvertUserDeepT{
    	
    	List<ExtendedUserDeepT> users;
    	List<UserDeepT> usersDeepT;;
    	
    	
    	public ConvertUserDeepT(List<ExtendedUserDeepT> users){
    		this.users = users;
    		
    		usersDeepT = new ArrayList<UserDeepT>();
    	}
    	
    	public List<UserDeepT> convert() throws TwitterException{
    		for(ExtendedUserDeepT u : users){
    			
    			UserDeepT udt = new UserDeepT(u);
    			
    			usersDeepT.add(udt);
    		}
    		
    		return usersDeepT;
    	}
    }
	
	
	
	

}
