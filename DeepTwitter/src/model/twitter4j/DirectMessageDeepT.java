package model.twitter4j;

import java.util.Date;

import org.w3c.dom.Element;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;
import twitter4j.User;

public class DirectMessageDeepT extends TwitterResponse{
	
    private int id;
    private String text;
    private int sender_id;
    private int recipient_id;
    private Date created_at;
    private String sender_screen_name;
    private String recipient_screen_name;
    
    private UserDeepT sender;
    private UserDeepT recipient;
    
    public DirectMessageDeepT(Element elem, Twitter twitter) throws TwitterException{
    	super();
    	
    	 ensureRootNodeNameIs("direct_message", elem);

         id = getChildInt("id", elem);
         text = getChildText("text", elem);
         sender_id = getChildInt("sender_id", elem);
         recipient_id = getChildInt("recipient_id", elem);
         created_at = getChildDate("created_at", elem);
         sender_screen_name = getChildText("sender_screen_name", elem);
         recipient_screen_name = getChildText("recipient_screen_name", elem);
         
         sender = new UserDeepT((Element) elem.getElementsByTagName("sender").item(0),
                 null);
         
         recipient = new UserDeepT((Element) elem.getElementsByTagName("recipient").item(0),
                 null);
    }

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public int getSender_id() {
		return sender_id;
	}

	public int getRecipient_id() {
		return recipient_id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public String getSender_screen_name() {
		return sender_screen_name;
	}

	public String getRecipient_screen_name() {
		return recipient_screen_name;
	}

	public UserDeepT getSender() {
		return sender;
	}

	public UserDeepT getRecipient() {
		return recipient;
	}
	
	@Override
	public String toString(){
		return "Direct-message{" +
				"id=" + id +
				", text=" + text +
				", sender_id=" + sender_id +
				", recipient_id=" + recipient_id +
				", created_at=" + created_at +
				", sender_screen_name=" + sender_screen_name +
				", recipient_screen_name=" + recipient_screen_name +
				", SENDER " + sender +
				", RECIPIENT " + recipient +
				"}";
	}

}
