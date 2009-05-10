package model.twitter4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import twitter4j.ExtendedUser;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;

public class UserDeepT extends TwitterResponse{
	
	//Simple User
	private int id;
	private String name;
	private String screenName;
	private String description;
	private String location;
	private String profileImageUrl;
	private String url;
	private boolean isProtected;
	private int followersCount;
	
	//Extended User
    private String profileBackgroundColor = null;
    private String profileTextColor = null;
    private String profileLinkColor = null;
    private String profileSidebarFillColor = null;
    private String profileSidebarBorderColor = null;
    private int friendsCount = -1;
    private Date createdAt = null;
    private int favouritesCount = - 1;
    private int utcOffset = -1;
    private String timeZone = null;
    private String profileBackgroundImageUrl = null;
    private String profileBackgroundTile = null;
    private boolean following = false;
    private boolean notificationEnabled = false;
    private int statusesCount = -1;
    
    
    static final String[] POSSIBLE_ROOT_NAMES = new String[]{"user", "sender", "recipient"};
    
    
    public UserDeepT(Element elem, Twitter twitter) throws TwitterException{
    	
    	super();
    	
        ensureRootNodeNameIs(POSSIBLE_ROOT_NAMES, elem);
        id = getChildInt("id", elem);
        name = getChildText("name", elem);
        screenName = getChildText("screen_name", elem);
        location = getChildText("location", elem);
        description = getChildText("description", elem);
        profileImageUrl = getChildText("profile_image_url", elem);
        url = getChildText("url", elem);
        isProtected = getChildBoolean("protected", elem);
        followersCount = getChildInt("followers_count", elem);
        
        try{
        profileBackgroundColor = getChildText("profile_background_color", elem);
        profileTextColor = getChildText("profile_text_color", elem);
        profileLinkColor = getChildText("profile_link_color", elem);
        profileSidebarFillColor = getChildText("profile_sidebar_fill_color", elem);
        profileSidebarBorderColor = getChildText("profile_sidebar_border_color", elem);
        friendsCount = getChildInt("friends_count", elem);
        createdAt = getChildDate("created_at", elem);
        favouritesCount = getChildInt("favourites_count", elem);
        utcOffset = getChildInt("utc_offset", elem);
        timeZone = getChildText("time_zone", elem);
        profileBackgroundImageUrl = getChildText("profile_background_image_url", elem);
        profileBackgroundTile = getChildText("profile_background_tile", elem);
        following = getChildBoolean("following", elem);
        notificationEnabled = getChildBoolean("notifications", elem);
        statusesCount = getChildInt("statuses_count", elem);
        } catch(Exception e){
        	
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


	public String getDescription() {
		return description;
	}


	public String getLocation() {
		return location;
	}


	public URL getProfileImageUrl() {
		try {
			return new URL(profileImageUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}


	public String getUrl() {
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


	public boolean isFollowing() {
		return following;
	}


	public boolean isNotificationEnabled() {
		return notificationEnabled;
	}


	public int getStatusesCount() {
		return statusesCount;
	}

	
	@Override
    public String toString() {
        return "FullUserDeepT{" +
                "id=" + id +
                ", name=" + name +
                ", screenName=" + screenName +
                ", description=" + description +
                ", location=" + location +
                ", profileImageUrl=" + profileImageUrl +
                ", url=" + url +
                ", isProtected=" + isProtected +
                ", followersCount=" + followersCount +
                
                ". ExtendedUser{" +
                "profileBackgroundColor=" + profileBackgroundColor +
                ", profileTextColor=" + profileTextColor +
                ", profileLinkColor=" + profileLinkColor +
                ", profileSidebarFillColor=" + profileSidebarFillColor +
                ", profileSidebarBorderColor=" + profileSidebarBorderColor +
                ", friendsCount=" + friendsCount +
                ", createdAt=" + createdAt +
                ", favouritesCount=" + favouritesCount +
                ", utcOffset=" + utcOffset +
                ", timeZone=" + timeZone +
                ", profileBackgroundImageUrl=" + profileBackgroundImageUrl +
                ", profileBackgroundTile=" + profileBackgroundTile +
                ", following=" + following +
                ", notificationEnabled=" + notificationEnabled +
                ", statusesCount=" + statusesCount +
                "}}";
    }
}
