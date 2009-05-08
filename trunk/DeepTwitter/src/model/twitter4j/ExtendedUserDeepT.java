package model.twitter4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import twitter4j.ExtendedUser;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class ExtendedUserDeepT extends ExtendedUser{
	
  

    public ExtendedUserDeepT(Element elem, Twitter twitter)
			throws TwitterException {
		super(elem, twitter);
		
	}
    

	
	static List<ExtendedUserDeepT> constructExtendedUserDeepT(Document doc,
			Twitter twitter) throws TwitterException {

		if (isRootNodeNilClasses(doc)) {
			return new ArrayList<ExtendedUserDeepT>(0);
		} else {
			try {
				ensureRootNodeNameIs("users", doc);

				NodeList list = doc.getDocumentElement().getElementsByTagName(
						"user");

				int size = list.getLength();
				List<ExtendedUserDeepT> users = new ArrayList<ExtendedUserDeepT>(
						size);

				for (int i = 0; i < size; i++) {
					users.add(new ExtendedUserDeepT((Element) list.item(i),
							twitter));
				}
				return users;
			} catch (TwitterException e) {
				if (isRootNodeNilClasses(doc)) {
					return new ArrayList<ExtendedUserDeepT>(0);
				} else
					throw e;
			}

		}
	}
	
	@Override
    public String toString() {
        return "ExtendedUser{" +
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
                ", notifications=" + notificationEnabled +
                ", statusesCount=" + statusesCount +
                '}' +
                "message: " + this.getStatusText() +
                "user: " + this.getScreenName();
    }
	
	
	
	
	
	
    
    
	
	

}
