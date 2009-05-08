package model.twitter4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import twitter4j.DirectMessage;
import twitter4j.ExtendedUser;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;
import twitter4j.User;

public class DirectMessageDeepT extends DirectMessage {
    
	private int id;
    private String text;
    private int sender_id;
    private int recipient_id;
    private Date created_at;
    private String sender_screen_name;
    private String recipient_screen_name;
    
    private ExtendedUser sender;
    private ExtendedUser recipient;
    
    DirectMessageDeepT(Element elem, Twitter twitter) throws TwitterException {
    	super();
    	
        ensureRootNodeNameIs("direct_message", elem);
        sender = new ExtendedUser((Element) elem.getElementsByTagName("sender").item(0),
                twitter);
        recipient = new ExtendedUser((Element) elem.getElementsByTagName("recipient").item(0),
                twitter);
        
        id = getChildInt("id", elem);
        text = getChildText("text", elem);
        sender_id = getChildInt("sender_id", elem);
        recipient_id = getChildInt("recipient_id", elem);
        created_at = getChildDate("created_at", elem);
        sender_screen_name = getChildText("sender_screen_name", elem);
        recipient_screen_name = getChildText("recipient_screen_name", elem);
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



	public ExtendedUser getSender() {
		return sender;
	}



	public ExtendedUser getRecipient() {
		return recipient;
	}



	public static List<DirectMessageDeepT> constructDirectMessagesDeepT(Document doc,
			Twitter twitter) throws TwitterException {
		if (TwitterResponse.isRootNodeNilClasses(doc)) {
			return new ArrayList<DirectMessageDeepT>(0);
		} else {
			try {
				TwitterResponse.ensureRootNodeNameIs("direct-messages", doc);
				NodeList list = doc.getDocumentElement().getElementsByTagName(
						"direct_message");
				int size = list.getLength();
				List<DirectMessageDeepT>  messages = new ArrayList<DirectMessageDeepT>(
						size);
				for (int i = 0; i < size; i++) {
					Element status = (Element) list.item(i);
					messages.add(new DirectMessageDeepT(status, twitter));
				}
				return messages;
			} catch (TwitterException te) {
				if (isRootNodeNilClasses(doc)) {
					return new ArrayList<DirectMessageDeepT>(0);
				} else {
					throw te;
				}
			}
		}
	}
	
	 @Override
	    public String toString() {
	        return "DirectMessage{" +
	                "id=" + id +
	                ", text='" + text + '\'' +
	                ", sender_id=" + sender_id +
	                ", recipient_id=" + recipient_id +
	                ", created_at=" + created_at +
	                ", sender_screen_name='" + sender_screen_name + '\'' +
	                ", recipient_screen_name='" + recipient_screen_name + '\'' +
	                ", sender=" + sender +
	                ", recipient=" + recipient +
	                '}' +
	                "sender: "+ sender +
	                "recipient: "+recipient;
	    }
    
    

}
