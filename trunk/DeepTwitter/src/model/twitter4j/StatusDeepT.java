package model.twitter4j;

import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import twitter4j.ExtendedUser;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;

public class StatusDeepT extends TwitterResponse{
	
    private Date createdAt;
    private long id;
    private String text;
    private String source;
    private boolean isTruncated;
    private long inReplyToStatusId;
    private int inReplyToUserId;
    private boolean isFavorited;
    private String inReplyToScreenName; //nao tem no public timeline
    
    public StatusDeepT(Element elem, Twitter twitter) throws TwitterException {
		super();

		try {
			ensureRootNodeNameIs("status", elem);

			id = getChildLong("id", elem);
			text = getChildText("text", elem);
			source = getChildText("source", elem);
			createdAt = getChildDate("created_at", elem);
			isTruncated = getChildBoolean("truncated", elem);
			inReplyToStatusId = getChildInt("in_reply_to_status_id", elem);
			inReplyToUserId = getChildInt("in_reply_to_user_id", elem);
			isFavorited = getChildBoolean("favorited", elem);

			inReplyToScreenName = getChildText("in_reply_to_screen_name", elem);
		} catch (Exception e) {

		}

	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getSource() {
		return source;
	}

	public boolean isTruncated() {
		return isTruncated;
	}

	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public int getInReplyToUserId() {
		return inReplyToUserId;
	}

	public boolean isFavorited() {
		return isFavorited;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}
	
	@Override
	public String toString(){
		return "Status{" +
				"createdAt=" + createdAt +
				", id=" + id +
				", text=" + text +
				", source=" + source +
				", isTruncated=" + isTruncated +
				", inReplyToStatusId=" + inReplyToStatusId +
				", inReplyToUserId=" + inReplyToUserId +
				", isFavorited=" + isFavorited +
				", inReplyToScreenName=" + inReplyToScreenName +
				"}";
	}
}
