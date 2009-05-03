package model;

import java.util.Date;

import twitter4j.UserWithStatus;

public class StatusDeepT {
	
	private Date createdAt;
    private long id;
    private String text;
    private String source;
    private boolean isTruncated;
    private long inReplyToStatusId;
    private int inReplyToUserId;
    private boolean isFavorited;
    private String inReplyToScreenName;
    private UserDeepT userDeepT;
    
	public StatusDeepT(Date createdAt, long id, String text, String source,
			boolean isTruncated, long inReplyToStatusId, int inReplyToUserId,
			boolean isFavorited, String inReplyToScreenName) {

		this.createdAt = createdAt;
		this.id = id;
		this.text = text;
		this.source = source;
		this.isTruncated = isTruncated;
		this.inReplyToStatusId = inReplyToStatusId;
		this.inReplyToUserId = inReplyToUserId;
		this.isFavorited = isFavorited;
		this.inReplyToScreenName = inReplyToScreenName;
	}
	
	public StatusDeepT(twitter4j.Status status){
		this.createdAt = status.getCreatedAt();
		this.id = status.getId();
		this.text = status.getText();
		this.source = status.getSource();
		this.isTruncated = status.isTruncated();
		this.inReplyToStatusId = status.getInReplyToStatusId();
		this.inReplyToUserId = status.getInReplyToUserId();
		this.isFavorited = status.isFavorited();
		this.inReplyToScreenName = status.getInReplyToScreenName();		
		this.userDeepT = new UserDeepT(status.getUser());
	}
	
	public StatusDeepT(twitter4j.Tweet status){
		this.createdAt = status.getCreatedAt();
		this.id = status.getId();
		this.text = status.getText();
		this.source = status.getSource();		
		this.inReplyToUserId = status.getToUserId();
		this.inReplyToScreenName = status.getToUser();		
		this.userDeepT = new UserDeepT(status.getFromUserId(), status.getFromUser(),status.getProfileImageUrl());
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
	
	public UserDeepT getUser() {
		return userDeepT;
	}
    
    




}
