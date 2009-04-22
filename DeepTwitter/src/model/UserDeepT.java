package model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

import twitter4j.DirectMessage;
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
    private String utcOffset;
    private String timeZone;
    private String profileBackgroundImageUrl;
    private String profileBackgroundTile;
    
    private int statusesCount;
    private boolean notifications;
    private boolean following;
    
    
    private Map<Long,StatusDeepT> statuses;
    
    //TODO implementar direct messages
    private Map<Integer,DirectMessage> directMessages;
    
    public UserDeepT(UserWithStatus userWithStatus){
    	
    	statuses = new HashMap<Long, StatusDeepT>();
    	    	
    	//Data of User
    	this.id = userWithStatus.getId();
    	this.name = userWithStatus.getName();
    	this.screenName = userWithStatus.getScreenName();
    	this.location = userWithStatus.getLocation();
    	this.description = userWithStatus.getDescription();
    	this.profileImageUrl = userWithStatus.getProfileImageURL();
    	this.url = userWithStatus.getURL();
    	this.isProtected = userWithStatus.isProtected();
    	this.followersCount = userWithStatus.getFollowersCount();
    	
    	this.profileBackgroundColor = userWithStatus.getProfileBackgroundColor();
    	this.profileTextColor = userWithStatus.getProfileTextColor();
    	this.profileLinkColor = userWithStatus.getProfileLinkColor();
    	this.profileSidebarFillColor = userWithStatus.getProfileSidebarFillColor();
    	this.profileSidebarBorderColor = userWithStatus.getProfileSidebarBorderColor();
    	
    	this.friendsCount = userWithStatus.getFriendsCount();
    	this.createdAt = userWithStatus.getCreatedAt();
    	this.favouritesCount = userWithStatus.getFavouritesCount();
    	this.utcOffset = userWithStatus.getUtcOffset();
    	this.timeZone = userWithStatus.getTimeZone();
    	this.profileBackgroundImageUrl = userWithStatus.getProfileBackgroundImageUrl();
    	this.profileBackgroundTile = userWithStatus.getProfileBackgroundTile();
    	
    	this.statusesCount = userWithStatus.getStatusesCount();
    	this.notifications = userWithStatus.isNotifications();
    	this.following = userWithStatus.isFollowing();
    	
    	if(userWithStatus.getStatusCreatedAt() != null){
	    	//Last status posted by user
	    	StatusDeepT statusDeepT = new StatusDeepT(userWithStatus.getStatusCreatedAt(),
	    			userWithStatus.getStatusId(), userWithStatus.getStatusText(), 
	    			userWithStatus.getStatusSource(), userWithStatus.isStatusTruncated(),
	    			userWithStatus.getStatusInReplyToStatusId(), userWithStatus.getStatusInReplyToUserId(),
	    			userWithStatus.isStatusFavorited(), userWithStatus.getStatusInReplyToScreenName());
	    	
	    	statuses.put(userWithStatus.getStatusId(), statusDeepT);
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

	public URL getProfileImageUrl() {
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

	public String getUtcOffset() {
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

	public Map<Long, StatusDeepT> getStatuses() {
		return statuses;
	}

	public Map<Integer, DirectMessage> getDirectMessages() {
		return directMessages;
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
