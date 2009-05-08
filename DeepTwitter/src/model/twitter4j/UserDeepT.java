package model.twitter4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.net.MalformedURLException;
import java.net.URL;


import twitter4j.DirectMessage;
import twitter4j.ExtendedUser;
import twitter4j.TwitterException;
import twitter4j.UserWithStatus;

public class UserDeepT {
	
	private int id;
    private String name;
    private String screenName;
    private String location;
    private String description;
    private URL profileImageUrl;
    private URL url;
    private boolean isProtected;
    private int followersCount;
    
    private String profileBackgroundColor;
    private String profileTextColor;
    private String profileLinkColor;
    private String profileSidebarFillColor;
    private String profileSidebarBorderColor;
    
    private int friendsCount;
    private Date createdAt;
    private int favouritesCount;
    private int utcOffset;
    private String timeZone;
    private String profileBackgroundImageUrl;
    private String profileBackgroundTile;
    
    private int statusesCount;
    private boolean notifications;
    private boolean following;
    
    
    
    private long lastStatus = -1;
    
    
    //private Map<Long,StatusDeepT> statuses;
    
    //TODO implementar direct messages
    private Map<Integer,DirectMessageDeepT> directMessages;
    
//ExtendedUserDeepT extende ExtendedUser!
    
//    public UserDeepT(ExtendedUserDeepT e) throws TwitterException{
//    	
//    	statuses = new HashMap<Long, StatusDeepT>();
//    	directMessages = new HashMap<Integer, DirectMessageDeepT>();
//    	
//    	//Data of User
//    	this.id = e.getId();
//        this.name = e.getName();
//        this.screenName = e.getScreenName();
//        this.location = e.getLocation();
//        this.description = e.getDescription();
//        this.profileImageUrl = e.getProfileImageURL();
//        this.url = e.getURL();
//        this.isProtected = e.isProtected();
//        this.followersCount = e.getFavouritesCount();
//        
//        this.profileBackgroundColor = e.getProfileBackgroundColor();
//        this.profileTextColor = e.getProfileTextColor();
//        this.profileLinkColor = e.getProfileLinkColor();
//        this.profileSidebarFillColor = e.getProfileSidebarFillColor();
//        this.profileSidebarBorderColor = e.getProfileSidebarBorderColor();
//        
//        this.friendsCount = e.getFriendsCount();
//        this.createdAt = e.getCreatedAt();
//        this.favouritesCount = e.getFavouritesCount();
//        this.utcOffset = e.getUtcOffset();
//        this.timeZone = e.getTimeZone();
//        this.profileBackgroundImageUrl = e.getProfileBackgroundImageUrl();
//        this.profileBackgroundTile = e.getProfileBackgroundTile();
//        
//        this.statusesCount = e.getStatusesCount();
//        this.notifications = e.isNotificationEnabled();
//        this.following = e.isFollowing();
//        
//        //Status
//        //se existe status
//        if(e.getStatusId() > 0){
//        	
//        	if(e.getStatusId() > lastStatus)
//        		lastStatus = e.getStatusId();
//        	
//        	StatusDeepT statusDeepT = new StatusDeepT(e.getStatusCreatedAt(),
//        			e.getStatusId(), e.getStatusText(), e.getStatusSource(),
//        			e.isStatusTruncated(), e.getStatusInReplyToStatusId(),
//        			e.getStatusInReplyToUserId(), e.isStatusFavorited(),
//        			e.getStatusInReplyToScreenName());
//        	
//        	//Se já tem a mensagem vai atualizar com novo bjeto
//        	statuses.put(e.getStatusId(), statusDeepT);
//       	
//        }
//   	
//    }
    
    public UserDeepT(ExtendedUser e) throws TwitterException{    	
    	//statuses = new HashMap<Long, StatusDeepT>();
    	directMessages = new HashMap<Integer, DirectMessageDeepT>();
    	
    	//Data of User
    	this.id = e.getId();
        this.name = e.getName();
        this.screenName = e.getScreenName();
        this.location = e.getLocation();
        this.description = e.getDescription();
        this.profileImageUrl = e.getProfileImageURL();
        this.url = e.getURL();
        this.isProtected = e.isProtected();
        this.followersCount = e.getFavouritesCount();
        
        this.profileBackgroundColor = e.getProfileBackgroundColor();
        this.profileTextColor = e.getProfileTextColor();
        this.profileLinkColor = e.getProfileLinkColor();
        this.profileSidebarFillColor = e.getProfileSidebarFillColor();
        this.profileSidebarBorderColor = e.getProfileSidebarBorderColor();
        
        this.friendsCount = e.getFriendsCount();
        this.createdAt = e.getCreatedAt();
        this.favouritesCount = e.getFavouritesCount();
        this.utcOffset = e.getUtcOffset();
        this.timeZone = e.getTimeZone();
        this.profileBackgroundImageUrl = e.getProfileBackgroundImageUrl();
        this.profileBackgroundTile = e.getProfileBackgroundTile();
        
        this.statusesCount = e.getStatusesCount();
        this.notifications = e.isNotificationEnabled();
        this.following = e.isFollowing();
        
//        //Status
//        //se existe status
//        if(e.getStatusId() > 0){
//        	
//        	if(e.getStatusId() > lastStatus)
//        		lastStatus = e.getStatusId();
//        	
//        	StatusDeepT statusDeepT = new StatusDeepT(e.getStatusCreatedAt(),
//        			e.getStatusId(), e.getStatusText(), e.getStatusSource(),
//        			e.isStatusTruncated(), e.getStatusInReplyToStatusId(),
//        			e.getStatusInReplyToUserId(), e.isStatusFavorited(),
//        			e.getStatusInReplyToScreenName());
//        	
//        	//Se já tem a mensagem vai atualizar com novo bjeto
//        	statuses.put(e.getStatusId(), statusDeepT);
//       	
//        }
   	
    }
    
    public UserDeepT(int fromUserId, String fromUserName, String imageURL) {
    	this.id = fromUserId;
    	this.screenName = fromUserName;
    	try {
			this.profileImageUrl = new URL(imageURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public URL getProfileImageURL() {
		return profileImageUrl;
	}

	public URL getUrl() {
		return url;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public String getProfileBackgroundColor() {
		return profileBackgroundColor;
	}

	public String getProfileTextColor() {
		return profileTextColor;
	}

	public String getProfileLinkColor() {
		return profileLinkColor;
	}

	public String getProfileSidebarFillColor() {
		return profileSidebarFillColor;
	}

	public String getProfileSidebarBorderColor() {
		return profileSidebarBorderColor;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public int getUtcOffset() {
		return utcOffset;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getProfileBackgroundImageUrl() {
		return profileBackgroundImageUrl;
	}

	public String getProfileBackgroundTile() {
		return profileBackgroundTile;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public boolean isNotifications() {
		return notifications;
	}

	public boolean isFollowing() {
		return following;
	}

//	public Map<Long, StatusDeepT> getStatuses() {
//		return statuses;
//	}

	public Map<Integer, DirectMessageDeepT> getDirectMessages() {
		return directMessages;
	}
    
	@Override
    public String toString() {
        String aux ="ExtendedUser{" +
                "profileBackgroundColor='" + profileBackgroundColor + '\'' +
                ", profileTextColor='" + profileTextColor + '\'' +
                ", profileLinkColor='" + profileLinkColor + '\'' +
                ", profileSidebarFillColor='" + profileSidebarFillColor + '\'' +
                ", profileSidebarBorderColor='" + profileSidebarBorderColor + '\'' +
                ", friendsCount=" + friendsCount +
                ", createdAt=" + createdAt +
                ", favouritesCount=" + favouritesCount +
                ", utcOffset=" + utcOffset +
                ", timeZone='" + timeZone + '\'' +
                ", profileBackgroundImageUrl='" + profileBackgroundImageUrl + '\'' +
                ", profileBackgroundTile='" + profileBackgroundTile + '\'' +
                ", following=" + following +
                ", notifications=" + notifications+
                ", statusesCount=" + statusesCount +
                '}';
        
//        if(statuses.get(lastStatus) != null){
//                aux+="message: " + statuses.get(lastStatus).getText() +
//                "user: " + this.name;
//        }
//        else{
//        	aux+="NENHUMA MENSAGEMMMM" +
//        		"user: "+this.name;
//        }
        
        return aux;
    }
    
    /*
     * 
<id>1401881</id>
<name>Doug Williams</name>
<screen_name>dougw</screen_name>
<location>San Francisco, CA</location>
<description>Twitter API Support. Internet, greed, users, dougw and opportunities are my passions.</description>
<profile_image_url>http://s3.amazonaws.com/twitter_production/profile_images/59648642/avatar_normal.png</profile_image_url>
<url>http://www.igudo.com</url>
<protected>false</protected>
<followers_count>1031</followers_count>
<profile_background_color>9ae4e8</profile_background_color>
<profile_text_color>000000</profile_text_color>
<profile_link_color>0000ff</profile_link_color>
<profile_sidebar_fill_color>e0ff92</profile_sidebar_fill_color>
<profile_sidebar_border_color>87bc44</profile_sidebar_border_color>
<friends_count>293</friends_count>
<created_at>Sun Mar 18 06:42:26 +0000 2007</created_at>
<favourites_count>0</favourites_count>
<utc_offset>-18000</utc_offset>
<time_zone>Eastern Time (US & Canada)</time_zone>
<profile_background_image_url>http://s3.amazonaws.com/twitter_production/profile_background_images/2752608/twitter_bg_grass.jpg</profile_background_image_url>
<profile_background_tile>false</profile_background_tile>
<statuses_count>3390</statuses_count>
<notifications>false</notifications>
<following>false</following>
*/

}
